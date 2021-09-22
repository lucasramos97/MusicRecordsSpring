package br.com.musicrecordsspring.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.musicrecordsspring.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  public Optional<User> findUserByEmail(String email);

}
