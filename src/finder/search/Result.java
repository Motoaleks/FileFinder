package finder.search;

import java.util.Observable;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by: Aleksandr
 * Date: 08.01.2017
 * Project: FileFinder
 * <p>
 * "The more we do, the more we can do" ©
 */
public class Result<T> extends Observable {
    private Set<T> result;

    public Result() {
        super();
        result = new TreeSet<T>();
    }

    public synchronized void addResult(T newResult) {
        result.add(newResult);
        setChanged();
        notifyObservers(newResult);
    }

    public synchronized void clearResults() {
        result.clear();
        setChanged();
        notifyObservers();
    }

    public Set getResult() {
        return result;
    }

    public int count(){
        return result.size();
    }
}
