package com.example.ecommercebackend.Controller;

import com.example.ecommercebackend.Entity.Ordine;
import com.example.ecommercebackend.Entity.OrdineMaglia;
import com.example.ecommercebackend.Entity.Pagamento;
import com.example.ecommercebackend.Entity.User;
import com.example.ecommercebackend.Service.AccountingService;
import com.example.ecommercebackend.Service.OrdineService;
import com.example.ecommercebackend.Service.PagamentoService;
import com.example.ecommercebackend.Support.DTO.PagamentoDTO;
import com.example.ecommercebackend.Support.Exception.MailUserNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private PagamentoService pagamentoService;
    @Autowired
    private AccountingService accountingService;
    @Autowired
    private OrdineService ordineService;

    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestBody List<OrdineMaglia> o, @AuthenticationPrincipal Jwt jwt){
        String email = jwt.getClaimAsString("preferred_username");
        User u = accountingService.getUserByMail(email);
        Ordine ord = ordineService.getByID(u.getCarrello());
        System.out.println(ord);
        ordineService.popolaOrdine(o, ord);
        Pagamento pagamento = ord.getPagamento();
        if(accountingService.getUserByMail(email) == null) throw new MailUserNotExistException();
        if(pagamentoService.paga(pagamento, accountingService.getUserByMail(email)))
            return ResponseEntity.ok("Pagamento effettuato");
        return ResponseEntity.internalServerError().body("pagamento non effettuato");
    }

    @PostMapping("/annulla")
    public ResponseEntity<?> annulla(@RequestParam Integer idPagamento){
        if(pagamentoService.annullaPagamento(idPagamento))
            return ResponseEntity.ok("Pagamento annullato");
        return ResponseEntity.internalServerError().body("Qualcosa è andato storto");
    }
}