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

    private final Color azulBanco = new Color(30, 58, 95);
    private final Color fundoClaro = new Color(244, 246, 249);
    private final Color verdePositivo = new Color(25, 135, 84);
    private final Color vermelhoNegativo = new Color(220, 53, 69);

    public TelaPrincipalCorrente(Banco banco, Usuario usuarioLogado, CardLayout controlador, JPanel painelCentro) {
        this.banco = banco;
        setLayout(new BorderLayout());
        setBackground(fundoClaro);

        if (usuarioLogado.getContaCorrente() != null) {
            desenharPainelComConta(usuarioLogado);
        } else {
            desenharPainelSemConta(usuarioLogado);
        }

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                removeAll();

                if (usuarioLogado.getContaCorrente() != null) {
                    desenharPainelComConta(usuarioLogado);
                } else {
                    desenharPainelSemConta(usuarioLogado);
                }

                revalidate();
                repaint();
            }
        });
    }

    private void estilizarBotaoAcao(JButton botao, Color corFundo, Color corTexto) {
        botao.setFont(new Font("SansSerif", Font.BOLD, 13));
        botao.setBackground(corFundo);
        botao.setForeground(corTexto);
        botao.setFocusPainted(false);
    }

    private void atualizarExibicaoSaldo(ContaCorrente cc, JLabel textoSaldo, JLabel textoLimite) {
        double saldoAtual = cc.getSaldo();
        textoSaldo.setText(String.format("R$ %.2f", saldoAtual));

        if (saldoAtual < 0) {
            textoSaldo.setForeground(vermelhoNegativo);
        } else {
            textoSaldo.setForeground(verdePositivo);
        }

        textoLimite.setText(String.format("Cheque Especial Disponível: R$ %.2f (Limite Total: R$ %.2f)",
                cc.getChequeEspecialRestante(), cc.getLimiteChequeEspecial()));
    }

    private void desenharPainelComConta(Usuario usuarioLogado) {
        DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        ContaCorrente cc = usuarioLogado.getContaCorrente();

        painelContaCorrente = new JPanel();
        painelContaCorrente.setLayout(new BoxLayout(painelContaCorrente, BoxLayout.Y_AXIS));
        painelContaCorrente.setBackground(fundoClaro);
        painelContaCorrente.setBorder(BorderFactory.createEmptyBorder(15, 25, 10, 25));

        JLabel tituloBoasVindas = new JLabel("Olá, " + usuarioLogado.getNome() + " — Conta Corrente");
        tituloBoasVindas.setFont(new Font("SansSerif", Font.BOLD, 18));
        tituloBoasVindas.setForeground(azulBanco);
        tituloBoasVindas.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelSaldo = new JPanel();
        painelSaldo.setLayout(new BoxLayout(painelSaldo, BoxLayout.Y_AXIS));
        painelSaldo.setBackground(Color.WHITE);

        javax.swing.border.TitledBorder bordaSaldo = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                "Saldo em Conta"
        );
        bordaSaldo.setTitleFont(new Font("SansSerif", Font.BOLD, 14));
        bordaSaldo.setTitleColor(azulBanco);

        painelSaldo.setBorder(bordaSaldo);
        painelSaldo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelSaldo.setPreferredSize(new Dimension(Integer.MAX_VALUE, 105));
        painelSaldo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        JLabel textoSaldo = new JLabel();
        textoSaldo.setFont(new Font("SansSerif", Font.BOLD, 34));
        textoSaldo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textoLimite = new JLabel();
        textoLimite.setFont(new Font("SansSerif", Font.PLAIN, 14));
        textoLimite.setForeground(new Color(100, 116, 139));
        textoLimite.setAlignmentX(Component.CENTER_ALIGNMENT);

        atualizarExibicaoSaldo(cc, textoSaldo, textoLimite);

        painelAcoes = new JPanel();
        painelAcoes.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 12));
        painelAcoes.setBackground(fundoClaro);
        painelAcoes.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnDepositar = new JButton("Depositar");
        btnSacar = new JButton("Sacar (taxa R$ 1,00)");
        btnPix = new JButton("Fazer Pix");
        btnGuardarPoupanca = new JButton("Guardar na Poupança");

        estilizarBotaoAcao(btnDepositar, verdePositivo, Color.WHITE);
        estilizarBotaoAcao(btnSacar, azulBanco, Color.WHITE);
        estilizarBotaoAcao(btnPix, azulBanco, Color.WHITE);
        estilizarBotaoAcao(btnGuardarPoupanca, new Color(13, 110, 253), Color.WHITE);

        btnPix.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chavePix = JOptionPane.showInputDialog("Insira a chave pix de quem você quer pagar: ");

                if ((chavePix == null) || (chavePix.isBlank())) {
                    JOptionPane.showMessageDialog(null, "Digite uma chave pix válida!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Conta contaDestino = banco.buscarContaPorPix(chavePix);

                if (contaDestino == null) {
                    JOptionPane.showMessageDialog(null, "Usuário não encontrado!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (contaDestino.getTitular().getCpf().equals(usuarioLogado.getCpf())) {
                    JOptionPane.showMessageDialog(null, "Você não pode enviar um pix para si mesmo!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    String valorDigitado = JOptionPane.showInputDialog(String.format(
                            "Seu saldo: R$ %.2f\nDisponível (com Cheque Especial): R$ %.2f\nDigite o valor do pix: ",
                            cc.getSaldo(), cc.getSaldoDisponivel()));

                    if (valorDigitado == null) {
                        JOptionPane.showMessageDialog(null, "Digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double valor = Double.parseDouble(valorDigitado.replace(",", "."));

                    if (valor <= 0) {
                        JOptionPane.showMessageDialog(null, "Digite um valor maior que zero.");
                        return;
                    }

                    int opcao = JOptionPane.showConfirmDialog(null, String.format("Tem certeza que deseja fazer um pix para:\n%s\nNo valor de: R$ %.2f", contaDestino.getTitular().getNome(), valor), "Fazer pix", JOptionPane.YES_NO_OPTION);

                    if (opcao == JOptionPane.YES_OPTION) {
                        boolean sucesso = cc.pix(contaDestino, valor);
                        if (sucesso) {
                            atualizarExibicaoSaldo(cc, textoSaldo, textoLimite);
                            GerenciadorCSV.salvarContas(banco);
                            GerenciadorCSV.salvarExtrato(banco);

                            java.util.List<Transacao> extrato = cc.getExtrato();
                            Transacao ultimaTransacao = extrato.get(extrato.size() - 1);

                            modeloTabela.addRow(new Object[]{
                                    ultimaTransacao.getData().format(formatadorData),
                                    ultimaTransacao.getTipoOperacao(),
                                    String.format("%.2f", ultimaTransacao.getValor()),
                                    ultimaTransacao.getOrigemDestino()
                            });

                            JOptionPane.showMessageDialog(null, "Transação realizada com sucesso!", "Transação feita!", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Saldo e limite de cheque especial insuficientes para o Pix.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnDepositar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String valorDigitado = JOptionPane.showInputDialog(String.format("Seu saldo: R$ %.2f\nDigite o valor que deseja depositar: ", cc.getSaldo()));

                    if (valorDigitado == null) {
                        JOptionPane.showMessageDialog(null, "Digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double valor = Double.parseDouble(valorDigitado.replace(",", "."));

                    if (valor <= 0) {
                        JOptionPane.showMessageDialog(null, "Digite um valor maior que zero.");
                        return;
                    }

                    int opcao = JOptionPane.showConfirmDialog(null, String.format("Tem certeza que deseja fazer um depósito de R$ %.2f para sua conta?", valor), "Depositar", JOptionPane.YES_NO_OPTION);

                    if (opcao == JOptionPane.YES_OPTION) {
                        boolean sucesso = cc.depositar(valor);

                        if (sucesso) {
                            atualizarExibicaoSaldo(cc, textoSaldo, textoLimite);
                            GerenciadorCSV.salvarContas(banco);
                            GerenciadorCSV.salvarExtrato(banco);

                            java.util.List<Transacao> extrato = cc.getExtrato();
                            Transacao ultimaTransacao = extrato.get(extrato.size() - 1);

                            modeloTabela.addRow(new Object[]{
                                    ultimaTransacao.getData().format(formatadorData),
                                    ultimaTransacao.getTipoOperacao(),
                                    String.format("%.2f", ultimaTransacao.getValor()),
                                    ultimaTransacao.getOrigemDestino()
                            });

                            JOptionPane.showMessageDialog(null, "Depósito realizado com sucesso!", "Depósito feito!", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Algo deu errado, tente novamente daqui a algum tempo", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnSacar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String valorDigitado = JOptionPane.showInputDialog(String.format(
                            "Seu saldo: R$ %.2f\nDisponível (com Cheque Especial): R$ %.2f\nDigite o valor que deseja retirar: ",
                            cc.getSaldo(), cc.getSaldoDisponivel()));

                    if (valorDigitado == null) {
                        JOptionPane.showMessageDialog(null, "Digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double valor = Double.parseDouble(valorDigitado.replace(",", "."));

                    if (valor <= 0) {
                        JOptionPane.showMessageDialog(null, "Digite um valor maior que zero.");
                        return;
                    }

                    int opcao = JOptionPane.showConfirmDialog(null, String.format("Você tem certeza que deseja retirar de sua conta R$ %.2f (valor + taxa)?", (valor + 1)), "Sacar saldo", JOptionPane.YES_NO_OPTION);

                    if (opcao == JOptionPane.YES_OPTION) {
                        boolean sucesso = cc.sacar(valor);

                        if (sucesso) {
                            atualizarExibicaoSaldo(cc, textoSaldo, textoLimite);
                            GerenciadorCSV.salvarContas(banco);
                            GerenciadorCSV.salvarExtrato(banco);

                            java.util.List<Transacao> extrato = cc.getExtrato();
                            Transacao ultimaTransacao = extrato.get(extrato.size() - 1);

                            modeloTabela.addRow(new Object[]{
                                    ultimaTransacao.getData().format(formatadorData),
                                    ultimaTransacao.getTipoOperacao(),
                                    String.format("%.2f", ultimaTransacao.getValor()),
                                    ultimaTransacao.getOrigemDestino()
                            });

                            JOptionPane.showMessageDialog(null, "Saque realizado com sucesso!", "Saque feito!", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Saldo e limite de cheque especial insuficientes para realizar o saque com a taxa.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (NumberFormatException ex) {
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
                if (cc.getSaldo() <= 0) {
                    JOptionPane.showMessageDialog(null, "Você não pode transferir o limite do cheque especial para a poupança. Apenas saldo positivo próprio.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    String valorDigitado = JOptionPane.showInputDialog(null, String.format("Seu saldo próprio: R$ %.2f\nQual a quantia que você deseja mandar para sua conta poupança?", cc.getSaldo()));

                    if (valorDigitado == null) {
                        JOptionPane.showMessageDialog(null, "Digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    double valor = Double.parseDouble(valorDigitado.replace(",", "."));

                    if (valor <= 0) {
                        JOptionPane.showMessageDialog(null, "Digite um número válido!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (valor > cc.getSaldo()) {
                        JOptionPane.showMessageDialog(null, "Você não possui esse saldo próprio (não é permitido usar cheque especial para investir na poupança).", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    boolean sucesso = usuarioLogado.guardarNaPoupanca(valor);

                    if (sucesso) {
                        atualizarExibicaoSaldo(cc, textoSaldo, textoLimite);

                        GerenciadorCSV.salvarContas(banco);
                        GerenciadorCSV.salvarExtrato(banco);

                        java.util.List<Transacao> extrato = cc.getExtrato();
                        Transacao ultimaTransacao = extrato.get(extrato.size() - 1);

                        modeloTabela.addRow(new Object[]{
                                ultimaTransacao.getData().format(formatadorData),
                                ultimaTransacao.getTipoOperacao(),
                                String.format("%.2f", ultimaTransacao.getValor()),
                                ultimaTransacao.getOrigemDestino()
                        });

                        JOptionPane.showMessageDialog(null, String.format("R$ %.2f transferidos para sua Poupança!", valor), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Por favor, digite um valor numérico válido.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        painelSaldo.add(Box.createVerticalGlue());
        painelSaldo.add(textoSaldo);
        painelSaldo.add(Box.createRigidArea(new Dimension(0, 4)));
        painelSaldo.add(textoLimite);
        painelSaldo.add(Box.createVerticalGlue());

        painelAcoes.add(btnDepositar);
        painelAcoes.add(btnSacar);
        painelAcoes.add(btnPix);
        painelAcoes.add(btnGuardarPoupanca);

        painelContaCorrente.add(tituloBoasVindas);
        painelContaCorrente.add(Box.createRigidArea(new Dimension(0, 12)));
        painelContaCorrente.add(painelSaldo);
        painelContaCorrente.add(Box.createRigidArea(new Dimension(0, 5)));
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

        for (Transacao transacaoAtual : cc.getExtrato()) {
            modeloTabela.addRow(new Object[]{
                    transacaoAtual.getData().format(formatadorData),
                    transacaoAtual.getTipoOperacao(),
                    String.format("%.2f", transacaoAtual.getValor()),
                    transacaoAtual.getOrigemDestino()
            });
        }

        JTable tabelaExtrato = new JTable(modeloTabela);
        tabelaExtrato.setRowHeight(36);
        tabelaExtrato.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabelaExtrato.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tabelaExtrato.getTableHeader().setBackground(azulBanco);
        tabelaExtrato.getTableHeader().setForeground(Color.WHITE);

        JScrollPane painelRolagem = new JScrollPane(tabelaExtrato);
        painelRolagem.setBackground(Color.WHITE);

        javax.swing.border.TitledBorder bordaExtrato = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                "Histórico de Transações"
        );
        bordaExtrato.setTitleFont(new Font("SansSerif", Font.BOLD, 15));
        bordaExtrato.setTitleColor(azulBanco);

        JPanel containerTabela = new JPanel(new BorderLayout());
        containerTabela.setBackground(fundoClaro);
        containerTabela.setBorder(BorderFactory.createEmptyBorder(0, 25, 20, 25));
        painelRolagem.setBorder(bordaExtrato);
        containerTabela.add(painelRolagem, BorderLayout.CENTER);

        add(containerTabela, BorderLayout.CENTER);
    }

    private void desenharPainelSemConta(Usuario usuarioLogado) {
        painelSemUsuario = new JPanel();
        painelSemUsuario.setLayout(new BoxLayout(painelSemUsuario, BoxLayout.Y_AXIS));
        painelSemUsuario.setBackground(Color.WHITE);

        javax.swing.border.TitledBorder borda = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                "Conta Corrente não ativada"
        );
        borda.setTitleFont(new Font("SansSerif", Font.BOLD, 16));
        borda.setTitleColor(azulBanco);

        painelSemUsuario.setBorder(BorderFactory.createCompoundBorder(
                borda,
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        JLabel textoCriarConta = new JLabel("Olá, " + usuarioLogado.getNome() + "! Você ainda não possui uma Conta Corrente.");
        textoCriarConta.setFont(new Font("SansSerif", Font.PLAIN, 15));
        textoCriarConta.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnCriarConta = new JButton("Abrir Conta Corrente Agora");
        estilizarBotaoAcao(btnCriarConta, verdePositivo, Color.WHITE);
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

                    JOptionPane.showMessageDialog(null, "Conta Corrente criada com sucesso! Você ganhou R$ 500,00 de limite no Cheque Especial.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

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
        painelSemUsuario.add(Box.createRigidArea(new Dimension(0, 15)));
        painelSemUsuario.add(btnCriarConta);

        JPanel centralizador = new JPanel(new GridBagLayout());
        centralizador.setBackground(fundoClaro);
        centralizador.add(painelSemUsuario);
        add(centralizador, BorderLayout.CENTER);
    }
}