package org.ifpa.joaoadriel.sistemabancario.view;

import org.ifpa.joaoadriel.sistemabancario.domain.Banco;
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
    private JPanel painelFantasmaEsquerda;
    private JButton btnContaCorrente;
    private JButton btnContaPoupanca;
    private JButton btnSairConta;
    private CardLayout controlador;

    public TelaPrincipal(Banco banco, Usuario usuarioLogado) {
        this.banco = banco;
        this.usuarioLogado = usuarioLogado;

        setTitle("Sistema Bancário");
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        painelCima = new JPanel();
        painelCima.setLayout(new BorderLayout());
        painelCima.setBackground(Color.DARK_GRAY);

        painelBtnCentro = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBtnCentro.setOpaque(false);

        painelBtnDireita = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBtnDireita.setOpaque(false);
        painelBtnDireita.setPreferredSize(new Dimension(150, 40));

        painelFantasmaEsquerda = new JPanel();
        painelFantasmaEsquerda.setOpaque(false);
        painelFantasmaEsquerda.setPreferredSize(new Dimension(150, 40));

        btnContaCorrente = new JButton("Conta Corrente");
        btnContaPoupanca = new JButton("Conta Poupança");
        btnSairConta = new JButton("Sair da sua conta");

        painelBtnCentro.add(btnContaCorrente);
        painelBtnCentro.add(btnContaPoupanca);
        painelBtnCentro.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelBtnDireita.add(btnSairConta);

        painelCima.add(painelFantasmaEsquerda, BorderLayout.WEST);
        painelCima.add(painelBtnCentro, BorderLayout.CENTER);
        painelCima.add(painelBtnDireita, BorderLayout.EAST);

        add(painelCima, BorderLayout.NORTH);

        TelaPrincipalCorrente telaPrincipalCorrente = new TelaPrincipalCorrente(banco, usuarioLogado, controlador, painelCentro);
        TelaPrincipalPoupanca telaPrincipalPoupanca = new TelaPrincipalPoupanca(banco, usuarioLogado, controlador, painelCentro);

        controlador = new CardLayout();
        painelCentro = new JPanel(controlador);

        painelCentro.add(telaPrincipalCorrente, "telaPrincipalCorrente");
        painelCentro.add(telaPrincipalPoupanca, "telaPrincipalPoupanca");
        painelCentro.setAlignmentX(Component.CENTER_ALIGNMENT);

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
