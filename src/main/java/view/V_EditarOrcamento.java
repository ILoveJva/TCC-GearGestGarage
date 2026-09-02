package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
import br.com.oficina.atendimento.dto.OrcamentoResponseDTO;
import br.com.oficina.estoque.PecaEntity;
import controller.OficinaController;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Edição de itens de serviço e peças de um orçamento existente.
 * Campos de cliente/veículo/responsável/reclamação são somente-leitura.
 */
public class V_EditarOrcamento extends JPanel {

    private final OficinaController controller;
    private final long idOrcamento;

    // Header de leitura
    private JPanel pnl_InfoHeader;

    // Itens de serviço
    private JComboBox<CatalogoServicoEntity> cmb_ItemCatalogo;
    private JComboBox<SistemaItem> cmb_FiltroSistema;
    private final List<CatalogoServicoEntity> todosItens = new ArrayList<>();
    private DefaultTableModel mdl_ItensSelecionados;
    private JTable tbl_ItensSelecionados;
    private JLabel lbl_Total;
    private final List<CatalogoServicoEntity> itensSelecionados = new ArrayList<>();
    private final List<Double> valoresItensSelecionados = new ArrayList<>();

    // Peças
    private JComboBox<ItemPeca> cmb_Peca;
    private JTextField txt_NomeTecnicoPeca;
    private JTextField txt_FabricantePeca;
    private DefaultTableModel mdl_PecasSelecionadas;
    private JTable tbl_PecasSelecionadas;
    private final List<Long> pecasSelecionadas = new ArrayList<>();
    private final List<Double> valoresPecasSelecionadas = new ArrayList<>();
    private final List<String> nomesTecnicosPecas = new ArrayList<>();
    private final List<String> fabricantesPecas = new ArrayList<>();

    private JButton btn_Salvar;

    public V_EditarOrcamento(OficinaController controller, long idOrcamento) {
        this.controller = controller;
        this.idOrcamento = idOrcamento;
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        initComponents();
        carregarDados();
        carregarCatalogo();
        carregarPecas();
        vincularAcoes();
    }

    private void initComponents() {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(640, 680));

