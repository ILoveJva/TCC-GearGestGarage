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
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class V_VisualizarServicos extends JPanel {

    private final OficinaController controller;

    private List<ServicoResponseDTO> todos = new ArrayList<>();
    private List<Orcamento> orcamentosPendentes = new ArrayList<>();
    private Map<Long, Veiculo> veicMap = new HashMap<>();

    private JTextField txt_Busca;
    private JComboBox<String> cmb_Tipo;
    private JComboBox<String> cmb_Status;

    private JPanel corpo;
    private JScrollPane scroll;

    private DefaultTableModel mdl_Pendentes;
    private JTable tbl_Pendentes;
    private JPanel pnl_Pendentes;
    private JPanel pnl_PendentesCorpo;
    private JButton btn_TogglePendentes;

    private static final String[] STATUS_LABELS = {"Todos os status", "Aberta", "Em Andamento", "Concluída"};
    private static final String[] STATUS_CODES  = {null, "ABERTA", "EM_ANDAMENTO", "CONCLUIDA"};

    public V_VisualizarServicos(OficinaController controller) {
        this.controller = controller;
        setBackground(Color.decode("#F5F5F5"));
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        construir();
        carregar();
    }

    private void construir() {
        // ---- Breadcrumb + botão criar OS ----
        JLabel lbl_Titulo = new JLabel("Página Inicial > Consultar Serviços");
        lbl_Titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Titulo.setForeground(Color.decode("#4D4D4D"));

        JButton btn_CriarOS = new JButton("+ Criar OS");
        btn_CriarOS.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_CriarOS.setForeground(Color.WHITE);
        btn_CriarOS.setBackground(Color.decode("#FF9900"));
        btn_CriarOS.setFocusPainted(false);
        btn_CriarOS.setBorderPainted(false);
        btn_CriarOS.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_CriarOS.setPreferredSize(new Dimension(140, 36));
        btn_CriarOS.addActionListener(e -> navegar(new V_CadastrarServico(controller)));

        JPanel pnl_Linha1 = new JPanel(new BorderLayout());
        pnl_Linha1.setOpaque(false);
        pnl_Linha1.add(lbl_Titulo, BorderLayout.WEST);
        pnl_Linha1.add(btn_CriarOS, BorderLayout.EAST);

        // ---- Filtros ----
        txt_Busca = new JTextField();
        txt_Busca.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_Busca.setPreferredSize(new Dimension(240, 32));
        txt_Busca.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(6, Color.decode("#CCCCCC")),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        txt_Busca.putClientProperty("JTextField.placeholderText", "Buscar por título ou veículo...");

        // Combo tipo: "Todos os tipos" + cada TipoServicoOS
        String[] tiposLabels = new String[OrdemDeServico.TipoServicoOS.values().length + 1];
        tiposLabels[0] = "Todos os tipos";
        for (int i = 0; i < OrdemDeServico.TipoServicoOS.values().length; i++)
            tiposLabels[i + 1] = OrdemDeServico.TipoServicoOS.values()[i].getLabel();
        cmb_Tipo = new JComboBox<>(tiposLabels);
        cmb_Tipo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_Tipo.setPreferredSize(new Dimension(165, 32));
        cmb_Tipo.setBackground(Color.WHITE);

        cmb_Status = new JComboBox<>(STATUS_LABELS);
        cmb_Status.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb_Status.setPreferredSize(new Dimension(175, 32));
        cmb_Status.setBackground(Color.WHITE);

        JLabel lbl_B = new JLabel("Buscar:");
        JLabel lbl_T = new JLabel("Tipo:");
        JLabel lbl_S = new JLabel("Status:");
        for (JLabel l : new JLabel[]{lbl_B, lbl_T, lbl_S})
            l.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JPanel pnl_Filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnl_Filtros.setOpaque(false);
        pnl_Filtros.add(lbl_B); pnl_Filtros.add(txt_Busca);
        pnl_Filtros.add(Box.createHorizontalStrut(6));
        pnl_Filtros.add(lbl_T); pnl_Filtros.add(cmb_Tipo);
        pnl_Filtros.add(Box.createHorizontalStrut(6));
        pnl_Filtros.add(lbl_S); pnl_Filtros.add(cmb_Status);

        // ---- Seção orçamentos pendentes (colapsável) ----
        pnl_Pendentes = new JPanel(new BorderLayout(0, 6));
        pnl_Pendentes.setOpaque(false);
        pnl_Pendentes.setVisible(false);
        pnl_Pendentes.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#FFD580")),
            BorderFactory.createEmptyBorder(8, 0, 10, 0)));

        JPanel pnl_PendentesHeader = new JPanel(new BorderLayout());
        pnl_PendentesHeader.setOpaque(false);
        JLabel lbl_Pend = new JLabel("⚠  Orçamentos aprovados aguardando OS");
        lbl_Pend.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl_Pend.setForeground(Color.decode("#B8860B"));
        btn_TogglePendentes = new JButton("Mostrar ▾");
        btn_TogglePendentes.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn_TogglePendentes.setForeground(Color.decode("#B8860B"));
        btn_TogglePendentes.setBackground(Color.decode("#FFF8E1"));
        btn_TogglePendentes.setFocusPainted(false);
        btn_TogglePendentes.setBorder(BorderFactory.createLineBorder(Color.decode("#FFD580")));
        btn_TogglePendentes.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        tbl_Pendentes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tbl_Pendentes.setRowHeight(24);
        tbl_Pendentes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tbl_Pendentes.getTableHeader().setReorderingAllowed(false);
        tbl_Pendentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scp_Pend = new JScrollPane(tbl_Pendentes);
        scp_Pend.setBorder(BorderFactory.createLineBorder(Color.decode("#FFD580")));
        scp_Pend.setPreferredSize(new Dimension(0, 100));

        JButton btn_GerarOS = new JButton("Gerar OS do selecionado →");
        btn_GerarOS.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn_GerarOS.setForeground(Color.WHITE);
        btn_GerarOS.setBackground(Color.decode("#17A2B8"));
        btn_GerarOS.setFocusPainted(false);
        btn_GerarOS.setBorderPainted(false);
        btn_GerarOS.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        scroll.getVerticalScrollBar().setUnitIncrement(14);

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
            vazio.setForeground(Color.decode("#888888"));
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
            new RoundedBorder(10, Color.decode("#E0E0E0")),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));
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
            lblTipo.setForeground(Color.decode("#555555"));
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
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.setRowHeight(26);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabela.getTableHeader().setReorderingAllowed(false);
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

        // Renderer colorido para Status (col 2)
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
                        default             -> { c.setForeground(Color.decode("#333333")); c.setBackground(Color.WHITE); }
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
        int altTabela = tabela.getRowHeight() * itens.size() + 28;
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, altTabela));
        sp.setPreferredSize(new Dimension(0, altTabela));
        sp.setBorder(BorderFactory.createLineBorder(Color.decode("#E8E8E8")));

        JLabel dica = new JLabel("Duplo clique para abrir a O.S.");
        dica.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        dica.setForeground(Color.decode("#BBBBBB"));
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

    private void gerarOSDeOrcamento() {
        int linha = tbl_Pendentes.getSelectedRow();
        if (linha < 0 || linha >= orcamentosPendentes.size()) {
            JOptionPane.showMessageDialog(this, "Selecione um orçamento na tabela.",
                "Atenção", JOptionPane.WARNING_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Título deve ter pelo menos 3 caracteres.",
                "Atenção", JOptionPane.WARNING_MESSAGE); return;
        }
        int km;
        try {
            km = Integer.parseInt(txtKm.getText().trim());
            if (km < 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Informe um KM válido.",
                "Atenção", JOptionPane.WARNING_MESSAGE); return;
        }
        try {
            OrdemDeServico.TipoServicoOS tipoEnum = OrdemDeServico.TipoServicoOS.fromLabel((String) cmbTipo.getSelectedItem());
            OrdemDeServico.TipoManutencao manutEnum = OrdemDeServico.TipoManutencao.fromLabel((String) cmbManut.getSelectedItem());
            controller.abrirOSDeOrcamento(titulo, tipoEnum.name(), manutEnum.name(),
                txtData.getText().trim(), km, o.getIdVeiculo(), o.getIdOrcamento());
            JOptionPane.showMessageDialog(this, "Ordem de Serviço gerada com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar OS: " + ex.getMessage(),
                "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
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
}
