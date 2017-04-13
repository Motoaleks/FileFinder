/*
 * Created by Aleksandr Smilyanskiy
 * Date: 27.03.17 19:09
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.Storages.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.transaction.Transactional;

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

  //  @Id
//  @Column(unique = true)
  private String word;

  @OneToMany(orphanRemoval = true, mappedBy = "word", fetch = FetchType.LAZY)
  private Set<Occurrence> occurrences = new HashSet<>();
  ;

  public Word() {
  }

  public Word(String word) {
    this.word = word;
  }

  public String getWord() {
    return word;
  }

  public void addOccurrence(Occurrence occurrence) {
    occurrences.add(occurrence);
    if (occurrence.getWord() != this) {
      occurrence.setWord(this);
    }
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

  public Set<Occurrence> getOccurrences() {
    return occurrences;
  }
}
