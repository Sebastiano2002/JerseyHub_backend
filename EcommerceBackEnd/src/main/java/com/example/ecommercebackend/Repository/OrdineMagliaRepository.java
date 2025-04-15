package com.example.ecommercebackend.Repository;

import com.example.ecommercebackend.Entity.Maglia;
import com.example.ecommercebackend.Entity.Ordine;
import com.example.ecommercebackend.Entity.OrdineMaglia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdineMagliaRepository extends JpaRepository<OrdineMaglia, Integer> {

    OrdineMaglia findByMagliaAndOrdine(Maglia maglia, Ordine ordine);

    OrdineMaglia findByMagliaIdMagliaAndOrdineIdOrdine(Integer maglia, Integer ordine);

    List<OrdineMaglia> findByOrdineIdOrdine(Integer ordine);

}
