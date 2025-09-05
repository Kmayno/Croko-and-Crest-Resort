package com.hotel.crock_crest.repository;

import com.hotel.crock_crest.model.Camera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CamereRepository extends JpaRepository<Camera, Integer> {
}
