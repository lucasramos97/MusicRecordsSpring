package br.com.musicrecordsspring.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.HashMap;
import java.util.Map;
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
class CreateUserTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserFactory userFactory;

  @Autowired
  private UserRepository userRepository;

  private Map<String, Object> userMap;

  @BeforeEach
  public void commit() {

    userMap = new HashMap<>();
    userMap.put("username", "test");
    userMap.put("email", "test@email.com");
    userMap.put("password", "123");
  }

  @AfterEach
  public void rollback() {
    userRepository.deleteAll();
  }

  @Test
  void createUser() throws Exception {

    String jsonRequest = objectMapper.writeValueAsString(userMap);

    MockHttpServletResponse response =
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    User dbUser = userRepository.findById(Long.valueOf(responseMap.get("id").toString())).get();

    assertEquals(userMap.get("username"), dbUser.getUsername());
    assertEquals(dbUser.getUsername(), responseMap.get("username"));
    assertEquals(userMap.get("email"), dbUser.getEmail());
    assertEquals(dbUser.getEmail(), responseMap.get("email"));
    assertNotEquals(userMap.get("password"), dbUser.getPassword());
    assertEquals(dbUser.getPassword(), responseMap.get("password"));
    assertEquals(dbUser.getCreatedAt().toString(), responseMap.get("created_at"));
    assertEquals(dbUser.getUpdatedAt().toString(), responseMap.get("updated_at"));
    assertEquals(responseMap.get("created_at"), responseMap.get("updated_at"));
    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
  }

  @ParameterizedTest
  @CsvSource({"username, Username is required!", "email, E-mail is required!",
      "password, Password is required!",})
  void createUserWithoutRequiredFields(String field, String expectedMessage) throws Exception {

    userMap.put(field, "");
    String jsonRequest = objectMapper.writeValueAsString(userMap);

    MockHttpServletResponse response =
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  void createUserWithExistentEmail() throws Exception {

    userFactory.create();

    String jsonRequest = objectMapper.writeValueAsString(userMap);

    MockHttpServletResponse response =
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    @SuppressWarnings("unchecked")
    Map<String, Object> responseMap =
        objectMapper.readValue(response.getContentAsString(), Map.class);

    String expectedMessage =
        String.format("The %s e-mail has already been registered!", userMap.get("email"));

    assertEquals(expectedMessage, responseMap.get("message"));
    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

}
