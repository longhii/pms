package br.com.longhi.data;

import com.vaadin.flow.component.charts.model.style.SolidColor;

public enum Status {
    ATENDIDO(SolidColor.BLUE.toString()), CANCELADO(SolidColor.DARKRED.toString()), EM_ESPERA(SolidColor.YELLOW.toString());

    private final String cor;

    Status(String cor) {
        this.cor = cor;
    }

    public String getCor() {
        return this.cor;
    }
}
