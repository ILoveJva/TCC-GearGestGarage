package view.paginas;

import br.com.oficina.atendimento.dto.ServicoResponseDTO;
import controller.OficinaController;
import model.OrdemDeServico;
import model.Veiculo;
import view.V_Main;
import view.V_OrdemServico;
import view.design.Botao;
import view.design.ComboBox;
import view.design.ScrollBar;
import view.design.Tabela;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tela de Visualização de Serviços refatorada utilizando o componente ComboBox padronizado do sistema.
 */
public class V_VisualizarServicos extends JPanel {

    private static final Color COR_FUNDO_PAGINA = Color.decode("#FAFBFC");
    private static final Color COR_LABEL = Color.decode("#57626F");
    private static final Color COR_TITULO = Color.decode("#4A5568");

    private final OficinaController controller;

    private List<ServicoResponseDTO> todos = new ArrayList<>();
    private Map<Long, Veiculo> veicMap = new HashMap<>();

    private JTextField txt_Busca;
    private ComboBox<String> cmb_Tipo;
    private ComboBox<String> cmb_Status;

    private JPanel corpo;
    private JScrollPane scroll;

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
        JLabel lbl_Titulo = new JLabel("Página Inicial > Consultar Serviços");
        lbl_Titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Titulo.setForeground(COR_TITULO);

        Botao.AcaoPrincipal btn_CriarOS = new Botao.AcaoPrincipal("+ Criar OS", null);
        btn_CriarOS.setPreferredSize(new Dimension(140, 34));
        btn_CriarOS.addActionListener(e -> navegar(new V_CadastrarServico(controller)));

        JPanel pnl_Linha1 = new JPanel(new BorderLayout());
        pnl_Linha1.setOpaque(false);
        pnl_Linha1.add(lbl_Titulo, BorderLayout.WEST);
        pnl_Linha1.add(btn_CriarOS, BorderLayout.EAST);

        txt_Busca = new JTextField();
        txt_Busca.setPreferredSize(new Dimension(240, 32));
        txt_Busca.putClientProperty("JTextField.placeholderText", "Buscar por título ou veículo...");

        String[] tiposLabels = new String[OrdemDeServico.TipoServicoOS.values().length + 1];
        tiposLabels[0] = "Todos os tipos";
        for (int i = 0; i < OrdemDeServico.TipoServicoOS.values().length; i++)
            tiposLabels[i + 1] = OrdemDeServico.TipoServicoOS.values()[i].getLabel();

        cmb_Tipo = new ComboBox<>(tiposLabels);
        cmb_Tipo.setPreferredSize(new Dimension(165, 32));

        cmb_Status = new ComboBox<>(STATUS_LABELS);
        cmb_Status.setPreferredSize(new Dimension(175, 32));

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

        JPanel pnl_Norte = new JPanel();
        pnl_Norte.setLayout(new BoxLayout(pnl_Norte, BoxLayout.Y_AXIS));
        pnl_Norte.setOpaque(false);
        pnl_Linha1.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_Norte.add(pnl_Linha1);
        pnl_Norte.add(Box.createVerticalStrut(10));
        pnl_Filtros.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_Norte.add(pnl_Filtros);
        pnl_Norte.add(Box.createVerticalStrut(10));

        corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setOpaque(false);

        scroll = new JScrollPane(corpo);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        ScrollBar.aplicar(scroll);

        add(pnl_Norte, BorderLayout.NORTH);
        add(scroll,    BorderLayout.CENTER);

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
        veicMap.clear();
        for (Veiculo v : controller.listarVeiculos()) veicMap.put(v.getIdVeiculo(), v);
        todos = controller.listarTodosServicos();
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
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#E0E0E0")),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel("Ordens de Serviço  (" + itens.size() + ")");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.decode("#FF9900"));
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#F0F0F0")));
        lbl.setPreferredSize(new Dimension(0, 32));
        card.add(lbl, BorderLayout.NORTH);

        JPanel gruposPanel = new JPanel();
        gruposPanel.setLayout(new BoxLayout(gruposPanel, BoxLayout.Y_AXIS));
        gruposPanel.setOpaque(false);

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
        Tabela tabelaComp = new Tabela(cols, Tabela.TipoVisual.SIMPLES, 24);
        JTable tabela = tabelaComp.tabela;

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
            tabelaComp.adicionarLinha(new Object[]{dto.idServico(), dto.titulo(), statusLabel, manut, veiculo, data});
        }

        tabela.getColumnModel().getColumn(0).setMinWidth(45);
        tabela.getColumnModel().getColumn(0).setMaxWidth(55);
        tabela.getColumnModel().getColumn(2).setMinWidth(110);
        tabela.getColumnModel().getColumn(2).setMaxWidth(130);
        tabela.getColumnModel().getColumn(3).setMinWidth(95);
        tabela.getColumnModel().getColumn(3).setMaxWidth(110);
        tabela.getColumnModel().getColumn(5).setMinWidth(90);
        tabela.getColumnModel().getColumn(5).setMaxWidth(100);

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
                        default             -> { c.setForeground(Color.decode("#2B2E33")); c.setBackground(Color.WHITE); }
                    }
                } else {
                    c.setForeground(Color.WHITE);
                    c.setBackground(t.getSelectionBackground());
                }
                return c;
            }
        });

        tabelaComp.aoClicar(e -> {
            if (e.getClickCount() != 2) return;
            int row = tabela.getSelectedRow();
            if (row < 0) return;
            try {
                long id = Long.parseLong(String.valueOf(tabela.getValueAt(row, 0)).trim());
                navegar(new V_OrdemServico(controller, id));
            } catch (NumberFormatException ignored) {}
        });

        int altTabela = tabela.getRowHeight() * itens.size() + 26;
        tabelaComp.setAlignmentX(Component.LEFT_ALIGNMENT);
        tabelaComp.setMaximumSize(new Dimension(Integer.MAX_VALUE, altTabela));
        tabelaComp.setPreferredSize(new Dimension(0, altTabela));

        JLabel dica = new JLabel("Duplo clique para abrir a O.S.");
        dica.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        dica.setForeground(COR_LABEL);
        dica.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

        JPanel wrapper = new JPanel(new BorderLayout(0, 2));
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, altTabela + 20));
        wrapper.setPreferredSize(new Dimension(0, altTabela + 20));
        wrapper.add(tabelaComp, BorderLayout.CENTER);
        wrapper.add(dica, BorderLayout.SOUTH);
        return wrapper;
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
}