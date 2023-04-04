package com.longhi.pms.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "pacientes")
@Getter @Setter @NoArgsConstructor
public class Paciente extends Usuario {

    @NotNull
    @ManyToOne
    private Psicologo psicologo;

    public Paciente(String login, String senha, String email, String telefone, Psicologo psicologo) {
        this.login = login;
        this.senha = senha;
        this.email = email;
        this.telefone = telefone;
        this.psicologo = psicologo;
        this.role = Role.ROLE_USER;
    }
}
