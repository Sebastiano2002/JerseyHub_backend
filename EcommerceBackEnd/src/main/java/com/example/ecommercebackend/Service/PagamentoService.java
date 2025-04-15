package com.example.ecommercebackend.Service;

import com.example.ecommercebackend.Entity.*;
import com.example.ecommercebackend.Repository.MagliaRepository;
import com.example.ecommercebackend.Repository.OrdineRepository;
import com.example.ecommercebackend.Repository.PagamentoRepository;
import com.example.ecommercebackend.Repository.UserRepository;
import com.example.ecommercebackend.Support.DTO.PagamentoDTO;
import com.example.ecommercebackend.Support.StatoPagamento;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class PagamentoService {
    @Autowired
    private PagamentoRepository pagamentoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrdineRepository ordineRepository;
    @Autowired
    private MagliaRepository magliaRepository;


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Pagamento addPagamento(Ordine ordine) throws RuntimeException {
        if(ordine == null) throw new RuntimeException("Il pagamento deve essere associato ad un ordine");
        Pagamento pagamento = new Pagamento();
        pagamento.setOrdine(ordine);
        pagamento.setStato(StatoPagamento.IN_ATTESA);
        pagamentoRepository.save(pagamento);
        
        ordine.setPagamento(pagamento);
        ordineRepository.save(ordine);
        return pagamento;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean paga(Pagamento pagamento, User user) throws RuntimeException {

        for(OrdineMaglia om : pagamento.getOrdine().getOrdineMaglie()) {
            Maglia maglia = om.getMaglia();
            if(maglia.getQuantitaDisp() < om.getRichiesta()) throw new RuntimeException("La maglia selezionata non è più disponibile nella quantità selezionata");
            maglia.setQuantitaDisp(maglia.getQuantitaDisp()-om.getRichiesta());
            magliaRepository.save(maglia);
        }

        ordineRepository.save(pagamento.getOrdine());
        user.setCarrello(-1);
        pagamento.setStato(StatoPagamento.COMPLETATO);
        pagamentoRepository.save(pagamento);
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean annullaPagamento(Integer idPagamento) throws RuntimeException {
        Pagamento pagamento = pagamentoRepository.findByIdPagamento(idPagamento);
        if (pagamento.getStato() == StatoPagamento.ANNULLATO) {
            throw new RuntimeException("Il pagamento è già stato annullato");
        }

        if (pagamento.getStato() == StatoPagamento.COMPLETATO) {
            throw new RuntimeException("Il pagamento è già stato completato e non può essere annullato");
        }

        pagamento.setStato(StatoPagamento.ANNULLATO);
        pagamentoRepository.save(pagamento);

        Ordine ord = pagamento.getOrdine();
        Pagamento newPayment = addPagamento(ord);
        ord.setPagamento(newPayment);
        ordineRepository.save(ord);
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean annullaPagamentoForDeletedOrder(Integer idPagamento) throws RuntimeException {
        Pagamento pagamento = pagamentoRepository.findByIdPagamento(idPagamento);

        pagamento.setStato(StatoPagamento.ANNULLATO);
        pagamentoRepository.save(pagamento);

        return true;
    }

    public Pagamento getById(Integer id){ return pagamentoRepository.findByIdPagamento(id);}

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Pagamento modificaPagamento(Integer id, String metodo, String numeroCarta) {
        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Pagamento non trovato"));
        if(pagamento.getStato() != StatoPagamento.IN_ATTESA) throw new RuntimeException("Puoi modificare solo pagamenti ancora incompleti");

        pagamento.setMetodoPag(metodo);
        pagamento.setNumCarta(numeroCarta);

        return pagamentoRepository.save(pagamento);
    }

    @Transactional(readOnly = true)
    public List<Pagamento> findPagamentoByUser(User user) throws RuntimeException {
        if ( !userRepository.existsById(user.getIdUser()) ) {
            throw new RuntimeException("User non trovato");
        }
        return pagamentoRepository.findByOrdineUtenteEmail(user.getEmail());
    }

    @Transactional(readOnly = true)
    public List<Pagamento> findPaymentBetweenDatesByUser(User user, Date startDate, Date endDate) throws RuntimeException{
        if ( !userRepository.existsById(user.getIdUser()) ) {
            throw new RuntimeException("User non trovato");
        }
        if ( startDate.compareTo(endDate) >= 0 ) {
            throw new RuntimeException("Non puoi viaggiare nel tempo");
        }
        return pagamentoRepository.findByOrdineUtenteIdUserAndDataBetween(user.getIdUser(), startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<Pagamento> getAllPayments() {
        return pagamentoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pagamento getByOrdine(Ordine ordine) throws RuntimeException{
        if(!ordineRepository.existsById(ordine.getIdOrdine())) throw new RuntimeException("Ordine inesistente");
        return pagamentoRepository.findByOrdine(ordine);
    }

    @Transactional(readOnly = true)
    public List<Pagamento> getByEmailAndStato(String email, StatoPagamento statoPagamento) throws RuntimeException{
        if(userRepository.findByEmail(email) == null) throw new RuntimeException("User inesistente");
        return pagamentoRepository.findByOrdineUtenteEmailAndStato(email, statoPagamento);
    }

}
