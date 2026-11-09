package view;

import br.com.oficina.veiculo.AbastecimentoEntity;
import controller.OficinaController;
import model.Veiculo;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * "Abastecimentos" do veículo (Portal do Cliente): formulário para o cliente registrar
 * cada vez que abastece (data, km, litros, combustível e valor) e o histórico já
 * registrado, mais recente primeiro.
 */
public class V_ClienteAbastecimentos extends JPanel {

    private static final String[] COMBUSTIVEL_LABEL = {"Gasolina", "Etanol", "Diesel", "GNV", "Flex", "Outros"};
    private static final String[] COMBUSTIVEL_CODE  = {"GASOLINA", "ETANOL", "DIESEL", "GNV", "FLEX", "OUTROS"};

    private final OficinaController controller;
    private final Veiculo veiculo;

    private JTextField txtData;
    private JTextField txtKm;
    private JTextField txtLitros;
    private JTextField txtValor;
    private JComboBox<String> cbbCombustivel;
    private JPanel listaHistorico;

    public V_ClienteAbastecimentos(OficinaController controller, Veiculo veiculo) {
        this.controller = controller;
        this.veiculo = veiculo;

        setLayout(new BorderLayout());
        setBackground(EstiloCliente.FUNDO);

        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setOpaque(false);
        conteudo.add(construirFormulario());
        conteudo.add(Box.createVerticalStrut(24));
        conteudo.add(construirHistorico());

        add(EstiloCliente.pagina("Abastecimentos — " + EstiloCliente.nomeVeiculo(veiculo),
                () -> EstiloCliente.navegar(this, new V_ClienteDetalhesVeiculo(controller, veiculo)), conteudo));

        atualizarHistorico();
    }

    // ------------------------------------------------------------------ formulário

