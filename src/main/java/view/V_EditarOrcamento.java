package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
import br.com.oficina.atendimento.dto.OrcamentoResponseDTO;
import br.com.oficina.estoque.CatalogoPecaEntity;
import br.com.oficina.estoque.PecaEntity;
import controller.OficinaController;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Edição de itens de serviço e peças de um orçamento existente.
 * Campos de cliente/veículo/responsável/reclamação são somente-leitura.
 *
 * Visual alinhado ao mesmo padrão em vidro (glassmorphism) usado em
 * V_CadastrarOrcamento / V_VisualizarOrcamento — mesma paleta e os mesmos
 * renderizadores de tabela (CabecalhoVidroClaro + CelulaBrancaRenderer).
 */
public class V_EditarOrcamento extends JPanel {

    // =========================================================================
    // PALETA E MEDIDAS — mesma linguagem visual de V_CadastrarOrcamento / V_VisualizarOrcamento
    // =========================================================================
    private static final Color COR_FUNDO_PAGINA = Color.decode("#FAFBFC");
    private static final Color COR_CARD_TOPO    = Color.decode("#EFF1F4");
    private static final Color COR_CARD_BASE    = Color.decode("#DFE4EA");
    private static final Color COR_BORDA_CARD   = Color.decode("#DDE3EA");
    private static final Color COR_TITULO       = Color.decode("#4A5568");
    private static final Color COR_LABEL        = Color.decode("#57626F");
    private static final Color COR_LABEL_SUTIL  = Color.decode("#8A94A3");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");

    // Vidro cinza claro estilo Windows 7 (Aero) — cabeçalho das tabelas
    private static final Color COR_AERO_TOPO_A  = Color.decode("#FBFBFC");
    private static final Color COR_AERO_TOPO_B  = Color.decode("#ECEEF1");
    private static final Color COR_AERO_BASE_A  = Color.decode("#DADDE2");
    private static final Color COR_AERO_BASE_B  = Color.decode("#EFF1F3");
    private static final Color COR_AERO_BORDA   = Color.decode("#B6BCC4");
    private static final Color COR_AERO_SEPARA  = Color.decode("#CCD1D8");
    private static final Color COR_AERO_TEXTO   = Color.decode("#3A4149");

    // Tabela de dados
    private static final Color COR_TABELA_FUNDO   = Color.WHITE;
    private static final Color COR_TABELA_SELECAO = Color.decode("#FFE4BF");

    private static final int ALTURA_CABECALHO = 26;
    private static final int ALTURA_LINHA     = 26;

    private final OficinaController controller;
    private final long idOrcamento;

    // Header de leitura
    private JPanel pnl_InfoHeader;

    // Itens de serviço
    private JComboBox<CatalogoServicoEntity> cmb_ItemCatalogo;
    private JComboBox<SistemaItem> cmb_FiltroSistema;
    private JTextField txt_BuscaServico;
    private final List<CatalogoServicoEntity> todosItens = new ArrayList<>();
    private DefaultTableModel mdl_ItensSelecionados;
    private JTable tbl_ItensSelecionados;
    private JScrollPane scroll_Itens;
    private JLabel lbl_Total;
    private final List<CatalogoServicoEntity> itensSelecionados = new ArrayList<>();
    private final List<Double> valoresItensSelecionados = new ArrayList<>();

    // Peças
    private JComboBox<ItemPeca> cmb_Peca;
    private JTextField txt_BuscaPeca;
    private final List<CatalogoPecaEntity> todasPecasCatalogo = new ArrayList<>();
    private JTextField txt_NomeTecnicoPeca;
    private JTextField txt_FabricantePeca;
    private JTextField txt_QuantidadePeca;
    private DefaultTableModel mdl_PecasSelecionadas;
    private JTable tbl_PecasSelecionadas;
    private JScrollPane scroll_Pecas;
    private final List<Long> pecasSelecionadas = new ArrayList<>();
    private final List<Double> valoresPecasSelecionadas = new ArrayList<>();
    private final List<String> nomesTecnicosPecas = new ArrayList<>();
    private final List<String> fabricantesPecas = new ArrayList<>();
    private final List<Integer> quantidadesPecas = new ArrayList<>();

    private JButton btn_Salvar;

