package org.ifpa.joaoadriel.sistemabancario.view;

import org.ifpa.joaoadriel.sistemabancario.domain.Banco;
import org.ifpa.joaoadriel.sistemabancario.domain.GerenciadorCSV;
import org.ifpa.joaoadriel.sistemabancario.domain.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TelaConfiguracaoConta extends JPanel{
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

    public void receberUsuario(Usuario usuario){
        this.usuarioEmAndamento = usuario;
    }

    public TelaConfiguracaoConta(Banco banco, CardLayout controlador, JPanel painelCentro){
        this.banco = banco;
        this.controlador = controlador;
        this.painelCentro = painelCentro;

        setLayout(new GridBagLayout());

        painelFormulario = new JPanel();
        painelFormulario.setLayout(new BoxLayout(painelFormulario, BoxLayout.Y_AXIS));
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Tela de Cadastro"));

        JLabel tipoConta = new JLabel("Selecione o tipo de conta que deseja criar: ");
        tipoConta.setAlignmentX(Component.CENTER_ALIGNMENT);

        radioContaCorrente = new JRadioButton("Conta Corrente");
        radioContaCorrente.setAlignmentX(Component.CENTER_ALIGNMENT);
        radioContaCorrente.setSelected(true);

        radioContaPoupanca = new JRadioButton("Conta Poupança");
        radioContaPoupanca.setAlignmentX(Component.CENTER_ALIGNMENT);

        ButtonGroup grupoRadioTipoConta = new ButtonGroup();
        grupoRadioTipoConta.add(radioContaCorrente);
        grupoRadioTipoConta.add(radioContaPoupanca);

        JLabel labelChavePix = new JLabel("Crie uma chave pix para você: ");
        labelChavePix.setAlignmentX(Component.CENTER_ALIGNMENT);

        chavePix = new JTextField(15);
        chavePix.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout());

        botaoCriarConta = new JButton("Criar conta");
        botaoVoltar = new JButton("Voltar");

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
                String chavePixDigitada = chavePix.getText();

                if (chavePixDigitada.isBlank()){
                    JOptionPane.showMessageDialog(null, "[ERRO] Você não criou uma chave pix!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (radioContaCorrente.isSelected()){
                    usuarioEmAndamento.abrirContaCorrente(chavePixDigitada, 0.0);
                }else if (radioContaPoupanca.isSelected()){
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
        painelFormulario.add(radioContaCorrente);
        painelFormulario.add(radioContaPoupanca);
        painelFormulario.add(labelChavePix);
        painelFormulario.add(chavePix);
        painelFormulario.add(painelBotoes);
        add(painelFormulario);
    }

    private void limparDados(){
        chavePix.setText("");
        radioContaCorrente.setSelected(true);
    }
}
