package br.com.musicrecordsspring.musics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import br.com.musicrecordsspring.MusicFactory;
import br.com.musicrecordsspring.models.Music;
import br.com.musicrecordsspring.models.PagedMusic;
import br.com.musicrecordsspring.repositories.MusicRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class GetMusicsTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MusicFactory musicFactory;

  @Autowired
  private MusicRepository musicRepository;

  @BeforeEach
  public void commit() {
    musicFactory.createBatch(10);
    musicFactory.create(true);
  }

  @AfterEach
  public void rollback() {
    musicRepository.deleteAll();
  }

  @Test
  public void getMusicsWithDefaultQueryParams() throws Exception {

    MockHttpServletResponse response = mockMvc.perform(get("/musics")).andReturn().getResponse();

    PagedMusic responseMusics =
        objectMapper.readValue(response.getContentAsString(), PagedMusic.class);
    String responseContent = objectMapper.writeValueAsString(responseMusics.getContent());

    List<Music> dbMusics = musicRepository.findAllByDeletedIsFalse(Sort.by("artist", "title"));
    String dbContent =
        objectMapper.writeValueAsString(Arrays.copyOfRange(dbMusics.toArray(), 0, 5));

    assertEquals(dbContent, responseContent);
    assertEquals(5, responseMusics.getContent().size());
    assertEquals(dbMusics.size(), responseMusics.getTotal());
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

  @Test
  public void getMusicsWithExplicitQueryParams() throws Exception {

    MockHttpServletResponse response = mockMvc
        .perform(get("/musics").param("page", "2").param("size", "4")).andReturn().getResponse();

    PagedMusic responseMusics =
        objectMapper.readValue(response.getContentAsString(), PagedMusic.class);
    String responseContent = objectMapper.writeValueAsString(responseMusics.getContent());

    List<Music> dbMusics = musicRepository.findAllByDeletedIsFalse(Sort.by("artist", "title"));
    String dbContent =
        objectMapper.writeValueAsString(Arrays.copyOfRange(dbMusics.toArray(), 4, 8));

    assertEquals(dbContent, responseContent);
    assertEquals(4, responseMusics.getContent().size());
    assertEquals(dbMusics.size(), responseMusics.getTotal());
    assertEquals(HttpStatus.OK.value(), response.getStatus());
  }

}
