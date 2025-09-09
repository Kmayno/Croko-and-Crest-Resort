package com.hotel.crock_crest.service;

import com.hotel.crock_crest.model.*;
import com.hotel.crock_crest.repository.*;
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
    private OptionsRepository optionsRepository;

    @Autowired
    private DettaglioPrenotazioneRepository dettaglioPrenotazioneRepository;

    @Transactional
    public Prenotazione save(Prenotazione prenotazione) {
        return prenotazioneRepository.save(prenotazione);
    }
    
    public Optional<Prenotazione> findById(Integer id) {
        return prenotazioneRepository.findById(id);
    }

    public List<Prenotazione> findAll() {
        return prenotazioneRepository.findAll();
    }

    @Transactional
    public void deleteById(Integer id) {
        prenotazioneRepository.deleteById(id);
    }

    public List<Prenotazione> findByCliente(Integer clienteId) {
        return prenotazioneRepository.findByClienteIdCliente(clienteId);
    }

    public List<Prenotazione> findByStato(Boolean stato) {
        return prenotazioneRepository.findByStatoPrenotazione(stato);
    }

    public List<Prenotazione> findByClienteAndStato(Integer clienteId, Boolean stato) {
        return prenotazioneRepository.findByClienteIdClienteAndStatoPrenotazione(clienteId, stato);
    }

    public boolean verificaDisponibilita(Integer idCamera, LocalDate dataInizio, LocalDate dataFine) {
        List<Prenotazione> prenotazioniSovrapposte = prenotazioneRepository
            .findPrenotazioniSovrapposte(idCamera, dataInizio, dataFine);
        return prenotazioniSovrapposte.isEmpty();
    }

    public double calcolaPrezzoOpzioni(List<Integer> opzioniIds) {
        if (opzioniIds == null || opzioniIds.isEmpty()) {
            return 0.0;
        }

        double totale = 0.0;
        for (Integer opzioneId : opzioniIds) {
            Optional<OpzionePersonalizzazione> opzione = optionsRepository.findById(opzioneId);
            if (opzione.isPresent()) {
                totale += opzione.get().getPrezzoAggiuntivo();
            }
        }
        return totale;
    }

    @Transactional
    public void aggiungiOpzioniPrenotazione(Integer idPrenotazione, List<Integer> opzioniIds) {
        if (opzioniIds == null || opzioniIds.isEmpty()) {
            return;
        }

        Optional<Prenotazione> prenotazioneOpt = prenotazioneRepository.findById(idPrenotazione);
        if (!prenotazioneOpt.isPresent()) {
            throw new RuntimeException("Prenotazione non trovata con ID: " + idPrenotazione);
        }

        Prenotazione prenotazione = prenotazioneOpt.get();

        for (Integer opzioneId : opzioniIds) {
            Optional<OpzionePersonalizzazione> opzioneOpt = optionsRepository.findById(opzioneId);
            if (opzioneOpt.isPresent()) {
                DettaglioPrenotazione dettaglio = new DettaglioPrenotazione();
                dettaglio.setPrenotazione(prenotazione);
                dettaglio.setOpzionePersonalizzazione(opzioneOpt.get());
                
                dettaglioPrenotazioneRepository.save(dettaglio);
            }
        }
    }

    @Transactional
    public void eliminaDettagliPrenotazione(Integer idPrenotazione) {
        dettaglioPrenotazioneRepository.deleteByPrenotazioneIdPrenotazione(idPrenotazione);
    }

    public boolean puoPrenotare(Integer clienteId) {
        boolean hasPrenotazioniAttive = prenotazioneRepository.hasPrenotazioniAttive(clienteId, LocalDate.now());
        Long numeroPrenotazioni = prenotazioneRepository.countPrenotazioniConfermate(clienteId);
        
        return numeroPrenotazioni < 3;
    }


    public Double calcolaFatturatoCamera(Integer idCamera) {
        Double fatturato = prenotazioneRepository.calcolaFatturatoCamera(idCamera);
        return fatturato != null ? fatturato : 0.0;
    }
    public List<Prenotazione> getPrenotazioniInScadenza(int giorni) {
        LocalDate oggi = LocalDate.now();
        LocalDate dataLimite = oggi.plusDays(giorni);
        
        return prenotazioneRepository.findPrenotazioniInScadenza(oggi, dataLimite);
    }

    public List<Prenotazione> getUltimePrenotazioni() {
        return prenotazioneRepository.findTop10ByOrderByIdPrenotazioneDesc();
    }

    public List<String> validaPrenotazione(LocalDate dataInizio, LocalDate dataFine, Integer idCamera) {
        List<String> errori = new java.util.ArrayList<>();

        if (dataInizio.isAfter(dataFine)) {
            errori.add("La data di inizio non può essere successiva alla data di fine");
        }
        
        if (dataInizio.isBefore(LocalDate.now())) {
            errori.add("Non puoi prenotare nel passato");
        }
        
        if (java.time.temporal.ChronoUnit.DAYS.between(dataInizio, dataFine) > 30) {
            errori.add("La prenotazione non può superare i 30 giorni");
        }

        if (!verificaDisponibilita(idCamera, dataInizio, dataFine)) {
            errori.add("La camera non è disponibile nelle date richieste");
        }

        return errori;
    }

    @Transactional
    public Optional<Prenotazione> aggiornaStato(Integer id, boolean nuovoStato) {
        Optional<Prenotazione> prenotazioneOpt = prenotazioneRepository.findById(id);
        
        if (prenotazioneOpt.isPresent()) {
            Prenotazione prenotazione = prenotazioneOpt.get();
            prenotazione.setStatoPrenotazione(nuovoStato);
            return Optional.of(prenotazioneRepository.save(prenotazione));
        }
        
        return Optional.empty();
    }
}