        JPanel headerGlass = new CabecalhoVidro();
        headerGlass.setLayout(new BorderLayout());
        headerGlass.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        headerGlass.setPreferredSize(new Dimension(10, 42));
        headerGlass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel titulo = new JLabel("Editar Orçamento");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.decode("#3A4149"));
        headerGlass.add(titulo, BorderLayout.WEST);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);

        pnl_InfoHeader = new JPanel(new GridLayout(0, 2, 8, 4));
        pnl_InfoHeader.setBackground(Color.decode("#F5F5F5"));
        pnl_InfoHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#E0E0E0")),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        pnl_InfoHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_InfoHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        form.add(pnl_InfoHeader);
        form.add(Box.createVerticalStrut(14));
        form.add(criarSecaoItens());
        form.add(Box.createVerticalStrut(14));
        form.add(criarSecaoPecas());

        btn_Salvar = new BotaoVidro("💾 SALVAR ALTERAÇÕES", Color.decode("#FF9900"));
        btn_Salvar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn_Salvar.setPreferredSize(new Dimension(250, 45));

        BotaoVidro btn_Voltar = new BotaoVidro("← Cancelar", Color.decode("#8A94A3"));
        btn_Voltar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_Voltar.setPreferredSize(new Dimension(130, 40));
        btn_Voltar.addActionListener(e -> navegar(new V_VisualizarOrcamento(controller, controller.buscarOrcamentoModel(idOrcamento))));

        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        pnlBtn.setOpaque(false);
        pnlBtn.add(btn_Voltar);
        pnlBtn.add(btn_Salvar);

        card.add(headerGlass, BorderLayout.NORTH);
        card.add(new JScrollPane(form) {{
            setBorder(null);
            setOpaque(false);
            getViewport().setOpaque(false);
            ScrollBarPadrao.aplicar(this);
        }}, BorderLayout.CENTER);
        card.add(pnlBtn, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 20, 10, 20);
        add(card, gbc);
    }

    private JPanel criarSecaoItens() {
        JPanel sec = new JPanel(new BorderLayout(0, 6));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 310));

        JLabel lbl = new JLabel("Itens de Serviço");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(Color.decode("#333333"));

        cmb_ItemCatalogo = new JComboBox<>();
        cmb_ItemCatalogo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_ItemCatalogo.setBackground(Color.WHITE);
        cmb_ItemCatalogo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean focus) {
                super.getListCellRendererComponent(l, v, i, sel, focus);
                if (v instanceof CatalogoServicoEntity c) {
                    String tipo = "REVISAO".equals(c.getTipo()) ? "Revisão" : "Padrão";
                    setText(c.getNome() + "  [" + tipo + " – R$ " + String.format("%.2f", c.getValor()) + "]");
                }
                return this;
            }
        });

        cmb_FiltroSistema = new JComboBox<>();
        cmb_FiltroSistema.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_FiltroSistema.setBackground(Color.WHITE);
        cmb_FiltroSistema.addItem(new SistemaItem("",             "Todos os Sistemas"));
        cmb_FiltroSistema.addItem(new SistemaItem("MOTOR",        "Motor"));
        cmb_FiltroSistema.addItem(new SistemaItem("TRANSMISSAO",  "Transmissão"));
        cmb_FiltroSistema.addItem(new SistemaItem("DIRECAO",      "Direção"));
        cmb_FiltroSistema.addItem(new SistemaItem("SUSPENSAO",    "Suspensão"));
        cmb_FiltroSistema.addItem(new SistemaItem("FREIOS",       "Freios"));
        cmb_FiltroSistema.addItem(new SistemaItem("ARREFECIMENTO","Arrefecimento"));
        cmb_FiltroSistema.addItem(new SistemaItem("ELETRICA",     "Elétrica"));
        cmb_FiltroSistema.addItem(new SistemaItem("ALIMENTACAO",  "Alimentação"));
        cmb_FiltroSistema.addItem(new SistemaItem("OUTROS",       "Outros"));
        cmb_FiltroSistema.addActionListener(e -> filtrarCatalogo());

        JLabel lbl_LocalFiltro = new JLabel("Local:");
        lbl_LocalFiltro.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl_LocalFiltro.setForeground(Color.decode("#555555"));
        lbl_LocalFiltro.setPreferredSize(new Dimension(46, 28));

        JPanel pnl_FiltroRow = new JPanel(new BorderLayout(8, 0));
        pnl_FiltroRow.setOpaque(false);
        pnl_FiltroRow.add(lbl_LocalFiltro, BorderLayout.WEST);
        pnl_FiltroRow.add(cmb_FiltroSistema, BorderLayout.CENTER);

        JButton btn_Add = botaoAcao("+ Adicionar", "#28A745");
        btn_Add.addActionListener(e -> adicionarItem());

        JPanel pnl_AddRow = new JPanel(new BorderLayout(8, 0));
        pnl_AddRow.setOpaque(false);
        pnl_AddRow.add(cmb_ItemCatalogo, BorderLayout.CENTER);
        pnl_AddRow.add(btn_Add, BorderLayout.EAST);

        JPanel pnl_NorteRows = new JPanel(new GridLayout(2, 1, 0, 4));
        pnl_NorteRows.setOpaque(false);
        pnl_NorteRows.add(pnl_FiltroRow);
        pnl_NorteRows.add(pnl_AddRow);

        String[] cols = {"Serviço", "Tipo", "Valor (R$)"};
        mdl_ItensSelecionados = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return true; }
        };
        mdl_ItensSelecionados.addTableModelListener(ev -> {
            if (ev.getType() != TableModelEvent.UPDATE) return;
            int row = ev.getFirstRow();
            int col = ev.getColumn();
            if (row < 0 || col != 2 || row >= valoresItensSelecionados.size()) return;
            try {
                String txt = String.valueOf(mdl_ItensSelecionados.getValueAt(row, 2)).replace(",", ".");
                valoresItensSelecionados.set(row, Double.parseDouble(txt));
            } catch (NumberFormatException ex) {
                double def = itensSelecionados.get(row).getValor();
                valoresItensSelecionados.set(row, def);
                mdl_ItensSelecionados.setValueAt(String.format("%.2f", def), row, 2);
            }
            atualizarTotal();
        });
        tbl_ItensSelecionados = criarTabela(mdl_ItensSelecionados);
        JScrollPane scroll = scrollTabela(tbl_ItensSelecionados, 100);

        JButton btn_Rem = new JButton("Remover selecionado");
        btn_Rem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn_Rem.setForeground(Color.decode("#DC3545"));
        btn_Rem.setContentAreaFilled(false);
        btn_Rem.setBorderPainted(false);
        btn_Rem.setFocusPainted(false);
        btn_Rem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Rem.addActionListener(e -> {
            int row = tbl_ItensSelecionados.getSelectedRow();
            if (row >= 0) {
                itensSelecionados.remove(row);
                valoresItensSelecionados.remove(row);
                mdl_ItensSelecionados.removeRow(row);
                atualizarTotal();
            }
        });

        lbl_Total = new JLabel("Total: R$ 0,00");
        lbl_Total.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Total.setForeground(Color.decode("#FF9900"));
        lbl_Total.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel pnl_Rodape = new JPanel(new BorderLayout());
        pnl_Rodape.setOpaque(false);
        pnl_Rodape.add(btn_Rem, BorderLayout.WEST);
        pnl_Rodape.add(lbl_Total, BorderLayout.EAST);

        JPanel corpo = new JPanel(new BorderLayout(0, 6));
        corpo.setOpaque(false);
        corpo.add(pnl_NorteRows, BorderLayout.NORTH);
        corpo.add(scroll, BorderLayout.CENTER);
        corpo.add(pnl_Rodape, BorderLayout.SOUTH);

        sec.add(lbl, BorderLayout.NORTH);
        sec.add(corpo, BorderLayout.CENTER);
        return sec;
    }

    private JPanel criarSecaoPecas() {
        JPanel sec = new JPanel(new BorderLayout(0, 6));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));

        JLabel lbl = new JLabel("Peças a Substituir");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(Color.decode("#333333"));

        cmb_Peca = new JComboBox<>();
        cmb_Peca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_Peca.setBackground(Color.WHITE);

        txt_NomeTecnicoPeca = new JTextField();
        txt_NomeTecnicoPeca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_NomeTecnicoPeca.setToolTipText("Ex: Filtro Mann W811/80");
        txt_NomeTecnicoPeca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#CCCCCC")),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        txt_NomeTecnicoPeca.setPreferredSize(new Dimension(0, 32));

        txt_FabricantePeca = new JTextField();
        txt_FabricantePeca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_FabricantePeca.setToolTipText("Ex: Mann, Bosch, NGK");
        txt_FabricantePeca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#CCCCCC")),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        txt_FabricantePeca.setPreferredSize(new Dimension(0, 32));

        JButton btn_AddPeca = botaoAcao("+ Adicionar", "#17A2B8");
        btn_AddPeca.addActionListener(e -> adicionarPeca());

        JPanel pnl_Row = new JPanel(new BorderLayout(8, 0));
        pnl_Row.setOpaque(false);
        pnl_Row.add(cmb_Peca, BorderLayout.CENTER);
        pnl_Row.add(btn_AddPeca, BorderLayout.EAST);

        JPanel pnl_NomeTec = new JPanel(new BorderLayout(0, 2));
        pnl_NomeTec.setOpaque(false);
        JLabel lbl_NomeTec = new JLabel("Nome Técnico da Peça (opcional)");
        lbl_NomeTec.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl_NomeTec.setForeground(Color.decode("#888888"));
        pnl_NomeTec.add(lbl_NomeTec, BorderLayout.NORTH);
        pnl_NomeTec.add(txt_NomeTecnicoPeca, BorderLayout.CENTER);

        JPanel pnl_Fabricante = new JPanel(new BorderLayout(0, 2));
        pnl_Fabricante.setOpaque(false);
        JLabel lbl_Fabricante = new JLabel("Fabricante da Peça (opcional)");
        lbl_Fabricante.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl_Fabricante.setForeground(Color.decode("#888888"));
        pnl_Fabricante.add(lbl_Fabricante, BorderLayout.NORTH);
        pnl_Fabricante.add(txt_FabricantePeca, BorderLayout.CENTER);

        String[] cols = {"Peça", "Nome Técnico", "Fabricante", "Valor (R$)"};
        mdl_PecasSelecionadas = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c >= 1; }
        };
        mdl_PecasSelecionadas.addTableModelListener(ev -> {
            if (ev.getType() != TableModelEvent.UPDATE) return;
            int row = ev.getFirstRow();
            int col = ev.getColumn();
            if (row < 0) return;
            if (col == 3 && row < valoresPecasSelecionadas.size()) {
                try {
                    String txt = String.valueOf(mdl_PecasSelecionadas.getValueAt(row, 3)).replace(",", ".");
                    valoresPecasSelecionadas.set(row, Double.parseDouble(txt));
                } catch (NumberFormatException ex) {
                    valoresPecasSelecionadas.set(row, 0.0);
                    mdl_PecasSelecionadas.setValueAt("0.00", row, 3);
                }
                atualizarTotal();
            } else if (col == 1 && row < nomesTecnicosPecas.size()) {
                nomesTecnicosPecas.set(row, String.valueOf(mdl_PecasSelecionadas.getValueAt(row, 1)));
            } else if (col == 2 && row < fabricantesPecas.size()) {
                fabricantesPecas.set(row, String.valueOf(mdl_PecasSelecionadas.getValueAt(row, 2)));
            }
        });
        tbl_PecasSelecionadas = criarTabela(mdl_PecasSelecionadas);
        JScrollPane scroll = scrollTabela(tbl_PecasSelecionadas, 160);

        JButton btn_RemPeca = new JButton("Remover selecionada");
        btn_RemPeca.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn_RemPeca.setForeground(Color.decode("#DC3545"));
        btn_RemPeca.setContentAreaFilled(false);
        btn_RemPeca.setBorderPainted(false);
        btn_RemPeca.setFocusPainted(false);
        btn_RemPeca.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_RemPeca.addActionListener(e -> {
            int row = tbl_PecasSelecionadas.getSelectedRow();
            if (row >= 0) {
                pecasSelecionadas.remove(row);
                valoresPecasSelecionadas.remove(row);
                nomesTecnicosPecas.remove(row);
                fabricantesPecas.remove(row);
                mdl_PecasSelecionadas.removeRow(row);
                atualizarTotal();
            }
        });

        JPanel pnl_Rodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnl_Rodape.setOpaque(false);
        pnl_Rodape.add(btn_RemPeca);

        JPanel pnl_Norte = new JPanel();
        pnl_Norte.setLayout(new BoxLayout(pnl_Norte, BoxLayout.Y_AXIS));
        pnl_Norte.setOpaque(false);
        pnl_Norte.add(pnl_Row);
        pnl_Norte.add(Box.createVerticalStrut(6));
        pnl_Norte.add(pnl_NomeTec);
        pnl_Norte.add(Box.createVerticalStrut(6));
        pnl_Norte.add(pnl_Fabricante);

        JPanel corpo = new JPanel(new BorderLayout(0, 6));
        corpo.setOpaque(false);
        corpo.add(pnl_Norte, BorderLayout.NORTH);
        corpo.add(scroll, BorderLayout.CENTER);
        corpo.add(pnl_Rodape, BorderLayout.SOUTH);

        sec.add(lbl, BorderLayout.NORTH);
        sec.add(corpo, BorderLayout.CENTER);
        return sec;
    }

    private void carregarDados() {
        try {
            OrcamentoResponseDTO dto = controller.buscarOrcamento(idOrcamento);
            if (dto == null) return;

            pnl_InfoHeader.removeAll();
            addInfo("Cód. Orçamento:", dto.codigo() != null ? dto.codigo() : "#" + idOrcamento);
            addInfo("Status:", dto.status() != null ? dto.status() : "—");
            addInfo("Responsável:", dto.responsavel() != null ? dto.responsavel() : "—");
            pnl_InfoHeader.revalidate();

            for (var entry : controller.listarItensOrcamentoComValor(idOrcamento).entrySet()) {
                CatalogoServicoEntity item = entry.getKey();
                double valor = entry.getValue();
                itensSelecionados.add(item);
                valoresItensSelecionados.add(valor);
                String tipo = "REVISAO".equals(item.getTipo()) ? "Revisão" : "Padrão";
                mdl_ItensSelecionados.addRow(new Object[]{item.getNome(), tipo, String.format("%.2f", valor)});
            }

            for (Object[] triple : controller.listarPecasOrcamentoComValor(idOrcamento)) {
                PecaEntity peca = (PecaEntity) triple[0];
                double valor = (Double) triple[1];
                String nomeTecnico = triple.length > 2 ? (String) triple[2] : "";
                String fabricante = triple.length > 3 ? (String) triple[3] : "";
                pecasSelecionadas.add(peca.getIdPeca());
                valoresPecasSelecionadas.add(valor);
                nomesTecnicosPecas.add(nomeTecnico);
                fabricantesPecas.add(fabricante);
                mdl_PecasSelecionadas.addRow(new Object[]{peca.getNomeExibicao(), nomeTecnico, fabricante,
                        String.format("%.2f", valor)});
            }
            atualizarTotal();
        } catch (Exception e) {
            DialogoAlerta.erro(this, "Erro ao carregar orçamento: " + e.getMessage(), "Erro");
        }
    }

    private void addInfo(String rotulo, String valor) {
        JLabel lbl = new JLabel(rotulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.decode("#555555"));
        JLabel val = new JLabel(valor);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        pnl_InfoHeader.add(lbl);
        pnl_InfoHeader.add(val);
    }

    private void carregarCatalogo() {
        todosItens.clear();
        todosItens.addAll(controller.listarCatalogoServicos());
        filtrarCatalogo();
    }

    private void filtrarCatalogo() {
        cmb_ItemCatalogo.removeAllItems();
        SistemaItem sel = (SistemaItem) cmb_FiltroSistema.getSelectedItem();
        String cod = (sel != null && !sel.codigo.isEmpty()) ? sel.codigo : "";
        for (CatalogoServicoEntity item : todosItens) {
            if (cod.isEmpty() || cod.equals(item.getSistema()))
                cmb_ItemCatalogo.addItem(item);
        }
    }

    private void carregarPecas() {
        cmb_Peca.removeAllItems();
        List<PecaEntity> pecas = controller.listarTodasPecas();
        if (pecas.isEmpty()) cmb_Peca.addItem(new ItemPeca(null));
        else for (PecaEntity p : pecas) cmb_Peca.addItem(new ItemPeca(p));
    }

    private void adicionarItem() {
        CatalogoServicoEntity item = (CatalogoServicoEntity) cmb_ItemCatalogo.getSelectedItem();
        if (item == null) return;
        itensSelecionados.add(item);
        valoresItensSelecionados.add(item.getValor());
        String tipo = "REVISAO".equals(item.getTipo()) ? "Revisão" : "Padrão";
        mdl_ItensSelecionados.addRow(new Object[]{item.getNome(), tipo, String.format("%.2f", item.getValor())});
        atualizarTotal();
    }

    private void adicionarPeca() {
        ItemPeca sel = (ItemPeca) cmb_Peca.getSelectedItem();
        if (sel == null || sel.peca == null) return;
        String nomeTecnico = txt_NomeTecnicoPeca.getText().trim();
        String fabricante = txt_FabricantePeca.getText().trim();
        pecasSelecionadas.add(sel.peca.getIdPeca());
        valoresPecasSelecionadas.add(0.0);
        nomesTecnicosPecas.add(nomeTecnico);
        fabricantesPecas.add(fabricante);
        mdl_PecasSelecionadas.addRow(new Object[]{sel.peca.getNomeExibicao(), nomeTecnico, fabricante, "0.00"});
        txt_NomeTecnicoPeca.setText("");
        txt_FabricantePeca.setText("");
        atualizarTotal();
    }

    private void atualizarTotal() {
        double total = valoresItensSelecionados.stream().mapToDouble(Double::doubleValue).sum()
                + valoresPecasSelecionadas.stream().mapToDouble(Double::doubleValue).sum();
        lbl_Total.setText(String.format("Total: R$ %.2f", total));
    }

    private void vincularAcoes() {
        btn_Salvar.addActionListener(e -> {
            if (itensSelecionados.isEmpty()) {
                DialogoAlerta.aviso(this, "Adicione pelo menos um item de serviço.", "Campo Inválido");
                return;
            }
            try {
                controller.atualizarItensOrcamento(idOrcamento,
                        new ArrayList<>(itensSelecionados), new ArrayList<>(valoresItensSelecionados),
                        new ArrayList<>(pecasSelecionadas), new ArrayList<>(valoresPecasSelecionadas),
                        new ArrayList<>(nomesTecnicosPecas), new ArrayList<>(fabricantesPecas));
                DialogoAlerta.sucesso(this, "Orçamento atualizado com sucesso!", "Sucesso");
                navegar(new V_VisualizarOrcamento(controller, controller.buscarOrcamentoModel(idOrcamento)));
            } catch (Exception ex) {
                DialogoAlerta.erro(this, "Erro ao salvar: " + ex.getMessage(), "Erro no Sistema");
            }
        });
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    // ===== inner classes =====
    private static class ItemPeca {
        final PecaEntity peca;
        ItemPeca(PecaEntity p) { this.peca = p; }
        @Override public String toString() {
            return peca != null ? peca.getNomeExibicao() : "(nenhuma peça cadastrada)";
        }
    }
    private static class SistemaItem {
        final String codigo, label;
        SistemaItem(String c, String l) { this.codigo = c; this.label = l; }
        @Override public String toString() { return label; }
    }

    // ===== helpers visuais =====
    private JButton botaoAcao(String texto, String corHex) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.decode(corHex));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(120, 34));
        return btn;
    }

    private JTable criarTabela(DefaultTableModel model) {
        JTable tbl = new JTable(model);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl.setRowHeight(24);
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return tbl;
    }

    private JScrollPane scrollTabela(JTable tbl, int altura) {
        JScrollPane scp = new JScrollPane(tbl);
        scp.setBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC")));
        scp.setPreferredSize(new Dimension(0, altura));
        ScrollBarPadrao.aplicar(scp);
        return scp;
    }

    @SuppressWarnings("unused")
    private static class RoundedBorder implements javax.swing.border.Border {
        private final int raio; private final Color cor;
        RoundedBorder(int r, Color c) { this.raio = r; this.cor = c; }
        public Insets getBorderInsets(Component c) { return new Insets(raio/2, raio/2, raio/2, raio/2); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.draw(new RoundRectangle2D.Double(x, y, w-1, h-1, raio, raio));
            g2.dispose();
        }
    }

    /**
     * Cabeçalho fino em vidro Aero, usado na faixa de título no topo do card
     * (mesma linguagem visual usada nas demais telas do sistema).
     */
    private static class CabecalhoVidro extends JPanel {
        private static final Color TOPO_A = Color.decode("#FBFBFC");
        private static final Color TOPO_B = Color.decode("#ECEEF1");
        private static final Color BASE_A = Color.decode("#DADDE2");
        private static final Color BASE_B = Color.decode("#EFF1F3");
        private static final Color BORDA  = Color.decode("#B6BCC4");

        CabecalhoVidro() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int meio = h / 2;

            RoundRectangle2D corpo = new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, 14, 14);
            Shape clipAnterior = g2.getClip();
            g2.clip(corpo);

            g2.setPaint(new GradientPaint(0, 0, TOPO_A, 0, meio, TOPO_B));
            g2.fillRect(0, 0, w, meio);
            g2.setPaint(new GradientPaint(0, meio, BASE_A, 0, h, BASE_B));
            g2.fillRect(0, meio, w, h - meio);

            g2.setColor(new Color(255, 255, 255, 120));
            g2.fillRect(0, 0, w, Math.max(1, h / 4));

            g2.setClip(clipAnterior);

            g2.setColor(BORDA);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(corpo);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Botão de ação em vidro: preenchimento translúcido em gradiente, brilho
     * frosted no topo e reação a hover/clique — mesma linguagem visual usada
     * nos demais botões de ação do sistema.
     */
    private static class BotaoVidro extends JButton {
        private final Color corBase;
        private final Color corClara;
        private final Color corEscura;
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoVidro(String texto, Color corBase) {
            super(texto);
            this.corBase = corBase;
            this.corClara = clarear(corBase);
            this.corEscura = escurecer(corBase);
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e)  { sobreMouse = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)   { sobreMouse = false; repaint(); }
                @Override public void mousePressed(MouseEvent e)  { pressionado = true; repaint(); }
                @Override public void mouseReleased(MouseEvent e) { pressionado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int raio = 12;

            g2.setColor(new Color(0, 0, 0, 35));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, raio, raio));

            Color corPreenchimento = pressionado ? corEscura : (sobreMouse ? corClara : corBase);
            RoundRectangle2D corpo = new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, raio, raio);

            Shape clipAnterior = g2.getClip();
            g2.clip(corpo);

            GradientPaint gp = new GradientPaint(0, 0, comAlpha(clarear(corPreenchimento), 235),
                    0, h, comAlpha(corPreenchimento, 215));
            g2.setPaint(gp);
            g2.fill(corpo);

            g2.setColor(new Color(255, 255, 255, 60));
            g2.fill(new Ellipse2D.Double(-w * 0.1, -h * 0.7, w * 1.2, h * 1.4));

            g2.setColor(new Color(255, 255, 255, 45));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), raio - 5, raio - 5));

            g2.setClip(clipAnterior);

            g2.setColor(comAlpha(escurecer(corPreenchimento), 160));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(corpo);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ===== utilitários de cor =====
    private static Color clarear(Color c) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1] * 0.75f, Math.min(1f, hsb[2] * 1.18f));
    }

    private static Color escurecer(Color c) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2] * 0.82f);
    }

    private static Color comAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }
}