package view.design;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class Tabela extends JPanel {

    private static final Color COR_TABELA_FUNDO   = Color.WHITE;
    private static final Color COR_TEXTO_CAMPO    = Color.decode("#2B2E33");
    private static final Color COR_TABELA_SELECAO = Color.decode("#FFE4BF");
    private static final Color COR_GRID           = Color.decode("#E8E8E8");

    public JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane scrollPane;

    public enum TipoVisual {
        SIMPLES,       // Listagem padrão com linhas e colunas discretas
        AGRUPADA,      // Com setas de expansão / hierarquia
        FINANCEIRA     // Com colunas numéricas e alinhamentos customizados
    }

    public Tabela(String[] colunas, TipoVisual tipoVisual, int alturaLinha) {
        setLayout(new BorderLayout());
        setOpaque(false);

        modelo = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(modelo);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.setBackground(COR_TABELA_FUNDO);
        tabela.setForeground(COR_TEXTO_CAMPO);
        tabela.setOpaque(true);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setSelectionBackground(COR_TABELA_SELECAO);
        tabela.setSelectionForeground(COR_TEXTO_CAMPO);
        tabela.setRowHeight(alturaLinha > 0 ? alturaLinha : 24);
        tabela.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Design exato extraído de V_VisualizarCatalogoServicos
        tabela.setShowGrid(false);
        tabela.setShowHorizontalLines(true);
        tabela.setShowVerticalLines(true);
        tabela.setGridColor(COR_GRID);
        tabela.setIntercellSpacing(new Dimension(0, 5));
        tabela.setFillsViewportHeight(true);

        JTableHeader cabecalho = tabela.getTableHeader();
        cabecalho.setFont(new Font("Segoe UI", Font.BOLD, 11));
        cabecalho.setReorderingAllowed(false);
        cabecalho.setOpaque(true);

        // Cabeçalho centralizado por padrão igual ao design de referência
        DefaultTableCellRenderer cabecalhoRenderer = (DefaultTableCellRenderer) cabecalho.getDefaultRenderer();
        cabecalhoRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Renderer padrão das células
        tabela.setDefaultRenderer(Object.class, new CelulaPadraoRenderer());

        scrollPane = new JScrollPane(tabela);
        scrollPane.getViewport().setBackground(COR_TABELA_FUNDO);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(COR_GRID));
        ScrollBar.aplicar(scrollPane);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void adicionarLinha(Object[] dados) { modelo.addRow(dados); }
    public void limpar() { modelo.setRowCount(0); }
    public void setLarguraColuna(int col, int largura) { tabela.getColumnModel().getColumn(col).setPreferredWidth(largura); }
    public void setMaxWidthColuna(int col, int largura) { tabela.getColumnModel().getColumn(col).setMaxWidth(largura); }
    public void setMinWidthColuna(int col, int largura) { tabela.getColumnModel().getColumn(col).setMinWidth(largura); }
    public void setRendererColuna(int col, TableCellRenderer renderer) { tabela.getColumnModel().getColumn(col).setCellRenderer(renderer); }

    public void aoClicar(Consumer<MouseEvent> acao) {
        tabela.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { acao.accept(e); }
        });
    }

    public int getLinhaSelecionada() { return tabela.getSelectedRow(); }
    public Object getValor(int linha, int coluna) { return tabela.getValueAt(linha, coluna); }

    private static class CelulaPadraoRenderer extends DefaultTableCellRenderer {
        CelulaPadraoRenderer() {
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        }
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            setBackground(sel ? COR_TABELA_SELECAO : COR_TABELA_FUNDO);
            setForeground(COR_TEXTO_CAMPO);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            return this;
        }
    }
}