package edu.jhu.nlp.sequence.classify;

import cc.mallet.fst.Transducer;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.InstanceList;

/**
 * Interface for sequence learning
 * @author Delip Rao
 *
 */
public interface SequenceLearner {
  /**
   * Train a model from the training data
   * @param trainingData
   * @throws Exception
   */
  public void train(InstanceList trainingData) throws Exception;
  
  /**
   * Uses splitRatio to split the trainingData for model training
   * @param trainingData
   * @param splitRatio
   * @throws Exception
   */
  public void train(InstanceList trainingData, double splitRatio) throws Exception;
  
  
  /**
   * 
   * @param trainingData
   * @param evaluationData
   * @throws Exception
   */
  public void train(InstanceList trainingData, InstanceList evaluationData) throws Exception;
  
  /**
   * Save a trained model to a file specified by modelFileName 
   * @param modelFileName
   * @throws Exception
   */
  public void saveModel(String modelFileName) throws Exception;
  
  /**
   * Load a trained model from the file specified by modelFileName
   * @param modelFileName
   * @throws Exception
   */
  public void loadModel(String modelFileName) throws Exception;
  
  /**
   * Classifies instances in the testFileName. For each classification result:
   * outputCallback.process() is called
   * @param testFileName
   * @param outputCallback
   * @throws Exception
   */
  public void classify(String testFileName, OutputCallback outputCallback) throws Exception;
  
  
  /**
   * 
   * @return learnerOptions
   */
  public SequenceLearnerOptions getLearnerOptions();

  /**
   * If the model is trained, return the pip from the model
   * @return
   * @throws Exception
   */
  public Pipe getInputPipe() throws Exception;

  /**
   * Return transducer model
   * @return
   */
  public Transducer getModel();

}
