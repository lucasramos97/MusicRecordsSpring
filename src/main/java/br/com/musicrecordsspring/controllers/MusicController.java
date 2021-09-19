package br.com.musicrecordsspring.controllers;

import java.util.Date;
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
import br.com.musicrecordsspring.exceptions.FutureDateException;
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

    if (page > 0) {
      page -= 1;
    }

    Page<Music> result = musicService.getMusics(page, size);

    return new PagedMusic(result.getContent(), result.getTotalElements());
  }

  @GetMapping("/{id}")
  public Music getById(@PathVariable Long id) {
    return musicService.getMusicById(id);
  }

  @PostMapping
  public ResponseEntity<Music> post(@Valid @RequestBody Music music) {

    validateFutureDate(music);

    if (music.getNumberViews() == null) {
      music.setNumberViews(0);
    }

    if (music.getFeat() == null) {
      music.setFeat(false);
    }

    return new ResponseEntity<>(musicService.createOrUpdateMusic(music), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public Music put(@PathVariable Long id, @Valid @RequestBody Music music) {

    validateFutureDate(music);

    Music dbMusic = musicService.getMusicById(id);
    dbMusic.setTitle(music.getTitle());
    dbMusic.setArtist(music.getArtist());
    dbMusic.setReleaseDate(music.getReleaseDate());
    dbMusic.setDuration(music.getDuration());

    if (music.getNumberViews() != null) {
      dbMusic.setNumberViews(music.getNumberViews());
    }

    if (music.getFeat() != null) {
      dbMusic.setFeat(music.getFeat());
    }

    return musicService.createOrUpdateMusic(dbMusic);
  }

  @DeleteMapping("/{id}")
  public Music delete(@PathVariable Long id) {

    Music music = musicService.getMusicById(id);
    music.setDeleted(true);

    return musicService.createOrUpdateMusic(music);
  }

  private void validateFutureDate(Music music) {

    if (music.getReleaseDate().compareTo(new Date()) > 0) {
      throw new FutureDateException("Release Date cannot be future!");
    }
  }

}
