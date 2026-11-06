package view;

import br.com.oficina.atendimento.dto.OrcamentoResponseDTO;
import br.com.oficina.atendimento.dto.ServicoResponseDTO;
import br.com.oficina.usuario.FuncionarioEntity;
import controller.OficinaController;
import model.Oficina;
import model.Veiculo;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * "Serviços em Andamento" e "Serviços finalizados" (protótipo do Figma): cartões sanfona
 * com bolinha de status (verde = em andamento, amarelo = aguardando início, vazada =
 * finalizado), serviço, veículo e oficina. Expandido mostra as etapas com ✓, a descrição
 * do trabalho (e quem a escreveu), o status e — nos finalizados — "Exportar relatório".
 */
public class V_ClienteServicos extends JPanel {

    private final OficinaController controller;
    private final Map<Long, Veiculo> veiculos = new HashMap<>();
    private final Map<Long, String> oficinas = new HashMap<>();
    private final Map<Long, String> funcionarios = new HashMap<>();
    private final boolean finalizados;

    public V_ClienteServicos(OficinaController controller) {
        this(controller, false);
    }

    /** @param finalizados abre a lista de finalizados (atalho "Consultar Serviços Antigos"). */
    public V_ClienteServicos(OficinaController controller, boolean finalizados) {
        this.controller = controller;
        this.finalizados = finalizados;
        for (Veiculo v : controller.listarMeusVeiculos()) veiculos.put(v.getIdVeiculo(), v);
        for (Oficina o : controller.listarTodas()) oficinas.put(o.getIdOficina(), o.getNome());
        try {
            for (FuncionarioEntity f : controller.listarFuncionarios())
                if (f.getIdFuncionario() != null) funcionarios.put(f.getIdFuncionario(), f.getNome());
        } catch (Exception ignored) { }

        setLayout(new BorderLayout());
        setBackground(EstiloCliente.FUNDO);

        JPanel conteudo = new JPanel(new BorderLayout(0, 18));
        conteudo.setOpaque(false);
        conteudo.add(construirAlternador(), BorderLayout.NORTH);
        conteudo.add(construirLista(), BorderLayout.CENTER);

        add(EstiloCliente.pagina(finalizados ? "Serviços finalizados" : "Serviços em Andamento",
                () -> EstiloCliente.navegar(this, new V_ClienteMenu(controller)), conteudo));
    }