    public V_EditarOrcamento(OficinaController controller, long idOrcamento) {
        this.controller = controller;
        this.idOrcamento = idOrcamento;
        setLayout(new GridBagLayout());
        setBackground(COR_FUNDO_PAGINA);
        initComponents();
        carregarDados();
        carregarCatalogo();
        carregarPecas();
        vincularAcoes();
    }

    private void initComponents() {
        JPanel card = new PainelGradiente(new BorderLayout(0, 14), COR_CARD_TOPO, COR_CARD_BASE);
        card.setPreferredSize(new Dimension(680, 740));
        card.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JPanel headerGlass = new CabecalhoVidro();
        headerGlass.setLayout(new BorderLayout());
        headerGlass.setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
        headerGlass.setPreferredSize(new Dimension(10, 42));
        headerGlass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JLabel titulo = new JLabel("Orçamentos > Editar Orçamento");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(COR_AERO_TEXTO);
        headerGlass.add(titulo, BorderLayout.WEST);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        pnl_InfoHeader = new PainelGradiente(new GridLayout(0, 2, 8, 4), COR_CARD_TOPO, COR_CARD_BASE, COR_BORDA_CARD);
        pnl_InfoHeader.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
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
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 900));

        JLabel lbl = new JLabel("Itens de Serviço");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(COR_TITULO);

        cmb_ItemCatalogo = new JComboBox<>();
        cmb_ItemCatalogo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_ItemCatalogo.setBackground(Color.WHITE);
        estilizarCombo(cmb_ItemCatalogo);
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
        estilizarCombo(cmb_FiltroSistema);
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
        lbl_LocalFiltro.setForeground(COR_LABEL);
        lbl_LocalFiltro.setPreferredSize(new Dimension(46, 28));

        JPanel pnl_FiltroRow = new JPanel(new BorderLayout(8, 0));
        pnl_FiltroRow.setOpaque(false);
        pnl_FiltroRow.add(lbl_LocalFiltro, BorderLayout.WEST);
        pnl_FiltroRow.add(cmb_FiltroSistema, BorderLayout.CENTER);

        txt_BuscaServico = new CampoArredondado();
        txt_BuscaServico.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_BuscaServico.setToolTipText("Digite para buscar um serviço pelo nome");
        txt_BuscaServico.setPreferredSize(new Dimension(0, 28));
        BuscaUtil.aoDigitar(txt_BuscaServico, this::filtrarCatalogo);

        JLabel lbl_Busca = new JLabel("Buscar:");
        lbl_Busca.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl_Busca.setForeground(COR_LABEL);
        lbl_Busca.setPreferredSize(new Dimension(46, 28));

        JPanel pnl_BuscaRow = new JPanel(new BorderLayout(8, 0));
        pnl_BuscaRow.setOpaque(false);
        pnl_BuscaRow.add(lbl_Busca, BorderLayout.WEST);
        pnl_BuscaRow.add(txt_BuscaServico, BorderLayout.CENTER);

        JButton btn_Add = botaoAcao("+ Adicionar", "#28A745");
        btn_Add.addActionListener(e -> adicionarItem());

        JPanel pnl_AddRow = new JPanel(new BorderLayout(8, 0));
        pnl_AddRow.setOpaque(false);
        pnl_AddRow.add(cmb_ItemCatalogo, BorderLayout.CENTER);
        pnl_AddRow.add(btn_Add, BorderLayout.EAST);

        JPanel pnl_NorteRows = new JPanel(new GridLayout(3, 1, 0, 4));
        pnl_NorteRows.setOpaque(false);
        pnl_NorteRows.add(pnl_BuscaRow);
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
        scroll_Itens = scrollTabela(tbl_ItensSelecionados, ALTURA_CABECALHO + ALTURA_LINHA);

        JButton btn_Rem = botaoRemover("Remover selecionado");
        btn_Rem.addActionListener(e -> {
            int row = tbl_ItensSelecionados.getSelectedRow();
            if (row >= 0) {
                itensSelecionados.remove(row);
                valoresItensSelecionados.remove(row);
                mdl_ItensSelecionados.removeRow(row);
                ajustarAlturaTabela(scroll_Itens, tbl_ItensSelecionados);
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
        corpo.add(scroll_Itens, BorderLayout.CENTER);
        corpo.add(pnl_Rodape, BorderLayout.SOUTH);

        sec.add(lbl, BorderLayout.NORTH);
        sec.add(corpo, BorderLayout.CENTER);
        return sec;
    }

    private JPanel criarSecaoPecas() {
        JPanel sec = new JPanel(new BorderLayout(0, 6));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 900));

        JLabel lbl = new JLabel("Peças a Substituir");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(COR_TITULO);

        cmb_Peca = new JComboBox<>();
        cmb_Peca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_Peca.setBackground(Color.WHITE);
        estilizarCombo(cmb_Peca);

        txt_BuscaPeca = new CampoArredondado();
        txt_BuscaPeca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_BuscaPeca.setToolTipText("Digite para buscar uma peça pelo nome popular");
        txt_BuscaPeca.setPreferredSize(new Dimension(0, 32));
        BuscaUtil.aoDigitar(txt_BuscaPeca, this::filtrarPecas);

        JLabel lbl_BuscaPeca = new JLabel("Buscar peça:");
        lbl_BuscaPeca.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl_BuscaPeca.setForeground(COR_LABEL_SUTIL);

        JPanel pnl_BuscaPeca = new JPanel(new BorderLayout(0, 2));
        pnl_BuscaPeca.setOpaque(false);
        pnl_BuscaPeca.add(lbl_BuscaPeca, BorderLayout.NORTH);
        pnl_BuscaPeca.add(txt_BuscaPeca, BorderLayout.CENTER);

        txt_NomeTecnicoPeca = new CampoArredondado();
        txt_NomeTecnicoPeca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_NomeTecnicoPeca.setToolTipText("Ex: Filtro Mann W811/80");
        txt_NomeTecnicoPeca.setPreferredSize(new Dimension(0, 32));

        txt_FabricantePeca = new CampoArredondado();
        txt_FabricantePeca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_FabricantePeca.setToolTipText("Ex: Mann, Bosch, NGK");
        txt_FabricantePeca.setPreferredSize(new Dimension(0, 32));

        txt_QuantidadePeca = new CampoArredondado();
        txt_QuantidadePeca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_QuantidadePeca.setText("1");
        txt_QuantidadePeca.setToolTipText("Quantas unidades desta peça serão trocadas nesta OS");
        txt_QuantidadePeca.setPreferredSize(new Dimension(0, 32));

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
        lbl_NomeTec.setForeground(COR_LABEL_SUTIL);
        pnl_NomeTec.add(lbl_NomeTec, BorderLayout.NORTH);
        pnl_NomeTec.add(txt_NomeTecnicoPeca, BorderLayout.CENTER);

        JPanel pnl_Fabricante = new JPanel(new BorderLayout(0, 2));
        pnl_Fabricante.setOpaque(false);
        JLabel lbl_Fabricante = new JLabel("Fabricante da Peça (opcional)");
        lbl_Fabricante.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl_Fabricante.setForeground(COR_LABEL_SUTIL);
        pnl_Fabricante.add(lbl_Fabricante, BorderLayout.NORTH);
        pnl_Fabricante.add(txt_FabricantePeca, BorderLayout.CENTER);

        JPanel pnl_Quantidade = new JPanel(new BorderLayout(0, 2));
        pnl_Quantidade.setOpaque(false);
        JLabel lbl_Quantidade = new JLabel("Quantidade");
        lbl_Quantidade.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl_Quantidade.setForeground(COR_LABEL_SUTIL);
        pnl_Quantidade.add(lbl_Quantidade, BorderLayout.NORTH);
        pnl_Quantidade.add(txt_QuantidadePeca, BorderLayout.CENTER);

        String[] cols = {"Peça", "Nome Técnico", "Fabricante", "Qtd.", "Valor Unit. (R$)"};
        mdl_PecasSelecionadas = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return c >= 1; }
        };
        mdl_PecasSelecionadas.addTableModelListener(ev -> {
            if (ev.getType() != TableModelEvent.UPDATE) return;
            int row = ev.getFirstRow();
            int col = ev.getColumn();
            if (row < 0) return;
            if (col == 4 && row < valoresPecasSelecionadas.size()) {
                try {
                    String txt = String.valueOf(mdl_PecasSelecionadas.getValueAt(row, 4)).replace(",", ".");
                    valoresPecasSelecionadas.set(row, Double.parseDouble(txt));
                } catch (NumberFormatException ex) {
                    valoresPecasSelecionadas.set(row, 0.0);
                    mdl_PecasSelecionadas.setValueAt("0.00", row, 4);
                }
                atualizarTotal();
            } else if (col == 3 && row < quantidadesPecas.size()) {
                try {
                    int qtd = Integer.parseInt(String.valueOf(mdl_PecasSelecionadas.getValueAt(row, 3)).trim());
                    if (qtd <= 0) throw new NumberFormatException();
                    quantidadesPecas.set(row, qtd);
                } catch (NumberFormatException ex) {
                    quantidadesPecas.set(row, 1);
                    mdl_PecasSelecionadas.setValueAt(1, row, 3);
                }
                atualizarTotal();
            } else if (col == 1 && row < nomesTecnicosPecas.size()) {
                nomesTecnicosPecas.set(row, String.valueOf(mdl_PecasSelecionadas.getValueAt(row, 1)));
            } else if (col == 2 && row < fabricantesPecas.size()) {
                fabricantesPecas.set(row, String.valueOf(mdl_PecasSelecionadas.getValueAt(row, 2)));
            }
        });
        tbl_PecasSelecionadas = criarTabela(mdl_PecasSelecionadas);
        tbl_PecasSelecionadas.getColumnModel().getColumn(3).setMaxWidth(60);
        scroll_Pecas = scrollTabela(tbl_PecasSelecionadas, ALTURA_CABECALHO + ALTURA_LINHA);

        JButton btn_RemPeca = botaoRemover("Remover selecionada");
        btn_RemPeca.addActionListener(e -> {
            int row = tbl_PecasSelecionadas.getSelectedRow();
            if (row >= 0) {
                pecasSelecionadas.remove(row);
                valoresPecasSelecionadas.remove(row);
                nomesTecnicosPecas.remove(row);
                fabricantesPecas.remove(row);
                quantidadesPecas.remove(row);
                mdl_PecasSelecionadas.removeRow(row);
                ajustarAlturaTabela(scroll_Pecas, tbl_PecasSelecionadas);
                atualizarTotal();
            }
        });

        JPanel pnl_Rodape = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnl_Rodape.setOpaque(false);
        pnl_Rodape.add(btn_RemPeca);

        JPanel pnl_Norte = new JPanel();
        pnl_Norte.setLayout(new BoxLayout(pnl_Norte, BoxLayout.Y_AXIS));
        pnl_Norte.setOpaque(false);
        pnl_Norte.add(pnl_BuscaPeca);
        pnl_Norte.add(Box.createVerticalStrut(6));
        pnl_Norte.add(pnl_Row);
        pnl_Norte.add(Box.createVerticalStrut(6));
        pnl_Norte.add(pnl_NomeTec);
        pnl_Norte.add(Box.createVerticalStrut(6));
        pnl_Norte.add(pnl_Fabricante);
        pnl_Norte.add(Box.createVerticalStrut(6));
        pnl_Norte.add(pnl_Quantidade);

        JPanel corpo = new JPanel(new BorderLayout(0, 6));
        corpo.setOpaque(false);
        corpo.add(pnl_Norte, BorderLayout.NORTH);
        corpo.add(scroll_Pecas, BorderLayout.CENTER);
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

            // Cada unidade é uma linha em orcamento_peca (mesmo id_peca repetido) — agrupa para
            // reconstituir a quantidade e mostrar uma única linha editável por peça.
            java.util.LinkedHashMap<Long, Object[]> pecasAgrupadas = new java.util.LinkedHashMap<>();
            java.util.Map<Long, Integer> qtdPorIdPeca = new java.util.LinkedHashMap<>();
            for (Object[] triple : controller.listarPecasOrcamentoComValor(idOrcamento)) {
                PecaEntity peca = (PecaEntity) triple[0];
                double valor = (Double) triple[1];
                String nomeTecnico = triple.length > 2 ? (String) triple[2] : "";
                String fabricante = triple.length > 3 ? (String) triple[3] : "";
                long idPeca = peca.getIdPeca();
                pecasAgrupadas.putIfAbsent(idPeca, new Object[]{peca, valor, nomeTecnico, fabricante});
                qtdPorIdPeca.merge(idPeca, 1, Integer::sum);
            }
            for (var entry : pecasAgrupadas.entrySet()) {
                Object[] dados = entry.getValue();
                PecaEntity peca = (PecaEntity) dados[0];
                double valor = (Double) dados[1];
                String nomeTecnico = (String) dados[2];
                String fabricante = (String) dados[3];
                int quantidade = qtdPorIdPeca.get(entry.getKey());
                pecasSelecionadas.add(peca.getIdCatalogoPeca());
                valoresPecasSelecionadas.add(valor);
                nomesTecnicosPecas.add(nomeTecnico);
                fabricantesPecas.add(fabricante);
                quantidadesPecas.add(quantidade);
                mdl_PecasSelecionadas.addRow(new Object[]{peca.getNomeExibicao(), nomeTecnico, fabricante,
                        quantidade, String.format("%.2f", valor)});
            }
            ajustarAlturaTabela(scroll_Itens, tbl_ItensSelecionados);
            ajustarAlturaTabela(scroll_Pecas, tbl_PecasSelecionadas);
            atualizarTotal();
        } catch (Exception e) {
            DialogoAlerta.erro(this, "Erro ao carregar orçamento: " + e.getMessage(), "Erro");
        }
    }

    /** Redimensiona a área da tabela para caber todas as linhas sem precisar rolar internamente. */
    private void ajustarAlturaTabela(JScrollPane scroll, JTable tbl) {
        int linhas = Math.max(1, tbl.getRowCount());
        int altura = ALTURA_CABECALHO + linhas * ALTURA_LINHA + 4;
        scroll.setPreferredSize(new Dimension(0, altura));
        revalidate();
        repaint();
    }

    private void addInfo(String rotulo, String valor) {
        JLabel lbl = new JLabel(rotulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(COR_LABEL);
        JLabel val = new JLabel(valor);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        val.setForeground(COR_TEXTO_CAMPO);
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
        String termo = txt_BuscaServico != null ? txt_BuscaServico.getText() : "";
        for (CatalogoServicoEntity item : todosItens) {
            boolean bateSistema = cod.isEmpty() || cod.equals(item.getSistema());
            if (bateSistema && BuscaUtil.contem(item.getNome(), termo))
                cmb_ItemCatalogo.addItem(item);
        }
    }

    private void carregarPecas() {
        todasPecasCatalogo.clear();
        todasPecasCatalogo.addAll(controller.listarTodasPecas());
        filtrarPecas();
    }

    private void filtrarPecas() {
        cmb_Peca.removeAllItems();
        String termo = txt_BuscaPeca != null ? txt_BuscaPeca.getText() : "";
        boolean alguma = false;
        for (CatalogoPecaEntity p : todasPecasCatalogo) {
            if (BuscaUtil.contem(p.getNomeExibicao(), termo)) {
                cmb_Peca.addItem(new ItemPeca(p));
                alguma = true;
            }
        }
        if (!alguma) cmb_Peca.addItem(new ItemPeca(null));
    }

    private void adicionarItem() {
        CatalogoServicoEntity item = (CatalogoServicoEntity) cmb_ItemCatalogo.getSelectedItem();
        if (item == null) return;
        itensSelecionados.add(item);
        valoresItensSelecionados.add(item.getValor());
        String tipo = "REVISAO".equals(item.getTipo()) ? "Revisão" : "Padrão";
        mdl_ItensSelecionados.addRow(new Object[]{item.getNome(), tipo, String.format("%.2f", item.getValor())});
        ajustarAlturaTabela(scroll_Itens, tbl_ItensSelecionados);
        atualizarTotal();
    }

    private void adicionarPeca() {
        ItemPeca sel = (ItemPeca) cmb_Peca.getSelectedItem();
        if (sel == null || sel.peca == null) return;
        String nomeTecnico = txt_NomeTecnicoPeca.getText().trim();
        String fabricante = txt_FabricantePeca.getText().trim();
        int quantidade;
        try {
            String qtdTxt = txt_QuantidadePeca.getText().trim();
            quantidade = qtdTxt.isEmpty() ? 1 : Integer.parseInt(qtdTxt);
            if (quantidade <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            DialogoAlerta.aviso(this, "Informe uma quantidade válida maior que zero.", "Campo Inválido");
            return;
        }
        pecasSelecionadas.add(sel.peca.getIdCatalogoPeca());
        valoresPecasSelecionadas.add(0.0);
        nomesTecnicosPecas.add(nomeTecnico);
        fabricantesPecas.add(fabricante);
        quantidadesPecas.add(quantidade);
        mdl_PecasSelecionadas.addRow(new Object[]{sel.peca.getNomeExibicao(), nomeTecnico, fabricante, quantidade, "0.00"});
        txt_NomeTecnicoPeca.setText("");
        txt_FabricantePeca.setText("");
        txt_QuantidadePeca.setText("1");
        ajustarAlturaTabela(scroll_Pecas, tbl_PecasSelecionadas);
        atualizarTotal();
    }

    private void atualizarTotal() {
        double totalPecas = 0;
        for (int i = 0; i < valoresPecasSelecionadas.size(); i++) {
            int qtd = i < quantidadesPecas.size() ? quantidadesPecas.get(i) : 1;
            totalPecas += valoresPecasSelecionadas.get(i) * qtd;
        }
        double total = valoresItensSelecionados.stream().mapToDouble(Double::doubleValue).sum() + totalPecas;
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
                        new ArrayList<>(nomesTecnicosPecas), new ArrayList<>(fabricantesPecas),
                        new ArrayList<>(quantidadesPecas));
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
        final CatalogoPecaEntity peca;
        ItemPeca(CatalogoPecaEntity p) { this.peca = p; }
        @Override public String toString() {
            return peca != null ? peca.getNomeExibicao() : "(nenhuma peça cadastrada)";
        }
    }
    private static class SistemaItem {
        final String codigo, label;
        SistemaItem(String c, String l) { this.codigo = c; this.label = l; }
        @Override public String toString() { return label; }
    }

    /** Cabeçalho de coluna com vidro cinza claro no estilo Aero (Windows 7), igual às demais telas. */
    private static class CabecalhoVidroClaro extends JLabel implements TableCellRenderer {

        CabecalhoVidroClaro() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(COR_AERO_TEXTO);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int meio = h / 2;

            g2.setPaint(new GradientPaint(0, 0, COR_AERO_TOPO_A, 0, meio, COR_AERO_TOPO_B));
            g2.fillRect(0, 0, w, meio);

            g2.setPaint(new GradientPaint(0, meio, COR_AERO_BASE_A, 0, h, COR_AERO_BASE_B));
            g2.fillRect(0, meio, w, h - meio);

            g2.setColor(new Color(255, 255, 255, 140));
            g2.fillRect(0, 0, w, Math.max(1, h / 5));

            g2.setColor(COR_AERO_SEPARA);
            g2.drawLine(w - 1, 3, w - 1, h - 4);

            g2.setColor(COR_AERO_BORDA);
            g2.drawLine(0, h - 1, w, h - 1);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Células de dados sempre com fundo branco (e destaque âmbar quando selecionadas), igual às demais telas. */
    private static class CelulaBrancaRenderer extends DefaultTableCellRenderer {
        CelulaBrancaRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(isSelected ? COR_TABELA_SELECAO : COR_TABELA_FUNDO);
            setForeground(COR_TEXTO_CAMPO);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            return this;
        }
    }

    /** Painel com fundo em gradiente suave, opcionalmente com uma borda arredondada colorida. */
    private static class PainelGradiente extends JPanel {
        private final Color corTopo;
        private final Color corBase;
        private final Color corBorda;

        PainelGradiente(LayoutManager layout, Color corTopo, Color corBase) {
            this(layout, corTopo, corBase, null);
        }

        PainelGradiente(LayoutManager layout, Color corTopo, Color corBase, Color corBorda) {
            super(layout);
            this.corTopo = corTopo;
            this.corBase = corBase;
            this.corBorda = corBorda;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            GradientPaint gp = new GradientPaint(0, 0, corTopo, 0, h, corBase);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, 14, 14));

            if (corBorda != null) {
                g2.setStroke(new BasicStroke(1.3f));
                g2.setColor(corBorda);
                g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.5, h - 1.5, 14, 14));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ===== helpers visuais =====
    private JButton botaoAcao(String texto, String corHex) {
        BotaoVidro btn = new BotaoVidro(texto, Color.decode(corHex));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btn.setPreferredSize(new Dimension(120, 34));
        return btn;
    }

    private JButton botaoRemover(String texto) {
        BotaoVidro btn = new BotaoVidro(texto, Color.decode("#DC3545"));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btn.setPreferredSize(new Dimension(170, 30));
        return btn;
    }

    /** Combo com fundo e borda arredondados (mesmo raio dos campos de texto). */
    private static void estilizarCombo(JComboBox<?> cmb) {
        cmb.setUI(new ComboArredondadoUI());
        cmb.setOpaque(false);
        cmb.setBorder(BorderFactory.createEmptyBorder(1, 8, 1, 4));
    }

    /** Monta a tabela com o mesmo vidro Aero cinza no cabeçalho e fundo branco nas células das demais telas. */
    private JTable criarTabela(DefaultTableModel model) {
        JTable tbl = new JTable(model);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl.setForeground(COR_TEXTO_CAMPO);
        tbl.setBackground(COR_TABELA_FUNDO);
        tbl.setOpaque(true);
        tbl.setRowHeight(ALTURA_LINHA);
        tbl.setShowGrid(false);
        tbl.setIntercellSpacing(new Dimension(0, 0));
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.setSelectionBackground(COR_TABELA_SELECAO);
        tbl.setSelectionForeground(COR_TEXTO_CAMPO);
        tbl.setDefaultRenderer(Object.class, new CelulaBrancaRenderer());

        JTableHeader header = tbl.getTableHeader();
        header.setDefaultRenderer(new CabecalhoVidroClaro());
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, ALTURA_CABECALHO));
        header.setReorderingAllowed(false);
        header.setOpaque(false);
        return tbl;
    }

    private JScrollPane scrollTabela(JTable tbl, int altura) {
        JScrollPane scp = new JScrollPane(tbl);
        scp.setBorder(BorderFactory.createLineBorder(COR_AERO_BORDA));
        scp.setOpaque(false);
        scp.getViewport().setBackground(COR_TABELA_FUNDO);
        scp.getViewport().setOpaque(true);
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

    /** Campo de texto com fundo e borda arredondados; borda laranja quando focado. */
    private static class CampoArredondado extends JTextField {
        private static final int RAIO = 14;
        private boolean focado = false;

        CampoArredondado() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(2, 12, 2, 12));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; repaint(); }
                @Override public void focusLost(FocusEvent e)   { focado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, RAIO, RAIO));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(focado ? Color.decode("#FF9900") : Color.decode("#CCCCCC"));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, RAIO, RAIO));
            g2.dispose();
        }
    }

    /** UI de combo com cantos arredondados e seta simples. */
    private static class ComboArredondadoUI extends BasicComboBoxUI {
        private static final int RAIO = 14;

        @Override
        protected JButton createArrowButton() {
            JButton b = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Color.decode("#666666"));
                    int cx = getWidth() / 2, cy = getHeight() / 2;
                    g2.fillPolygon(new int[]{cx - 4, cx + 4, cx}, new int[]{cy - 2, cy - 2, cy + 3}, 3);
                    g2.dispose();
                }
            };
            b.setContentAreaFilled(false);
            b.setBorderPainted(false);
            b.setFocusPainted(false);
            b.setOpaque(false);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return b;
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D forma = new RoundRectangle2D.Double(0.5, 0.5, c.getWidth() - 1, c.getHeight() - 1, RAIO, RAIO);
            g2.setColor(Color.WHITE);
            g2.fill(forma);
            g2.setColor(Color.decode("#CCCCCC"));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(forma);
            g2.dispose();
            super.paint(g, c);
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // fundo já pintado em paint(), com cantos arredondados
        }

        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            super.paintCurrentValue(g, bounds, false); // evita o retângulo azul de seleção
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

            GradientPaint gp = new GradientPaint(0, 0, comAlpha(clarear(corPreenchimento), 220),
                    0, h, comAlpha(corPreenchimento, 190));
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

            // filete interno claro: reforça o efeito de vidro
            g2.setColor(new Color(255, 255, 255, 110));
            g2.draw(new RoundRectangle2D.Double(1.5, 1.5, w - 4, h - 6, raio - 2, raio - 2));

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