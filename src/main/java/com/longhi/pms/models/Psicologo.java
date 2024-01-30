package com.longhi.pms.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "psicologos")
@Getter @Setter @NoArgsConstructor
public class Psicologo extends Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "psicologos_id_seq")
    private Long id;

    @NotNull
    private LocalDateTime dataRenovacao;

    @NotNull
    private Boolean permitirRenovar = true;

    public Psicologo(String login, String senha, String email, String telefone, LocalDateTime dataRenovacao, Boolean permitirRenovar) {
        this.login = login;
        this.senha = senha;
        this.email = email;
        this.telefone = telefone;
        this.dataRenovacao = dataRenovacao;
        this.permitirRenovar = permitirRenovar;
        this.role = Role.ROLE_ADMIN;
    }
}
