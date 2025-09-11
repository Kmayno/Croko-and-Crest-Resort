package com.hotel.crock_crest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hotel.crock_crest.model.OpzionePersonalizzazione;
import com.hotel.crock_crest.service.OptionService;


@RestController
@RequestMapping("/api/options")
@CrossOrigin(origins = "*")
public class ControllerOptions {

    @Autowired
    private OptionService os;

    //METODO DI AGGIUNTA DI UNA NUOVA OPZIONE
    @PostMapping("/addOption")
    public void addOption(@RequestBody OpzionePersonalizzazione option) {
        os.addOption(option);
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //endpoint per delete option by id
    @DeleteMapping("/delete/{id}")
    public void deleteById(@PathVariable Integer id){
        os.deleteOption(id);
    }
    @PutMapping("/update")
    public ResponseEntity<OpzionePersonalizzazione> updateById(@RequestBody OpzionePersonalizzazione op){
        OpzionePersonalizzazione updated = os.updateOption(op);
        return ResponseEntity.ok(updated);  // ritorna l'opzione aggiornata
    }

    //METODO DI RICHIESTA PER TUTTE LE OPZIONI
    @GetMapping("/getAllOptions")
    public List<OpzionePersonalizzazione> getAllOptions() {
        List<OpzionePersonalizzazione> allOptions = os.getAllOptions();
        return allOptions;
    }
}
