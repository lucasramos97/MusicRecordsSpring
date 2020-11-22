package br.com.musicrecords.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  public Page<Music> getMusics(@RequestParam(defaultValue = "0") int page) {
    return musicRepository.findAll(PageRequest.of(page, 5, Sort.by("artist", "title")));
  }

  @PostMapping
  public ResponseEntity<Music> save(@RequestBody Music music) {
    try {
      this.musicRepository.save(music);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

}
