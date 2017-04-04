/*
 * Created by Aleksandr Smilyanskiy
 * Date: 27.03.17 17:31
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.Storages.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by: Aleksandr
 * Date: 27.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */
@Entity
public class Occurrence implements Serializable {
//  @GeneratedValue(strategy = GenerationType.AUTO)
//  private int nid;

  @Id
  @GeneratedValue
  private long oid;

  private long place;

  @ManyToOne(fetch = FetchType.LAZY)
  private Path path;

  @ManyToOne(fetch = FetchType.LAZY)
  private Word word;

  public Occurrence(Word word, Path file, int place) {
    this.word = word;
    this.path = file;
    this.place = place;
  }

  public Occurrence() {
  }


  public Path getPath() {
    return path;
  }

  public void setPath(Path path) {
    this.path = path;
//    path.addOccurance(this);
  }

  public long getPlace() {
    return place;
  }

  public void setPlace(int place) {
    this.place = place;
  }

  public Word getWord() {
    return word;
  }

  public void setWord(Word word) {
    this.word = word;
//    word.addOccurrence(this);
  }

  @Override
  public String toString() {
    return "Occurrence "
           + "["
           + "word=" + (word == null ? null : word.getWord())
           + ", path=" + (path == null ? null : path.getPath())
           + ", placeInFile=" + place
           + "]";
//           + ", path=" + (path == null ? null : path.getPid()) + "]";
  }
}
