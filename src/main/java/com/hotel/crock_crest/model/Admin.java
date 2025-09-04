package com.hotel.crock_crest.model;

import jakarta.persistence.*;
@Entity
@Table(name = "camere")
public class Camera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCamera;

    private String numeroStanza;
    private String tipoCamera;
    private String descrizione;

    private Double prezzoBaseNotte;
    private Integer capienzaMassima;

    private Boolean disponibile;

    @OneToMany(mappedBy = "camera", cascade = CascadeType.ALL)
    private List<InventarioCamera> inventario;

    @OneToMany(mappedBy = "camera", cascade = CascadeType.ALL)
    private List<Prenotazione> prenotazioni;
}