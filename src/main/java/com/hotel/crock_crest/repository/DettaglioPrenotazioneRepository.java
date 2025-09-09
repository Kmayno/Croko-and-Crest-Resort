package com.hotel.crock_crest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.crock_crest.model.DettaglioPrenotazione;

@Repository
public interface DettaglioPrenotazioneRepository extends JpaRepository<DettaglioPrenotazione, Integer> {

    @Transactional
    void deleteByPrenotazioneIdPrenotazione(Integer idPrenotazione);
}
