package br.com.musicrecordsspring.musics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import br.com.musicrecordsspring.factories.MusicFactory;
import br.com.musicrecordsspring.factories.UserFactory;
import br.com.musicrecordsspring.models.Music;
import br.com.musicrecordsspring.models.User;
import br.com.musicrecordsspring.repositories.MusicRepository;
import br.com.musicrecordsspring.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class GetMusicByIdTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MusicFactory musicFactory;

  @Autowired
  private MusicRepository musicRepository;

  @Autowired
  private UserFactory userFactory;

  @Autowired
  private UserRepository userRepository;

  private Music music;
  private Music deletedMusic;

  @BeforeEach
  public void commit() {

    User user = userFactory.create();
    music = musicFactory.create(false, user);
    deletedMusic = musicFactory.create(true, user);
  }

  @AfterEach
  public void rollback() {
    userRepository.deleteAll();
  }

  @Test
  public void getMusicById() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(get(String.format("/musics/%s", music.getId()))).andReturn().getResponse();

    Music dbMusic = musicRepository.findById(music.getId()).get();
    String dbContent = objectMapper.writeValueAsString(dbMusic);

    assertEquals(dbContent, response.getContentAsString());
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  public void getNonexistentMusicById() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(get(String.format("/musics/%s", 100))).andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, String> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Music not found!", responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  public void getDeletedMusicById() throws Exception {

    MockHttpServletResponse response = mockMvc
        .perform(get(String.format("/musics/%s", deletedMusic.getId()))).andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, String> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Music not found!", responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

}
