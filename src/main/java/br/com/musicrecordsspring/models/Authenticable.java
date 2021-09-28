package br.com.musicrecordsspring.models;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Authenticable implements Serializable {

  private static final long serialVersionUID = -4451256824459936948L;

  private String token;
  private String username;
  private String email;

}
