package com.hotel.crock_crest.controller;

import com.hotel.crock_crest.model.Admin;
import com.hotel.crock_crest.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/admin-login")
@CrossOrigin(origins = "*")
public class AdminController {
    @Autowired
    private AdminService as;

    @GetMapping("/getAllAdmin")
    public List<Admin> getAllAdmin(){
        List<Admin> adminList = as.getAllAdmin();
        return adminList;
    }

    @PostMapping("/add-admin")
    public void addAdmin(@RequestBody Admin a){
        as.addAdmin(a);
    }


    @GetMapping("/get-admin/{id}")
    public Admin getAdminById(@PathVariable int id){
        Admin foundById = as.getAdminById(id);
        return foundById;
    }

    @GetMapping("/login")
    public ResponseEntity<Admin> login(@RequestParam String email, @RequestParam String password) {
        Admin a = as.findByEmailAndPass(email, password);
        if (a == null)
            return ResponseEntity.status(404).body(null);
        if (!password.equals(a.getPasswordAdmin()))
            return ResponseEntity.status(404).body(null);;
        String token = UUID.randomUUID().toString();
        a.setToken(token);
        return ResponseEntity.ok(a);
    }

    @PutMapping("/update")
    public void updateAdmin(@RequestBody Admin a){
       as.addAdmin(a);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteAdmin(@PathVariable int id){
        as.deleteAdmin(id);
    }
}
