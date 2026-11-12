package org.ifpa.joaoadriel.sistemabancario.test;

import javax.swing.*; // Importa todas as ferramentas gráficas
import java.awt.*;    // Importa ferramentas de layout
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingTeste {
    public static void main(String[] args) {
        // 1. CRIANDO A JANELA PRINCIPAL (JFrame)
        JFrame janela = new JFrame("Meu Primeiro Programa Swing");
        janela.setSize(600, 400); // Largura x Altura
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Faz o programa parar de rodar ao fechar no "X"
        janela.setLayout(new FlowLayout()); // Organiza os itens um do lado do outro automaticamente

        // 2. CRIANDO OS COMPONENTES (As peças do lego)
        JLabel texto = new JLabel("Digite o seu nome:");
        JTextField campoDeTexto = new JTextField(15); // Campo em branco com espaço para 15 letras
        JButton botao = new JButton("Dar Oi");

        // 3. ADICIONANDO VIDA AO BOTÃO (Eventos)
        // Isso diz ao botão: "Fique ouvindo. Quando alguém te clicar, execute este código."
        botao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Pega o texto que o usuário digitou no campo
                String nomeDigitado = campoDeTexto.getText();

                // Mostra uma caixinha de alerta na tela
                JOptionPane.showMessageDialog(janela, "Olá, " + nomeDigitado + "! Bem-vindo ao Swing.");
            }
        });

        // 4. COLOCANDO AS PEÇAS DENTRO DA JANELA
        janela.add(texto);
        janela.add(campoDeTexto);
        janela.add(botao);

        // 5. EXIBINDO TUDO
        // Isso sempre deve ser a última linha do código da tela!
        janela.setVisible(true);
    }
}