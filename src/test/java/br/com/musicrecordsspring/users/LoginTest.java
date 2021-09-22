package br.com.musicrecordsspring.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.musicrecordsspring.factories.UserFactory;
import br.com.musicrecordsspring.models.User;
import br.com.musicrecordsspring.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
class LoginTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserFactory userFactory;

  @Autowired
  private UserRepository userRepository;

  private User user;
  private Map<String, String> userMap;

  @BeforeEach
  public void commit() {

    user = userFactory.create();

    userMap = new HashedMap<>();
    userMap.put("email", user.getEmail());
    userMap.put("password", "123");
  }

  @AfterEach
  public void rollback() {
    userRepository.deleteAll();
  }

  @Test
  void login() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(userMap);

    MockHttpServletResponse response =
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertNotNull(responseMap.get("token"));
    assertNotEquals("", responseMap.get("token"));
    assertEquals(user.getUsername(), responseMap.get("username"));
    assertEquals(userMap.get("email"), responseMap.get("email"));
    assertNull(responseMap.get("password"));
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @ParameterizedTest
  @CsvSource({"email, E-mail is required!", "password, Password is required!",})
  void loginWithoutRequiredFields(String field, String expectedMessage) throws Exception {

    userMap.put(field, "");
    String jsonRequest = objectMapper.writeValueAsString(userMap);

    MockHttpServletResponse response =
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  void loginWithInvalidEmail() throws Exception {

    userMap.put("email", "test");
    String jsonRequest = objectMapper.writeValueAsString(userMap);

    MockHttpServletResponse response =
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals("E-mail invalid!", responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  void loginWithNonexistentEmail() throws Exception {

    userMap.put("email", "test2@email.com");
    String jsonRequest = objectMapper.writeValueAsString(userMap);

    MockHttpServletResponse response =
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    String expectedMessage = String.format("User not found by e-mail: %s!", userMap.get("email"));

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

  @Test
  void loginWithNonMatchingPassword() throws Exception {

    userMap.put("password", "321");
    String jsonRequest = objectMapper.writeValueAsString(userMap);

    MockHttpServletResponse response =
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    String expectedMessage =
        String.format("Password does not match with email: %s!", userMap.get("email"));

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
  }

}
