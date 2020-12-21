package br.com.musicrecords.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @NotBlank(message = "Name is required!")
  @Column(nullable = false, length = 60)
  private String name;
  @Email(message = "Valid E-Mail format is required!")
  @NotBlank(message = "E-Mail is required!")
  @Column(nullable = false, unique = true, length = 100)
  private String email;
  @JsonProperty(access = Access.WRITE_ONLY)
  @NotBlank(message = "Password is required!")
  @Column(nullable = false, length = 100)
  private String password;

}
