package finder.search;

import javafx.collections.ObservableList;

import java.io.File;
import java.util.Observer;

/**
 * Created by: Aleksandr
 * Date: 08.01.2017
 * Project: FileFinder
 * <p>
 * "The more we do, the more we can do" ©
 */
public class Search {
    private static volatile Search instance;
    private boolean searchActive = false;
    private ObservableList<?> result;
    private boolean searchInFiles = false;

    public static Search getInstance() {
        Search localInstance = instance;
        if (localInstance == null) {
            synchronized (Search.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Search();
                }
            }
        }
        return localInstance;
    }

    public void findFile(String request, Observer updater) {
        Result requestResult = new Result<File>();
        requestResult.addObserver(updater);
    }
}
