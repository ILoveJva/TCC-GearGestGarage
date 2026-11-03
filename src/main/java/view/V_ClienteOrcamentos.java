package view;

import controller.OficinaController;
import model.Orcamento;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Lista os orçamentos do cliente logado (de qualquer oficina); permite aprovar/reprovar os pendentes. */
public class V_ClienteOrcamentos extends JPanel {

    private final OficinaController controller;
    private JPanel pnlLista;

    public V_ClienteOrcamentos(OficinaController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel titulo = new JLabel("Orçamentos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(titulo, BorderLayout.NORTH);

        pnlLista = new JPanel();
        pnlLista.setLayout(new BoxLayout(pnlLista, BoxLayout.Y_AXIS));
        pnlLista.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(pnlLista);
        scroll.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        atualizarLista();
    }

    private void atualizarLista() {
        pnlLista.removeAll();
        List<Orcamento> orcamentos = controller.listarMeusOrcamentos();

        if (orcamentos.isEmpty()) {
            JLabel vazio = new JLabel("Você ainda não tem orçamentos.");
            vazio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            pnlLista.add(vazio);
        } else {
            for (Orcamento o : orcamentos) {
                pnlLista.add(criarCartao(o));
                pnlLista.add(Box.createVerticalStrut(10));
            }
        }
        pnlLista.revalidate();
        pnlLista.repaint();
    }

    private JPanel criarCartao(Orcamento o) {
        JPanel card = new JPanel(new BorderLayout(10, 6));
        card.setBackground(Color.decode("#F7F7F7"));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#CCCCCC")),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        JLabel status = new JLabel("● " + traduzirStatus(o.getStatus()));
        status.setForeground(corStatus(o.getStatus()));
        status.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cabecalho.add(status, BorderLayout.WEST);

        JLabel codigoTipo = new JLabel(o.getCodigo() + " — " + o.getPlacaVeiculo()
            + " — Por: " + o.getNomeOficina());
        codigoTipo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cabecalho.add(codigoTipo, BorderLayout.EAST);

        card.add(cabecalho, BorderLayout.NORTH);

        String levarBuscar = (o.isLevar() ? "Levar " : "") + (o.isBuscar() ? "Buscar" : "");
        JLabel detalhes = new JLabel("<html>" + o.getReclamacao().replace("\n", "<br>")
            + "<br><b>Data:</b> " + (o.getDataCriacao() != null ? o.getDataCriacao() : "—")
            + " &nbsp;&nbsp; <b>Valor:</b> R$ " + String.format("%.2f", o.getValor())
            + (levarBuscar.isBlank() ? "" : " &nbsp;&nbsp; <b>" + levarBuscar + "</b>")
            + "</html>");
        detalhes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        card.add(detalhes, BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acoes.setOpaque(false);

        JButton btnExportar = new JButton("Exportar (Word)");
        btnExportar.addActionListener(e -> ExportadorOrcamento.exportar(this, controller, o));
        acoes.add(btnExportar);

        if ("PENDENTE".equalsIgnoreCase(o.getStatus())) {
            JButton btnReprovar = new JButton("Reprovar");
            btnReprovar.addActionListener(e -> {
                controller.reprovarOrcamento(o.getIdOrcamento());
                atualizarLista();
            });
            JButton btnAprovar = new JButton("Aprovar");
            btnAprovar.addActionListener(e -> {
                controller.aprovarOrcamento(o.getIdOrcamento());
                atualizarLista();
            });
            acoes.add(btnReprovar);
            acoes.add(btnAprovar);
        }
        card.add(acoes, BorderLayout.SOUTH);

        return card;
    }

    private String traduzirStatus(String status) {
        if (status == null) return "—";
        return switch (status.toUpperCase()) {
            case "PENDENTE" -> "Aguardando";
            case "APROVADO" -> "Aprovado";
            case "REPROVADO", "RECUSADO" -> "Reprovado";
            default -> status;
        };
    }

    private Color corStatus(String status) {
        if (status == null) return Color.GRAY;
        return switch (status.toUpperCase()) {
            case "PENDENTE" -> Color.decode("#E0A800");
            case "APROVADO" -> Color.decode("#2E8B57");
            case "REPROVADO", "RECUSADO" -> Color.decode("#C0392B");
            default -> Color.GRAY;
        };
    }
}
