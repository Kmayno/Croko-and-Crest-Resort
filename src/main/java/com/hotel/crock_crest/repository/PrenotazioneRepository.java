package com.hotel.crock_crest.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hotel.crock_crest.model.Prenotazione;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Integer> {

    /**
     * Trova prenotazioni di un cliente specifico
     * @param clienteId ID del cliente
     * @return Lista delle prenotazioni del cliente
     */
    List<Prenotazione> findByClienteIdCliente(Integer clienteId);

    /**
     * Trova prenotazioni per stato
     * @param statoPrenotazione true = confermate, false = in attesa
     * @return Lista delle prenotazioni con lo stato specificato
     */
    List<Prenotazione> findByStatoPrenotazione(Boolean statoPrenotazione);

    /**
     * Trova prenotazioni di un cliente con uno stato specifico
     * @param clienteId ID del cliente
     * @param statoPrenotazione Stato della prenotazione
     * @return Lista delle prenotazioni filtrate
     */
    List<Prenotazione> findByClienteIdClienteAndStatoPrenotazione(Integer clienteId, Boolean statoPrenotazione);

    /**
     * QUERY CHIAVE: Trova prenotazioni che si sovrappongono con le date richieste
     * 
     * Una prenotazione si sovrappone se:
     * - inizia prima della fine del periodo richiesto E
     * - finisce dopo l'inizio del periodo richiesto
     * 
     * @param idCamera ID della camera
     * @param dataInizio Data inizio del periodo richiesto
     * @param dataFine Data fine del periodo richiesto
     * @return Lista delle prenotazioni sovrapposte
     */
    @Query("SELECT p FROM Prenotazione p WHERE p.camera.idCamera = :idCamera " +
           "AND p.statoPrenotazione = true " +
           "AND p.dataInizio < :dataFine " +
           "AND p.dataFine > :dataInizio")
    List<Prenotazione> findPrenotazioniSovrapposte(
        @Param("idCamera") Integer idCamera,
        @Param("dataInizio") LocalDate dataInizio, 
        @Param("dataFine") LocalDate dataFine
    );

    /**
     * Verifica se un cliente ha prenotazioni attive (non scadute)
     * @param clienteId ID del cliente
     * @param oggi Data odierna
     * @return true se ha prenotazioni attive
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Prenotazione p " +
           "WHERE p.cliente.idCliente = :clienteId " +
           "AND p.dataFine >= :oggi " +
           "AND p.statoPrenotazione = true")
    boolean hasPrenotazioniAttive(@Param("clienteId") Integer clienteId, @Param("oggi") LocalDate oggi);

    /**
     * Conta le prenotazioni confermate di un cliente
     * @param clienteId ID del cliente
     * @return Numero di prenotazioni confermate
     */
    @Query("SELECT COUNT(p) FROM Prenotazione p " +
           "WHERE p.cliente.idCliente = :clienteId " +
           "AND p.statoPrenotazione = true")
    Long countPrenotazioniConfermate(@Param("clienteId") Integer clienteId);

    /**
     * Calcola il fatturato totale di una camera
     * @param idCamera ID della camera
     * @return Fatturato totale
     */
    @Query("SELECT SUM(p.prezzoTotale) FROM Prenotazione p " +
           "WHERE p.camera.idCamera = :idCamera " +
           "AND p.statoPrenotazione = true")
    Double calcolaFatturatoCamera(@Param("idCamera") Integer idCamera);

    /**
     * Trova prenotazioni che iniziano in un determinato periodo (per reminder)
     * @param dataInizio Data di inizio del periodo
     * @param dataFine Data di fine del periodo
     * @return Lista delle prenotazioni in scadenza
     */
    @Query("SELECT p FROM Prenotazione p " +
           "WHERE p.dataInizio BETWEEN :dataInizio AND :dataFine " +
           "AND p.statoPrenotazione = true")
    List<Prenotazione> findPrenotazioniInScadenza(
        @Param("dataInizio") LocalDate dataInizio, 
        @Param("dataFine") LocalDate dataFine
    );

    /**
     * Trova le ultime 10 prenotazioni per dashboard admin
     * @return Lista delle ultime prenotazioni
     */
    List<Prenotazione> findTop10ByOrderByIdPrenotazioneDesc();

    /**
     * Trova prenotazioni di una camera specifica
     * @param idCamera ID della camera
     * @return Lista delle prenotazioni della camera
     */
    List<Prenotazione> findByCameraIdCamera(Integer idCamera);

    /**
     * Trova prenotazioni in un periodo specifico
     * @param dataInizio Data di inizio
     * @param dataFine Data di fine
     * @return Lista delle prenotazioni nel periodo
     */
    @Query("SELECT p FROM Prenotazione p " +
           "WHERE p.dataInizio >= :dataInizio AND p.dataFine <= :dataFine")
    List<Prenotazione> findPrenotazioniInPeriodo(
        @Param("dataInizio") LocalDate dataInizio, 
        @Param("dataFine") LocalDate dataFine
    );

    /**
     * Trova prenotazioni scadute (per pulizia dati)
     * @param dataOggi Data odierna
     * @return Lista delle prenotazioni scadute
     */
    @Query("SELECT p FROM Prenotazione p " +
           "WHERE p.dataFine < :dataOggi")
    List<Prenotazione> findPrenotazioniScadute(@Param("dataOggi") LocalDate dataOggi);

    /**
     * Calcola il fatturato totale dell'hotel in un periodo
     * @param dataInizio Data di inizio
     * @param dataFine Data di fine
     * @return Fatturato totale
     */
    @Query("SELECT SUM(p.prezzoTotale) FROM Prenotazione p " +
           "WHERE p.dataInizio BETWEEN :dataInizio AND :dataFine " +
           "AND p.statoPrenotazione = true")
    Double calcolaFatturatoTotale(
        @Param("dataInizio") LocalDate dataInizio, 
        @Param("dataFine") LocalDate dataFine
    );

    /**
     * Trova le camere più prenotate
     * @return Lista delle prenotazioni ordinate per camera più frequente
     */
    @Query("SELECT p FROM Prenotazione p " +
           "GROUP BY p.camera.idCamera " +
           "ORDER BY COUNT(p.camera.idCamera) DESC")
    List<Prenotazione> findCamerePiuPrenotate();
}