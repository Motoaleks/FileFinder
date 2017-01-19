package finder.index;

import finder.search.Request;
import finder.search.Result;
import java.nio.file.Path;

/**
 * Created by: Aleksandr
 * Date: 19.01.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public abstract class IndexLogic {

  protected Result result;
  protected State currentState;

  public abstract void search(Request request);

  public abstract void index(Path indexPath);

  public abstract void changeState(State state);

  public enum State {
    PENDING,
    RUNNING,
    PAUSED,
    STOPPED,
    COMPLETED,
    ERROR
  }
}
