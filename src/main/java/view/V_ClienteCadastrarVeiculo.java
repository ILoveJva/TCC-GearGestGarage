package view;

import controller.OficinaController;
import model.Modelo;
import model.Montadora;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * "Adicionar veículo" (protótipo do Figma): Tipo do veículo → Marca → Modelo → Ano,
 * mais a placa, e o botão preto "Cadastrar". O cadastro usa o cliente logado.
 *
 * Os combos são encadeados: cada escolha filtra a próxima com base nos modelos
 * cadastrados (o ano faz parte do modelo no banco).
 */
public class V_ClienteCadastrarVeiculo extends JPanel {

    private final OficinaController controller;
    private final List<Montadora> montadoras;

    private JComboBox<String> cbbTipo;
    private JComboBox<Montadora> cbbMarca;
    private JComboBox<String> cbbModelo;
    private JComboBox<Modelo> cbbAno;
    private JTextField txtPlaca;

    public V_ClienteCadastrarVeiculo(OficinaController controller) {
        this.controller = controller;
        this.montadoras = controller.montadorasComModelos();

        setLayout(new BorderLayout());
        setBackground(EstiloCliente.FUNDO);
        add(EstiloCliente.pagina("Adicionar veículo",
                () -> EstiloCliente.navegar(this, new V_ClienteMenu(controller)), construirFormulario()));

        carregarTipos();
    }

