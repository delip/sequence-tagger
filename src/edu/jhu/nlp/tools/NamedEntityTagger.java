package edu.jhu.nlp.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import edu.jhu.nlp.sequence.classify.CRFSequenceLearner;
import edu.jhu.nlp.sequence.classify.CRFSequenceLearnerOptions;
import edu.jhu.nlp.sequence.classify.DefaultOutputCallback;
import edu.jhu.nlp.sequence.classify.SequenceEvaluator;
import edu.jhu.nlp.sequence.classify.SequenceLearnerOptions;
import edu.jhu.nlp.sequence.classify.Tester;
import edu.jhu.nlp.sequence.classify.Trainer;
import edu.jhu.nlp.sequence.classify.features.FeatureFactory;


/**
 * Named entity tagger
 * @author Delip Rao
 *
 */
public class NamedEntityTagger {

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    CommandLineParser parser = new GnuParser();
    Options options = NamedEntityTaggerUtils.buildOptions();
    CommandLine commandLine = parser.parse(options, args);
    execute(options, commandLine);
  }

  private static void execute(Options options, CommandLine commandLine) throws Exception {
    
    if(commandLine.hasOption("help") || commandLine.getOptions().length == 0) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp( "NamedEntityTagger", options);
      System.exit(-1);
    }
    if(commandLine.hasOption("model") == false) 
      throw new Exception("Model file should be specified.");
    
    CRFSequenceLearnerOptions defaultOptions = new CRFSequenceLearnerOptions();
    NamedEntityTaggerUtils.updateLearnerOptions(commandLine, defaultOptions);
    CRFSequenceLearner sequenceLearner = new CRFSequenceLearner(defaultOptions);
    
    if(commandLine.hasOption("train")) {
      Trainer trainer = new Trainer(sequenceLearner);
      trainer.train(commandLine.getOptionValue("train"));
      trainer.saveModel(commandLine.getOptionValue("model"));
    }
    
    if(commandLine.hasOption("test")) {
      Tester tester = new Tester(sequenceLearner);
      tester.loadModel(commandLine.getOptionValue("model"));
      if(commandLine.hasOption("evaluate")) {
        SequenceEvaluator sequenceEvaluator = new SequenceEvaluator(
            new String[] { "B-PER", "B-ORG", "B-LOC", "B-MISC" },
            new String[] { "I-PER", "I-ORG", "I-LOC", "I-MISC" }
            );
        tester.setEvaluator(sequenceEvaluator);
        tester.evaluate(commandLine.getOptionValue("test"));
      }
      else tester.classify(commandLine.getOptionValue("test"), new DefaultOutputCallback());
    }
  }
}
