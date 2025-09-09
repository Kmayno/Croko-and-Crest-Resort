package com.hotel.crock_crest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventario_camere")
public class InventarioCamera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private int idInventario;

    @ManyToOne
    @JoinColumn(name = "id_camera")
    private Camera camera;

    @ManyToOne
    @JoinColumn(name = "id_opzione")
    private OpzionePersonalizzazione opzione;

    // Constructors

    public InventarioCamera() {
    }

    // Getters & Setters

    public int getIdInventario() {
        return idInventario;
    }

    public Camera getCamera() {
        return camera;
    }

    public OpzionePersonalizzazione getOpzione() {
        return opzione;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setOpzione(OpzionePersonalizzazione opzione) {
        this.opzione = opzione;
    }



}
 