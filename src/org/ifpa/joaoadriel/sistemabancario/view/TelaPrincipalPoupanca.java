package org.ifpa.joaoadriel.sistemabancario.view;

import org.ifpa.joaoadriel.sistemabancario.domain.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaPrincipalPoupanca extends JPanel {
    private Banco banco;
    private Usuario usuarioLogado;
    private JPanel painelContaPoupanca;
    private JPanel painelAcoes;
    private JPanel painelSaldo;
    private JPanel painelSemUsuario;
    private JButton btnPix;
    private JButton btnDepositar;
    private JButton btnSacar;
    private JButton btnCriarConta;
    private JButton btnGuardarCorrente;
    private DefaultTableModel modeloTabela;

    public TelaPrincipalPoupanca(Banco banco, Usuario usuarioLogado, CardLayout controlador, JPanel painelCentro){
        this.banco = banco;
        this.usuarioLogado = usuarioLogado;

        setLayout(new BorderLayout());

        if (usuarioLogado.getContaPoupanca() != null){
            desenharPainelComConta(usuarioLogado);
        }else{
            desenharpainelSemConta(usuarioLogado);
        }

    }

    private void desenharPainelComConta(Usuario usuarioLogado){
        painelContaPoupanca = new JPanel();
        painelContaPoupanca.setLayout(new BoxLayout(painelContaPoupanca, BoxLayout.Y_AXIS));

        javax.swing.border.TitledBorder bordaCima = BorderFactory.createTitledBorder("Olá" + usuarioLogado.getNome() + " esta é sua Conta Poupança");
        bordaCima.setTitleFont(new Font("Arial", Font.ITALIC, 16));

        painelContaPoupanca.setBorder(bordaCima);

        painelSaldo = new JPanel();
        painelSaldo.setLayout(new FlowLayout(FlowLayout.CENTER));

        javax.swing.border.TitledBorder bordaSaldo = BorderFactory.createTitledBorder("Seu saldo");
        bordaSaldo.setTitleFont(new Font("Arial", Font.ITALIC, 16));

        painelSaldo.setBorder(bordaSaldo);
        painelSaldo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelSaldo.setPreferredSize(new Dimension(Integer.MAX_VALUE, 80));
        painelSaldo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel textoSaldo = new JLabel("R$ " + usuarioLogado.getContaPoupanca().getSaldo());
        textoSaldo.setFont(new Font("Arial", Font.BOLD, 32));
        textoSaldo.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelAcoes = new JPanel();
        painelAcoes.setLayout(new FlowLayout());
        painelAcoes.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnPix = new JButton("Pix");
        btnDepositar = new JButton("Depositar saldo");
        btnSacar = new JButton("Sacar saldo");
        btnGuardarCorrente = new JButton("Passar para Corrente");

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
                    String valorDigitado = JOptionPane.showInputDialog("Seu saldo: R$" + usuarioLogado.getContaPoupanca().getSaldo() + "\n Digite o valor do pix: ");

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
                        boolean sucesso = usuarioLogado.getContaPoupanca().pix(contaDestino, valor);
                        if (sucesso){
                            textoSaldo.setText("R$ " + usuarioLogado.getContaPoupanca().getSaldo());
                            GerenciadorCSV.salvarContas(banco);
                            GerenciadorCSV.salvarExtrato(banco);

                            java.util.List<Transacao> extrato = usuarioLogado.getContaPoupanca().getExtrato();
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
                    String valorDigitado = JOptionPane.showInputDialog("Seu saldo R$" + usuarioLogado.getContaPoupanca().getSaldo() + "\n Digite o valor que deseja depositar: ");

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
                        boolean sucesso = usuarioLogado.getContaPoupanca().depositar(valor);

                        if (sucesso){
                            textoSaldo.setText("R$ " + usuarioLogado.getContaPoupanca().getSaldo());
                            GerenciadorCSV.salvarContas(banco);
                            GerenciadorCSV.salvarExtrato(banco);

                            java.util.List<Transacao> extrato = usuarioLogado.getContaPoupanca().getExtrato();
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
                    String valorDigitado = JOptionPane.showInputDialog("Seu saldo R$" + usuarioLogado.getContaPoupanca().getSaldo() + "\n Digite o valor que deseja retirar: ");

                    if (valorDigitado == null) {
                        JOptionPane.showMessageDialog(null, "Digite  um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double valor = Double.parseDouble(valorDigitado);

                    if (valor <= 0){
                        JOptionPane.showMessageDialog(null, "Digite um valor maior que zero.");
                        return;
                    }

                    int opcao = JOptionPane.showConfirmDialog(null, "Você tem certeza que deseja retirar de sua conta R$" + valor+ "?", "Sacar saldo", JOptionPane.YES_NO_OPTION);

                    if (opcao == JOptionPane.YES_OPTION){
                        boolean sucesso = usuarioLogado.getContaPoupanca().sacar(valor);

                        if (sucesso){
                            textoSaldo.setText("R$ " + usuarioLogado.getContaPoupanca().getSaldo());
                            GerenciadorCSV.salvarContas(banco);
                            GerenciadorCSV.salvarExtrato(banco);

                            java.util.List<Transacao> extrato = usuarioLogado.getContaPoupanca().getExtrato();
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

        btnGuardarCorrente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (usuarioLogado.getContaCorrente() == null){
                    JOptionPane.showMessageDialog(null, "Você não possui uma conta corrente para passar seu saldo", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try{
                    String valorDigitado = JOptionPane.showInputDialog("Qual a quantia que você deseja mandar para sua conta Corrente?");

                    if (valorDigitado == null){
                        JOptionPane.showMessageDialog(null, "Digite um valor númerico válido", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double valor = Double.parseDouble(valorDigitado);

                    if (valor <= 0){
                        JOptionPane.showMessageDialog(null, "Digite um valor válido", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (valor > usuarioLogado.getContaPoupanca().getSaldo()){
                        JOptionPane.showMessageDialog(null, "Você não possui esse saldo para passar para sua conta corrente!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    boolean sucesso = usuarioLogado.resgatarDaPoupanca(valor);

                    if (sucesso){
                        textoSaldo.setText("R$ " + usuarioLogado.getContaPoupanca().getSaldo());

                        GerenciadorCSV.salvarContas(banco);
                        GerenciadorCSV.salvarExtrato(banco);

                        java.util.List<Transacao> extrato = usuarioLogado.getContaPoupanca().getExtrato();
                        Transacao ultimaTransacao = extrato.get(extrato.size() - 1);

                        modeloTabela.addRow(new Object[]{
                                ultimaTransacao.getData(),
                                ultimaTransacao.getTipoOperacao(),
                                ultimaTransacao.getValor(),
                                ultimaTransacao.getOrigemDestino()
                        });
                    }

                }catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        painelSaldo.add(textoSaldo);
        painelAcoes.add(btnDepositar);
        painelAcoes.add(btnSacar);
        painelAcoes.add(btnPix);
        painelContaPoupanca.add(painelSaldo);
        painelContaPoupanca.add(painelAcoes);
        add(painelContaPoupanca, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel();

        modeloTabela.addColumn("Data/Hora");
        modeloTabela.addColumn("Operação");
        modeloTabela.addColumn("Valor(R$)");

        for (Transacao transacaoAtual : usuarioLogado.getContaPoupanca().getExtrato()){
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

    private void desenharpainelSemConta(Usuario usuarioLogado){
        painelSemUsuario = new JPanel();
        painelSemUsuario.setLayout(new BoxLayout(painelSemUsuario, BoxLayout.Y_AXIS));
        painelSemUsuario.setBorder(BorderFactory.createTitledBorder("Olá, " + usuarioLogado.getNome() + ", você não possui uma conta poupanca!"));

        JLabel textoCriarConta = new JLabel("Deseja criar uma conta poupança? ");
        textoCriarConta.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnCriarConta = new JButton("Criar conta poupança");
        btnCriarConta.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnCriarConta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chavePix = JOptionPane.showInputDialog(null, "Crie uma chave Pix para a sua nova Conta Poupança:");

                if (chavePix == null || chavePix.isBlank()) {
                    JOptionPane.showMessageDialog(null, "Digite alguma coisa válida", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean sucesso = usuarioLogado.abrirContaPoupanca(chavePix, 0.0);

                if (sucesso) {
                    GerenciadorCSV.salvarContas(banco);

                    JOptionPane.showMessageDialog(null, "Conta poupança criada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                    removeAll();
                    desenharPainelComConta(usuarioLogado);
                    revalidate();
                    repaint();

                } else {
                    JOptionPane.showMessageDialog(null, "Já possuis uma conta poupança!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        painelSemUsuario.add(textoCriarConta);
        painelSemUsuario.add(btnCriarConta);
        add(painelSemUsuario, BorderLayout.CENTER);
    }
}
