package com.longhi.pms.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@MappedSuperclass
@Getter @Setter
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_id_seq")
    @SequenceGenerator(name = "usuario_id_seq", sequenceName = "usuario_id_seq", allocationSize = 1)
    protected Long id;

    @NotNull
    protected String login;

    @NotNull
    protected String senha;

    protected String email;

    protected String telefone;

    @NotNull
    protected Role role;
}
