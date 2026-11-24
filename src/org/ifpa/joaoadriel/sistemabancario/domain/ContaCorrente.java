package org.ifpa.joaoadriel.sistemabancario.domain;

import java.time.LocalDateTime;

public class ContaCorrente extends Conta implements Tributavel {
    private double limiteChequeEspecial;

    //construtor para delimitar o cheque especial existente
    public ContaCorrente(Usuario titular, String chavePix, double saldo) {
        this(titular, chavePix, saldo, 500.00);
    }

    //construtor sobrecarregado para cadastrar uma conta corrente
    public ContaCorrente(Usuario titular, String chavePix, double saldo, double limiteChequeEspecial) {
        super(titular, chavePix, saldo);
        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    public double getLimiteChequeEspecial() {
        return limiteChequeEspecial;
    }

    public void setLimiteChequeEspecial(double limiteChequeEspecial) {
        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    public double getSaldoDisponivel() {
        return this.saldo + this.limiteChequeEspecial;
    }

    public double getChequeEspecialRestante() {
        if (this.saldo < 0) {
            return Math.max(0, this.limiteChequeEspecial + this.saldo);
        }
        return this.limiteChequeEspecial;
    }

    @Override
    public double calcularTaxa() {
        return 1.00;
    }

    @Override
    public boolean sacar(double valor) {
        if (valor <= 0) return false;
        double valorTotal = calcularTaxa() + valor;

        if (this.getSaldoDisponivel() >= valorTotal) {
            this.saldo -= valorTotal;
            LocalDateTime dataAtual = LocalDateTime.now();
            Transacao transacao = new Transacao(dataAtual, "Saque + taxa Realizado", valorTotal, "Você mesmo");
            adicionarTransacao(transacao);
            return true;
        }
        return false;
    }

    @Override
    public boolean depositar(double valor) {
        if (valor > 0) {
            this.saldo += valor;
            LocalDateTime dataAtual = LocalDateTime.now();
            Transacao transacao = new Transacao(dataAtual, "Depósito Realizado", valor, "Você mesmo");
            adicionarTransacao(transacao);
            return true;
        }
        return false;
    }

    @Override
    public boolean pix(Conta contaDestino, double valor) {
        if (valor <= 0) return false;
        if (this.getSaldoDisponivel() >= valor) {
            this.saldo -= valor;
            contaDestino.saldo += valor;

            LocalDateTime dataAtual = LocalDateTime.now();

            Transacao transacaoEnviada = new Transacao(dataAtual, "Pix Enviado", valor, contaDestino.getTitular().getNome());
            Transacao transacaoRecebida = new Transacao(dataAtual, "Pix Recebido", valor, this.getTitular().getNome());

            this.adicionarTransacao(transacaoEnviada);
            contaDestino.adicionarTransacao(transacaoRecebida);

            return true;
        }
        return false;
    }
}