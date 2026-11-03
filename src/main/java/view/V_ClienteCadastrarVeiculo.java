package view;

import controller.OficinaController;
import model.Montadora;
import model.Modelo;

import javax.swing.*;
import java.awt.*;

/** Cadastro de veículo pelo próprio cliente (sem seleção de proprietário — usa o cliente logado). */
public class V_ClienteCadastrarVeiculo extends JPanel {

    private final OficinaController controller;
    private JComboBox<Montadora> cbbMontadora;
    private JComboBox<Modelo> cbbModelo;
    private JTextField txtPlaca;

    public V_ClienteCadastrarVeiculo(OficinaController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JButton btnVoltar = new JButton("← Voltar");
        btnVoltar.addActionListener(e -> navegar(new V_ClienteMenu(this.controller)));
        add(btnVoltar, BorderLayout.NORTH);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 60, 0, 60));

        JLabel titulo = new JLabel("Adicionar novo veículo");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        form.add(titulo);
        form.add(Box.createVerticalStrut(20));

        form.add(new JLabel("Montadora:"));
        cbbMontadora = new JComboBox<>();
        cbbMontadora.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        form.add(cbbMontadora);
        form.add(Box.createVerticalStrut(12));

        form.add(new JLabel("Modelo:"));
        cbbModelo = new JComboBox<>();
        cbbModelo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        form.add(cbbModelo);
        form.add(Box.createVerticalStrut(12));

        form.add(new JLabel("Placa:"));
        txtPlaca = new JTextField();
        txtPlaca.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        form.add(txtPlaca);
        form.add(Box.createVerticalStrut(20));

        JButton btnCadastrar = new JButton("Cadastrar veículo");
        btnCadastrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCadastrar.addActionListener(e -> cadastrar());
        form.add(btnCadastrar);

        add(form, BorderLayout.CENTER);

        carregarMontadoras();
    }

    private void carregarMontadoras() {
        for (Montadora m : controller.montadorasComModelos()) cbbMontadora.addItem(m);
        cbbMontadora.addActionListener(e -> atualizarModelos());
        atualizarModelos();
    }

    private void atualizarModelos() {
        cbbModelo.removeAllItems();
        Montadora m = (Montadora) cbbMontadora.getSelectedItem();
        if (m == null) return;
        for (Modelo mod : m.listarModelos()) cbbModelo.addItem(mod);
    }

    private void cadastrar() {
        Montadora montadora = (Montadora) cbbMontadora.getSelectedItem();
        Modelo modelo = (Modelo) cbbModelo.getSelectedItem();
        String placa = txtPlaca.getText().trim().toUpperCase();

        if (montadora == null || modelo == null) {
            DialogoAlerta.aviso(this, "Selecione uma montadora e um modelo.", "Dados inválidos");
            return;
        }
        if (placa.isEmpty()) {
            DialogoAlerta.aviso(this, "Informe a placa do veículo.", "Dados inválidos");
            return;
        }

        model.Cliente meu = controller.getMeuCliente();
        if (meu == null) {
            DialogoAlerta.erro(this, "Não foi possível identificar o seu cadastro de cliente.", "Erro");
            return;
        }

        try {
            controller.salvarVeiculo(modelo, placa, meu.getIdUsuario());
            DialogoAlerta.sucesso(this, "Veículo cadastrado com sucesso!", "Sucesso");
            navegar(new V_ClienteVeiculos(controller));
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao cadastrar veículo: " + ex.getMessage(), "Erro");
        }
    }

    private void navegar(JPanel pagina) {
        Window janela = SwingUtilities.getWindowAncestor(this);
        if (janela instanceof V_ClienteMain) ((V_ClienteMain) janela).atualizarConteudo(pagina);
    }
}
