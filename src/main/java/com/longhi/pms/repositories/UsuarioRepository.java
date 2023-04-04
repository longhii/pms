package com.longhi.pms.repositories;

import com.longhi.pms.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    @Query("SELECT p FROM Psicologo p WHERE p.login = :login")
    Optional<Usuario> findPsicologoByLogin(@Param("login") String login);

    @Query("SELECT p FROM Paciente p WHERE p.login = :login")
    Optional<Usuario> findPacienteByLogin(@Param("login") String login);

}
