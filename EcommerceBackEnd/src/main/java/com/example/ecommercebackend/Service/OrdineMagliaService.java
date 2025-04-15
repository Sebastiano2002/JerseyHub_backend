package com.example.ecommercebackend.Service;

import com.example.ecommercebackend.Entity.Maglia;
import com.example.ecommercebackend.Entity.Ordine;
import com.example.ecommercebackend.Entity.OrdineMaglia;
import com.example.ecommercebackend.Repository.MagliaRepository;
import com.example.ecommercebackend.Repository.OrdineMagliaRepository;
import com.example.ecommercebackend.Repository.OrdineRepository;
import com.example.ecommercebackend.Support.DTO.OrdineMagliaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class OrdineMagliaService {

    @Autowired
    private OrdineMagliaRepository ordineMagliaRepository;
    @Autowired
    private MagliaRepository magliaRepository;
    @Autowired
    private OrdineRepository ordineRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public OrdineMaglia aggiungiOrdineMaglia(OrdineMagliaDTO ordineMaglia) {
        Integer magliaId = ordineMaglia.getIdMaglia();
        Integer ordineId = ordineMaglia.getIdOrdine();

        Maglia maglia = magliaRepository.findById(magliaId)
                .orElseThrow(() -> new IllegalArgumentException("Maglia non trovata"));
        Ordine ordine = ordineRepository.findById(ordineId)
                .orElseThrow(() -> new IllegalArgumentException("Ordine non trovato"));

        if (maglia.getQuantitaDisp() < ordineMaglia.getRichiesta()) {
            throw new IllegalArgumentException("Quantità richiesta superiore alla disponibilità");
        }

        if(!ordine.getIdOrdine().equals(ordine.getUtente().getCarrello())) throw new RuntimeException("Puoi aggiungere oggetti solo al carrello attivo");

        OrdineMaglia existingOrdineMaglia = ordine.getOrdineMaglie().stream()
                .filter(om -> om.getMaglia().equals(maglia))
                .findFirst()
                .orElse(null);

        if(existingOrdineMaglia == null){

            OrdineMaglia ordM = new OrdineMaglia();
            ordM.setOrdine(ordine);
            ordM.setMaglia(maglia);
            ordM.setRichiesta(ordineMaglia.getRichiesta());
            ordM.setTaglia(ordineMaglia.getTaglia());
            ordM.setGiocatore(ordineMaglia.getGiocatore());
            ordM.setNumero(ordineMaglia.getNumero());

            ordineMagliaRepository.save(ordM);
            ordine.getOrdineMaglie().add(ordM);
            ordineRepository.save(ordine);

            ordine.setPrezzoTot(ordine.getOrdineMaglie().stream()
                    .mapToDouble(om -> om.getMaglia().getPrezzo() * om.getRichiesta())
                    .sum());

            ordineRepository.save(ordine);


            return ordM;
        }else{
            int newQT = existingOrdineMaglia.getRichiesta() + ordineMaglia.getRichiesta();
            modificaQuantitaMaglia(ordine, maglia, newQT);
            return existingOrdineMaglia;
        }
    }

    private void modificaQuantitaMaglia(Ordine ordine, Maglia maglia, int nuovaQuantita) {
        if (nuovaQuantita < 0) {
            throw new IllegalArgumentException("La quantità deve essere positiva");
        }

        OrdineMaglia ordineMaglia = ordine.getOrdineMaglie().stream()
                .filter(om -> om.getMaglia().equals(maglia))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("OrdineMaglia non trovato"));

        if (maglia.getQuantitaDisp() < nuovaQuantita) {
            throw new IllegalArgumentException("Quantità richiesta superiore alla disponibilità");
        }

        if (nuovaQuantita == 0) {
            ordine.getOrdineMaglie().remove(ordineMaglia);
            ordineMagliaRepository.delete(ordineMaglia);
        } else {
            ordineMaglia.setRichiesta(nuovaQuantita);
            ordineMagliaRepository.save(ordineMaglia);
        }

        ordine.setPrezzoTot(ordine.getOrdineMaglie().stream()
                .mapToDouble(om -> om.getMaglia().getPrezzo() * om.getRichiesta())
                .sum());

        ordineRepository.save(ordine);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean rimuoviOrdineMaglia(Integer ordineId, Integer magliaId) {
        if(ordineMagliaRepository.findByMagliaIdMagliaAndOrdineIdOrdine(magliaId, ordineId) == null) throw new RuntimeException("La maglia o l'ordine inseriti sono inesistenti");
        OrdineMaglia ordineMaglia = ordineMagliaRepository.findByMagliaIdMagliaAndOrdineIdOrdine(magliaId, ordineId);

        Ordine ordine = ordineRepository.findByIdOrdine(ordineMaglia.getOrdine().getIdOrdine());

        if(!ordine.getIdOrdine().equals(ordine.getUtente().getCarrello())) throw new RuntimeException("Puoi rimuovere oggetti solo dal carrello attivo");

        ordine.getOrdineMaglie().remove(ordineMaglia);
        ordineRepository.save(ordine);
        ordineMagliaRepository.delete(ordineMaglia);

        ordine.setPrezzoTot(ordine.getOrdineMaglie().stream()
                .mapToDouble(om -> om.getMaglia().getPrezzo() * om.getRichiesta())
                .sum());

        ordineRepository.save(ordine);

        return true;
    }

    @Transactional(readOnly = true)
    public Collection<OrdineMaglia> findAllInAOrder(Ordine ordine){
        if(ordineRepository.findByIdOrdine(ordine.getIdOrdine())==null) throw new RuntimeException("Ordine inesistente");
        return ordineMagliaRepository.findByOrdineIdOrdine(ordine.getIdOrdine());
    }

}

