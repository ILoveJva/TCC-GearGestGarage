package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
import br.com.oficina.atendimento.dto.ServicoResponseDTO;
import br.com.oficina.usuario.FuncionarioEntity;
import controller.OficinaController;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class V_OrdemServico extends JPanel {

    private final OficinaController controller;
    private final long idOS;

    private JLabel lbl_TituloServico;
    private SeloStatus lbl_StatusTexto;
    private JLabel lbl_TipoServico;
    private JPanel pnl_Corpo;

    // Campos interativos (modo edição)
    private GlassTextArea txa_Descricao;
    private DefaultTableModel mdl_Checklist;
    private JTable tbl_Checklist;

    private ServicoResponseDTO servico;

    // Cores semânticas por status/etapa (tema original preservado)
    private static final Color COR_LARANJA = Color.decode("#FF9900"); // status ABERTA / ação principal
    private static final Color COR_CINZA   = Color.decode("#4D4D4D");
    private static final Color COR_AZUL    = Color.decode("#2980B9"); // etapa registro / editar orçamento
    private static final Color COR_VERDE   = Color.decode("#27AE60"); // etapa finalização / concluída
    private static final Color COR_INFO    = Color.decode("#17A2B8"); // orçamento interno (revisão)
    private static final Color COR_CONGELADA = Color.decode("#6C757D"); // serviço congelado aguardando orçamento

    // Paleta harmonizada com o mesmo efeito de vidro usado nas demais telas
    private static final Color COR_FUNDO_PAGINA = Color.decode("#F5F7FA");
    private static final Color COR_CARD_BASE    = Color.decode("#EEF2F7");
    private static final Color COR_TITULO       = Color.decode("#4A5568");
    private static final Color COR_LABEL        = Color.decode("#57626F");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");
    private static final Color COR_BORDA_SUAVE  = new Color(160, 175, 195, 130);

    private static final int RAIO_COMPONENTE     = 12;
    private static final int TAMANHO_FONTE_LABEL = 13;
    private static final int TAMANHO_FONTE_CAMPO = 14;
    private static final int ALTURA_CAMPO        = 34;

    public V_OrdemServico(OficinaController controller, long idOS) {
        this.controller = controller;
        this.idOS = idOS;
        setLayout(new BorderLayout(0, 10));
        setBackground(COR_FUNDO_PAGINA);
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        initComponents();
        layoutComponents();
        carregarServico();
    }

    private void initComponents() {
        lbl_TituloServico = new JLabel("ORDEM DE SERVIÇO");
        lbl_TituloServico.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl_TituloServico.setForeground(COR_TITULO);

        lbl_StatusTexto = new SeloStatus("STATUS: CARREGANDO...");
        lbl_StatusTexto.setCorDestaque(COR_LARANJA);

        lbl_TipoServico = new JLabel();
        lbl_TipoServico.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl_TipoServico.setForeground(COR_LABEL);

        pnl_Corpo = new JPanel();
        pnl_Corpo.setLayout(new BoxLayout(pnl_Corpo, BoxLayout.Y_AXIS));
        pnl_Corpo.setOpaque(false);
    }

    private void layoutComponents() {
        JButton btn_Voltar = botaoLink("← Voltar para Lista", COR_LABEL);
        btn_Voltar.addActionListener(e -> navegar(new V_VisualizarServicos(controller)));

        JLabel lbl_Mapa = new JLabel("Página Inicial > Consultar Serviços > Detalhes");
        lbl_Mapa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Mapa.setForeground(COR_LABEL);

        JPanel pnl_Topo = new JPanel(new BorderLayout());
        pnl_Topo.setOpaque(false);
        pnl_Topo.add(lbl_Mapa, BorderLayout.WEST);
        pnl_Topo.add(btn_Voltar, BorderLayout.EAST);
        add(pnl_Topo, BorderLayout.NORTH);

        JPanel pnl_Header = new JPanel();
        pnl_Header.setLayout(new BoxLayout(pnl_Header, BoxLayout.Y_AXIS));
        pnl_Header.setOpaque(false);
        lbl_TituloServico.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl_TipoServico.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl_StatusTexto.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl_Header.add(lbl_TituloServico);
        pnl_Header.add(Box.createVerticalStrut(4));
        pnl_Header.add(lbl_TipoServico);
        pnl_Header.add(Box.createVerticalStrut(6));
        pnl_Header.add(lbl_StatusTexto);
        pnl_Header.add(Box.createVerticalStrut(16));

        JScrollPane scp = new JScrollPane(pnl_Corpo);
        scp.setBorder(BorderFactory.createEmptyBorder());
        scp.setOpaque(false);
        scp.getViewport().setOpaque(false);
        ScrollBarPadrao.aplicar(scp);

        JPanel pnl_Central = new JPanel(new BorderLayout());
        pnl_Central.setOpaque(false);
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
        boolean congelada = "CONGELADA".equalsIgnoreCase(servico.status());

        if (aberta) lbl_StatusTexto.setCorDestaque(COR_LARANJA);
        else if (emAndamento) lbl_StatusTexto.setCorDestaque(COR_AZUL);
        else if (concluida) lbl_StatusTexto.setCorDestaque(COR_VERDE);
        else if (congelada) lbl_StatusTexto.setCorDestaque(COR_CONGELADA);
        else lbl_StatusTexto.setCorDestaque(COR_LABEL);

        // Itens/peças do orçamento vinculado — apenas leitura (edição acontece na tela de Visualizar Orçamento)
        if (servico.idOrcamento() != null && servico.idOrcamento() > 0) {
            final long idOrcVinc = servico.idOrcamento();

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

        if (congelada) {
            pnl_Corpo.add(construirAvisoCongelado());
        } else {
            JLabel lblEtapa = criarLabelEtapa("ETAPA — REGISTRO DO TRABALHO", COR_AZUL);
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
        }

        pnl_Corpo.add(Box.createVerticalGlue());
        pnl_Corpo.revalidate();
        pnl_Corpo.repaint();
    }

    // -----------------------------------------------------------------------
    // Painel editável — OS ABERTA
    // -----------------------------------------------------------------------
    private JPanel construirPainelEdicao() {
        JPanel pnl = new PainelCartao(new BorderLayout(0, 12));
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        // -- Descrição geral --
        JLabel lblDesc = criarLabelCampo("Descrição geral do trabalho:");

        txa_Descricao = new GlassTextArea(4, 40);

        JPanel pnl_Desc = new JPanel(new BorderLayout(0, 4));
        pnl_Desc.setOpaque(false);
        pnl_Desc.add(lblDesc, BorderLayout.NORTH);
        pnl_Desc.add(envolverEmScroll(txa_Descricao), BorderLayout.CENTER);

        // -- Checklist de itens realizados --
        JLabel lblCheck = criarLabelCampo("Itens realizados:");
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

        tbl_Checklist = criarTabela(mdl_Checklist);
        tbl_Checklist.getColumnModel().getColumn(0).setMaxWidth(55);
        tbl_Checklist.getColumnModel().getColumn(0).setMinWidth(45);
        tbl_Checklist.getColumnModel().getColumn(2).setPreferredWidth(110);
        tbl_Checklist.getColumnModel().getColumn(2).setMaxWidth(160);

        int alturaCheck = Math.min(Math.max((mdl_Checklist.getRowCount() * 26) + 30, 90), 220);
        JScrollPane scpCheck = scrollTabela(tbl_Checklist, alturaCheck);

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
            JButton btn_OrcInterno = botaoAcao("Gerar Orçamento Interno", COR_INFO);
            btn_OrcInterno.addActionListener(e -> gerarOrcamentoInterno());
            pnl_Botoes.add(btn_OrcInterno);
        }

        JButton btn_Confirmar = botaoAcao("✔ Confirmar Etapa", COR_LARANJA);
        btn_Confirmar.addActionListener(e -> confirmarEtapa());
        pnl_Botoes.add(btn_Confirmar);

        pnl.add(pnl_Centro, BorderLayout.CENTER);
        pnl.add(pnl_Botoes, BorderLayout.SOUTH);
        return pnl;
    }

    // -----------------------------------------------------------------------
    // Aviso — OS congelada aguardando atualização do orçamento
    // -----------------------------------------------------------------------
    private JPanel construirAvisoCongelado() {
        JPanel pnl = new PainelCartao(new BorderLayout(0, 6));
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JLabel lblTitulo = new JLabel("SERVIÇO CONGELADO — AGUARDANDO ATUALIZAÇÕES");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(COR_CONGELADA);

        JLabel lblTexto = new JLabel("<html>O orçamento vinculado a esta O.S. foi editado e perdeu a "
                + "aprovação. O registro do trabalho fica pausado até que o orçamento seja "
                + "aprovado novamente na tela de Visualizar Orçamento.</html>");
        lblTexto.setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
        lblTexto.setForeground(COR_TEXTO_CAMPO);
        lblTexto.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        pnl.add(lblTitulo, BorderLayout.NORTH);
        pnl.add(lblTexto, BorderLayout.CENTER);
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
        pnl.add(criarCardLeitura("Descrição do trabalho", descricao, COR_AZUL), BorderLayout.NORTH);

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

        JTable tbl = criarTabela(mdl);
        tbl.getColumnModel().getColumn(0).setMaxWidth(55);
        tbl.getColumnModel().getColumn(0).setMinWidth(45);
        tbl.getColumnModel().getColumn(2).setPreferredWidth(110);
        tbl.getColumnModel().getColumn(2).setMaxWidth(160);

        int alturaTabela = Math.min((mdl.getRowCount() * 26) + 30, 220);

        JLabel lblItens = criarLabelCampo("Itens realizados");
        JScrollPane scp = scrollTabela(tbl, alturaTabela);

        JPanel pnl_Itens = new JPanel(new BorderLayout(0, 4));
        pnl_Itens.setOpaque(false);
        pnl_Itens.add(lblItens, BorderLayout.NORTH);
        pnl_Itens.add(scp, BorderLayout.CENTER);

        pnl.add(pnl_Itens, BorderLayout.CENTER);
        return pnl;
    }

    // -----------------------------------------------------------------------
    // Painel de finalização — OS em andamento
    // -----------------------------------------------------------------------
    private JPanel construirPainelFinalizacao() {
        JLabel lblEtapa = criarLabelEtapa("ETAPA — FINALIZAÇÃO DO SERVIÇO", COR_VERDE);

        JPanel pnl = new PainelCartao(new BorderLayout(0, 12));
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));

        JLabel lblMecanico = criarLabelCampo("Mecânico responsável:");

        GlassComboBox<FuncionarioEntity> cmb_Mecanico = criarCombo();
        cmb_Mecanico.setRenderer(criarRendererFuncionario());
        for (FuncionarioEntity f : controller.listarFuncionarios()) cmb_Mecanico.addItem(f);

        JPanel pnl_Mecanico = new JPanel(new BorderLayout(0, 4));
        pnl_Mecanico.setOpaque(false);
        pnl_Mecanico.add(lblMecanico, BorderLayout.NORTH);
        pnl_Mecanico.add(cmb_Mecanico, BorderLayout.CENTER);

        JLabel lblObs = criarLabelCampo("Observação do mecânico — estado do veículo na saída:");
        lblObs.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));

        GlassTextArea txa_ObsSaida = new GlassTextArea(4, 40);

        JPanel pnl_Obs = new JPanel(new BorderLayout(0, 4));
        pnl_Obs.setOpaque(false);
        pnl_Obs.add(lblObs, BorderLayout.NORTH);
        pnl_Obs.add(envolverEmScroll(txa_ObsSaida), BorderLayout.CENTER);

        JPanel pnl_Centro = new JPanel(new BorderLayout(0, 8));
        pnl_Centro.setOpaque(false);
        pnl_Centro.add(pnl_Mecanico, BorderLayout.NORTH);
        pnl_Centro.add(pnl_Obs, BorderLayout.CENTER);

        JPanel pnl_Botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pnl_Botoes.setOpaque(false);

        JButton btn_Finalizar = botaoAcao("✔ Finalizar Serviço", COR_VERDE);
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
            DialogoAlerta.aviso(this, "Selecione o mecânico responsável.", "Atenção");
            return;
        }

        String observacao = txa.getText().trim();
        if (observacao.length() < 3) {
            DialogoAlerta.aviso(this, "Descreva o estado do veículo na saída (mínimo 3 caracteres).", "Atenção");
            return;
        }

        boolean confirmado = DialogoConfirmacao.confirmar(this,
                "Confirma a finalização desta O.S.? O status ficará CONCLUÍDA e não poderá ser reaberto.",
                "Confirmar Finalização");
        if (!confirmado) return;

        try {
            controller.finalizarOS(idOS, observacao, mecanico.getIdFuncionario());
            DialogoAlerta.sucesso(this, "O.S. finalizada com sucesso!", "Sucesso");
            carregarServico();
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro: " + ex.getMessage(), "Erro");
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
        return criarCardLeitura(titulo, obsItem.descricao(), COR_VERDE);
    }

    // -----------------------------------------------------------------------
    private void confirmarEtapa() {
        if (tbl_Checklist != null && tbl_Checklist.isEditing())
            tbl_Checklist.getCellEditor().stopCellEditing();

        String descricao = txa_Descricao != null ? txa_Descricao.getText().trim() : "";
        if (descricao.length() < 3) {
            DialogoAlerta.aviso(this, "Informe a descrição geral do trabalho (mínimo 3 caracteres).", "Atenção");
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
            DialogoAlerta.sucesso(this, "Etapa registrada. O.S. movida para EM ANDAMENTO.", "Sucesso");
            carregarServico();
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro: " + ex.getMessage(), "Erro");
        }
    }

    private void gerarOrcamentoInterno() {
        if (servico == null) return;
        GlassTextField txtValor = criarTextField();
        GlassComboBox<FuncionarioEntity> cmbResponsavel = criarCombo();
        cmbResponsavel.setRenderer(criarRendererFuncionario());
        for (FuncionarioEntity f : controller.listarFuncionarios()) cmbResponsavel.addItem(f);
        GlassTextField txtObs = criarTextField();

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 6));
        form.setOpaque(false);
        form.add(criarLabelCampo("Valor estimado (R$):")); form.add(txtValor);
        form.add(criarLabelCampo("Mecânico responsável:")); form.add(cmbResponsavel);
        form.add(criarLabelCampo("Observação:"));           form.add(txtObs);

        int res = JOptionPane.showConfirmDialog(this, form,
                "Gerar Orçamento Interno — OS " + servico.codigo(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        double valor;
        try {
            valor = Double.parseDouble(txtValor.getText().trim().replace(",", "."));
            if (valor < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            DialogoAlerta.aviso(this, "Informe um valor válido.", "Atenção");
            return;
        }
        FuncionarioEntity resp = (FuncionarioEntity) cmbResponsavel.getSelectedItem();
        if (resp == null) {
            DialogoAlerta.aviso(this, "Selecione o mecânico responsável.", "Atenção");
            return;
        }
        try {
            long idVeic = servico.idVeiculo() != null ? servico.idVeiculo() : 0L;
            long idCliente = controller.getIdClientePorVeiculo(idVeic);
            controller.criarOrcamentoRevisao(idOS, valor, resp.getNome(),
                    txtObs.getText().trim(), idVeic, idCliente, resp.getIdFuncionario());
            DialogoAlerta.sucesso(this, "Orçamento interno criado com sucesso!", "Sucesso");
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro: " + ex.getMessage(), "Erro");
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

        JPanel pnl = new PainelCartao(new BorderLayout(0, 8));
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel lbl = new JLabel("SERVIÇOS DO ORÇAMENTO");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(COR_AZUL);

        String[] cols = {"Serviço", "Tipo"};
        javax.swing.table.DefaultTableModel mdl = new javax.swing.table.DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (var entry : itens.entrySet()) {
            br.com.oficina.atendimento.CatalogoServicoEntity item = entry.getKey();
            String tipo = "REVISAO".equals(item.getTipo()) ? "Revisão" : "Padrão";
            mdl.addRow(new Object[]{item.getNome(), tipo});
        }

        JTable tbl = criarTabela(mdl);
        tbl.getColumnModel().getColumn(1).setMaxWidth(80);
        tbl.getColumnModel().getColumn(1).setMinWidth(60);

        int altura = Math.min((mdl.getRowCount() * 26) + 30, 200);
        JScrollPane scp = scrollTabela(tbl, altura);

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

        JPanel pnl = new PainelCartao(new BorderLayout(0, 8));
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel lbl = new JLabel("PEÇAS UTILIZADAS");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(COR_VERDE);

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

        JTable tbl = criarTabela(mdl);

        int altura = Math.min((mdl.getRowCount() * 26) + 30, 200);
        JScrollPane scp = scrollTabela(tbl, altura);

        pnl.add(lbl, BorderLayout.NORTH);
        pnl.add(scp, BorderLayout.CENTER);
        return pnl;
    }

    // -----------------------------------------------------------------------
    // Card de leitura genérico (usado em "Descrição do trabalho" e
    // "Estado do veículo na saída"). Antes não definia alignmentX, então
    // ficava CENTRALIZADO quando adicionado direto ao BoxLayout de pnl_Corpo
    // (etapa concluída) — corrigido abaixo.
    // -----------------------------------------------------------------------
    private JPanel criarCardLeitura(String titulo, String conteudo, Color cor) {
        JPanel pnl = new PainelCartao(new BorderLayout(0, 6));
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT); // <- correção do alinhamento
        pnl.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(cor);
        pnl.add(lbl, BorderLayout.NORTH);

        JTextArea txa = new JTextArea(conteudo);
        txa.setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
        txa.setForeground(COR_TEXTO_CAMPO);
        txa.setLineWrap(true);
        txa.setWrapStyleWord(true);
        txa.setEditable(false);
        txa.setOpaque(false);
        txa.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        pnl.add(txa, BorderLayout.CENTER);
        return pnl;
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    // ===== helpers visuais =====
    private JLabel criarLabelEtapa(String texto, Color cor) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(cor);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JLabel criarLabelCampo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_LABEL));
        l.setForeground(COR_LABEL);
        return l;
    }

    private <T> GlassComboBox<T> criarCombo() {
        GlassComboBox<T> cmb = new GlassComboBox<>();
        cmb.setPreferredSize(new Dimension(100, ALTURA_CAMPO));
        return cmb;
    }

    private GlassTextField criarTextField() {
        GlassTextField f = new GlassTextField();
        f.setPreferredSize(new Dimension(0, ALTURA_CAMPO));
        return f;
    }

    private JScrollPane envolverEmScroll(JTextArea area) {
        JScrollPane scp = new JScrollPane(area);
        scp.setOpaque(false);
        scp.getViewport().setOpaque(false);
        scp.setBorder(BorderFactory.createEmptyBorder());
        ScrollBarPadrao.aplicar(scp);
        return scp;
    }

    private DefaultListCellRenderer criarRendererFuncionario() {
        return new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean focus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(l, v, i, sel, focus);
                if (v instanceof FuncionarioEntity f) lbl.setText(f.getNome() + " — " + f.getCargo());
                estilizarCelula(lbl, i, sel);
                return lbl;
            }
        };
    }

    private void estilizarCelula(JLabel lbl, int indice, boolean selecionado) {
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
        lbl.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        if (indice == -1) {
            lbl.setOpaque(false);
            lbl.setForeground(COR_TEXTO_CAMPO);
        } else if (selecionado) {
            lbl.setOpaque(true);
            lbl.setBackground(new Color(255, 173, 51, 60));
            lbl.setForeground(COR_TEXTO_CAMPO);
        } else {
            lbl.setOpaque(true);
            lbl.setBackground(Color.WHITE);
            lbl.setForeground(COR_TEXTO_CAMPO);
        }
    }

    private JButton botaoAcao(String texto, Color cor) {
        return new BotaoAcaoPequeno(texto, cor);
    }

    private JButton botaoLink(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(cor);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JTable criarTabela(DefaultTableModel model) {
        JTable tbl = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                } else {
                    c.setBackground(new Color(255, 173, 51, 80));
                }
                return c;
            }
        };
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl.setForeground(COR_TEXTO_CAMPO);
        tbl.setRowHeight(26);
        tbl.setShowGrid(false);
        tbl.setIntercellSpacing(new Dimension(0, 0));
        tbl.setSelectionBackground(new Color(255, 173, 51, 80));
        tbl.setSelectionForeground(COR_TEXTO_CAMPO);
        JTableHeader header = tbl.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setForeground(COR_TITULO);
        header.setBackground(COR_CARD_BASE);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 28));
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return tbl;
    }

    private JScrollPane scrollTabela(JTable tbl, int altura) {
        JScrollPane scp = new JScrollPane(tbl);
        scp.setBorder(new RoundedBorder(RAIO_COMPONENTE - 4, COR_BORDA_SUAVE));
        scp.getViewport().setBackground(Color.WHITE);
        scp.setPreferredSize(new Dimension(0, altura));
        ScrollBarPadrao.aplicar(scp);
        return scp;
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

    /** Selo/pill do status, com fundo translúcido na cor da etapa atual. */
    private static class SeloStatus extends JLabel {
        private Color corDestaque = COR_LARANJA;

        SeloStatus(String texto) {
            super(texto);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
            setOpaque(false);
        }

        void setCorDestaque(Color cor) {
            this.corDestaque = cor;
            setForeground(cor);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g2.setColor(new Color(corDestaque.getRed(), corDestaque.getGreen(), corDestaque.getBlue(), 35));
            g2.fill(new RoundRectangle2D.Double(0, 0, w, h, h, h));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Campo de texto com efeito de vidro translúcido (glassmorphism), no mesmo
     * padrão visual usado em V_CadastrarCliente / V_CadastrarOrcamento.
     */
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

            Color corBorda = focado ? new Color(255, 153, 0, 210) : COR_BORDA_SUAVE;
            float espessura = focado ? 1.6f : 1f;

            g2.setStroke(new BasicStroke(espessura));
            g2.setColor(corBorda);
            g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.75, h - 2.25, RAIO_COMPONENTE, RAIO_COMPONENTE));
            g2.dispose();
        }
    }

    /**
     * Variante em "vidro" do JTextArea, usada nos campos de descrição/observação,
     * seguindo exatamente a mesma linguagem visual do GlassTextField.
     */
    private static class GlassTextArea extends JTextArea {
        private boolean focado = false;

        GlassTextArea(int linhas, int colunas) {
            super(linhas, colunas);
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
            setCaretColor(COR_TEXTO_CAMPO);
            setSelectionColor(new Color(255, 153, 0, 90));
            setLineWrap(true);
            setWrapStyleWord(true);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
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
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.3), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            Color corBorda = focado ? new Color(255, 153, 0, 210) : COR_BORDA_SUAVE;
            float espessura = focado ? 1.6f : 1f;

            g2.setStroke(new BasicStroke(espessura));
            g2.setColor(corBorda);
            g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.75, h - 2.25, RAIO_COMPONENTE, RAIO_COMPONENTE));
            g2.dispose();
        }
    }

    /**
     * ComboBox com o mesmo acabamento em vidro dos demais campos: pintura própria
     * do fundo/gradiente/borda, com o botão de seta redesenhado via GlassComboBoxUI.
     */
    private static class GlassComboBox<T> extends JComboBox<T> {
        private boolean focado = false;

        GlassComboBox() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, TAMANHO_FONTE_CAMPO));
            setForeground(COR_TEXTO_CAMPO);
            setFocusable(true);
            setUI(new GlassComboBoxUI());
            setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 28));
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

            Color corBorda = focado ? new Color(255, 153, 0, 210) : COR_BORDA_SUAVE;
            float espessura = focado ? 1.6f : 1f;

            g2.setStroke(new BasicStroke(espessura));
            g2.setColor(corBorda);
            g2.draw(new RoundRectangle2D.Double(0.75, 0.75, w - 1.75, h - 2.25, RAIO_COMPONENTE, RAIO_COMPONENTE));
            g2.dispose();
        }
    }

    /** UI mínima que remove a pintura padrão do Swing e estiliza apenas o botão de seta. */
    private static class GlassComboBoxUI extends BasicComboBoxUI {
        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // vazio de propósito: o próprio JComboBox já pinta o fundo em vidro
        }

        @Override
        protected JButton createArrowButton() {
            JButton seta = new JButton("\u25BE");
            seta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            seta.setForeground(COR_LABEL);
            seta.setContentAreaFilled(false);
            seta.setBorderPainted(false);
            seta.setFocusPainted(false);
            seta.setOpaque(false);
            seta.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return seta;
        }
    }

    /**
     * Painel "cartão" translúcido usado para envolver cada bloco de conteúdo da
     * página (edição, leitura, finalização, tabelas de orçamento/peças etc.),
     * reforçando a hierarquia visual sobre o fundo cinza-claro da página.
     */
    private static class PainelCartao extends JPanel {
        PainelCartao(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(70, 90, 110, 18));
            g2.fill(new RoundRectangle2D.Double(1.5, 3, w - 3, h - 3, RAIO_COMPONENTE + 4, RAIO_COMPONENTE + 4));

            g2.setColor(new Color(255, 255, 255, 205));
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 4, RAIO_COMPONENTE + 4, RAIO_COMPONENTE + 4));

            g2.setStroke(new BasicStroke(1f));
            g2.setColor(COR_BORDA_SUAVE);
            g2.draw(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 4, RAIO_COMPONENTE + 4, RAIO_COMPONENTE + 4));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Botão de ação compacto com a mesma linguagem visual dos campos em vidro.
     * A cor de hover/clique é derivada automaticamente da cor base
     * (brighter()/darker()), então basta uma única cor por chamada. Sem
     * tamanho fixo — cresce conforme o texto, evitando corte em rótulos longos
     * como "Gerar Orçamento Interno".
     */
    private static class BotaoAcaoPequeno extends JButton {
        private final Color corBase;
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoAcaoPequeno(String texto, Color corBase) {
            super(texto);
            this.corBase = corBase;
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
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
            int raio = RAIO_COMPONENTE - 2;

            g2.setColor(new Color(0, 0, 0, 35));
            g2.fill(new RoundRectangle2D.Double(1, 2, w - 2, h - 2, raio, raio));

            Color corPreenchimento = pressionado ? corBase.darker() : (sobreMouse ? corBase.brighter() : corBase);
            g2.setColor(corPreenchimento);
            g2.fill(new RoundRectangle2D.Double(0.5, 0.5, w - 2, h - 3, raio, raio));

            g2.setColor(new Color(255, 255, 255, 40));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), raio - 3, raio - 3));

            g2.dispose();
            super.paintComponent(g);
        }
    }
}