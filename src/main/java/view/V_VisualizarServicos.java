package view;

import br.com.oficina.atendimento.dto.ServicoResponseDTO;
import controller.OficinaController;
import model.Orcamento;
import model.OrdemDeServico;
import model.Veiculo;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class V_VisualizarServicos extends JPanel {

    // =========================================================================
    // PALETA E MEDIDAS — mexa só aqui para alterar tudo de uma vez
    // =========================================================================
    private static final Color COR_FUNDO_PAGINA = Color.decode("#FAFBFC");
    private static final Color COR_CARD_TOPO    = Color.decode("#EFF1F4");
    private static final Color COR_CARD_BASE    = Color.decode("#DFE4EA");
    private static final Color COR_TITULO       = Color.decode("#4A5568");
    private static final Color COR_LABEL        = Color.decode("#57626F");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");

    // Cores do popup da lista suspensa — precisa ser SÓLIDO (o vidro é só na caixa fechada)
    private static final Color COR_POPUP_FUNDO   = Color.decode("#FFFFFF");
    private static final Color COR_POPUP_SELECAO = Color.decode("#FFE4BF");
    private static final Color COR_POPUP_BORDA   = Color.decode("#C3CDDA");

    // Ação principal: "+ Criar OS" (tema laranja original preservado)
    private static final Color COR_ACAO         = Color.decode("#FF9900");
    private static final Color COR_ACAO_CLARA   = Color.decode("#FFAD33");
    private static final Color COR_ACAO_ESCURA  = Color.decode("#E68A00");

    // Ação secundária: "Gerar OS do selecionado" (tema azul-petróleo original preservado)
    private static final Color COR_INFO         = Color.decode("#17A2B8");
    private static final Color COR_INFO_CLARA   = Color.decode("#33B8CC");
    private static final Color COR_INFO_ESCURA  = Color.decode("#148A9C");

    // Seção "Orçamentos pendentes" — identidade âmbar preservada
    private static final Color COR_PEND_TEXTO       = Color.decode("#B8860B");
    private static final Color COR_PEND_BORDA       = Color.decode("#FFD580");
    private static final Color COR_PEND_CARD_TOPO   = Color.decode("#FFFDF6");
    private static final Color COR_PEND_CARD_BASE   = Color.decode("#FFF3D6");
    private static final Color COR_PEND_BTN         = Color.decode("#B8860B");
    private static final Color COR_PEND_BTN_CLARA   = Color.decode("#C79A2A");
    private static final Color COR_PEND_BTN_ESCURA  = Color.decode("#9C7209");

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
    private static final Color COR_TABELA_GRADE   = Color.decode("#E3E9F0");
    private static final Color COR_TABELA_SELECAO = Color.decode("#FFE4BF");

    private static final int RAIO_COMPONENTE     = 12;
    private static final int TAMANHO_FONTE_CAMPO = 12;
    private static final int ALTURA_CAMPO        = 32;
    private static final int TAMANHO_FONTE_BOTAO = 12;
    private static final int ALTURA_BOTAO        = 34;
    private static final int ALTURA_CABECALHO    = 26;
    private static final int ALTURA_LINHA_TABELA = 24;

    private final OficinaController controller;

    private List<ServicoResponseDTO> todos = new ArrayList<>();
    private List<Orcamento> orcamentosPendentes = new ArrayList<>();
    private Map<Long, Veiculo> veicMap = new HashMap<>();

    private GlassTextField txt_Busca;
    private JComboBox<String> cmb_Tipo;
    private JComboBox<String> cmb_Status;

    private JPanel corpo;
    private JScrollPane scroll;

    private DefaultTableModel mdl_Pendentes;
    private JTable tbl_Pendentes;
    private JPanel pnl_Pendentes;
    private JPanel pnl_PendentesCorpo;
    private BotaoAcao btn_TogglePendentes;

    private static final String[] STATUS_LABELS = {"Todos os status", "Aberta", "Em Andamento", "Concluída"};
    private static final String[] STATUS_CODES  = {null, "ABERTA", "EM_ANDAMENTO", "CONCLUIDA"};

    public V_VisualizarServicos(OficinaController controller) {
        this.controller = controller;
        setBackground(COR_FUNDO_PAGINA);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        construir();
        carregar();
    }

    private void construir() {
        // ---- Breadcrumb + botão criar OS ----
        JLabel lbl_Titulo = new JLabel("Página Inicial > Consultar Serviços");
        lbl_Titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Titulo.setForeground(COR_TITULO);

        BotaoAcao btn_CriarOS = new BotaoAcao("+ Criar OS", COR_ACAO, COR_ACAO_CLARA, COR_ACAO_ESCURA);
        btn_CriarOS.setPreferredSize(new Dimension(140, ALTURA_BOTAO));
        btn_CriarOS.addActionListener(e -> navegar(new V_CadastrarServico(controller)));

        JPanel pnl_Linha1 = new JPanel(new BorderLayout());
        pnl_Linha1.setOpaque(false);
        pnl_Linha1.add(lbl_Titulo, BorderLayout.WEST);
        pnl_Linha1.add(btn_CriarOS, BorderLayout.EAST);

        // ---- Filtros ----
        txt_Busca = new GlassTextField();
        txt_Busca.setPreferredSize(new Dimension(240, ALTURA_CAMPO));
        txt_Busca.putClientProperty("JTextField.placeholderText", "Buscar por título ou veículo...");

        // Combo tipo: "Todos os tipos" + cada TipoServicoOS
        String[] tiposLabels = new String[OrdemDeServico.TipoServicoOS.values().length + 1];
        tiposLabels[0] = "Todos os tipos";
        for (int i = 0; i < OrdemDeServico.TipoServicoOS.values().length; i++)
            tiposLabels[i + 1] = OrdemDeServico.TipoServicoOS.values()[i].getLabel();
        cmb_Tipo = new GlassComboBox<>(tiposLabels);
        cmb_Tipo.setPreferredSize(new Dimension(165, ALTURA_CAMPO));

        cmb_Status = new GlassComboBox<>(STATUS_LABELS);
        cmb_Status.setPreferredSize(new Dimension(175, ALTURA_CAMPO));

        JLabel lbl_B = new JLabel("Buscar:");
        JLabel lbl_T = new JLabel("Tipo:");
        JLabel lbl_S = new JLabel("Status:");
        for (JLabel l : new JLabel[]{lbl_B, lbl_T, lbl_S}) {
            l.setFont(new Font("Segoe UI", Font.BOLD, 12));
            l.setForeground(COR_LABEL);
        }

        JPanel pnl_Filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnl_Filtros.setOpaque(false);
        pnl_Filtros.add(lbl_B); pnl_Filtros.add(txt_Busca);
        pnl_Filtros.add(Box.createHorizontalStrut(6));
        pnl_Filtros.add(lbl_T); pnl_Filtros.add(cmb_Tipo);
        pnl_Filtros.add(Box.createHorizontalStrut(6));
        pnl_Filtros.add(lbl_S); pnl_Filtros.add(cmb_Status);

        // ---- Seção orçamentos pendentes (colapsável) ----
        pnl_Pendentes = new PainelGradiente(new BorderLayout(0, 6), COR_PEND_CARD_TOPO, COR_PEND_CARD_BASE);
        pnl_Pendentes.setVisible(false);
        pnl_Pendentes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_PEND_BORDA),
                BorderFactory.createEmptyBorder(8, 10, 10, 10)));

        JPanel pnl_PendentesHeader = new JPanel(new BorderLayout());
        pnl_PendentesHeader.setOpaque(false);
        JLabel lbl_Pend = new JLabel("⚠  Orçamentos aprovados aguardando OS");
        lbl_Pend.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl_Pend.setForeground(COR_PEND_TEXTO);
        btn_TogglePendentes = new BotaoAcao("Mostrar ▾", COR_PEND_BTN, COR_PEND_BTN_CLARA, COR_PEND_BTN_ESCURA);
        btn_TogglePendentes.setPreferredSize(new Dimension(100, 26));
        pnl_PendentesHeader.add(lbl_Pend, BorderLayout.WEST);
        pnl_PendentesHeader.add(btn_TogglePendentes, BorderLayout.EAST);

        pnl_PendentesCorpo = new JPanel(new BorderLayout(0, 4));
        pnl_PendentesCorpo.setOpaque(false);
        pnl_PendentesCorpo.setVisible(false);

        String[] colsPend = {"Cód.", "Cliente", "Veículo", "Responsável", "Valor (R$)"};
        mdl_Pendentes = new DefaultTableModel(colsPend, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbl_Pendentes = new JTable(mdl_Pendentes);
        estilizarTabela(tbl_Pendentes);
        tbl_Pendentes.setRowHeight(ALTURA_LINHA_TABELA);
        JScrollPane scp_Pend = new JScrollPane(tbl_Pendentes);
        scp_Pend.getViewport().setBackground(COR_TABELA_FUNDO);
        scp_Pend.getViewport().setOpaque(true);
        scp_Pend.setOpaque(false);
        scp_Pend.setBorder(BorderFactory.createLineBorder(COR_PEND_BORDA));
        scp_Pend.setPreferredSize(new Dimension(0, 100));
        ScrollBarPadrao.aplicar(scp_Pend);

        BotaoAcao btn_GerarOS = new BotaoAcao("Gerar OS do selecionado →", COR_INFO, COR_INFO_CLARA, COR_INFO_ESCURA);
        btn_GerarOS.setPreferredSize(new Dimension(210, 32));
        btn_GerarOS.addActionListener(e -> gerarOSDeOrcamento());
        JPanel pnl_BtnGerar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnl_BtnGerar.setOpaque(false);
        pnl_BtnGerar.add(btn_GerarOS);

        pnl_PendentesCorpo.add(scp_Pend, BorderLayout.CENTER);
        pnl_PendentesCorpo.add(pnl_BtnGerar, BorderLayout.SOUTH);

        btn_TogglePendentes.addActionListener(e -> {
            boolean abrir = !pnl_PendentesCorpo.isVisible();
            pnl_PendentesCorpo.setVisible(abrir);
            btn_TogglePendentes.setText(abrir ? "Ocultar ▴" : "Mostrar ▾");
        });

        pnl_Pendentes.add(pnl_PendentesHeader, BorderLayout.NORTH);
        pnl_Pendentes.add(pnl_PendentesCorpo, BorderLayout.CENTER);

        // ---- Norte: empilha breadcrumb + pendentes + filtros ----
        JPanel pnl_Norte = new JPanel();
        pnl_Norte.setLayout(new BoxLayout(pnl_Norte, BoxLayout.Y_AXIS));
        pnl_Norte.setOpaque(false);
        pnl_Linha1.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_Norte.add(pnl_Linha1);
        pnl_Norte.add(Box.createVerticalStrut(10));
        pnl_Pendentes.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_Norte.add(pnl_Pendentes);
        pnl_Norte.add(Box.createVerticalStrut(8));
        pnl_Filtros.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_Norte.add(pnl_Filtros);
        pnl_Norte.add(Box.createVerticalStrut(10));

        // ---- Corpo rolável com grupos ----
        corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setOpaque(false);

        scroll = new JScrollPane(corpo);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        ScrollBarPadrao.aplicar(scroll);

        add(pnl_Norte, BorderLayout.NORTH);
        add(scroll,    BorderLayout.CENTER);

        // ---- Listeners filtros ----
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { atualizarCorpo(); }
            public void removeUpdate(DocumentEvent e)  { atualizarCorpo(); }
            public void changedUpdate(DocumentEvent e) { atualizarCorpo(); }
        };
        txt_Busca.getDocument().addDocumentListener(dl);
        cmb_Tipo.addActionListener(e -> atualizarCorpo());
        cmb_Status.addActionListener(e -> atualizarCorpo());
    }

    private void carregar() {
        // Veículos
        veicMap.clear();
        for (Veiculo v : controller.listarVeiculos()) veicMap.put(v.getIdVeiculo(), v);

        // OS
        todos = controller.listarTodosServicos();

        // Orçamentos pendentes
        orcamentosPendentes = controller.listarOrcamentosAprovadosSemOS();
        mdl_Pendentes.setRowCount(0);
        for (Orcamento o : orcamentosPendentes) {
            String cod = o.getCodigo() != null && !o.getCodigo().isEmpty()
                    ? o.getCodigo() : String.format("%04d", o.getIdOrcamento());
            mdl_Pendentes.addRow(new Object[]{
                    cod, o.getNomeCliente(), o.getPlacaVeiculo(),
                    o.getResponsavel(), String.format("R$ %.2f", o.getValor())
            });
        }
        pnl_Pendentes.setVisible(!orcamentosPendentes.isEmpty());

        atualizarCorpo();
    }

    private void atualizarCorpo() {
        String busca       = txt_Busca.getText().trim().toLowerCase();
        int idxTipo        = cmb_Tipo.getSelectedIndex();
        int idxStatus      = cmb_Status.getSelectedIndex();
        String statusCode  = (idxStatus > 0) ? STATUS_CODES[idxStatus] : null;
        String tipoLabel   = (idxTipo > 0)
                ? OrdemDeServico.TipoServicoOS.values()[idxTipo - 1].getLabel() : null;

        List<ServicoResponseDTO> filtrados = todos.stream()
                .filter(dto -> statusCode == null || statusCode.equalsIgnoreCase(dto.status()))
                .filter(dto -> tipoLabel == null ||
                        (dto.tipoServico() != null &&
                                OrdemDeServico.TipoServicoOS.fromName(dto.tipoServico()).getLabel().equals(tipoLabel)))
                .filter(dto -> {
                    if (busca.isEmpty()) return true;
                    String titulo  = dto.titulo() != null ? dto.titulo().toLowerCase() : "";
                    Veiculo v = dto.idVeiculo() != null ? veicMap.get(dto.idVeiculo()) : null;
                    String placa   = v != null ? v.getPlaca().toLowerCase() : "";
                    String modelo  = v != null && v.getModelo() != null ? v.getModelo().getNome().toLowerCase() : "";
                    return titulo.contains(busca) || placa.contains(busca) || modelo.contains(busca);
                })
                .collect(Collectors.toList());

        corpo.removeAll();

        if (filtrados.isEmpty()) {
            JLabel vazio = new JLabel(todos.isEmpty()
                    ? "Nenhuma Ordem de Serviço cadastrada. Clique em '+ Criar OS' para começar."
                    : "Nenhuma O.S. corresponde ao filtro.");
            vazio.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            vazio.setForeground(COR_LABEL);
            vazio.setAlignmentX(Component.LEFT_ALIGNMENT);
            vazio.setBorder(BorderFactory.createEmptyBorder(20, 4, 0, 0));
            corpo.add(vazio);
        } else {
            corpo.add(criarCardGrupos(filtrados));
        }

        corpo.revalidate();
        corpo.repaint();
    }

    private JPanel criarCardGrupos(List<ServicoResponseDTO> itens) {
        JPanel card = new PainelGradiente(new BorderLayout(0, 12), COR_CARD_TOPO, COR_CARD_BASE);
        card.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel("Ordens de Serviço  (" + itens.size() + ")");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(COR_ACAO);
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COR_CARD_BASE));
        lbl.setPreferredSize(new Dimension(0, 32));
        card.add(lbl, BorderLayout.NORTH);

        JPanel gruposPanel = new JPanel();
        gruposPanel.setLayout(new BoxLayout(gruposPanel, BoxLayout.Y_AXIS));
        gruposPanel.setOpaque(false);

        // Agrupa por TipoServicoOS preservando ordem do enum
        Map<OrdemDeServico.TipoServicoOS, List<ServicoResponseDTO>> grupos = new LinkedHashMap<>();
        for (OrdemDeServico.TipoServicoOS t : OrdemDeServico.TipoServicoOS.values())
            grupos.put(t, new ArrayList<>());
        for (ServicoResponseDTO dto : itens) {
            OrdemDeServico.TipoServicoOS tipo = OrdemDeServico.TipoServicoOS.fromName(dto.tipoServico());
            grupos.get(tipo).add(dto);
        }

        for (Map.Entry<OrdemDeServico.TipoServicoOS, List<ServicoResponseDTO>> entry : grupos.entrySet()) {
            if (entry.getValue().isEmpty()) continue;

            JLabel lblTipo = new JLabel("  ▸  " + entry.getKey().getLabel());
            lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblTipo.setForeground(COR_LABEL);
            lblTipo.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
            lblTipo.setAlignmentX(Component.LEFT_ALIGNMENT);
            gruposPanel.add(lblTipo);
            gruposPanel.add(criarTabelaOS(entry.getValue()));
            gruposPanel.add(Box.createVerticalStrut(4));
        }

        card.add(gruposPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarTabelaOS(List<ServicoResponseDTO> itens) {
        String[] cols = {"Cód.", "Título", "Status", "Manutenção", "Veículo", "Data"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (ServicoResponseDTO dto : itens) {
            Veiculo v = dto.idVeiculo() != null ? veicMap.get(dto.idVeiculo()) : null;
            String veiculo = v != null
                    ? (v.getModelo() != null
                    ? v.getModelo().getNome() + "  ·  " + v.getPlaca()
                    : v.getPlaca())
                    : "—";
            String statusLabel = formatarStatus(dto.status());
            String manut = formatarManut(dto.tipoManutencao());
            String data  = dto.dataServico() != null && !dto.dataServico().isBlank() ? dto.dataServico() : "—";
            mdl.addRow(new Object[]{dto.idServico(), dto.titulo(), statusLabel, manut, veiculo, data});
        }

        JTable tabela = new JTable(mdl);
        estilizarTabela(tabela);
        tabela.setRowHeight(ALTURA_LINHA_TABELA);
        tabela.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Larguras fixas
        tabela.getColumnModel().getColumn(0).setMinWidth(45);
        tabela.getColumnModel().getColumn(0).setMaxWidth(55);
        tabela.getColumnModel().getColumn(2).setMinWidth(110);
        tabela.getColumnModel().getColumn(2).setMaxWidth(130);
        tabela.getColumnModel().getColumn(3).setMinWidth(95);
        tabela.getColumnModel().getColumn(3).setMaxWidth(110);
        tabela.getColumnModel().getColumn(5).setMinWidth(90);
        tabela.getColumnModel().getColumn(5).setMaxWidth(100);

        // Renderer colorido para Status (col 2) — sobrepõe o CelulaBrancaRenderer padrão só nessa coluna
        tabela.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                c.setHorizontalAlignment(SwingConstants.CENTER);
                c.setFont(new Font("Segoe UI", Font.BOLD, 11));
                c.setOpaque(true);
                if (!sel) {
                    switch (String.valueOf(val)) {
                        case "Aberta"       -> { c.setForeground(Color.decode("#C0392B")); c.setBackground(Color.decode("#FDECEA")); }
                        case "Em Andamento" -> { c.setForeground(Color.decode("#9A6700")); c.setBackground(Color.decode("#FEF6E4")); }
                        case "Concluída"    -> { c.setForeground(Color.decode("#1E8449")); c.setBackground(Color.decode("#E9F7EF")); }
                        default             -> { c.setForeground(COR_TEXTO_CAMPO); c.setBackground(COR_TABELA_FUNDO); }
                    }
                } else {
                    c.setForeground(Color.WHITE);
                    c.setBackground(tabela.getSelectionBackground());
                }
                return c;
            }
        });

        // Duplo clique → abre V_OrdemServico
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() != 2) return;
                int row = tabela.getSelectedRow();
                if (row < 0) return;
                try {
                    long id = Long.parseLong(String.valueOf(tabela.getValueAt(row, 0)).trim());
                    navegar(new V_OrdemServico(controller, id));
                } catch (NumberFormatException ignored) {}
            }
        });

        JScrollPane sp = new JScrollPane(tabela);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        int altTabela = tabela.getRowHeight() * itens.size() + ALTURA_CABECALHO;
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, altTabela));
        sp.setPreferredSize(new Dimension(0, altTabela));
        sp.getViewport().setBackground(COR_TABELA_FUNDO);
        ScrollBarPadrao.aplicar(sp);
        sp.getViewport().setOpaque(true);
        sp.setOpaque(false);
        sp.setBorder(BorderFactory.createLineBorder(COR_AERO_BORDA));

        JLabel dica = new JLabel("Duplo clique para abrir a O.S.");
        dica.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        dica.setForeground(COR_LABEL);
        dica.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

        JPanel wrapper = new JPanel(new BorderLayout(0, 2));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, altTabela + 20));
        wrapper.setPreferredSize(new Dimension(0, altTabela + 20));
        wrapper.add(sp, BorderLayout.CENTER);
        wrapper.add(dica, BorderLayout.SOUTH);
        return wrapper;
    }

    /** Aplica o vidro Aero cinza no cabeçalho e fundo branco nas células, igual às demais telas. */
    private void estilizarTabela(JTable tabela) {
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.setBackground(COR_TABELA_FUNDO);
        tabela.setForeground(COR_TEXTO_CAMPO);
        tabela.setOpaque(true);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setSelectionBackground(COR_TABELA_SELECAO);
        tabela.setSelectionForeground(COR_TEXTO_CAMPO);
        tabela.setDefaultRenderer(Object.class, new CelulaBrancaRenderer());

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setDefaultRenderer(new CabecalhoVidroClaro());
        cabecalho.setPreferredSize(new Dimension(cabecalho.getPreferredSize().width, ALTURA_CABECALHO));
        cabecalho.setReorderingAllowed(false);
        cabecalho.setOpaque(false);
    }

    private void gerarOSDeOrcamento() {
        int linha = tbl_Pendentes.getSelectedRow();
        if (linha < 0 || linha >= orcamentosPendentes.size()) {
            DialogoAlerta.aviso(this, "Selecione um orçamento na tabela.", "Atenção");
            return;
        }
        Orcamento o = orcamentosPendentes.get(linha);

        JTextField txtTitulo = new JTextField();
        JComboBox<String> cmbTipo = new JComboBox<>();
        for (OrdemDeServico.TipoServicoOS t : OrdemDeServico.TipoServicoOS.values())
            cmbTipo.addItem(t.getLabel());
        JComboBox<String> cmbManut = new JComboBox<>();
        for (OrdemDeServico.TipoManutencao m : OrdemDeServico.TipoManutencao.values())
            cmbManut.addItem(m.getLabel());
        JTextField txtData = new JTextField(LocalDate.now().toString());
        JTextField txtKm   = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 4));
        form.add(new JLabel("Título da OS:"));     form.add(txtTitulo);
        form.add(new JLabel("Tipo de Serviço:")); form.add(cmbTipo);
        form.add(new JLabel("Tipo de Manutenção:")); form.add(cmbManut);
        form.add(new JLabel("Data (AAAA-MM-DD):")); form.add(txtData);
        form.add(new JLabel("KM atual:"));        form.add(txtKm);

        String codOrc = (o.getCodigo() != null && !o.getCodigo().isEmpty())
                ? o.getCodigo() : String.format("%04d", o.getIdOrcamento());
        int res = JOptionPane.showConfirmDialog(this, form,
                "Gerar OS — Orçamento " + codOrc,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String titulo = txtTitulo.getText().trim();
        if (titulo.length() < 3) {
            DialogoAlerta.aviso(this, "Título deve ter pelo menos 3 caracteres.", "Atenção"); return;
        }
        int km;
        try {
            km = Integer.parseInt(txtKm.getText().trim());
            if (km < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            DialogoAlerta.aviso(this, "Informe um KM válido.", "Atenção"); return;
        }
        try {
            OrdemDeServico.TipoServicoOS tipoEnum = OrdemDeServico.TipoServicoOS.fromLabel((String) cmbTipo.getSelectedItem());
            OrdemDeServico.TipoManutencao manutEnum = OrdemDeServico.TipoManutencao.fromLabel((String) cmbManut.getSelectedItem());
            controller.abrirOSDeOrcamento(titulo, tipoEnum.name(), manutEnum.name(),
                    txtData.getText().trim(), km, o.getIdVeiculo(), o.getIdOrcamento());
            DialogoAlerta.sucesso(this, "Ordem de Serviço gerada com sucesso!", "Sucesso");
            carregar();
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro ao gerar OS: " + ex.getMessage(), "Erro no Sistema");
        }
    }

    private String formatarStatus(String raw) {
        if (raw == null) return "—";
        return switch (raw.toUpperCase()) {
            case "ABERTA"        -> "Aberta";
            case "EM_ANDAMENTO"  -> "Em Andamento";
            case "CONCLUIDA"     -> "Concluída";
            default              -> raw;
        };
    }

    private String formatarManut(String raw) {
        if (raw == null) return "—";
        return switch (raw.toUpperCase()) {
            case "PREVENTIVA" -> "Preventiva";
            case "CORRETIVA"  -> "Corretiva";
            default           -> raw;
        };
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    // =========================================================================
    // INNER CLASSES — RENDERERS DA TABELA
    // =========================================================================

    /** Cabeçalho de coluna com vidro cinza claro no estilo Aero (Windows 7). */
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

            g2.setColor(new Color(255, 255, 255, 90));
            g2.fillRect(0, 0, w, Math.max(1, h / 6));

            g2.setColor(COR_AERO_SEPARA);
            g2.drawLine(w - 1, 3, w - 1, h - 4);

            g2.setColor(COR_AERO_BORDA);
            g2.drawLine(0, h - 1, w, h - 1);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** Células de dados sempre com fundo branco (e destaque no tom do tema quando selecionadas). */
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

    // =========================================================================
    // INNER CLASSES — COMPONENTES EM VIDRO
    // =========================================================================

    /** Campo de texto com efeito de vidro translúcido: cantos arredondados, sombra leve, reflexo no topo. */
    private static class GlassTextField extends JTextField {
        private boolean focado = false;

        GlassTextField() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
            setCaretColor(COR_TEXTO_CAMPO);
            setSelectionColor(new Color(255, 153, 0, 90));
            setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; repaint(); }
                @Override public void focusLost(FocusEvent e) { focado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(70, 90, 110, 28));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            GradientPaint vidro = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 210),
                    0, h, new Color(255, 255, 255, 145)
            );
            g2.setPaint(vidro);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            g2.setColor(new Color(255, 255, 255, 110));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            Color corBorda = focado ? new Color(255, 153, 0, 210) : new Color(160, 175, 195, 130);
            float espessura = focado ? 1.6f : 1f;
            g2.setStroke(new BasicStroke(espessura));
            g2.setColor(corBorda);
            g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.75, h - 2.25, RAIO_COMPONENTE, RAIO_COMPONENTE));
            g2.dispose();
        }
    }

    /** Painel com fundo em gradiente suave, harmonizando cada cartão com o restante das telas. */
    private static class PainelGradiente extends JPanel {
        private final Color corTopo;
        private final Color corBase;

        PainelGradiente(LayoutManager layout, Color corTopo, Color corBase) {
            super(layout);
            this.corTopo = corTopo;
            this.corBase = corBase;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, corTopo, 0, getHeight(), corBase);
            g2.setPaint(gp);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Botão de ação com a mesma linguagem visual dos campos em vidro: cantos
     * arredondados, sombra suave, reflexo no topo e reação a hover/clique.
     * Cor configurável — laranja (ação principal), azul-petróleo (gerar OS)
     * ou âmbar (mostrar/ocultar pendentes).
     */
    private static class BotaoAcao extends JButton {
        private final Color corBase;
        private final Color corClara;
        private final Color corEscura;
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoAcao(String texto, Color corBase, Color corClara, Color corEscura) {
            super(texto);
            this.corBase = corBase;
            this.corClara = corClara;
            this.corEscura = corEscura;
            setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_BOTAO));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
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

            g2.setColor(new Color(0, 0, 0, 35));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            Color corPreenchimento = pressionado ? corEscura : (sobreMouse ? corClara : corBase);
            g2.setColor(corPreenchimento);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            g2.setColor(new Color(255, 255, 255, 30));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /** JComboBox com o mesmo efeito de vidro dos campos de texto. */
    private static class GlassComboBox<T> extends JComboBox<T> {
        private boolean focado = false;

        GlassComboBox() { super(); estilizar(); }
        GlassComboBox(T[] itens) { super(itens); estilizar(); }

        private void estilizar() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
            // Não usar cor transparente: o BasicComboPopup copia este background
            // para a lista suspensa, e um alpha 0 deixaria o popup transparente.
            setBackground(COR_POPUP_FUNDO);
            setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 4));
            setFocusable(true);
            setUI(new GlassComboBoxUI());
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focado = true; repaint(); }
                @Override public void focusLost(FocusEvent e) { focado = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(70, 90, 110, 28));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            GradientPaint vidro = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 210),
                    0, h, new Color(255, 255, 255, 145)
            );
            g2.setPaint(vidro);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, RAIO_COMPONENTE, RAIO_COMPONENTE));

            g2.setColor(new Color(255, 255, 255, 110));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            Color corBorda = focado ? new Color(255, 153, 0, 210) : new Color(160, 175, 195, 130);
            float espessura = focado ? 1.6f : 1f;
            g2.setStroke(new BasicStroke(espessura));
            g2.setColor(corBorda);
            g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.75, h - 2.25, RAIO_COMPONENTE, RAIO_COMPONENTE));
            g2.dispose();
        }
    }

    /**
     * UI do GlassComboBox: impede que o Swing pinte fundo sólido (inclusive o
     * azul de seleção quando em foco) por cima do vidro, troca a seta padrão
     * por um triângulo vetorial e usa um popup arredondado.
     */
    private static class GlassComboBoxUI extends BasicComboBoxUI {
        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // Vazio de propósito — o fundo já é pintado em GlassComboBox.paintComponent().
        }

        @Override
        @SuppressWarnings("unchecked")
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            ListCellRenderer renderer = comboBox.getRenderer();
            Component c = renderer.getListCellRendererComponent(
                    listBox, comboBox.getSelectedItem(), -1, false, false);
            c.setFont(comboBox.getFont());
            c.setForeground(comboBox.isEnabled() ? COR_TEXTO_CAMPO : Color.GRAY);

            boolean opacoOriginal = false;
            if (c instanceof JComponent) {
                opacoOriginal = ((JComponent) c).isOpaque();
                ((JComponent) c).setOpaque(false);
            }

            boolean shouldValidate = c instanceof JPanel;
            currentValuePane.paintComponent(g, c, comboBox, bounds.x, bounds.y, bounds.width, bounds.height, shouldValidate);

            if (c instanceof JComponent) {
                ((JComponent) c).setOpaque(opacoOriginal);
            }
        }

        @Override
        protected ComboPopup createPopup() {
            return new GlassComboPopup(comboBox);
        }

        @Override
        protected JButton createArrowButton() {
            JButton seta = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight();
                    int cx = w / 2, cy = h / 2;
                    Path2D triangulo = new Path2D.Double();
                    triangulo.moveTo(cx - 4, cy - 2);
                    triangulo.lineTo(cx + 4, cy - 2);
                    triangulo.lineTo(cx, cy + 3);
                    triangulo.closePath();
                    g2.setColor(COR_LABEL);
                    g2.fill(triangulo);
                    g2.dispose();
                }
            };
            seta.setPreferredSize(new Dimension(20, 20));
            seta.setContentAreaFilled(false);
            seta.setBorderPainted(false);
            seta.setFocusPainted(false);
            seta.setOpaque(false);
            seta.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return seta;
        }
    }

    /** Popup do combo com cantos arredondados e conteúdo sólido/legível. */
    private static class GlassComboPopup extends BasicComboPopup {

        GlassComboPopup(JComboBox<Object> combo) {
            super(combo);
        }

        @Override
        protected void configurePopup() {
            super.configurePopup();
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        }

        @Override
        protected void configureList() {
            super.configureList();
            list.setOpaque(true);
            list.setBackground(COR_POPUP_FUNDO);
            list.setForeground(COR_TEXTO_CAMPO);
            list.setSelectionBackground(COR_POPUP_SELECAO);
            list.setSelectionForeground(COR_TEXTO_CAMPO);
            list.setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
        }

        @Override
        protected JScrollPane createScroller() {
            JScrollPane scroller = super.createScroller();
            scroller.setOpaque(false);
            scroller.getViewport().setOpaque(false);
            scroller.setBorder(BorderFactory.createEmptyBorder());
            ScrollBarPadrao.aplicar(scroller);
            return scroller;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(COR_POPUP_FUNDO);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, RAIO_COMPONENTE, RAIO_COMPONENTE));
            g2.setStroke(new BasicStroke(1f));
            g2.setColor(COR_POPUP_BORDA);
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 1, h - 1, RAIO_COMPONENTE, RAIO_COMPONENTE));

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public void show() {
            super.show();
            try {
                Window janela = SwingUtilities.getWindowAncestor(this);
                if (janela != null) {
                    janela.setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), RAIO_COMPONENTE, RAIO_COMPONENTE));
                }
            } catch (Exception | Error ignorado) {
                // Sem suporte a formato de janela nesta plataforma.
            }
        }
    }
}