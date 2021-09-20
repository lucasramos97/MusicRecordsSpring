package br.com.musicrecordsspring.musics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import br.com.musicrecordsspring.factories.MusicFactory;
import br.com.musicrecordsspring.factories.UserFactory;
import br.com.musicrecordsspring.models.User;
import br.com.musicrecordsspring.repositories.MusicRepository;
import br.com.musicrecordsspring.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class CountDeletedMusicsTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MusicFactory musicFactory;

  @Autowired
  private MusicRepository musicRepository;

  @Autowired
  private UserFactory userFactory;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  public void commit() {

    User user = userFactory.create();
    musicFactory.createBatch(10, true, user);
    musicFactory.create(false, user);
  }

  @AfterEach
  public void rollback() {
    userRepository.deleteAll();
  }

  @Test
  public void countDeletedMusics() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(get("/musics/deleted/count")).andReturn().getResponse();

    Long dbCountDeletedMusics = musicRepository.countByDeletedIsTrue();

    assertEquals(dbCountDeletedMusics.toString(), response.getContentAsString());
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

}
