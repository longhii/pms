package com.longhi.pms.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consultorios")
@Getter @Setter @NoArgsConstructor
public class Consultorio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consultorios_id_seq")
    private Long id;

    @NotNull
    private String endereco;

    @NotNull
    private String cidade;

    @NotNull
    private String uf;

    @NotNull
    private String numero;

    private String cep;

    @OneToMany(mappedBy = "consultorio",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER,
            orphanRemoval = true)
    private List<HorarioAtendimento> horariosAtendimento = new ArrayList<>();

    @NotNull
    @ManyToOne
    private Psicologo psicologo;

    public Consultorio(String endereco,
                       String cidade,
                       String uf,
                       String numero,
                       String cep,
                       List<HorarioAtendimento> horariosAtendimento,
                       Psicologo psicologo) {
        this.endereco = endereco;
        this.cidade = cidade;
        this.uf = uf;
        this.numero = numero;
        this.cep = cep;
        this.horariosAtendimento = horariosAtendimento;
        this.psicologo = psicologo;
    }

    public void adicionarHorarioAtendimento(HorarioAtendimento horarioAtendimento) {
        this.horariosAtendimento.add(horarioAtendimento);
    }

    @Override
    public String toString() {
        return "Consultorio{" +
            "endereco='" + endereco + '\'' +
            ", cidade='" + cidade + '\'' +
            ", uf='" + uf + '\'' +
            ", numero='" + numero + '\'' +
            ", cep='" + cep + '\'' +
            ", horariosAtendimento=" + horariosAtendimento +
            ", psicologo=" + psicologo +
            '}';
    }

    //    public void setDiasDisponiveis(Set<String> diasDisponiveis) {
//        this.diasDisponiveis = Arrays.stream(DiaSemana.values())
//                .filter(d -> diasDisponiveis.contains(d.getDescricao()))
//                .map(d -> String.format("%d", d.getValor()))
//                .collect(Collectors.joining("_"));
//    }
//
//    public Set<String> getDiasDisponiveis() {
//        var dias = Arrays
//                .stream((this.diasDisponiveis == null ? new String[0] : this.diasDisponiveis.split("_")))
//                .map(Integer::parseInt)
//                .collect(Collectors.toList());
//
//        var diasSemana = DiaSemana.values();
//        var result = new HashSet<String>();
//
//        for (int i = 0; i < diasSemana.length; i++)
//            if (dias.contains(diasSemana[i].getValor())) result.add(diasSemana[i].getDescricao());
//
//        return result;
//    }
}
