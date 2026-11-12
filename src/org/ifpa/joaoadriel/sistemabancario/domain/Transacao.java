package org.ifpa.joaoadriel.sistemabancario.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transacao {
    private LocalDateTime data;
    private String tipoOperacao;
    private double valor;
    private String origemDestino;

    public Transacao(LocalDateTime data, String tipoOperacao, double valor, String origemDestino) {
        this.data = data;
        this.tipoOperacao = tipoOperacao;
        this.valor = valor;
        this.origemDestino = origemDestino;
    }

    public LocalDateTime getData() {
        return data;
    }

    public String getTipoOperacao() {
        return tipoOperacao;
    }

    public double getValor() {
        return valor;
    }

    public String getOrigemDestino() {
        return origemDestino;
    }

}
