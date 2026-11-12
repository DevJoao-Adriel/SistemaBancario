package org.ifpa.joaoadriel.sistemabancario.test;

import org.ifpa.joaoadriel.sistemabancario.domain.Banco;
import org.ifpa.joaoadriel.sistemabancario.domain.GerenciadorCSV;
import org.ifpa.joaoadriel.sistemabancario.view.TelaInicial;

public class SistemaBancarioTeste {
    public static void main(String[] args) {
        Banco bancoIfpa = new Banco();
        GerenciadorCSV.carregarContas(bancoIfpa);
        GerenciadorCSV.carregarExtrato(bancoIfpa);
        TelaInicial telaInicial = new TelaInicial(bancoIfpa);

    }
}
