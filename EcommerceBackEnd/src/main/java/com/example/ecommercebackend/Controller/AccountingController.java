package com.example.ecommercebackend.Controller;

import com.example.ecommercebackend.Entity.Ordine;
import com.example.ecommercebackend.Entity.OrdineMaglia;
import com.example.ecommercebackend.Entity.User;
import com.example.ecommercebackend.Service.AccountingService;
import com.example.ecommercebackend.Service.KeycloakService;
import com.example.ecommercebackend.Service.OrdineService;
import com.example.ecommercebackend.Support.DTO.UserDTO;
import com.example.ecommercebackend.Support.Exception.MailUserNotExistException;
import com.example.ecommercebackend.Support.RegistrationReq;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AccountingController {

    @Autowired
    private AccountingService accountingService;
    @Autowired
    private KeycloakService keycloakService;
    @Autowired
    private OrdineService ordineService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody @Valid RegistrationReq req){
        try{
            if(req==null) return ResponseEntity.badRequest().body("richiesta nulla");
            UserDTO user = req.getUser();
            if(user == null) return ResponseEntity.badRequest().body("User nullo");
            User newUser= keycloakService.userRegistration(user, req.getPass());
            if(newUser == null) return ResponseEntity.internalServerError().body("Registrazione fallita");
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);

        }catch(RuntimeException e){return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante la registrazione dell'utente. "+e.getMessage());}
    }

    @PutMapping("/update")
    public  ResponseEntity<?> updateUser(
            @RequestBody @Valid UserDTO user,
            @AuthenticationPrincipal Jwt jwt) {

        try {
            String currentEmail = jwt.getClaimAsString("preferred_username");
            boolean success = accountingService.modifyUser(currentEmail, user );
            if (success) {
                return ResponseEntity.status(HttpStatus.OK).body("Modifica avvenuta con successo");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Impossibile effettuare la modifica");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante la modifica. "+e.getMessage());
        }
    }

    @PostMapping("/debug")
    public ResponseEntity<?> debug(@RequestBody String rawBody) {
        System.out.println("Corpo ricevuto: " + rawBody);
        return ResponseEntity.ok("Controlla il log");
    }

    @GetMapping("/allUsers")
    public List<User> getAll(){
        return accountingService.getAll();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal Jwt jwt) {
        try {
            String currentEmail = jwt.getClaimAsString("preferred_username");
            boolean dbDeleted = accountingService.deleteUser(currentEmail);
            boolean keycloakDeleted = keycloakService.deleteUser(currentEmail);
            if (dbDeleted && keycloakDeleted) {
                return ResponseEntity.status(HttpStatus.OK).body("Utente eliminato con successo.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore nell'eliminazione dell'utente.");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante l'eliminazione: " + e.getMessage());
        }
    }

    @PostMapping("/loadCarrello")
    public List<OrdineMaglia> userCart(@AuthenticationPrincipal Jwt jwt){
        String email = jwt.getClaimAsString("preferred_username");
        User user = accountingService.getUserByMail(email);
        if(user == null) throw new MailUserNotExistException();
        if(user.getCarrello() == -1) return null;
        return new LinkedList<OrdineMaglia>(ordineService.getByID(user.getCarrello()).getOrdineMaglie());
    }

    @GetMapping("/getUser")
    public UserDTO getUser(@AuthenticationPrincipal Jwt jwt){
        User u = accountingService.getUserByMail(jwt.getClaimAsString("preferred_username"));
        UserDTO ud = new UserDTO();
        ud.setIdUser(u.getIdUser());
        ud.setTelefono(u.getTelefono());
        ud.setEmail(u.getEmail());
        ud.setCarrello(u.getCarrello());
        ud.setCap(u.getCap());
        ud.setCitta(u.getCitta());
        ud.setIndirizzo(u.getIndirizzo());
        ud.setNome(u.getNome());
        ud.setCognome(u.getCognome());
        return ud;
    }
}