package com.example.ecommercebackend.Controller;

import com.example.ecommercebackend.Entity.Maglia;
import com.example.ecommercebackend.Service.MagliaService;
import com.example.ecommercebackend.Support.DTO.MagliaDTO;
import com.example.ecommercebackend.Support.Exception.ShirtNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/shirts")
public class MagliaController {

    @Autowired
    private MagliaService magliaService;

    @PreAuthorize("hasRole('client_admin')")
    @PostMapping("/admin/addMaglia")
    public ResponseEntity<?> addMaglia(@RequestBody MagliaDTO magliaDTO){
        Maglia newMaglia = magliaService.createMaglia(magliaDTO);
        if(newMaglia == null) return  ResponseEntity.internalServerError().body("Qualcosa è andato storto nella creazione della maglia");
        return ResponseEntity.ok("Maglia creata correttamente");
    }

    @PreAuthorize("hasRole('ec_admin')")
    @DeleteMapping("/deleteMaglia")
    public ResponseEntity<?> deleteMaglia(@RequestBody MagliaDTO magliaDTO){
        Maglia m = magliaService.getMagliaByNome(magliaDTO.getNomeMaglia());
        if(m == null) throw new ShirtNotExistException();
        boolean success = magliaService.deleteMaglia(m);
        if(success)
            return ResponseEntity.ok("Maglia eliminata correttamente");
        return ResponseEntity.internalServerError().body("Errore durante l'eliminazione");
    }

    @PreAuthorize("hasRole('ec_admin')")
    @PutMapping("/modifyPrice")
    public ResponseEntity<?> modPrice(@RequestBody MagliaDTO maglia, @RequestParam(name = "price") Double price){
        Maglia m = magliaService.getMagliaByNome(maglia.getNomeMaglia());
        if(m == null) throw new ShirtNotExistException();
        boolean mod = magliaService.modifyPrice(m, price);
        if(mod)
            return ResponseEntity.ok("Maglia modificata correttamente");
        return ResponseEntity.internalServerError().body("Errore durante la modifica");
    }

    @GetMapping("/getReadyToSell")
    public List<MagliaDTO> getReadyToSell(@RequestParam(name = "min", required = false) Integer min){
        return magliaService.maglieDisponibili(Objects.requireNonNullElse(min, 1));
    }

    @GetMapping("/mpShirt")
    public Maglia mpShirt(){
        return magliaService.magliaTopSell();
    }
}