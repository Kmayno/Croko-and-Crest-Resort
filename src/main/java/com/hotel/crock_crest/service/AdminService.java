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

    public Admin getAdminById(Integer id){
        Admin find = ar.findById(id).orElseThrow(() -> new RuntimeException("Admin non trovato con ID: " + id));
        return find;
    }

    public Admin findByEmailAndPass(String email, String password){
        Admin findByEmail = ar.findByEmailAndPasswordAdmin(email,password);
        return findByEmail;
    }

    public Admin updateAdmin(Admin a){
        ar.save(a);
        return a;
    }
}
