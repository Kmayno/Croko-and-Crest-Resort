package com.hotel.crock_crest.service;

import com.hotel.crock_crest.model.Admin;
import com.hotel.crock_crest.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private AdminRepository ar;


    public List<Admin> getAllAdmin(){
        return ar.findAll();
    }

    public void addAdmin(Admin a){
        ar.save(a);
    }
}
