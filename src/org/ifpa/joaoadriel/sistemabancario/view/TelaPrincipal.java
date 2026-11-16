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

        setTitle("Sistema Bancário");
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        painelCima = new JPanel();
        painelCima.setLayout(new BorderLayout());
        painelCima.setBackground(Color.DARK_GRAY);

        painelBtnEsquerda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBtnEsquerda.setOpaque(false);
        painelBtnEsquerda.setPreferredSize(new Dimension(160, 40));

        painelBtnCentro = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBtnCentro.setOpaque(false);

        painelBtnDireita = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelBtnDireita.setOpaque(false);
        painelBtnDireita.setPreferredSize(new Dimension(160, 40));

        btnDeletarConta = new JButton("Deletar Contas");
        btnContaCorrente = new JButton("Conta Corrente");
        btnContaPoupanca = new JButton("Conta Poupança");
        btnSairConta = new JButton("Sair da sua conta");

        btnDeletarConta.setBackground(new Color(200, 40, 40));
        btnDeletarConta.setForeground(Color.WHITE);
        btnDeletarConta.setFocusPainted(false);
        btnDeletarConta.setOpaque(true);

        painelBtnEsquerda.add(btnDeletarConta);

        painelBtnCentro.add(btnContaCorrente);
        painelBtnCentro.add(btnContaPoupanca);

        painelBtnDireita.add(btnSairConta);

        painelCima.add(painelBtnEsquerda, BorderLayout.WEST);
        painelCima.add(painelBtnCentro, BorderLayout.CENTER);
        painelCima.add(painelBtnDireita, BorderLayout.EAST);

        add(painelCima, BorderLayout.NORTH);

        // Instancia o controlador e o painelCentro ANTES de passá-los para as telas filhas
        controlador = new CardLayout();
        painelCentro = new JPanel(controlador);

        TelaPrincipalCorrente telaPrincipalCorrente = new TelaPrincipalCorrente(banco, usuarioLogado, controlador, painelCentro);
        TelaPrincipalPoupanca telaPrincipalPoupanca = new TelaPrincipalPoupanca(banco, usuarioLogado, controlador, painelCentro);

        painelCentro.add(telaPrincipalCorrente, "telaPrincipalCorrente");
        painelCentro.add(telaPrincipalPoupanca, "telaPrincipalPoupanca");

        btnDeletarConta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (((usuarioLogado.getContaCorrente() != null) && (usuarioLogado.getContaCorrente().getSaldo() > 0)) || ((usuarioLogado.getContaPoupanca() != null) && (usuarioLogado.getContaPoupanca().getSaldo() > 0))){
                    JOptionPane.showMessageDialog(null, "Você não pode fazer essa operação, você possui saldo em alguma das suas contas, saque ou transfira seu saldo para poder deletar sua conta", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int resp = JOptionPane.showConfirmDialog(null, "Deseja deletar sua conta permanentemente? esta opção é irreversível!", "Deletar conta", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (resp == JOptionPane.YES_OPTION){

                    boolean sucesso = banco.deletarUsuarios(usuarioLogado);
                    if (sucesso){
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