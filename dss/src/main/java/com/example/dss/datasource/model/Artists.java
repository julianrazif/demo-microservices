package com.example.dss.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Cacheable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "artists", schema = "public")
@SQLDelete(sql = "update Artists set is_deleted=true where id=?")
@Where(clause = "is_deleted=false")
public class Artists implements Serializable {

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

  @Column(name = "is_deleted", nullable = false)
  private boolean isDeleted;
}
