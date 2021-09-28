package br.com.musicrecordsspring.models;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import br.com.musicrecordsspring.utils.Messages;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {

  private static final long serialVersionUID = 5070069591987517433L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = Messages.USERNAME_IS_REQUIRED)
  @Column(nullable = false, length = 100)
  private String username;

  @Email(message = Messages.EMAIL_INVALID)
  @NotBlank(message = Messages.EMAIL_IS_REQUIRED)
  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @NotBlank(message = Messages.PASSWORD_IS_REQUIRED)
  @Column(nullable = false)
  private String password;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
  @JsonProperty("created_at")
  @Column(name = "created_at", nullable = false, updatable = false)
  @CreatedDate
  private Date createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.S")
  @JsonProperty("updated_at")
  @Column(name = "updated_at")
  @LastModifiedDate
  private Date updatedAt;

}
