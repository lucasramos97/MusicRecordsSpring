package br.com.musicrecordsspring.services;

import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import br.com.musicrecordsspring.models.Music;
import br.com.musicrecordsspring.repositories.MusicRepository;

@Service
public class MusicService {

  @Autowired
  private MusicRepository musicRepository;

  public Page<Music> getMusics(int page, int size) {
    return musicRepository
        .findAllByDeletedIsFalse(PageRequest.of(page, size, Sort.by("artist", "title")));
  }

  public Music getMusicById(Long id) {

    Optional<Music> optionalMusic = musicRepository.findByIdAndDeletedIsFalse(id);

    if (optionalMusic.isEmpty()) {
      throw new EntityNotFoundException("Music not found!");
    }

    return optionalMusic.get();
  }

  public Music createOrUpdateMusic(Music music) {
    return musicRepository.save(music);
  }

}
