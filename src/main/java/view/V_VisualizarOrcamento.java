package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
import br.com.oficina.estoque.PecaEntity;
import br.com.oficina.usuario.FuncionarioEntity;
import controller.OficinaController;
import model.Cliente;
import model.Orcamento;
import model.OrdemDeServico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class V_VisualizarOrcamento extends JPanel {

    private final OficinaController controller;
    private final Orcamento orcamento;

    private LinkedHashMap<CatalogoServicoEntity, Double> dadosItens = new LinkedHashMap<>();
    private List<Object[]> dadosPecas = new ArrayList<>();
    private double subtotalServicos = 0;
    private double subtotalPecas = 0;

    public V_VisualizarOrcamento(OficinaController controller, Orcamento orcamento) {
        this.controller = controller;
        this.orcamento = orcamento;
        carregarDados();
        setBackground(Color.decode("#F5F5F5"));
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        construir();
    }

    private void carregarDados() {
        try {
            dadosItens = controller.listarItensOrcamentoComValor(orcamento.getIdOrcamento());
            subtotalServicos = dadosItens.values().stream().mapToDouble(Double::doubleValue).sum();
        } catch (Exception ignored) {}
        try {
            dadosPecas = controller.listarPecasOrcamentoComValor(orcamento.getIdOrcamento());
            subtotalPecas = dadosPecas.stream().mapToDouble(t -> (Double) t[1]).sum();
        } catch (Exception ignored) {}
    }

    private String resolverNomeProprietario() {
        long id = orcamento.getIdCliente();
        Cliente c = controller.cliente_id(id);
        if (c != null) return c.getNome();
        for (FuncionarioEntity f : controller.listarFuncionarios())
            if (f.getIdUsuario() != null && f.getIdUsuario() == id)
                return f.getNome() + " (Func.)";
        return "—";
    }

    private void construir() {
        add(criarTopo(), BorderLayout.NORTH);

        JPanel corpo = new JPanel();
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
        corpo.setOpaque(false);
        corpo.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));

        corpo.add(criarBadgeStatus());
        corpo.add(Box.createVerticalStrut(16));
        corpo.add(criarCardInfo());
        corpo.add(Box.createVerticalStrut(14));
        corpo.add(criarCardReclamacao());
        corpo.add(Box.createVerticalStrut(14));
        corpo.add(criarSecaoItens());
        corpo.add(Box.createVerticalStrut(14));
        corpo.add(criarSecaoPecas());
        corpo.add(Box.createVerticalStrut(14));
        corpo.add(criarRodapeTotal());
        corpo.add(Box.createVerticalStrut(8));

        JScrollPane scroll = new JScrollPane(corpo);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel criarTopo() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);

        JLabel lbl = new JLabel("Orçamentos > Detalhes do Orçamento");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(Color.decode("#4D4D4D"));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setOpaque(false);

        String status = orcamento.getStatus();
        if ("PENDENTE".equalsIgnoreCase(status)) {
            JButton btnAprovar = criarBotao("Aprovar", "#28A745");
            JButton btnReprovar = criarBotao("Reprovar", "#DC3545");
            btnAprovar.addActionListener(e -> mudarStatus(true));
            btnReprovar.addActionListener(e -> mudarStatus(false));
            botoes.add(btnReprovar);
            botoes.add(btnAprovar);
        } else if ("APROVADO".equalsIgnoreCase(status)) {
            JButton btnGerar = criarBotao("Gerar Serviço", "#17A2B8");
            btnGerar.addActionListener(e -> gerarServico());
            botoes.add(btnGerar);
        }

        JButton btnVoltar = new JButton("← Voltar");
        btnVoltar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVoltar.setFocusPainted(false);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setContentAreaFilled(false);
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.addActionListener(e -> navegar(new V_AprovarOrcamento(controller)));
        botoes.add(btnVoltar);

        pnl.add(lbl, BorderLayout.WEST);
        pnl.add(botoes, BorderLayout.EAST);
        return pnl;
    }

    private JPanel criarBadgeStatus() {
        String status = orcamento.getStatus() != null ? orcamento.getStatus().toUpperCase() : "—";
        Color cor = switch (status) {
            case "APROVADO"  -> Color.decode("#28A745");
            case "REPROVADO" -> Color.decode("#DC3545");
            default          -> Color.decode("#FF9900");
        };

        String codigo = orcamento.getCodigo().isEmpty()
            ? String.format("#%04d", orcamento.getIdOrcamento())
            : orcamento.getCodigo();

        JPanel pnl = new JPanel(new BorderLayout(12, 0));
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.setBackground(cor);
        pnl.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel lblCodigo = new JLabel("Orçamento " + codigo);
        lblCodigo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCodigo.setForeground(Color.WHITE);

        JLabel lblStatus = new JLabel(status);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatus.setForeground(Color.WHITE);

        pnl.add(lblCodigo, BorderLayout.WEST);
        pnl.add(lblStatus, BorderLayout.EAST);
        return pnl;
    }

    private JPanel criarCardInfo() {
        JPanel card = new JPanel(new GridLayout(0, 4, 16, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E0E0E0")),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        addCampo(card, "Cliente", resolverNomeProprietario());
        addCampo(card, "Veículo (Placa)", valorOuTracinho(orcamento.getPlacaVeiculo()));
        addCampo(card, "Responsável", valorOuTracinho(orcamento.getResponsavel()));
        addCampo(card, "Data de Criação", valorOuTracinho(orcamento.getDataCriacao()));

        return card;
    }

    private JPanel criarCardReclamacao() {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E0E0E0")),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel lbl = new JLabel("Reclamação / Descrição do Problema");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.decode("#555555"));

        JLabel val = new JLabel("<html>" + valorOuTracinho(orcamento.getReclamacao()).replace("\n", "<br>") + "</html>");
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(Color.decode("#333333"));

        card.add(lbl, BorderLayout.NORTH);
        card.add(val, BorderLayout.CENTER);
        return card;
    }

    private JPanel criarSecaoItens() {
        JPanel sec = new JPanel(new BorderLayout(0, 6));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JLabel lbl = new JLabel("Itens de Serviço");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(Color.decode("#333333"));

        String[] cols = {"Serviço", "Tipo", "Valor (R$)"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        for (var entry : dadosItens.entrySet()) {
            CatalogoServicoEntity item = entry.getKey();
            String tipo = "REVISAO".equals(item.getTipo()) ? "Revisão" : "Padrão";
            mdl.addRow(new Object[]{item.getNome(), tipo, String.format("R$ %.2f", entry.getValue())});
        }

        JTable tbl = criarTabela(mdl);
        JScrollPane scroll = new JScrollPane(tbl);
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")));

        sec.add(lbl, BorderLayout.NORTH);
        sec.add(scroll, BorderLayout.CENTER);
        return sec;
    }

    private JPanel criarSecaoPecas() {
        JPanel sec = new JPanel(new BorderLayout(0, 6));
        sec.setOpaque(false);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel lbl = new JLabel("Peças a Substituir");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(Color.decode("#333333"));

        sec.add(lbl, BorderLayout.NORTH);

        if (dadosPecas.isEmpty()) {
            JLabel lblVazio = new JLabel("Nenhuma peça vinculada a este orçamento.");
            lblVazio.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblVazio.setForeground(Color.decode("#888888"));
            sec.add(lblVazio, BorderLayout.CENTER);
        } else {
            String[] cols = {"Peça (Popular)", "Nome Técnico", "Fabricante", "Valor (R$)"};
            DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };
            for (Object[] triple : dadosPecas) {
                PecaEntity peca = (PecaEntity) triple[0];
                double valor = (Double) triple[1];
                String nomeTecnico = triple.length > 2 ? (String) triple[2] : "";
                String fabricante = triple.length > 3 ? (String) triple[3] : "";
                mdl.addRow(new Object[]{peca.getNomeExibicao(), nomeTecnico,
                        fabricante.isBlank() ? "—" : fabricante, String.format("R$ %.2f", valor)});
            }
            JTable tbl = criarTabela(mdl);
            JScrollPane scroll = new JScrollPane(tbl);
            scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")));
            sec.add(scroll, BorderLayout.CENTER);
        }
        return sec;
    }

    private JPanel criarRodapeTotal() {
        JPanel pnl = new JPanel(new GridLayout(3, 2, 0, 4));
        pnl.setBackground(Color.decode("#FFF8F0"));
        pnl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#FF9900")),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        addLinhaTotais(pnl, "Serviços:", String.format("R$ %.2f", subtotalServicos), "#555555", 12, Font.PLAIN);
        addLinhaTotais(pnl, "Peças:", String.format("R$ %.2f", subtotalPecas), "#555555", 12, Font.PLAIN);
        addLinhaTotais(pnl, "Total:", String.format("R$ %.2f", subtotalServicos + subtotalPecas), "#FF9900", 15, Font.BOLD);

        return pnl;
    }

    private void addLinhaTotais(JPanel pnl, String rotulo, String valor, String corHex, int tamanho, int estilo) {
        JLabel lbl = new JLabel(rotulo);
        lbl.setFont(new Font("Segoe UI", estilo, tamanho));
        lbl.setForeground(Color.decode("#555555"));

        JLabel val = new JLabel(valor);
        val.setFont(new Font("Segoe UI", estilo, tamanho));
        val.setForeground(Color.decode(corHex));
        val.setHorizontalAlignment(SwingConstants.RIGHT);

        pnl.add(lbl);
        pnl.add(val);
    }

    private void mudarStatus(boolean aprovar) {
        if (!confirmarComSenha(aprovar ? "aprovar este orçamento" : "reprovar este orçamento")) return;
        try {
            if (aprovar) controller.aprovarOrcamento(orcamento.getIdOrcamento());
            else controller.reprovarOrcamento(orcamento.getIdOrcamento());
            JOptionPane.showMessageDialog(this,
                "Orçamento " + (aprovar ? "aprovado" : "reprovado") + " com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            navegar(new V_AprovarOrcamento(controller));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(),
                "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void gerarServico() {
        JTextField txtTitulo = new JTextField();
        JComboBox<String> cmbTipo = new JComboBox<>();
        for (OrdemDeServico.TipoServicoOS t : OrdemDeServico.TipoServicoOS.values())
            cmbTipo.addItem(t.getLabel());
        JComboBox<String> cmbManutencao = new JComboBox<>();
        for (OrdemDeServico.TipoManutencao m : OrdemDeServico.TipoManutencao.values())
            cmbManutencao.addItem(m.getLabel());
        JTextField txtData = new JTextField(LocalDate.now().toString());
        JTextField txtKm = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 4));
        form.add(new JLabel("Título do Serviço:")); form.add(txtTitulo);
        form.add(new JLabel("Tipo de Serviço:"));   form.add(cmbTipo);
        form.add(new JLabel("Tipo de Manutenção:")); form.add(cmbManutencao);
        form.add(new JLabel("Data (AAAA-MM-DD):")); form.add(txtData);
        form.add(new JLabel("KM atual:"));           form.add(txtKm);

        int r = JOptionPane.showConfirmDialog(this, form,
            "Gerar Serviço do Orçamento " + orcamento.getCodigo(),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        String titulo = txtTitulo.getText().trim();
        if (titulo.length() < 3) { aviso("Título deve ter pelo menos 3 caracteres."); return; }
        int km;
        try { km = Integer.parseInt(txtKm.getText().trim()); if (km < 0) throw new NumberFormatException(); }
        catch (NumberFormatException ex) { aviso("Informe um KM válido."); return; }

        try {
            OrdemDeServico.TipoServicoOS tipoEnum =
                OrdemDeServico.TipoServicoOS.fromLabel((String) cmbTipo.getSelectedItem());
            OrdemDeServico.TipoManutencao manutEnum =
                OrdemDeServico.TipoManutencao.fromLabel((String) cmbManutencao.getSelectedItem());
            controller.abrirOSDeOrcamento(titulo, tipoEnum.name(), manutEnum.name(),
                txtData.getText().trim(), km, orcamento.getIdVeiculo(), orcamento.getIdOrcamento());
            JOptionPane.showMessageDialog(this, "Ordem de Serviço gerada com sucesso!",
                "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            navegar(new V_AprovarOrcamento(controller));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar serviço: " + ex.getMessage(),
                "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    private void aviso(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    private boolean confirmarComSenha(String acao) {
        JPasswordField pwd = new JPasswordField();
        int r = JOptionPane.showConfirmDialog(this, pwd, "Confirme a senha para " + acao,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return false;
        String senha = new String(pwd.getPassword());
        if (senha.isEmpty() || !controller.confirmarSenha(senha)) {
            JOptionPane.showMessageDialog(this, "Senha incorreta.", "Acesso Negado", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void addCampo(JPanel pnl, String rotulo, String valor) {
        JLabel lbl = new JLabel(rotulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.decode("#555555"));
        JLabel val = new JLabel(valor);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(Color.decode("#333333"));
        pnl.add(lbl);
        pnl.add(val);
    }

    private JTable criarTabela(DefaultTableModel mdl) {
        JTable tbl = new JTable(mdl);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl.setRowHeight(26);
        tbl.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return tbl;
    }

    private JButton criarBotao(String texto, String cor) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.decode(cor));
        btn.setPreferredSize(new Dimension(130, 34));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private String valorOuTracinho(String s) {
        return (s == null || s.isBlank()) ? "—" : s;
    }
}
