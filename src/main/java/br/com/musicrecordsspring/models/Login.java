package br.com.musicrecordsspring.models;

import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import br.com.musicrecordsspring.utils.Messages;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Login implements Serializable {

  private static final long serialVersionUID = -1270674427011768854L;

  @Email(message = Messages.EMAIL_INVALID)
  @NotBlank(message = Messages.EMAIL_IS_REQUIRED)
  private String email;

  @NotBlank(message = Messages.PASSWORD_IS_REQUIRED)
  private String password;

}
