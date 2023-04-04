package com.longhi.pms.repositories;

import com.longhi.pms.models.Consultorio;
import com.longhi.pms.models.Psicologo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultorioRepository extends JpaRepository<Consultorio, Integer> {

    @Query("SELECT c FROM Consultorio c WHERE c.psicologo = :psicologo order by c.endereco")
    List<Consultorio> findConsultorioByPsicologo(@Param("psicologo") Psicologo psicologo);

}
