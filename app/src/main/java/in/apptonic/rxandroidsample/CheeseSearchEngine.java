

package in.apptonic.rxandroidsample;

import java.util.LinkedList;
import java.util.List;

public class CheeseSearchEngine {

  private final List<String> mCheeses;
  private final int mCheesesCount;

  public CheeseSearchEngine(List<String> cheeses) {
    mCheeses = cheeses;
    mCheesesCount = mCheeses.size();
  }

  public List<String> search(String query) {
    query = query.toLowerCase();

    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    List<String> result = new LinkedList<>();

    for (int i = 0; i < mCheesesCount; i++) {
      if (mCheeses.get(i).toLowerCase().contains(query)) {
        result.add(mCheeses.get(i));
      }
    }

    return result;
  }

}
