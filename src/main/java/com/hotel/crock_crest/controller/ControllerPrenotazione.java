package com.hotel.crock_crest.controller;

import com.hotel.crock_crest.model.*;
import com.hotel.crock_crest.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/prenotazioni")
@CrossOrigin(origins = "*")
public class ControllerPrenotazione {

    @Autowired
    private PrenotazioneService prenotazioneService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private CamereService camereService; // Cambiato da CameraService a CamereService

    @PostMapping
    public ResponseEntity<?> creaPrenotazione(@RequestBody PrenotazioneRequest request) {
        try {
            // validazione dati in input
            if (request.getDataInizio().isAfter(request.getDataFine())) {
                return ResponseEntity.badRequest()
                    .body("Errore: La data di inizio non può essere successiva alla data di fine");
            }
            
            if (request.getDataInizio().isBefore(LocalDate.now())) {
                return ResponseEntity.badRequest()
                    .body("Errore: Non puoi prenotare nel passato");
            }

            // verifica che cliente esista - usando Integer invece di Long
            Optional<Cliente> cliente = clienteService.getClienteById(request.getIdCliente());
            if (!cliente.isPresent()) {
                return ResponseEntity.badRequest()
                    .body("Errore: Cliente non trovato");
            }

            // verifica che camera esista e sia disponibile
            Camera camera = camereService.findById(request.getIdCamera());
            if (camera == null) {
                return ResponseEntity.badRequest()
                    .body("Errore: Camera non trovata");
            }
            
            if (!camera.getDisponibile()) { // Cambiato da isDisponibile() a getDisponibile()
                return ResponseEntity.badRequest()
                    .body("Errore: Camera non disponibile");
            }

            // verifica disponibilità nelle date richieste
            boolean cameraLibera = prenotazioneService.verificaDisponibilita(
                request.getIdCamera(), 
                request.getDataInizio(), 
                request.getDataFine()
            );
            
            if (!cameraLibera) {
                return ResponseEntity.badRequest()
                    .body("Errore: Camera già prenotata nelle date richieste");
            }

            // calcola il prezzo totale
            long numeroNotti = ChronoUnit.DAYS.between(request.getDataInizio(), request.getDataFine());
            double prezzoBase = camera.getPrezzoBaseNotte() * numeroNotti; // Cambiato da getPrezzoBase()
            double prezzoOpzioni = 0;
            
            // aggiungi costo opzioni personalizzazione
            if (request.getOpzioniIds() != null) {
                prezzoOpzioni = prenotazioneService.calcolaPrezzoOpzioni(request.getOpzioniIds());
            }
            
            double prezzoTotale = prezzoBase + prezzoOpzioni;

            // crea la prenotazione
            Prenotazione nuovaPrenotazione = new Prenotazione();
            nuovaPrenotazione.setCliente(cliente.get());
            nuovaPrenotazione.setCamera(camera);
            nuovaPrenotazione.setDataInizio(request.getDataInizio());
            nuovaPrenotazione.setDataFine(request.getDataFine());
            nuovaPrenotazione.setPrezzoTotale(prezzoTotale);
            nuovaPrenotazione.setStatoPrenotazione(true); // true = confermata, false = in attesa

            // salva la prenotazione
            Prenotazione prenotazioneSalvata = prenotazioneService.save(nuovaPrenotazione);

            // aggiungi i dettagli delle opzioni personalizzazione
            if (request.getOpzioniIds() != null && !request.getOpzioniIds().isEmpty()) {
                prenotazioneService.aggiungiOpzioniPrenotazione(
                    prenotazioneSalvata.getIdPrenotazione(), 
                    request.getOpzioniIds()
                );
            }

            // restituisci risposta di successo
            PrenotazioneResponse response = new PrenotazioneResponse(prenotazioneSalvata);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Errore interno del server: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPrenotazione(@PathVariable Integer id) { // Cambiato da Long a Integer
        try {
            Optional<Prenotazione> prenotazione = prenotazioneService.findById(id);
            
            if (!prenotazione.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            PrenotazioneResponse response = new PrenotazioneResponse(prenotazione.get());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Errore interno del server: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> aggiornaStatoPrenotazione(
            @PathVariable Integer id,  // Cambiato da Long a Integer
            @RequestParam boolean statoConfermato) {
        try {
            Optional<Prenotazione> prenotazioneOpt = prenotazioneService.findById(id);
            
            if (!prenotazioneOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Prenotazione prenotazione = prenotazioneOpt.get();
            
            // Verifica che la prenotazione non sia nel passato
            if (prenotazione.getDataInizio().isBefore(LocalDate.now())) {
                return ResponseEntity.badRequest()
                    .body("Errore: Non puoi modificare una prenotazione passata");
            }

            prenotazione.setStatoPrenotazione(statoConfermato);
            Prenotazione prenotazioneAggiornata = prenotazioneService.save(prenotazione);

            PrenotazioneResponse response = new PrenotazioneResponse(prenotazioneAggiornata);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Errore interno del server: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminaPrenotazione(@PathVariable Integer id) { // Cambiato da Long a Integer
        try {
            Optional<Prenotazione> prenotazioneOpt = prenotazioneService.findById(id);
            
            if (!prenotazioneOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Prenotazione prenotazione = prenotazioneOpt.get();
            
            // verifica che si possa ancora cancellare (almeno 24h prima)
            if (prenotazione.getDataInizio().minusDays(1).isBefore(LocalDate.now())) {
                return ResponseEntity.badRequest()
                    .body("Errore: Non puoi cancellare una prenotazione con meno di 24 ore di preavviso");
            }

            // elimina i dettagli della prenotazione (foreign key)
            prenotazioneService.eliminaDettagliPrenotazione(id);
            
            // poi elimina la prenotazione
            prenotazioneService.deleteById(id);

            return ResponseEntity.ok("Prenotazione eliminata con successo");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Errore interno del server: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getTutteLePrenotazioni(
            @RequestParam(required = false) Integer clienteId, // Cambiato da Long a Integer
            @RequestParam(required = false) Boolean stato) {
        try {
            List<Prenotazione> prenotazioni;

            // filtra in base ai parametri
            if (clienteId != null && stato != null) {
                prenotazioni = prenotazioneService.findByClienteAndStato(clienteId, stato);
            } else if (clienteId != null) {
                prenotazioni = prenotazioneService.findByCliente(clienteId);
            } else if (stato != null) {
                prenotazioni = prenotazioneService.findByStato(stato);
            } else {
                prenotazioni = prenotazioneService.findAll();
            }

            // Converti in DTO per risposta
            List<PrenotazioneResponse> response = prenotazioni.stream()
                .map(PrenotazioneResponse::new)
                .toList();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Errore interno del server: " + e.getMessage());
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> getPrenotazioniCliente(@PathVariable Integer clienteId) { // Cambiato da Long a Integer
        try {
            // Verifica che il cliente esista
            if (!clienteService.getClienteById(clienteId).isPresent()) {
                return ResponseEntity.notFound().build();
            }

            List<Prenotazione> prenotazioni = prenotazioneService.findByCliente(clienteId);
            
            List<PrenotazioneResponse> response = prenotazioni.stream()
                .map(PrenotazioneResponse::new)
                .toList();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Errore interno del server: " + e.getMessage());
        }
    }
}