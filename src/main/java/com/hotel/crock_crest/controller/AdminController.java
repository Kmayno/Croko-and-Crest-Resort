package com.hotel.crock_crest.controller;

import com.hotel.crock_crest.model.Admin;
import com.hotel.crock_crest.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class AdminController {
    @Autowired
    private AdminService as;

    @GetMapping("/getALlAdmin")
    public List<Admin> getAllAdmin(){
        List<Admin> adminList = as.getAllAdmin();
        return adminList;
    }

    @PostMapping("/add-admin")
    public void addAdmin(@RequestBody Admin a){
        as.addAdmin(a);
    }
}
