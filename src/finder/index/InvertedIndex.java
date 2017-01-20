package finder.index;

import finder.search.Request;
import finder.search.Result;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by: Aleksandr
 * Date: 19.01.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
public class InvertedIndex extends Index {

  private final HashMap<String, Set<String>> container;
  private String symbols = "[!@#$%^&*()_+1234567890-=|/.,<>]";
  private PathMatcher pathMatcher;
  private String searchFor;

  public InvertedIndex() {
    super();
    container = new HashMap<>();
  }

  private void indexRow(String row, String path) {
    for (String word : row.split(" ")) {
      // todo: stripping the string here, biatch
      addToIndex(word, path);
    }
  }

  private void addToIndex(String string, String path) {
    string = string.toLowerCase();
    Set<String> paths = container.computeIfAbsent(string, k -> new TreeSet<String>());
    paths.add(path);
  }

  @Override
  protected void initializeAvailableParameters() {
    // Setting available parameters
    super.availableParameters.addAll(Arrays.asList(Parameter.WORDS,
                                                   Parameter.FORMATS));
  }

  private void searchConcrete(Request request) {
    if (!container.containsKey(request.getSearchFor().toLowerCase())) {
      return;
    }
    Result<Path> result = request.getResult();
    for (String path : container.get(request.getSearchFor().toLowerCase())) {
      result.addResult(Paths.get(path));
    }
  }

  private void searchEquality(Request request) {
    Result<Path> result = request.getResult();
    for (Entry<String, Set<String>> entry : container.entrySet()) {
      if (entry.getKey().matches(request.getSearchFor().toLowerCase())) {
        for (String path : container.get(request.getSearchFor().toLowerCase())) {
          result.addResult(Paths.get(path));
        }
      }
    }
  }

  private void searchSimple(Request request) {
    //todo: Call for simple searcher
  }

  @Override
  public void search(Request request) {
    searchConcrete(request); // fast search in index by straight request
    searchEquality(request); // mediocre search for all keys and find ones, matching the pattern
    searchSimple(request); // very slow search by straight looking for files
  }

  @Override
  public void index(Path indexPath) {
    if (indexPath.getFileName() != null) {
      indexRow(indexPath.getFileName().toString(), indexPath.normalize().toString());
    }
//    }
//
//    // todo: try all supported encodings, cause it can cause MalformedInputException
//    try (Stream<String> stream = Files.lines(indexPath)) {
//      stream.forEach(s -> indexRow(s, indexPath.normalize().toString()));
//    } catch (UncheckedIOException ignored) {
//      System.out.println("WTF MAN");
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
  }


  @Override
  public void changeState(State state) {

  }
}
