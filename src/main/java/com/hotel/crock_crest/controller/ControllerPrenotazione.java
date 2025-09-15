package com.hotel.crock_crest.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.crock_crest.model.Camera;
import com.hotel.crock_crest.model.Cliente;
import com.hotel.crock_crest.model.Prenotazione;
import com.hotel.crock_crest.model.PrenotazioneRequest;
import com.hotel.crock_crest.model.PrenotazioneResponse;
import com.hotel.crock_crest.service.CamereService;
import com.hotel.crock_crest.service.ClienteService;
import com.hotel.crock_crest.service.PrenotazioneService;

@RestController // setta gli endpoint che saranno chiamati dal client
@RequestMapping("/api/prenotazioni") // mappa l'endpoint generale
@CrossOrigin(origins = "*") // evita le politiche di cors (evita chiamate ad endpoint per sicurezza), possiamo fare delle chiamate al server da localhost
public class ControllerPrenotazione {

    private static final Logger logger = LoggerFactory.getLogger(ControllerPrenotazione.class);

    @Autowired // dependency injection, invece di una classe che crea direttamente le sue dipendenze, queste le vengono "iniettate" dall'esterno
    private PrenotazioneService prenotazioneService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private CamereService camereService; 

    @PostMapping("addPrenotazione")
    // reqEntity wrapper per risposte http, ci da controllo su body headers etc.
    public ResponseEntity<?> creaPrenotazione(@RequestBody PrenotazioneRequest request) { // reqBody si pija il json
        logger.info("=== RICEVUTA RICHIESTA PRENOTAZIONE ===");
        
        // Verifica che la request non sia null
        if (request == null) {
            logger.error("Request body è null");
            return ResponseEntity.badRequest()
                .body("Errore: Request body mancante o malformato");
        }
        
        logger.info("Request data: {}", request);
        logger.info("ID Cliente: {}", request.getIdCliente());
        logger.info("ID Camera: {}", request.getIdCamera());
        logger.info("Data Inizio: {}", request.getDataInizio());
        logger.info("Data Fine: {}", request.getDataFine());
        logger.info("Opzioni IDs: {}", request.getOpzioniIds());
        
        try {
            // validazione dati in input
            if (request.getIdCliente() == null) {
                logger.error("ID cliente mancante");
                return ResponseEntity.badRequest()
                    .body("Errore: ID cliente mancante");
            }
            
            if (request.getIdCamera() == null) {
                logger.error("ID camera mancante");
                return ResponseEntity.badRequest()
                    .body("Errore: ID camera mancante");
            }
            
            if (request.getDataInizio() == null || request.getDataFine() == null) {
                logger.error("Date mancanti - Data Inizio: {}, Data Fine: {}", request.getDataInizio(), request.getDataFine());
                return ResponseEntity.badRequest()
                    .body("Errore: Date mancanti");
            }
            
            if (request.getDataInizio().isAfter(request.getDataFine())) {
                logger.error("Data inizio dopo data fine - Inizio: {}, Fine: {}", request.getDataInizio(), request.getDataFine());
                return ResponseEntity.badRequest()
                    .body("La data di check-in deve essere precedente alla data di check-out");
            }
            
            if (request.getDataInizio().isBefore(LocalDate.now())) {
                logger.error("Tentativo di prenotazione nel passato - Data: {}, Oggi: {}", request.getDataInizio(), LocalDate.now());
                return ResponseEntity.badRequest()
                    .body("Non puoi prenotare per date passate");
            }

            // verifica che cliente esista - usando Integer invece di Long
            logger.info("Ricerca cliente con ID: {}", request.getIdCliente());
            Optional<Cliente> cliente = clienteService.getClienteById(request.getIdCliente());
            if (!cliente.isPresent()) {
                logger.error("Cliente non trovato con ID: {}", request.getIdCliente());
                return ResponseEntity.badRequest()
                    .body("Errore: Cliente non trovato con ID: " + request.getIdCliente());
            }
            logger.info("Cliente trovato: {}", cliente.get().getNome() + " " + cliente.get().getCognome());

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
                logger.error("Camera {} già prenotata dal {} al {}", 
                    request.getIdCamera(), request.getDataInizio(), request.getDataFine());
                return ResponseEntity.badRequest()
                    .body("Camera già prenotata nelle date richieste. Scegli altre date o un'altra camera.");
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
            nuovaPrenotazione.setStatoPrenotazione(false); // false = in attesa di conferma admin, true = confermata

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
            logger.info("Prenotazione creata con successo con ID: {}", prenotazioneSalvata.getIdPrenotazione());
            PrenotazioneResponse response = new PrenotazioneResponse(prenotazioneSalvata);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.error("Errore durante la creazione della prenotazione", e);
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
            @PathVariable Integer id,  // accetta dei parametri dall
            @RequestParam boolean statoConfermato) { // accetta dei paramentri che sono nella querystring
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

    // Endpoint per calcolare il prezzo di una prenotazione
    @PostMapping("/calculatePrice")
    public ResponseEntity<?> calculatePrice(@RequestBody PrenotazioneRequest request) {
        try {
            // Validazione dati in input
            if (request.getDataInizio().isAfter(request.getDataFine())) {
                return ResponseEntity.badRequest()
                    .body("Errore: La data di inizio non può essere successiva alla data di fine");
            }
            
            if (request.getDataInizio().isBefore(LocalDate.now())) {
                return ResponseEntity.badRequest()
                    .body("Errore: Non puoi prenotare nel passato");
            }

            // verifica che camera esista
            Camera camera = camereService.findById(request.getIdCamera());
            if (camera == null) {
                return ResponseEntity.badRequest()
                    .body("Errore: Camera non trovata");
            }
            
            if (!camera.getDisponibile()) {
                return ResponseEntity.badRequest()
                    .body("Errore: Camera non disponibile");
            }

            // calcola il prezzo totale
            long numeroNotti = ChronoUnit.DAYS.between(request.getDataInizio(), request.getDataFine());
            double prezzoBase = camera.getPrezzoBaseNotte() * numeroNotti;
            double prezzoOpzioni = 0;
            
            // aggiungi costo opzioni personalizzazione
            if (request.getOpzioniIds() != null) {
                prezzoOpzioni = prenotazioneService.calcolaPrezzoOpzioni(request.getOpzioniIds());
            }
            
            double prezzoTotale = prezzoBase + prezzoOpzioni;

            // Restituisci il prezzo calcolato
            return ResponseEntity.ok(prezzoTotale);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Errore interno del server: " + e.getMessage());
        }
    }

    // Endpoint di test per verificare la connessione
    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        logger.info("Test endpoint chiamato");
        return ResponseEntity.ok("Controller prenotazioni funziona correttamente!");
    }

    // Endpoint di test per verificare la ricezione di dati
    @PostMapping("/test-data")
    public ResponseEntity<?> testData(@RequestBody PrenotazioneRequest request) {
        logger.info("=== TEST RICEZIONE DATI ===");
        
        if (request == null) {
            logger.error("Request body è null nel test");
            return ResponseEntity.badRequest()
                .body("Errore: Request body null");
        }
        
        logger.info("Request ricevuta: {}", request);
        logger.info("ID Cliente: {}", request.getIdCliente());
        logger.info("ID Camera: {}", request.getIdCamera());
        logger.info("Data Inizio: {}", request.getDataInizio());
        logger.info("Data Fine: {}", request.getDataFine());
        logger.info("Opzioni: {}", request.getOpzioniIds());
        
        return ResponseEntity.ok("Dati ricevuti correttamente: " + request.toString());
    }

    // Endpoint per testare la deserializzazione JSON con stringa raw
    @PostMapping("/test-raw")
    public ResponseEntity<?> testRaw(@RequestBody String rawJson) {
        logger.info("=== TEST RAW JSON ===");
        logger.info("JSON ricevuto: {}", rawJson);
        
        try {
            return ResponseEntity.ok("JSON ricevuto: " + rawJson);
        } catch (Exception e) {
            logger.error("Errore nel parsing JSON raw", e);
            return ResponseEntity.badRequest()
                .body("Errore nel parsing: " + e.getMessage());
        }
    }

    // Endpoint per confermare prenotazione (solo admin)
    @PutMapping("/conferma/{idPrenotazione}")
    public ResponseEntity<String> confermaPrenotazione(@PathVariable int idPrenotazione) {
        try {
            logger.info("Tentativo di conferma prenotazione con ID: {}", idPrenotazione);
            
            // Verifica che la prenotazione esista
            Optional<Prenotazione> prenotazioneOpt = prenotazioneService.findById(idPrenotazione);
            if (!prenotazioneOpt.isPresent()) {
                logger.error("Prenotazione non trovata con ID: {}", idPrenotazione);
                return ResponseEntity.badRequest()
                    .body("Prenotazione non trovata con ID: " + idPrenotazione);
            }
            
            Prenotazione prenotazione = prenotazioneOpt.get();
            
            // Verifica che la prenotazione non sia già confermata
            if (prenotazione.getStatoPrenotazione()) {
                logger.warn("Prenotazione {} già confermata", idPrenotazione);
                return ResponseEntity.badRequest()
                    .body("Prenotazione già confermata");
            }
            
            // Verifica nuovamente la disponibilità della camera
            boolean cameraLibera = prenotazioneService.verificaDisponibilita(
                prenotazione.getCamera().getIdCamera(),
                prenotazione.getDataInizio(),
                prenotazione.getDataFine()
            );
            
            if (!cameraLibera) {
                logger.error("Camera non più disponibile per prenotazione {}", idPrenotazione);
                return ResponseEntity.badRequest()
                    .body("Camera non più disponibile nelle date richieste");
            }
            
            // Conferma la prenotazione
            prenotazione.setStatoPrenotazione(true);
            prenotazioneService.save(prenotazione);
            
            logger.info("Prenotazione {} confermata con successo", idPrenotazione);
            return ResponseEntity.ok("Prenotazione confermata con successo");
            
        } catch (Exception e) {
            logger.error("Errore durante la conferma della prenotazione {}", idPrenotazione, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Errore interno del server: " + e.getMessage());
        }
    }

    // Endpoint per rifiutare prenotazione (solo admin)
    @DeleteMapping("/rifiuta/{idPrenotazione}")
    public ResponseEntity<String> rifiutaPrenotazione(@PathVariable int idPrenotazione) {
        try {
            logger.info("Tentativo di rifiuto prenotazione con ID: {}", idPrenotazione);
            
            // Verifica che la prenotazione esista
            Optional<Prenotazione> prenotazioneOpt = prenotazioneService.findById(idPrenotazione);
            if (!prenotazioneOpt.isPresent()) {
                logger.error("Prenotazione non trovata con ID: {}", idPrenotazione);
                return ResponseEntity.badRequest()
                    .body("Prenotazione non trovata con ID: " + idPrenotazione);
            }
            
            Prenotazione prenotazione = prenotazioneOpt.get();
            
            // Verifica che la prenotazione non sia già confermata
            if (prenotazione.getStatoPrenotazione()) {
                logger.warn("Impossibile rifiutare prenotazione {} già confermata", idPrenotazione);
                return ResponseEntity.badRequest()
                    .body("Impossibile rifiutare una prenotazione già confermata");
            }
            
            // Elimina la prenotazione
            prenotazioneService.deleteById(idPrenotazione);
            
            logger.info("Prenotazione {} rifiutata ed eliminata", idPrenotazione);
            return ResponseEntity.ok("Prenotazione rifiutata ed eliminata");
            
        } catch (Exception e) {
            logger.error("Errore durante il rifiuto della prenotazione {}", idPrenotazione, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Errore interno del server: " + e.getMessage());
        }
    }

    // Endpoint per ottenere prenotazioni in attesa di conferma (solo admin)
    @GetMapping("/pending")
    public ResponseEntity<List<PrenotazioneResponse>> getPrenotazioniInAttesa() {
        try {
            logger.info("Richiesta prenotazioni in attesa di conferma");
            List<Prenotazione> prenotazioniInAttesa = prenotazioneService.findByStato(false);
            
            List<PrenotazioneResponse> response = prenotazioniInAttesa.stream()
                .map(PrenotazioneResponse::new)
                .toList();
            
            logger.info("Trovate {} prenotazioni in attesa", response.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Errore durante il recupero delle prenotazioni in attesa", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);
        }
    }
}