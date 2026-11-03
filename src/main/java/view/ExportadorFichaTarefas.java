package view;

import br.com.oficina.atendimento.CatalogoServicoEntity;
import br.com.oficina.atendimento.dto.OrcamentoResponseDTO;
import br.com.oficina.atendimento.dto.ServicoResponseDTO;
import br.com.oficina.veiculo.DetalhesVeiculoEntity;
import controller.OficinaController;
import model.Cliente;
import model.Oficina;
import model.Veiculo;

import java.awt.Component;
import java.util.List;

import static view.RtfUtil.*;

/**
 * Gera a Ficha de Tarefas de uma O.S. em .rtf (abre no Word, margens/fonte ABNT):
 * um documento impresso com tudo que o mecânico precisa para executar o serviço
 * sem precisar acessar o sistema — veículo, cliente, reclamação, checklist de
 * serviços contratados, peças necessárias e espaço para observações.
 */
public final class ExportadorFichaTarefas {

    private ExportadorFichaTarefas() {}

    public static void exportar(Component pai, OficinaController controller, ServicoResponseDTO servico) {
        String codigo = servico.codigo() != null && !servico.codigo().isEmpty()
            ? servico.codigo() : String.valueOf(servico.idServico());
        String nomeSugerido = "ficha_tarefas_" + codigo.replace(".", "-") + ".rtf";
        RtfUtil.salvar(pai, nomeSugerido, () -> gerarRtf(controller, servico));
    }

