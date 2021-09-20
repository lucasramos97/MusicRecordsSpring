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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

  @NotBlank(message = "Title is required!")
  @Column(nullable = false, length = 100)
  private String title;

  @NotBlank(message = "Artist is required!")
  @Column(nullable = false, length = 100)
  private String artist;

  @JsonFormat(pattern = "yyyy-MM-dd")
  @JsonProperty("release_date")
  @NotNull(message = "Release Date is required!")
  @Temporal(TemporalType.DATE)
  @Column(name = "release_date", nullable = false)
  private Date releaseDate;

  @JsonFormat(pattern = "HH:mm:ss")
  @NotNull(message = "Duration is required!")
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

  @ManyToOne
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(nullable = false)
  private User user;

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
