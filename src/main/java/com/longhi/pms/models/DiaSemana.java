package com.longhi.pms.models;

public enum DiaSemana {
    SEGUNDA(1, "Segunda-feira"),
    TERCA(2, "Terça-feira"),
    QUARTA(3, "Quarta-feira"),
    QUINTA(4, "Quinta-feira"),
    SEXTA(5, "Sexta-feira"),
    SABADO(6, "Sábado"),
    DOMINGO(7, "Domingo");

    private final int valor;
    private final String descricao;

    DiaSemana(int valor, String descricao) {
        this.valor = valor;
        this.descricao = descricao;
    }

    public int getValor() {
        return valor;
    }

    public String getDescricao() {
        return descricao;
    }
}
