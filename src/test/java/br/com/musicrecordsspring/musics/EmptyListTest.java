package br.com.musicrecordsspring.musics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import java.util.List;
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
import br.com.musicrecordsspring.models.Music;
import br.com.musicrecordsspring.models.User;
import br.com.musicrecordsspring.repositories.MusicRepository;
import br.com.musicrecordsspring.repositories.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class EmptyListTest {

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
  public void emptyList() throws Exception {

    MockHttpServletResponse response =
        mockMvc.perform(delete("/musics/empty-list")).andReturn().getResponse();

    List<Music> dbMusics = musicRepository.findAll();
    Music music = dbMusics.get(0);

    assertEquals("10", response.getContentAsString());
    assertEquals(1, dbMusics.size());
    assertFalse(music.isDeleted());
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }
}
