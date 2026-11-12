package org.ifpa.joaoadriel.sistemabancario.domain;

import java.util.ArrayList;
import java.util.List;

public class Banco {
    private List<Usuario> usuarios;

    public Banco(){
        this.usuarios = new ArrayList<>();
    }

    public boolean cadastrarUsuarios(Usuario usuario){
        if (buscarPorCpf(usuario.getCpf()) != null){
            return false;
        }

        this.usuarios.add(usuario);
        return true;
    }

    public Usuario buscarPorCpf(String cpf){
        for (Usuario usuarioAtual : this.usuarios){
            if (usuarioAtual.getCpf().equals(cpf)){
                return usuarioAtual;
            }
        }
        return null;
    }

    public Conta buscarContaPorPix(String chavePix){
        for (Usuario usuarioAtual : this.usuarios){
            if (usuarioAtual.getContaCorrente() != null && usuarioAtual.getContaCorrente().getChavePix().equals(chavePix)){
                return usuarioAtual.getContaCorrente();
            }
            if (usuarioAtual.getContaPoupanca() != null && usuarioAtual.getContaPoupanca().getChavePix().equals(chavePix)){
                return usuarioAtual.getContaPoupanca();
            }
        }
        return null;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }
}
