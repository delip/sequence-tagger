package edu.jhu.nlp.sequence.classify.features;

import java.io.PrintWriter;
import java.util.ArrayList;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Vector;

/**
 * Feature factory for building composite features
 * @author Delip Rao
 *
 */
public abstract class CompositeFeatureFactory extends FeatureFactory {
  protected FeaturePipeline _featurePipeline = new FeaturePipeline();
  protected PrintWriter _outputStream = null; 
  protected ArrayList<Pipe> endPipesList = new ArrayList<Pipe>();
  
  public void registerEndPipe(Pipe p) {
    if(p != null) endPipesList.add(p);
  }
  
  public void setFeatureDumpStream(PrintWriter outputStream) {
    _outputStream = outputStream;
    registerEndPipe(new TeePipe(_outputStream));
  }

  public Pipe getPipe(Pipe endPipe) {
    Pipe pipeToReturn = null;
    endPipesList.add(endPipe);
    for(Pipe p : endPipesList) {
      _featurePipeline.addPipe(p);
    }
    pipeToReturn =  _featurePipeline.getCompositePipe();
    for(Pipe p : endPipesList) {
      _featurePipeline.removeLastPipe();
    }
    
    return pipeToReturn;
  }

}
