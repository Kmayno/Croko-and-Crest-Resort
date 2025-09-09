package com.hotel.crock_crest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.crock_crest.model.OpzionePersonalizzazione;

@Repository
public interface OptionsRepository extends JpaRepository<OpzionePersonalizzazione,Integer>{
    }

