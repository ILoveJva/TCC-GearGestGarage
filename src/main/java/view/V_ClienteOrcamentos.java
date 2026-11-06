package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
import controller.OficinaController;
import model.Orcamento;
import model.Veiculo;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * "Orçamentos" (protótipo do Figma): um cartão sanfona por orçamento, com bolinha de
 * status (amarelo = aguardando, verde = aprovado, vermelho = reprovado), serviço,
 * veículo e oficina. Expandido mostra serviços, peças, subtotais e — se pendente —
 * os botões Reprovar / Aprovar. Lista orçamentos de qualquer oficina.
 */
public class V_ClienteOrcamentos extends JPanel {

    private final OficinaController controller;
    private final Map<Long, Veiculo> veiculos = new HashMap<>();
    private final Set<Long> abertos = new HashSet<>();
    private JPanel pnlLista;

    public V_ClienteOrcamentos(OficinaController controller) {
        this.controller = controller;
        for (Veiculo v : controller.listarMeusVeiculos()) veiculos.put(v.getIdVeiculo(), v);

        setLayout(new BorderLayout());
        setBackground(EstiloCliente.FUNDO);

        pnlLista = new JPanel();
        pnlLista.setLayout(new BoxLayout(pnlLista, BoxLayout.Y_AXIS));
        pnlLista.setOpaque(false);

        add(EstiloCliente.pagina("Orçamentos",
                () -> EstiloCliente.navegar(this, new V_ClienteMenu(controller)), pnlLista));
        atualizarLista();
    }

    private void atualizarLista() {
        pnlLista.removeAll();
        List<Orcamento> orcamentos = controller.listarMeusOrcamentos();
        if (orcamentos.isEmpty()) {
            JLabel vazio = new JLabel("Você ainda não tem orçamentos. Use \"Buscar oficinas\" para solicitar uma revisão.",
                    SwingConstants.CENTER);
            vazio.setFont(EstiloCliente.fonte(Font.ITALIC, 14));
            vazio.setForeground(EstiloCliente.TEXTO_SUAVE);
            vazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            pnlLista.add(vazio);
        } else {
            // Pendentes primeiro (precisam de ação); depois os mais recentes.
            List<Orcamento> ordenados = new ArrayList<>(orcamentos);
            ordenados.sort(Comparator.comparing((Orcamento o) -> !"PENDENTE".equalsIgnoreCase(o.getStatus())));
            for (Orcamento o : ordenados) {
                boolean abrir = abertos.contains(o.getIdOrcamento());
                EstiloCliente.CartaoExpansivel card = new EstiloCliente.CartaoExpansivel(
                        EstiloCliente.bolinha(corStatus(o.getStatus()), false),
                        titulo(o) + ":", EstiloCliente.nomeVeiculo(veiculos.get(o.getIdVeiculo()))
                            + " · " + o.getPlacaVeiculo(),
                        valor(o.getNomeOficina()), construirCorpo(o), abrir) {
                    @Override void setAberto(boolean a) {
                        super.setAberto(a);
                        if (a) abertos.add(o.getIdOrcamento()); else abertos.remove(o.getIdOrcamento());
                    }
                };
                card.setAlignmentX(Component.LEFT_ALIGNMENT);
                pnlLista.add(card);
                pnlLista.add(Box.createVerticalStrut(12));
            }
        }
        pnlLista.revalidate();
        pnlLista.repaint();
    }

    /** "Troca de óleo e filtro" (+ N) a partir dos serviços; se não houver, o motivo da solicitação. */
    private String titulo(Orcamento o) {
        List<CatalogoServicoEntity> itens;
        try { itens = controller.listarItensDoOrcamento(o.getIdOrcamento()); }
        catch (Exception e) { itens = List.of(); }
        if (!itens.isEmpty())
            return itens.get(0).getNome() + (itens.size() > 1 ? " (+" + (itens.size() - 1) + ")" : "");
        String motivo = o.getReclamacao() == null ? "" : o.getReclamacao().split("\\R", 2)[0].trim();
        if (!motivo.isEmpty()) return motivo.length() > 45 ? motivo.substring(0, 44) + "…" : motivo;
        return "Orçamento " + (o.getCodigo().isBlank() ? "#" + o.getIdOrcamento() : o.getCodigo());
    }

    // ------------------------------------------------------------------ área expandida

