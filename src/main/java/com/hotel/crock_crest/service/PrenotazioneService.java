package com.hotel.crock_crest.service;

import com.hotel.crock_crest.model.*;
import com.hotel.crock_crest.repository.OptionsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PrenotazioneService {

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;
    
    @Autowired
    private DettaglioPrenotazioneRepository dettaglioPrenotazioneRepository;
    
    @Autowired
    private OptionsRepository opzioneRepository;

    @Transactional
    public Prenotazione save(Prenotazione prenotazione) {
        return prenotazioneRepository.save(prenotazione);
    }

    /**
     * Trova una prenotazione per ID
     * @param id L'ID della prenotazione
     * @return Optional contenente la prenotazione se trovata
     */
    public Optional<Prenotazione> findById(Long id) {
        return prenotazioneRepository.findById(id);
    }

    /**
     * Trova tutte le prenotazioni
     * @return Lista di tutte le prenotazioni
     */
    public List<Prenotazione> findAll() {
        return prenotazioneRepository.findAll();
    }

    /**
     * Elimina una prenotazione per ID
     * @param id L'ID della prenotazione da eliminare
     */
    @Transactional
    public void deleteById(Long id) {
        prenotazioneRepository.deleteById(id);
    }

    /**
     * Trova prenotazioni di un cliente specifico
     * @param clienteId L'ID del cliente
     * @return Lista delle prenotazioni del cliente
     */
    public List<Prenotazione> findByCliente(Long clienteId) {
        return prenotazioneRepository.findByClienteIdCliente(clienteId);
    }

    /**
     * Trova prenotazioni per stato
     * @param stato true = confermate, false = in attesa
     * @return Lista delle prenotazioni con lo stato specificato
     */
    public List<Prenotazione> findByStato(Boolean stato) {
        return prenotazioneRepository.findByStatoPrenotazione(stato);
    }

    /**
     * Trova prenotazioni di un cliente con uno stato specifico
     * @param clienteId L'ID del cliente
     * @param stato Lo stato delle prenotazioni
     * @return Lista delle prenotazioni filtrate
     */
    public List<Prenotazione> findByClienteAndStato(Long clienteId, Boolean stato) {
        return prenotazioneRepository.findByClienteIdClienteAndStatoPrenotazione(clienteId, stato);
    }

    /**
     * METODO CHIAVE: Verifica se una camera è disponibile in un periodo
     * 
     * Logica: Una camera è disponibile se NON ci sono prenotazioni confermate
     * che si sovrappongono al periodo richiesto
     * 
     * @param idCamera ID della camera da verificare
     * @param dataInizio Data di inizio del soggiorno richiesto
     * @param dataFine Data di fine del soggiorno richiesto
     * @return true se disponibile, false se occupata
     */
    public boolean verificaDisponibilita(Long idCamera, LocalDate dataInizio, LocalDate dataFine) {
        // Cerchiamo prenotazioni che si sovrappongono
        List<Prenotazione> prenotazioniSovrapposte = prenotazioneRepository
            .findPrenotazioniSovrapposte(idCamera, dataInizio, dataFine);
        
        // Se la lista è vuota, la camera è libera
        return prenotazioniSovrapposte.isEmpty();
    }

    /**
     * Calcola il prezzo totale delle opzioni personalizzazione
     * @param opzioniIds Lista degli ID delle opzioni scelte
     * @return Prezzo totale delle opzioni
     */
    public double calcolaPrezzoOpzioni(List<Long> opzioniIds) {
        if (opzioniIds == null || opzioniIds.isEmpty()) {
            return 0.0;
        }

        double totale = 0.0;
        for (Long opzioneId : opzioniIds) {
            Optional<OpzionePersonalizzazione> opzione = opzioneRepository.findById(opzioneId);
            if (opzione.isPresent()) {
                totale += opzione.get().getPrezzoAggiuntivo();
            }
        }
        return totale;
    }

    /**
     * Aggiunge le opzioni personalizzazione a una prenotazione
     * Questo metodo crea i record nella tabella dettagli_prenotazione
     * 
     * @param idPrenotazione ID della prenotazione
     * @param opzioniIds Lista degli ID delle opzioni da aggiungere
     */
    @Transactional
    public void aggiungiOpzioniPrenotazione(Long idPrenotazione, List<Long> opzioniIds) {
        if (opzioniIds == null || opzioniIds.isEmpty()) {
            return;
        }

        // Troviamo la prenotazione
        Optional<Prenotazione> prenotazioneOpt = prenotazioneRepository.findById(idPrenotazione);
        if (!prenotazioneOpt.isPresent()) {
            throw new RuntimeException("Prenotazione non trovata con ID: " + idPrenotazione);
        }

        Prenotazione prenotazione = prenotazioneOpt.get();

        // Per ogni opzione scelta, creiamo un DettaglioPrenotazione
        for (Long opzioneId : opzioniIds) {
            Optional<OpzionePersonalizzazione> opzioneOpt = opzioneRepository.findById(opzioneId);
            if (opzioneOpt.isPresent()) {
                DettaglioPrenotazione dettaglio = new DettaglioPrenotazione();
                dettaglio.setPrenotazione(prenotazione);
                dettaglio.setOpzionePersonalizzazione(opzioneOpt.get());
                
                dettaglioPrenotazioneRepository.save(dettaglio);
            }
        }
    }

    /**
     * Elimina tutti i dettagli di una prenotazione
     * Necessario prima di eliminare la prenotazione (foreign key)
     * 
     * @param idPrenotazione ID della prenotazione
     */
    @Transactional
    public void eliminaDettagliPrenotazione(Long idPrenotazione) {
        dettaglioPrenotazioneRepository.deleteByPrenotazioneIdPrenotazione(idPrenotazione);
    }

    /**
     * Verifica se un cliente può fare una nuova prenotazione
     * Regole business: max 3 prenotazioni attive contemporaneamente
     * 
     * @param clienteId ID del cliente
     * @return true se può prenotare, false altrimenti
     */
    public boolean puoPrenotare(Long clienteId) {
        boolean hasPrenotazioniAttive = prenotazioneRepository.hasPrenotazioniAttive(clienteId, LocalDate.now());
        Long numeroPrenotazioni = prenotazioneRepository.countPrenotazioniConfermate(clienteId);
        
        // Massimo 3 prenotazioni attive
        return numeroPrenotazioni < 3;
    }

    /**
     * Calcola statistiche per una camera (utile per admin)
     * @param idCamera ID della camera
     * @return Fatturato totale della camera
     */
    public Double calcolaFatturatoCamera(Long idCamera) {
        Double fatturato = prenotazioneRepository.calcolaFatturatoCamera(idCamera);
        return fatturato != null ? fatturato : 0.0;
    }

    /**
     * Trova prenotazioni che iniziano presto (per reminder)
     * @param giorni Numero di giorni di anticipo
     * @return Lista delle prenotazioni in scadenza
     */
    public List<Prenotazione> getPrenotazioniInScadenza(int giorni) {
        LocalDate oggi = LocalDate.now();
        LocalDate dataLimite = oggi.plusDays(giorni);
        
        return prenotazioneRepository.findPrenotazioniInScadenza(oggi, dataLimite);
    }

    /**
     * Trova le ultime prenotazioni per dashboard admin
     * @return Lista delle ultime 10 prenotazioni
     */
    public List<Prenotazione> getUltimePrenotazioni() {
        return prenotazioneRepository.findTop10ByOrderByIdPrenotazioneDesc();
    }

    /**
     * Valida i dati di una prenotazione prima di salvarla
     * @param dataInizio Data di inizio
     * @param dataFine Data di fine
     * @param idCamera ID della camera
     * @return Lista degli errori trovati (vuota se tutto OK)
     */
    public List<String> validaPrenotazione(LocalDate dataInizio, LocalDate dataFine, Long idCamera) {
        List<String> errori = new java.util.ArrayList<>();

        // Validazione date
        if (dataInizio.isAfter(dataFine)) {
            errori.add("La data di inizio non può essere successiva alla data di fine");
        }
        
        if (dataInizio.isBefore(LocalDate.now())) {
            errori.add("Non puoi prenotare nel passato");
        }
        
        // Validazione durata massima (es. 30 giorni)
        if (java.time.temporal.ChronoUnit.DAYS.between(dataInizio, dataFine) > 30) {
            errori.add("La prenotazione non può superare i 30 giorni");
        }

        // Validazione disponibilità
        if (!verificaDisponibilita(idCamera, dataInizio, dataFine)) {
            errori.add("La camera non è disponibile nelle date richieste");
        }

        return errori;
    }

    /**
     * Aggiorna lo stato di una prenotazione
     * @param id ID della prenotazione
     * @param nuovoStato Nuovo stato (true/false)
     * @return La prenotazione aggiornata
     */
    @Transactional
    public Optional<Prenotazione> aggiornaStato(Long id, boolean nuovoStato) {
        Optional<Prenotazione> prenotazioneOpt = prenotazioneRepository.findById(id);
        
        if (prenotazioneOpt.isPresent()) {
            Prenotazione prenotazione = prenotazioneOpt.get();
            prenotazione.setStatoPrenotazione(nuovoStato);
            return Optional.of(prenotazioneRepository.save(prenotazione));
        }
        
        return Optional.empty();
    }
}
