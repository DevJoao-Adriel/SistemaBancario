package org.ifpa.joaoadriel.sistemabancario.domain;

import java.time.LocalDateTime;

public class Usuario {
    private String nome;
    private String cpf;
    private String senha;

    private ContaCorrente contaCorrente;
    private ContaPoupanca contaPoupanca;

    public Usuario(String nome, String cpf, String senha) {
        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha;
        this.contaCorrente = null;
        this.contaPoupanca = null;
    }

    public boolean abrirContaCorrente(String chavePix, double saldoInicial){
        if (this.contaCorrente != null) {
            return false;
        }
        this.contaCorrente = new ContaCorrente(this, chavePix, saldoInicial);
        return true;
    }

    public boolean abrirContaPoupanca(String chavePix, double saldoInicial){
        if (this.contaPoupanca != null){
            return false;
        }
        this.contaPoupanca = new ContaPoupanca(this, chavePix, saldoInicial);
        return true;
    }

    public boolean guardarNaPoupanca(double valor){
        if ((this.getContaCorrente() != null) && (this.getContaPoupanca() != null)){
            if (valor <= 0) return false;
            if (contaCorrente.getSaldo() < valor) return false;

            contaCorrente.saldo -= valor;
            contaPoupanca.saldo += valor;

            LocalDateTime dataAtual = LocalDateTime.now();

            Transacao transacaoEnviada = new Transacao(dataAtual, "Saldo Enviado", valor, "Para: Sua Conta Poupança");
            Transacao transacaoRecebida = new Transacao(dataAtual, "Saldo Recebido", valor, "De: Sua Conta Corrente");

            contaCorrente.adicionarTransacao(transacaoEnviada);
            contaPoupanca.adicionarTransacao(transacaoRecebida);

            return true;
        }else{
            return false;
        }
    }

    public boolean resgatarDaPoupanca(double valor){
        if ((this.getContaCorrente() != null) && (this.getContaPoupanca() != null)){
            if (valor <= 0) return false;
            if (contaPoupanca.getSaldo() < valor) return false;

            contaPoupanca.saldo -= valor;
            contaCorrente.saldo += valor;

            LocalDateTime dataAtual = LocalDateTime.now();

            Transacao transacaoEnviada = new Transacao(dataAtual, "Saldo Enviado", valor, "Para: Sua Conta Corrente");
            Transacao transacaoRecebida = new Transacao(dataAtual, "Saldo Recebido", valor, "De: Sua Conta Poupança");

            contaPoupanca.adicionarTransacao(transacaoEnviada);
            contaCorrente.adicionarTransacao(transacaoRecebida);

            return true;
        }else{
            return false;
        }
    }

    public ContaCorrente getContaCorrente() {
        return contaCorrente;
    }

    public ContaPoupanca getContaPoupanca() {
        return contaPoupanca;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
