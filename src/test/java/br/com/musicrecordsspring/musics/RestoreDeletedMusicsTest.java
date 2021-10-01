package br.com.musicrecordsspring.musics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
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

class RestoreDeletedMusicsTest extends BaseTdd {

  @BeforeAll
  public void commitPerClass() throws Exception {

    user1 = userFactory.create("1");
    tokenUser1 = generateToken(user1);

    user2 = userFactory.create("2");
    tokenUser2 = generateToken(user2);
  }

  @BeforeEach
  public void commitPerMethod() throws Exception {

    deletedMusics = musicFactory.createBatch(10, true, user1);
    musics = musicFactory.createBatch(1, false, user1);
    musicFactory.createBatch(10, true, user2);
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
  void restoreDeletedMusics() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(deletedMusics);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics/deleted/restore").header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Long countDeletedMusicsUser1 = musicRepository.countByUserAndDeleted(user1, false);
    Long countDeletedMusicsUser2 = musicRepository.countByUserAndDeleted(user2, true);

    assertEquals("10", response.getContentAsString());
    assertEquals(11L, countDeletedMusicsUser1);
    assertEquals(10L, countDeletedMusicsUser2);
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  void restoreDeletedNonexistentMusicsById() throws Exception {

    Music changedMusic = deletedMusics.remove(0);
    changedMusic.setId(1000L);
    deletedMusics.add(changedMusic);

    String jsonRequest = objectMapper.writeValueAsString(deletedMusics);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics/deleted/restore").header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Long countDeletedMusicsUser1 = musicRepository.countByUserAndDeleted(user1, false);
    Long countDeletedMusicsUser2 = musicRepository.countByUserAndDeleted(user2, true);

    assertEquals("9", response.getContentAsString());
    assertEquals(10L, countDeletedMusicsUser1);
    assertEquals(10L, countDeletedMusicsUser2);
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  void restoreDeletedNonDeletedMusics() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(musics);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics/deleted/restore").header("Authorization", tokenUser1)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    List<Music> dbMusicsUser1 = musicRepository.findAllByUser(user1);
    Long countDeletedMusicsUser2 = musicRepository.countByUserAndDeleted(user2, true);

    assertEquals("0", response.getContentAsString());
    assertEquals(11, dbMusicsUser1.size());
    assertTrue(dbMusicsUser1.stream().anyMatch(m -> !m.isDeleted()));
    assertEquals(10L, countDeletedMusicsUser2);
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  void restoreDeletedNonexistentMusicsByUser() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(deletedMusics);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics/deleted/restore").header("Authorization", tokenUser2)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Long countDeletedMusicsUser1 = musicRepository.countByUserAndDeleted(user1, false);
    Long countDeletedMusicsUser2 = musicRepository.countByUserAndDeleted(user2, true);

    assertEquals("0", response.getContentAsString());
    assertEquals(1L, countDeletedMusicsUser1);
    assertEquals(10L, countDeletedMusicsUser2);
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  void restoreDeletedMusicsWithoutIdField() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(deletedMusics);

    MockHttpServletResponse response = mockMvc.perform(post("/musics/deleted/restore")
        .header("Authorization", tokenUser1).contentType(MediaType.APPLICATION_JSON)
        .content(jsonRequest.replaceFirst("id", "none"))).andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.ID_IS_REQUIRED, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @ParameterizedTest
  @CsvSource({INVALID_TOKEN_CSV_SOURCE, EMPTY_AUTHORIZATION_HEADER_CSV_SOURCE,
      NO_TOKEN_PROVIDED_CSV_SOURCE,})
  void restoreDeletedMusicsWithInappropriateTokens(String token, String expectedMessage)
      throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(deletedMusics);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics/deleted/restore").header("Authorization", token)
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void restoreDeletedMusicsWithoutAuthorizationHeader() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(deletedMusics);

    MockHttpServletResponse response = mockMvc.perform(post("/musics/deleted/restore")
        .contentType(MediaType.APPLICATION_JSON).content(jsonRequest)).andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.HEADER_AUTHORIZATION_NOT_PRESENT, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void restoreDeletedMusicsWithoutBearerAuthenticationScheme() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(deletedMusics);

    MockHttpServletResponse response = mockMvc
        .perform(post("/musics/deleted/restore")
            .header("Authorization", tokenUser1.replace("Bearer", "Token"))
            .contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
        .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.NO_BEARER_AUTHORIZATION_SCHEME, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }
}
