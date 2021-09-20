package br.com.musicrecordsspring.musics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.musicrecordsspring.factories.MusicFactory;
import br.com.musicrecordsspring.factories.UserFactory;
import br.com.musicrecordsspring.models.Music;
import br.com.musicrecordsspring.models.User;
import br.com.musicrecordsspring.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class PutMusicTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MusicFactory musicFactory;

  @Autowired
  private UserFactory userFactory;

  @Autowired
  private UserRepository userRepository;

  private Music music;
  private Music deletedMusic;
  private Map<String, Object> putAllAttributesMusic;
  private Map<String, Object> putMinimalAttributesMusic;

  @BeforeEach
  public void commit() {

    User user = userFactory.create();
    music = musicFactory.create(false, user);
    deletedMusic = musicFactory.create(true, user);

    putAllAttributesMusic = new HashMap<>();
    putAllAttributesMusic.put("title", String.format("%s Test", music.getTitle()));
    putAllAttributesMusic.put("artist", String.format("%s Test", music.getArtist()));
    putAllAttributesMusic.put("release_date",
        new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    putAllAttributesMusic.put("duration", new SimpleDateFormat("HH:mm:ss").format(new Date()));
    putAllAttributesMusic.put("number_views", music.getNumberViews() + 1);
    putAllAttributesMusic.put("feat", !music.getFeat());

    putMinimalAttributesMusic = new HashMap<>();
    putMinimalAttributesMusic.put("title", String.format("%s Test", music.getTitle()));
    putMinimalAttributesMusic.put("artist", String.format("%s Test", music.getArtist()));
    putMinimalAttributesMusic.put("release_date",
        new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    putMinimalAttributesMusic.put("duration", new SimpleDateFormat("HH:mm:ss").format(new Date()));
  }

  @AfterEach
  public void rollback() {
    userRepository.deleteAll();
  }

  @Test
  public void putAllAttributesMusic() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(putAllAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId()))
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals(putAllAttributesMusic.get("title"), responseMap.get("title"));
    assertEquals(putAllAttributesMusic.get("artist"), responseMap.get("artist"));
    assertEquals(putAllAttributesMusic.get("release_date"), responseMap.get("release_date"));
    assertEquals(putAllAttributesMusic.get("duration"), responseMap.get("duration"));
    assertEquals(putAllAttributesMusic.get("number_views"), responseMap.get("number_views"));
    assertEquals(putAllAttributesMusic.get("feat"), responseMap.get("feat"));
    assertNull(responseMap.get("deleted"));
    assertNotNull(responseMap.get("created_at"));
    assertNotNull(responseMap.get("updated_at"));
    assertNotEquals(responseMap.get("updated_at"), responseMap.get("created_at"));
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  public void putMinimalAttributesMusic() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(putMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId()))
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals(putMinimalAttributesMusic.get("title"), responseMap.get("title"));
    assertEquals(putMinimalAttributesMusic.get("artist"), responseMap.get("artist"));
    assertEquals(putMinimalAttributesMusic.get("release_date"), responseMap.get("release_date"));
    assertEquals(putMinimalAttributesMusic.get("duration"), responseMap.get("duration"));
    assertEquals(music.getNumberViews(), responseMap.get("number_views"));
    assertEquals(music.getFeat(), responseMap.get("feat"));
    assertNull(responseMap.get("deleted"));
    assertNotNull(responseMap.get("created_at"));
    assertNotNull(responseMap.get("updated_at"));
    assertNotEquals(responseMap.get("updated_at"), responseMap.get("created_at"));
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  public void putNonexistentMusic() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(putMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc.perform(put(String.format("/musics/%s", 100))
        .contentType(MediaType.APPLICATION_JSON).content(jsonRequest)).andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Music not found!", responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  public void putDeletedMusic() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(putMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", deletedMusic.getId()))
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Music not found!", responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  public void putMusicWithoutTitleField() throws Exception {

    putMinimalAttributesMusic.put("title", "");
    String jsonRequest = objectMapper.writeValueAsString(putMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId()))
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Title is required!", responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void putMusicWithoutArtistField() throws Exception {

    putMinimalAttributesMusic.put("artist", "");
    String jsonRequest = objectMapper.writeValueAsString(putMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId()))
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Artist is required!", responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void putMusicWithoutReleaseDateField() throws Exception {

    putMinimalAttributesMusic.put("release_date", "");
    String jsonRequest = objectMapper.writeValueAsString(putMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId()))
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Release Date is required!", responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void putMusicWithoutDurationField() throws Exception {

    putMinimalAttributesMusic.put("duration", "");
    String jsonRequest = objectMapper.writeValueAsString(putMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId()))
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Duration is required!", responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void putMusicWithReleaseDateFuture() throws Exception {

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, 1);
    putMinimalAttributesMusic.put("release_date",
        new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
    String jsonRequest = objectMapper.writeValueAsString(putMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId()))
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Release Date cannot be future!", responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void putMusicWrongReleaseDateFormat() throws Exception {

    putMinimalAttributesMusic.put("release_date",
        putMinimalAttributesMusic.get("release_date").toString().replace("-", "/"));
    String jsonRequest = objectMapper.writeValueAsString(putMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId()))
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Wrong Release Date format, try yyyy-MM-dd!", responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void putMusicWrongDurationFormat() throws Exception {

    putMinimalAttributesMusic.put("duration",
        putMinimalAttributesMusic.get("duration").toString().replace(":", "/"));
    String jsonRequest = objectMapper.writeValueAsString(putMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId()))
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Wrong Duration format, try HH:mm:ss!", responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

}
