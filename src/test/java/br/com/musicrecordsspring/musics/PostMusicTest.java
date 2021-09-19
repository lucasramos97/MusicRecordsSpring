package br.com.musicrecordsspring.musics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import br.com.musicrecordsspring.repositories.MusicRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class PostMusicTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MusicRepository musicRepository;

  private Map<String, Object> postAllAttributesMusic;
  private Map<String, Object> postMinimalAttributesMusic;

  @BeforeEach
  public void commit() {

    postAllAttributesMusic = new HashMap<>();
    postAllAttributesMusic.put("title", "Title Test");
    postAllAttributesMusic.put("artist", "Artist Test");
    postAllAttributesMusic.put("release_date",
        new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    postAllAttributesMusic.put("duration", new SimpleDateFormat("HH:mm:ss").format(new Date()));
    postAllAttributesMusic.put("number_views", 1);
    postAllAttributesMusic.put("feat", true);

    postMinimalAttributesMusic = new HashMap<>();
    postMinimalAttributesMusic.put("title", "Title Test");
    postMinimalAttributesMusic.put("artist", "Artist Test");
    postMinimalAttributesMusic.put("release_date",
        new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    postMinimalAttributesMusic.put("duration", new SimpleDateFormat("HH:mm:ss").format(new Date()));
  }

  @AfterEach
  public void rollback() {
    musicRepository.deleteAll();
  }

  @Test
  public void postAllAttributesMusic() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(postAllAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals(postAllAttributesMusic.get("title"), responseMap.get("title"));
    assertEquals(postAllAttributesMusic.get("artist"), responseMap.get("artist"));
    assertEquals(postAllAttributesMusic.get("release_date"), responseMap.get("release_date"));
    assertEquals(postAllAttributesMusic.get("duration"), responseMap.get("duration"));
    assertEquals(postAllAttributesMusic.get("number_views"), responseMap.get("number_views"));
    assertEquals(postAllAttributesMusic.get("feat"), responseMap.get("feat"));
    assertNull(responseMap.get("deleted"));
    assertNotNull(responseMap.get("created_at"));
    assertNotNull(responseMap.get("updated_at"));
    assertEquals(responseMap.get("created_at"), responseMap.get("updated_at"));
    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
  }

  @Test
  public void postMinimalAttributesMusic() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(postMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals(postMinimalAttributesMusic.get("title"), responseMap.get("title"));
    assertEquals(postMinimalAttributesMusic.get("artist"), responseMap.get("artist"));
    assertEquals(postMinimalAttributesMusic.get("release_date"), responseMap.get("release_date"));
    assertEquals(postMinimalAttributesMusic.get("duration"), responseMap.get("duration"));
    assertEquals(0, responseMap.get("number_views"));
    assertEquals(false, responseMap.get("feat"));
    assertNull(responseMap.get("deleted"));
    assertNotNull(responseMap.get("created_at"));
    assertNotNull(responseMap.get("updated_at"));
    assertEquals(responseMap.get("created_at"), responseMap.get("updated_at"));
    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
  }

  @Test
  public void postMusicWithoutTitleField() throws Exception {

    postMinimalAttributesMusic.put("title", "");
    String jsonRequest = objectMapper.writeValueAsString(postMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Title is required!", responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void postMusicWithoutArtistField() throws Exception {

    postMinimalAttributesMusic.put("artist", "");
    String jsonRequest = objectMapper.writeValueAsString(postMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Artist is required!", responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void postMusicWithoutReleaseDateField() throws Exception {

    postMinimalAttributesMusic.put("release_date", "");
    String jsonRequest = objectMapper.writeValueAsString(postMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Release Date is required!", responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void postMusicWithoutDurationField() throws Exception {

    postMinimalAttributesMusic.put("duration", "");
    String jsonRequest = objectMapper.writeValueAsString(postMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Duration is required!", responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void postMusicWithReleaseDateFuture() throws Exception {

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, 1);
    postMinimalAttributesMusic.put("release_date",
        new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
    String jsonRequest = objectMapper.writeValueAsString(postMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Release Date cannot be future!", responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void postMusicWrongReleaseDateFormat() throws Exception {

    postMinimalAttributesMusic.put("release_date",
        postMinimalAttributesMusic.get("release_date").toString().replace("-", "/"));
    String jsonRequest = objectMapper.writeValueAsString(postMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Wrong Release Date format, try yyyy-MM-dd!", responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void postMusicWrongDurationFormat() throws Exception {

    postMinimalAttributesMusic.put("duration",
        postMinimalAttributesMusic.get("duration").toString().replace(":", "/"));
    String jsonRequest = objectMapper.writeValueAsString(postMinimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("Wrong Duration format, try HH:mm:ss!", responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

}
