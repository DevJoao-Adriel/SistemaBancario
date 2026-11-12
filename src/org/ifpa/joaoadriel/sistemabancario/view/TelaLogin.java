package org.ifpa.joaoadriel.sistemabancario.view;

import org.ifpa.joaoadriel.sistemabancario.domain.Banco;
import org.ifpa.joaoadriel.sistemabancario.domain.Usuario;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

public class TelaLogin extends JPanel{
    private Banco banco;
    private JFormattedTextField cpf;
    private JPasswordField senha;
    private JButton botaoLogin;
    private JPanel painelFormulario;
    private TelaPrincipal telaPrincipal;

    public TelaLogin (Banco banco){
        this.banco = banco;

        setLayout(new GridBagLayout());

        painelFormulario = new JPanel();
        painelFormulario.setLayout(new BoxLayout(painelFormulario, BoxLayout.Y_AXIS));
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Tela de Cadastro"));

        JLabel textoCpf = new JLabel("Digite seu CPF: ");
        textoCpf.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            MaskFormatter mascaraCpf = new MaskFormatter("###.###.###-##");
            mascaraCpf.setPlaceholderCharacter('_');
            cpf = new JFormattedTextField(mascaraCpf);
            cpf.setFocusLostBehavior(JFormattedTextField.PERSIST);
            cpf.setAlignmentX(Component.CENTER_ALIGNMENT);
            cpf.setColumns(14);

        }catch (ParseException e){
            cpf = new JFormattedTextField();
            JOptionPane.showMessageDialog(null, "Erro ao validar o CPF: "+ e.getMessage(), "[ERRO]", JOptionPane.ERROR_MESSAGE);
        }

        JLabel textoSenha = new JLabel("Digite sua senha: ");
        textoSenha.setAlignmentX(Component.CENTER_ALIGNMENT);

        senha = new JPasswordField(15);
        senha.setAlignmentX(Component.CENTER_ALIGNMENT);


        botaoLogin = new JButton("Fazer login");
        botaoLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        cpf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                senha.requestFocus();
            }
        });

        senha.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                botaoLogin.doClick();
            }
        });

        botaoLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){

                String cpfDigitado = cpf.getText();
                String senhaDigitada = new String(senha.getPassword());
                if ((cpfDigitado.isBlank()) || (senhaDigitada.isBlank())){
                    JOptionPane.showMessageDialog(null, "[ERRRO] Não há nada digitado!", "ERRO" , JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Usuario usuarioLogado = banco.buscarPorCpf(cpfDigitado);

                if ((usuarioLogado != null) && (senhaDigitada.equals(usuarioLogado.getSenha()))){
                    telaPrincipal = new TelaPrincipal(banco, usuarioLogado);
                    telaPrincipal.setVisible(true);
                    SwingUtilities.getWindowAncestor(TelaLogin.this).dispose();
                    limparDados();
                }else{
                    JOptionPane.showMessageDialog(null, "[ERRO] CPF ou Senha incorretos!", "[ERRO]",  JOptionPane.ERROR_MESSAGE);
                    limparDados();
                }

            }
        });

        painelFormulario.add(textoCpf);
        painelFormulario.add(cpf);
        painelFormulario.add(textoSenha);
        painelFormulario.add(senha);
        painelFormulario.add(botaoLogin);
        add(painelFormulario);
    }

    private void limparDados(){
        cpf.setValue(null);
        senha.setText("");
    }
}
