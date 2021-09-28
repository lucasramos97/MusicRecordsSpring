package br.com.musicrecordsspring.musics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import java.util.Map;
import java.util.Optional;
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

class DefinitiveDeleteMusicTest extends BaseTdd {

  @BeforeAll
  public void commitPerClass() throws Exception {

    user1 = userFactory.create("1");
    tokenUser1 = generateToken(user1);

    user2 = userFactory.create("2");
    tokenUser2 = generateToken(user2);
  }

  @BeforeEach
  public void commitPerMethod() throws Exception {

    deletedMusic = musicFactory.create(true, user1);
    music = musicFactory.create(false, user1);
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
  void definitiveDeleteMusic() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(delete(String.format("/musics/definitive/%s", deletedMusic.getId()))
            .header("Authorization", tokenUser1)).andReturn().getResponse();

    Optional<Music> musicUser1 = musicRepository.findById(deletedMusic.getId());

    assertTrue(musicUser1.isEmpty());
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  void definitiveDeleteNonexistentMusicById() throws Exception {

    MockHttpServletResponse response = mockMvc
        .perform(
            delete(String.format("/musics/definitive/%s", 100)).header("Authorization", tokenUser1))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.MUSIC_NOT_FOUND, responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  void definitiveDeleteNonDeletedMusic() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(delete(String.format("/musics/definitive/%s", music.getId()))
            .header("Authorization", tokenUser1)).andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.MUSIC_NOT_FOUND, responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @Test
  void definitiveDeleteNonexistentMusicByUser() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(delete(String.format("/musics/definitive/%s", deletedMusic.getId()))
            .header("Authorization", tokenUser2)).andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.MUSIC_NOT_FOUND, responseMap.get("message"));
    assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
  }

  @ParameterizedTest
  @CsvSource({INVALID_TOKEN_CSV_SOURCE, HEADER_AUTHORIZATION_NOT_PRESENT_CSV_SOURCE,
      NO_TOKEN_PROVIDED_CSV_SOURCE,})
  void definitiveDeleteMusicWithInappropriateTokens(String token, String expectedMessage)
      throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(delete(String.format("/musics/definitive/%s", deletedMusic.getId()))
            .header("Authorization", token)).andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void definitiveDeleteMusicWithoutAuthorizationHeader() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(delete(String.format("/musics/definitive/%s", deletedMusic.getId())))
            .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.HEADER_AUTHORIZATION_NOT_PRESENT, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void definitiveDeleteMusicWithoutBearerAuthenticationScheme() throws Exception {

    MockHttpServletResponse response = mockMvc
        .perform(delete(String.format("/musics/definitive/%s", deletedMusic.getId()))
            .header("Authorization", tokenUser1.replace("Bearer", "Token")))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.NO_BEARER_AUTHORIZATION_SCHEME, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }
}
