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
    private JButton btnContaCorrente;
    private JButton btnContaPoupanca;
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
        painelCima.setLayout(new FlowLayout());
        painelCima.setBackground(Color.DARK_GRAY);

        btnContaCorrente = new JButton("Conta Corrente");
        btnContaPoupanca = new JButton("Conta Poupança");

        painelCima.add(btnContaCorrente);
        painelCima.add(btnContaPoupanca);
        painelCima.setAlignmentX(Component.CENTER_ALIGNMENT);

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
        add(painelCentro, BorderLayout.CENTER);
    }
}
