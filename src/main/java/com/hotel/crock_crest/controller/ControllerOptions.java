package com.hotel.crock_crest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.crock_crest.model.OpzionePersonalizzazione;
import com.hotel.crock_crest.service.OptionService;


@RestController
@RequestMapping("/api/options")
public class ControllerOptions {

    @Autowired
    private OptionService os;

    @PostMapping("/addOption")
    public void addOption(@RequestBody OpzionePersonalizzazione option) {
        os.addOption(option);
    }

    @GetMapping("/getAllOptions")
    public List<OpzionePersonalizzazione> getAllOptions() {
        List<OpzionePersonalizzazione> allOptions = os.getAllOptions();
        return allOptions;
    }
    

}
