package view;

import controller.OficinaController;
import model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

/**
 * Painel de Conteúdo para a Listagem de Clientes.
 * Segue o padrão de design modular, utilizando o Pnl_SelecaoCliente no centro.
 */
public class V_VisualizarClientes extends JPanel {

    // =========================================================================
    // PALETA E MEDIDAS — mexa só aqui para alterar tudo de uma vez
    // =========================================================================
    private static final Color COR_FUNDO_PAGINA = Color.decode("#FAFBFC");
    private static final Color COR_CARD_TOPO    = Color.decode("#EFF1F4");
    private static final Color COR_CARD_BASE    = Color.decode("#DFE4EA");
    private static final Color COR_TITULO       = Color.decode("#4A5568");
    private static final Color COR_TEXTO_CAMPO  = Color.decode("#2B2E33");

    // Vidro cinza claro estilo Windows 7 (Aero) — cabeçalho da tabela
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

    // Ação principal (Cadastrar) — laranja, tema original preservado
    private static final Color COR_ACAO         = Color.decode("#FF9900");
    private static final Color COR_ACAO_CLARA   = Color.decode("#FFAD33");
    private static final Color COR_ACAO_ESCURA  = Color.decode("#E68A00");

    // Ação secundária (Editar) — cinza, tema original preservado
    private static final Color COR_EDITAR         = Color.decode("#6C757D");
    private static final Color COR_EDITAR_CLARA    = Color.decode("#7C848C");
    private static final Color COR_EDITAR_ESCURA   = Color.decode("#5A6169");

    // Ação destrutiva (Excluir) — vermelho, tema original preservado
    private static final Color COR_EXCLUIR         = Color.decode("#DC3545");
    private static final Color COR_EXCLUIR_CLARA   = Color.decode("#E4606D");
    private static final Color COR_EXCLUIR_ESCURA  = Color.decode("#BD2130");

    private static final int RAIO_COMPONENTE     = 12;
    private static final int TAMANHO_FONTE_BOTAO = 12;
    private static final int ALTURA_BOTAO        = 34;
    private static final int TAMANHO_ICONE_BOTAO = 16;
    private static final int ALTURA_CABECALHO    = 30;
    private static final int ALTURA_LINHA_TABELA = 26;

    // ==========================================
    // DECLARAÇÃO DOS COMPONENTES (Padrão Prefixo)
    // ==========================================
    private JPanel pnl_CardCentral;
    private JLabel lbl_MapaNavegacao; // "Mapa" de onde o usuário se encontra
    private BotaoAcao btn_CadastrarCliente;
    private BotaoAcao btn_EditarCliente;
    private BotaoAcao btn_ExcluirCliente;

    // Painel Modular da Tabela de Clientes
    private Pnl_SelecaoCliente pnl_SelecaoCliente;

    // Referência do Controller
    private OficinaController controller;

    /**
     * Construtor recebe o Controller unificado do sistema.
     */
    public V_VisualizarClientes(OficinaController controller) {
        this.controller = controller;

        // Alinhamento centralizado idêntico ao painel de veículos e cadastros
        setLayout(new GridBagLayout());
        setBackground(COR_FUNDO_PAGINA);

        initComponents();
        layoutComponents();
        carregarDadosClientes();
        vincularAcoes();
        estilizarTabelaClientes();
    }

    private void initComponents() {
        // Card Central de Contenção (Mesmas proporções das telas de cadastro e veículos)
        pnl_CardCentral = new PainelGradiente(new BorderLayout(0, 15), COR_CARD_TOPO, COR_CARD_BASE);
        pnl_CardCentral.setPreferredSize(new Dimension(680, 560));
        pnl_CardCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // "Mapa" de Navegação Superior (Substituindo o antigo menu suspenso)
        lbl_MapaNavegacao = new JLabel("Página Inicial > Consultar Clientes");
        lbl_MapaNavegacao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_MapaNavegacao.setForeground(COR_TITULO);

        // Instanciação do Painel Modular da Tabela
        pnl_SelecaoCliente = new Pnl_SelecaoCliente();

        btn_CadastrarCliente = new BotaoAcao("+ Cadastrar Cliente",
                new IconeAdicionarUsuario(TAMANHO_ICONE_BOTAO, Color.WHITE),
                COR_ACAO, COR_ACAO_CLARA, COR_ACAO_ESCURA);
        btn_CadastrarCliente.setPreferredSize(new Dimension(190, ALTURA_BOTAO));

        btn_EditarCliente = new BotaoAcao("Editar", COR_EDITAR, COR_EDITAR_CLARA, COR_EDITAR_ESCURA);
        btn_EditarCliente.setPreferredSize(new Dimension(90, ALTURA_BOTAO));

        btn_ExcluirCliente = new BotaoAcao("Excluir", COR_EXCLUIR, COR_EXCLUIR_CLARA, COR_EXCLUIR_ESCURA);
        btn_ExcluirCliente.setPreferredSize(new Dimension(90, ALTURA_BOTAO));
    }

