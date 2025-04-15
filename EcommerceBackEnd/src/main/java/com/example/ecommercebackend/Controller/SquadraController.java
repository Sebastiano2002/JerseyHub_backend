package com.example.ecommercebackend.Controller;

import com.example.ecommercebackend.Entity.Squadra;
import com.example.ecommercebackend.Service.SquadraService;
import com.example.ecommercebackend.Support.DTO.SquadraDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/squads")
public class SquadraController {

    @Autowired
    private SquadraService squadraService;

    @PostMapping("/admin/addSquad")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<?> addSquadra(@RequestBody SquadraDTO squadraDTO){
        if(squadraDTO.getNome() == null || squadraDTO.getLeagueName() == null) throw new RuntimeException("Inserire i dati");
        Squadra newSquad = squadraService.addSquad(squadraDTO);
        if(newSquad != null)
            return ResponseEntity.ok("Squadra creata correttamente");
        return ResponseEntity.internalServerError().body("Errore durante la creazione della league");
    }

    @DeleteMapping("/admin/deleteSquad")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<?> deleteSquad(@RequestBody String squadName){
        boolean deleted = squadraService.deleteSquad(squadName);
        if(deleted) return ResponseEntity.ok("Eliminazione eseguita correttamente");
        return ResponseEntity.internalServerError().body("Qualcosa è andato storto durante l'eliminazione");
    }

    @PutMapping("/admin/modifySquad")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<?> modifySquad(@RequestBody String oldSquadName, @RequestBody String newName, @RequestBody(required = false) String newImg){
        Squadra squadra = squadraService.getSquad(oldSquadName);
        if(squadra != null){
            boolean success = squadraService.modifySquad(oldSquadName, newName, newImg);
            if(success) return ResponseEntity.ok("Eliminazione eseguita correttamente");
        }
        return ResponseEntity.internalServerError().body("Qualcosa è andato storto");
    }

    @GetMapping("/getAllSquads")
    public List<Squadra> getAll(){
        return squadraService.getAll();
    }

    @GetMapping("/readyToSell")
    public List<Squadra> getReadySquads(@RequestBody Integer min){
        return squadraService.okToSell(min);
    }

    @GetMapping("/mpSquad")
    public Squadra getMPsquad(){
        return squadraService.mostPopularSquad();
    }

    @PostMapping("/debug")
    public ResponseEntity<?> debug(@RequestBody String rawBody) {
        System.out.println("Corpo ricevuto: " + rawBody);
        return ResponseEntity.ok("Controlla il log");
    }

}