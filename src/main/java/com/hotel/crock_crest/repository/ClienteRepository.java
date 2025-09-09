package com.hotel.crock_crest.repository;
import com.hotel.crock_crest.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    // Per login (ricerca per email)
    Optional<Cliente> findByEmail(String email);
}
