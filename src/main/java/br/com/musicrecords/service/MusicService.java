package br.com.musicrecords.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import br.com.musicrecords.model.Music;
import br.com.musicrecords.model.User;
import br.com.musicrecords.repository.MusicRepository;
import br.com.musicrecords.utils.StringUtils;
import br.com.musicrecords.utils.ValidatorUtils;

@Service
public class MusicService {

  @Autowired
  private MusicRepository musicRepository;

  @Autowired
  private UserService userService;

  @Autowired
  private AuthenticationService authenticationService;

  public Music save(Music music) {
    this.beforePersist(music);
    return this.musicRepository.save(music);
  }

  public List<Music> saveAll(List<Music> musics) {
    User user = this.userService.getUserIfExistsByAuthenticationUserName();
    musics.stream().forEach(music -> {
      this.beforePersist(music);
      music.setUser(user);
    });
    return this.musicRepository.saveAll(musics);
  }

  public Music delete(Long musicId) {
    Optional<Music> maybeMusic = this.musicRepository.findById(musicId);
    if (!maybeMusic.isPresent()) {
      String message = String.format("Music not found by id '%s'!", musicId);
      throw new IllegalArgumentException(message);
    }
    Music music = maybeMusic.get();
    if (music.isDeleted()) {
      String message = String.format("Music not found by id '%s'!", musicId);
      throw new IllegalArgumentException(message);
    }
    music.setDeleted(true);
    return this.musicRepository.save(music);
  }

  public Page<Music> findAllMusics(int page) {
    String authenticationUserName = this.authenticationService.getAuthenticationUserName();
    return this.musicRepository.findAllByUserEmailAndDeletedIsFalse(authenticationUserName,
        PageRequest.of(page, 5, Sort.by("artist", "title")));
  }

  public Page<Music> findAllDeletedMusics(int page) {
    String authenticationUserName = this.authenticationService.getAuthenticationUserName();
    return this.musicRepository.findAllByUserEmailAndDeletedIsTrue(authenticationUserName,
        PageRequest.of(page, 5, Sort.by("artist", "title")));
  }

  public String countDeletedMusics() {
    String authenticationUserName = this.authenticationService.getAuthenticationUserName();
    long recordsNumber =
        this.musicRepository.countByUserEmailAndDeletedIsTrue(authenticationUserName);
    return String.valueOf(recordsNumber);
  }

  private void beforePersist(Music music) {
    String launchDate = StringUtils.leaveOnlyNumbers(music.getLaunchDate());
    ValidatorUtils.validLaunchDate(launchDate);
    music.setLaunchDate(launchDate);
    User user = this.userService.getUserIfExistsByAuthenticationUserName();
    music.setUser(user);
  }

}
