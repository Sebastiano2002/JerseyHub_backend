package com.example.ecommercebackend.Service;

import com.example.ecommercebackend.Entity.Maglia;
import com.example.ecommercebackend.Entity.Ordine;
import com.example.ecommercebackend.Entity.Squadra;
import com.example.ecommercebackend.Repository.MagliaRepository;
import com.example.ecommercebackend.Repository.OrdineRepository;
import com.example.ecommercebackend.Repository.SquadraRepository;
import com.example.ecommercebackend.Support.DTO.MagliaDTO;
import com.example.ecommercebackend.Support.Exception.ShirtAlreadyExistException;
import com.example.ecommercebackend.Support.Exception.ShirtNotExistException;
import com.example.ecommercebackend.Support.Exception.SquadNotExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
public class MagliaService {

    @Autowired
    private MagliaRepository magliaRepository;
    @Autowired
    private SquadraRepository squadraRepository;
    @Autowired
    private OrdineRepository ordineRepository;
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Maglia createMaglia(MagliaDTO m){
        if (squadraRepository.findBySquadra(m.getSquadra()) == null) throw new SquadNotExistException();
        Squadra squad=squadraRepository.findBySquadra(m.getSquadra());

        Maglia newMaglia = new Maglia();
        newMaglia.setNome(m.getNomeMaglia());
        newMaglia.setQuantitaDisp(m.getQuantitaDisp());
        newMaglia.setImg(m.getImg());
        newMaglia.setPrezzo(m.getPrezzo());
        if(squad.getMaglie().contains(magliaRepository.findByNome(m.getNomeMaglia()))) throw new ShirtAlreadyExistException();
        newMaglia.setSquadra(squad);

        squad.getMaglie().add(newMaglia);
        return magliaRepository.save(newMaglia);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean deleteMaglia(Maglia m){
        if(magliaRepository.findByNome(m.getNome())==null) throw new ShirtAlreadyExistException();
        Squadra squad=squadraRepository.findBySquadra(m.getSquadra().getSquadra());
        squad.getMaglie().remove(m);
        squadraRepository.save(squad);
        magliaRepository.delete(m);
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean modifyPrice(Maglia maglia, double newPrice) {
        if (maglia == null) {
            throw new ShirtNotExistException();
        }
        maglia.setPrezzo(newPrice);
        magliaRepository.save(maglia);

        maglia.getItems().forEach(ordineMaglia -> {
            Ordine ordine = ordineMaglia.getOrdine();
            double prezzoTotale = ordine.getOrdineMaglie().stream()
                    .mapToDouble(om -> om.getMaglia().getPrezzo() * om.getRichiesta())
                    .sum();
            ordine.setPrezzoTot(prezzoTotale);
            ordineRepository.save(ordine);
        });
        return true;
    }
    @Transactional(readOnly = true)
    public Maglia getMagliaByNome(String nome){
        if(magliaRepository.findByNome(nome)==null) throw new ShirtNotExistException();
        return magliaRepository.findByNome(nome);
    }

    @Transactional(readOnly = true)
    public List<MagliaDTO> maglieDisponibili(Integer quantita){
        List<Maglia> maglie = magliaRepository.disponibili(quantita);
        List<MagliaDTO> maglieDTO = new LinkedList<>();
        for(Maglia m : maglie){
            MagliaDTO mdto= new MagliaDTO();
            mdto.setIdMaglia(m.getIdMaglia());
            mdto.setNomeMaglia(m.getNome());
            mdto.setPrezzo(m.getPrezzo());
            mdto.setImg(m.getImg());
            mdto.setSquadra(m.getSquadra().getSquadra());
            mdto.setQuantitaDisp(m.getQuantitaDisp());
            maglieDTO.add(mdto);
        }
        return maglieDTO;
    }

    @Transactional(readOnly = true)
    public Maglia magliaTopSell(){
        return magliaRepository.findTopMagliaXvendite();
    }

}