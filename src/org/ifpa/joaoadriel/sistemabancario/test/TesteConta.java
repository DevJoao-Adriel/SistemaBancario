package org.ifpa.joaoadriel.sistemabancario.test;

import org.ifpa.joaoadriel.sistemabancario.domain.Banco;
import org.ifpa.joaoadriel.sistemabancario.domain.GerenciadorCSV;
import org.ifpa.joaoadriel.sistemabancario.domain.Usuario;

public class TesteConta {
    public static void main(String[] args) {
        // 1. Inicia o banco vazio
        Banco meuBanco = new Banco();

        // 2. Tenta carregar os dados do arquivo (se existir)
        GerenciadorCSV.carregarContas(meuBanco);
        System.out.println("--- Sistema Iniciado ---");
        System.out.println("Usuários carregados: " + meuBanco.getUsuarios().size());

        // =======================================================
        // BLOCO DE CRIAÇÃO (Na segunda vez que rodar, pode apagar ou comentar isso aqui)
        if (meuBanco.getUsuarios().isEmpty()) {
            System.out.println("Nenhum dado encontrado. Criando dados de teste...");

            Usuario lilian = new Usuario("Lilian", "000.000.000-00", "senha123");
            lilian.abrirContaCorrente("lilian.cc@pix", 1500.00);
            lilian.abrirContaPoupanca("lilian.cp@pix", 5000.00);
            meuBanco.cadastrarUsuarios(lilian);

            Usuario joao = new Usuario("Joao", "111.111.111-11", "senha456");
            joao.abrirContaCorrente("joao.cc@pix", 300.00);
            meuBanco.cadastrarUsuarios(joao);
        }
        // =======================================================

        // 3. Simula uma operação (Ex: Lilian fazendo um depósito na corrente)
        Usuario lilian = meuBanco.buscarPorCpf("000.000.000-00");
        if (lilian != null && lilian.getContaCorrente() != null) {
            System.out.println("Saldo anterior da Lilian: R$ " + lilian.getContaCorrente().getSaldo());

            lilian.getContaCorrente().depositar(200.00);

            System.out.println("Novo saldo da Lilian: R$ " + lilian.getContaCorrente().getSaldo());
        }

        // 4. Salva as alterações no arquivo antes de fechar o programa
        GerenciadorCSV.salvarContas(meuBanco);
        System.out.println("Dados salvos com sucesso. Fim do programa.");
    }
}