package com.hotel.crock_crest.controller;

import java.util.List;

import com.hotel.crock_crest.model.Camera;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.crock_crest.service.CamereService;


@RestController
@RequestMapping("api/camere")
public class ControllerCamere {

    @Autowired
    private CamereService cs;


//metodo per ottenere la lista di tutte 
    @GetMapping("/getAllRooms")
    public String getAllRooms() {
        List<Camera> roomList = cs.getAllRooms;
        return roomList();
    }
    

}
