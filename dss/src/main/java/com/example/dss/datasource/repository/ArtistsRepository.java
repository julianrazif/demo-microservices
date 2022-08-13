package com.example.dss.datasource.repository;

import com.example.dss.datasource.model.Artists;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistsRepository extends JpaRepository<Artists, Long> {
}
