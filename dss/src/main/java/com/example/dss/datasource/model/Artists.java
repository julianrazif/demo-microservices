package com.example.dss.datasource.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "artists", schema = "master")
public class Artists implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "album", nullable = false)
  private String album;

  @Column(name = "imageURL", nullable = false)
  private String imageURL;

  @Column(name = "releaseDate", nullable = false)
  private String releaseDate;

  @Column(name = "price", nullable = false)
  private BigDecimal price;

  @Column(name = "sampleURL", nullable = false)
  private String sampleURL;
}
