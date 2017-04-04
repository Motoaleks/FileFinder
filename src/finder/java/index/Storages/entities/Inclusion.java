/*
 * Created by Aleksandr Smilyanskiy
 * Date: 28.03.17 16:36
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.Storages.entities;

import java.nio.file.Path;
import java.util.Date;

/**
 * Represents
 */
public class Inclusion {

  private long place;
  private Date updated;
  private Path file;

  public Inclusion(Path file, long place, Date updated) {
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

  public Path getFile() {
    return file;
  }
}
