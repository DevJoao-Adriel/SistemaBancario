package org.ifpa.joaoadriel.sistemabancario.view;

import org.ifpa.joaoadriel.sistemabancario.domain.Banco;
import org.ifpa.joaoadriel.sistemabancario.domain.GerenciadorCSV;
import org.ifpa.joaoadriel.sistemabancario.domain.Usuario;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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

        Font fonteGeral = new Font("Arial", Font.PLAIN, 16);
        Font fonteBotao = new Font("Arial", Font.BOLD, 14);

        painelFormulario = new JPanel();
        painelFormulario.setLayout(new BoxLayout(painelFormulario, BoxLayout.Y_AXIS));

        TitledBorder bordaTitulo = BorderFactory.createTitledBorder("Configuração da Conta");
        bordaTitulo.setTitleFont(new Font("Arial", Font.BOLD, 18));
        painelFormulario.setBorder(BorderFactory.createCompoundBorder(
                bordaTitulo,
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        JLabel tipoConta = new JLabel("Selecione o tipo de conta que deseja criar: ");
        tipoConta.setFont(fonteGeral);
        tipoConta.setAlignmentX(Component.CENTER_ALIGNMENT);

        radioContaCorrente = new JRadioButton("Conta Corrente");
        radioContaCorrente.setFont(fonteGeral);
        radioContaCorrente.setAlignmentX(Component.CENTER_ALIGNMENT);
        radioContaCorrente.setSelected(true);

        radioContaPoupanca = new JRadioButton("Conta Poupança");
        radioContaPoupanca.setFont(fonteGeral);
        radioContaPoupanca.setAlignmentX(Component.CENTER_ALIGNMENT);

        ButtonGroup grupoRadioTipoConta = new ButtonGroup();
        grupoRadioTipoConta.add(radioContaCorrente);
        grupoRadioTipoConta.add(radioContaPoupanca);

        JLabel labelChavePix = new JLabel("Crie uma chave pix para você: ");
        labelChavePix.setFont(fonteGeral);
        labelChavePix.setAlignmentX(Component.CENTER_ALIGNMENT);

        chavePix = new JTextField(15);
        chavePix.setFont(fonteGeral);
        chavePix.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));

        botaoCriarConta = new JButton("Criar conta");
        botaoCriarConta.setFont(fonteBotao);
        botaoVoltar = new JButton("Voltar");
        botaoVoltar.setFont(fonteBotao);

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

                if (banco.buscarContaPorPix(chavePixDigitada) != null) {
                    JOptionPane.showMessageDialog(null, "Esta chave PIX já está em uso no banco. Escolha outra.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
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
        painelFormulario.add(Box.createRigidArea(new Dimension(0, 10)));
        painelFormulario.add(radioContaCorrente);
        painelFormulario.add(radioContaPoupanca);
        painelFormulario.add(Box.createRigidArea(new Dimension(0, 20)));
        painelFormulario.add(labelChavePix);
        painelFormulario.add(Box.createRigidArea(new Dimension(0, 5)));
        painelFormulario.add(chavePix);
        painelFormulario.add(Box.createRigidArea(new Dimension(0, 25)));
        painelFormulario.add(painelBotoes);
        add(painelFormulario);
    }

    private void limparDados(){
        chavePix.setText("");
        radioContaCorrente.setSelected(true);
    }
}