package br.com.musicrecords.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.com.musicrecords.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

  public Optional<User> findByEmail(String email);

}
