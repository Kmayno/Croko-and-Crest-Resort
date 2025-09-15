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
    public List<Prenotazione> getPrenotazioniCliente(Integer idCliente) {
        return clienteRepository.findById(idCliente)
                .map(Cliente::getPrenotazioni)
                .orElse(List.of());
    }

    //visualizzare i dati di un cliente tramite id
    public Optional<Cliente> getClienteById(Integer id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> updateCliente(Integer id, Cliente clienteAggiornato) {
        Optional<Cliente> optCliente = clienteRepository.findById(id);
        if (!optCliente.isPresent()) return Optional.empty();

        Cliente cliente = optCliente.get();
        cliente.setNome(clienteAggiornato.getNome());
        cliente.setCognome(clienteAggiornato.getCognome());
        cliente.setEmail(clienteAggiornato.getEmail());

        // aggiorna la password solo se non vuota
        if (clienteAggiornato.getPasswordCliente() != null && !clienteAggiornato.getPasswordCliente().isEmpty()) {
            cliente.setPasswordCliente(clienteAggiornato.getPasswordCliente());
        }

        clienteRepository.save(cliente);
        return Optional.of(cliente);
    }

    //elimina un cliente tramite id
    public void deleteCliente(Integer id){
        clienteRepository.deleteById(id);
    }

}
