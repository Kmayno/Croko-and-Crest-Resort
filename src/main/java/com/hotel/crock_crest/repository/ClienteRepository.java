package com.hotel.crock_crest.repository;
import com.hotel.crock_crest.model.Admin;
import com.hotel.crock_crest.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    // Per login (ricerca per email e password)
    Cliente findByEmailAndPasswordCliente(String email, String passwordAdmin);
}
