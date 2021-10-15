package br.com.musicrecordsspring.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.Calendar;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import br.com.musicrecordsspring.BaseTdd;
import br.com.musicrecordsspring.utils.Messages;
import io.jsonwebtoken.Claims;

class LoginTest extends BaseTdd {

  private Map<String, String> allAttributesLogin;

  @BeforeEach
  public void commit() {

    user1 = userFactory.create("1");

    allAttributesLogin = new HashedMap<>();
    allAttributesLogin.put("email", user1.getEmail());
    allAttributesLogin.put("password", "123");
  }

  @AfterEach
  public void rollback() {
    userRepository.deleteAll();
  }

  @Test
  void login() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(allAttributesLogin);

    MockHttpServletResponse response =
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertTrue(StringUtils.isNotBlank(responseMap.get("token").toString()));
    assertEquals(user1.getUsername(), responseMap.get("username"));
    assertEquals(allAttributesLogin.get("email"), responseMap.get("email"));
    assertNull(responseMap.get("password"));
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  void tokenLasts24Hours() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(allAttributesLogin);

    MockHttpServletResponse response =
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    String token = responseMap.get("token").toString();
    Claims claims = jwtService.decode(token);

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DAY_OF_MONTH, 1);

    String expected = String.valueOf(calendar.getTime().getTime()).substring(0, 10);
    String actual = String.valueOf(claims.getExpiration().getTime()).substring(0, 10);

    assertEquals(expected, actual);
  }

  @ParameterizedTest
  @CsvSource({EMAIL_IS_REQUIRED_CSV_SOURCE, PASSWORD_IS_REQUIRED_CSV_SOURCE,})
  void loginWithoutRequiredFields(String field, String expectedMessage) throws Exception {

    allAttributesLogin.put(field, "");
    String jsonRequest = objectMapper.writeValueAsString(allAttributesLogin);

    MockHttpServletResponse response =
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  void loginWithInvalidEmail() throws Exception {

    allAttributesLogin.put("email", "test");
    String jsonRequest = objectMapper.writeValueAsString(allAttributesLogin);

    MockHttpServletResponse response =
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    assertEquals(Messages.EMAIL_INVALID, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  void loginWithNonexistentEmail() throws Exception {

    allAttributesLogin.put("email", "user2@email.com");
    String jsonRequest = objectMapper.writeValueAsString(allAttributesLogin);

    MockHttpServletResponse response =
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    String expectedMessage = Messages.getUserNotFoundByEmail(allAttributesLogin.get("email"));

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void loginWithNonMatchingPassword() throws Exception {

    allAttributesLogin.put("password", "321");
    String jsonRequest = objectMapper.writeValueAsString(allAttributesLogin);

    MockHttpServletResponse response =
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    String expectedMessage =
        Messages.getPasswordDoesNotMatchWithEmail(allAttributesLogin.get("email"));

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }
}
