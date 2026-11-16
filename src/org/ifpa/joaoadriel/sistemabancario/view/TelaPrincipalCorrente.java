package org.ifpa.joaoadriel.sistemabancario.view;

import org.ifpa.joaoadriel.sistemabancario.domain.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.format.DateTimeFormatter;

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

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                removeAll();

                if (usuarioLogado.getContaCorrente() != null){
                    desenharPainelComConta(usuarioLogado);
                }else{
                    desenharPainelSemConta(usuarioLogado);
                }

                revalidate();
                repaint();
            }
        });
    }

    private void desenharPainelComConta(Usuario usuarioLogado){
        DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        painelContaCorrente = new JPanel();
        painelContaCorrente.setLayout(new BoxLayout(painelContaCorrente, BoxLayout.Y_AXIS));

        javax.swing.border.TitledBorder bordaCima = BorderFactory.createTitledBorder("Olá, " + usuarioLogado.getNome() + ", esta é sua Conta Corrente");
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

        JLabel textoSaldo = new JLabel(String.format("R$ %.2f", usuarioLogado.getContaCorrente().getSaldo()));
        textoSaldo.setFont(new Font("Arial", Font.BOLD, 32));
        textoSaldo.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelAcoes = new JPanel();
        painelAcoes.setLayout(new FlowLayout());
        painelAcoes.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnPix = new JButton("Pix");
        btnDepositar = new JButton("Depositar saldo");
        btnSacar = new JButton("Sacar saldo (essa opção possui acréscimo de R$ 1,00)");
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
                    JOptionPane.showMessageDialog(null, "Usuário não encontrado!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (contaDestino.getTitular().getCpf().equals(usuarioLogado.getCpf())){
                    JOptionPane.showMessageDialog(null, "Você não pode enviar um pix para si mesmo!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try{
                    String valorDigitado = JOptionPane.showInputDialog(String.format("Seu saldo: R$ %.2f\nDigite o valor do pix: ", usuarioLogado.getContaCorrente().getSaldo()));

                    if (valorDigitado == null){
                        JOptionPane.showMessageDialog(null, "Digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double valor = Double.parseDouble(valorDigitado.replace(",", "."));

                    if (valor <= 0){
                        JOptionPane.showMessageDialog(null, "Digite um valor maior que zero.");
                        return;
                    }

                    int opcao = JOptionPane.showConfirmDialog(null, String.format("Tem certeza que deseja fazer um pix para:\n%s\nNo valor de: R$ %.2f", contaDestino.getTitular().getNome(), valor), "Fazer pix", JOptionPane.YES_NO_OPTION);

                    if (opcao == JOptionPane.YES_OPTION) {
                        boolean sucesso = usuarioLogado.getContaCorrente().pix(contaDestino, valor);
                        if (sucesso){
                            textoSaldo.setText(String.format("R$ %.2f", usuarioLogado.getContaCorrente().getSaldo()));
                            GerenciadorCSV.salvarContas(banco);
                            GerenciadorCSV.salvarExtrato(banco);

                            java.util.List<Transacao> extrato = usuarioLogado.getContaCorrente().getExtrato();
                            Transacao ultimaTransacao = extrato.get(extrato.size() - 1);

                            modeloTabela.addRow(new Object[]{
                                    ultimaTransacao.getData().format(formatadorData),
                                    ultimaTransacao.getTipoOperacao(),
                                    String.format("%.2f", ultimaTransacao.getValor()),
                                    ultimaTransacao.getOrigemDestino()
                            });

                            JOptionPane.showMessageDialog(null, "Transação realizada com sucesso!", "Transação feita!", JOptionPane.INFORMATION_MESSAGE);
                        }else{
                            JOptionPane.showMessageDialog(null, "Saldo insuficiente para o Pix.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnDepositar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String valorDigitado = JOptionPane.showInputDialog(String.format("Seu saldo: R$ %.2f\nDigite o valor que deseja depositar: ", usuarioLogado.getContaCorrente().getSaldo()));

                    if (valorDigitado == null){
                        JOptionPane.showMessageDialog(null, "Digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double valor = Double.parseDouble(valorDigitado.replace(",", "."));

                    if (valor <= 0){
                        JOptionPane.showMessageDialog(null, "Digite um valor maior que zero.");
                        return;
                    }

                    int opcao = JOptionPane.showConfirmDialog(null, String.format("Tem certeza que deseja fazer um depósito de R$ %.2f para sua conta?", valor), "Depositar", JOptionPane.YES_NO_OPTION);

                    if (opcao == JOptionPane.YES_OPTION){
                        boolean sucesso = usuarioLogado.getContaCorrente().depositar(valor);

                        if (sucesso){
                            textoSaldo.setText(String.format("R$ %.2f", usuarioLogado.getContaCorrente().getSaldo()));
                            GerenciadorCSV.salvarContas(banco);
                            GerenciadorCSV.salvarExtrato(banco);

                            java.util.List<Transacao> extrato = usuarioLogado.getContaCorrente().getExtrato();
                            Transacao ultimaTransacao = extrato.get(extrato.size() - 1);

                            modeloTabela.addRow(new Object[]{
                                    ultimaTransacao.getData().format(formatadorData),
                                    ultimaTransacao.getTipoOperacao(),
                                    String.format("%.2f", ultimaTransacao.getValor()),
                                    ultimaTransacao.getOrigemDestino()
                            });

                            JOptionPane.showMessageDialog(null, "Depósito realizado com sucesso!", "Depósito feito!", JOptionPane.INFORMATION_MESSAGE);
                        }else{
                            JOptionPane.showMessageDialog(null, "Algo deu errado, tente novamente daqui a algum tempo", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                } catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnSacar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String valorDigitado = JOptionPane.showInputDialog(String.format("Seu saldo: R$ %.2f\nDigite o valor que deseja retirar: ", usuarioLogado.getContaCorrente().getSaldo()));

                    if (valorDigitado == null) {
                        JOptionPane.showMessageDialog(null, "Digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double valor = Double.parseDouble(valorDigitado.replace(",", "."));

                    if (valor <= 0){
                        JOptionPane.showMessageDialog(null, "Digite um valor maior que zero.");
                        return;
                    }

                    int opcao = JOptionPane.showConfirmDialog(null, String.format("Você tem certeza que deseja retirar de sua conta R$ %.2f (valor + taxa)?", (valor + 1)), "Sacar saldo", JOptionPane.YES_NO_OPTION);

                    if (opcao == JOptionPane.YES_OPTION){
                        boolean sucesso = usuarioLogado.getContaCorrente().sacar(valor);

                        if (sucesso){
                            textoSaldo.setText(String.format("R$ %.2f", usuarioLogado.getContaCorrente().getSaldo()));
                            GerenciadorCSV.salvarContas(banco);
                            GerenciadorCSV.salvarExtrato(banco);

                            java.util.List<Transacao> extrato = usuarioLogado.getContaCorrente().getExtrato();
                            Transacao ultimaTransacao = extrato.get(extrato.size() - 1);

                            modeloTabela.addRow(new Object[]{
                                    ultimaTransacao.getData().format(formatadorData),
                                    ultimaTransacao.getTipoOperacao(),
                                    String.format("%.2f", ultimaTransacao.getValor()),
                                    ultimaTransacao.getOrigemDestino()
                            });

                            JOptionPane.showMessageDialog(null, "Saque realizado com sucesso!", "Saque feito!", JOptionPane.INFORMATION_MESSAGE);
                        }else{
                            JOptionPane.showMessageDialog(null, "Saldo insuficiente para realizar o saque com a taxa.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnGuardarPoupanca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (usuarioLogado.getContaPoupanca() == null) {
                    JOptionPane.showMessageDialog(null, "Você não possui uma conta poupança para passar seu saldo", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    String valorDigitado = JOptionPane.showInputDialog(null, String.format("Seu saldo: R$ %.2f\nQual a quantia que você deseja mandar para sua conta poupança?", usuarioLogado.getContaCorrente().getSaldo()));

                    if (valorDigitado == null) {
                        JOptionPane.showMessageDialog(null, "Digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double valor = Double.parseDouble(valorDigitado.replace(",", "."));

                    if (valor <= 0) {
                        JOptionPane.showMessageDialog(null, "Digite um número válido!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (valor > usuarioLogado.getContaCorrente().getSaldo()) {
                        JOptionPane.showMessageDialog(null, "Você não possui esse saldo", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    boolean sucesso = usuarioLogado.guardarNaPoupanca(valor);

                    if (sucesso){
                        textoSaldo.setText(String.format("R$ %.2f", usuarioLogado.getContaCorrente().getSaldo()));

                        GerenciadorCSV.salvarContas(banco);
                        GerenciadorCSV.salvarExtrato(banco);

                        java.util.List<Transacao> extrato = usuarioLogado.getContaCorrente().getExtrato();
                        Transacao ultimaTransacao = extrato.get(extrato.size() - 1);

                        modeloTabela.addRow(new Object[]{
                                ultimaTransacao.getData().format(formatadorData),
                                ultimaTransacao.getTipoOperacao(),
                                String.format("%.2f", ultimaTransacao.getValor()),
                                ultimaTransacao.getOrigemDestino()
                        });

                        JOptionPane.showMessageDialog(null, String.format("R$ %.2f transferidos para sua Poupança!", valor), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
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
        painelAcoes.add(btnGuardarPoupanca);
        painelContaCorrente.add(painelSaldo);
        painelContaCorrente.add(painelAcoes);
        add(painelContaCorrente, BorderLayout.NORTH);

        modeloTabela = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        modeloTabela.addColumn("Data/Hora");
        modeloTabela.addColumn("Operação");
        modeloTabela.addColumn("Valor (R$)");
        modeloTabela.addColumn("De / Para");

        for (Transacao transacaoAtual : usuarioLogado.getContaCorrente().getExtrato()){
            modeloTabela.addRow(new Object[]{
                    transacaoAtual.getData().format(formatadorData),
                    transacaoAtual.getTipoOperacao(),
                    String.format("%.2f", transacaoAtual.getValor()),
                    transacaoAtual.getOrigemDestino()
            });
        }

        JTable tabelaExtrato = new JTable(modeloTabela);
        tabelaExtrato.setRowHeight(60);

        JScrollPane painelRolagem = new JScrollPane(tabelaExtrato);

        javax.swing.border.TitledBorder bordaExtrato = BorderFactory.createTitledBorder("Histórico de transações");
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

                chavePix = chavePix.trim();

                if (chavePix.contains(";")) {
                    JOptionPane.showMessageDialog(null, "O caractere ';' (ponto e vírgula) não é permitido na chave Pix.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (banco.buscarContaPorPix(chavePix) != null) {
                    JOptionPane.showMessageDialog(null, "Esta chave PIX já está em uso no banco. Escolha outra.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
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
                    JOptionPane.showMessageDialog(null, "Já possui uma conta corrente!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        painelSemUsuario.add(textoCriarConta);
        painelSemUsuario.add(btnCriarConta);
        add(painelSemUsuario, BorderLayout.CENTER);
    }
}