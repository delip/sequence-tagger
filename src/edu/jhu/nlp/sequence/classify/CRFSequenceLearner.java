package edu.jhu.nlp.sequence.classify;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.util.regex.Pattern;

import cc.mallet.fst.CRF;
import cc.mallet.fst.CRFCacheStaleIndicator;
import cc.mallet.fst.CRFOptimizableByBatchLabelLikelihood;
import cc.mallet.fst.CRFTrainerByLabelLikelihood;
import cc.mallet.fst.CRFTrainerByValueGradients;
import cc.mallet.fst.ThreadedOptimizable;
import cc.mallet.fst.Transducer;
import cc.mallet.optimize.Optimizable;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.iterator.LineGroupIterator;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Sequence;

/**
 * An implementation of the SequenceLearner interface using Mallet CRF
 * @author Delip Rao
 *
 */
public class CRFSequenceLearner implements SequenceLearner {

  boolean learnerInitialized = false;
  CRF crf = null;
  private CRFSequenceLearnerOptions _learnerOptions = null;

  public CRFSequenceLearner(CRFSequenceLearnerOptions learnerOptions) throws Exception{
    _learnerOptions = learnerOptions;
  }

  public CRFSequenceLearnerOptions getLearnerOptions() {
    return _learnerOptions;
  }

  /**
   * {@inheritDoc}
   */
  public void saveModel(String modelFileName) throws Exception {
    if(crf == null) throw new Exception("Model not trained. Nothing to save");
    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(modelFileName));
    oos.writeObject(crf);
    oos.close();
  }

  /**
   * {@inheritDoc}
   */
  public void train(InstanceList trainingData) throws Exception {
    train(trainingData, (Double)_learnerOptions.get(_learnerOptions.TRAIN_RATIO));
  }

  /**
   * {@inheritDoc}
   */
  public void train(InstanceList trainingData, double splitRatio) throws Exception {
    InstanceList[] split = null;
    if(splitRatio == 1.0)
      split = new InstanceList [] { trainingData, null};
    else
      split = trainingData.split(new Random(2009), //TODO(delip): fix magic
        new double [] {splitRatio, 1 - splitRatio});
    train(split[0], split[1]);
  }


  /**
   * {@inheritDoc}
   */
  public void train(InstanceList trainingData, 
      InstanceList evaluationData) throws Exception {

    //TODO(delip): take care of evaluationData

    if(learnerInitialized == false)
      initMalletCRF(trainingData);

    int numThreads = (Integer) _learnerOptions.get(_learnerOptions.NUM_THREADS);

    if(numThreads > 1) {
      CRFOptimizableByBatchLabelLikelihood batchOptLabel =
        new CRFOptimizableByBatchLabelLikelihood(crf, trainingData, numThreads);

      ThreadedOptimizable optLabel = new ThreadedOptimizable(
          batchOptLabel, trainingData, crf.getParameters().getNumFactors(),
          new CRFCacheStaleIndicator(crf));
      // CRF trainer
      Optimizable.ByGradientValue[] opts =
        new Optimizable.ByGradientValue[]{optLabel};
      // by default, use L-BFGS as the optimizer
      CRFTrainerByValueGradients crfTrainer =
        new CRFTrainerByValueGradients(crf, opts);
      //crfTrainer.setMaxResets(0);
      // train till convergence
      crfTrainer.train(trainingData, 
          (Integer)_learnerOptions.get(_learnerOptions.MAX_ITERATIONS));
      optLabel.shutdown();
      
    } else {
      CRFTrainerByLabelLikelihood crfTrainer = new CRFTrainerByLabelLikelihood(crf);
      crfTrainer.setGaussianPriorVariance(1.0); // TODO(delip): Fix Magic

      // train till convergence
      crfTrainer.train(trainingData, 
          (Integer)_learnerOptions.get(_learnerOptions.MAX_ITERATIONS));
    }
  }

  /**
   * Internal method to parse command line argument for the --orders parameter
   * @return
   */
  private int [] getOrders() {
    Object ordersObj = _learnerOptions.get(_learnerOptions.ORDERS);
    String [] ordersString = 
      ordersObj.toString().split(",");
    int [] orders = new int[ordersString.length];
    for(int i = 0; i < ordersString.length; i++) {
      orders[i] = Integer.parseInt(ordersString[i]);
      System.err.println("Orders: " + orders[i]);
    }
    return orders;
  }
  
  
  /**
   * Initialize Mallet CRF using trainingData and evaluationData
   * @param trainingData
   * @throws Exception
   */
  private void initMalletCRF(InstanceList trainingData) throws Exception {
    // Initialize Mallet CRF
    crf = new CRF(trainingData.getPipe(), (Pipe) null);
    String startStateName = crf.addOrderNStates(trainingData, 
        getOrders(), // order of the CRF
        (boolean[]) null, // "defaults" parameter; see mallet javadoc
        // default label
        (String)_learnerOptions.get(_learnerOptions.DEFAULT_LABEL),
        Pattern.compile("O,I-*"), // forbidden pattern
        null, // allowed pattern
        true); // true for a fully connected CRF

    for (int i = 0; i < crf.numStates(); i++)
      crf.getState(i).setInitialWeight (Transducer.IMPOSSIBLE_WEIGHT);
    crf.getState(startStateName).setInitialWeight(0.0);
    crf.setWeightsDimensionAsIn(trainingData, true);
    //Everything done. Set the flag to indicate that.
    //Still need to initialize trainer
    learnerInitialized = true;
  }

  /**
   * {@inheritDoc}
   */
  public void loadModel(String modelFileName) throws Exception {
    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelFileName));
    crf = (CRF)ois.readObject();
    ois.close();
    learnerInitialized = true;
  }

  private InstanceList loadTestData(String testFileName) throws Exception {
    if(crf == null) throw new Exception("Model not trained/loaded");
    InstanceList testData = new InstanceList(crf.getInputPipe());
    testData.addThruPipe(
        new LineGroupIterator(new FileReader(testFileName),
            Pattern.compile("^\\s*$"), true));
    return testData;
  }

  @SuppressWarnings("unchecked")
  public void classify(String testFileName, OutputCallback outputCallback) throws Exception {
    InstanceList testData = loadTestData(testFileName);
    for (int i = 0; i < testData.size(); i++) {
      Sequence input = (Sequence)testData.get(i).getData();
      Sequence output = crf.transduce(input);
      outputCallback.process(input, output);
    }
  }

  /**
   * {@inheritDoc}
   */
  public Pipe getInputPipe() throws Exception {
    return crf.getInputPipe();
  }

  /**
   * {@inheritDoc}
   */
  public Transducer getModel() {
    return crf;
  }

}
