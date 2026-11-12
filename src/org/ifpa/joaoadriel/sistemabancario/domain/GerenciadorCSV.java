package org.ifpa.joaoadriel.sistemabancario.domain;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

public class GerenciadorCSV {
    public static void carregarContas(Banco banco){
        File arquivo = new File("contas.csv");

        if (arquivo.exists()){
            try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))){
                String linha;

                while((linha = reader.readLine()) != null){
                    String[] partes = linha.split(";");

                    if (partes.length < 6) continue;

                    String tipo = partes[0];
                    String nome = partes[1];
                    String cpf = partes[2];
                    String senha = partes[3];
                    String chavePix = partes[4];
                    double saldo = Double.parseDouble(partes[5]);

                    Usuario usuario = banco.buscarPorCpf(cpf);

                    if (usuario == null){
                        usuario = new Usuario(nome, cpf, senha);
                        banco.cadastrarUsuarios(usuario);
                    }

                    if(tipo.equalsIgnoreCase("CORRENTE")){
                        usuario.abrirContaCorrente(chavePix, saldo);
                    }else if(tipo.equalsIgnoreCase("POUPANCA")){
                        usuario.abrirContaPoupanca(chavePix, saldo);
                    }
                }
            }catch (IOException e){
                System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            }catch (NumberFormatException e){
                System.out.println("Erro ao converter saldo numérico no arquivo.");
            }
        }
    }

    public static void salvarContas(Banco banco){
         try (BufferedWriter writer = new BufferedWriter(new FileWriter("contas.csv", false))){

            for (Usuario usuarioAtual : banco.getUsuarios()){
                if (usuarioAtual.getContaCorrente() != null){
                    String linha = "CORRENTE;" + usuarioAtual.getNome() + ";" + usuarioAtual.getCpf() + ";" +
                            usuarioAtual.getSenha() + ";" + usuarioAtual.getContaCorrente().getChavePix() + ";" +
                            usuarioAtual.getContaCorrente().getSaldo();
                    writer.write(linha);
                    writer.newLine();
                }

                if(usuarioAtual.getContaPoupanca() != null){
                    String linha = "POUPANCA;" + usuarioAtual.getNome() + ";" + usuarioAtual.getCpf() + ";" +
                            usuarioAtual.getSenha() + ";" + usuarioAtual.getContaPoupanca().getChavePix() + ";" +
                            usuarioAtual.getContaPoupanca().getSaldo();

                    writer.write(linha);
                    writer.newLine();
                }
            }
        }catch (IOException e){
            System.out.println("Erro ao salvar o arquivo " + e.getMessage());
        }
    }

    public static void salvarExtrato(Banco banco){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("extratos.csv", false))){

            for (Usuario usuarioAtual : banco.getUsuarios()){
                if (usuarioAtual.getContaCorrente() != null){

                    List<Transacao> listaCorrente = usuarioAtual.getContaCorrente().getExtrato();

                    for (Transacao transacao : listaCorrente){
                        String linha = "CORRENTE;" + usuarioAtual.getCpf() + ";" + transacao.getData() + ";" + transacao.getTipoOperacao() + ";" + transacao.getValor() + ";" + transacao.getOrigemDestino();
                        writer.write(linha);
                        writer.newLine();
                    }
                }
                if (usuarioAtual.getContaPoupanca() != null){

                    List <Transacao> listaPoupanca = usuarioAtual.getContaPoupanca().getExtrato();
                    for (Transacao transacao : listaPoupanca) {
                        String linha = "POUPANCA;" + usuarioAtual.getCpf() + ";" + transacao.getData() + ";" + transacao.getTipoOperacao() + ";" + transacao.getValor() + ";" + transacao.getOrigemDestino();
                        writer.write(linha);
                        writer.newLine();
                    }
                }
            }
        }catch (IOException e){
            System.out.println("Erro ao salvar o arquivo" + e.getMessage());
        }
    }

    public static void carregarExtrato(Banco banco){
        File arquivo = new File("extratos.csv");

        if (arquivo.exists()){
            try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))){
                String linha;

                while ((linha = reader.readLine()) != null){
                    String[] dados = linha.split(";");

                    if (dados.length < 6) continue;

                    String tipoConta = dados[0];
                    String cpf = dados[1];
                    LocalDateTime dataTransacao = LocalDateTime.parse(dados[2]);
                    String operacao = dados[3];
                    double valor = Double.parseDouble(dados[4]);
                    String origemDestino = dados[5];

                    Usuario usuarioAtual = banco.buscarPorCpf(cpf);
                    if (usuarioAtual != null){
                        Transacao transacao = new Transacao(dataTransacao, operacao, valor, origemDestino);

                        if (tipoConta.equals("CORRENTE") && (usuarioAtual.getContaCorrente() != null)){
                            usuarioAtual.getContaCorrente().adicionarTransacao(transacao);
                        }

                        if (tipoConta.equals("POUPANCA") && (usuarioAtual.getContaPoupanca() != null)){
                            usuarioAtual.getContaPoupanca().adicionarTransacao(transacao);
                        }
                    }
                }


            }catch (IOException e){
                System.out.println("Erro ao ler o arquivo" + e.getMessage());
            }catch (NumberFormatException e){
                System.out.println("Erro ao converter saldo numérico no arquivo.");
            }
        }
    }
}
