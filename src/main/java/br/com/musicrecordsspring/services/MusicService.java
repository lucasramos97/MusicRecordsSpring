package br.com.musicrecordsspring.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import br.com.musicrecordsspring.exceptions.FutureDateException;
import br.com.musicrecordsspring.models.Music;
import br.com.musicrecordsspring.models.PagedMusic;
import br.com.musicrecordsspring.models.User;
import br.com.musicrecordsspring.repositories.MusicRepository;
import br.com.musicrecordsspring.utils.Messages;

@Service
public class MusicService {

  @Autowired
  private MusicRepository musicRepository;

  public PagedMusic getAllMusics(int page, int size) {
    return getMusics(page, size, false);
  }

  public Music getMusicById(Long id) {
    return getMusicByIdAndDeleted(id, false);
  }

  public Music postMusic(Music music) {

    validateFutureDate(music);

    if (music.getNumberViews() == null) {
      music.setNumberViews(0);
    }

    if (music.getFeat() == null) {
      music.setFeat(false);
    }

    music.setUser(getAuthenticationUser());

    return musicRepository.save(music);
  }

  public Music putMusic(Long id, Music music) {

    validateFutureDate(music);

    Music dbMusic = getMusicById(id);
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

    return musicRepository.save(dbMusic);
  }

  public Music deleteMusic(Long id) {

    Music music = getMusicByIdAndDeleted(id, false);
    music.setDeleted(true);

    return musicRepository.save(music);
  }

  public Long getCountDeletedMusics() {
    return musicRepository.countByUserAndDeleted(getAuthenticationUser(), true);
  }

  public PagedMusic getAllDeletedMusics(int page, int size) {
    return getMusics(page, size, true);
  }

  public int restoreDeletedMusics(List<Music> deletedMusics) throws Exception {

    List<Long> musicIds = deletedMusics.stream().map(Music::getId).collect(Collectors.toList());

    if (musicIds.contains(null)) {
      throwIdIsrequiredException();
    }

    return musicRepository.restoreDeletedMusicsByUser(musicIds, getAuthenticationUser());
  }

  public void definitiveDeleteMusic(Long id) {

    Music music = getMusicByIdAndDeleted(id, true);

    musicRepository.delete(music);
  }

  public int emptyListMusic() {
    return musicRepository.deleteAllByUserAndDeletedIsTrue(getAuthenticationUser());
  }

  private User getAuthenticationUser() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  private PagedMusic getMusics(int page, int size, boolean deleted) {

    if (page > 0) {
      page -= 1;
    }

    Pageable pageable = PageRequest.of(page, size, Sort.by("artist", "title"));

    Page<Music> result =
        musicRepository.findAllByUserAndDeleted(getAuthenticationUser(), deleted, pageable);

    return new PagedMusic(result.getContent(), result.getTotalElements());
  }

  private Music getMusicByIdAndDeleted(Long id, boolean deleted) {

    Optional<Music> optionalMusic =
        musicRepository.findByIdAndUserAndDeleted(id, getAuthenticationUser(), deleted);

    if (optionalMusic.isEmpty()) {
      throw new EntityNotFoundException(Messages.MUSIC_NOT_FOUND);
    }

    return optionalMusic.get();
  }

  private void validateFutureDate(Music music) {

    if (music.getReleaseDate().compareTo(new Date()) > 0) {
      throw new FutureDateException(Messages.RELEASE_DATE_CANNOT_BE_FUTURE);
    }
  }

  private void throwIdIsrequiredException() throws Exception {

    MethodParameter parameter = new MethodParameter(Music.class.getConstructor(), -1);
    BindingResult bindingResult = new BindException(Music.class, "music");
    bindingResult.addError(new ObjectError("music", Messages.ID_IS_REQUIRED));

    throw new MethodArgumentNotValidException(parameter, bindingResult);
  }
}
