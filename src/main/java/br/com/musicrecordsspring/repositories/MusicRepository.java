package br.com.musicrecordsspring.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import br.com.musicrecordsspring.models.Music;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {

  public Page<Music> findAllByDeleted(boolean deleted, Pageable pageable);

  public List<Music> findAllByDeleted(boolean deleted, Sort sort);

  public Optional<Music> findByIdAndDeleted(Long id, boolean deleted);

  public Long countByDeletedIsTrue();

  @Transactional
  @Modifying
  @Query("UPDATE Music m SET m.deleted = false WHERE m.id IN :ids")
  public int restoreDeletedMusics(@Param("ids") List<Long> musicIds);

  @Transactional
  public int deleteAllByDeletedIsTrue();

}
