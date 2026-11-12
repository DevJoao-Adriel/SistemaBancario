package org.ifpa.joaoadriel.sistemabancario.domain;

import java.time.LocalDateTime;

public class ContaPoupanca extends Conta{
    public ContaPoupanca(Usuario titutar, String chavePix, double saldo) {
        super(titutar, chavePix, saldo);
    }

    @Override
    public boolean sacar(double valor) {
        if (valor <= 0) return false;
        if (this.saldo >= valor){
            this.saldo -= valor;
            LocalDateTime dataAtual = LocalDateTime.now();
            Transacao transacao = new Transacao(dataAtual, "Saque realizado", valor, "Você mesmo");
            this.adicionarTransacao(transacao);
            return true;
        }
        return false;
    }

    @Override
    public boolean depositar(double valor){
        if (valor > 0){
            this.saldo += valor;
            LocalDateTime dataAtual = LocalDateTime.now();
            Transacao transacao = new Transacao(dataAtual, "Depósito Realizado", valor, "Você mesmo");
            this.adicionarTransacao(transacao);
            return true;
        }
        return false;
    }

    @Override
    public boolean pix(Conta contaDestino, double valor){
        if (valor <= 0) return false;

        if (this.saldo >= valor){
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

