package edu.jhu.nlp.sequence.classify.features;

import java.io.PrintWriter;

import cc.mallet.types.Token;

/**
 * Like the Unix "tee" command, this dumps a copy of 
 * the features to an output stream before passing it
 * on to the next pipe.
 * @author Delip Rao
 *
 */
public class TeePipe extends TokenTransformingPipe {
 
  private static final long serialVersionUID = 4535561324958774377L;
  
  private transient PrintWriter _outputStream = null;
  
  public TeePipe() {
    _outputStream = null;
  }
  
  public TeePipe(PrintWriter outputStream) {
    _outputStream = outputStream;
  }
  
  @Override
  public Token transform(Token token) {
    if(_outputStream != null)
      _outputStream.println(token);
    return token;
  }

}
