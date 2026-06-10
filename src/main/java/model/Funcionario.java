package model;

public class Funcionario extends Usuario {
    private double salario;
    private String cargo;

    public Funcionario() {}
    public Funcionario(long id, String nome, String cargo) {
        this.idUsuario = id; this.nome = nome; this.cargo = cargo;
    }

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
}
