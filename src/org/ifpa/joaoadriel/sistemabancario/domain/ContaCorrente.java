package org.ifpa.joaoadriel.sistemabancario.domain;

import java.time.LocalDateTime;

public class ContaCorrente extends Conta implements Tributavel{
    public ContaCorrente(Usuario titutar, String chavePix, double saldo) {
        super(titutar, chavePix, saldo);
    }

    @Override
    public double calcularTaxa() {
        return 1.00;
    }

    @Override
    public boolean sacar(double valor) {
        if (valor <= 0) return false;
        double valorTotal = calcularTaxa() + valor;

        if (this.saldo >= valorTotal){
            this.saldo -= valorTotal;
            LocalDateTime dataAtual = LocalDateTime.now();
            Transacao transacao = new Transacao(dataAtual, "Saque + taxa Realizado", valorTotal, "Você mesmo");
            adicionarTransacao(transacao);

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
            adicionarTransacao(transacao);
            return true;
        }
        return false;
    }

    @Override
    public boolean pix(Conta contaDestino, double valor) {
        if (valor <= 0) return false;
        if (this.getSaldo() >= valor){
            this.saldo -= valor;
            contaDestino.saldo += valor;

            LocalDateTime dataAtual = LocalDateTime.now();

            Transacao transacaoEnviada = new Transacao(dataAtual, "Pix Enviado", valor, "Para: "+ contaDestino.getTitular().getNome());
            Transacao transacaoRecebida = new Transacao(dataAtual, "Pix Recebido", valor, this.getTitular().getNome());

            this.adicionarTransacao(transacaoEnviada);
            contaDestino.adicionarTransacao(transacaoRecebida);

            return true;
        }
        return false;
    }
}
