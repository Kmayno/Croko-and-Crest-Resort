package com.hotel.crock_crest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.crock_crest.model.Camera;

@Repository
public interface CamereRepository extends JpaRepository<Camera, Integer> {

    List<Camera> findByTipoCamera(String tipo);
    
}
