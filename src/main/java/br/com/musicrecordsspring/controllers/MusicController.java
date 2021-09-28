package br.com.musicrecordsspring.controllers;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import br.com.musicrecordsspring.models.Music;
import br.com.musicrecordsspring.models.PagedMusic;
import br.com.musicrecordsspring.services.MusicService;

@CrossOrigin(origins = "http://localhost:3000/")
@RestController
@RequestMapping("/musics")
public class MusicController {

  @Autowired
  private MusicService musicService;

  @GetMapping
  public PagedMusic getAll(@RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "5") int size) {
    return musicService.getAllMusics(page, size);
  }

  @GetMapping("/{id}")
  public Music getById(@PathVariable Long id) {
    return musicService.getMusicById(id);
  }

  @PostMapping
  public ResponseEntity<Music> post(@Valid @RequestBody Music music) {
    return new ResponseEntity<>(musicService.postMusic(music), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public Music put(@PathVariable Long id, @Valid @RequestBody Music music) {
    return musicService.putMusic(id, music);
  }

  @DeleteMapping("/{id}")
  public Music delete(@PathVariable Long id) {
    return musicService.deleteMusic(id);
  }

  @GetMapping("/deleted/count")
  public Long getCountDeletedMusics() {
    return musicService.getCountDeletedMusics();
  }

  @GetMapping("/deleted")
  public PagedMusic getAllDeleted(@RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "5") int size) {
    return musicService.getAllDeletedMusics(page, size);
  }

  @PostMapping("/deleted/restore")
  public int restoreDeleted(@RequestBody List<Music> deletedMusics) throws Exception {
    return musicService.restoreDeletedMusics(deletedMusics);
  }

  @DeleteMapping("/definitive/{id}")
  public void definitiveDelete(@PathVariable Long id) {
    musicService.definitiveDeleteMusic(id);
  }

  @DeleteMapping("/empty-list")
  public int emptyList() {
    return musicService.emptyListMusic();
  }
}
