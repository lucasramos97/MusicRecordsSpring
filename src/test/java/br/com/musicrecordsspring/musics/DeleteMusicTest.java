package br.com.musicrecordsspring.musics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.musicrecordsspring.MusicFactory;
import br.com.musicrecordsspring.models.Music;
import br.com.musicrecordsspring.repositories.MusicRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class DeleteMusicTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MusicFactory musicFactory;

  @Autowired
  private MusicRepository musicRepository;

  private Music music;
  private Music deletedMusic;

  @BeforeEach
  public void commit() {

    music = musicFactory.create(false);
    deletedMusic = musicFactory.create(true);
  }

  @AfterEach
  public void rollback() {
    musicRepository.deleteAll();
  }

  @Test
  public void deleteMusic() throws Exception {

    MockHttpServletResponse response = mockMvc
        .perform(delete(String.format("/musics/%s", music.getId()))).andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    Music dbMusic = musicRepository.findById(music.getId()).get();

    assertEquals(dbMusic.getTitle(), responseMap.get("title"));
    assertEquals(dbMusic.getArtist(), responseMap.get("artist"));
    assertEquals(dbMusic.getReleaseDate().toString(), responseMap.get("release_date"));
    assertEquals(dbMusic.getDuration().toString(), responseMap.get("duration"));
    assertEquals(dbMusic.getNumberViews(), responseMap.get("number_views"));
    assertEquals(dbMusic.getFeat(), responseMap.get("feat"));
    assertTrue(dbMusic.isDeleted());
    assertEquals(dbMusic.getCreatedAt().toString(), responseMap.get("created_at"));
    assertEquals(dbMusic.getUpdatedAt().toString(), responseMap.get("updated_at"));
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  public void deleteNonexistentMusic() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(delete(String.format("/musics/%s", 100))).andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Music not found!", responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  public void deleteDeletedMusic() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(delete(String.format("/musics/%s", deletedMusic.getId()))).andReturn()
            .getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Music not found!", responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

}
