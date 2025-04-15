package com.example.ecommercebackend.Controller;

import com.example.ecommercebackend.Entity.League;
import com.example.ecommercebackend.Entity.Squadra;
import com.example.ecommercebackend.Service.LeagueService;
import com.example.ecommercebackend.Support.DTO.LeagueDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/league")
public class LeagueController {

    @Autowired
    private LeagueService leagueService;

    @PostMapping("/admin/addLeague")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<?> addLeague(@RequestBody LeagueDTO league){
        System.out.println(league);
        League newLeague = leagueService.addLeague(league);
        if(newLeague != null)
            return ResponseEntity.ok("League creata correttamente");
        return ResponseEntity.internalServerError().body("Errore durante la creazione della league");
    }

    @DeleteMapping("/admin/deleteLeague")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<?> deleteLeague(@RequestBody String leagueName){
        boolean deleted = leagueService.removeLeague(leagueName);
        if(deleted) return ResponseEntity.ok("Eliminazione eseguita correttamente");
        return ResponseEntity.internalServerError().body("Qualcosa è andato storto");
    }

    @GetMapping("/getAllLeague")
    public List<League> getLeague(){
        return leagueService.getAllLeagues();
    }

    @GetMapping("/searchLeague")
    public League searchLeague(@RequestBody String leagueName){
        return leagueService.getLeague(leagueName);
    }

    @GetMapping("/squads")
    public Collection<Squadra> getAllSquads(@RequestBody String leagueName){
        return leagueService.getAllSquads(leagueName);
    }

    @PostMapping("/debug")
    public ResponseEntity<?> debug(@RequestBody String rawBody) {
        System.out.println("Corpo ricevuto: " + rawBody);
        return ResponseEntity.ok("Controlla il log");
    }

}
