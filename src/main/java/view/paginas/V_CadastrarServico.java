package view.paginas;

import controller.OficinaController;
import model.Orcamento;
import model.OrdemDeServico;
import view.V_Main;
import view.design.Botao;
import view.DialogoAlerta;
import view.design.ComboBox;
import view.design.ScrollBar;
import view.paginas.V_VisualizarServicos;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Abertura de uma Ordem de Serviço a partir de um orçamento APROVADO que ainda não tem OS.
 * Padronizado com a classe ComboBox de design do sistema.
 */
public class V_CadastrarServico extends JPanel {

    private static final Color COR_FUNDO_PAGINA = Color.decode("#FAFBFC");
    private static final Color COR_LABEL = Color.decode("#57626F");
    private static final Color COR_TEXTO_CAMPO = Color.decode("#2B2E33");

    private ComboBox<ItemOrcamento> cbb_Orcamento;
    private JTextField txt_Titulo, txt_Data, txt_Km;
    private ComboBox<String> cbb_Tipo;
    private ComboBox<String> cbb_Manutencao;
    private JButton btn_Criar;

    private JLabel val_Cliente, val_Veiculo, val_Responsavel, val_Valor, val_Reclamacao;

    private List<Orcamento> orcamentos = new ArrayList<>();
    private Orcamento selecionado;
    private boolean carregando = false;

    private final OficinaController controller;

    public V_CadastrarServico(OficinaController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        setBackground(COR_FUNDO_PAGINA);
        iniciarComponentes();
        carregarDados();
    }

    private void iniciarComponentes() {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(680, 640));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#E0E0E0")),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        JLabel titulo = new JLabel("Serviços > Nova Ordem de Serviço");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(Color.decode("#FF9900"));

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.add(titulo, BorderLayout.WEST);
        pnlHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#F0F0F0")));
        pnlHeader.setPreferredSize(new Dimension(0, 35));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        form.add(construirSecaoOrcamento());
        form.add(Box.createVerticalStrut(12));
        form.add(construirResumo());
        form.add(Box.createVerticalStrut(16));
        form.add(construirSecaoDados());

        Botao.LinkTexto btn_Voltar = new Botao.LinkTexto("← Voltar", Botao.Paleta.CINZA, 130, 40);
        btn_Voltar.addActionListener(e -> navegar(new V_VisualizarServicos(controller)));

        btn_Criar = new Botao.AcaoPrincipal("ABRIR ORDEM DE SERVIÇO", null);
        btn_Criar.setPreferredSize(new Dimension(290, 42));
        btn_Criar.setEnabled(false);
        btn_Criar.addActionListener(e -> criarOS());

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        pnlBtn.setOpaque(false);
        pnlBtn.add(btn_Voltar);
        pnlBtn.add(btn_Criar);

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        scrollForm.setOpaque(false);
        scrollForm.getViewport().setOpaque(false);
        ScrollBar.aplicar(scrollForm);

