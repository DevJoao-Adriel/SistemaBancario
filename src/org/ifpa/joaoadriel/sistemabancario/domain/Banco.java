package org.ifpa.joaoadriel.sistemabancario.domain;

import java.util.ArrayList;
import java.util.List;

public class Banco {
    private List<Usuario> usuarios;

    //O banco fica responsável por gerenciar os usuários dentro do banco
    public Banco() {
        this.usuarios = new ArrayList<>();
    }

    //metodo para cadastrar o usuário
    public boolean cadastrarUsuarios(Usuario usuario) {
        if (buscarPorCpf(usuario.getCpf()) != null) {
            return false;
        }

        this.usuarios.add(usuario);
        return true;
    }

    //metodo para buscar um usuário pelo cpf
    public Usuario buscarPorCpf(String cpf) {
        for (Usuario usuarioAtual : this.usuarios) {
            if (usuarioAtual.getCpf().equals(cpf)) {
                return usuarioAtual;
            }
        }
        return null;
    }

    //metodo para achar um usuario pela chavepix
    public Conta buscarContaPorPix(String chavePix) {
        for (Usuario usuarioAtual : this.usuarios) {
            if (usuarioAtual.getContaCorrente() != null && usuarioAtual.getContaCorrente().getChavePix().equals(chavePix)) {
                return usuarioAtual.getContaCorrente();
            }
            if (usuarioAtual.getContaPoupanca() != null && usuarioAtual.getContaPoupanca().getChavePix().equals(chavePix)) {
                return usuarioAtual.getContaPoupanca();
            }
        }
        return null;
    }

    //declarando a lista de usuarios
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    //metodo para deletar um usuario
    public boolean deletarUsuarios(Usuario usuarioLogado) {
        boolean temSaldoCorrente = (usuarioLogado.getContaCorrente() != null) && (Math.abs(usuarioLogado.getContaCorrente().getSaldo()) >= 0.01);
        boolean temSaldoPoupanca = (usuarioLogado.getContaPoupanca() != null) && (Math.abs(usuarioLogado.getContaPoupanca().getSaldo()) >= 0.01);

        if (temSaldoCorrente || temSaldoPoupanca) {
            return false;
        }
        usuarios.remove(usuarioLogado);
        return true;
    }
}