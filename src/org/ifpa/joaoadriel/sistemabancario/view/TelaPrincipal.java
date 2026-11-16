package org.ifpa.joaoadriel.sistemabancario.view;

import org.ifpa.joaoadriel.sistemabancario.domain.Banco;
import org.ifpa.joaoadriel.sistemabancario.domain.GerenciadorCSV;
import org.ifpa.joaoadriel.sistemabancario.domain.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaPrincipal extends JFrame {
    private Banco banco;
    private Usuario usuarioLogado;
    private JPanel painelCima;
    private JPanel painelCentro;
    private JPanel painelBtnCentro;
    private JPanel painelBtnDireita;
    private JPanel painelBtnEsquerda;
    private JButton btnContaCorrente;
    private JButton btnContaPoupanca;
    private JButton btnSairConta;
    private JButton btnDeletarConta;
    private CardLayout controlador;

    public TelaPrincipal(Banco banco, Usuario usuarioLogado) {
        this.banco = banco;
        this.usuarioLogado = usuarioLogado;

        Color azulBanco = new Color(30, 58, 95);
        Color vermelhoAlerta = new Color(220, 53, 69);
        Color fundoClaro = new Color(244, 246, 249);

        setTitle("Sistema Bancário");
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        painelCima = new JPanel();
        painelCima.setLayout(new BorderLayout());
        painelCima.setBackground(azulBanco);
        painelCima.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        painelBtnEsquerda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBtnEsquerda.setOpaque(false);
        painelBtnEsquerda.setPreferredSize(new Dimension(180, 42));

        painelBtnCentro = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        painelBtnCentro.setOpaque(false);

        painelBtnDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBtnDireita.setOpaque(false);
        painelBtnDireita.setPreferredSize(new Dimension(180, 42));

        Font fonteNav = new Font("SansSerif", Font.BOLD, 14);

        btnDeletarConta = new JButton("Deletar Contas");
        btnDeletarConta.setFont(fonteNav);
        btnDeletarConta.setBackground(vermelhoAlerta);
        btnDeletarConta.setForeground(Color.WHITE);
        btnDeletarConta.setFocusPainted(false);

        btnContaCorrente = new JButton("Conta Corrente");
        btnContaCorrente.setFont(fonteNav);
        btnContaCorrente.setBackground(Color.WHITE);
        btnContaCorrente.setForeground(azulBanco);
        btnContaCorrente.setFocusPainted(false);

        btnContaPoupanca = new JButton("Conta Poupança");
        btnContaPoupanca.setFont(fonteNav);
        btnContaPoupanca.setBackground(Color.WHITE);
        btnContaPoupanca.setForeground(azulBanco);
        btnContaPoupanca.setFocusPainted(false);

        btnSairConta = new JButton("Sair da Conta");
        btnSairConta.setFont(fonteNav);
        btnSairConta.setBackground(new Color(226, 232, 240));
        btnSairConta.setForeground(azulBanco);
        btnSairConta.setFocusPainted(false);

        painelBtnEsquerda.add(btnDeletarConta);
        painelBtnCentro.add(btnContaCorrente);
        painelBtnCentro.add(btnContaPoupanca);
        painelBtnDireita.add(btnSairConta);

        painelCima.add(painelBtnEsquerda, BorderLayout.WEST);
        painelCima.add(painelBtnCentro, BorderLayout.CENTER);
        painelCima.add(painelBtnDireita, BorderLayout.EAST);

        add(painelCima, BorderLayout.NORTH);

        controlador = new CardLayout();
        painelCentro = new JPanel(controlador);
        painelCentro.setBackground(fundoClaro);

        TelaPrincipalCorrente telaPrincipalCorrente = new TelaPrincipalCorrente(banco, usuarioLogado, controlador, painelCentro);
        TelaPrincipalPoupanca telaPrincipalPoupanca = new TelaPrincipalPoupanca(banco, usuarioLogado, controlador, painelCentro);

        painelCentro.add(telaPrincipalCorrente, "telaPrincipalCorrente");
        painelCentro.add(telaPrincipalPoupanca, "telaPrincipalPoupanca");

        btnDeletarConta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean temSaldoCorrente = (usuarioLogado.getContaCorrente() != null) && (Math.abs(usuarioLogado.getContaCorrente().getSaldo()) >= 0.01);
                boolean temSaldoPoupanca = (usuarioLogado.getContaPoupanca() != null) && (Math.abs(usuarioLogado.getContaPoupanca().getSaldo()) >= 0.01);

                if (temSaldoCorrente || temSaldoPoupanca) {
                    JOptionPane.showMessageDialog(null, "Você não pode deletar sua conta pois possui saldo positivo ou débito pendente no cheque especial. Zere suas contas antes de prosseguir.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int resp = JOptionPane.showConfirmDialog(null, "Deseja deletar sua conta permanentemente? Esta opção é irreversível!", "Deletar conta", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (resp == JOptionPane.YES_OPTION) {
                    boolean sucesso = banco.deletarUsuarios(usuarioLogado);
                    if (sucesso) {
                        GerenciadorCSV.salvarContas(banco);
                        GerenciadorCSV.salvarExtrato(banco);

                        btnSairConta.doClick();
                    }
                }
            }
        });

        btnContaCorrente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controlador.show(painelCentro, "telaPrincipalCorrente");
            }
        });

        btnContaPoupanca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controlador.show(painelCentro, "telaPrincipalPoupanca");
            }
        });

        btnSairConta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TelaInicial telaInicial = new TelaInicial(banco);
                telaInicial.setVisible(true);

                TelaPrincipal.this.dispose();
            }
        });

        add(painelCentro, BorderLayout.CENTER);
    }
}