        card.add(pnlHeader, BorderLayout.NORTH);
        card.add(scrollForm, BorderLayout.CENTER);
        card.add(pnlBtn, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 20, 10, 20);
        add(card, gbc);
    }

    private JPanel construirSecaoOrcamento() {
        JPanel sec = new JPanel(new BorderLayout(0, 4));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JLabel lbl = rotulo("Orçamento aprovado *");

        cbb_Orcamento = new ComboBox<>();
        cbb_Orcamento.setPreferredSize(new Dimension(0, 34));
        cbb_Orcamento.addActionListener(e -> onSelecionarOrcamento());

        Botao.LinkTexto btn_Atualizar = new Botao.LinkTexto("↺ Atualizar", Botao.Paleta.CINZA, 120, 34);
        btn_Atualizar.setToolTipText("Recarregar a lista de orçamentos aprovados sem OS");
        btn_Atualizar.addActionListener(e -> carregarDados());

        JPanel linha = new JPanel(new BorderLayout(8, 0));
        linha.setOpaque(false);
        linha.add(cbb_Orcamento, BorderLayout.CENTER);
        linha.add(btn_Atualizar, BorderLayout.EAST);

        sec.add(lbl, BorderLayout.NORTH);
        sec.add(linha, BorderLayout.CENTER);
        return sec;
    }

    private JPanel construirResumo() {
        JPanel pnl = new JPanel(new GridLayout(0, 2, 8, 4));
        pnl.setBackground(Color.decode("#F8F9FA"));
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#E0E0E0")),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        val_Cliente     = valorInfo();
        val_Veiculo     = valorInfo();
        val_Responsavel = valorInfo();
        val_Valor       = valorInfo();
        val_Reclamacao  = valorInfo();

        pnl.add(rotuloInfo("Cliente:"));      pnl.add(val_Cliente);
        pnl.add(rotuloInfo("Veículo:"));      pnl.add(val_Veiculo);
        pnl.add(rotuloInfo("Responsável:"));  pnl.add(val_Responsavel);
        pnl.add(rotuloInfo("Valor:"));        pnl.add(val_Valor);
        pnl.add(rotuloInfo("Reclamação:"));   pnl.add(val_Reclamacao);
        return pnl;
    }

    private JPanel construirSecaoDados() {
        JPanel sec = new JPanel();
        sec.setLayout(new BoxLayout(sec, BoxLayout.Y_AXIS));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);

        txt_Titulo = new JTextField();
        txt_Titulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_Titulo.setPreferredSize(new Dimension(0, 34));
        txt_Titulo.setEnabled(false);
        ((AbstractDocument) txt_Titulo.getDocument()).setDocumentFilter(new FiltroTexto());

        cbb_Tipo = new ComboBox<>();
        cbb_Tipo.setPreferredSize(new Dimension(0, 34));
        cbb_Tipo.setEnabled(false);

        cbb_Manutencao = new ComboBox<>();
        cbb_Manutencao.setPreferredSize(new Dimension(0, 34));
        cbb_Manutencao.setEnabled(false);

        txt_Data = new JTextField();
        txt_Data.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_Data.setPreferredSize(new Dimension(0, 34));
        txt_Data.setText(LocalDate.now().toString());
        txt_Data.setEnabled(false);

        txt_Km = new JTextField();
        txt_Km.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_Km.setPreferredSize(new Dimension(0, 34));
        txt_Km.setEnabled(false);
        ((AbstractDocument) txt_Km.getDocument()).setDocumentFilter(new FiltroDigitos(8));

        JPanel linhaTitulo = campoVertical("Título da OS *", txt_Titulo);
        linhaTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaTitulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JPanel linhaTipos = new JPanel(new GridLayout(1, 2, 14, 0));
        linhaTipos.setOpaque(false);
        linhaTipos.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaTipos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        linhaTipos.add(campoVertical("Tipo de Serviço *", cbb_Tipo));
        linhaTipos.add(campoVertical("Manutenção *", cbb_Manutencao));

        JPanel linhaDataKm = new JPanel(new GridLayout(1, 2, 14, 0));
        linhaDataKm.setOpaque(false);
        linhaDataKm.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaDataKm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        linhaDataKm.add(campoVertical("Data *", txt_Data));
        linhaDataKm.add(campoVertical("KM Atual *", txt_Km));

        sec.add(linhaTitulo);
        sec.add(Box.createVerticalStrut(12));
        sec.add(linhaTipos);
        sec.add(Box.createVerticalStrut(12));
        sec.add(linhaDataKm);
        return sec;
    }

    private void carregarDados() {
        if (controller == null) return;
        carregando = true;

        cbb_Tipo.removeAllItems();
        for (OrdemDeServico.TipoServicoOS t : OrdemDeServico.TipoServicoOS.values())
            cbb_Tipo.addItem(t.getLabel());

        cbb_Manutencao.removeAllItems();
        for (OrdemDeServico.TipoManutencao m : OrdemDeServico.TipoManutencao.values())
            cbb_Manutencao.addItem(m.getLabel());

        orcamentos = controller.listarOrcamentosAprovadosSemOS();

        cbb_Orcamento.removeAllItems();
        cbb_Orcamento.addItem(new ItemOrcamento(null, orcamentos.isEmpty()
                ? "(nenhum orçamento aprovado sem OS)"
                : "Selecione um orçamento aprovado..."));
        for (Orcamento o : orcamentos) cbb_Orcamento.addItem(new ItemOrcamento(o, null));
        cbb_Orcamento.setSelectedIndex(0);

        carregando = false;
        selecionado = null;
        atualizarResumo();
        setFormEnabled(false);
    }

    private void onSelecionarOrcamento() {
        if (carregando) return;

        ItemOrcamento item = (ItemOrcamento) cbb_Orcamento.getSelectedItem();
        if (item == null || item.orcamento == null) {
            selecionado = null;
            atualizarResumo();
            setFormEnabled(false);
            return;
        }
        selecionado = item.orcamento;
        atualizarResumo();
        txt_Titulo.setText(selecionado.getReclamacao());
        txt_Data.setText(LocalDate.now().toString());
        txt_Km.setText("");
        setFormEnabled(true);
    }

    private void atualizarResumo() {
        if (selecionado == null) {
            val_Cliente.setText("—");
            val_Veiculo.setText("—");
            val_Responsavel.setText("—");
            val_Valor.setText("—");
            val_Reclamacao.setText("—");
            val_Reclamacao.setToolTipText(null);
            return;
        }
        val_Cliente.setText(valorOuTraco(selecionado.getNomeCliente()));
        val_Veiculo.setText(valorOuTraco(selecionado.getPlacaVeiculo()));
        val_Responsavel.setText(valorOuTraco(selecionado.getResponsavel()));
        val_Valor.setText(String.format("R$ %.2f", selecionado.getValor()));
        val_Reclamacao.setText(valorOuTraco(selecionado.getReclamacao()));
        val_Reclamacao.setToolTipText(selecionado.getReclamacao());
    }

    private String valorOuTraco(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }

    private void setFormEnabled(boolean enabled) {
        txt_Titulo.setEnabled(enabled);
        cbb_Tipo.setEnabled(enabled);
        cbb_Manutencao.setEnabled(enabled);
        txt_Data.setEnabled(enabled);
        txt_Km.setEnabled(enabled);
        btn_Criar.setEnabled(enabled);
        if (!enabled) {
            txt_Titulo.setText("");
            txt_Km.setText("");
            txt_Data.setText(LocalDate.now().toString());
        }
    }

    private void criarOS() {
        if (selecionado == null) return;

        String titulo = txt_Titulo.getText().trim();
        String data   = txt_Data.getText().trim();
        String kmText = txt_Km.getText().trim();

        StringBuilder erros = new StringBuilder();
        if (titulo.length() < 3) erros.append("• Título deve ter pelo menos 3 caracteres.\n");
        if (data.isEmpty())       erros.append("• Informe a data do serviço.\n");
        int km = 0;
        try {
            if (kmText.isEmpty()) throw new NumberFormatException();
            km = Integer.parseInt(kmText);
            if (km < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            erros.append("• Informe o KM atual (número positivo).\n");
        }

        if (erros.length() > 0) {
            DialogoAlerta.aviso(this, "Corrija os campos:\n\n" + erros, "Dados Inválidos");
            return;
        }

        try {
            OrdemDeServico.TipoServicoOS tipoEnum =
                    OrdemDeServico.TipoServicoOS.fromLabel(cbb_Tipo.getSelectedItem().toString());
            OrdemDeServico.TipoManutencao manutEnum =
                    OrdemDeServico.TipoManutencao.fromLabel(cbb_Manutencao.getSelectedItem().toString());

            controller.abrirOSDeOrcamento(titulo, tipoEnum.name(), manutEnum.name(),
                    data, km, selecionado.getIdVeiculo(), selecionado.getIdOrcamento());

            DialogoAlerta.sucesso(this, "Ordem de Serviço aberta com sucesso!\nOrçamento: " + selecionado.getCodigo(), "Sucesso");
            carregarDados();
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao abrir OS: " + ex.getMessage(), "Erro no Sistema");
        }
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    private JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(COR_LABEL);
        return l;
    }

    private JLabel rotuloInfo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(COR_LABEL);
        return l;
    }

    private JLabel valorInfo() {
        JLabel l = new JLabel("—");
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(COR_TEXTO_CAMPO);
        return l;
    }

    private JPanel campoVertical(String rotuloTexto, Component comp) {
        JPanel c = new JPanel(new BorderLayout(0, 4));
        c.setOpaque(false);
        c.add(rotulo(rotuloTexto), BorderLayout.NORTH);
        c.add(comp, BorderLayout.CENTER);
        return c;
    }

    private static class ItemOrcamento {
        final Orcamento orcamento;
        private final String textoGuia;

        ItemOrcamento(Orcamento o, String textoGuia) {
            this.orcamento = o;
            this.textoGuia = textoGuia;
        }

        @Override
        public String toString() {
            if (orcamento == null) return textoGuia != null ? textoGuia : "";
            String cod = orcamento.getCodigo() == null || orcamento.getCodigo().isEmpty()
                    ? "#" + orcamento.getIdOrcamento() : orcamento.getCodigo();
            return cod + "  —  " + orcamento.getNomeCliente()
                    + "  —  " + orcamento.getPlacaVeiculo()
                    + "  —  " + String.format("R$ %.2f", orcamento.getValor());
        }
    }

    private static class FiltroTexto extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            super.insertString(fb, off, filtrar(text), attr);
        }
        @Override
        public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            super.replace(fb, off, len, filtrar(text), attr);
        }
        private String filtrar(String t) {
            return t == null ? "" : t.replaceAll("[^a-zA-ZÀ-ÿ0-9\\s'\\-./,!?()+]", "");
        }
    }

    private static class FiltroDigitos extends DocumentFilter {
        private final int max;
        FiltroDigitos(int max) { this.max = max; }
        @Override
        public void insertString(FilterBypass fb, int off, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.replaceAll("[^0-9]", "") : "";
            if (fb.getDocument().getLength() + novo.length() <= max)
                super.insertString(fb, off, novo, attr);
        }
        @Override
        public void replace(FilterBypass fb, int off, int len, String text, AttributeSet attr) throws BadLocationException {
            String novo = text != null ? text.replaceAll("[^0-9]", "") : "";
            if (fb.getDocument().getLength() - len + novo.length() <= max)
                super.replace(fb, off, len, novo, attr);
        }
    }
}