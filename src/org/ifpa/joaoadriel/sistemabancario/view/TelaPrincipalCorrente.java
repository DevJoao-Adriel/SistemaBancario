package org.ifpa.joaoadriel.sistemabancario.view;

import org.ifpa.joaoadriel.sistemabancario.domain.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaPrincipalCorrente extends JPanel {
    private Banco banco;
    private JPanel painelContaCorrente;
    private JPanel painelSaldo;
    private JPanel painelAcoes;
    private JPanel painelSemUsuario;
    private JButton btnPix;
    private JButton btnSacar;
    private JButton btnDepositar;
    private JButton btnCriarConta;
    private JButton btnGuardarPoupanca;
    private DefaultTableModel modeloTabela;

    public TelaPrincipalCorrente(Banco banco, Usuario usuarioLogado, CardLayout controlador, JPanel painelCentro){
        this.banco = banco;
        setLayout(new BorderLayout());

        if (usuarioLogado.getContaCorrente() != null) {
            desenharPainelComConta(usuarioLogado);
        }else{
            desenharPainelSemConta(usuarioLogado);
        }
    }

    private void desenharPainelComConta(Usuario usuarioLogado){
        painelContaCorrente = new JPanel();
        painelContaCorrente.setLayout(new BoxLayout(painelContaCorrente, BoxLayout.Y_AXIS));

        javax.swing.border.TitledBorder bordaCima = BorderFactory.createTitledBorder("Olá" + usuarioLogado.getNome() + " esta é sua Conta Corrente");
        bordaCima.setTitleFont(new Font("Arial", Font.ITALIC, 16));

        painelContaCorrente.setBorder(bordaCima);

        painelSaldo = new JPanel();
        painelSaldo.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));

        javax.swing.border.TitledBorder bordaSaldo = BorderFactory.createTitledBorder("Seu saldo");
        bordaSaldo.setTitleFont(new Font("Arial", Font.ITALIC, 16));

        painelSaldo.setBorder(bordaSaldo);
        painelSaldo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelSaldo.setPreferredSize(new Dimension(Integer.MAX_VALUE, 80));
        painelSaldo.setMaximumSize(new Dimension(Integer.MAX_VALUE ,80));

        JLabel textoSaldo = new JLabel("R$ " + usuarioLogado.getContaCorrente().getSaldo());
        textoSaldo.setFont(new Font("Arial", Font.BOLD, 32));
        textoSaldo.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelAcoes = new JPanel();
        painelAcoes.setLayout(new FlowLayout());
        painelAcoes.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnPix = new JButton("Pix");
        btnDepositar = new JButton("Depositar saldo");
        btnSacar = new JButton("Sacar saldo (essa opção possui acrêscimo de R$1)");
        btnGuardarPoupanca = new JButton("Passar para Poupança");

        btnPix.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chavePix = JOptionPane.showInputDialog("Insira a chave pix de quem você quer pagar: ");

                if ((chavePix == null) || (chavePix.isBlank())) {
                    JOptionPane.showMessageDialog(null, "Digite uma chave pix válida!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Conta contaDestino = banco.buscarContaPorPix(chavePix);

                if (contaDestino == null){
                    JOptionPane.showMessageDialog(null, "Usário não encontrado!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (contaDestino.getTitular().getCpf().equals(usuarioLogado.getCpf())){
                    JOptionPane.showMessageDialog(null, "Você não pode enviar um pix para si mesmo!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try{
                    String valorDigitado = JOptionPane.showInputDialog("Seu saldo: R$" + usuarioLogado.getContaCorrente().getSaldo() + "\n Digite o valor do pix: ");

                    if (valorDigitado == null){
                        JOptionPane.showMessageDialog(null, "Digite  um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double valor = Double.parseDouble(valorDigitado);

                    if (valor <= 0){
                        JOptionPane.showMessageDialog(null, "Digite um valor maior que zero.");
                        return;
                    }

                    int opcao = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja fazer um pix para: \n" + contaDestino.getTitular().getNome() + "\n No valor de: R$" + valor, "Fazer pix", JOptionPane.YES_NO_OPTION);

                    if (opcao == JOptionPane.YES_OPTION) {
                        boolean sucesso = usuarioLogado.getContaCorrente().pix(contaDestino, valor);
                        if (sucesso){
                            textoSaldo.setText("" + usuarioLogado.getContaCorrente().getSaldo());
                            GerenciadorCSV.salvarContas(banco);
                            GerenciadorCSV.salvarExtrato(banco);

                            java.util.List<Transacao> extrato = usuarioLogado.getContaCorrente().getExtrato();
                            Transacao ultimaTransacao = extrato.get(extrato.size() - 1);

                            modeloTabela.addRow(new Object[]{
                                    ultimaTransacao.getData(),
                                    ultimaTransacao.getTipoOperacao(),
                                    ultimaTransacao.getValor(),
                                    ultimaTransacao.getOrigemDestino()
                            });

                            JOptionPane.showMessageDialog(null, "Transação realizada com sucesso!", "Transação feita!", JOptionPane.INFORMATION_MESSAGE);
                        }else{
                            JOptionPane.showMessageDialog(null, "Saldo insuficiente para o Pix.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        }
                    }else return;
                }catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        btnDepositar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String valorDigitado = JOptionPane.showInputDialog("Seu saldo R$" + usuarioLogado.getContaCorrente().getSaldo() + "\n Digite o valor que deseja depositar: ");

                    if (valorDigitado == null){
                        JOptionPane.showMessageDialog(null, "Digite  um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double valor = Double.parseDouble(valorDigitado);

                    if (valor <= 0){
                        JOptionPane.showMessageDialog(null, "Digite um valor maior que zero.");
                        return;
                    }

                    int opcao = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja fazer um depósito de R$" + valor + "para sua conta ", "Depositar", JOptionPane.YES_NO_OPTION);

                    if (opcao == JOptionPane.YES_OPTION){
                        boolean sucesso = usuarioLogado.getContaCorrente().depositar(valor);

                        if (sucesso){
                            textoSaldo.setText("" + usuarioLogado.getContaCorrente().getSaldo());
                            GerenciadorCSV.salvarContas(banco);
                            GerenciadorCSV.salvarExtrato(banco);

                            java.util.List<Transacao> extrato = usuarioLogado.getContaCorrente().getExtrato();
                            Transacao ultimaTransacao = extrato.get(extrato.size() - 1);

                            modeloTabela.addRow(new Object[]{
                                    ultimaTransacao.getData(),
                                    ultimaTransacao.getTipoOperacao(),
                                    ultimaTransacao.getValor(),
                                    ultimaTransacao.getOrigemDestino()
                            });

                            JOptionPane.showMessageDialog(null, "Depósito realizado com sucesso!", "Depósito feito!", JOptionPane.INFORMATION_MESSAGE);
                        }else{
                            JOptionPane.showMessageDialog(null, "Algo deu errado, tente novamente daqui a algum tempo", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }else return;

                } catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnSacar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String valorDigitado = JOptionPane.showInputDialog("Seu saldo R$" + usuarioLogado.getContaCorrente().getSaldo() + "\n Digite o valor que deseja retirar: ");

                    if (valorDigitado == null) {
                        JOptionPane.showMessageDialog(null, "Digite  um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double valor = Double.parseDouble(valorDigitado);

                    if (valor <= 0){
                        JOptionPane.showMessageDialog(null, "Digite um valor maior que zero.");
                        return;
                    }

                    int opcao = JOptionPane.showConfirmDialog(null, "Você tem certeza que deseja retirar de sua conta R$" + (valor+1)+ "?", "Sacar saldo", JOptionPane.YES_NO_OPTION);

                    if (opcao == JOptionPane.YES_OPTION){
                        boolean sucesso = usuarioLogado.getContaCorrente().sacar(valor);

                        if (sucesso){
                            textoSaldo.setText("" + usuarioLogado.getContaCorrente().getSaldo());
                            GerenciadorCSV.salvarContas(banco);
                            GerenciadorCSV.salvarExtrato(banco);

                            java.util.List<Transacao> extrato = usuarioLogado.getContaCorrente().getExtrato();
                            Transacao ultimaTransacao = extrato.get(extrato.size() - 1);

                            modeloTabela.addRow(new Object[]{
                                    ultimaTransacao.getData(),
                                    ultimaTransacao.getTipoOperacao(),
                                    ultimaTransacao.getValor(),
                                    ultimaTransacao.getOrigemDestino()
                            });

                            JOptionPane.showMessageDialog(null, "Depósito realizado com sucesso!", "Depósito feito!", JOptionPane.INFORMATION_MESSAGE);
                        }else{
                            JOptionPane.showMessageDialog(null, "Algo deu errado, tente novamente daqui a algum tempo", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }else return;
                }catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnGuardarPoupanca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (usuarioLogado.getContaPoupanca() == null) JOptionPane.showMessageDialog(null, "Você não possui uma conta poupança para passar seu saldo", "[ERRO]", JOptionPane.ERROR_MESSAGE); return;

                
            }

        });

        painelSaldo.add(textoSaldo);
        painelAcoes.add(btnDepositar);
        painelAcoes.add(btnSacar);
        painelAcoes.add(btnPix);
        painelContaCorrente.add(painelSaldo);
        painelContaCorrente.add(painelAcoes);
        add(painelContaCorrente, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel();
        modeloTabela.addColumn("Data/Hora");
        modeloTabela.addColumn("Operação");
        modeloTabela.addColumn("Valor(R$)");
        modeloTabela.addColumn("De / Para");

        for (Transacao transacaoAtual : usuarioLogado.getContaCorrente().getExtrato()){
            modeloTabela.addRow(new Object[]{
                    transacaoAtual.getData(),
                    transacaoAtual.getTipoOperacao(),
                    transacaoAtual.getValor(),
                    transacaoAtual.getOrigemDestino()
            });
        }

        JTable tabelaExtrato = new JTable(modeloTabela);
        tabelaExtrato.setRowHeight(60);

        JScrollPane painelRolagem = new JScrollPane(tabelaExtrato);

        javax.swing.border.TitledBorder bordaExtrato = BorderFactory.createTitledBorder("Histórico transação");
        bordaExtrato.setTitleFont(new Font("Arial", Font.ITALIC, 16));

        painelRolagem.setBorder(bordaExtrato);

        add(painelRolagem, BorderLayout.CENTER);

    }

    private void desenharPainelSemConta(Usuario usuarioLogado){

        painelSemUsuario = new JPanel();
        painelSemUsuario.setLayout(new BoxLayout(painelSemUsuario, BoxLayout.Y_AXIS));
        painelSemUsuario.setBorder(BorderFactory.createTitledBorder("Olá, "+ usuarioLogado.getNome() +", você não possui uma conta corrente."));

        JLabel textoCriarConta = new JLabel("Deseja criar uma conta corrente? ");
        textoCriarConta.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnCriarConta = new JButton("Criar conta corrente");
        btnCriarConta.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnCriarConta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chavePix = JOptionPane.showInputDialog(null, "Crie uma chave Pix para a sua nova Conta Corrente:");

                if (chavePix == null || chavePix.isBlank()) {
                    JOptionPane.showMessageDialog(null, "Digite alguma coisa válida", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean sucesso = usuarioLogado.abrirContaCorrente(chavePix, 0.0);

                if (sucesso) {
                    GerenciadorCSV.salvarContas(banco);

                    JOptionPane.showMessageDialog(null, "Conta Corrente criada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                    removeAll();
                    desenharPainelComConta(usuarioLogado);
                    revalidate();
                    repaint();

                } else {
                    JOptionPane.showMessageDialog(null, "Já possuis uma conta corrente!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                }
            }

        });
        painelSemUsuario.add(textoCriarConta);
        painelSemUsuario.add(btnCriarConta);
        add(painelSemUsuario, BorderLayout.CENTER);
    }
}
