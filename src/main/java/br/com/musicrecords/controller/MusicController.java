package br.com.musicrecords.controller;

import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import br.com.musicrecords.model.User;
import br.com.musicrecords.repository.MusicRepository;
import br.com.musicrecords.repository.UserRepository;
import br.com.musicrecords.security.AuthenticationService;

@CrossOrigin
@RestController
@RequestMapping("/musics")
public class MusicController {

  @Autowired
  private MusicRepository musicRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private AuthenticationService authenticationService;

  @GetMapping
  public Page<Music> getMusics(@RequestParam(defaultValue = "0") int page) {
    String authenticationUserName = this.authenticationService.getAuthenticationUserName();
    return this.musicRepository.findAllByUserEmailAndDeletedIsFalse(authenticationUserName,
        PageRequest.of(page, 5, Sort.by("artist", "title")));
  }

  @PostMapping
  public ResponseEntity<MessageResponse> save(@Valid @RequestBody Music music) {
    try {
      String authenticationUserName = this.authenticationService.getAuthenticationUserName();
      Optional<User> maybeUser = this.userRepository.findByEmail(authenticationUserName);
      if (!maybeUser.isPresent()) {
        String message = String.format("User not found by 'Email' $s!", authenticationUserName);
        return new ResponseEntity<>(new MessageResponse(message), HttpStatus.BAD_REQUEST);
      }
      music.setUser(maybeUser.get());
      this.musicRepository.save(music);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping
  public ResponseEntity<MessageResponse> edit(@Valid @RequestBody Music music) {
    try {
      this.musicRepository.save(music);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @DeleteMapping("/{musicId}")
  public ResponseEntity<MessageResponse> delete(@PathVariable Long musicId) {
    try {
      Music music = this.musicRepository.findById(musicId).get();
      music.setDeleted(true);
      this.musicRepository.save(music);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

}
