package org.ifpa.joaoadriel.sistemabancario.view;

import org.ifpa.joaoadriel.sistemabancario.domain.Banco;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaInicial extends JFrame {
    private Banco banco;
    private JPanel painelCima;
    private JButton botaoLogin;
    private JButton botaoCadastro;
    private CardLayout controlador;
    private JPanel painelCentro;

    public TelaInicial(Banco banco) {
        this.banco = banco;

        Color azulBanco = new Color(30, 58, 95);
        Color fundoClaro = new Color(244, 246, 249);
        Color verdeAcao = new Color(25, 135, 84);

        setTitle("Sistema Bancário");
        setSize(680, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        painelCima = new JPanel();
        painelCima.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 12));
        painelCima.setBackground(azulBanco);

        botaoLogin = new JButton("Acessar Conta");
        botaoLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        botaoLogin.setBackground(Color.WHITE);
        botaoLogin.setForeground(azulBanco);
        botaoLogin.setFocusPainted(false);

        botaoCadastro = new JButton("Criar Nova Conta");
        botaoCadastro.setFont(new Font("SansSerif", Font.BOLD, 14));
        botaoCadastro.setBackground(verdeAcao);
        botaoCadastro.setForeground(Color.WHITE);
        botaoCadastro.setFocusPainted(false);

        painelCima.add(botaoLogin);
        painelCima.add(botaoCadastro);

        add(painelCima, BorderLayout.NORTH);

        controlador = new CardLayout();
        painelCentro = new JPanel(controlador);
        painelCentro.setBackground(fundoClaro);

        TelaLogin telaLogin = new TelaLogin(banco);
        TelaConfiguracaoConta telaConfiguracaoConta = new TelaConfiguracaoConta(banco, controlador, painelCentro);
        TelaCadastro telaCadastro = new TelaCadastro(banco, controlador, painelCentro, telaConfiguracaoConta);

        painelCentro.add(telaLogin, "telaLogin");
        painelCentro.add(telaCadastro, "telaCadastro");
        painelCentro.add(telaConfiguracaoConta, "telaConfiguracaoConta");

        add(painelCentro, BorderLayout.CENTER);

        this.botaoLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controlador.show(painelCentro, "telaLogin");
            }
        });

        this.botaoCadastro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controlador.show(painelCentro, "telaCadastro");
            }
        });

        setVisible(true);
    }
}