    private JComponent construirFormulario() {
        cbbTipo = EstiloCliente.combo(s -> s);
        cbbMarca = EstiloCliente.combo(Montadora::getNome);
        cbbModelo = EstiloCliente.combo(s -> s);
        cbbAno = EstiloCliente.combo(m -> String.valueOf(m.getAno()));
        txtPlaca = EstiloCliente.campoTexto();
        ((PlainDocument) txtPlaca.getDocument()).setDocumentFilter(new FiltroPlaca());
        txtPlaca.setToolTipText("Ex.: ABC1D23 ou ABC1234");

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        adicionarCampo(form, "Tipo do veículo", cbbTipo);
        adicionarCampo(form, "Marca", cbbMarca);
        adicionarCampo(form, "Modelo", cbbModelo);

        // Placa e Ano lado a lado, como no protótipo.
        JPanel linha = new JPanel(new GridLayout(2, 2, 24, 8));
        linha.setOpaque(false);
        linha.add(EstiloCliente.rotuloCampo("Placa"));
        linha.add(EstiloCliente.rotuloCampo("Ano"));
        linha.add(txtPlaca);
        linha.add(cbbAno);
        linha.setAlignmentX(Component.CENTER_ALIGNMENT);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, linha.getPreferredSize().height));
        form.add(linha);
        form.add(Box.createVerticalStrut(30));

        JButton btnCadastrar = EstiloCliente.botao("Cadastrar", EstiloCliente.TEXTO, Color.WHITE, 30);
        btnCadastrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCadastrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        btnCadastrar.setPreferredSize(new Dimension(400, 60));
        btnCadastrar.addActionListener(e -> cadastrar());
        form.add(btnCadastrar);

        // Formulário estreito e centralizado: em tela de computador os campos não esticam.
        JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centro.setOpaque(false);
        form.setPreferredSize(new Dimension(560, form.getPreferredSize().height));
        centro.add(form);
        return centro;
    }

    private void adicionarCampo(JPanel form, String rotulo, JComponent campo) {
        JLabel l = EstiloCliente.rotuloCampo(rotulo);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        form.add(l);
        form.add(Box.createVerticalStrut(8));
        form.add(campo);
        form.add(Box.createVerticalStrut(18));
    }

    // ------------------------------------------------------------------ combos encadeados

    private void carregarTipos() {
        Set<String> tipos = new TreeSet<>();
        for (Montadora m : montadoras)
            for (Modelo mod : m.listarModelos())
                if (!mod.getTipo().isBlank()) tipos.add(mod.getTipo());
        for (String t : tipos) cbbTipo.addItem(t);
        if (tipos.contains("Carro")) cbbTipo.setSelectedItem("Carro");

        cbbTipo.addActionListener(e -> atualizarMarcas());
        cbbMarca.addActionListener(e -> atualizarModelos());
        cbbModelo.addActionListener(e -> atualizarAnos());
        atualizarMarcas();
    }

    private void atualizarMarcas() {
        String tipo = (String) cbbTipo.getSelectedItem();
        cbbMarca.removeAllItems();
        for (Montadora m : montadoras)
            if (m.listarModelos().stream().anyMatch(mod -> mod.getTipo().equals(tipo))) cbbMarca.addItem(m);
        atualizarModelos();
    }

    private void atualizarModelos() {
        String tipo = (String) cbbTipo.getSelectedItem();
        Montadora marca = (Montadora) cbbMarca.getSelectedItem();
        cbbModelo.removeAllItems();
        if (marca != null) {
            Set<String> nomes = new TreeSet<>();
            for (Modelo mod : marca.listarModelos())
                if (mod.getTipo().equals(tipo)) nomes.add(mod.getNome());
            for (String n : nomes) cbbModelo.addItem(n);
        }
        atualizarAnos();
    }

    private void atualizarAnos() {
        String tipo = (String) cbbTipo.getSelectedItem();
        Montadora marca = (Montadora) cbbMarca.getSelectedItem();
        String nome = (String) cbbModelo.getSelectedItem();
        cbbAno.removeAllItems();
        if (marca == null || nome == null) return;
        marca.listarModelos().stream()
                .filter(mod -> mod.getTipo().equals(tipo) && mod.getNome().equals(nome))
                .sorted(Comparator.comparingInt(Modelo::getAno).reversed())
                .forEach(cbbAno::addItem);
    }

    // ------------------------------------------------------------------ cadastro

    private void cadastrar() {
        Modelo modelo = (Modelo) cbbAno.getSelectedItem();
        String placa = txtPlaca.getText().trim().toUpperCase();

        List<String> erros = new ArrayList<>();
        if (modelo == null) erros.add("Selecione tipo, marca, modelo e ano do veículo.");
        if (!placa.matches("[A-Z]{3}[0-9][A-Z0-9][0-9]{2}")) {
            erros.add("Informe uma placa válida (ex.: ABC1D23 ou ABC1234).");
            txtPlaca.setBackground(Color.decode("#F6C6C6"));
        } else {
            txtPlaca.setBackground(EstiloCliente.TILE);
        }
        if (!erros.isEmpty()) {
            DialogoAlerta.aviso(this, String.join("\n", erros), "Dados inválidos");
            return;
        }

        model.Cliente meu = controller.getMeuCliente();
        if (meu == null) {
            DialogoAlerta.erro(this, "Não foi possível identificar o seu cadastro de cliente.", "Erro");
            return;
        }
        boolean placaRepetida = controller.listarVeiculos().stream()
                .anyMatch(v -> placa.equalsIgnoreCase(v.getPlaca()));
        if (placaRepetida) {
            DialogoAlerta.aviso(this, "Já existe um veículo cadastrado com a placa " + placa + ".", "Placa em uso");
            return;
        }

        try {
            controller.salvarVeiculo(modelo, placa, meu.getIdUsuario());
            DialogoAlerta.sucesso(this, "Veículo cadastrado com sucesso!", "Sucesso");
            EstiloCliente.navegar(this, new V_ClienteVeiculos(controller));
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao cadastrar veículo: " + ex.getMessage(), "Erro");
        }
    }

    /** Placa: só letras/números, maiúsculas, no máximo 7 caracteres. */
    private static final class FiltroPlaca extends DocumentFilter {
        @Override public void insertString(FilterBypass fb, int off, String s, AttributeSet a) throws BadLocationException {
            replace(fb, off, 0, s, a);
        }
        @Override public void replace(FilterBypass fb, int off, int len, String s, AttributeSet a) throws BadLocationException {
            if (s == null) return;
            String limpo = s.toUpperCase().replaceAll("[^A-Z0-9]", "");
            int espaco = 7 - (fb.getDocument().getLength() - len);
            if (limpo.length() > espaco) limpo = limpo.substring(0, Math.max(espaco, 0));
            super.replace(fb, off, len, limpo, a);
        }
    }
}
