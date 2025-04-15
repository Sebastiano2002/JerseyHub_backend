package com.example.ecommercebackend.Service;

import com.example.ecommercebackend.Entity.Maglia;
import com.example.ecommercebackend.Entity.Ordine;
import com.example.ecommercebackend.Entity.OrdineMaglia;
import com.example.ecommercebackend.Entity.User;
import com.example.ecommercebackend.Repository.OrdineMagliaRepository;
import com.example.ecommercebackend.Repository.OrdineRepository;
import com.example.ecommercebackend.Repository.UserRepository;
import com.example.ecommercebackend.Support.DTO.OrdineDTO;
import com.example.ecommercebackend.Support.DTO.OrdineMagliaDTO;
import com.example.ecommercebackend.Support.Exception.MailUserNotExistException;
import com.example.ecommercebackend.Support.StatoPagamento;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.lang.Integer.sum;

@Service
public class OrdineService {

    @Autowired
    private OrdineRepository ordineRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrdineMagliaService ordineMagliaService;
    @Autowired
    private OrdineMagliaRepository ordineMagliaRepository;
    @Autowired
    private MagliaService magliaService;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Ordine createOrdine(List<OrdineMaglia> ordine, User user){
        if(user.getCarrello() != -1) throw new RuntimeException("Completa l'ordine precedente prima di crearne uno nuovo");

        Ordine o = new Ordine();
        o.setUtente(user);
        if(ordine == null) {
            o.setOrdineMaglie(new HashSet<OrdineMaglia>());
            o.setPrezzoTot(0.00);
        }
        else {
            for(OrdineMaglia om: ordine){
                if(om.getMaglia().getPrezzo() != magliaService.getMagliaByNome(om.getMaglia().getNome()).getPrezzo())
                    throw new RuntimeException("Non cercare di fregarmi");
            }
            o.setOrdineMaglie(new HashSet<OrdineMaglia>(ordine));
            o.setPrezzoTot(ordine.stream()
                .mapToDouble(om -> om.getMaglia().getPrezzo() * om.getRichiesta())
                .sum());
        }

        Ordine savedOrd = ordineRepository.save(o);

        user.getOrders().add(o);
        user.setCarrello(o.getIdOrdine());
        userRepository.save(user);

        return savedOrd;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteOrdine(Ordine ordine, User user){
        if (ordineRepository.findByIdOrdine(ordine.getIdOrdine()) == null) throw new RuntimeException("L'ordine non esiste");
        if (userRepository.findByEmail(user.getEmail()) == null) throw new MailUserNotExistException();
        if(!ordine.getStatoPagamento().equals(StatoPagamento.IN_ATTESA)) throw new RuntimeException("Non puoi eliminare un ordine completato");
        user.getOrders().remove(ordine);
        user.setCarrello(-1);
        userRepository.save(user);
        ordineRepository.delete(ordine);
    }

    @Transactional(readOnly = true)
    public Collection<Ordine> getAllByUser(User user){
        if (userRepository.findByEmail(user.getEmail())==null) throw new MailUserNotExistException();
        return ordineRepository.findByUtente(user);
    }

    @Transactional(readOnly = true)
    public Collection<Ordine> getAllByUserEmail(String email){
        if (userRepository.findByEmail(email)==null) throw new MailUserNotExistException();
        return ordineRepository.findByUtenteEmail(email);
    }
    @Transactional(readOnly = true)
    public Collection<Ordine> getBetween(Date lb, Date ub){
        return ordineRepository.findByDataOrdineBetween(lb, ub);
    }

    @Transactional(readOnly = true)
    public Collection<Ordine> getByUserBetween(User user, Date lb, Date ub){
        if (userRepository.findByEmail(user.getEmail())==null) throw new MailUserNotExistException();
        return ordineRepository.findByUtenteAndDataOrdineBetween(user, lb, ub);
    }

    @Transactional(readOnly = true)
    public Collection<Ordine> getByPriceGreaterThanEqual(Double tot){
        return ordineRepository.findByPrezzoTotGreaterThanEqual(tot);
    }

    @Transactional(readOnly = true)
    public Collection<User> getByUserAndPriceGreaterThanEqual(User user, Double tot){
        if (userRepository.findByEmail(user.getEmail())==null) throw new MailUserNotExistException();
        List<Ordine> ordini= ordineRepository.findByPrezzoTotGreaterThanEqual(tot);
        List<User> users = new ArrayList<>();
        for (Ordine o : ordini)
            users.add(o.getUtente());
        return users;
    }

    @Transactional(readOnly = true)
    public Ordine getByID(Integer id){
        return ordineRepository.findByIdOrdine(id);
    }

    @Transactional(readOnly = true)
    public List<Ordine> getOrdiniCompletati(String email, StatoPagamento statoPagamento){
        if(userRepository.findByEmail(email) == null) throw new MailUserNotExistException();
        User user = userRepository.findByEmail(email);
        return ordineRepository.findByUtenteAndPagamentoStato(user, statoPagamento);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void popolaOrdine(List<OrdineMaglia> maglie, Ordine o) {
        for (OrdineMaglia om : maglie) {

            OrdineMagliaDTO ordm = new OrdineMagliaDTO();
            ordm.setRichiesta(om.getRichiesta());
            ordm.setIdOrdine(o.getIdOrdine());
            ordm.setIdMaglia(om.getMaglia().getIdMaglia());
            ordm.setTaglia(om.getTaglia());
            ordm.setGiocatore(om.getGiocatore());
            ordm.setNumero(om.getNumero());
            ordm.setSquadra(magliaService.getMagliaByNome(om.getMaglia().getNome()).getSquadra().getSquadra()
            );

            ordineMagliaService.aggiungiOrdineMaglia(ordm);
        }
        System.out.println("Ordine popolato. Totale: " + o.getPrezzoTot());
    }


}
