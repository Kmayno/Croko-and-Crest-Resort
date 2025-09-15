package com.hotel.crock_crest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.crock_crest.model.Camera;
import com.hotel.crock_crest.service.CamereService;

@RestController
@RequestMapping("/api/camere")
@CrossOrigin(origins = "*")
public class ControllerCamere {

    @Autowired
    private CamereService cs;

    //METODO DI AGGIUNTA DI UNA NUOVA STANZA
    @PostMapping("/addRoom")
    public void addRoom(@RequestBody Camera camera) {
        cs.addRoom(camera);
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
//METODO DI RICHIESTA PER TUTTE LE STANZE
    
    @GetMapping("/getAllRooms")
    public List<Camera> getAllRooms() {
        List<Camera> allRooms = cs.getAllRooms();
        return allRooms;
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
//METODO DI RICHIESTA PER UNA SINGOLA STANZA TRAMITE ID

@GetMapping("/{id}")
    public Camera getById(@PathVariable int id) {
        Camera getRoomById = cs.findById(id);
        return getRoomById;
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

//METODO DI RICHIESTA STANZA PER TIPO

@GetMapping("/getCamera/{tipo}")
public List<Camera> getByType(@PathVariable String tipo) {
    return cs.getByType(tipo);
}

 // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

//METODO DI MODIFICA PER UNA STANZA TRAMITE ID
@PutMapping("/{id}")
public String updateCamera(@PathVariable Integer id, @RequestBody Camera updatedCamera) {
    return cs.updateRoom(id, updatedCamera);
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
//METODO DI ELIMINAZIONE DI UNA STANZA TRAMITE ID
@DeleteMapping("/{id}")
public void deleteRoom (@PathVariable Integer id) {
    cs.deleteRoom(id);
}

}
