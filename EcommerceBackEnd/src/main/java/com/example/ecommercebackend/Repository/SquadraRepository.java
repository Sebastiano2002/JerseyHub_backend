package com.example.ecommercebackend.Repository;

import com.example.ecommercebackend.Entity.Squadra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SquadraRepository extends JpaRepository<Squadra, Integer> {
    Squadra findBySquadra(String squadName);

    //Restituisce le squadre con maglie con una quantità dipsonibile >= a quella richiesta
    @Query("select s from Squadra s JOIN s.maglie m where m.quantitaDisp > :minimo group by s")
    List<Squadra> findDisponibili(@Param("minimo") Integer minimo);

    //Squadra con più vendite
    @Query(" SELECT s FROM Squadra s JOIN s.maglie m JOIN m.items om GROUP BY s.idSquadra ORDER BY SUM(om.richiesta) DESC LIMIT 1")
    Squadra mpSquad();
}
