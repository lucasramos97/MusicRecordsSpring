package br.com.musicrecords.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import br.com.musicrecords.model.Music;
import br.com.musicrecords.repository.MusicRepository;

@CrossOrigin
@RestController
@RequestMapping("/musics")
public class MusicController {

  @Autowired
  private MusicRepository musicRepository;

  @GetMapping
  public List<Music> getMusics() {
    return musicRepository.findAll(Sort.by("artist", "title"));
  }

}
