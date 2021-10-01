package br.com.musicrecordsspring.musics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import br.com.musicrecordsspring.BaseTdd;
import br.com.musicrecordsspring.models.Music;
import br.com.musicrecordsspring.models.PagedMusic;
import br.com.musicrecordsspring.utils.Messages;

class GetDeletedMusicsTest extends BaseTdd {

  @BeforeAll
  public void commit() throws Exception {

    user1 = userFactory.create("1");
    tokenUser1 = generateToken(user1);

    musicFactory.createBatch(10, true, user1);
    musicFactory.createBatch(10, true, userFactory.create("2"));
    musicFactory.create(false, user1);
  }

  @AfterAll
  public void rollback() {
    userRepository.deleteAll();
  }

  @Test
  void getDeletedMusicsWithDefaultQueryParams() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(get("/musics/deleted").header("Authorization", tokenUser1)).andReturn()
            .getResponse();

    PagedMusic responseMusics =
        objectMapper.readValue(response.getContentAsString(), PagedMusic.class);

    String responseContent = objectMapper.writeValueAsString(responseMusics.getContent());

    List<Music> dbMusics =
        musicRepository.findAllByUserAndDeleted(user1, true, Sort.by("artist", "title"));

    String dbContent =
        objectMapper.writeValueAsString(Arrays.copyOfRange(dbMusics.toArray(), 0, 5));

    assertEquals(dbContent, responseContent);
    assertEquals(5, responseMusics.getContent().size());
    assertEquals(dbMusics.size(), responseMusics.getTotal());
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  void getDeletedMusicsWithExplicitQueryParams() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(get("/musics/deleted").header("Authorization", tokenUser1)
            .param("page", "2").param("size", "4")).andReturn().getResponse();

    PagedMusic responseMusics =
        objectMapper.readValue(response.getContentAsString(), PagedMusic.class);

    String responseContent = objectMapper.writeValueAsString(responseMusics.getContent());

    List<Music> dbMusics =
        musicRepository.findAllByUserAndDeleted(user1, true, Sort.by("artist", "title"));

    String dbContent =
        objectMapper.writeValueAsString(Arrays.copyOfRange(dbMusics.toArray(), 4, 8));

    assertEquals(dbContent, responseContent);
    assertEquals(4, responseMusics.getContent().size());
    assertEquals(dbMusics.size(), responseMusics.getTotal());
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @ParameterizedTest
  @CsvSource({INVALID_TOKEN_CSV_SOURCE, EMPTY_AUTHORIZATION_HEADER_CSV_SOURCE,
      NO_TOKEN_PROVIDED_CSV_SOURCE,})
  void getDeletedMusicsWithInappropriateTokens(String token, String expectedMessage)
      throws Exception {

    MockHttpServletResponse response = mockMvc
        .perform(get("/musics/deleted").header("Authorization", token)).andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void getDeletedMusicsWithoutAuthorizationHeader() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(get("/musics/deleted")).andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.HEADER_AUTHORIZATION_NOT_PRESENT, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void getDeletedMusicsWithoutBearerAuthenticationScheme() throws Exception {

    MockHttpServletResponse response = mockMvc
        .perform(
            get("/musics/deleted").header("Authorization", tokenUser1.replace("Bearer", "Token")))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.NO_BEARER_AUTHORIZATION_SCHEME, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }
}
