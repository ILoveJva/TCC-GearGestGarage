package view;

import controller.OficinaController;
import br.com.oficina.usuario.FuncionarioEntity;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

/**
 * Segue o mesmo padrão visual da Página Inicial (cards com opacidade/cantos
 * reais, ícone por seção e scrollbar animada), já que as duas telas são do
 * mesmo nível hierárquico (Início ⇄ Configurações).
 */
public class V_Configuracoes extends JPanel {

    private static final Color COR_CARTAO             = new Color(255, 253, 248, 235);
    private static final Color COR_CARTAO_OPACA        = new Color(255, 253, 248);
    private static final Color COR_CARTAO_OPACA_HOVER  = new Color(255, 248, 235);

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
        ScrollBarPadrao.aplicar(scroll);
        add(scroll, BorderLayout.CENTER);
    }

    // =========================================================================
    // SEÇÃO 1 — Dados da Oficina
    // =========================================================================
    private JPanel criarSecaoDados() {
        JPanel card = criarCard("Dados da Oficina",
                obterIcone("/assets/icons/oficina.png", 18, IconeVetorial.Tipo.GARAGEM, Color.decode("#FF9900")));

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
        JPanel card = criarCard("Catálogo de Veículos",
                obterIcone("/assets/icons/estoque.png", 18, IconeVetorial.Tipo.CATALOGO, Color.decode("#FF9900")));

        JPanel corpo = (JPanel) card.getComponent(1);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));

        JLabel lbl_Desc = new JLabel("Gerencie as montadoras, modelos e peças genéricas disponíveis no sistema.");
        lbl_Desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl_Desc.setForeground(Color.decode("#666666"));
        lbl_Desc.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        lbl_Desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnl_Linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        pnl_Linha.setOpaque(false);
        pnl_Linha.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btn_Montadora = criarBotaoQuadrado("Nova Montadora", "/assets/icons/montadoras.png", IconeVetorial.Tipo.MONTADORA, "#FF9900");
        JButton btn_Modelo    = criarBotaoQuadrado("Novo Modelo",    "/assets/icons/modelos.png",    IconeVetorial.Tipo.MODELO,    "#6C757D");
        JButton btn_Peca      = criarBotaoQuadrado("Nova Peça",      "/assets/icons/pecas.png",      IconeVetorial.Tipo.PECA,       "#28A745");

        btn_Montadora.addActionListener(e -> navegarPara(new V_CadastrarMontadora(controller)));
        btn_Modelo.addActionListener(e    -> navegarPara(new V_CadastrarModelo(controller)));
        btn_Peca.addActionListener(e      -> navegarPara(new V_CadastrarPeca(controller)));

        pnl_Linha.add(btn_Montadora);
        pnl_Linha.add(btn_Modelo);
        pnl_Linha.add(btn_Peca);

        corpo.add(lbl_Desc);
        corpo.add(pnl_Linha);
        return card;
    }

    // =========================================================================
    // SEÇÃO 3 — Equipe
    // =========================================================================
    private JPanel criarSecaoEquipe() {
        JPanel card = criarCard("Equipe",
                obterIcone("/assets/icons/vclientes.png", 18, IconeVetorial.Tipo.PESSOA, Color.decode("#FF9900")));

        JPanel corpo = (JPanel) card.getComponent(1);
        corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));

        JLabel lbl_Desc = new JLabel("Cadastre funcionários e defina os tipos de serviço da oficina.");
        lbl_Desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl_Desc.setForeground(Color.decode("#666666"));
        lbl_Desc.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));
        lbl_Desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnl_Linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        pnl_Linha.setOpaque(false);
        pnl_Linha.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btn_Funcionario   = criarBotaoQuadrado("Novo Funcionário", "/assets/icons/servicos.png",     IconeVetorial.Tipo.PESSOA,    "#6C757D");
        JButton btn_TipoServico   = criarBotaoQuadrado("Tipos de Serviço", "/assets/icons/add_servico.png",  IconeVetorial.Tipo.SERVICO,   "#FF9900");
        JButton btn_ItemServico   = criarBotaoQuadrado("Itens de Serviço", "/assets/icons/vservicos.png",    IconeVetorial.Tipo.DOCUMENTO, "#17A2B8");

        btn_Funcionario.addActionListener(e -> navegarPara(new V_CadastrarFuncionario(controller)));
        btn_TipoServico.addActionListener(e -> navegarPara(new V_CadastrarTipoServico(controller)));
        btn_ItemServico.addActionListener(e -> navegarPara(new V_CadastrarItemServico(controller)));

        pnl_Linha.add(btn_Funcionario);
        pnl_Linha.add(btn_TipoServico);
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
        tbl_Funcionarios.setSelectionBackground(Color.decode("#FFE8CC"));
        tbl_Funcionarios.setSelectionForeground(Color.decode("#333333"));
        tbl_Funcionarios.setGridColor(Color.decode("#EEEEEE"));
        tbl_Funcionarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tbl_Funcionarios.getTableHeader().setReorderingAllowed(false);
        tbl_Funcionarios.getTableHeader().setBackground(Color.decode("#FAFAFA"));
        tbl_Funcionarios.getTableHeader().setForeground(Color.decode("#4D4D4D"));

        JScrollPane scroll = new JScrollPane(tbl_Funcionarios);
        scroll.setPreferredSize(new Dimension(0, 160));
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")));
        ScrollBarPadrao.aplicar(scroll);

        JPanel pnl_Acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        pnl_Acoes.setOpaque(false);
        JButton btn_Editar = criarBotaoQuadrado("Editar", null, IconeVetorial.Tipo.EDITAR, "#6C757D", 84, 76);
        JButton btn_Excluir = criarBotaoQuadrado("Excluir", null, IconeVetorial.Tipo.EXCLUIR, "#DC3545", 84, 76);

        btn_Editar.addActionListener(e -> {
            FuncionarioEntity alvo = obterFuncionarioSelecionado();
            if (alvo == null) {
                DialogoAlerta.aviso(this, "Selecione um funcionário na lista para editar.", "Nenhum funcionário selecionado");
                return;
            }
            navegarPara(new V_EditarFuncionario(controller, alvo));
        });

        btn_Excluir.addActionListener(e -> {
            FuncionarioEntity alvo = obterFuncionarioSelecionado();
            if (alvo == null) {
                DialogoAlerta.aviso(this, "Selecione um funcionário na lista para excluir.", "Nenhum funcionário selecionado");
                return;
            }
            boolean confirmado = DialogoConfirmacao.confirmar(this,
                "Tem certeza que deseja excluir o funcionário \"" + alvo.getNome() + "\"?",
                "Confirmar exclusão");
            if (!confirmado) return;
            try {
                controller.excluirFuncionario(alvo.getIdFuncionario());
                DialogoAlerta.sucesso(this, "Funcionário excluído com sucesso!", "Sucesso");
                carregarFuncionarios();
            } catch (Exception ex) {
                DialogoAlerta.erro(this, "Erro ao excluir funcionário: " + ex.getMessage(), "Erro no Sistema");
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

    /** Cria um painel card no mesmo padrão da Página Inicial: fundo com opacidade, cantos reais, título com ícone. */
    private JPanel criarCard(String titulo, Icon icone) {
        int raioCard = 14;
        PainelCartao card = new PainelCartao(new BorderLayout(0, 12), raioCard, COR_CARTAO);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(raioCard, Color.decode("#E0E0E0")),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnl_TituloCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnl_TituloCard.setOpaque(false);
        pnl_TituloCard.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#F0F0F0")));
        pnl_TituloCard.setPreferredSize(new Dimension(0, 30));

        if (icone != null) {
            pnl_TituloCard.add(new JLabel(icone));
        }

        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(Color.decode("#FF9900"));
        pnl_TituloCard.add(lbl);

        JPanel corpo = new JPanel();
        corpo.setOpaque(false);
        corpo.setLayout(new BorderLayout());

        card.add(pnl_TituloCard, BorderLayout.NORTH);
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

    private JButton criarBotaoQuadrado(String texto, String caminhoPng, IconeVetorial.Tipo tipoIcone, String corHex) {
        return criarBotaoQuadrado(texto, caminhoPng, tipoIcone, corHex, 108, 96);
    }

    /**
     * Botão quadrado com ícone acima do texto (mesmo padrão dos cards de "Consultas" da Página
     * Inicial). Usa a imagem PNG de resources/assets/icons quando disponível; se não existir,
     * cai para o ícone vetorial desenhado na hora.
     */
    private JButton criarBotaoQuadrado(String texto, String caminhoPng, IconeVetorial.Tipo tipoIcone, String corHex, int largura, int altura) {
        JButton btn = new JButton(texto);
        btn.setIcon(obterIcone(caminhoPng, Math.min(largura, altura) - 46, tipoIcone, Color.WHITE));
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setIconTextGap(8);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        Color corBase = Color.decode(corHex);
        btn.setBackground(corBase);
        btn.setPreferredSize(new Dimension(largura, altura));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(corBase.brighter()); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(corBase); }
        });
        return btn;
    }

    // =========================================================================
    // FUNDO DOS CARDS (COR + OPACIDADE, CANTOS REAIS)
    // =========================================================================
    private static class PainelCartao extends JPanel {
        private final int raio;
        private final Color corFundo;

        PainelCartao(LayoutManager layout, int raio, Color corFundo) {
            super(layout);
            this.raio = raio;
            this.corFundo = corFundo;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(corFundo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), raio, raio);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // =========================================================================
    // BORDA ARREDONDADA
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

    // =========================================================================
    // ÍCONES (PNG do projeto, com fallback vetorial desenhado na hora)
    // =========================================================================
    private ImageIcon carregarERedimensionarIcone(String caminho, int larguraAlvo, int alturaAlvo) {
        URL url = getClass().getResource(caminho);
        if (url == null) {
            String caminhoLimpo = caminho.startsWith("/") ? caminho.substring(1) : caminho;
            url = Thread.currentThread().getContextClassLoader().getResource(caminhoLimpo);
        }

        if (url != null) {
            ImageIcon imagemOriginal = new ImageIcon(url);
            Image imgEscalada = imagemOriginal.getImage().getScaledInstance(larguraAlvo, alturaAlvo, Image.SCALE_SMOOTH);
            return new ImageIcon(imgEscalada);
        }
        return null;
    }

    private Icon obterIcone(String caminhoPng, int tamanho, IconeVetorial.Tipo tipoFallback, Color corFallback) {
        if (caminhoPng != null) {
            ImageIcon png = carregarERedimensionarIcone(caminhoPng, tamanho, tamanho);
            if (png != null) return png;
        }
        return new IconeVetorial(tipoFallback, corFallback, tamanho);
    }

    /** Ícone desenhado por código (Graphics2D), sem depender de nenhum arquivo PNG. */
    private static class IconeVetorial implements Icon {
        enum Tipo { GARAGEM, CATALOGO, PESSOA, MONTADORA, MODELO, PECA, SERVICO, DOCUMENTO, EDITAR, EXCLUIR }

        private final Tipo tipo;
        private final Color cor;
        private final int tam;

        IconeVetorial(Tipo tipo, Color cor, int tam) {
            this.tipo = tipo;
            this.cor = cor;
            this.tam = tam;
        }

        @Override public int getIconWidth()  { return tam; }
        @Override public int getIconHeight() { return tam; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(x, y);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(cor);
            g2.setStroke(new BasicStroke(Math.max(1.4f, tam / 11f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int s = tam;
            int pad = Math.max(2, s / 7);
            int area = s - pad * 2;

            switch (tipo) {
                case GARAGEM:
                    g2.drawPolyline(new int[]{pad, s / 2, s - pad}, new int[]{pad + area / 3, pad, pad + area / 3}, 3);
                    g2.drawRect(pad, pad + area / 3, area, area - area / 3);
                    break;
                case CATALOGO: {
                    int q = Math.max(3, area / 2 - 2);
                    g2.fillRoundRect(pad, pad, q, q, 3, 3);
                    g2.fillRoundRect(pad + q + 4, pad, q, q, 3, 3);
                    g2.fillRoundRect(pad, pad + q + 4, q, q, 3, 3);
                    g2.fillRoundRect(pad + q + 4, pad + q + 4, q, q, 3, 3);
                    break;
                }
                case PESSOA:
                    g2.drawOval(s / 2 - area / 5, pad, area * 2 / 5, area * 2 / 5);
                    g2.drawArc(pad, pad + area / 2, area, area / 2, 0, 180);
                    break;
                case MONTADORA:
                    g2.drawRect(pad, pad + area / 4, area, area - area / 4);
                    g2.drawRect(pad + area / 6, pad, area / 4, area / 4);
                    g2.fillRect(pad + area / 4, pad + area / 2, Math.max(2, area / 8), Math.max(2, area / 8));
                    g2.fillRect(pad + area * 5 / 8, pad + area / 2, Math.max(2, area / 8), Math.max(2, area / 8));
                    break;
                case MODELO:
                    g2.drawRoundRect(pad, pad + area / 3, area, area / 3, area / 6, area / 6);
                    g2.fillOval(pad + area / 8, pad + area * 2 / 3, Math.max(3, area / 5), Math.max(3, area / 5));
                    g2.fillOval(pad + area * 5 / 8, pad + area * 2 / 3, Math.max(3, area / 5), Math.max(3, area / 5));
                    break;
                case PECA: {
                    int[] xs = new int[6];
                    int[] ys = new int[6];
                    double raioHex = area / 2.0;
                    double cx = s / 2.0, cy = s / 2.0;
                    for (int i = 0; i < 6; i++) {
                        double ang = Math.toRadians(60 * i - 90);
                        xs[i] = (int) (cx + raioHex * Math.cos(ang));
                        ys[i] = (int) (cy + raioHex * Math.sin(ang));
                    }
                    g2.drawPolygon(xs, ys, 6);
                    g2.drawOval(s / 2 - area / 6, s / 2 - area / 6, Math.max(3, area / 3), Math.max(3, area / 3));
                    break;
                }
                case SERVICO:
                    g2.drawLine(pad + 2, s - pad - 2, s - pad - 2, pad + 2);
                    g2.drawOval(pad, s - pad - area / 4, Math.max(3, area / 4), Math.max(3, area / 4));
                    g2.drawOval(s - pad - area / 4, pad, Math.max(3, area / 4), Math.max(3, area / 4));
                    break;
                case DOCUMENTO:
                    g2.drawRoundRect(pad, pad, area, area, 4, 4);
                    for (int i = 1; i <= 3; i++) {
                        int ly = pad + (area * i) / 4;
                        g2.drawLine(pad + area / 5, ly, s - pad - area / 5, ly);
                    }
                    break;
                case EDITAR: {
                    g2.drawLine(pad, s - pad, s - pad, pad);
                    int[] xsPonta = { pad, pad + area / 5, pad };
                    int[] ysPonta = { s - pad, s - pad, s - pad - area / 5 };
                    g2.fillPolygon(xsPonta, ysPonta, 3);
                    g2.fillRect(s - pad - area / 6, pad, Math.max(3, area / 6), Math.max(3, area / 6));
                    break;
                }
                case EXCLUIR: {
                    int larguraCorpo = area * 2 / 3;
                    int xCorpo = pad + (area - larguraCorpo) / 2;
                    int yCorpo = pad + area / 4;
                    g2.drawRect(xCorpo, yCorpo, larguraCorpo, area - area / 4);
                    g2.drawLine(pad, yCorpo, s - pad, yCorpo);
                    g2.drawLine(s / 2 - area / 8, pad, s / 2 + area / 8, pad);
                    g2.drawLine(s / 2, yCorpo + 3, s / 2, s - pad - 3);
                    break;
                }
            }
            g2.dispose();
        }
    }
}
