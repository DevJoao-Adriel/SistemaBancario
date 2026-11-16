package org.ifpa.joaoadriel.sistemabancario.view;

import org.ifpa.joaoadriel.sistemabancario.domain.Banco;
import org.ifpa.joaoadriel.sistemabancario.domain.GerenciadorCSV;
import org.ifpa.joaoadriel.sistemabancario.domain.Usuario;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaConfiguracaoConta extends JPanel {
    private Banco banco;
    private CardLayout controlador;
    private JPanel painelCentro;
    private Usuario usuarioEmAndamento;
    private JPanel painelFormulario;
    private JPanel painelBotoes;
    private JRadioButton radioContaCorrente;
    private JRadioButton radioContaPoupanca;
    private JTextField chavePix;
    private JButton botaoCriarConta;
    private JButton botaoVoltar;

    public void receberUsuario(Usuario usuario) {
        this.usuarioEmAndamento = usuario;
    }

    public TelaConfiguracaoConta(Banco banco, CardLayout controlador, JPanel painelCentro) {
        this.banco = banco;
        this.controlador = controlador;
        this.painelCentro = painelCentro;

        Color azulBanco = new Color(30, 58, 95);
        Color fundoClaro = new Color(244, 246, 249);
        Color verdeAcao = new Color(25, 135, 84);
        Color textoEscuro = new Color(45, 55, 72);

        setLayout(new GridBagLayout());
        setBackground(fundoClaro);

        Font fonteGeral = new Font("SansSerif", Font.PLAIN, 15);
        Font fonteBotao = new Font("SansSerif", Font.BOLD, 14);

        painelFormulario = new JPanel();
        painelFormulario.setLayout(new BoxLayout(painelFormulario, BoxLayout.Y_AXIS));
        painelFormulario.setBackground(Color.WHITE);

        TitledBorder bordaTitulo = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                "Configuração da Conta"
        );
        bordaTitulo.setTitleFont(new Font("SansSerif", Font.BOLD, 18));
        bordaTitulo.setTitleColor(azulBanco);

        painelFormulario.setBorder(BorderFactory.createCompoundBorder(
                bordaTitulo,
                BorderFactory.createEmptyBorder(25, 45, 25, 45)
        ));

        JLabel tipoConta = new JLabel("Selecione o tipo de conta:");
        tipoConta.setFont(fonteGeral);
        tipoConta.setForeground(textoEscuro);
        tipoConta.setAlignmentX(Component.CENTER_ALIGNMENT);

        radioContaCorrente = new JRadioButton("Conta Corrente");
        radioContaCorrente.setFont(fonteGeral);
        radioContaCorrente.setBackground(Color.WHITE);
        radioContaCorrente.setForeground(textoEscuro);
        radioContaCorrente.setAlignmentX(Component.CENTER_ALIGNMENT);
        radioContaCorrente.setSelected(true);

        radioContaPoupanca = new JRadioButton("Conta Poupança");
        radioContaPoupanca.setFont(fonteGeral);
        radioContaPoupanca.setBackground(Color.WHITE);
        radioContaPoupanca.setForeground(textoEscuro);
        radioContaPoupanca.setAlignmentX(Component.CENTER_ALIGNMENT);

        ButtonGroup grupoRadioTipoConta = new ButtonGroup();
        grupoRadioTipoConta.add(radioContaCorrente);
        grupoRadioTipoConta.add(radioContaPoupanca);

        JLabel labelChavePix = new JLabel("Crie sua chave Pix:");
        labelChavePix.setFont(fonteGeral);
        labelChavePix.setForeground(textoEscuro);
        labelChavePix.setAlignmentX(Component.CENTER_ALIGNMENT);

        chavePix = new JTextField(15);
        chavePix.setFont(fonteGeral);
        chavePix.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        painelBotoes.setBackground(Color.WHITE);

        botaoCriarConta = new JButton("Finalizar Cadastro");
        botaoCriarConta.setFont(fonteBotao);
        botaoCriarConta.setBackground(verdeAcao);
        botaoCriarConta.setForeground(Color.WHITE);
        botaoCriarConta.setFocusPainted(false);

        botaoVoltar = new JButton("Voltar");
        botaoVoltar.setFont(fonteBotao);
        botaoVoltar.setBackground(new Color(226, 232, 240));
        botaoVoltar.setForeground(textoEscuro);
        botaoVoltar.setFocusPainted(false);

        painelBotoes.add(botaoCriarConta);
        painelBotoes.add(botaoVoltar);

        chavePix.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                botaoCriarConta.doClick();
            }
        });

        botaoCriarConta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chavePixDigitada = chavePix.getText().trim();

                if (chavePixDigitada.isBlank()) {
                    JOptionPane.showMessageDialog(null, "[ERRO] Você não criou uma chave pix!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (chavePixDigitada.contains(";")) {
                    JOptionPane.showMessageDialog(null, "O caractere ';' (ponto e vírgula) não é permitido na chave Pix.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (banco.buscarContaPorPix(chavePixDigitada) != null) {
                    JOptionPane.showMessageDialog(null, "Esta chave PIX já está em uso no banco. Escolha outra.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (radioContaCorrente.isSelected()) {
                    usuarioEmAndamento.abrirContaCorrente(chavePixDigitada, 0.0);
                } else if (radioContaPoupanca.isSelected()) {
                    usuarioEmAndamento.abrirContaPoupanca(chavePixDigitada, 0.0);
                }
                banco.cadastrarUsuarios(usuarioEmAndamento);
                GerenciadorCSV.salvarContas(banco);
                JOptionPane.showMessageDialog(null, "Conta criada com sucesso! Faça seu login.", "Tela Cadastro", JOptionPane.INFORMATION_MESSAGE);
                limparDados();
                controlador.show(painelCentro, "telaLogin");
            }
        });

        botaoVoltar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limparDados();
                controlador.show(painelCentro, "telaCadastro");
            }
        });

        painelFormulario.add(tipoConta);
        painelFormulario.add(Box.createRigidArea(new Dimension(0, 10)));
        painelFormulario.add(radioContaCorrente);
        painelFormulario.add(radioContaPoupanca);
        painelFormulario.add(Box.createRigidArea(new Dimension(0, 18)));
        painelFormulario.add(labelChavePix);
        painelFormulario.add(Box.createRigidArea(new Dimension(0, 5)));
        painelFormulario.add(chavePix);
        painelFormulario.add(Box.createRigidArea(new Dimension(0, 22)));
        painelFormulario.add(painelBotoes);
        add(painelFormulario);
    }

    private void limparDados() {
        chavePix.setText("");
        radioContaCorrente.setSelected(true);
    }
}