package com.example.ecommercebackend.Repository;

import com.example.ecommercebackend.Entity.Maglia;
import com.example.ecommercebackend.Entity.Squadra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MagliaRepository extends JpaRepository<Maglia, Integer> {

    Maglia findByNome(String nome);

    List<Maglia> findBySquadra(Squadra squadra);

    @Query("select m from Maglia m where m.quantitaDisp >=:quantita")
    List<Maglia> disponibili(@Param("quantita") Integer quantita);

    @Query("SELECT om.maglia FROM OrdineMaglia om GROUP BY om.maglia ORDER BY SUM(om.richiesta) DESC LIMIT 1")
    Maglia findTopMagliaXvendite();
}