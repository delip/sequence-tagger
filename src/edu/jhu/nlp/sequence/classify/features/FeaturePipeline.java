package edu.jhu.nlp.sequence.classify.features;

import java.util.Arrays;
import java.util.Vector;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;

/**
 * An implementation for building cascading feature pipes incrementally
 * @author Delip Rao
 *
 */
public class FeaturePipeline {
  private Vector<Pipe> pipeVector = new Vector<Pipe>();
  
  public void addPipe(Pipe p) {
    pipeVector.add(p);
  }
  
  public void addPipes(Pipe [] pipes) {
    pipeVector.addAll(Arrays.asList(pipes));
  }
  
  public Pipe getCompositePipe() {
    if(pipeVector.size() == 0) return null;
    Pipe [] plist = new Pipe[pipeVector.size()];
    for (int i = 0; i < pipeVector.size(); i++) 
      plist[i] = pipeVector.get(i);
    return new SerialPipes(plist);
  }

  public void rebuild() {
    pipeVector.clear();
  }

  public void removeLastPipe() {
    if(pipeVector.size() == 0) return;
    pipeVector.remove(pipeVector.size() - 1);
  }
  
}
