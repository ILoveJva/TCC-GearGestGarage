package model;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario {
    private final List<Veiculo> veiculos = new ArrayList<>();

    public Cliente() {}
    public Cliente(long id, String nome, String cpf, String telefone, String email) {
        this.idUsuario = id; this.nome = nome; this.cpf = cpf;
        this.telefone = telefone; this.email = email;
    }

    public List<Veiculo> getVeiculos() { return veiculos; }
    public void addVeiculo(Veiculo v) { veiculos.add(v); }

    @Override public String toString() { return nome; }
}
