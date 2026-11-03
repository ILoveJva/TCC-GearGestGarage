package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
import controller.OficinaController;
import model.Oficina;
import model.Orcamento;

import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.List;

import static view.RtfUtil.*;

/**
 * Gera um documento do orçamento em formato .rtf (abre nativamente no Word/LibreOffice,
 * totalmente editável) com margens e fonte no padrão ABNT (Times New Roman 12,
 * margens esquerda/superior 3 cm e direita/inferior 2 cm).
 */
public final class ExportadorOrcamento {

    private ExportadorOrcamento() {}

    /** Abre um diálogo "Salvar como" e exporta o orçamento para o arquivo .rtf escolhido. */
    public static void exportar(Component pai, OficinaController controller, Orcamento orcamento) {
        String nomeSugerido = "orcamento_" + (orcamento.getCodigo().isEmpty()
            ? orcamento.getIdOrcamento() : orcamento.getCodigo().replace(".", "-")) + ".rtf";
        RtfUtil.salvar(pai, nomeSugerido, () -> gerarRtf(controller, orcamento));
    }

    private static String gerarRtf(OficinaController controller, Orcamento orcamento) {
        Oficina oficina = null;
        for (Oficina o : controller.listarTodas())
            if (o.getIdOficina() == orcamento.getIdOficina()) { oficina = o; break; }

        LinkedHashMap<CatalogoServicoEntity, Double> itens;
        try { itens = controller.listarItensOrcamentoComValor(orcamento.getIdOrcamento()); }
        catch (Exception e) { itens = new LinkedHashMap<>(); }

        List<Object[]> pecasBrutas;
        try { pecasBrutas = controller.listarPecasOrcamentoComValor(orcamento.getIdOrcamento()); }
        catch (Exception e) { pecasBrutas = List.of(); }

        double subtotalServicos = itens.values().stream().mapToDouble(Double::doubleValue).sum();
        List<PecaAgrupada> pecas = agruparPecas(pecasBrutas);
        double subtotalPecas = pecas.stream().mapToDouble(p -> p.valorUnit() * p.quantidade()).sum();

        StringBuilder rtf = new StringBuilder();
        rtf.append(PREAMBULO);

        // Cabeçalho da oficina
        rtf.append("\\qc\\b\\fs28 ").append(escapar("ORÇAMENTO DE SERVIÇOS")).append("\\b0\\fs24\\par\n");
        rtf.append("\\qc ").append(escapar(oficina != null ? oficina.getNome() : orcamento.getNomeOficina())).append("\\par\n");
        if (oficina != null) {
            rtf.append("\\qc ").append(escapar(oficina.getEndereco())).append("\\par\n");
            rtf.append("\\qc ").append(escapar("Telefone: " + oficina.getTelefone() + "   CNPJ: " + oficina.getCnpj())).append("\\par\n");
        }
        rtf.append("\\par\n\\pard\\ql\n");

        // Dados do orçamento
        String codigo = orcamento.getCodigo().isEmpty() ? ("#" + orcamento.getIdOrcamento()) : orcamento.getCodigo();
        rtf.append("\\b ").append(escapar("Orçamento: ")).append("\\b0 ").append(escapar(codigo)).append("\\par\n");
        rtf.append("\\b ").append(escapar("Data: ")).append("\\b0 ").append(escapar(valorOuTraco(orcamento.getDataCriacao()))).append("\\par\n");
        rtf.append("\\b ").append(escapar("Cliente: ")).append("\\b0 ").append(escapar(valorOuTraco(orcamento.getNomeCliente()))).append("\\par\n");
        rtf.append("\\b ").append(escapar("Veículo (placa): ")).append("\\b0 ").append(escapar(valorOuTraco(orcamento.getPlacaVeiculo()))).append("\\par\n");
        rtf.append("\\b ").append(escapar("Responsável: ")).append("\\b0 ").append(escapar(valorOuTraco(orcamento.getResponsavel()))).append("\\par\n");
        rtf.append("\\b ").append(escapar("Status: ")).append("\\b0 ").append(escapar(traduzirStatus(orcamento.getStatus()))).append("\\par\n");
        if (orcamento.isLevar() || orcamento.isBuscar()) {
            String opcoes = (orcamento.isLevar() ? "Levar o veículo até a oficina" : "")
                + (orcamento.isLevar() && orcamento.isBuscar() ? "; " : "")
                + (orcamento.isBuscar() ? "Buscar o veículo no endereço do cliente" : "");
            rtf.append("\\b ").append(escapar("Opções: ")).append("\\b0 ").append(escapar(opcoes)).append("\\par\n");
        }
        rtf.append("\\par\n");
        rtf.append("\\b ").append(escapar("Motivo / Descrição:")).append("\\b0\\par\n");
        rtf.append(escapar(orcamento.getReclamacao())).append("\\par\n\\par\n");

        // Itens de serviço
        rtf.append("\\b\\fs26 ").append(escapar("Itens de Serviço")).append("\\b0\\fs24\\par\n");
        if (itens.isEmpty()) {
            rtf.append(escapar("Nenhum item de serviço cadastrado.")).append("\\par\n");
        } else {
            int[] larguras = {5103, 1701, 2268};
            tabela(rtf, new String[]{"Serviço", "Tipo", "Valor (R$)"}, larguras, true);
            for (var e : itens.entrySet()) {
                CatalogoServicoEntity item = e.getKey();
                String tipo = "REVISAO".equalsIgnoreCase(item.getTipo()) ? "Revisão" : "Padrão";
                tabela(rtf, new String[]{item.getNome(), tipo, formatarValor(e.getValue())}, larguras, false);
            }
        }
        rtf.append("\\pard\\par\n");

        // Peças
        if (!pecas.isEmpty()) {
            rtf.append("\\b\\fs26 ").append(escapar("Peças a Trocar")).append("\\b0\\fs24\\par\n");
            int[] larguras = {2268, 1843, 1843, 850, 1134, 1134};
            tabela(rtf, new String[]{"Peça", "Nome Técnico", "Fabricante", "Qtd.", "Valor Unit.", "Subtotal"}, larguras, true);
            for (PecaAgrupada p : pecas) {
                tabela(rtf, new String[]{p.nome(), p.nomeTecnico(), p.fabricante(),
                    String.valueOf(p.quantidade()), formatarValor(p.valorUnit()), formatarValor(p.valorUnit() * p.quantidade())}, larguras, false);
            }
            rtf.append("\\pard\\par\n");
        }

        // Totais
        double total = subtotalServicos + subtotalPecas;
        rtf.append("\\qr\\b ").append(escapar("Subtotal serviços: " + formatarValor(subtotalServicos))).append("\\par\n");
        rtf.append("\\qr\\b ").append(escapar("Subtotal peças: " + formatarValor(subtotalPecas))).append("\\par\n");
        rtf.append("\\qr\\fs28\\b ").append(escapar("TOTAL: " + formatarValor(total))).append("\\b0\\fs24\\par\n");
        rtf.append("\\pard\\par\\par\n");

        // Assinaturas
        rtf.append("\\par\\par\n");
        rtf.append("\\qc ").append(escapar("_________________________________________")).append("\\par\n");
        rtf.append("\\qc ").append(escapar("Assinatura do Cliente")).append("\\par\\par\n");
        rtf.append("\\qc ").append(escapar("_________________________________________")).append("\\par\n");
        rtf.append("\\qc ").append(escapar("Assinatura do Responsável pela Oficina")).append("\\par\n");

        rtf.append(FECHAMENTO);
        return rtf.toString();
    }

    private static String traduzirStatus(String status) {
        if (status == null) return "—";
        return switch (status.toUpperCase()) {
            case "PENDENTE" -> "Aguardando aprovação";
            case "APROVADO" -> "Aprovado";
            case "REPROVADO", "RECUSADO" -> "Reprovado";
            default -> status;
        };
    }
}
