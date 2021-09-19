package br.com.musicrecordsspring.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import br.com.musicrecordsspring.exceptions.FutureDateException;
import br.com.musicrecordsspring.models.Music;
import br.com.musicrecordsspring.models.PagedMusic;
import br.com.musicrecordsspring.repositories.MusicRepository;

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
    return musicRepository.countByDeletedIsTrue();
  }

  public PagedMusic getAllDeletedMusics(int page, int size) {
    return getMusics(page, size, true);
  }

  public int restoreDeletedMusics(List<Music> deletedMusics) {

    List<Long> musicIds = deletedMusics.stream().map(Music::getId).collect(Collectors.toList());

    return musicRepository.restoreDeletedMusics(musicIds);
  }

  public int emptyListMusic() {
    return musicRepository.deleteAllByDeletedIsTrue();
  }

  public void definitiveDeleteMusic(Long id) {

    Music music = getMusicByIdAndDeleted(id, true);

    musicRepository.delete(music);
  }

  private PagedMusic getMusics(int page, int size, boolean deleted) {

    if (page > 0) {
      page -= 1;
    }

    Page<Music> result = musicRepository.findAllByDeleted(deleted,
        PageRequest.of(page, size, Sort.by("artist", "title")));

    return new PagedMusic(result.getContent(), result.getTotalElements());
  }

  private Music getMusicByIdAndDeleted(Long id, boolean deleted) {

    Optional<Music> optionalMusic = musicRepository.findByIdAndDeleted(id, deleted);

    if (optionalMusic.isEmpty()) {
      throw new EntityNotFoundException("Music not found!");
    }

    return optionalMusic.get();
  }

  private void validateFutureDate(Music music) {

    if (music.getReleaseDate().compareTo(new Date()) > 0) {
      throw new FutureDateException("Release Date cannot be future!");
    }
  }

}
