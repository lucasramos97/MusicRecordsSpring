package br.com.musicrecords.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import br.com.musicrecords.utils.StringUtils;

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
      String launchDate = StringUtils.leaveOnlyNumbers(music.getLaunchDate());
      this.validLaunchDate(launchDate);
      User user = getUserIfExistsByAuthenticationUserName();
      music.setUser(user);
      music.setLaunchDate(launchDate);
      this.musicRepository.save(music);
      return new ResponseEntity<>(HttpStatus.CREATED);
    } catch (UsernameNotFoundException | IllegalArgumentException e) {
      return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  @PutMapping
  public ResponseEntity<MessageResponse> edit(@Valid @RequestBody Music music) {
    try {
      String launchDate = StringUtils.leaveOnlyNumbers(music.getLaunchDate());
      this.validLaunchDate(launchDate);
      music.setLaunchDate(launchDate);
      this.musicRepository.save(music);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
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

  @GetMapping("/deleted")
  public Page<Music> getAllDeletedMusics(@RequestParam(defaultValue = "0") int page) {
    String authenticationUserName = this.authenticationService.getAuthenticationUserName();
    return this.musicRepository.findAllByUserEmailAndDeletedIsTrue(authenticationUserName,
        PageRequest.of(page, 5, Sort.by("artist", "title")));
  }

  @GetMapping("/deleted/count")
  public ResponseEntity<MessageResponse> getCountDeletedMusics() {
    String authenticationUserName = this.authenticationService.getAuthenticationUserName();
    long recordsNumber =
        this.musicRepository.countByUserEmailAndDeletedIsTrue(authenticationUserName);
    return new ResponseEntity<>(new MessageResponse(String.valueOf(recordsNumber)), HttpStatus.OK);
  }

  @PostMapping("/recover")
  public ResponseEntity<MessageResponse> recoverDeletedMusics(
      @Valid @RequestBody List<Music> musics) {
    try {
      User user = getUserIfExistsByAuthenticationUserName();
      musics.stream().forEach(music -> {
        music.setUser(user);
        music.setLaunchDate(StringUtils.leaveOnlyNumbers(music.getLaunchDate()));
      });
      this.musicRepository.saveAll(musics);
      return new ResponseEntity<>(HttpStatus.OK);
    } catch (UsernameNotFoundException e) {
      return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
  }

  private User getUserIfExistsByAuthenticationUserName() {
    String authenticationUserName = this.authenticationService.getAuthenticationUserName();
    Optional<User> maybeUser = this.userRepository.findByEmail(authenticationUserName);
    if (!maybeUser.isPresent()) {
      String message = String.format("User not found by 'E-Mail' $s!", authenticationUserName);
      throw new UsernameNotFoundException(message);
    }
    return maybeUser.get();
  }

  private void validLaunchDate(String launchDate) {
    try {
      if (launchDate.length() != 8) {
        String errorMessage =
            String.format("Invalid date value '%s' follow 'ddMMyyyy' pattern!", launchDate);
        throw new IllegalArgumentException(errorMessage);
      }
      LocalDate.parse(launchDate, DateTimeFormatter.ofPattern("ddMMyyyy"));
    } catch (DateTimeParseException e) {
      String invalidDate = e.getMessage().split("'")[1];
      String typeDate = e.getMessage().split("Invalid value for ")[1].split(" ")[0];
      String validValues = e.getMessage().split("valid values ")[1].split("[)]")[0];
      String invalidValue = e.getMessage().split(": ")[2];
      String errorMessage = String.format(
          "Date '%s' is invalid, %s does not exist, valid values are (%s) informed: %s!",
          invalidDate, this.formattedTypeDate(typeDate), validValues, invalidValue);
      throw new IllegalArgumentException(errorMessage);
    }
  }

  private String formattedTypeDate(String typeDate) {
    String[] dayMonthOrYear = typeDate.split("Of");
    String dayOrMonth = dayMonthOrYear[0];
    String monthOrYear = dayMonthOrYear[1];
    return String.format("%s of %s", dayOrMonth.toLowerCase(), monthOrYear.toLowerCase());
  }

}
