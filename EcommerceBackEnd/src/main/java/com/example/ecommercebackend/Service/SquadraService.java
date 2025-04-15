package com.example.ecommercebackend.Service;

import com.example.ecommercebackend.Entity.League;
import com.example.ecommercebackend.Entity.Maglia;
import com.example.ecommercebackend.Entity.Squadra;
import com.example.ecommercebackend.Repository.LeagueRepository;
import com.example.ecommercebackend.Repository.SquadraRepository;
import com.example.ecommercebackend.Support.DTO.SquadraDTO;
import com.example.ecommercebackend.Support.Exception.SquadAlreadyExistException;
import com.example.ecommercebackend.Support.Exception.SquadNotExistException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class SquadraService {

    @Autowired
    private SquadraRepository squadraRepository;
    @Autowired
    private LeagueRepository leagueRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Squadra addSquad(@NotNull SquadraDTO squadra){
        if(squadraRepository.findBySquadra(squadra.getNome())!=null) throw new SquadAlreadyExistException();
        League l = leagueRepository.findByLeagueName(squadra.getLeagueName());
        Squadra s = new Squadra();
        s.setSquadra(squadra.getNome());
        s.setLeague(l);
        s.setImg(squadra.getImg());
        s.setMaglie(new HashSet<Maglia>());
        l.getSquadre().add(s);
        return squadraRepository.save(s);
    }

    @Transactional(readOnly = true)
    public List<Squadra> getAll(){
        return squadraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Squadra getSquad(String squadra){
        return squadraRepository.findBySquadra(squadra);
    }

    @Transactional(readOnly = true)
    public List<Squadra> okToSell(Integer min){
        if(min <= 0) throw new IllegalArgumentException();
        return squadraRepository.findDisponibili(min);
    }

    @Transactional(readOnly = true)
    public Squadra mostPopularSquad(){
        return squadraRepository.mpSquad();
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean modifySquad(@NotNull String oldName, @NotNull String newName, @Nullable String newImg){
        Squadra squad=squadraRepository.findBySquadra(oldName);
        if (squad == null) throw new SquadNotExistException();
        if(!(oldName.equals(newName)))
            squad.setSquadra(newName);
        if(!(squad.getImg().equals(newImg)) && newImg != null)
            squad.setImg(newImg);
        squadraRepository.saveAndFlush(squad);
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean deleteSquad(String s){
        if (squadraRepository.findBySquadra(s) == null) throw new SquadNotExistException();
        squadraRepository.delete(squadraRepository.findBySquadra(s));
        return true;
    }
}