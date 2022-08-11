package com.example.api.domain;

import com.example.api.dto.DomainObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistsDto implements DomainObject {

  private Long artistId;
  private String artistsName;
  private String albumName;
  private String imageURL;
  private String releaseDate;
  private Double price;
  private String sampleURL;
}
