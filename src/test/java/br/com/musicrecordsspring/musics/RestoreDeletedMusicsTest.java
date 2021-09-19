package br.com.musicrecordsspring.musics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.musicrecordsspring.MusicFactory;
import br.com.musicrecordsspring.models.Music;
import br.com.musicrecordsspring.repositories.MusicRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class RestoreDeletedMusicsTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MusicFactory musicFactory;

  @Autowired
  private MusicRepository musicRepository;

  private List<Music> deletedMusics;
  private List<Music> musics;

  @BeforeEach
  public void commit() {
    deletedMusics = musicFactory.createBatch(10, true);
    musics = musicFactory.createBatch(10, false);
  }

  @AfterEach
  public void rollback() {
    musicRepository.deleteAll();
  }

  @Test
  public void restoreDeletedMusics() throws Exception {

    String jsonRequest =
        objectMapper.writeValueAsString(Arrays.copyOfRange(deletedMusics.toArray(), 0, 4));

    MockHttpServletResponse response = mockMvc.perform(post("/musics/deleted/restore")
        .contentType(MediaType.APPLICATION_JSON).content(jsonRequest)).andReturn().getResponse();

    Long dbCountDeletedMusics = musicRepository.countByDeletedIsTrue();

    assertEquals("4", response.getContentAsString());
    assertEquals(6L, dbCountDeletedMusics);
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  public void restoreNondeletedMusics() throws Exception {

    String jsonRequest =
        objectMapper.writeValueAsString(Arrays.copyOfRange(musics.toArray(), 0, 4));

    MockHttpServletResponse response = mockMvc.perform(post("/musics/deleted/restore")
        .contentType(MediaType.APPLICATION_JSON).content(jsonRequest)).andReturn().getResponse();

    Long dbCountDeletedMusics = musicRepository.countByDeletedIsTrue();

    assertEquals("4", response.getContentAsString());
    assertEquals(10L, dbCountDeletedMusics);
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }
}
