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

    //METODO PER AGGIUNGERE UN OPZIONE AL DB
    public void addOption(OpzionePersonalizzazione op){
        or.save(op);
    }

    public OpzionePersonalizzazione updateOption(OpzionePersonalizzazione op){
        OpzionePersonalizzazione ex = or.findById(op.getIdOpzione())
                .orElseThrow(() -> new RuntimeException("Opzione non trovata."));
        ex.setNomeOpzione(op.getNomeOpzione());
        ex.setPrezzoAggiuntivo(op.getPrezzoAggiuntivo());
        return or.save(ex);  // ritorna l'entità salvata
    }
    //metodo per eliminare
    public void deleteOption(Integer id){
        or.deleteById(id);
    }


}
