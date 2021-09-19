package br.com.musicrecordsspring.models;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PagedMusic {

  private List<Music> content;
  private Long total;

}
