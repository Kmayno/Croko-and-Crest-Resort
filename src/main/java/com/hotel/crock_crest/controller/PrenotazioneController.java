package com.hotel.crock_crest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import io.micrometer.core.ipc.http.HttpSender;

@RestController
@RequestMapping("/api/prenotazioni")
@CrossOrigin(origins = "*") // permette chiamate da frontend - da vedere
public class PrenotazioneController {
    @Autowired
    private PrenotazioneService prenotazioneService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CameraService cameraService;

    @PostMapping
    public ResponseEntity<?> creaPrenotazione(@RequestBody PrenotazioneRequest request) {
        try {
            // validazione dati input
            if (request.getDataInizio().isAfter(request.getDataFine())) {
                return ResponseEntity.badRequest().body("Errore: La data di inizio non può essere successiva alla data di fine.");
            }

            if (request.getDataInizio().isBefore(LocalDate.now())) {
                return ResponseEntity.badRequest().body("Errore: Non puoi prenotare nel passato!");
            }

            // controlla che il cliente esiste
            Optional<Cliente> cliente = clienteService.findById(request.getIdCliente());
            if (!cliente.isPresent()) {
                return ResponseEntity.badRequest().body("Errore: Cliente non trovato.");
            }

            // verifica che la camera esiste e che sia disponibile
            Optional<Camera> camera = cameraService.findById(request.getIdCamera());
            if (!camera.isPresent()) {
                return ResponseEntity.badRequest().body("Errore: Camera non trovata.")
            }

            if (!camera.get.isDisposnibile()) {
                return ResponseEntity.badRequest().body("Errore: Camera non disponibile.")
            }

            // verifica disponibilità nelle date richieste
            boolean cameraLibera = prenotazioneService.verificaDisponibilita(
                request.getIdCamera(),
                request.getDataInizio(),
                request.getDataFine()
            );

            if (!cameraLibera) {
                return ResponseEntity.badRequest().body("Errore: Camera già prenotata nelle date richieste.");
            }

            


        }
    }
}
