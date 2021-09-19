package br.com.musicrecordsspring.musics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import java.util.Map;
import java.util.Optional;
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
public class DefinitiveDeleteMusicTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MusicFactory musicFactory;

  @Autowired
  private MusicRepository musicRepository;

  private Music deletedMusic;
  private Music music;

  @BeforeEach
  public void commit() {

    deletedMusic = musicFactory.create(true);
    music = musicFactory.create(false);
  }

  @AfterEach
  public void rollback() {
    musicRepository.deleteAll();
  }

  @Test
  public void definitiveDeleteMusic() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(delete(String.format("/musics/definitive/%s", deletedMusic.getId())))
            .andReturn().getResponse();

    Optional<Music> dbOptionalMusic = musicRepository.findById(deletedMusic.getId());

    assertTrue(dbOptionalMusic.isEmpty());
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  public void definitiveDeleteNonexistentMusic() throws Exception {

    MockHttpServletResponse response = mockMvc
        .perform(delete(String.format("/musics/definitive/%s", 100))).andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Music not found!", responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  public void definitiveDeleteNondeletedMusic() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(delete(String.format("/musics/definitive/%s", music.getId()))).andReturn()
            .getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Music not found!", responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

}
