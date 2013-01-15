package edu.jhu.nlp.sequence.classify;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;
import java.util.regex.Pattern;

import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.iterator.LineGroupIterator;
import cc.mallet.types.InstanceList;
import edu.jhu.nlp.sequence.classify.features.*;


/**
 * Trainer class for sequence classification
 * @author Delip Rao
 *
 */
public class Trainer {

  private SequenceLearner _learner = null;
  private FeatureFactory _featureFactory = null;
  private FeatureFactory _auxFeatureFactory = null;

  public Trainer(SequenceLearner learner) {
    _learner = learner;
  }

  public void setFeatureFactory(FeatureFactory featureFactory) {
    _featureFactory = featureFactory;
  }

  private void setAuxillaryFeatureFactory() throws Exception {
    SequenceLearnerOptions options = _learner.getLearnerOptions();
    if(options.containsKey(SequenceLearnerOptions.AUX_FEATURE_FACTORY)) {
      String featureFactoryString =  (String) options.get(options.AUX_FEATURE_FACTORY);
      FeatureFactory featureFactory = FeatureFactory.loadFeatureFactory(options, featureFactoryString);
      featureFactory.setLearnerOptions(options);
      _auxFeatureFactory = featureFactory;
    }
  }

  private PrintWriter getFeatureDumpStream(
      SequenceLearnerOptions defaultOptions) throws Exception {
    String featureDumpFile = (String)defaultOptions.get(defaultOptions.DUMP_FEATURES);
    return new PrintWriter(new FileWriter(featureDumpFile));
  }


  private void setFeatureFactory() throws Exception {
    SequenceLearnerOptions options = _learner.getLearnerOptions();
    if(options.containsKey(SequenceLearnerOptions.FEATURE_FACTORY)) {
      String featureFactoryString =  (String) options.get(options.FEATURE_FACTORY);
      FeatureFactory featureFactory = FeatureFactory.loadFeatureFactory(options, featureFactoryString);
      featureFactory.setLearnerOptions(options);
      if(options.containsKey(SequenceLearnerOptions.DUMP_FEATURES))
        featureFactory.setFeatureDumpStream(getFeatureDumpStream(options));
      _featureFactory = featureFactory;
    } else throw new Exception("Feature factory not initialized.");
  }

  public void train(String trainingFileName) throws Exception {

    if(_featureFactory == null) setFeatureFactory();
    if(_auxFeatureFactory == null) setAuxillaryFeatureFactory();

    _featureFactory.build();
    Pipe p = _featureFactory.getPipe();

    SequenceLearnerOptions options =  _learner.getLearnerOptions();
    String defaultLabel = (String) options.get(options.DEFAULT_LABEL);

    // add default label
    InstanceList trainingData = new InstanceList(p);
    trainingData.addThruPipe(
        new LineGroupIterator( new FileReader(trainingFileName),
            Pattern.compile("^\\s*$"), true));
    p.getTargetAlphabet().lookupIndex(defaultLabel);
    p.setTargetProcessing(true);
    
    // Add auxillary training data
    if(_auxFeatureFactory != null && options.containsKey(options.AUX_TRAIN)) {
      String auxTrainingFileName = (String)options.get(options.AUX_TRAIN);
      _auxFeatureFactory.build();
      Pipe auxPipe = _auxFeatureFactory.getPipe();
      InstanceList auxTrainingData = new InstanceList(auxPipe);
      auxTrainingData.addThruPipe(
          new LineGroupIterator(new FileReader(auxTrainingFileName),
            Pattern.compile("^\\s*$"), true));
      auxPipe.getTargetAlphabet().lookupIndex(defaultLabel);
      auxPipe.setTargetProcessing(true);
      double auxSplitRatio = (Double)options.get(options.AUX_TRAIN_RATIO);
      InstanceList [] split = auxTrainingData.split(new Random(2009), //TODO(delip): fix magic
          new double [] {auxSplitRatio, 1 - auxSplitRatio});
      for(int i = 0; i < split[0].size(); i++)
        trainingData.add(split[0].get(i));
    }
    _learner.train(trainingData);
  }

  public void saveModel(String modelFileName) throws Exception {
    _learner.saveModel(modelFileName);
  }

}
