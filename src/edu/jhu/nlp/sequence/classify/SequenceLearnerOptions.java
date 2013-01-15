package edu.jhu.nlp.sequence.classify;

import java.util.HashMap;

/**
 * A table of options specific to SequenceLearner
 * @author Delip Rao
 *
 */
public class SequenceLearnerOptions extends HashMap<String, Object>{
 
  private static final long serialVersionUID = 4146697884205408763L;
  
  // Add all SequenceLearner option strings (test, training)
  public static final String MAX_ITERATIONS = "SL_MAX_ITERATIONS";
  public static final String TRAIN_RATIO = "SL_TRAIN_RATIO";
  public static final String NUM_THREADS = "SL_NUM_THREADS";
  public static final String ORDERS = "SL_ORDERS";
  public static final String FEATURE_FACTORY = "SL_FEATURE_FACTORY";
  public static final String DUMP_FEATURES = "SL_DUMP_FEATURES";
  public static final String DEFAULT_LABEL = "SL_DEFAULT_LABEL";
  public static final String ENTITY_LEXICON = "SL_ENTITY_LEXICON";
  public static final String AUX_FEATURE_FACTORY = "SL_AUX_FEATURE_FACTORY";
  public static final String AUX_TRAIN = "SL_AUX_TRAIN";
  public static final String AUX_TRAIN_RATIO = "SL_AUX_TRAIN_RATIO";
  
  public SequenceLearnerOptions() {
    initializeOptionDefaults();
  }

  private void initializeOptionDefaults() {
    
    // set the options to their defaults
    put(MAX_ITERATIONS, Integer.MAX_VALUE);
    put(NUM_THREADS, 1);
    put(TRAIN_RATIO, new Double(1.0));
    put(ORDERS, "0,1");
    put(DEFAULT_LABEL, "O");
    put(FEATURE_FACTORY, "edu.jhu.nlp.sequence.classify.features.BasicNERFeatureFactory");
    put(AUX_FEATURE_FACTORY, "edu.jhu.nlp.sequence.classify.features.BasicNERFeatureFactory");
    put(AUX_TRAIN_RATIO, new Double(1.0));
  }
}
