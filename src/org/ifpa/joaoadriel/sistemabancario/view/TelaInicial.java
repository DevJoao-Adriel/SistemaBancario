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

    public TelaInicial (Banco banco){
        this.banco = banco;

        setTitle("Sistema bancário");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        painelCima = new JPanel();
        painelCima.setLayout(new FlowLayout());
        painelCima.setBackground(Color.DARK_GRAY);
        botaoLogin = new JButton("Acessar Conta");
        botaoCadastro = new JButton("Criar Nova Conta");

        painelCima.add(botaoLogin);
        painelCima.add(botaoCadastro);

        add(painelCima, BorderLayout.NORTH);

        controlador = new CardLayout();
        painelCentro = new JPanel(controlador);

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
                controlador.show(painelCentro, "telaCadastro");}
        });

        setVisible(true);
    }
}
