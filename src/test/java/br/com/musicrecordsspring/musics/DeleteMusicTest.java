package br.com.musicrecordsspring.musics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import br.com.musicrecordsspring.BaseTdd;
import br.com.musicrecordsspring.models.Music;
import br.com.musicrecordsspring.utils.Messages;

class DeleteMusicTest extends BaseTdd {

  @BeforeAll
  public void commitPerClass() throws Exception {

    user1 = userFactory.create("1");
    tokenUser1 = generateToken(user1);
    tokenUser2 = generateToken(userFactory.create("2"));
  }

  @BeforeEach
  public void commitPerMethod() throws Exception {

    music = musicFactory.create(false, user1);
    deletedMusic = musicFactory.create(true, user1);
  }

  @AfterEach
  public void rollbackPerMethod() {
    musicRepository.deleteAll();
  }

  @AfterAll
  public void rollbackPerClass() {
    userRepository.deleteAll();
  }

  @Test
  void deleteMusic() throws Exception {

    MockHttpServletResponse response = mockMvc
        .perform(
            delete(String.format("/musics/%s", music.getId())).header("Authorization", tokenUser1))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    String musicContent = objectMapper.writeValueAsString(music);
    Map<String, Object> musicContentMap = convertStringToMap(musicContent);

    Music dbMusic = musicRepository.findByIdAndUser(music.getId(), user1).get();
    String dbMusicContent = objectMapper.writeValueAsString(dbMusic);
    Map<String, Object> dbMusicContentMap = convertStringToMap(dbMusicContent);

    boolean validCreateAt = allEquals(musicContentMap.get("created_at"),
        dbMusicContentMap.get("created_at"), responseMap.get("created_at"));

    assertEquals(dbMusicContent, response.getContentAsString());
    assertNull(responseMap.get("deleted"));
    assertNull(responseMap.get("user"));
    assertTrue(dbMusic.isDeleted());
    assertNotNull(responseMap.get("created_at"));
    assertNotNull(responseMap.get("updated_at"));
    assertTrue(validCreateAt);
    assertEquals(dbMusicContentMap.get("updated_at"), responseMap.get("updated_at"));
    assertNotEquals(musicContentMap.get("updated_at"), responseMap.get("updated_at"));
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  void deleteNonexistentMusicById() throws Exception {

    MockHttpServletResponse response = mockMvc
        .perform(delete(String.format("/musics/%s", 100)).header("Authorization", tokenUser1))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.MUSIC_NOT_FOUND, responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  void deleteDeletedMusic() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(delete(String.format("/musics/%s", deletedMusic.getId()))
            .header("Authorization", tokenUser1)).andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.MUSIC_NOT_FOUND, responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  void deleteNonexistentMusicByUser() throws Exception {

    MockHttpServletResponse response = mockMvc
        .perform(
            delete(String.format("/musics/%s", music.getId())).header("Authorization", tokenUser2))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.MUSIC_NOT_FOUND, responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @ParameterizedTest
  @CsvSource({INVALID_TOKEN_CSV_SOURCE, EMPTY_AUTHORIZATION_HEADER_CSV_SOURCE,
      NO_TOKEN_PROVIDED_CSV_SOURCE,})
  void deleteMusicWithInappropriateTokens(String token, String expectedMessage) throws Exception {

    MockHttpServletResponse response = mockMvc
        .perform(delete(String.format("/musics/%s", music.getId())).header("Authorization", token))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void deleteMusicWithoutAuthorizationHeader() throws Exception {

    MockHttpServletResponse response = mockMvc
        .perform(delete(String.format("/musics/%s", music.getId()))).andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.HEADER_AUTHORIZATION_NOT_PRESENT, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void deleteMusicWithoutBearerAuthenticationScheme() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(delete(String.format("/musics/%s", music.getId())).header("Authorization",
            tokenUser1.replace("Bearer", "Token"))).andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.NO_BEARER_AUTHORIZATION_SCHEME, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }
}
