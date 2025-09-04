package com.hotel.crock_crest.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inventario_camere")
public class InventarioCamera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Long idInventario;

    @ManyToOne
    @JoinColumn(name = "id_camera")
    private Camera camera;

    @ManyToOne
    @JoinColumn(name = "id_opzione")
    private OpzionePersonalizzazione opzione;

    // Constructors

    public InventarioCamera() {
    }

    public InventarioCamera(Camera camera, Long idInventario, OpzionePersonalizzazione opzione) {
        this.camera = camera;
        this.idInventario = idInventario;
        this.opzione = opzione;
    }

    // Getters & Setters

    public Long getIdInventario() {
        return idInventario;
    }

    public Camera getCamera() {
        return camera;
    }

    public OpzionePersonalizzazione getOpzione() {
        return opzione;
    }

    public void setIdInventario(Long idInventario) {
        this.idInventario = idInventario;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setOpzione(OpzionePersonalizzazione opzione) {
        this.opzione = opzione;
    }



}
