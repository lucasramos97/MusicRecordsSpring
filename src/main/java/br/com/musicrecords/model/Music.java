package br.com.musicrecords.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Music implements Serializable {

  private static final long serialVersionUID = 8557374919268342896L;

  @Id
  @GeneratedValue
  private Long id;
  @Column(length = 50)
  private String title;
  @Column(length = 50)
  private String artist;
  private LocalDate launchDate;
  private LocalTime duration;
  private Long viewsNumber;
  private boolean feat;

}
