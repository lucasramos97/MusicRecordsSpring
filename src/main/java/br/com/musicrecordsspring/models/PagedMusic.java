package br.com.musicrecordsspring.models;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PagedMusic implements Serializable {

  private static final long serialVersionUID = -4256393450299697108L;

  private List<Music> content;
  private Long total;

}
