* All code here is distributed with Apache 2.0 license but
  the following dependencies have their own licensing restrictions:
  -  Depends on Mallet 2.0RC4 or later. The mallet jar is bundled with 
     this but you should see http://mallet.cs.umass.edu for license 
     details etc.
  -  The sample NER code using this API depends on Apache commons CLI
     parser (also bundled here but with its own license)

This code allows arbitrary sequence tagging tasks to be defined by
introducing an abstract FeatureFactory. If you're implementing a new
sequence tagging task all you have to do is to extend the
FeatureFactory for that task and call the setFeatureFactory() method
on the CRFSequenceLearner object without having to figure out any of
the obscure Mallet details. For an example, see how the named entity
recognition task is defined in:

inn

src/edu/jhu/nlp/tools/NamedEntityTagger.java
(We get around 89 F-score for the CONLL'03 English NER data using the
BasicNERFeatureFactory)

Also this code simplifies setting up parallel training in CRFs. All
you've to do is call setNumThreads() on the CRFSequenceLearner object.
To give an idea, by using four threads on four cores the CRF
training time is well under 15 mins for English CoNLL data
(non-parallel takes more than 4 hours).

If you're doing something special to the CRF output other than
printing it, you've to simply implement the OutputCallback.
(DefaultOutputCallback just prints)
