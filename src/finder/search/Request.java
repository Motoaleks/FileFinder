package finder.search;

import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Observable;

/**
 * Created by: Aleksandr
 * Date: 11.01.2017
 * Project: FileFinder
 * <p>
 * "The more we do, the more we can do" ©
 */
public class Request
        extends Observable
        implements Cloneable {
    private Path searchIn;
    private String searchFor;
    private Result<Path> result = new Result<>();
    private Boolean searchInFiles = false;
    private Status currentSearchStatus;

    private Request() {
        super();
    }

    public static Builder getBuilder() {
        return new Request().new Builder();
    }

    public Result<Path> execute(Result previous) {
        new Thread(this::search).start();
        return result;
    }

    private void search() {
        currentSearchStatus = Status.WORKING;
        try {
            FileVisitor<Path> finder = new Finder(searchFor, result);
            Files.walkFileTree(searchIn, finder);
        } catch (IOException e) {
            e.printStackTrace();
            currentSearchStatus = Status.ERROR;
        }
        if (currentSearchStatus != Status.ERROR) {
            currentSearchStatus = Status.DONE;
        }
    }

    public String getSearchFor() {
        return searchFor;
    }

    public Result<Path> getResult() {
        return result;
    }

    public Boolean getSearchInFiles() {
        return searchInFiles;
    }

    public Path getSearchIn() {
        return searchIn;
    }

    public Request clone() throws CloneNotSupportedException {
        super.clone();
        Request request = new Request();
        request.searchIn = searchIn;
        request.searchFor = searchFor;
        request.result = result;
        request.searchInFiles = searchInFiles;
        return request;
    }

    public enum Status {
        ERROR(-1), PENDING(0), PREPARED(1), WORKING(2), DONE(3);

        private int code;

        Status(int code) {
            this.code = code;
        }

        public int code() {
            return code;
        }

        public boolean prepared() {
            if (code >= Status.PREPARED.code) {
                return true;
            } else {
                return false;
            }
        }
    }

    public class Builder {
        private Builder() {
            Request.this.currentSearchStatus = Status.PENDING;
        }

        public Builder setSearchFor(String searchFor) {
            Request.this.searchFor = searchFor;
            return this;
        }

        public Builder setSearchInFiles(boolean searchInFiles) {
            Request.this.searchInFiles = searchInFiles;
            return this;
        }

        public Builder setSearchIn(String searchIn) {
            Request.this.searchIn = Paths.get(searchIn);
            return this;
        }

        public boolean prepared() {
            if (searchFor != null && !"".equals(searchFor) && searchIn != null && !"".equals(searchIn.normalize().toString())) {
                Request.this.currentSearchStatus = Status.PREPARED;
                return true;
            }
            return false;
        }

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
