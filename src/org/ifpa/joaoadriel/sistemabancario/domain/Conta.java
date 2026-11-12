package org.ifpa.joaoadriel.sistemabancario.domain;

import java.util.ArrayList;
import java.util.List;

public abstract class Conta {
    private Usuario titular;
    private String chavePix;
    protected double saldo;
    private List<Transacao> extrato;

    public Conta(Usuario titular, String chavePix, double saldo) {
        this.titular = titular;
        this.chavePix = chavePix;
        this.saldo = saldo;
        this.extrato = new ArrayList<>();
    }

    public void adicionarTransacao(Transacao transacao){
        this.extrato.add(transacao);
    }

    public List<Transacao> getExtrato() {
        return extrato;
    }

    public abstract boolean sacar(double valor);

    public abstract boolean depositar(double valor);

    public abstract boolean pix(Conta contaDestinho, double valor);

    public Usuario getTitular() {
        return titular;
    }

    public void setTitular(Usuario titular) {
        this.titular = titular;
    }

    public String getChavePix() {
        return chavePix;
    }

    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}
