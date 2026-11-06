package view;

import controller.OficinaController;
import model.Oficina;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

/**
 * "Buscar oficinas" (protótipo do Figma): campo de busca sublinhado com lupa, o primeiro
 * resultado em destaque (com "Solicitar Revisão") e "Outros resultados" abaixo — no
 * desktop em duas colunas. Busca por nome ou endereço em todas as oficinas do sistema.
 */
public class V_ClienteBuscarOficinas extends JPanel {

    private final OficinaController controller;
    private JTextField txtBusca;
    private JPanel pnlResultados;

    public V_ClienteBuscarOficinas(OficinaController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBackground(EstiloCliente.FUNDO);

        JPanel conteudo = new JPanel(new BorderLayout(0, 24));
        conteudo.setOpaque(false);
        conteudo.add(construirBusca(), BorderLayout.NORTH);

        pnlResultados = new JPanel();
        pnlResultados.setLayout(new BoxLayout(pnlResultados, BoxLayout.Y_AXIS));
        pnlResultados.setOpaque(false);
        conteudo.add(pnlResultados, BorderLayout.CENTER);

        add(EstiloCliente.pagina("Buscar oficinas",
                () -> EstiloCliente.navegar(this, new V_ClienteMenu(controller)), conteudo));
        atualizarResultados();
    }

    private JComponent construirBusca() {
        txtBusca = new JTextField();
        txtBusca.setFont(EstiloCliente.fonte(Font.PLAIN, 18));
        txtBusca.setOpaque(false);
        txtBusca.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, EstiloCliente.LINHA),
                BorderFactory.createEmptyBorder(4, 2, 4, 2)));
        txtBusca.setToolTipText("Digite o nome ou o endereço da oficina e tecle Enter");
        txtBusca.addActionListener(e -> atualizarResultados());
        // Busca enquanto digita.
        txtBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { atualizarResultados(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { atualizarResultados(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { }
        });

        JLabel lupa = new JLabel(EstiloCliente.iconeLupa(22));
        lupa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lupa.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { atualizarResultados(); }
        });

        JPanel barra = new JPanel(new BorderLayout(10, 0));
        barra.setOpaque(false);
        barra.add(txtBusca, BorderLayout.CENTER);
        barra.add(lupa, BorderLayout.EAST);
        barra.setPreferredSize(new Dimension(620, 40));

        JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centro.setOpaque(false);
        centro.add(barra);
        return centro;
    }

    private void atualizarResultados() {
        pnlResultados.removeAll();
        List<Oficina> oficinas = controller.buscarOficinas(txtBusca.getText());

        if (oficinas.isEmpty()) {
            JLabel vazio = new JLabel("Nenhuma oficina encontrada.", SwingConstants.CENTER);
            vazio.setFont(EstiloCliente.fonte(Font.ITALIC, 14));
            vazio.setForeground(EstiloCliente.TEXTO_SUAVE);
            vazio.setAlignmentX(Component.LEFT_ALIGNMENT);
            pnlResultados.add(vazio);
        } else {
            JComponent destaque = cartao(oficinas.get(0), true);
            destaque.setAlignmentX(Component.LEFT_ALIGNMENT);
            pnlResultados.add(destaque);

            if (oficinas.size() > 1) {
                pnlResultados.add(Box.createVerticalStrut(22));
                JLabel outros = new JLabel("Outros resultados");
                outros.setFont(EstiloCliente.fonte(Font.PLAIN, 19));
                outros.setAlignmentX(Component.LEFT_ALIGNMENT);
                pnlResultados.add(outros);
                pnlResultados.add(Box.createVerticalStrut(10));

                JPanel grade = new JPanel(new GridLayout(0, 2, 16, 16));
                grade.setOpaque(false);
                for (Oficina o : oficinas.subList(1, oficinas.size())) grade.add(cartao(o, false));
                grade.setAlignmentX(Component.LEFT_ALIGNMENT);
                pnlResultados.add(grade);
            }
        }
        pnlResultados.revalidate();
        pnlResultados.repaint();
    }

    /** Cartão branco de borda fina arredondada. O destaque é maior e tem avatar da oficina. */
    private JComponent cartao(Oficina o, boolean destaque) {
        JPanel card = new JPanel(new BorderLayout(16, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D r = new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1.5, getHeight() - 1.5, 26, 26);
                g2.setColor(Color.WHITE);
                g2.fill(r);
                g2.setColor(Color.decode("#8A8A8A"));
                g2.draw(r);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(destaque ? 16 : 12, 18, destaque ? 14 : 12, 18));

        StringBuilder html = new StringBuilder("<html><span style='font-size:")
                .append(destaque ? 17 : 15).append("px'>").append(EstiloCliente.escapar(o.getNome())).append("</span><br>");
        if (o.getDescricao() != null && !o.getDescricao().isBlank())
            html.append(EstiloCliente.escapar(o.getDescricao())).append(" - ");
        html.append(EstiloCliente.escapar(valor(o.getEndereco()))).append("<br>")
            .append("<span style='font-size:10px'>").append(EstiloCliente.escapar(valor(o.getTelefone())))
            .append("</span></html>");
        JLabel info = new JLabel(html.toString());
        info.setFont(EstiloCliente.fonte(Font.PLAIN, 12));
        info.setForeground(EstiloCliente.TEXTO);
        card.add(info, BorderLayout.CENTER);

        JButton solicitar = EstiloCliente.botao("Solicitar Revisão", EstiloCliente.TILE, EstiloCliente.TEXTO, destaque ? 14 : 12);
        solicitar.addActionListener(e -> EstiloCliente.navegar(this, new V_ClienteSolicitarRevisao(controller, o)));

        if (destaque) {
            card.add(new JLabel(EstiloCliente.avatar(null, o.getNome(), 64)), BorderLayout.EAST);
            JPanel rodape = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            rodape.setOpaque(false);
            solicitar.setPreferredSize(new Dimension(220, 32));
            rodape.add(solicitar);
            card.add(rodape, BorderLayout.SOUTH);
        } else {
            JPanel lado = new JPanel(new GridBagLayout());
            lado.setOpaque(false);
            lado.add(solicitar);
            card.add(lado, BorderLayout.EAST);
        }
        return card;
    }

    private static String valor(String s) { return s == null || s.isBlank() ? "—" : s; }
}
