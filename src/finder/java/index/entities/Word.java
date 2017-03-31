/*
 * Created by Aleksandr Smilyanskiy
 * Date: 27.03.17 19:09
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Created by: Aleksandr
 * Date: 27.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */

@Entity
public class Word implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long wid;
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified;

  private String word;
//  @OneToMany(cascade = CascadeType.ALL, mappedBy = "word",orphanRemoval = true, fetch= FetchType.LAZY)
//  private List<Occurrence> entries = new ArrayList<>();

  public Word() {

  }

  public Word(String word) {
    this.word = word;
  }

//  public void addOccurrence(Occurrence occurrence) {
//    entries.add(occurrence);
//    occurrence.setWord(this);
//  }

  public String getWord() {
    return word;
  }

  @PreUpdate
  @PrePersist
  public void updateTimeStamps() {
    lastModified = new Date();
  }

  @Override
  public String toString() {
    return "Word [word=" + word + ", lastModified=" + (lastModified == null ? null : lastModified) + "]";
  }
}
