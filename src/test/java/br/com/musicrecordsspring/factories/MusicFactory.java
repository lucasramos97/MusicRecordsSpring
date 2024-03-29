package br.com.musicrecordsspring.factories;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.github.javafaker.Faker;
import br.com.musicrecordsspring.models.Music;
import br.com.musicrecordsspring.models.User;
import br.com.musicrecordsspring.repositories.MusicRepository;

@Component
public class MusicFactory {

  @Autowired
  private MusicRepository musicRepository;

  private Faker faker;

  @PostConstruct
  public void init() {
    faker = new Faker();
  }

  public List<Music> createBatch(int quant, boolean deleted, User user) {

    List<Music> musics = new ArrayList<>();
    for (int i = 0; i < quant; i++) {
      musics.add(createMusic(deleted, user));
    }

    return musicRepository.saveAll(musics);
  }

  public Music create(boolean deleted, User user) {
    return musicRepository.save(createMusic(deleted, user));
  }

  private Music createMusic(boolean deleted, User user) {

    Music music = new Music();
    music.setTitle(StringUtils.join(faker.lorem().words(3), " "));
    music.setArtist(faker.name().name());
    music.setReleaseDate(faker.date().birthday());
    music.setDuration(faker.date().birthday());
    music.setNumberViews((int) faker.number().randomNumber());
    music.setFeat(faker.bool().bool());
    music.setDeleted(deleted);
    music.setUser(user);

    return music;
  }
}