    private JComponent construirCorpo(Orcamento o) {
        LinkedHashMap<CatalogoServicoEntity, Double> servicos;
        try { servicos = controller.listarItensOrcamentoComValor(o.getIdOrcamento()); }
        catch (Exception e) { servicos = new LinkedHashMap<>(); }
        List<RtfUtil.PecaAgrupada> pecas;
        try { pecas = RtfUtil.agruparPecas(controller.listarPecasOrcamentoComValor(o.getIdOrcamento())); }
        catch (Exception e) { pecas = List.of(); }

        double totalServicos = servicos.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalPecas = pecas.stream().mapToDouble(p -> p.valorUnit() * p.quantidade()).sum();

        JPanel corpo = new JPanel(new BorderLayout(24, 12));
        corpo.setBorder(BorderFactory.createEmptyBorder(14, 22, 16, 22));

        // Coluna esquerda: detalhes (motivo, serviços, peças).
        StringBuilder html = new StringBuilder("<html><div style='text-align:center'>");
        html.append("<b>Detalhes:</b><br>");
        String motivo = o.getReclamacao() == null ? "" : o.getReclamacao().trim();
        if (!motivo.isEmpty())
            html.append("<i>").append(EstiloCliente.escapar(motivo).replace("\n", "<br>")).append("</i><br>");
        html.append("<br><b>Serviço(s):</b><br>");
        if (servicos.isEmpty()) html.append("Ainda não definidos pela oficina.<br>");
        for (var e : servicos.entrySet())
            html.append(EstiloCliente.escapar(e.getKey().getNome())).append(" — ")
                .append(EstiloCliente.reais(e.getValue())).append("<br>");
        if (!pecas.isEmpty()) {
            html.append("<br><b>Iten(s) utilizado(s):</b><br>");
            for (RtfUtil.PecaAgrupada p : pecas) {
                String nome = p.nome() + (p.fabricante().equals("—") ? "" : " " + p.fabricante());
                html.append(EstiloCliente.escapar(nome))
                    .append(p.quantidade() > 1 ? " (" + p.quantidade() + "x)" : "").append(" — ")
                    .append(EstiloCliente.reais(p.valorUnit() * p.quantidade())).append("<br>");
            }
        }
        html.append("</div></html>");
        JLabel detalhes = new JLabel(html.toString(), SwingConstants.CENTER);
        detalhes.setFont(EstiloCliente.fonte(Font.PLAIN, 13));
        detalhes.setForeground(EstiloCliente.TEXTO);
        detalhes.setVerticalAlignment(SwingConstants.TOP);
        corpo.add(detalhes, BorderLayout.CENTER);

        // Coluna direita: status, caixa de subtotais e data.
        JPanel direita = new JPanel();
        direita.setLayout(new BoxLayout(direita, BoxLayout.Y_AXIS));
        direita.setOpaque(false);

        JLabel status = new JLabel("Status: " + traduzirStatus(o.getStatus()));
        status.setFont(EstiloCliente.fonte(Font.PLAIN, 13));
        status.setAlignmentX(Component.CENTER_ALIGNMENT);
        direita.add(status);
        direita.add(Box.createVerticalStrut(10));

        EstiloCliente.PainelArredondado caixa = new EstiloCliente.PainelArredondado(
                new BorderLayout(), 14, EstiloCliente.CAIXA_CLARA);
        caixa.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        JLabel totais = new JLabel("<html><div style='text-align:center'>Sub-Total:<br>"
                + "Serviços: " + EstiloCliente.reais(totalServicos) + "<br>"
                + "Peças: " + EstiloCliente.reais(totalPecas) + "<br>"
                + "<b>Total:<br>" + EstiloCliente.reais(totalServicos + totalPecas) + "</b></div></html>",
                SwingConstants.CENTER);
        totais.setFont(EstiloCliente.fonte(Font.PLAIN, 13));
        caixa.add(totais);
        caixa.setAlignmentX(Component.CENTER_ALIGNMENT);
        caixa.setMaximumSize(new Dimension(220, caixa.getPreferredSize().height));
        direita.add(caixa);
        direita.add(Box.createVerticalStrut(12));

        JLabel data = new JLabel("<html><div style='text-align:center'>Solicitado em:<br>"
                + EstiloCliente.dataBR(o.getDataCriacao()) + "</div></html>", SwingConstants.CENTER);
        data.setFont(EstiloCliente.fonte(Font.PLAIN, 13));
        data.setAlignmentX(Component.CENTER_ALIGNMENT);
        direita.add(data);
        direita.setPreferredSize(new Dimension(240, direita.getPreferredSize().height));
        corpo.add(direita, BorderLayout.EAST);

        // Rodapé: Reprovar / Aprovar (pendentes) e exportar.
        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.setOpaque(false);
        JPanel botoes = new JPanel(new GridLayout(1, 0, 24, 0));
        botoes.setOpaque(false);
        if ("PENDENTE".equalsIgnoreCase(o.getStatus())) {
            JButton reprovar = EstiloCliente.botao("Reprovar", Color.decode("#D98C8C"), EstiloCliente.TEXTO, 14);
            reprovar.addActionListener(e -> {
                if (DialogoConfirmacao.confirmar(this, "Reprovar este orçamento?", "Reprovar orçamento")) {
                    controller.reprovarOrcamento(o.getIdOrcamento());
                    atualizarLista();
                }
            });
            JButton aprovar = EstiloCliente.botao("Aprovar", Color.decode("#99D699"), EstiloCliente.TEXTO, 14);
            aprovar.addActionListener(e -> {
                if (DialogoConfirmacao.confirmar(this, "Aprovar este orçamento de "
                        + EstiloCliente.reais(totalServicos + totalPecas) + "?", "Aprovar orçamento")) {
                    controller.aprovarOrcamento(o.getIdOrcamento());
                    atualizarLista();
                }
            });
            botoes.add(reprovar);
            botoes.add(aprovar);
        }
        JButton exportar = EstiloCliente.botao("Exportar (Word)", EstiloCliente.TEXTO, Color.WHITE, 13);
        exportar.addActionListener(e -> ExportadorOrcamento.exportar(this, controller, o));
        botoes.add(exportar);
        acoes.add(botoes);
        corpo.add(acoes, BorderLayout.SOUTH);
        return corpo;
    }

    // ------------------------------------------------------------------ status

    private static String valor(String s) { return s == null || s.isBlank() ? "—" : s; }

    private static String traduzirStatus(String status) {
        if (status == null) return "—";
        return switch (status.toUpperCase()) {
            case "PENDENTE" -> "Aguardando";
            case "APROVADO" -> "Aprovado";
            case "REPROVADO", "RECUSADO" -> "Reprovado";
            default -> status;
        };
    }

    private static Color corStatus(String status) {
        if (status == null) return Color.GRAY;
        return switch (status.toUpperCase()) {
            case "PENDENTE" -> EstiloCliente.AMARELO;
            case "APROVADO" -> EstiloCliente.VERDE;
            case "REPROVADO", "RECUSADO" -> EstiloCliente.VERMELHO;
            default -> Color.GRAY;
        };
    }
}
