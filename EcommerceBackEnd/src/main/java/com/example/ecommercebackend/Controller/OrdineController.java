package com.example.ecommercebackend.Controller;

import com.example.ecommercebackend.Entity.Ordine;
import com.example.ecommercebackend.Entity.OrdineMaglia;
import com.example.ecommercebackend.Entity.User;
import com.example.ecommercebackend.Service.AccountingService;
import com.example.ecommercebackend.Service.OrdineMagliaService;
import com.example.ecommercebackend.Service.OrdineService;
import com.example.ecommercebackend.Service.PagamentoService;
import com.example.ecommercebackend.Support.DTO.OrdineDTO;
import com.example.ecommercebackend.Support.DTO.OrdineMagliaDTO;
import com.example.ecommercebackend.Support.Exception.MailUserNotExistException;
import com.example.ecommercebackend.Support.StatoPagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class OrdineController {

    @Autowired
    private OrdineService ordineService;
    @Autowired
    private OrdineMagliaService ordineMagliaService;
    @Autowired
    private AccountingService accountingService;
    @Autowired
    private PagamentoService pagamentoService;

    @PostMapping("/addOrdine")
    public OrdineDTO addOrdine(@RequestBody(required = false) List<OrdineMaglia> ord, @AuthenticationPrincipal Jwt jwt){
        String email = jwt.getClaimAsString("preferred_username");
        User u= accountingService.getUserByMail(email);
        System.out.println("utente:"+u+" ordine:"+ord);
        if(u == null) throw new MailUserNotExistException();
        if(u.getCarrello() != -1) throw new RuntimeException("Carrello già esistente");
        Ordine ordine = ordineService.createOrdine(ord, u);
        if(ordine != null) {
            pagamentoService.addPagamento(ordine);
            OrdineDTO odto = new OrdineDTO();
            odto.setIdOrdine(ordine.getIdOrdine());
            odto.setUtente(ordine.getUtente().getEmail());
            odto.setPrezzoTot(ordine.getPrezzoTot());
            odto.setItems(new ArrayList<>(ordine.getOrdineMaglie()));
            odto.setPagamento(ordine.getPagamento().getIdPagamento());
            return odto;
        }
        return null;
    }

    @PostMapping("/addItem")
    public ResponseEntity<?> addItem(@RequestBody OrdineMagliaDTO ordMaglia, @AuthenticationPrincipal Jwt jwt){
        if(ordineService.getByID(ordMaglia.getIdOrdine()).getStatoPagamento().equals(StatoPagamento.COMPLETATO) || ordineService.getByID(ordMaglia.getIdOrdine()).getStatoPagamento().equals(StatoPagamento.ANNULLATO))
            throw new RuntimeException("Non è possibile modificare un pagamento non modificabile");
        String email = jwt.getClaimAsString("preferred_username");
        User user= accountingService.getUserByMail(email);
        if(user == null) throw new MailUserNotExistException();

        OrdineMaglia om = ordineMagliaService.aggiungiOrdineMaglia(ordMaglia);

        if(om != null)
            return ResponseEntity.ok("Aggiunta andata a buon fine");
        return ResponseEntity.internalServerError().body("Qualcosa è andato storto");
    }


    @DeleteMapping("/deleteOrder")
    public ResponseEntity<?> deleteOrder(@RequestParam(name = "idOrd")  Integer idOrd, @AuthenticationPrincipal Jwt jwt){
        User user = accountingService.getUserByMail(jwt.getClaimAsString("preferred_username"));
        Ordine ord = ordineService.getByID(idOrd);
        if(ord == null) throw new RuntimeException("Ordine inesistente");
        if(ord.getStatoPagamento().equals(StatoPagamento.COMPLETATO) || ord.getStatoPagamento().equals(StatoPagamento.ANNULLATO))
            throw new RuntimeException("Non è possibile modificare un pagamento non modificabile");
        if(user != null){
            if(pagamentoService.annullaPagamentoForDeletedOrder(ord.getPagamento().getIdPagamento()))
                ordineService.deleteOrdine(ord, user);
            else throw new RuntimeException("Impossibile annullare il pagamento");

            return ResponseEntity.ok("Eliminazione effettuata con successo");
        }
        return ResponseEntity.internalServerError().body("Eliminazione non effettuata");
    }

    @DeleteMapping("/deleteItem")
    public ResponseEntity<?> deleteItem(@RequestBody OrdineMagliaDTO ordineMagliaDTO){
        if(ordineService.getByID(ordineMagliaDTO.getIdOrdine()).getStatoPagamento().equals(StatoPagamento.COMPLETATO) || ordineService.getByID(ordineMagliaDTO.getIdOrdine()).getStatoPagamento().equals(StatoPagamento.ANNULLATO))
            throw new RuntimeException("Non è possibile modificare un pagamento non modificabile");
        if(ordineMagliaService.rimuoviOrdineMaglia(ordineMagliaDTO.getIdOrdine(), ordineMagliaDTO.getIdMaglia()))
            return ResponseEntity.ok("Eliminazione effettuata con successo");
        return ResponseEntity.internalServerError().body("Qualcosa è andato storto");
    }

}
