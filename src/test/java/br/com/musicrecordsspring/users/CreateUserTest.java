package br.com.musicrecordsspring.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import br.com.musicrecordsspring.BaseTdd;
import br.com.musicrecordsspring.models.User;
import br.com.musicrecordsspring.utils.Messages;

class CreateUserTest extends BaseTdd {

  private Map<String, String> allAttributesUser;

  @BeforeEach
  public void commit() {

    allAttributesUser = new HashMap<>();
    allAttributesUser.put("username", "user1");
    allAttributesUser.put("email", "user1@email.com");
    allAttributesUser.put("password", "123");
  }

  @AfterEach
  public void rollback() {
    userRepository.deleteAll();
  }

  @Test
  void createUser() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(allAttributesUser);

    MockHttpServletResponse response =
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    User dbUser = userRepository.findById(Long.valueOf(responseMap.get("id").toString())).get();
    String dbUserContent = objectMapper.writeValueAsString(dbUser);
    Map<String, Object> dbUserContentMap = convertStringToMap(dbUserContent);

    boolean validUsername = allEquals(allAttributesUser.get("username"),
        dbUserContentMap.get("username"), responseMap.get("username"));

    boolean validEmail = allEquals(allAttributesUser.get("email"), dbUserContentMap.get("email"),
        responseMap.get("email"));

    assertTrue(validUsername);
    assertTrue(validEmail);
    assertNotEquals(allAttributesUser.get("password"), dbUserContentMap.get("password"));
    assertNotNull(responseMap.get("created_at"));
    assertNotNull(responseMap.get("updated_at"));
    assertEquals(dbUserContentMap.get("password"), responseMap.get("password"));
    assertEquals(dbUserContentMap.get("created_at"), responseMap.get("created_at"));
    assertEquals(dbUserContentMap.get("updated_at"), responseMap.get("updated_at"));
    assertEquals(responseMap.get("created_at"), responseMap.get("updated_at"));
    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
  }

  @ParameterizedTest
  @CsvSource({USERNAME_IS_REQUIRED_CSV_SOURCE, EMAIL_IS_REQUIRED_CSV_SOURCE,
      PASSWORD_IS_REQUIRED_CSV_SOURCE,})
  void createUserWithoutRequiredFields(String field, String expectedMessage) throws Exception {

    allAttributesUser.put(field, "");
    String jsonRequest = objectMapper.writeValueAsString(allAttributesUser);

    MockHttpServletResponse response =
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  void createUserWithExistentEmail() throws Exception {

    userFactory.create("1");

    String jsonRequest = objectMapper.writeValueAsString(allAttributesUser);

    MockHttpServletResponse response =
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    String expectedMessage = Messages.getEmailAlreadyRegistered(allAttributesUser.get("email"));

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }
}
