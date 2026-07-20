package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
import br.com.oficina.atendimento.dto.ServicoResponseDTO;
import br.com.oficina.usuario.FuncionarioEntity;
import controller.OficinaController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class V_OrdemServico extends JPanel {

    private final OficinaController controller;
    private final long idOS;

    private JLabel lbl_TituloServico;
    private JLabel lbl_StatusTexto;
    private JLabel lbl_TipoServico;
    private JPanel pnl_Corpo;

    // Campos interativos (modo edição)
    private JTextArea txa_Descricao;
    private DefaultTableModel mdl_Checklist;
    private JTable tbl_Checklist;

    private ServicoResponseDTO servico;

    private static final Color COR_LARANJA = Color.decode("#FF9900");
    private static final Color COR_CINZA   = Color.decode("#4D4D4D");
    private static final Color COR_AZUL    = Color.decode("#2980B9");
    private static final Color COR_VERDE   = Color.decode("#27AE60");

    public V_OrdemServico(OficinaController controller, long idOS) {
        this.controller = controller;
        this.idOS = idOS;
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        initComponents();
        layoutComponents();
        carregarServico();
    }

    private void initComponents() {
        lbl_TituloServico = new JLabel("ORDEM DE SERVIÇO");
        lbl_TituloServico.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl_TituloServico.setForeground(Color.BLACK);

        lbl_StatusTexto = new JLabel("STATUS: CARREGANDO...");
        lbl_StatusTexto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl_StatusTexto.setForeground(COR_LARANJA);

        lbl_TipoServico = new JLabel();
        lbl_TipoServico.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl_TipoServico.setForeground(COR_CINZA);

        pnl_Corpo = new JPanel();
        pnl_Corpo.setLayout(new BoxLayout(pnl_Corpo, BoxLayout.Y_AXIS));
        pnl_Corpo.setBackground(Color.WHITE);
    }

    private void layoutComponents() {
        JButton btn_Voltar = new JButton("← Voltar para Lista");
        btn_Voltar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn_Voltar.setForeground(COR_CINZA);
        btn_Voltar.setContentAreaFilled(false);
        btn_Voltar.setBorderPainted(false);
        btn_Voltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Voltar.addActionListener(e -> navegar(new V_VisualizarServicos(controller)));

        JLabel lbl_Mapa = new JLabel("Página Inicial > Consultar Serviços > Detalhes");
        lbl_Mapa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Mapa.setForeground(COR_CINZA);

        JPanel pnl_Topo = new JPanel(new BorderLayout());
        pnl_Topo.setBackground(Color.WHITE);
        pnl_Topo.add(lbl_Mapa, BorderLayout.WEST);
        pnl_Topo.add(btn_Voltar, BorderLayout.EAST);
        add(pnl_Topo, BorderLayout.NORTH);

        JPanel pnl_Header = new JPanel();
        pnl_Header.setLayout(new BoxLayout(pnl_Header, BoxLayout.Y_AXIS));
        pnl_Header.setBackground(Color.WHITE);
        pnl_Header.add(lbl_TituloServico);
        pnl_Header.add(Box.createVerticalStrut(4));
        pnl_Header.add(lbl_TipoServico);
        pnl_Header.add(Box.createVerticalStrut(2));
        pnl_Header.add(lbl_StatusTexto);
        pnl_Header.add(Box.createVerticalStrut(16));

        JScrollPane scp = new JScrollPane(pnl_Corpo);
        scp.setBorder(BorderFactory.createEmptyBorder());
        scp.getVerticalScrollBar().setUnitIncrement(16);

        JPanel pnl_Central = new JPanel(new BorderLayout());
        pnl_Central.setBackground(Color.WHITE);
        pnl_Central.add(pnl_Header, BorderLayout.NORTH);
        pnl_Central.add(scp, BorderLayout.CENTER);
        add(pnl_Central, BorderLayout.CENTER);
    }

    // -----------------------------------------------------------------------
    private void carregarServico() {
        if (controller == null) return;
        try { servico = controller.buscarServico(idOS); } catch (Exception e) { servico = null; }
        if (servico == null) { lbl_TituloServico.setText("O.S. não encontrada"); return; }

        String cod = servico.codigo() != null && !servico.codigo().isEmpty()
            ? servico.codigo() : String.format("%05d", servico.idServico());
        lbl_TituloServico.setText("ORDEM DE SERVIÇO  " + cod);
        lbl_TipoServico.setText("Tipo: " + servico.tipoServico());
        lbl_StatusTexto.setText("STATUS: " + (servico.status() != null ? servico.status() : "—"));

        pnl_Corpo.removeAll();

        boolean aberta = "ABERTA".equalsIgnoreCase(servico.status());
        boolean emAndamento = "EM_ANDAMENTO".equalsIgnoreCase(servico.status());
        boolean concluida = "CONCLUIDA".equalsIgnoreCase(servico.status());

        // Botão editar orçamento — visível quando há orçamento vinculado
        if (servico.idOrcamento() != null && servico.idOrcamento() > 0) {
            JPanel pnl_EditarOrc = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            pnl_EditarOrc.setOpaque(false);
            pnl_EditarOrc.setAlignmentX(Component.LEFT_ALIGNMENT);
            JButton btn_EditarOrc = new JButton("✎  Editar Orçamento");
            btn_EditarOrc.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn_EditarOrc.setBackground(COR_AZUL);
            btn_EditarOrc.setForeground(Color.WHITE);
            btn_EditarOrc.setFocusPainted(false);
            btn_EditarOrc.setBorderPainted(false);
            btn_EditarOrc.setCursor(new Cursor(Cursor.HAND_CURSOR));
            final long idOrcVinc = servico.idOrcamento();
            btn_EditarOrc.addActionListener(e -> navegar(new V_EditarOrcamento(controller, idOrcVinc, idOS)));
            pnl_EditarOrc.add(btn_EditarOrc);
            pnl_Corpo.add(pnl_EditarOrc);
            pnl_Corpo.add(Box.createVerticalStrut(10));

            JPanel pnl_ItensOrc = construirPainelItensOrcamento(idOrcVinc);
            if (pnl_ItensOrc != null) {
                pnl_Corpo.add(pnl_ItensOrc);
                pnl_Corpo.add(Box.createVerticalStrut(10));
            }
            JPanel pnl_Pecas = construirPainelPecas(idOrcVinc);
            if (pnl_Pecas != null) {
                pnl_Corpo.add(pnl_Pecas);
                pnl_Corpo.add(Box.createVerticalStrut(14));
            }
        }

        JLabel lblEtapa = new JLabel("ETAPA — REGISTRO DO TRABALHO");
        lblEtapa.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblEtapa.setForeground(COR_AZUL);
        lblEtapa.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        lblEtapa.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_Corpo.add(lblEtapa);

        if (aberta) {
            pnl_Corpo.add(construirPainelEdicao());
        } else {
            pnl_Corpo.add(construirPainelLeitura());

            if (emAndamento) {
                pnl_Corpo.add(Box.createVerticalStrut(14));
                pnl_Corpo.add(construirPainelFinalizacao());
            } else if (concluida) {
                JPanel pnl_ObsSaida = construirCardObservacaoSaida();
                if (pnl_ObsSaida != null) {
                    pnl_Corpo.add(Box.createVerticalStrut(14));
                    pnl_Corpo.add(pnl_ObsSaida);
                }
            }
        }

        pnl_Corpo.add(Box.createVerticalGlue());
        pnl_Corpo.revalidate();
        pnl_Corpo.repaint();
    }

    // -----------------------------------------------------------------------
    // Painel editável — OS ABERTA
    // -----------------------------------------------------------------------
    private JPanel construirPainelEdicao() {
        JPanel pnl = new JPanel(new BorderLayout(0, 12));
        pnl.setOpaque(false);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E5E5E5"), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // -- Descrição geral --
        JLabel lblDesc = new JLabel("Descrição geral do trabalho:");
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 13));

        txa_Descricao = new JTextArea(4, 40);
        txa_Descricao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txa_Descricao.setLineWrap(true);
        txa_Descricao.setWrapStyleWord(true);
        txa_Descricao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#CCCCCC")),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        JPanel pnl_Desc = new JPanel(new BorderLayout(0, 4));
        pnl_Desc.setOpaque(false);
        pnl_Desc.add(lblDesc, BorderLayout.NORTH);
        pnl_Desc.add(new JScrollPane(txa_Descricao), BorderLayout.CENTER);

        // -- Checklist de itens realizados --
        JLabel lblCheck = new JLabel("Itens realizados:");
        lblCheck.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCheck.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));

        String[] cols = {"Feito", "Item realizado", "Tempo gasto"};
        mdl_Checklist = new DefaultTableModel(cols, 0) {
            @Override public Class<?> getColumnClass(int c) {
                return c == 0 ? Boolean.class : String.class;
            }
            @Override public boolean isCellEditable(int r, int c) { return c == 0 || c == 2; }
        };
        // Pré-popular com itens do orçamento vinculado, se houver
        boolean populado = false;
        if (servico != null && servico.idOrcamento() != null && servico.idOrcamento() > 0) {
            try {
                List<CatalogoServicoEntity> itensOrc =
                    controller.listarItensDoOrcamento(servico.idOrcamento());
                for (CatalogoServicoEntity item : itensOrc) {
                    mdl_Checklist.addRow(new Object[]{Boolean.FALSE, item.getNome(), ""});
                    populado = true;
                }
            } catch (Exception ignored) {}
        }
        if (!populado) mdl_Checklist.addRow(new Object[]{Boolean.FALSE, "(sem itens no orçamento)", ""});

        tbl_Checklist = new JTable(mdl_Checklist);
        tbl_Checklist.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl_Checklist.setRowHeight(26);
        tbl_Checklist.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tbl_Checklist.getTableHeader().setReorderingAllowed(false);
        tbl_Checklist.getColumnModel().getColumn(0).setMaxWidth(55);
        tbl_Checklist.getColumnModel().getColumn(0).setMinWidth(45);
        tbl_Checklist.getColumnModel().getColumn(2).setPreferredWidth(110);
        tbl_Checklist.getColumnModel().getColumn(2).setMaxWidth(160);

        int alturaCheck = Math.min(Math.max((mdl_Checklist.getRowCount() * 26) + 30, 90), 220);
        JScrollPane scpCheck = new JScrollPane(tbl_Checklist);
        scpCheck.setBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC")));
        scpCheck.setPreferredSize(new Dimension(600, alturaCheck));

        JPanel pnl_Check = new JPanel(new BorderLayout(0, 4));
        pnl_Check.setOpaque(false);
        pnl_Check.add(lblCheck, BorderLayout.NORTH);
        pnl_Check.add(scpCheck, BorderLayout.CENTER);

        JPanel pnl_Centro = new JPanel(new BorderLayout(0, 8));
        pnl_Centro.setOpaque(false);
        pnl_Centro.add(pnl_Desc, BorderLayout.NORTH);
        pnl_Centro.add(pnl_Check, BorderLayout.CENTER);

        // -- Botões de ação --
        JPanel pnl_Botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pnl_Botoes.setOpaque(false);

        boolean isRevisao = servico != null && "REVISAO".equalsIgnoreCase(servico.tipoServico());
        if (isRevisao) {
            JButton btn_OrcInterno = new JButton("Gerar Orçamento Interno");
            btn_OrcInterno.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn_OrcInterno.setBackground(Color.decode("#17A2B8"));
            btn_OrcInterno.setForeground(Color.WHITE);
            btn_OrcInterno.setFocusPainted(false);
            btn_OrcInterno.setBorderPainted(false);
            btn_OrcInterno.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn_OrcInterno.addActionListener(e -> gerarOrcamentoInterno());
            pnl_Botoes.add(btn_OrcInterno);
        }

        JButton btn_Confirmar = new JButton("✔ Confirmar Etapa");
        btn_Confirmar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn_Confirmar.setBackground(COR_LARANJA);
        btn_Confirmar.setForeground(Color.WHITE);
        btn_Confirmar.setFocusPainted(false);
        btn_Confirmar.setBorderPainted(false);
        btn_Confirmar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Confirmar.addActionListener(e -> confirmarEtapa());
        pnl_Botoes.add(btn_Confirmar);

        pnl.add(pnl_Centro, BorderLayout.CENTER);
        pnl.add(pnl_Botoes, BorderLayout.SOUTH);
        return pnl;
    }

    // -----------------------------------------------------------------------
    // Painel leitura — OS em andamento / concluída
    // -----------------------------------------------------------------------
    private JPanel construirPainelLeitura() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setOpaque(false);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Descrição geral (etapa=0)
        ServicoResponseDTO.ItemView descItem = servico.itens().stream()
            .filter(it -> it.etapa() == 0).findFirst().orElse(null);
        String descricao = descItem != null ? descItem.descricao() : "(sem descrição)";
        pnl.add(criarCardLeitura("Descrição do trabalho", descricao, "#2980B9"), BorderLayout.NORTH);

        // Checklist (etapa=1)
        List<ServicoResponseDTO.ItemView> checkItems = servico.itens().stream()
            .filter(it -> it.etapa() == 1).toList();

        String[] cols = {"Feito", "Item realizado", "Tempo gasto"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public Class<?> getColumnClass(int c) { return c == 0 ? Boolean.class : String.class; }
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        if (checkItems.isEmpty()) {
            mdl.addRow(new Object[]{Boolean.FALSE, "(nenhum item registrado)", ""});
        } else {
            for (ServicoResponseDTO.ItemView it : checkItems)
                mdl.addRow(new Object[]{"CONCLUIDO".equals(it.status()), it.descricao(), it.tempoGasto()});
        }

        JTable tbl = new JTable(mdl);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl.setRowHeight(26);
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.getColumnModel().getColumn(0).setMaxWidth(55);
        tbl.getColumnModel().getColumn(0).setMinWidth(45);
        tbl.getColumnModel().getColumn(2).setPreferredWidth(110);
        tbl.getColumnModel().getColumn(2).setMaxWidth(160);

        int alturaTabela = Math.min((mdl.getRowCount() * 26) + 30, 220);
        JScrollPane scp = new JScrollPane(tbl);
        scp.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.decode("#E0E0E0")), "Itens realizados"));
        scp.setPreferredSize(new Dimension(600, alturaTabela));

        pnl.add(scp, BorderLayout.CENTER);
        return pnl;
    }

    // -----------------------------------------------------------------------
    // Painel de finalização — OS em andamento
    // -----------------------------------------------------------------------
    private JPanel construirPainelFinalizacao() {
        JLabel lblEtapa = new JLabel("ETAPA — FINALIZAÇÃO DO SERVIÇO");
        lblEtapa.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblEtapa.setForeground(COR_VERDE);
        lblEtapa.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        lblEtapa.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnl = new JPanel(new BorderLayout(0, 12));
        pnl.setOpaque(false);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E5E5E5"), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblMecanico = new JLabel("Mecânico responsável:");
        lblMecanico.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JComboBox<FuncionarioEntity> cmb_Mecanico = new JComboBox<>();
        cmb_Mecanico.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean focus) {
                super.getListCellRendererComponent(l, v, i, sel, focus);
                if (v instanceof FuncionarioEntity f) setText(f.getNome() + " — " + f.getCargo());
                return this;
            }
        });
        for (FuncionarioEntity f : controller.listarFuncionarios()) cmb_Mecanico.addItem(f);

        JPanel pnl_Mecanico = new JPanel(new BorderLayout(0, 4));
        pnl_Mecanico.setOpaque(false);
        pnl_Mecanico.add(lblMecanico, BorderLayout.NORTH);
        pnl_Mecanico.add(cmb_Mecanico, BorderLayout.CENTER);

        JLabel lblObs = new JLabel("Observação do mecânico — estado do veículo na saída:");
        lblObs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblObs.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));

        JTextArea txa_ObsSaida = new JTextArea(4, 40);
        txa_ObsSaida.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txa_ObsSaida.setLineWrap(true);
        txa_ObsSaida.setWrapStyleWord(true);
        txa_ObsSaida.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#CCCCCC")),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        JPanel pnl_Obs = new JPanel(new BorderLayout(0, 4));
        pnl_Obs.setOpaque(false);
        pnl_Obs.add(lblObs, BorderLayout.NORTH);
        pnl_Obs.add(new JScrollPane(txa_ObsSaida), BorderLayout.CENTER);

        JPanel pnl_Centro = new JPanel(new BorderLayout(0, 8));
        pnl_Centro.setOpaque(false);
        pnl_Centro.add(pnl_Mecanico, BorderLayout.NORTH);
        pnl_Centro.add(pnl_Obs, BorderLayout.CENTER);

        JPanel pnl_Botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pnl_Botoes.setOpaque(false);

        JButton btn_Finalizar = new JButton("✔ Finalizar Serviço");
        btn_Finalizar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn_Finalizar.setBackground(COR_VERDE);
        btn_Finalizar.setForeground(Color.WHITE);
        btn_Finalizar.setFocusPainted(false);
        btn_Finalizar.setBorderPainted(false);
        btn_Finalizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn_Finalizar.addActionListener(e -> finalizarServico(cmb_Mecanico, txa_ObsSaida));
        pnl_Botoes.add(btn_Finalizar);

        pnl.add(pnl_Centro, BorderLayout.CENTER);
        pnl.add(pnl_Botoes, BorderLayout.SOUTH);

        JPanel pnl_Wrapper = new JPanel(new BorderLayout());
        pnl_Wrapper.setOpaque(false);
        pnl_Wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_Wrapper.add(lblEtapa, BorderLayout.NORTH);
        pnl_Wrapper.add(pnl, BorderLayout.CENTER);
        return pnl_Wrapper;
    }

    private void finalizarServico(JComboBox<FuncionarioEntity> cmb, JTextArea txa) {
        FuncionarioEntity mecanico = (FuncionarioEntity) cmb.getSelectedItem();
        if (mecanico == null) {
            JOptionPane.showMessageDialog(this, "Selecione o mecânico responsável.",
                "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String observacao = txa.getText().trim();
        if (observacao.length() < 3) {
            JOptionPane.showMessageDialog(this,
                "Descreva o estado do veículo na saída (mínimo 3 caracteres).",
                "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirma a finalização desta O.S.? O status ficará CONCLUÍDA e não poderá ser reaberto.",
            "Confirmar Finalização", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            controller.finalizarOS(idOS, observacao, mecanico.getIdFuncionario());
            JOptionPane.showMessageDialog(this,
                "O.S. finalizada com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregarServico();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -----------------------------------------------------------------------
    // Card leitura — observação de saída (OS concluída)
    // -----------------------------------------------------------------------
    private JPanel construirCardObservacaoSaida() {
        ServicoResponseDTO.ItemView obsItem = servico.itens().stream()
            .filter(it -> it.etapa() == 2).findFirst().orElse(null);
        if (obsItem == null) return null;

        String titulo = "Estado do veículo na saída";
        if (obsItem.idFuncionario() != null) {
            for (FuncionarioEntity f : controller.listarFuncionarios()) {
                if (f.getIdFuncionario() != null && f.getIdFuncionario().equals(obsItem.idFuncionario())) {
                    titulo += " — registrado por " + f.getNome();
                    break;
                }
            }
        }
        return criarCardLeitura(titulo, obsItem.descricao(), "#27AE60");
    }

    // -----------------------------------------------------------------------
    private void confirmarEtapa() {
        if (tbl_Checklist != null && tbl_Checklist.isEditing())
            tbl_Checklist.getCellEditor().stopCellEditing();

        String descricao = txa_Descricao != null ? txa_Descricao.getText().trim() : "";
        if (descricao.length() < 3) {
            JOptionPane.showMessageDialog(this,
                "Informe a descrição geral do trabalho (mínimo 3 caracteres).",
                "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String[]> checklist = new ArrayList<>();
        if (mdl_Checklist != null) {
            for (int i = 0; i < mdl_Checklist.getRowCount(); i++) {
                Boolean feito  = (Boolean) mdl_Checklist.getValueAt(i, 0);
                String  item   = String.valueOf(mdl_Checklist.getValueAt(i, 1)).trim();
                String  tempo  = String.valueOf(mdl_Checklist.getValueAt(i, 2)).trim();
                if (!item.isEmpty())
                    checklist.add(new String[]{item, tempo, String.valueOf(feito != null && feito)});
            }
        }

        try {
            controller.registrarEtapaOS(idOS, descricao, checklist);
            JOptionPane.showMessageDialog(this,
                "Etapa registrada. O.S. movida para EM ANDAMENTO.",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregarServico();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gerarOrcamentoInterno() {
        if (servico == null) return;
        JTextField txtValor = new JTextField();
        JComboBox<FuncionarioEntity> cmbResponsavel = new JComboBox<>();
        cmbResponsavel.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean focus) {
                super.getListCellRendererComponent(l, v, i, sel, focus);
                if (v instanceof FuncionarioEntity f) setText(f.getNome() + " — " + f.getCargo());
                return this;
            }
        });
        for (FuncionarioEntity f : controller.listarFuncionarios()) cmbResponsavel.addItem(f);
        JTextField txtObs = new JTextField();
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 4));
        form.add(new JLabel("Valor estimado (R$):")); form.add(txtValor);
        form.add(new JLabel("Mecânico responsável:")); form.add(cmbResponsavel);
        form.add(new JLabel("Observação:"));           form.add(txtObs);

        int res = JOptionPane.showConfirmDialog(this, form,
            "Gerar Orçamento Interno — OS " + servico.codigo(),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        double valor;
        try {
            valor = Double.parseDouble(txtValor.getText().trim().replace(",", "."));
            if (valor < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Informe um valor válido.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        FuncionarioEntity resp = (FuncionarioEntity) cmbResponsavel.getSelectedItem();
        if (resp == null) {
            JOptionPane.showMessageDialog(this, "Selecione o mecânico responsável.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            long idVeic = servico.idVeiculo() != null ? servico.idVeiculo() : 0L;
            long idCliente = controller.getIdClientePorVeiculo(idVeic);
            controller.criarOrcamentoRevisao(idOS, valor, resp.getNome(),
                txtObs.getText().trim(), idVeic, idCliente, resp.getIdFuncionario());
            JOptionPane.showMessageDialog(this, "Orçamento interno criado com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -----------------------------------------------------------------------
    // Itens de serviço do orçamento com valores
    // -----------------------------------------------------------------------
    private JPanel construirPainelItensOrcamento(long idOrcamento) {
        java.util.Map<br.com.oficina.atendimento.CatalogoServicoEntity, Double> itens;
        try { itens = controller.listarItensOrcamentoComValor(idOrcamento); }
        catch (Exception e) { return null; }
        if (itens.isEmpty()) return null;

        JPanel pnl = new JPanel(new BorderLayout(0, 6));
        pnl.setOpaque(false);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel("SERVIÇOS DO ORÇAMENTO");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(COR_AZUL);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        String[] cols = {"Serviço", "Tipo"};
        javax.swing.table.DefaultTableModel mdl = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (var entry : itens.entrySet()) {
            br.com.oficina.atendimento.CatalogoServicoEntity item = entry.getKey();
            String tipo = "REVISAO".equals(item.getTipo()) ? "Revisão" : "Padrão";
            mdl.addRow(new Object[]{item.getNome(), tipo});
        }

        JTable tbl = new JTable(mdl);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl.setRowHeight(26);
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.getColumnModel().getColumn(1).setMaxWidth(80);
        tbl.getColumnModel().getColumn(1).setMinWidth(60);

        int altura = Math.min((mdl.getRowCount() * 26) + 30, 200);
        JScrollPane scp = new JScrollPane(tbl);
        scp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E5E5E5")),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        scp.setPreferredSize(new Dimension(600, altura));

        pnl.add(lbl, BorderLayout.NORTH);
        pnl.add(scp, BorderLayout.CENTER);
        return pnl;
    }

    // -----------------------------------------------------------------------
    // Peças vinculadas ao orçamento
    // -----------------------------------------------------------------------
    private JPanel construirPainelPecas(long idOrcamento) {
        List<Object[]> pecas;
        try { pecas = controller.listarPecasOrcamentoComValor(idOrcamento); }
        catch (Exception e) { return null; }
        if (pecas.isEmpty()) return null;

        JPanel pnl = new JPanel(new BorderLayout(0, 6));
        pnl.setOpaque(false);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel("PEÇAS UTILIZADAS");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(COR_VERDE);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        String[] cols = {"Peça", "Nome Técnico", "Fabricante"};
        javax.swing.table.DefaultTableModel mdl = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Object[] triple : pecas) {
            br.com.oficina.estoque.PecaEntity p = (br.com.oficina.estoque.PecaEntity) triple[0];
            String nomeTecnico = triple.length > 2 ? (String) triple[2] : "";
            String fabricante = triple.length > 3 ? (String) triple[3] : "";
            mdl.addRow(new Object[]{
                p.getNomeExibicao(),
                nomeTecnico.isBlank() ? "—" : nomeTecnico,
                fabricante.isBlank() ? "—" : fabricante
            });
        }

        JTable tbl = new JTable(mdl);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl.setRowHeight(26);
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tbl.getTableHeader().setReorderingAllowed(false);

        int altura = Math.min((mdl.getRowCount() * 26) + 30, 200);
        JScrollPane scp = new JScrollPane(tbl);
        scp.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E5E5E5")),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        scp.setPreferredSize(new Dimension(600, altura));

        pnl.add(lbl, BorderLayout.NORTH);
        pnl.add(scp, BorderLayout.CENTER);
        return pnl;
    }

    // -----------------------------------------------------------------------
    private JPanel criarCardLeitura(String titulo, String conteudo, String corHex) {
        JPanel pnl = new JPanel(new BorderLayout(0, 6));
        pnl.setBackground(Color.decode("#F9F9F9"));
        pnl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E5E5E5"), 1),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(Color.decode(corHex));
        pnl.add(lbl, BorderLayout.NORTH);
        JTextArea txa = new JTextArea(conteudo);
        txa.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txa.setForeground(Color.decode("#2C3E50"));
        txa.setLineWrap(true); txa.setWrapStyleWord(true);
        txa.setEditable(false);
        txa.setBackground(Color.decode("#F9F9F9"));
        pnl.add(txa, BorderLayout.CENTER);
        return pnl;
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }
}