    private static String gerarRtf(OficinaController controller, ServicoResponseDTO servico) {
        Oficina oficina = null;
        for (Oficina o : controller.listarTodas())
            if (servico.idOficina() != null && o.getIdOficina() == servico.idOficina()) { oficina = o; break; }

        long idVeiculo = servico.idVeiculo() != null ? servico.idVeiculo() : 0L;
        Veiculo veiculo = null;
        for (Veiculo v : controller.listarVeiculos())
            if (v.getIdVeiculo() == idVeiculo) { veiculo = v; break; }

        DetalhesVeiculoEntity detalhes = null;
        try { detalhes = controller.getDetalhesVeiculo(idVeiculo); } catch (Exception ignored) {}

        Cliente cliente = null;
        try {
            long idCliente = controller.getIdClientePorVeiculo(idVeiculo);
            cliente = controller.cliente_id(idCliente);
        } catch (Exception ignored) {}

        String reclamacao = null;
        List<CatalogoServicoEntity> itens = List.of();
        List<PecaAgrupada> pecas = List.of();
        if (servico.idOrcamento() != null && servico.idOrcamento() > 0) {
            try {
                OrcamentoResponseDTO orc = controller.buscarOrcamento(servico.idOrcamento());
                reclamacao = orc != null ? orc.reclamacao() : null;
            } catch (Exception ignored) {}
            try { itens = controller.listarItensDoOrcamento(servico.idOrcamento()); } catch (Exception ignored) {}
            try { pecas = agruparPecas(controller.listarPecasOrcamentoComValor(servico.idOrcamento())); } catch (Exception ignored) {}
        }

        StringBuilder rtf = new StringBuilder();
        rtf.append(PREAMBULO);

        // Cabeçalho da oficina
        rtf.append("\\qc\\b\\fs28 ").append(escapar("FICHA DE TAREFAS")).append("\\b0\\fs24\\par\n");
        if (oficina != null) {
            rtf.append("\\qc ").append(escapar(oficina.getNome())).append("\\par\n");
            rtf.append("\\qc ").append(escapar(oficina.getEndereco())).append("\\par\n");
        }
        rtf.append("\\par\n\\pard\\ql\n");

        // Dados da O.S.
        String codigo = servico.codigo() != null && !servico.codigo().isEmpty()
            ? servico.codigo() : ("#" + servico.idServico());
        rtf.append("\\b ").append(escapar("Ordem de Serviço: ")).append("\\b0 ").append(escapar(codigo)).append("\\par\n");
        rtf.append("\\b ").append(escapar("Título: ")).append("\\b0 ").append(escapar(valorOuTraco(servico.titulo()))).append("\\par\n");
        rtf.append("\\b ").append(escapar("Data: ")).append("\\b0 ").append(escapar(valorOuTraco(servico.dataServico()))).append("\\par\n");
        rtf.append("\\b ").append(escapar("Sistema do veículo: ")).append("\\b0 ").append(escapar(traduzirTipoServico(servico.tipoServico()))).append("\\par\n");
        rtf.append("\\b ").append(escapar("Natureza: ")).append("\\b0 ").append(escapar(traduzirTipoManutencao(servico.tipoManutencao()))).append("\\par\n");
        rtf.append("\\par\n");

        // Dados do veículo
        rtf.append("\\b\\fs26 ").append(escapar("Veículo")).append("\\b0\\fs24\\par\n");
        if (veiculo != null) {
            String marca = veiculo.getModelo() != null && veiculo.getModelo().getMontadora() != null
                ? veiculo.getModelo().getMontadora().getNome() : "—";
            String modelo = veiculo.getModelo() != null ? veiculo.getModelo().getNome() : "—";
            int ano = veiculo.getModelo() != null ? veiculo.getModelo().getAno() : 0;
            rtf.append("\\b ").append(escapar("Placa: ")).append("\\b0 ").append(escapar(valorOuTraco(veiculo.getPlaca()))).append("\\par\n");
            rtf.append("\\b ").append(escapar("Marca/Modelo: ")).append("\\b0 ").append(escapar(marca + " " + modelo)).append("\\par\n");
            rtf.append("\\b ").append(escapar("Ano: ")).append("\\b0 ").append(escapar(ano > 0 ? String.valueOf(ano) : "—")).append("\\par\n");
        } else {
            rtf.append(escapar("Veículo não encontrado.")).append("\\par\n");
        }
        if (detalhes != null) {
            rtf.append("\\b ").append(escapar("Motor: ")).append("\\b0 ").append(escapar(valorOuTraco(detalhes.getMotor()))).append("\\par\n");
            rtf.append("\\b ").append(escapar("Câmbio: ")).append("\\b0 ").append(escapar(valorOuTraco(detalhes.getCambio()))).append("\\par\n");
            rtf.append("\\b ").append(escapar("Direção: ")).append("\\b0 ").append(escapar(valorOuTraco(detalhes.getDirecao()))).append("\\par\n");
            rtf.append("\\b ").append(escapar("Freios: ")).append("\\b0 ").append(escapar(valorOuTraco(detalhes.getSistemaFreios()))).append("\\par\n");
            rtf.append("\\b ").append(escapar("Cor: ")).append("\\b0 ").append(escapar(valorOuTraco(detalhes.getCor()))).append("\\par\n");
            rtf.append("\\b ").append(escapar("VIN: ")).append("\\b0 ").append(escapar(valorOuTraco(detalhes.getVin()))).append("\\par\n");
        }
        rtf.append("\\par\n");

        // Dados do cliente
        rtf.append("\\b\\fs26 ").append(escapar("Cliente")).append("\\b0\\fs24\\par\n");
        rtf.append("\\b ").append(escapar("Nome: ")).append("\\b0 ").append(escapar(cliente != null ? valorOuTraco(cliente.getNome()) : "—")).append("\\par\n");
        rtf.append("\\b ").append(escapar("Telefone: ")).append("\\b0 ").append(escapar(cliente != null ? valorOuTraco(cliente.getTelefone()) : "—")).append("\\par\n");
        rtf.append("\\par\n");

        // Reclamação do cliente
        rtf.append("\\b\\fs26 ").append(escapar("Reclamação do Cliente")).append("\\b0\\fs24\\par\n");
        rtf.append(escapar(valorOuTraco(reclamacao))).append("\\par\n\\par\n");

        // Checklist de serviços a realizar
        rtf.append("\\b\\fs26 ").append(escapar("Serviços a Realizar")).append("\\b0\\fs24\\par\n");
        if (itens.isEmpty()) {
            rtf.append(escapar("Nenhum serviço contratado neste orçamento.")).append("\\par\n");
        } else {
            for (CatalogoServicoEntity item : itens) {
                rtf.append(escapar("☐ " + item.getNome())).append("\\par\n");
                if (item.getDescricao() != null && !item.getDescricao().isBlank())
                    rtf.append("\\li400 ").append(escapar(item.getDescricao())).append("\\li0\\par\n");
            }
        }
        rtf.append("\\par\n");

        // Peças necessárias
        if (!pecas.isEmpty()) {
            rtf.append("\\b\\fs26 ").append(escapar("Peças Necessárias")).append("\\b0\\fs24\\par\n");
            int[] larguras = {4500, 2500, 2072};
            tabela(rtf, new String[]{"Peça", "Fabricante", "Quantidade"}, larguras, true);
            for (PecaAgrupada p : pecas)
                tabela(rtf, new String[]{p.nome(), p.fabricante(), String.valueOf(p.quantidade())}, larguras, false);
            rtf.append("\\pard\\par\n");
        }

        // Observações do mecânico
        rtf.append("\\b\\fs26 ").append(escapar("Observações do Mecânico")).append("\\b0\\fs24\\par\n");
        for (int i = 0; i < 5; i++)
            rtf.append("\\par ").append(escapar("_________________________________________________________________")).append("\\par\n");
        rtf.append("\\par\n");

        // Assinatura
        rtf.append("\\par\\par\n");
        rtf.append("\\qc ").append(escapar("_________________________________________")).append("\\par\n");
        rtf.append("\\qc ").append(escapar("Assinatura do Mecânico Responsável")).append("\\par\n");

        rtf.append(FECHAMENTO);
        return rtf.toString();
    }

    private static String traduzirTipoServico(String tipo) {
        if (tipo == null) return "—";
        return switch (tipo.toUpperCase()) {
            case "REVISAO" -> "Revisão";
            case "MOTOR" -> "Motor";
            case "TRANSMISSAO" -> "Transmissão";
            case "DIRECAO" -> "Direção";
            case "SUSPENSAO" -> "Suspensão";
            case "FREIOS" -> "Freios";
            case "ARREFECIMENTO" -> "Arrefecimento";
            case "ELETRICA" -> "Elétrica";
            default -> "Outros";
        };
    }

    private static String traduzirTipoManutencao(String tipo) {
        if (tipo == null) return "—";
        return "PREVENTIVA".equalsIgnoreCase(tipo) ? "Preventiva" : "Corretiva";
    }
}
