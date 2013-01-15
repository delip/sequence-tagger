package edu.jhu.nlp.sequence.classify.features;

import java.io.PrintWriter;

import cc.mallet.pipe.Pipe;

/**
 * Assumes input has binary features all listed in a line
 * one line per data point. Using this feature factory should
 * provide the same output as using Mallet's SimpleTagger
 * @author Delip Rao
 *
 */
public class BinaryFeatureFactory extends FeatureFactory {

  @Override
  public void build() {
    // nothing to build
  }
  
  @Override
  public Pipe getPipe() {
    return new BinaryFeaturePipe();
  }

  @Override
  public void setFeatureDumpStream(PrintWriter outputStream) {
    System.err.println("dumpFeatures() not implemented for BinaryFeatureFactory");
  }

}
