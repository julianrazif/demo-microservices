package com.example.dss.datasource.service;

import com.example.dss.datasource.repository.impl.ArtistsRepositoryImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Transactional(readOnly = true)
@Service("artistsService")
public class ArtistsService extends ArtistsRepositoryImpl {

  public ArtistsService(@Qualifier("dssEntityManagerFactory") EntityManager em) {
    super(em);
  }
}
