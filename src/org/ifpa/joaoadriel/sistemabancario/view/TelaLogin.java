package org.ifpa.joaoadriel.sistemabancario.view;

import org.ifpa.joaoadriel.sistemabancario.domain.Banco;
import org.ifpa.joaoadriel.sistemabancario.domain.Usuario;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

public class TelaLogin extends JPanel {
    private Banco banco;
    private JFormattedTextField cpf;
    private JPasswordField senha;
    private JButton botaoLogin;
    private JPanel painelFormulario;

    public TelaLogin(Banco banco) {
        this.banco = banco;

        Color azulBanco = new Color(30, 58, 95);
        Color fundoClaro = new Color(244, 246, 249);
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
                "Acesso à Conta"
        );
        bordaTitulo.setTitleFont(new Font("SansSerif", Font.BOLD, 18));
        bordaTitulo.setTitleColor(azulBanco);

        painelFormulario.setBorder(BorderFactory.createCompoundBorder(
                bordaTitulo,
                BorderFactory.createEmptyBorder(25, 45, 25, 45)
        ));

        JLabel textoCpf = new JLabel("CPF:");
        textoCpf.setFont(fonteGeral);
        textoCpf.setForeground(textoEscuro);
        textoCpf.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            MaskFormatter mascaraCpf = new MaskFormatter("###.###.###-##");
            mascaraCpf.setPlaceholderCharacter('_');
            cpf = new JFormattedTextField(mascaraCpf);
            cpf.setFocusLostBehavior(JFormattedTextField.PERSIST);
            cpf.setFont(fonteGeral);
            cpf.setAlignmentX(Component.CENTER_ALIGNMENT);
            cpf.setColumns(15);
        } catch (ParseException e) {
            cpf = new JFormattedTextField();
            JOptionPane.showMessageDialog(null, "Erro ao validar o CPF: " + e.getMessage(), "[ERRO]", JOptionPane.ERROR_MESSAGE);
        }

        JLabel textoSenha = new JLabel("Senha:");
        textoSenha.setFont(fonteGeral);
        textoSenha.setForeground(textoEscuro);
        textoSenha.setAlignmentX(Component.CENTER_ALIGNMENT);

        senha = new JPasswordField(15);
        senha.setFont(fonteGeral);
        senha.setAlignmentX(Component.CENTER_ALIGNMENT);

        botaoLogin = new JButton("Entrar");
        botaoLogin.setFont(fonteBotao);
        botaoLogin.setBackground(azulBanco);
        botaoLogin.setForeground(Color.WHITE);
        botaoLogin.setFocusPainted(false);
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
            public void actionPerformed(ActionEvent e) {
                String cpfDigitado = cpf.getText();
                String senhaDigitada = new String(senha.getPassword());
                if ((cpfDigitado.isBlank()) || (senhaDigitada.isBlank())) {
                    JOptionPane.showMessageDialog(null, "[ERRO] Preencha CPF e senha!", "ERRO", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Usuario usuarioLogado = banco.buscarPorCpf(cpfDigitado);

                if ((usuarioLogado != null) && (senhaDigitada.equals(usuarioLogado.getSenha()))) {
                    TelaPrincipal telaPrincipal = new TelaPrincipal(banco, usuarioLogado);
                    telaPrincipal.setVisible(true);
                    SwingUtilities.getWindowAncestor(TelaLogin.this).dispose();
                    limparDados();
                } else {
                    JOptionPane.showMessageDialog(null, "[ERRO] CPF ou Senha incorretos!", "[ERRO]", JOptionPane.ERROR_MESSAGE);
                    limparDados();
                }
            }
        });

        painelFormulario.add(textoCpf);
        painelFormulario.add(Box.createRigidArea(new Dimension(0, 5)));
        painelFormulario.add(cpf);
        painelFormulario.add(Box.createRigidArea(new Dimension(0, 15)));
        painelFormulario.add(textoSenha);
        painelFormulario.add(Box.createRigidArea(new Dimension(0, 5)));
        painelFormulario.add(senha);
        painelFormulario.add(Box.createRigidArea(new Dimension(0, 25)));
        painelFormulario.add(botaoLogin);
        add(painelFormulario);
    }

    private void limparDados() {
        cpf.setValue(null);
        senha.setText("");
    }
}