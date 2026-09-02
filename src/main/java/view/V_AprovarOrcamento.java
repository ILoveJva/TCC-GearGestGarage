package view;

import controller.OficinaController;
import model.Orcamento;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * Lista de orçamentos com aprovação/reprovação e geração de O.S.
 * a partir de um orçamento aprovado.
 */
public class V_AprovarOrcamento extends JPanel {

    private final OficinaController controller;
    private JTable tabela;
    private DefaultTableModel modelo;
    private List<Orcamento> orcamentos;

    public V_AprovarOrcamento(OficinaController controller) {
        this.controller = controller;
        setBackground(Color.decode("#F5F5F5"));
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        construir();
        carregar();
    }

    private void construir() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        JLabel titulo = new JLabel("Página Inicial > Orçamentos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.decode("#4D4D4D"));
        JButton btnNovo = new JButton("+ Novo Orçamento");
        btnNovo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNovo.setBackground(Color.decode("#FF9900"));
        btnNovo.setForeground(Color.WHITE);
        btnNovo.setFocusPainted(false);
        btnNovo.setBorderPainted(false);
        btnNovo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ImageIcon icoOrc = carregarIcone("/assets/icons/vorcamentos.png", 20, 20);
        if (icoOrc != null) {
            btnNovo.setIcon(icoOrc);
            btnNovo.setHorizontalTextPosition(SwingConstants.RIGHT);
        }
        btnNovo.addActionListener(e -> navegar(new V_CadastrarOrcamento(controller)));
        topo.add(titulo, BorderLayout.WEST);
        topo.add(btnNovo, BorderLayout.EAST);
        add(topo, BorderLayout.NORTH);

        String[] cols = {"Cód.", "Cliente", "Veículo", "Responsável", "Valor (R$)", "Status"};
        modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(26);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader cabecalhoTabela = tabela.getTableHeader();
        cabecalhoTabela.setDefaultRenderer(new CabecalhoVidroClaro());
        cabecalhoTabela.setPreferredSize(new Dimension(cabecalhoTabela.getPreferredSize().width, 28));
        cabecalhoTabela.setReorderingAllowed(false);
        cabecalhoTabela.setOpaque(false);

        tabela.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Orcamento o = selecionado();
                    if (o != null) navegar(new V_VisualizarOrcamento(controller, o));
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#E0E0E0")));
        ScrollBarPadrao.aplicar(scroll);
        add(scroll, BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        acoes.setOpaque(false);
        JButton btnReprovar = criarBtn("✕ Reprovar", "#DC3545");
        JButton btnAprovar = criarBtn("✓ Aprovar", "#28A745");
        btnReprovar.addActionListener(e -> mudarStatus(false));
        btnAprovar.addActionListener(e -> mudarStatus(true));
        acoes.add(btnReprovar);
        acoes.add(btnAprovar);
        add(acoes, BorderLayout.SOUTH);
    }

    private void carregar() {
        orcamentos = controller.listarOrcamentos();
        modelo.setRowCount(0);

        for (Orcamento o : orcamentos) {
            modelo.addRow(new Object[]{
                    o.getCodigo().isEmpty() ? String.format("%04d", o.getIdOrcamento()) : o.getCodigo(),
                    controller.cliente_id(o.getIdCliente()).getNome(), o.getPlacaVeiculo(), o.getResponsavel(),
                    String.format("%.2f", o.getValor()), o.getStatus()
            });
        }
    }

    private Orcamento selecionado() {
        int linha = tabela.getSelectedRow();
        if (linha < 0 || orcamentos == null || linha >= orcamentos.size()) return null;
        return orcamentos.get(linha);
    }

    private void mudarStatus(boolean aprovar) {
        Orcamento o = selecionado();
        if (o == null) { aviso("Selecione um orçamento na tabela."); return; }
        if (!"PENDENTE".equalsIgnoreCase(o.getStatus())) {
            aviso("Este orçamento já foi " + o.getStatus().toLowerCase() + "."); return;
        }
        if (!confirmarComSenha(aprovar ? "aprovar este orçamento" : "reprovar este orçamento")) return;
        try {
            if (aprovar) controller.aprovarOrcamento(o.getIdOrcamento());
            else controller.reprovarOrcamento(o.getIdOrcamento());
            carregar();
        } catch (Exception ex) {
            DialogoAlerta.erro(this, "Erro: " + ex.getMessage(), "Erro no Sistema");
        }
    }

    private void navegar(JPanel destino) {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(destino);
    }

    private void aviso(String msg) {
        DialogoAlerta.aviso(this, msg, "Atenção");
    }

    /** Exige a senha de acesso do usuário logado antes de prosseguir com uma ação sensível. */
    private boolean confirmarComSenha(String acao) {
        String senha = DialogoConfirmacao.pedirSenha(this, "Confirme a senha para " + acao);
        if (senha == null) return false;
        if (senha.isEmpty() || !controller.confirmarSenha(senha)) {
            DialogoAlerta.erro(this, "Senha incorreta.", "Acesso Negado");
            return false;
        }
        return true;
    }

    private BotaoVidro criarBtn(String texto, String cor) {
        BotaoVidro b = new BotaoVidro(texto, Color.decode(cor));
        b.setPreferredSize(new Dimension(140, 38));
        return b;
    }

    private ImageIcon carregarIcone(String caminho, int w, int h) {
        java.net.URL url = getClass().getResource(caminho);
        if (url == null) {
            String s = caminho.startsWith("/") ? caminho.substring(1) : caminho;
            url = Thread.currentThread().getContextClassLoader().getResource(s);
        }
        if (url != null) {
            Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }

    /**
     * Cabeçalho de coluna com vidro cinza claro no estilo Aero (Windows 7),
     * fino em altura — mesma linguagem visual usada nas demais telas.
     */
    private static class CabecalhoVidroClaro extends JLabel implements TableCellRenderer {
        private static final Color TOPO_A  = Color.decode("#FBFBFC");
        private static final Color TOPO_B  = Color.decode("#ECEEF1");
        private static final Color BASE_A  = Color.decode("#DADDE2");
        private static final Color BASE_B  = Color.decode("#EFF1F3");
        private static final Color BORDA   = Color.decode("#B6BCC4");
        private static final Color SEPARA  = Color.decode("#CCD1D8");
        private static final Color TEXTO   = Color.decode("#3A4149");

        CabecalhoVidroClaro() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(TEXTO);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
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

            g2.setPaint(new GradientPaint(0, 0, TOPO_A, 0, meio, TOPO_B));
            g2.fillRect(0, 0, w, meio);

            g2.setPaint(new GradientPaint(0, meio, BASE_A, 0, h, BASE_B));
            g2.fillRect(0, meio, w, h - meio);

            g2.setColor(new Color(255, 255, 255, 140));
            g2.fillRect(0, 0, w, Math.max(1, h / 5));

            g2.setColor(SEPARA);
            g2.drawLine(w - 1, 3, w - 1, h - 4);

            g2.setColor(BORDA);
            g2.drawLine(0, h - 1, w, h - 1);

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
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(Color.WHITE);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
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