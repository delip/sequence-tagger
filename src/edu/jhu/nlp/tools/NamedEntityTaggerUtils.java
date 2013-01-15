package edu.jhu.nlp.tools;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import edu.jhu.nlp.sequence.classify.CRFSequenceLearnerOptions;

/**
 * Non-public helper class for NamedEntityTagger
 * @author Delip Rao
 *
 */
class NamedEntityTaggerUtils {

  public static void updateLearnerOptions(CommandLine commandLine,
      CRFSequenceLearnerOptions defaultOptions) throws Exception {

    if(commandLine.hasOption("iterations")) {
      defaultOptions.put(defaultOptions.MAX_ITERATIONS, 
          Integer.parseInt(commandLine.getOptionValue("iterations")));
    }
    if(commandLine.hasOption("threads")) {
      defaultOptions.put(defaultOptions.NUM_THREADS, 
          Integer.parseInt(commandLine.getOptionValue("threads")));
    }
    if(commandLine.hasOption("orders")) {
      defaultOptions.put(defaultOptions.ORDERS,
          commandLine.getOptionValue("orders"));
    }
    if(commandLine.hasOption("featureFactory")) {
      defaultOptions.put(defaultOptions.FEATURE_FACTORY,
          commandLine.getOptionValue("featureFactory"));
    }
    if(commandLine.hasOption("dumpFeatures")) {
      defaultOptions.put(defaultOptions.DUMP_FEATURES,
          commandLine.getOptionValue("dumpFeatures"));
    }
    if(commandLine.hasOption("trainRatio")) {
      defaultOptions.put(defaultOptions.TRAIN_RATIO,
          Double.parseDouble(commandLine.getOptionValue("trainRatio")));
    }

    if(commandLine.hasOption("entityLexicon")) {
      defaultOptions.put(defaultOptions.ENTITY_LEXICON,
          commandLine.getOptionValue("entityLexicon"));
    }

    if(commandLine.hasOption("auxTrain")) {
      defaultOptions.put(defaultOptions.AUX_TRAIN,
          commandLine.getOptionValue("auxTrain"));
    }
    if(commandLine.hasOption("auxTrainRatio")) {
      defaultOptions.put(defaultOptions.AUX_TRAIN_RATIO,
          Double.parseDouble(commandLine.getOptionValue("auxTrainRatio")));
    }
    if(commandLine.hasOption("auxFeatureFactory")) {
      defaultOptions.put(defaultOptions.AUX_FEATURE_FACTORY,
          commandLine.getOptionValue("auxFeatureFactory"));
    }
  }

  public static Options buildOptions() {
    Options options = new Options();
    options.addOption("train", true, "Location of the training file");
    options.addOption("test", true, "Location of the test file");
    options.addOption("evaluate", false, "Used with 'test'. Performs precision recall calculation. Optional.");
    options.addOption("model", true, "Location of the model file. Required.");
    options.addOption("iterations", true, "Maximum number of iterations to train CRF. Default INT_MAX");
    options.addOption("threads", true, "Number of threads to use for parallel training. Default 1");
    options.addOption("orders", true, "Order of the CRF. Default 0,1");
    options.addOption("help", false, "Display this help");
    options.addOption("dumpFeatures", true, "Dump a copy of the features to the specified file. Optional.");
    options.addOption("trainRatio", true, 
        "Fraction of the training data to be used for actual training. Default 1.0");
    
    options.addOption("featureFactory", true, 
        "The feature factory to load for training. Default BasicNERFeatureFactory");
    options.addOption("train", true, "Location of the training file");
    
    options.addOption("entityLexicon", true, "A dictionary of named entities to be used for training. Optional.");
    
    return options;
  }
  
}
