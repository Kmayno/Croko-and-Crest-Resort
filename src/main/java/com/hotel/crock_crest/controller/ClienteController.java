package com.hotel.crock_crest.controller;

import com.hotel.crock_crest.model.Cliente;
import com.hotel.crock_crest.model.Prenotazione;
import com.hotel.crock_crest.service.ClienteService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import com.hotel.crock_crest.model.Cliente;

@RestController
@RequestMapping("/api/cliente")
public class ClienteController {

	private final ClienteService clienteService;

	public ClienteController(ClienteService clienteService) {
		super();
		this.clienteService = clienteService;
	}

	// metodo post per registrare un cliente
/*	 @PostMapping
     public Cliente registraCliente(@RequestBody Cliente cliente) {
        return clienteService.saveCliente(cliente);
    }*/

	//metodo post per il login
	@PostMapping("/login")
   		public Optional<Cliente> loginCliente(@RequestParam String email, @RequestParam String password) {
       	 return clienteService.login(email, password);
    }

	// metodo get per visualizzare le prenotazioni di un cliente tramite ricerca per id
	 @GetMapping("/{id}/prenotazioni")
    public List<Prenotazione> getPrenotazioniCliente(@PathVariable Integer idCliente) {
        return clienteService.getPrenotazioniCliente(idCliente);
    }

	// metodo get per visualizzare i dati del cliente tramite ricerca per id
	  @GetMapping("/{id}")
    public Optional<Cliente> getCliente(@PathVariable Integer id) {
        return clienteService.getClienteById(id);
    }

	// metodo put per modificare i dati di un cliente tramite ricerca per id
	 @PutMapping("/{id}")
    public Optional<Cliente> updateCliente(@PathVariable Integer id, @RequestBody Cliente cliente) {
        return clienteService.updateCliente(id, cliente);
    }

	//metodo delete per eliminare un cliente tramite ricerca per id
	 @DeleteMapping("/{id}")
   public boolean deleteCliente(@PathVariable Integer id) {
        return clienteService.deleteCliente(id);
    }
}