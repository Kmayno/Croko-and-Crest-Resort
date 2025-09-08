package com.hotel.crock_crest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.crock_crest.model.OpzionePersonalizzazione;
import com.hotel.crock_crest.repository.OptionsRepository;

@Service
public class OptionService {

    @Autowired
    private OptionsRepository or;
    
    public List<OpzionePersonalizzazione> getAllOptions() {
        List<OpzionePersonalizzazione> findAllOptions = or.findAll();
        return findAllOptions;
    }

    public void addOption(OpzionePersonalizzazione op){
        or.save(op);
    }



}
