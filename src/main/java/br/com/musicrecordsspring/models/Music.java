package br.com.musicrecordsspring.models;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.OptBoolean;
import br.com.musicrecordsspring.utils.Messages;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Music implements Serializable {

  private static final long serialVersionUID = -8942813705359533480L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = Messages.TITLE_IS_REQUIRED)
  @Column(nullable = false, length = 100)
  private String title;

  @NotBlank(message = Messages.ARTIST_IS_REQUIRED)
  @Column(nullable = false, length = 100)
  private String artist;

  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd", lenient = OptBoolean.FALSE)
  @JsonProperty("release_date")
  @NotNull(message = Messages.RELEASE_DATE_IS_REQUIRED)
  @Temporal(TemporalType.DATE)
  @Column(name = "release_date", nullable = false)
  private Date releaseDate;

  @JsonFormat(shape = Shape.STRING, pattern = "HH:mm:ss", lenient = OptBoolean.FALSE)
  @NotNull(message = Messages.DURATION_IS_REQUIRED)
  @Temporal(TemporalType.TIME)
  @Column(nullable = false)
  private Date duration;

  @JsonProperty("number_views")
  @Column(name = "number_views", columnDefinition = "integer default 0")
  private Integer numberViews;

  @Column(columnDefinition = "boolean default false")
  private Boolean feat;

  @JsonIgnore
  @Column(columnDefinition = "boolean default false")
  private boolean deleted;

  @JsonIgnore
  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  @JsonProperty("created_at")
  @Column(name = "created_at", nullable = false, updatable = false)
  @CreatedDate
  private Date createdAt;

  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  @JsonProperty("updated_at")
  @Column(name = "updated_at")
  @LastModifiedDate
  private Date updatedAt;

  @Override
  public int hashCode() {

    int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());

    return result;
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj)
      return true;

    if (obj == null)
      return false;

    if (getClass() != obj.getClass())
      return false;

    Music other = (Music) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;

    return true;
  }

}
