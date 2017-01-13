/*
 * Created by: Aleksandr
 * Date: 11.01.2017
 * Project: FileFinder
 * <p>
 * "The more we do, the more we can do" ©
 */
package finder.search;

import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Observable;

/**
 * Encapsulated file search request,
 * containing:
 * - {@link Path} where to search
 * - {@link String} for what search for
 * - {@link Result} instance reference containing intermediate or final path collection.
 * It is also shared with view and all search algorithms.
 * - {@link Request.Status} with current search stage
 * - search options (like "search in files enabled")
 * <p>
 * This class is initialized by 'builder' pattern, so for initializing use {@link Request#getBuilder()}, and after
 * all settings are complete {@link Builder#build()}.
 * Example:
 * <pre>
 *  {@code
 *  Builder builder = Request.getBuilder();
 *  Request request = builder.setSearchFor(...).setSearchIn(...).build();
 *  }
 *  </pre>
 * <p>
 * {@link Request#execute(Result)} creates and run new thread for search.
 * <p>
 * Request extends {@link Observable}, register now if you want to know when new value is added!
 */
public class Request
        extends Observable
        implements Cloneable {
    /**
     * {@link Path} to search in.
     */
    private Path searchIn;
    /**
     * Pattern searching for.
     */
    private String searchFor;
    /**
     * Result for adding information.
     */
    private Result<Path> result = new Result<>();
    /**
     * Search IN files or not.
     */
    private Boolean searchInFiles = false;
    /**
     * Current request stage.
     */
    private Status currentSearchStatus;

    private Request() {
        super();
    }

    /**
     * Returns 'Builder' pattern builder to build ('oh my gosh') the Request. Builder takes care that all fields are
     * correctly initialized.
     *
     * @return Builder instance for current instance of request.
     */
    public static Builder getBuilder() {
        return new Request().new Builder();
    }

    /**
     * Begins execution of the request. Creates and start a new Thread to work for search.
     *
     * @param previous previous result.
     * @return current instance result, that would be updating during the search.
     */
    public Result<Path> execute(Result previous) {
        new Thread(this::search).start();
        return result;
    }

    /**
     * Perform search for files action. Updates status of request (Working, Error, Done).
     */
    private void search() {
        currentSearchStatus = Status.WORKING;
        try {
            // Initialize custom filevisitor - finder. It will find and told about any found to result.
            FileVisitor<Path> finder = new Finder(searchFor, result);
            // start filetree walking.
            Files.walkFileTree(searchIn, finder);
        } catch (IOException e) {
            e.printStackTrace();
            currentSearchStatus = Status.ERROR;
        }
        if (currentSearchStatus != Status.ERROR) {
            currentSearchStatus = Status.DONE;
        }
    }

    /**
     * @return Pattern searching for.
     */
    public String getSearchFor() {
        return searchFor;
    }

    /**
     * @return Current request results.
     */
    public Result<Path> getResult() {
        return result;
    }

    /**
     * @return Search IN files or not.
     */
    public Boolean getSearchInFiles() {
        return searchInFiles;
    }

    /**
     * @return Path where search should be performed.
     */
    public Path getSearchIn() {
        return searchIn;
    }

    /**
     * Clone current request instance to a new one.
     * Result are copied by reference!
     *
     * @return Cloned object.
     */
    public Request clone() throws CloneNotSupportedException {
        super.clone();
        Request request = new Request();
        request.searchIn = searchIn;
        request.searchFor = searchFor;
        request.result = result;
        request.searchInFiles = searchInFiles;
        return request;
    }

    /**
     * Request stage status.
     */
    public enum Status {
        /**
         * Error occurred.
         */
        ERROR(-1),
        /**
         * Created, but not finally initialized.
         */
        PENDING(0),
        /**
         * Prepared, all fields correctly initialized, but not started.
         */
        PREPARED(1),
        /**
         * Search working.
         */
        WORKING(2),
        /**
         * Search done.
         */
        DONE(3);

        /**
         * Stage code.
         */
        private int code;

        Status(int code) {
            this.code = code;
        }

        public int code() {
            return code;
        }

        /**
         * Indicates if all fields are correctly initialized.
         *
         * @return True if stage is above or equals PREPARED.
         */
        public boolean prepared() {
            return code >= Status.PREPARED.code;
        }
    }

    /**
     * 'Builder' pattern, to correctly initialize all fields.
     */
    public class Builder {
        /**
         * Initializing with Pending status - nothing is ready.
         */
        private Builder() {
            Request.this.currentSearchStatus = Status.PENDING;
        }

        /**
         * Setting search for pattern.
         *
         * @param searchFor Search for pattern.
         * @return this.
         */
        public Builder setSearchFor(String searchFor) {
            Request.this.searchFor = searchFor;
            return this;
        }

        /**
         * Setting search IN files indicator.
         *
         * @param searchInFiles Search IN indicator.
         * @return this.
         */
        public Builder setSearchInFiles(boolean searchInFiles) {
            Request.this.searchInFiles = searchInFiles;
            return this;
        }

        /**
         * Setting search in path.
         *
         * @param searchIn Search in string - path.
         * @return this.
         */
        public Builder setSearchIn(String searchIn) {
            Request.this.searchIn = Paths.get(searchIn);
            return this;
        }

        /**
         * Indicates if all fields are correctly initialized.
         *
         * @return Initialized correctly or not.
         */
        public boolean prepared() {
            if (searchFor != null && !"".equals(searchFor) && searchIn != null && !"".equals(searchIn.normalize().toString())) {
                Request.this.currentSearchStatus = Status.PREPARED;
                return true;
            }
            return false;
        }

        /**
         * Creates Request instance with initialized fields.
         *
         * @return Null if something went wrong, else - initialized instance.
         */
        public Request build() {
            if (!prepared()) {
                return null;
            }
            try {
                return Request.this.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
