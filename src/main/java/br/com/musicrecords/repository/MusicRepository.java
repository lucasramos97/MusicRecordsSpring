package br.com.musicrecords.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.musicrecords.model.Music;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {

  public Page<Music> findAllByUserEmailAndDeletedIsFalse(String email, Pageable pageable);

}