    private void layoutComponents() {
        // Header com mapa de navegação e botão de cadastro
        JPanel pnl_HeaderCard = new JPanel(new BorderLayout());
        pnl_HeaderCard.setOpaque(false);
        pnl_HeaderCard.add(lbl_MapaNavegacao, BorderLayout.WEST);

        JPanel pnl_BotoesHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnl_BotoesHeader.setOpaque(false);
        pnl_BotoesHeader.add(btn_EditarCliente);
        pnl_BotoesHeader.add(btn_ExcluirCliente);
        pnl_BotoesHeader.add(btn_CadastrarCliente);
        pnl_HeaderCard.add(pnl_BotoesHeader, BorderLayout.EAST);

        pnl_CardCentral.add(pnl_HeaderCard, BorderLayout.NORTH);

        // Injeta o painel modular da tabela ocupando o resto do espaço útil
        pnl_CardCentral.add(pnl_SelecaoCliente, BorderLayout.CENTER);

        // Injeta o card central usando o GridBagLayout para manter centralizado na tela
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(pnl_CardCentral, gbc);
    }

    /**
     * Carrega a tabela varrendo os clientes reais obtidos do Controller.
     */
    private void carregarDadosClientes() {
        if (controller == null) return;
        ArrayList<Cliente> todosClientes = controller.listarClientes();
        // Repassa a lista de clientes para a regra de negócio interna do painel modular
        pnl_SelecaoCliente.atualizarTabela(todosClientes);
    }

