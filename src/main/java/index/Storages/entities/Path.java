/*
 * Created by Aleksandr Smilyanskiy
 * Date: 27.03.17 17:14
 * Project: FileFinder
 *
 * "The more we do, the more we can do"
 * Copyright (c) 2017.
 */

package index.Storages.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import javax.transaction.Transactional;

/**
 * Created by: Aleksandr
 * Date: 27.03.2017
 * Project: FileFinder
 *
 * "The more we do, the more we can do" ©
 */

@Entity
public class Path implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int pid;
  @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Date updated;
  @OneToMany(orphanRemoval = true, mappedBy = "path", fetch = FetchType.LAZY)
  private Set<Occurrence> occurrences = new HashSet<>();
  ;
  private String path;

  public Path(String path) {
    this.path = path;
  }

  public Path() {
  }

  public void addOccurrence(Occurrence occurrence) {
    occurrences.add(occurrence);
    if (occurrence.getPath() != this) {
      occurrence.setPath(this);
    }
  }

  public Set<Occurrence> getOccurrences() {
    return occurrences;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Date getUpdated() {
    return updated;
  }

  public void setUpdated(Date updated) {
    this.updated = updated;
  }

  @PreUpdate
  @PrePersist
  public void updateTimeStamps() {
    updated = new Date();
  }

  @Override
  public String toString() {
    return "Path "
           + "["
           + "path=" + path
           + ", updated=" + updated
           + "]";
  }
}
