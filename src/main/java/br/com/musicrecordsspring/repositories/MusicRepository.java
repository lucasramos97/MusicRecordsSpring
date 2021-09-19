package br.com.musicrecordsspring.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.musicrecordsspring.models.Music;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {

  public Page<Music> findAllByDeletedIsFalse(Pageable pageable);

  public List<Music> findAllByDeletedIsFalse(Sort sort);

  public Optional<Music> findByIdAndDeletedIsFalse(Long id);

}
