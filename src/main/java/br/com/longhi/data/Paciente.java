package br.com.longhi.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "pacientes", uniqueConstraints = {
        @UniqueConstraint(name = "unique_psi_tel", columnNames = { "psicologo_id", "telefone" })
})
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pacientes_id_seq")
    @SequenceGenerator(name = "pacientes_id_seq", sequenceName = "pacientes_id_seq", allocationSize = 1)
    private Long id;

    @NotNull
    private String nome;

    @Email
    private String email;

    private String telefone;

    private LocalDate createdAt;

    @NotNull
    @ManyToOne
    private Psicologo psicologo;

    public Long getId() {
        return id;
    }

    public @NotNull String getNome() {
        return nome;
    }

    public void setNome(@NotNull String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getTelefoneMask() {
        if (telefone.matches("\\(\\d{2}\\) \\d{5}-\\d{4}")) {
            String ddd = telefone.substring(1, 3); // DDD
            String secondPart = telefone.substring(12); // Segunda parte do número

            // Monta o número transformado
            String transformedPhoneNumber = String.format("(%s) %s-*%s", ddd, "*****", secondPart);
            return transformedPhoneNumber;
        } else {
            // Caso o número não esteja no formato esperado
            throw new IllegalArgumentException("Número de telefone não está no formato esperado.");
        }
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public @NotNull Psicologo getPsicologo() {
        return psicologo;
    }

    public void setPsicologo(@NotNull Psicologo psicologo) {
        this.psicologo = psicologo;
    }
}
