package br.com.musicrecordsspring.musics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import br.com.musicrecordsspring.BaseTdd;
import br.com.musicrecordsspring.models.Music;
import br.com.musicrecordsspring.utils.Messages;

class GetMusicByIdTest extends BaseTdd {

  @BeforeAll
  public void commit() throws Exception {

    user1 = userFactory.create("1");
    tokenUser1 = generateToken(user1);
    tokenUser2 = generateToken(userFactory.create("2"));
    expiredToken = generateExpiredToken(user1);

    music = musicFactory.create(false, user1);
    deletedMusic = musicFactory.create(true, user1);
  }

  @AfterAll
  public void rollback() {
    userRepository.deleteAll();
  }

  @Test
  void getMusicById() throws Exception {

    MockHttpServletResponse response = mockMvc
        .perform(
            get(String.format("/musics/%s", music.getId())).header("Authorization", tokenUser1))
        .andReturn().getResponse();

    Music dbMusic = musicRepository.findByIdAndUser(music.getId(), user1).get();
    String dbContent = objectMapper.writeValueAsString(dbMusic);

    assertEquals(dbContent, response.getContentAsString());
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  void getNonexistentMusicById() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(get(String.format("/musics/%s", 100)).header("Authorization", tokenUser1))
            .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.MUSIC_NOT_FOUND, responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  void getDeletedMusicById() throws Exception {

    MockHttpServletResponse response = mockMvc.perform(
        get(String.format("/musics/%s", deletedMusic.getId())).header("Authorization", tokenUser1))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.MUSIC_NOT_FOUND, responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  void getNonexistentMusicByUser() throws Exception {

    MockHttpServletResponse response = mockMvc
        .perform(
            get(String.format("/musics/%s", music.getId())).header("Authorization", tokenUser2))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.MUSIC_NOT_FOUND, responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @ParameterizedTest
  @CsvSource({INVALID_TOKEN_CSV_SOURCE, EMPTY_AUTHORIZATION_HEADER_CSV_SOURCE,
      NO_TOKEN_PROVIDED_CSV_SOURCE,})
  void getMusicByIdWithInappropriateTokens(String token, String expectedMessage) throws Exception {

    MockHttpServletResponse response = mockMvc
        .perform(get(String.format("/musics/%s", music.getId())).header("Authorization", token))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void getMusicByIdWithoutAuthorizationHeader() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(get(String.format("/musics/%s", music.getId()))).andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.HEADER_AUTHORIZATION_NOT_PRESENT, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void getMusicByIdWithoutBearerAuthenticationScheme() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(get(String.format("/musics/%s", music.getId())).header("Authorization",
            tokenUser1.replace("Bearer", "Token"))).andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.NO_BEARER_AUTHORIZATION_SCHEME, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void getMusicByIdWithExpiredToken() throws Exception {

    MockHttpServletResponse response = mockMvc
        .perform(
            get(String.format("/musics/%s", music.getId())).header("Authorization", expiredToken))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.TOKEN_EXPIRED, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }
}
