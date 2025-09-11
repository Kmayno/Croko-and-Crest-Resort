package com.hotel.crock_crest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    //METODO DI RICHIESTA PER TUTTE LE OPZIONI
    @GetMapping("/getAllOptions")
    public List<OpzionePersonalizzazione> getAllOptions() {
        List<OpzionePersonalizzazione> allOptions = os.getAllOptions();
        return allOptions;
    }
}