    /** No desktop as duas listas ficam a um clique: "Em andamento | Finalizados". */
    private JComponent construirAlternador() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        p.setOpaque(false);
        JButton andamento = EstiloCliente.botao("Em andamento",
                finalizados ? EstiloCliente.TILE : EstiloCliente.TEXTO,
                finalizados ? EstiloCliente.TEXTO : Color.WHITE, 13);
        JButton fin = EstiloCliente.botao("Finalizados",
                finalizados ? EstiloCliente.TEXTO : EstiloCliente.TILE,
                finalizados ? Color.WHITE : EstiloCliente.TEXTO, 13);
        andamento.addActionListener(e -> { if (finalizados) EstiloCliente.navegar(this, new V_ClienteServicos(controller, false)); });
        fin.addActionListener(e -> { if (!finalizados) EstiloCliente.navegar(this, new V_ClienteServicos(controller, true)); });
        p.add(andamento);
        p.add(fin);
        return p;
    }

    private JComponent construirLista() {
        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setOpaque(false);

        List<ServicoResponseDTO> servicos = new ArrayList<>(controller.listarMeusServicos().stream()
                .filter(s -> finalizados == ServicoCliente.finalizado(s.status()))
                .toList());
        // Mais recentes primeiro.
        servicos.sort(Comparator.comparing((ServicoResponseDTO s) -> s.dataServico() == null ? "" : s.dataServico())
                .reversed());

        if (servicos.isEmpty()) {
            JLabel vazio = new JLabel(finalizados ? "Nenhum serviço finalizado ainda."
                    : "Nenhum serviço em andamento.", SwingConstants.CENTER);
            vazio.setFont(EstiloCliente.fonte(Font.ITALIC, 14));
            vazio.setForeground(EstiloCliente.TEXTO_SUAVE);
            vazio.setAlignmentX(Component.CENTER_ALIGNMENT);
            lista.add(vazio);
        }
        boolean primeiro = true;
        for (ServicoResponseDTO s : servicos) {
            Veiculo v = s.idVeiculo() != null ? veiculos.get(s.idVeiculo()) : null;
            String titulo = (s.titulo() != null && !s.titulo().isBlank() ? s.titulo() : "OS #" + s.idServico()) + ":";
            String oficina = s.idOficina() != null ? oficinas.getOrDefault(s.idOficina(), "—") : "—";
            EstiloCliente.CartaoExpansivel card = new EstiloCliente.CartaoExpansivel(
                    bolinha(s.status()), titulo,
                    EstiloCliente.nomeVeiculo(v) + (v != null ? " · " + v.getPlaca() : ""),
                    oficina, construirCorpo(s, v), primeiro && !finalizados);
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            lista.add(card);
            lista.add(Box.createVerticalStrut(12));
            primeiro = false;
        }
        return lista;
    }

    private Icon bolinha(String status) {
        if (ServicoCliente.finalizado(status))
            return EstiloCliente.bolinha(null, true);
        if ("EM_ANDAMENTO".equalsIgnoreCase(status)) return EstiloCliente.bolinha(EstiloCliente.VERDE, false);
        if ("CONGELADA".equalsIgnoreCase(status)) return EstiloCliente.bolinha(Color.decode("#9E9E9E"), false);
        return EstiloCliente.bolinha(EstiloCliente.AMARELO, false);
    }

    // ------------------------------------------------------------------ área expandida

    private JComponent construirCorpo(ServicoResponseDTO s, Veiculo v) {
        ServicoCliente.Etapas etapas = ServicoCliente.separar(s);

        JPanel corpo = new JPanel(new BorderLayout(24, 14));
        corpo.setBorder(BorderFactory.createEmptyBorder(14, 24, 16, 24));

        // Esquerda: etapas com ✓.
        StringBuilder html = new StringBuilder("<html><b>Serviço:</b><ul style='margin-left:16px'>");
        if (etapas.checklist().isEmpty()) html.append("<li>Etapas ainda não registradas pela oficina.</li>");
        for (ServicoResponseDTO.ItemView it : etapas.checklist()) {
            boolean ok = ServicoCliente.etapaConcluida(it.status());
            html.append("<li>").append(EstiloCliente.escapar(it.descricao()))
                .append(ok ? " <b>✓</b>" : "")
                .append(ok && it.dataRealizacao() != null
                        ? " <span style='color:#555555'>(" + EstiloCliente.dataBR(it.dataRealizacao()) + ")</span>" : "")
                .append("</li>");
        }
        html.append("</ul></html>");
        JLabel lblEtapas = new JLabel(html.toString());
        lblEtapas.setFont(EstiloCliente.fonte(Font.PLAIN, 13));
        lblEtapas.setForeground(EstiloCliente.TEXTO);
        lblEtapas.setVerticalAlignment(SwingConstants.TOP);
        corpo.add(lblEtapas, BorderLayout.CENTER);

        // Direita: "Descrição (por: fulano)" em caixa clara arredondada.
        String autor = null, descricao;
        if (etapas.descricao() != null) {
            descricao = etapas.descricao().descricao();
            if (etapas.descricao().idFuncionario() != null)
                autor = funcionarios.get(etapas.descricao().idFuncionario());
        } else {
            descricao = reclamacaoDoOrcamento(s);
        }
        String rotuloDesc = etapas.descricao() != null
                ? "Descrição" + (autor != null ? " (por: " + autor + ")" : "") + ":"
                : "Sua solicitação:";

        JPanel direita = new JPanel(new BorderLayout(0, 6));
        direita.setOpaque(false);
        JLabel tituloDesc = new JLabel(rotuloDesc, SwingConstants.CENTER);
        tituloDesc.setFont(EstiloCliente.fonte(Font.PLAIN, 13));
        direita.add(tituloDesc, BorderLayout.NORTH);
        EstiloCliente.PainelArredondado caixa = new EstiloCliente.PainelArredondado(
                new BorderLayout(), 16, EstiloCliente.CAIXA_CLARA);
        caixa.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel txtDesc = new JLabel("<html><div style='text-align:center;width:260px'>"
                + EstiloCliente.escapar(descricao == null || descricao.isBlank() ? "Sem descrição." : descricao)
                    .replace("\n", "<br>") + "</div></html>", SwingConstants.CENTER);
        txtDesc.setFont(EstiloCliente.fonte(Font.PLAIN, 13));
        caixa.add(txtDesc);
        direita.add(caixa, BorderLayout.CENTER);
        if (etapas.observacaoSaida() != null) {
            JLabel saida = new JLabel("<html><div style='text-align:center;width:280px'><b>Estado na saída:</b> "
                    + EstiloCliente.escapar(etapas.observacaoSaida().descricao()) + "</div></html>", SwingConstants.CENTER);
            saida.setFont(EstiloCliente.fonte(Font.PLAIN, 12));
            direita.add(saida, BorderLayout.SOUTH);
        }
        corpo.add(direita, BorderLayout.EAST);

        // Rodapé centralizado: status, datas e exportação.
        JPanel rodape = new JPanel();
        rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));
        rodape.setOpaque(false);
        rodape.add(centralizado("Status: " + ServicoCliente.traduzirStatus(s.status())));
        String datas = "Início: " + EstiloCliente.dataBR(s.dataServico());
        if (etapas.conclusao() != null) datas += "   ·   Concluído em: " + EstiloCliente.dataBR(etapas.conclusao());
        else if (!etapas.checklist().isEmpty())
            datas += "   ·   " + etapas.concluidas() + " de " + etapas.checklist().size() + " etapa(s) concluída(s)";
        rodape.add(Box.createVerticalStrut(4));
        rodape.add(centralizado(datas));
        if (finalizados) {
            rodape.add(Box.createVerticalStrut(10));
            JButton exportar = EstiloCliente.botao("Exportar relatório (Word)", EstiloCliente.TEXTO, Color.WHITE, 13);
            exportar.setAlignmentX(Component.CENTER_ALIGNMENT);
            String nomeVeiculo = EstiloCliente.nomeVeiculo(v) + (v != null ? " · " + v.getPlaca() : "");
            String descFinal = descricao;
            exportar.addActionListener(e -> ExportadorServicoCliente.exportar(this, controller, s,
                    nomeVeiculo, etapas, descFinal));
            rodape.add(exportar);
        }
        corpo.add(rodape, BorderLayout.SOUTH);
        return corpo;
    }

    private String reclamacaoDoOrcamento(ServicoResponseDTO s) {
        if (s.idOrcamento() == null || s.idOrcamento() <= 0) return null;
        try {
            OrcamentoResponseDTO orc = controller.buscarOrcamento(s.idOrcamento());
            return orc != null ? orc.reclamacao() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static JLabel centralizado(String texto) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER);
        l.setFont(EstiloCliente.fonte(Font.PLAIN, 13));
        l.setForeground(EstiloCliente.TEXTO);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }
}
