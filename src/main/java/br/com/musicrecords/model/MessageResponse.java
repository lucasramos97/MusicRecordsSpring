package br.com.musicrecords.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageResponse {

  private String message;
  private String username;

  public MessageResponse(String message) {
    this.message = message;
  }

}
