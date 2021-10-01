package br.com.musicrecordsspring.musics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import br.com.musicrecordsspring.BaseTdd;
import br.com.musicrecordsspring.models.Music;
import br.com.musicrecordsspring.utils.Messages;

class PostMusicTest extends BaseTdd {

  @BeforeAll
  public void commitPerClass() throws Exception {

    user1 = userFactory.create("1");
    tokenUser1 = generateToken(user1);
  }

  @BeforeEach
  public void commitPerMethod() {

    allAttributesMusic = new HashMap<>();
    allAttributesMusic.put("title", "Title Test");
    allAttributesMusic.put("artist", "Artist Test");
    allAttributesMusic.put("release_date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    allAttributesMusic.put("duration", new SimpleDateFormat("HH:mm:ss").format(new Date()));
    allAttributesMusic.put("number_views", 1);
    allAttributesMusic.put("feat", true);

    minimalAttributesMusic = new HashMap<>();
    minimalAttributesMusic.put("title", "Title Test");
    minimalAttributesMusic.put("artist", "Artist Test");
    minimalAttributesMusic.put("release_date",
        new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    minimalAttributesMusic.put("duration", new SimpleDateFormat("HH:mm:ss").format(new Date()));
  }

  @AfterAll
  public void rollback() {
    userRepository.deleteAll();
  }

  @Test
  void postAllAttributesMusic() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(allAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    Long musicId = Long.valueOf(responseMap.get("id").toString());
    Music dbMusic = musicRepository.findByIdAndUser(musicId, user1).get();
    String dbMusicContent = objectMapper.writeValueAsString(dbMusic);
    Map<String, Object> dbMusicContentMap = convertStringToMap(dbMusicContent);

    boolean validTitle = allEquals(allAttributesMusic.get("title"), dbMusicContentMap.get("title"),
        responseMap.get("title"));

    boolean validArtist = allEquals(allAttributesMusic.get("artist"),
        dbMusicContentMap.get("artist"), responseMap.get("artist"));

    boolean validReleaseDate = allEquals(allAttributesMusic.get("release_date"),
        dbMusicContentMap.get("release_date"), responseMap.get("release_date"));

    boolean validDuration = allEquals(allAttributesMusic.get("duration"),
        dbMusicContentMap.get("duration"), responseMap.get("duration"));

    boolean validNumberViews = allEquals(allAttributesMusic.get("number_views"),
        dbMusicContentMap.get("number_views"), responseMap.get("number_views"));

    boolean validFeat = allEquals(allAttributesMusic.get("feat"), dbMusicContentMap.get("feat"),
        responseMap.get("feat"));

    assertTrue(validTitle);
    assertTrue(validArtist);
    assertTrue(validReleaseDate);
    assertTrue(validDuration);
    assertTrue(validNumberViews);
    assertTrue(validFeat);
    assertNull(responseMap.get("deleted"));
    assertNull(responseMap.get("user"));
    assertNotNull(responseMap.get("created_at"));
    assertNotNull(responseMap.get("updated_at"));
    assertEquals(dbMusicContentMap.get("created_at"), responseMap.get("created_at"));
    assertEquals(dbMusicContentMap.get("updated_at"), responseMap.get("updated_at"));
    assertEquals(responseMap.get("created_at"), responseMap.get("updated_at"));
    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
  }

  @Test
  void postMinimalAttributesMusic() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    Long musicId = Long.valueOf(responseMap.get("id").toString());
    Music dbMusic = musicRepository.findByIdAndUser(musicId, user1).get();
    String dbMusicContent = objectMapper.writeValueAsString(dbMusic);
    Map<String, Object> dbMusicContentMap = convertStringToMap(dbMusicContent);

    boolean validTitle = allEquals(minimalAttributesMusic.get("title"),
        dbMusicContentMap.get("title"), responseMap.get("title"));

    boolean validArtist = allEquals(minimalAttributesMusic.get("artist"),
        dbMusicContentMap.get("artist"), responseMap.get("artist"));

    boolean validReleaseDate = allEquals(minimalAttributesMusic.get("release_date"),
        dbMusicContentMap.get("release_date"), responseMap.get("release_date"));

    boolean validDuration = allEquals(minimalAttributesMusic.get("duration"),
        dbMusicContentMap.get("duration"), responseMap.get("duration"));

    boolean validNumberViews =
        allEquals(0, dbMusicContentMap.get("number_views"), responseMap.get("number_views"));

    boolean validFeat = allEquals(false, dbMusicContentMap.get("feat"), responseMap.get("feat"));

    assertTrue(validTitle);
    assertTrue(validArtist);
    assertTrue(validReleaseDate);
    assertTrue(validDuration);
    assertTrue(validNumberViews);
    assertTrue(validFeat);
    assertNull(responseMap.get("deleted"));
    assertNull(responseMap.get("user"));
    assertNotNull(responseMap.get("created_at"));
    assertNotNull(responseMap.get("updated_at"));
    assertEquals(dbMusicContentMap.get("created_at"), responseMap.get("created_at"));
    assertEquals(dbMusicContentMap.get("updated_at"), responseMap.get("updated_at"));
    assertEquals(responseMap.get("created_at"), responseMap.get("updated_at"));
    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
  }

  @ParameterizedTest
  @CsvSource({TITLE_IS_REQUIRED_CSV_SOURCE, ARTIST_IS_REQUIRED_CSV_SOURCE,
      RELEASE_DATE_IS_REQUIRED_CSV_SOURCE, DURATION_IS_REQUIRED_CSV_SOURCE,})
  void postMusicWithoutRequiredFields(String field, String expectedMessage) throws Exception {

    minimalAttributesMusic.put(field, "");
    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  void postMusicWithReleaseDateFuture() throws Exception {

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, 1);
    minimalAttributesMusic.put("release_date",
        new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.RELEASE_DATE_CANNOT_BE_FUTURE, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  void postMusicWrongReleaseDateFormat() throws Exception {

    minimalAttributesMusic.put("release_date",
        minimalAttributesMusic.get("release_date").toString().replace("-", "/"));
    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.WRONG_RELEASE_DATE_FORMAT, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  void postMusicWrongDurationFormat() throws Exception {

    minimalAttributesMusic.put("duration",
        minimalAttributesMusic.get("duration").toString().replace(":", "/"));
    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.WRONG_DURATION_FORMAT, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @ParameterizedTest
  @CsvSource({INVALID_TOKEN_CSV_SOURCE, EMPTY_AUTHORIZATION_HEADER_CSV_SOURCE,
      NO_TOKEN_PROVIDED_CSV_SOURCE,})
  void postMusicWithInappropriateTokens(String token, String expectedMessage) throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void postMusicWithoutAuthorizationHeader() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.HEADER_AUTHORIZATION_NOT_PRESENT, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void postMusicWithoutBearerAuthenticationScheme() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics").header("Authorization", tokenUser1.replace("Bearer", "Token"))
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.NO_BEARER_AUTHORIZATION_SCHEME, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }
}
