package br.com.musicrecordsspring;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.musicrecordsspring.factories.MusicFactory;
import br.com.musicrecordsspring.factories.UserFactory;
import br.com.musicrecordsspring.models.Music;
import br.com.musicrecordsspring.models.User;
import br.com.musicrecordsspring.repositories.MusicRepository;
import br.com.musicrecordsspring.repositories.UserRepository;
import br.com.musicrecordsspring.services.JwtService;
import br.com.musicrecordsspring.utils.Messages;

@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
public abstract class BaseTdd {

  // User Messages
  protected static final String USERNAME_IS_REQUIRED_CSV_SOURCE =
      "username, " + Messages.USERNAME_IS_REQUIRED;
  protected static final String EMAIL_IS_REQUIRED_CSV_SOURCE =
      "email, " + Messages.EMAIL_IS_REQUIRED;
  protected static final String PASSWORD_IS_REQUIRED_CSV_SOURCE =
      "password, " + Messages.PASSWORD_IS_REQUIRED;

  // Music Messages
  protected static final String ID_IS_REQUIRED_CSV_SOURCE = "id, " + Messages.ID_IS_REQUIRED;
  protected static final String TITLE_IS_REQUIRED_CSV_SOURCE =
      "title, " + Messages.TITLE_IS_REQUIRED;
  protected static final String ARTIST_IS_REQUIRED_CSV_SOURCE =
      "artist, " + Messages.ARTIST_IS_REQUIRED;
  protected static final String RELEASE_DATE_IS_REQUIRED_CSV_SOURCE =
      "release_date, " + Messages.RELEASE_DATE_IS_REQUIRED;
  protected static final String DURATION_IS_REQUIRED_CSV_SOURCE =
      "duration, " + Messages.DURATION_IS_REQUIRED;

  // Authorization Messages
  protected static final String INVALID_TOKEN_CSV_SOURCE = "Bearer 123, " + Messages.INVALID_TOKEN;
  protected static final String EMPTY_AUTHORIZATION_HEADER_CSV_SOURCE =
      "'', " + Messages.HEADER_AUTHORIZATION_NOT_PRESENT;
  protected static final String NO_TOKEN_PROVIDED_CSV_SOURCE =
      "Bearer , " + Messages.NO_TOKEN_PROVIDED;

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected MusicFactory musicFactory;

  @Autowired
  protected MusicRepository musicRepository;

  @Autowired
  protected UserFactory userFactory;

  @Autowired
  protected UserRepository userRepository;

  @Autowired
  protected JwtService jwtService;

  @PostConstruct
  public void init() {
    objectMapper.setTimeZone(TimeZone.getDefault());
  }

  protected String expiredToken;

  protected User user1;
  protected String tokenUser1;

  protected User user2;
  protected String tokenUser2;

  protected Music music;
  protected Music deletedMusic;

  protected Map<String, Object> allAttributesMusic;
  protected Map<String, Object> minimalAttributesMusic;

  protected List<Music> musics;
  protected List<Music> deletedMusics;

  protected String generateToken(User user) throws Exception {

    Map<String, String> userMap = new HashedMap<>();
    userMap.put("email", user.getEmail());
    userMap.put("password", "123");

    String jsonRequest = objectMapper.writeValueAsString(userMap);

    MockHttpServletResponse response =
        mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(jsonRequest))
            .andReturn().getResponse();

    Map<String, Object> responseMap = convertStringToMap(response.getContentAsString());

    return String.format("Bearer %s", responseMap.get("token"));
  }

  protected String generateExpiredToken(User user) throws Exception {

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.SECOND, -10);

    String token = jwtService.encode(user1.getId().toString(), calendar.getTime());

    return String.format("Bearer %s", token);
  }

  @SuppressWarnings("unchecked")
  protected Map<String, Object> convertStringToMap(String content)
      throws JsonMappingException, JsonProcessingException {
    return objectMapper.readValue(content, Map.class);
  }

  protected boolean allEquals(Object object, Object... searchObjectss) {

    if (object == null) {
      return false;
    }

    String stringObject = object.toString();

    for (int i = 0; i < searchObjectss.length; i++) {

      if (!StringUtils.equals(stringObject, searchObjectss[i].toString())) {
        return false;
      }
    }

    return true;
  }

  public boolean matchDate(String date) {
    return Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$", date);
  }


  public boolean matchTime(String time) {
    return Pattern.matches("^\\d{2}:\\d{2}:\\d{2}$", time);
  }


  public boolean matchDateTime(String dateTime) {
    return Pattern.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}$", dateTime);
  }
}
