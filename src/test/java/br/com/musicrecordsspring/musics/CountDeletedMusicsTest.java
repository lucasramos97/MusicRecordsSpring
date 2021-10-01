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
import br.com.musicrecordsspring.utils.Messages;

class CountDeletedMusicsTest extends BaseTdd {

  @BeforeAll
  public void commit() throws Exception {

    user1 = userFactory.create("1");
    tokenUser1 = generateToken(user1);

    musicFactory.createBatch(10, true, user1);
    musicFactory.create(false, user1);
    musicFactory.create(true, userFactory.create("2"));
  }

  @AfterAll
  public void rollback() {
    userRepository.deleteAll();
  }

  @Test
  void countDeletedMusics() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(get("/musics/deleted/count").header("Authorization", tokenUser1))
            .andReturn().getResponse();

    assertEquals("10", response.getContentAsString());
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @ParameterizedTest
  @CsvSource({INVALID_TOKEN_CSV_SOURCE, EMPTY_AUTHORIZATION_HEADER_CSV_SOURCE,
      NO_TOKEN_PROVIDED_CSV_SOURCE,})
  void countDeletedMusicsWithInappropriateTokens(String token, String expectedMessage)
      throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(get("/musics/deleted/count").header("Authorization", token)).andReturn()
            .getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void countDeletedMusicsWithoutAuthorizationHeader() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(get("/musics/deleted/count")).andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.HEADER_AUTHORIZATION_NOT_PRESENT, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void countDeletedMusicsWithoutBearerAuthenticationScheme() throws Exception {

    MockHttpServletResponse response = mockMvc.perform(
        get("/musics/deleted/count").header("Authorization", tokenUser1.replace("Bearer", "Token")))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.NO_BEARER_AUTHORIZATION_SCHEME, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }
}
