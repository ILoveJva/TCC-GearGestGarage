package view;

import controller.OficinaController;
import br.com.oficina.usuario.FuncionarioEntity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class V_Configuracoes extends JPanel {

    private final OficinaController controller;
    private JTable tbl_Funcionarios;
    private DefaultTableModel mdl_Funcionarios;

    public V_Configuracoes(OficinaController controller) {
        this.controller = controller;
        setBackground(Color.decode("#F5F5F5"));
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        construirInterface();
    }

    private void construirInterface() {
        // ---- Breadcrumb ----
        JLabel lbl_Titulo = new JLabel("Página Inicial > Configurações da Oficina");
        lbl_Titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_Titulo.setForeground(Color.decode("#4D4D4D"));
        lbl_Titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        add(lbl_Titulo, BorderLayout.NORTH);

        // ---- Corpo central ----
        JPanel pnl_Corpo = new JPanel();
        pnl_Corpo.setLayout(new BoxLayout(pnl_Corpo, BoxLayout.Y_AXIS));
        pnl_Corpo.setOpaque(false);

        pnl_Corpo.add(criarSecaoDados());
        pnl_Corpo.add(Box.createVerticalStrut(16));
        pnl_Corpo.add(criarSecaoCatalogo());
        pnl_Corpo.add(Box.createVerticalStrut(16));
        pnl_Corpo.add(criarSecaoEquipe());

        JScrollPane scroll = new JScrollPane(pnl_Corpo);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        add(scroll, BorderLayout.CENTER);
    }

    // =========================================================================
    // SEÇÃO 1 — Dados da Oficina
    // =========================================================================
    private JPanel criarSecaoDados() {
        JPanel card = criarCard("Dados da Oficina");

        model.Oficina of = controller.getOficina();

        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 12));
        grid.setOpaque(false);
        grid.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        grid.add(criarCampoDado("Nome",     of.getNome()));
        grid.add(criarCampoDado("CNPJ",     of.getCnpj()));
        grid.add(criarCampoDado("Endereço", of.getEndereco()));
        grid.add(criarCampoDado("Telefone", of.getTelefone()));

        JPanel corpo = (JPanel) card.getComponent(1);
        corpo.add(grid);
        return card;
    }

    // =========================================================================
    // SEÇÃO 2 — Catálogo de Veículos
    // =========================================================================
    private JPanel criarSecaoCatalogo() {
        JPanel card = criarCard("Catálogo de Veículos");

        JPanel corpo = (JPanel) card.getComponent(1);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));

        JLabel lbl_Desc = new JLabel("Gerencie as montadoras, modelos e peças genéricas disponíveis no sistema.");
        lbl_Desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl_Desc.setForeground(Color.decode("#666666"));
        lbl_Desc.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        lbl_Desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnl_Linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnl_Linha.setOpaque(false);
        pnl_Linha.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btn_Montadora = criarBotaoAcao("Nova Montadora", "#FF9900");
        JButton btn_Modelo    = criarBotaoAcao("Novo Modelo",    "#6C757D");
        JButton btn_Peca      = criarBotaoAcao("Nova Peça",      "#28A745");

        btn_Montadora.addActionListener(e -> navegarPara(new V_CadastrarMontadora(controller)));
        btn_Modelo.addActionListener(e    -> navegarPara(new V_CadastrarModelo(controller)));
        btn_Peca.addActionListener(e      -> navegarPara(new V_CadastrarPeca(controller)));

        pnl_Linha.add(btn_Montadora);
        pnl_Linha.add(Box.createHorizontalStrut(12));
        pnl_Linha.add(btn_Modelo);
        pnl_Linha.add(Box.createHorizontalStrut(12));
        pnl_Linha.add(btn_Peca);

        corpo.add(lbl_Desc);
        corpo.add(pnl_Linha);
        return card;
    }

    // =========================================================================
    // SEÇÃO 4 — Equipe
    // =========================================================================
    private JPanel criarSecaoEquipe() {
        JPanel card = criarCard("Equipe");

        JPanel corpo = (JPanel) card.getComponent(1);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));

        JLabel lbl_Desc = new JLabel("Cadastre funcionários e defina os tipos de serviço da oficina.");
        lbl_Desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl_Desc.setForeground(Color.decode("#666666"));
        lbl_Desc.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        lbl_Desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnl_Linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnl_Linha.setOpaque(false);
        pnl_Linha.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btn_Funcionario   = criarBotaoAcao("Novo Funcionário",    "#6C757D");
        JButton btn_TipoServico   = criarBotaoAcao("Tipos de Serviço",    "#FF9900");
        JButton btn_ItemServico   = criarBotaoAcao("Itens de Serviço",    "#17A2B8");

        btn_Funcionario.addActionListener(e -> navegarPara(new V_CadastrarFuncionario(controller)));
        btn_TipoServico.addActionListener(e -> navegarPara(new V_CadastrarTipoServico(controller)));
        btn_ItemServico.addActionListener(e -> navegarPara(new V_CadastrarItemServico(controller)));

        pnl_Linha.add(btn_Funcionario);
        pnl_Linha.add(Box.createHorizontalStrut(12));
        pnl_Linha.add(btn_TipoServico);
        pnl_Linha.add(Box.createHorizontalStrut(12));
        pnl_Linha.add(btn_ItemServico);

        corpo.add(lbl_Desc);
        corpo.add(pnl_Linha);
        corpo.add(Box.createVerticalStrut(16));
        corpo.add(criarListaFuncionarios());
        return card;
    }

    /** Tabela de funcionários cadastrados, com ações de Editar/Excluir sobre a linha selecionada. */
    private JPanel criarListaFuncionarios() {
        JPanel pnl = new JPanel(new BorderLayout(0, 8));
        pnl.setOpaque(false);
        pnl.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] colunas = {"Cód.", "Nome", "Cargo", "Telefone"};
        mdl_Funcionarios = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tbl_Funcionarios = new JTable(mdl_Funcionarios);
        tbl_Funcionarios.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl_Funcionarios.setRowHeight(26);
        tbl_Funcionarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl_Funcionarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tbl_Funcionarios.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tbl_Funcionarios);
        scroll.setPreferredSize(new Dimension(0, 160));
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")));

        JPanel pnl_Acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        pnl_Acoes.setOpaque(false);
        JButton btn_Editar = criarBotaoAcao("Editar", "#6C757D");
        JButton btn_Excluir = criarBotaoAcao("Excluir", "#DC3545");
        btn_Editar.setPreferredSize(new Dimension(120, 36));
        btn_Excluir.setPreferredSize(new Dimension(120, 36));

        btn_Editar.addActionListener(e -> {
            FuncionarioEntity alvo = obterFuncionarioSelecionado();
            if (alvo == null) {
                JOptionPane.showMessageDialog(this,
                    "Selecione um funcionário na lista para editar.",
                    "Nenhum funcionário selecionado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            navegarPara(new V_EditarFuncionario(controller, alvo));
        });

        btn_Excluir.addActionListener(e -> {
            FuncionarioEntity alvo = obterFuncionarioSelecionado();
            if (alvo == null) {
                JOptionPane.showMessageDialog(this,
                    "Selecione um funcionário na lista para excluir.",
                    "Nenhum funcionário selecionado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int resp = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir o funcionário \"" + alvo.getNome() + "\"?",
                "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (resp != JOptionPane.YES_OPTION) return;
            try {
                controller.excluirFuncionario(alvo.getIdFuncionario());
                JOptionPane.showMessageDialog(this,
                    "Funcionário excluído com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarFuncionarios();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao excluir funcionário: " + ex.getMessage(),
                    "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnl_Acoes.add(btn_Editar);
        pnl_Acoes.add(btn_Excluir);

        tbl_Funcionarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) return;
                FuncionarioEntity alvo = obterFuncionarioSelecionado();
                if (alvo != null) navegarPara(new V_EditarFuncionario(controller, alvo));
            }
        });

        pnl.add(scroll, BorderLayout.CENTER);
        pnl.add(pnl_Acoes, BorderLayout.SOUTH);

        carregarFuncionarios();
        return pnl;
    }

    private void carregarFuncionarios() {
        mdl_Funcionarios.setRowCount(0);
        for (FuncionarioEntity f : controller.listarFuncionarios()) {
            mdl_Funcionarios.addRow(new Object[]{
                String.format("%05d", f.getIdFuncionario()),
                f.getNome(), f.getCargo(), f.getTelefone()
            });
        }
    }

    private FuncionarioEntity obterFuncionarioSelecionado() {
        int linha = tbl_Funcionarios.getSelectedRow();
        if (linha < 0) return null;
        Object cod = tbl_Funcionarios.getValueAt(linha, 0);
        long idFuncionario;
        try {
            idFuncionario = Long.parseLong(String.valueOf(cod).trim());
        } catch (NumberFormatException ex) {
            return null;
        }
        for (FuncionarioEntity f : controller.listarFuncionarios()) {
            if (f.getIdFuncionario() == idFuncionario) return f;
        }
        return null;
    }

    // =========================================================================
    // NAVEGAÇÃO
    // =========================================================================
    private void navegarPara(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    // =========================================================================
    // HELPERS VISUAIS
    // =========================================================================

    /** Cria um painel card (fundo branco, título laranja, corpo livre). */
    private JPanel criarCard(String titulo) {
        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, Color.decode("#E0E0E0")),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.decode("#FF9900"));
        lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#F0F0F0")));
        lbl.setPreferredSize(new Dimension(0, 30));

        JPanel corpo = new JPanel();
        corpo.setOpaque(false);
        corpo.setLayout(new BorderLayout());

        card.add(lbl, BorderLayout.NORTH);
        card.add(corpo, BorderLayout.CENTER);
        return card;
    }

    /** Campo informativo: label cinza + valor em negrito. */
    private JPanel criarCampoDado(String rotulo, String valor) {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);

        JLabel lbl = new JLabel(rotulo.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl.setForeground(Color.decode("#999999"));

        JLabel val = new JLabel(valor != null && !valor.isBlank() ? valor : "—");
        val.setFont(new Font("Segoe UI", Font.BOLD, 13));
        val.setForeground(Color.decode("#333333"));

        p.add(lbl, BorderLayout.NORTH);
        p.add(val, BorderLayout.CENTER);
        return p;
    }

    /** Card clicável de estatística com número grande e rótulo. */
    private JPanel criarCardEstat(String rotulo, int valor, String corHex, Runnable aoClicar) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, Color.decode("#E0E0E0")),
            BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel barra = new JPanel();
        barra.setBackground(Color.decode(corHex));
        barra.setPreferredSize(new Dimension(0, 4));

        JLabel num = new JLabel(String.valueOf(valor), SwingConstants.CENTER);
        num.setFont(new Font("Segoe UI", Font.BOLD, 28));
        num.setForeground(Color.decode(corHex));

        JLabel lbl = new JLabel(rotulo, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(Color.decode("#666666"));

        JLabel lbl_Dica = new JLabel("Ver detalhes →", SwingConstants.CENTER);
        lbl_Dica.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lbl_Dica.setForeground(Color.decode(corHex));

        JPanel sul = new JPanel(new BorderLayout(0, 2));
        sul.setOpaque(false);
        sul.add(lbl, BorderLayout.NORTH);
        sul.add(lbl_Dica, BorderLayout.SOUTH);

        MouseAdapter clique = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { aoClicar.run(); }
            @Override public void mouseEntered(MouseEvent e) { p.setBackground(Color.decode("#F9F9F9")); }
            @Override public void mouseExited(MouseEvent e)  { p.setBackground(Color.WHITE); }
        };
        p.addMouseListener(clique);
        num.addMouseListener(clique);
        lbl.addMouseListener(clique);
        lbl_Dica.addMouseListener(clique);

        p.add(barra, BorderLayout.NORTH);
        p.add(num,   BorderLayout.CENTER);
        p.add(sul,   BorderLayout.SOUTH);
        return p;
    }

    private JButton criarBotaoAcao(String texto, String corHex) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(Color.decode(corHex));
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // =========================================================================
    // INNER CLASS — Borda arredondada
    // =========================================================================
    private static class RoundedBorder implements javax.swing.border.Border {
        private final int raio;
        private final Color cor;
        RoundedBorder(int raio, Color cor) { this.raio = raio; this.cor = cor; }
        public Insets getBorderInsets(Component c) { return new Insets(raio/2, raio/2, raio/2, raio/2); }
        public boolean isBorderOpaque() { return false; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(cor);
            g2d.draw(new RoundRectangle2D.Double(x, y, w-1, h-1, raio, raio));
            g2d.dispose();
        }
    }
}
