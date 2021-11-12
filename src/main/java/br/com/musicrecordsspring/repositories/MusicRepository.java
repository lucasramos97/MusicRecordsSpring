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
import br.com.musicrecordsspring.models.User;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {

  public Page<Music> findAllByUserAndDeleted(User user, boolean deleted, Pageable pageable);

  public List<Music> findAllByUserAndDeleted(User user, boolean deleted, Sort sort);

  public List<Music> findAllByUser(User user);

  public Optional<Music> findByIdAndUserAndDeleted(Long id, User user, boolean deleted);

  public Optional<Music> findByIdAndUser(Long id, User user);

  public Long countByUserAndDeleted(User user, boolean deleted);

  public Long countByUser(User user);

  @Transactional
  @Modifying
  @Query("UPDATE Music m SET m.deleted = false, m.updatedAt = CURRENT_TIMESTAMP WHERE m.deleted = true AND m.id IN :ids AND m.user = :user")
  public int restoreDeletedMusicsByUser(@Param("ids") List<Long> musicIds,
      @Param("user") User user);

  @Transactional
  public int deleteAllByUserAndDeletedIsTrue(User user);

}