    private JComponent construirFormulario() {
        EstiloCliente.PainelArredondado card = new EstiloCliente.PainelArredondado(
                new BorderLayout(0, 14), 14, Color.decode("#F4F4F4"));
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel titulo = new JLabel("Registrar abastecimento");
        titulo.setFont(EstiloCliente.fonte(Font.BOLD, 18));
        titulo.setForeground(EstiloCliente.TEXTO);
        card.add(titulo, BorderLayout.NORTH);

        txtData = EstiloCliente.campoTexto();
        txtData.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtKm = EstiloCliente.campoTexto();
        txtLitros = EstiloCliente.campoTexto();
        txtValor = EstiloCliente.campoTexto();
        cbbCombustivel = EstiloCliente.combo(s -> s);
        for (String label : COMBUSTIVEL_LABEL) cbbCombustivel.addItem(label);

        JPanel linha = new JPanel(new GridLayout(2, 5, 14, 6));
        linha.setOpaque(false);
        linha.add(EstiloCliente.rotuloCampo("Data"));
        linha.add(EstiloCliente.rotuloCampo("Km atual"));
        linha.add(EstiloCliente.rotuloCampo("Litros"));
        linha.add(EstiloCliente.rotuloCampo("Combustível"));
        linha.add(EstiloCliente.rotuloCampo("Valor (R$)"));
        linha.add(txtData);
        linha.add(txtKm);
        linha.add(txtLitros);
        linha.add(cbbCombustivel);
        linha.add(txtValor);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, linha.getPreferredSize().height));
        card.add(linha, BorderLayout.CENTER);

        JButton btnRegistrar = EstiloCliente.botao("Registrar abastecimento", EstiloCliente.ACENTO, Color.WHITE, 14);
        JPanel pnlBotao = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlBotao.setOpaque(false);
        pnlBotao.add(btnRegistrar);
        btnRegistrar.addActionListener(e -> registrar());
        card.add(pnlBotao, BorderLayout.SOUTH);

        return card;
    }

    private void registrar() {
        String dataIso = normalizarData(txtData.getText().trim());
        if (dataIso == null) {
            DialogoAlerta.aviso(this, "Informe uma data válida (dd/mm/aaaa).", "Dados inválidos");
            return;
        }

        int km;
        try {
            km = Integer.parseInt(txtKm.getText().trim());
            if (km < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            DialogoAlerta.aviso(this, "Informe a quilometragem atual (número inteiro).", "Dados inválidos");
            return;
        }

        double litros;
        try {
            litros = Double.parseDouble(txtLitros.getText().trim().replace(",", "."));
            if (litros <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            DialogoAlerta.aviso(this, "Informe a quantidade de litros (ex: 35.5).", "Dados inválidos");
            return;
        }

        double valor;
        try {
            valor = Double.parseDouble(txtValor.getText().trim().replace(",", "."));
            if (valor <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            DialogoAlerta.aviso(this, "Informe o valor pago (ex: 200.00).", "Dados inválidos");
            return;
        }

        String tipoCombustivel = COMBUSTIVEL_CODE[cbbCombustivel.getSelectedIndex()];

        try {
            controller.registrarAbastecimento(veiculo.getIdVeiculo(), dataIso, valor, litros, tipoCombustivel, km);
            DialogoAlerta.sucesso(this, "Abastecimento registrado com sucesso!", "Sucesso");
            txtKm.setText("");
            txtLitros.setText("");
            txtValor.setText("");
            txtData.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            cbbCombustivel.setSelectedIndex(0);
            atualizarHistorico();
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao registrar abastecimento: " + ex.getMessage(), "Erro");
        }
    }

    // ------------------------------------------------------------------ histórico

    private JComponent construirHistorico() {
        JPanel col = new JPanel(new BorderLayout(0, 10));
        col.setOpaque(false);

        JLabel titulo = new JLabel("Histórico de abastecimentos", SwingConstants.CENTER);
        titulo.setFont(EstiloCliente.fonte(Font.PLAIN, 18));
        titulo.setForeground(EstiloCliente.TEXTO);
        col.add(titulo, BorderLayout.NORTH);

        listaHistorico = new JPanel();
        listaHistorico.setLayout(new BoxLayout(listaHistorico, BoxLayout.Y_AXIS));
        listaHistorico.setOpaque(false);
        col.add(listaHistorico, BorderLayout.CENTER);
        return col;
    }

    private void atualizarHistorico() {
        listaHistorico.removeAll();
        List<AbastecimentoEntity> registros = controller.listarAbastecimentos(veiculo.getIdVeiculo());

        if (registros.isEmpty()) {
            JLabel vazio = new JLabel("Nenhum abastecimento registrado ainda.", SwingConstants.CENTER);
            vazio.setFont(EstiloCliente.fonte(Font.ITALIC, 14));
            vazio.setForeground(EstiloCliente.TEXTO_SUAVE);
            vazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            listaHistorico.add(vazio);
        } else {
            for (AbastecimentoEntity a : registros) {
                listaHistorico.add(construirLinhaHistorico(a));
                listaHistorico.add(Box.createVerticalStrut(8));
            }
        }
        listaHistorico.revalidate();
        listaHistorico.repaint();
    }

    private JComponent construirLinhaHistorico(AbastecimentoEntity a) {
        EstiloCliente.PainelArredondado linha = new EstiloCliente.PainelArredondado(
                new GridLayout(1, 5, 10, 0), 10, EstiloCliente.TILE);
        linha.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, linha.getPreferredSize().height));

        linha.add(campoHistorico(EstiloCliente.dataBR(a.getDataAbastecimento())));
        linha.add(campoHistorico(a.getQuilometragem() + " km"));
        linha.add(campoHistorico(String.format("%.1f L", a.getLitros())));
        linha.add(campoHistorico(a.getTipoCombustivelLabel()));
        linha.add(campoHistorico(EstiloCliente.reais(a.getValor())
                + " (" + EstiloCliente.reais(a.getValorPorLitro()) + "/L)"));
        return linha;
    }

    private JLabel campoHistorico(String texto) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER);
        l.setFont(EstiloCliente.fonte(Font.PLAIN, 14));
        l.setForeground(EstiloCliente.TEXTO);
        return l;
    }

    /** Aceita dd/MM/yyyy ou AAAA-MM-DD e retorna sempre no formato ISO. Null se inválida. */
    private String normalizarData(String s) {
        if (s == null || s.isBlank()) return null;
        String t = s.trim();
        try { return LocalDate.parse(t, DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString(); } catch (Exception ignored) { }
        try { return LocalDate.parse(t).toString(); } catch (Exception ignored) { }
        return null;
    }
}
