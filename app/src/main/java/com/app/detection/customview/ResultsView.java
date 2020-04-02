
package com.app.detection.customview;

import java.util.List;
import com.app.detection.tflite.Classifier.Recognition;

public interface ResultsView {
  public void setResults(final List<Recognition> results);
}
