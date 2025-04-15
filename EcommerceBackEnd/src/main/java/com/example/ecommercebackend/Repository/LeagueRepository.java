package com.example.ecommercebackend.Repository;

import com.example.ecommercebackend.Entity.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LeagueRepository extends JpaRepository<League, Integer> {
    League findByLeagueName(String LeagueName);

    List<League> findAll();
}
