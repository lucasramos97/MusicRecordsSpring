package br.com.musicrecords.model;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Music implements Serializable {

  private static final long serialVersionUID = 8557374919268342896L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @NotBlank(message = "Title is required!")
  @Column(nullable = false, length = 50)
  private String title;
  @NotBlank(message = "Artist is required!")
  @Column(nullable = false, length = 50)
  private String artist;
  @NotBlank(message = "Launch Date is required!")
  @Column(nullable = false, length = 8)
  private String launchDate;
  @NotBlank(message = "Duration is required!")
  @Column(nullable = false, length = 5)
  private String duration;
  private Long viewsNumber;
  @Column(columnDefinition = "boolean default false")
  private boolean feat;
  @JsonIgnore
  @Column(columnDefinition = "boolean default false")
  private boolean deleted;
  @JsonIgnore
  @ManyToOne(cascade = CascadeType.ALL, optional = false)
  private User user;

}
