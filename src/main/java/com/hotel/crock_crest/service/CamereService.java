package com.hotel.crock_crest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.hotel.crock_crest.model.Camera;
import com.hotel.crock_crest.repository.CamereRepository;

@Service
public class CamereService {

    @Autowired
    private CamereRepository cr;

    //METODO DI RICHIESTA PER TUTTE LE STANZE NEL NOSTRO DB
    public List<Camera> getAllRooms() {
        List<Camera> findAllRooms = cr.findAll();
        return findAllRooms;
    }

    //METODO PER AGGIUNGERE UNA STANZA AL DB
    public void addRoom(Camera c) {
        cr.save(c);
    }

    //METODO DI RICHIESTA DI UNA STANZA PER ID
    public Camera findById(int id_camera){
    return cr.findById(id_camera).orElse(null);
    }
    
    //METODO DI MODIFICA DI UNA STANZA PER ID
    public String updateRoom(@PathVariable Integer idCamera, Camera updatedRoom) {
        return cr.findById(idCamera).map(r -> {
            r.setNumeroStanza(updatedRoom.getNumeroStanza());
            r.setTipoCamera(updatedRoom.getNumeroStanza());
            r.setDescrizione(updatedRoom.getDescrizione());
            r.setPrezzoBaseNotte(updatedRoom.getPrezzoBaseNotte());
            r.setCapienzaMassima(updatedRoom.getCapienzaMassima());
            r.setDisponibile(updatedRoom.getDisponibile());
            cr.save(r);
            return "Room updated";
        }).orElse("Room not updated.");
    }

    //METODO DI ELIMINAZIONE DI UNA STANZA PER ID
    public void deleteRoom (@PathVariable Integer idCamera) {
        cr.deleteById(idCamera);
    }

}