    /**
     * Aplica o visual da tabela sem tocar no Pnl_SelecaoCliente: cabeçalho em
     * vidro azulado... na verdade cinza claro (estilo Aero do Windows 7) e
     * células de dados com fundo branco — igual às demais telas de listagem.
     */
    private void estilizarTabelaClientes() {
        JTable tabela = pnl_SelecaoCliente.getTbl_Clientes();
        if (tabela == null) return;

        tabela.setBackground(COR_TABELA_FUNDO);
        tabela.setForeground(COR_TEXTO_CAMPO);
        tabela.setOpaque(true);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(ALTURA_LINHA_TABELA);
        tabela.setShowGrid(true);
        tabela.setGridColor(COR_TABELA_GRADE);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionBackground(COR_TABELA_SELECAO);
        tabela.setSelectionForeground(COR_TEXTO_CAMPO);
        tabela.setFillsViewportHeight(true);
        tabela.setDefaultRenderer(Object.class, new CelulaBrancaRenderer());

        JTableHeader cabecalho = tabela.getTableHeader();
        if (cabecalho != null) {
            cabecalho.setDefaultRenderer(new CabecalhoVidroClaro());
            cabecalho.setPreferredSize(new Dimension(cabecalho.getPreferredSize().width, ALTURA_CABECALHO));
            cabecalho.setReorderingAllowed(false);
            cabecalho.setOpaque(false);
        }

        JScrollPane scroll = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, tabela);
        if (scroll != null) {
            scroll.getViewport().setBackground(COR_TABELA_FUNDO);
            scroll.getViewport().setOpaque(true);
            scroll.setOpaque(false);
            scroll.setBorder(BorderFactory.createLineBorder(COR_AERO_BORDA));
        }
    }

    /**
     * Duplo-clique em um cliente abre a página de perfil correspondente.
     */
    private void vincularAcoes() {
        btn_CadastrarCliente.addActionListener(e -> {
            Window w = SwingUtilities.getWindowAncestor(V_VisualizarClientes.this);
            if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(new V_CadastrarCliente(controller));
        });

        btn_EditarCliente.addActionListener(e -> {
            Cliente alvo = obterClienteSelecionado();
            if (alvo == null) {
                DialogoAlerta.aviso(this, "Selecione um cliente na lista para editar.", "Nenhum cliente selecionado");
                return;
            }
            Window w = SwingUtilities.getWindowAncestor(V_VisualizarClientes.this);
            if (w instanceof V_Main) ((V_Main) w).atualizarConteudo(new V_EditarCliente(controller, alvo));
        });

        btn_ExcluirCliente.addActionListener(e -> {
            Cliente alvo = obterClienteSelecionado();
            if (alvo == null) {
                DialogoAlerta.aviso(this, "Selecione um cliente na lista para excluir.", "Nenhum cliente selecionado");
                return;
            }
            boolean confirmado = DialogoConfirmacao.confirmar(this,
                    "Tem certeza que deseja excluir o cliente \"" + alvo.getNome() + "\"?",
                    "Confirmar exclusão");
            if (!confirmado) return;
            try {
                controller.excluirCliente(alvo.getIdUsuario());
                DialogoAlerta.sucesso(this, "Cliente excluído com sucesso!", "Sucesso");
                carregarDadosClientes();
            } catch (Exception ex) {
                DialogoAlerta.erro(this, "Erro ao excluir cliente: " + ex.getMessage(), "Erro no Sistema");
            }
        });

        JTable tabela = pnl_SelecaoCliente.getTbl_Clientes();
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) return;
                Cliente alvo = obterClienteSelecionado();
                if (alvo == null) return;

                Window w = SwingUtilities.getWindowAncestor(V_VisualizarClientes.this);
                if (w instanceof V_Main) {
                    ((V_Main) w).atualizarConteudo(new V_PerfilCliente(controller, alvo));
                }
            }
        });
    }

    /** Resolve o Cliente correspondente à linha atualmente selecionada na tabela. */
    private Cliente obterClienteSelecionado() {
        JTable tabela = pnl_SelecaoCliente.getTbl_Clientes();
        int linha = tabela.getSelectedRow();
        if (linha < 0) return null;

        Object codCel = tabela.getValueAt(linha, 0); // "00005"
        long idCliente;
        try {
            idCliente = Long.parseLong(String.valueOf(codCel).trim());
        } catch (NumberFormatException ex) {
            return null;
        }

        for (Cliente c : controller.listarClientes()) {
            if (c.getIdUsuario() == idCliente) return c;
        }
        return null;
    }

    // Exposição da tabela e do painel para listeners ou manipulações externas
    public Pnl_SelecaoCliente getPnl_SelecaoCliente() {
        return pnl_SelecaoCliente;
    }

    public JTable getTbl_Clientes() {
        return pnl_SelecaoCliente.getTbl_Clientes();
    }

    // =========================================================================
    // INNER CLASSES — RENDERERS DA TABELA
    // =========================================================================

    /** Cabeçalho de coluna com vidro cinza claro no estilo Aero (Windows 7). */
    private static class CabecalhoVidroClaro extends JLabel implements TableCellRenderer {

        CabecalhoVidroClaro() {
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(COR_AERO_TEXTO);
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

    /** Células de dados sempre com fundo branco (e destaque no tom do tema quando selecionadas). */
    private static class CelulaBrancaRenderer extends DefaultTableCellRenderer {
        CelulaBrancaRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(isSelected ? COR_TABELA_SELECAO : COR_TABELA_FUNDO);
            setForeground(COR_TEXTO_CAMPO);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            return this;
        }
    }

    // =========================================================================
    // INNER CLASSES — COMPONENTES EM VIDRO
    // =========================================================================

    /** Painel com fundo em gradiente suave, harmonizando o cartão com o restante das telas. */
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
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Botão de ação com a mesma linguagem visual usada em todo o sistema:
     * cantos arredondados, sombra suave, reflexo no topo e reação a
     * hover/clique. A cor é configurável (laranja/cinza/vermelho aqui).
     */
    private static class BotaoAcao extends JButton {
        private final Color corBase;
        private final Color corClara;
        private final Color corEscura;
        private boolean sobreMouse = false;
        private boolean pressionado = false;

        BotaoAcao(String texto, Color corBase, Color corClara, Color corEscura) {
            this(texto, null, corBase, corClara, corEscura);
        }

        BotaoAcao(String texto, Icon icone, Color corBase, Color corClara, Color corEscura) {
            super(texto, icone);
            this.corBase = corBase;
            this.corClara = corClara;
            this.corEscura = corEscura;
            setFont(new Font("Segoe UI", Font.BOLD, TAMANHO_FONTE_BOTAO));
            setForeground(Color.WHITE);
            setIconTextGap(8);
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

            g2.setColor(new Color(255, 255, 255, 45));
            g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, Math.max(0, (h - 4) * 0.4), RAIO_COMPONENTE - 5, RAIO_COMPONENTE - 5));

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Ícone vetorial (pessoa + sinal de "+") desenhado diretamente com Java2D,
     * sem depender de arquivo externo — escala perfeitamente para qualquer
     * tamanho e não falha caso o asset não exista no classpath (como podia
     * acontecer com o ícone carregado de "/assets/icons/add_cliente.png").
     */
    private static class IconeAdicionarUsuario implements Icon {
        private final int tamanho;
        private final Color cor;

        IconeAdicionarUsuario(int tamanho, Color cor) {
            this.tamanho = tamanho;
            this.cor = cor;
        }

        @Override public int getIconWidth()  { return tamanho; }
        @Override public int getIconHeight() { return tamanho; }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            double escala = tamanho / 24.0;
            g2.scale(escala, escala);
            g2.setColor(cor);
            g2.setStroke(new BasicStroke(2.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Cabeça
            g2.draw(new Ellipse2D.Double(6.6, 0.9, 11.2, 11.2));

            // Corpo, aberto do lado direito (onde entra o "+")
            Path2D corpo = new Path2D.Double();
            corpo.moveTo(15.4, 12.3);
            corpo.curveTo(17.6, 13.0, 19.2, 13.9, 19.2, 13.9);
            corpo.moveTo(12.2, 12.1);
            corpo.curveTo(5.6, 12.3, 1.0, 17.6, 1.0, 21.6);
            corpo.curveTo(1.0, 22.6, 1.8, 23.2, 2.8, 23.2);
            corpo.lineTo(15.5, 23.2);
            g2.draw(corpo);

            // Sinal de "+"
            g2.draw(new Line2D.Double(19.6, 15.6, 19.6, 22.6));
            g2.draw(new Line2D.Double(16.1, 19.1, 23.1, 19.1));

            g2.dispose();
        }
    }
}
