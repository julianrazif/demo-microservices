package com.example.dss.datasource.repository.impl;

import com.example.dss.datasource.model.Artists;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;

public abstract class ArtistsRepositoryImpl extends SimpleJpaRepository<Artists, Long> {

  public ArtistsRepositoryImpl(@Qualifier("dssEntityManagerFactory") EntityManager em) {
    super(Artists.class, em);
  }
}
