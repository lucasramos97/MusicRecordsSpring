package br.com.musicrecordsspring.musics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import java.util.List;
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

class EmptyListTest extends BaseTdd {

  @BeforeAll
  public void commit() throws Exception {

    user1 = userFactory.create("1");
    tokenUser1 = generateToken(user1);
    expiredToken = generateExpiredToken(user1);

    user2 = userFactory.create("2");

    musicFactory.createBatch(10, true, user1);
    musicFactory.createBatch(10, true, user2);
    musicFactory.create(false, user1);
  }

  @AfterAll
  public void rollback() {
    userRepository.deleteAll();
  }

  @Test
  void emptyList() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(delete("/musics/empty-list").header("Authorization", tokenUser1))
            .andReturn().getResponse();

    List<Music> musicsUser1 = musicRepository.findAllByUser(user1);
    Music musicUser1 = musicsUser1.get(0);

    Long countMusicsUser2 = musicRepository.countByUser(user2);

    assertEquals("10", response.getContentAsString());
    assertEquals(1, musicsUser1.size());
    assertFalse(musicUser1.isDeleted());
    assertEquals(10L, countMusicsUser2);
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @ParameterizedTest
  @CsvSource({INVALID_TOKEN_CSV_SOURCE, EMPTY_AUTHORIZATION_HEADER_CSV_SOURCE,
      NO_TOKEN_PROVIDED_CSV_SOURCE,})
  void emptyListWithInappropriateTokens(String token, String expectedMessage) throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(delete("/musics/empty-list").header("Authorization", token)).andReturn()
            .getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void emptyListWithoutAuthorizationHeader() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(delete("/musics/empty-list")).andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.HEADER_AUTHORIZATION_NOT_PRESENT, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void emptyListWithoutBearerAuthenticationScheme() throws Exception {

    MockHttpServletResponse response = mockMvc.perform(
        delete("/musics/empty-list").header("Authorization", tokenUser1.replace("Bearer", "Token")))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.NO_BEARER_AUTHORIZATION_SCHEME, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void emptyListWithExpiredToken() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(delete("/musics/empty-list").header("Authorization", expiredToken))
            .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.TOKEN_EXPIRED, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }
}
