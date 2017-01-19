package finder.index;

import finder.search.Request;
import finder.search.Result;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
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

  public InvertedIndex() {
    super();
    container = new HashMap<>();
  }

  private void indexRow(String row, String path) {
    for (String word : row.split(" ")) {
      addToIndex(word, path);
    }

//    StringTokenizer stringTokenizer = new StringTokenizer(row);
//    while (stringTokenizer.hasMoreTokens()) {
//      addToIndex(stringTokenizer.nextToken(), path);
//    }
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

  @Override
  public void search(Request request) {
    String searchFor = request.getSearchFor().toLowerCase();
    if (!container.containsKey(searchFor)) {
      return;
    }
    Result<Path> result = request.getResult();
    for (String path : container.get(searchFor)) {
      result.addResult(Paths.get(path));
    }
  }

  @Override
  public void index(Path indexPath) {
//    if (Files.isDirectory(indexPath)) {
    if (indexPath.getFileName() != null) {
      indexRow(indexPath.getFileName().toString(), indexPath.normalize().toString());
    }
    return;
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
