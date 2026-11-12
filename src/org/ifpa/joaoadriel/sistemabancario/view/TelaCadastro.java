package org.ifpa.joaoadriel.sistemabancario.view;

import org.ifpa.joaoadriel.sistemabancario.domain.Banco;
import org.ifpa.joaoadriel.sistemabancario.domain.GerenciadorCSV;
import org.ifpa.joaoadriel.sistemabancario.domain.Usuario;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

public class TelaCadastro extends JPanel{
    private Banco banco;
    private CardLayout controlador;
    private JPanel painelCentro;
    private TelaConfiguracaoConta telaConfiguracaoConta;
    private JTextField nome;
    private JFormattedTextField cpf;
    private JPasswordField senha;
    private JPasswordField senhaRepetida;
    private JButton botaoCadastrar;
    private JPanel painelFormulario;

    public TelaCadastro(Banco banco, CardLayout controlador, JPanel painelCentro, TelaConfiguracaoConta telaConfiguracaoConta){
        this.banco = banco;
        this.controlador = controlador;
        this.painelCentro = painelCentro;
        this.telaConfiguracaoConta = telaConfiguracaoConta;

        setLayout(new GridBagLayout());

        painelFormulario = new JPanel();
        painelFormulario.setLayout(new BoxLayout(painelFormulario, BoxLayout.Y_AXIS));
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Tela de Cadastro"));
        JLabel textoNome = new JLabel("Digite seu nome: ");
        textoNome.setAlignmentX(Component.CENTER_ALIGNMENT);

        nome = new JTextField(15);
        nome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textoCpf = new JLabel("Digite seu CPF: ");
        textoCpf.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            MaskFormatter mascaraCpf = new MaskFormatter("###.###.###-##");
            mascaraCpf.setPlaceholderCharacter('_');
            cpf = new JFormattedTextField(mascaraCpf);
            cpf.setFocusLostBehavior(JFormattedTextField.PERSIST);
            cpf.setAlignmentX(Component.CENTER_ALIGNMENT);
            cpf.setColumns(14);

        } catch (ParseException e){
            cpf = new JFormattedTextField();
            JOptionPane.showMessageDialog(null, "Erro ao validar o CPF: "+ e.getMessage(), "[ERRO]", JOptionPane.ERROR_MESSAGE);
        }

        JLabel textoSenha = new JLabel("Digite uma senha: ");
        textoSenha.setAlignmentX(Component.CENTER_ALIGNMENT);

        senha = new JPasswordField(15);
        senha.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel textoRepeteSenha = new JLabel("Repita a senha novamente: ");
        textoRepeteSenha.setAlignmentX(Component.CENTER_ALIGNMENT);

        senhaRepetida = new JPasswordField(15);
        senhaRepetida.setAlignmentX(Component.CENTER_ALIGNMENT);

        botaoCadastrar = new JButton("Criar conta");
        botaoCadastrar.setAlignmentX(Component.CENTER_ALIGNMENT);

        nome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cpf.requestFocus();
            }
        });

        cpf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                senha.requestFocus();
            }
        });

        senha.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                senhaRepetida.requestFocus();
            }
        });

        senhaRepetida.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                botaoCadastrar.doClick();
            }
        });

        botaoCadastrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){

                String nomeDigitado = nome.getText();
                String cpfDigitado = cpf.getText();
                String senhaDigitada = new String(senha.getPassword());
                String senhaDigitadaRepetida = new String(senhaRepetida.getPassword());

                if ((nomeDigitado.isBlank()) || (senhaDigitada.isBlank()) || (senhaDigitadaRepetida.isBlank()) || (cpfDigitado.isBlank()) ) {
                    JOptionPane.showMessageDialog(null, "Preencha todos os campos!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (cpfDigitado.contains("_")){
                    JOptionPane.showMessageDialog(null, "Por favor, preencha todos os números do CPF.", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (senhaDigitada.length() < 8){
                    JOptionPane.showMessageDialog(null, "Digite uma senha com no mínimo 8 caracteres", "Tente novamente", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                Usuario usuarioAtual = banco.buscarPorCpf(cpfDigitado);

                if (usuarioAtual != null){
                    JOptionPane.showMessageDialog(null, "CPF já cadastrado", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!senhaDigitadaRepetida.equals(senhaDigitada)){
                    JOptionPane.showMessageDialog(null, "As senhas não coincidem", "Tente novamente", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                usuarioAtual = new Usuario(nomeDigitado, cpfDigitado, senhaDigitada);

                telaConfiguracaoConta.receberUsuario(usuarioAtual);
                limparDados();
                controlador.show(painelCentro, "telaConfiguracaoConta");
            }
        });

        painelFormulario.add(textoNome);
        painelFormulario.add(nome);
        painelFormulario.add(textoCpf);
        painelFormulario.add(cpf);
        painelFormulario.add(textoSenha);
        painelFormulario.add(senha);
        painelFormulario.add(textoRepeteSenha);
        painelFormulario.add(senhaRepetida);
        painelFormulario.add(botaoCadastrar);
        add(painelFormulario);
    }

    private void limparDados(){
        nome.setText("");
        cpf.setValue(null);
        senha.setText("");
        senhaRepetida.setText("");
    }
}
