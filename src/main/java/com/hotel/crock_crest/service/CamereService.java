package com.hotel.crock_crest.service;

import java.util.List;

import com.hotel.crock_crest.model.Camera;
import com.hotel.crock_crest.repository.CamereRepository;

import org.springframework.stereotype.Service;

@Service
public class CamereService {

    private CamereRepository cr;

    public List<Camera> getAllRooms() {
        return cr.getAllRooms();
    }

    public void addRoom(Camera c) {
        cr.save(c);
    }
}
