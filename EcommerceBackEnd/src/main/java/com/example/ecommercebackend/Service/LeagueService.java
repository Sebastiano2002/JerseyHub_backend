package com.example.ecommercebackend.Service;

import com.example.ecommercebackend.Entity.League;
import com.example.ecommercebackend.Entity.Squadra;
import com.example.ecommercebackend.Repository.LeagueRepository;
import com.example.ecommercebackend.Support.DTO.LeagueDTO;
import com.example.ecommercebackend.Support.Exception.LeagueExistAlreadyException;
import com.example.ecommercebackend.Support.Exception.LeagueNotExistException;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
public class LeagueService {

    @Autowired
    LeagueRepository leagueRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public League addLeague(LeagueDTO league){
        if (leagueRepository.findByLeagueName(league.getLeague()) != null) throw new LeagueExistAlreadyException();
        League l = new League();
        l.setLeagueName(league.getLeague());
        l.setImg(league.getImg());
        l.setSquadre(new HashSet<Squadra>());
        return leagueRepository.save(l);
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean removeLeague(String leagueName){
        League l = leagueRepository.findByLeagueName(leagueName);
        if ( l == null) throw new LeagueNotExistException();
        leagueRepository.delete(l);
        return true;
    }


    @Transactional(readOnly = true)
    public League getLeague(String leagueName){
        return leagueRepository.findByLeagueName(leagueName);
    }

    @Transactional(readOnly = true)
    public List<League> getAllLeagues(){
        return leagueRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Collection<Squadra> getAllSquads(String league){
        if(leagueRepository.findByLeagueName(league)==null) throw new LeagueNotExistException();
        return leagueRepository.findByLeagueName(league).getSquadre();
    }

}
