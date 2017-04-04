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
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

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
  private int              pid;
  @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Date             updated;
  @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Occurrence> word;


  private String path;
//  @OneToMany(cascade = CascadeType.ALL, mappedBy = "path", orphanRemoval = true, fetch = FetchType.LAZY)
//  private List<Occurrence> connected = new ArrayList<>();

  public Path(String path) {
    this.path = path;
  }

  public Path() {
  }

//  public void addOccurance(Occurrence occurrence) {
//    connected.add(occurrence);
//  }

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
