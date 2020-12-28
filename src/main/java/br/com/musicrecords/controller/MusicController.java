package br.com.musicrecords.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import br.com.musicrecords.model.MessageResponse;
import br.com.musicrecords.model.Music;
import br.com.musicrecords.service.MusicService;
import io.swagger.annotations.Api;

@Api(tags = "Music")
@CrossOrigin
@RestController
@RequestMapping("/musics")
public class MusicController {

  @Autowired
  private MusicService musicService;

  @GetMapping
  public Page<Music> getMusics(@RequestParam(defaultValue = "0") int page) {
    return this.musicService.findAllMusics(page);
  }

  @PostMapping
  public ResponseEntity<MessageResponse> save(@Valid @RequestBody Music music) {
    try {
      this.musicService.save(music);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<MessageResponse> edit(@PathVariable Long id,
      @Valid @RequestBody Music music) {
    try {
      music.setId(id);
      this.musicService.save(music);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResponse> delete(@PathVariable Long id) {
    try {
      this.musicService.delete(id);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/deleted")
  public Page<Music> getAllDeletedMusics(@RequestParam(defaultValue = "0") int page) {
    return this.musicService.findAllDeletedMusics(page);
  }

  @GetMapping("/deleted/count")
  public ResponseEntity<MessageResponse> getCountDeletedMusics() {
    String recordsNumber = this.musicService.countDeletedMusics();
    return new ResponseEntity<>(new MessageResponse(recordsNumber), HttpStatus.OK);
  }

  @PostMapping("/recover")
  public ResponseEntity<MessageResponse> recoverDeletedMusics(
      @Valid @RequestBody List<Music> musics) {
    try {
      this.musicService.saveAll(musics);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
  }

}
