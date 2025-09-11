package com.hotel.crock_crest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hotel.crock_crest.model.Camera;
import com.hotel.crock_crest.service.CamereService;

@RestController
@RequestMapping("api/camere")
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
        return allRooms ;
    }

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    
//METODO DI RICHIESTA PER UNA SINGOLA STANZA TRAMITE ID

@GetMapping("/{id}")
    public Camera getById(@PathVariable int idCamera) {
        Camera getRoomById = cs.findById(idCamera);
        return getRoomById;
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
public void deleteRoom (@PathVariable Integer idCamera) {
    cs.deleteRoom(idCamera);
}

}
