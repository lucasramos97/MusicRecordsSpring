package br.com.musicrecordsspring.musics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

class PutMusicTest extends BaseTdd {

  @BeforeAll
  public void commitPerClass() throws Exception {

    user1 = userFactory.create("1");
    tokenUser1 = generateToken(user1);
    tokenUser2 = generateToken(userFactory.create("2"));
    expiredToken = generateExpiredToken(user1);

    music = musicFactory.create(false, user1);
    deletedMusic = musicFactory.create(true, user1);
  }

  @BeforeEach
  public void commitPerMethod() {

    allAttributesMusic = new HashMap<>();
    allAttributesMusic.put("title", String.format("%s Test", music.getTitle()));
    allAttributesMusic.put("artist", String.format("%s Test", music.getArtist()));
    allAttributesMusic.put("release_date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    allAttributesMusic.put("duration", new SimpleDateFormat("HH:mm:ss").format(new Date()));
    allAttributesMusic.put("number_views", music.getNumberViews() + 1);
    allAttributesMusic.put("feat", !music.getFeat());

    minimalAttributesMusic = new HashMap<>();
    minimalAttributesMusic.put("title", String.format("%s Test", music.getTitle()));
    minimalAttributesMusic.put("artist", String.format("%s Test", music.getArtist()));
    minimalAttributesMusic.put("release_date",
        new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
    minimalAttributesMusic.put("duration", new SimpleDateFormat("HH:mm:ss").format(new Date()));
  }

  @AfterAll
  public void rollback() {
    userRepository.deleteAll();
  }

  @Test
  void putAllAttributesMusic() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(allAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId())).header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    String musicContent = objectMapper.writeValueAsString(music);
    Map<String, Object> musicContentMap = convertStringToMap(musicContent);

    Music dbMusic = musicRepository.findByIdAndUser(music.getId(), user1).get();
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

    boolean validCreatedAt = allEquals(musicContentMap.get("created_at"),
        dbMusicContentMap.get("created_at"), responseMap.get("created_at"));

    assertEquals(musicContentMap.get("id"), responseMap.get("id"));
    assertTrue(validTitle);
    assertNotEquals(musicContentMap.get("title"), responseMap.get("title"));
    assertTrue(validArtist);
    assertNotEquals(musicContentMap.get("artist"), responseMap.get("artist"));
    assertTrue(matchDate(responseMap.get("release_date").toString()));
    assertTrue(validReleaseDate);
    assertNotEquals(musicContentMap.get("release_date"), responseMap.get("release_date"));
    assertTrue(matchTime(responseMap.get("duration").toString()));
    assertTrue(validDuration);
    assertNotEquals(musicContentMap.get("duration"), responseMap.get("duration"));
    assertTrue(validNumberViews);
    assertNotEquals(musicContentMap.get("number_views"), responseMap.get("number_views"));
    assertTrue(validFeat);
    assertNotEquals(musicContentMap.get("feat"), responseMap.get("feat"));
    assertFalse(dbMusic.isDeleted());
    assertNull(responseMap.get("deleted"));
    assertNull(responseMap.get("user"));
    assertTrue(matchDateTime(responseMap.get("created_at").toString()));
    assertTrue(matchDateTime(responseMap.get("updated_at").toString()));
    assertTrue(validCreatedAt);
    assertEquals(dbMusicContentMap.get("updated_at"), responseMap.get("updated_at"));
    assertNotEquals(musicContentMap.get("updated_at"), responseMap.get("updated_at"));
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  void putMinimalAttributesMusic() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId())).header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    String musicContent = objectMapper.writeValueAsString(music);
    Map<String, Object> musicContentMap = convertStringToMap(musicContent);

    Music dbMusic = musicRepository.findByIdAndUser(music.getId(), user1).get();
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

    boolean validNumberViews = allEquals(musicContentMap.get("number_views"),
        dbMusicContentMap.get("number_views"), responseMap.get("number_views"));

    boolean validFeat = allEquals(musicContentMap.get("feat"), dbMusicContentMap.get("feat"),
        responseMap.get("feat"));

    boolean validCreatedAt = allEquals(musicContentMap.get("created_at"),
        dbMusicContentMap.get("created_at"), responseMap.get("created_at"));

    assertEquals(musicContentMap.get("id"), responseMap.get("id"));
    assertTrue(validTitle);
    assertNotEquals(musicContentMap.get("title"), responseMap.get("title"));
    assertTrue(validArtist);
    assertNotEquals(musicContentMap.get("artist"), responseMap.get("artist"));
    assertTrue(matchDate(responseMap.get("release_date").toString()));
    assertTrue(validReleaseDate);
    assertNotEquals(musicContentMap.get("release_date"), responseMap.get("release_date"));
    assertTrue(matchTime(responseMap.get("duration").toString()));
    assertTrue(validDuration);
    assertNotEquals(musicContentMap.get("duration"), responseMap.get("duration"));
    assertTrue(validNumberViews);
    assertTrue(validFeat);
    assertFalse(dbMusic.isDeleted());
    assertNull(responseMap.get("deleted"));
    assertNull(responseMap.get("user"));
    assertTrue(matchDateTime(responseMap.get("created_at").toString()));
    assertTrue(matchDateTime(responseMap.get("updated_at").toString()));
    assertTrue(validCreatedAt);
    assertEquals(dbMusicContentMap.get("updated_at"), responseMap.get("updated_at"));
    assertNotEquals(musicContentMap.get("updated_at"), responseMap.get("updated_at"));
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  void putNonexistentMusicById() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", 100)).header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.MUSIC_NOT_FOUND, responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  void putDeletedMusicById() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc.perform(
        put(String.format("/musics/%s", deletedMusic.getId())).header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.MUSIC_NOT_FOUND, responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  void putNonexistentMusicByUser() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId())).header("Authorization", tokenUser2)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.MUSIC_NOT_FOUND, responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @ParameterizedTest
  @CsvSource({TITLE_IS_REQUIRED_CSV_SOURCE, ARTIST_IS_REQUIRED_CSV_SOURCE,
      RELEASE_DATE_IS_REQUIRED_CSV_SOURCE, DURATION_IS_REQUIRED_CSV_SOURCE,})
  void putMusicWithoutRequiredFields(String field, String expectedMessage) throws Exception {

    minimalAttributesMusic.put(field, "");
    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId())).header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  void putMusicWithReleaseDateFuture() throws Exception {

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_YEAR, 1);
    minimalAttributesMusic.put("release_date",
        new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId())).header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.RELEASE_DATE_CANNOT_BE_FUTURE, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  void putMusicWrongReleaseDateFormat() throws Exception {

    minimalAttributesMusic.put("release_date",
        minimalAttributesMusic.get("release_date").toString().replace("-", "/"));
    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId())).header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.WRONG_RELEASE_DATE_FORMAT, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  void putMusicWrongDurationFormat() throws Exception {

    minimalAttributesMusic.put("duration",
        minimalAttributesMusic.get("duration").toString().replace(":", "/"));
    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId())).header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.WRONG_DURATION_FORMAT, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  void putMusicWithInvalidReleaseDate() throws Exception {

    minimalAttributesMusic.put("release_date", "2021-01-32");
    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId())).header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    String expectedMessage =
        Messages.getInvalidDate(minimalAttributesMusic.get("release_date").toString());

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  void putMusicWithInvalidDuration() throws Exception {

    minimalAttributesMusic.put("duration", "23:60:59");
    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId())).header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    String expectedMessage =
        Messages.getInvalidTime(minimalAttributesMusic.get("duration").toString());

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @ParameterizedTest
  @CsvSource({INVALID_TOKEN_CSV_SOURCE, EMPTY_AUTHORIZATION_HEADER_CSV_SOURCE,
      NO_TOKEN_PROVIDED_CSV_SOURCE,})
  void putMusicWithInappropriateTokens(String token, String expectedMessage) throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId())).header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void putMusicWithoutAuthorizationHeader() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId()))
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.HEADER_AUTHORIZATION_NOT_PRESENT, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void putMusicWithoutBearerAuthenticationScheme() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(put(String.format("/musics/%s", music.getId()))
            .header("Authorization", tokenUser1.replace("Bearer", "Token"))
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.NO_BEARER_AUTHORIZATION_SCHEME, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void putMusicWithExpiredToken() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(minimalAttributesMusic);

    MockHttpServletResponse response = mockMvc
        .perform(
            put(String.format("/musics/%s", music.getId())).header("Authorization", expiredToken)
                .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.TOKEN_EXPIRED, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }
}
