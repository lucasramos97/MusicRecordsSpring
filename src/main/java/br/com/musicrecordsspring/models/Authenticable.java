package br.com.musicrecordsspring.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Authenticable {

  private String token;
  private String username;
  private String email;

}
