/*
 * Created by Aleksandr Smilyanskiy
 * Date: 28.03.17 16:36
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.Storages;

import java.nio.file.Path;
import java.util.Date;

/**
 * Represents
 */
public class Inclusion {

  private String word;
  private long place;
  private Date updated;
  private Path file;

  public Inclusion(String word, Path file, long place, Date updated) {
    this.word = word;
    this.place = place;
    this.updated = updated;
    this.file = file;
  }

  public Date getUpdated() {
    return updated;
  }

  public long getPlace() {
    return place;
  }

  public Path getPath() {
    return file;
  }

  public String getWord() {
    return word;
  }

  @Override
  public boolean equals(Object other) {
    if (other == null) {
      return false;
    }
    if (other == this) {
      return true;
    }
    if (!(other instanceof Inclusion)) {
      return false;
    }
    Inclusion otherMyClass = (Inclusion) other;
    boolean worked = word == null ? null == otherMyClass.word : word.equals(otherMyClass.word);
    worked &= place == otherMyClass.place;
    worked &= file == null ? null == otherMyClass.file : file.equals(otherMyClass.file);
    return worked;
  }
}
