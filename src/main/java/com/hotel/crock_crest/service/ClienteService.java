package com.hotel.crock_crest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hotel.crock_crest.model.Cliente;
import com.hotel.crock_crest.model.Prenotazione;
import com.hotel.crock_crest.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    //registra un cliente
     public Cliente addCliente(Cliente c) {
        return clienteRepository.save(c);
    }

    //login tramite email e password
    public Cliente findByEmailAndPass(String email, String password){
        Cliente foundByEmail = clienteRepository.findByEmailAndPasswordCliente(email,password);
        return foundByEmail;
    }


    //visualizzare tutte le prenotazioni di un cliente
    public List<Prenotazione> getPrenotazioniCliente(int idCliente) {
        return clienteRepository.findById(idCliente)
                .map(Cliente::getPrenotazioni)
                .orElse(List.of());
    }

    //visualizzare i dati di un cliente tramite id
    public Optional<Cliente> getClienteById(int id) {
        return clienteRepository.findById(id);
    }

    //aggiorna i dati di un cliente tramite id
     public Optional<Cliente> updateCliente(int id, Cliente updated) {
        return clienteRepository.findById(id).map(c -> {
            c.setNome(updated.getNome());
            c.setCognome(updated.getCognome());
            c.setEmail(updated.getEmail());
            c.setPasswordCliente(updated.getPasswordCliente());
            return clienteRepository.save(c);
        });
    }

    //elimina un cliente tramite id
    public void deleteCliente(int id){
        clienteRepository.deleteById(id);
    }

}
