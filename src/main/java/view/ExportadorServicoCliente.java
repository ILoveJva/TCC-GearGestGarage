package view;

import br.com.oficina.atendimento.dto.ServicoResponseDTO;
import controller.OficinaController;
import model.Oficina;

import java.awt.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static view.RtfUtil.*;

/**
 * Relatório de um serviço (OS) para o cliente, em .rtf (abre no Word, que salva em PDF),
 * no padrão ABNT dos demais exportadores: oficina, veículo, datas, etapas realizadas,
 * descrição do trabalho e observação de saída.
 */
public final class ExportadorServicoCliente {

    private ExportadorServicoCliente() {}

    public static void exportar(Component pai, OficinaController controller, ServicoResponseDTO s,
                                String veiculo, ServicoCliente.Etapas etapas, String descricao) {
        String codigo = s.codigo() != null && !s.codigo().isBlank() ? s.codigo() : String.valueOf(s.idServico());
        RtfUtil.salvar(pai, "relatorio_servico_" + codigo.replace(".", "-") + ".rtf",
            () -> gerar(controller, s, veiculo, etapas, descricao));
    }

    private static String gerar(OficinaController controller, ServicoResponseDTO s, String veiculo,
                                ServicoCliente.Etapas etapas, String descricao) {
        Oficina oficina = null;
        for (Oficina o : controller.listarTodas())
            if (s.idOficina() != null && o.getIdOficina() == s.idOficina()) { oficina = o; break; }

        StringBuilder rtf = new StringBuilder(PREAMBULO);
        rtf.append("\\qc\\b\\fs28 ").append(escapar("RELATÓRIO DE SERVIÇO")).append("\\b0\\fs24\\par\n");
        if (oficina != null) {
            rtf.append("\\qc ").append(escapar(oficina.getNome())).append("\\par\n");
            rtf.append("\\qc ").append(escapar(valorOuTraco(oficina.getEndereco()))).append("\\par\n");
            rtf.append("\\qc ").append(escapar("Telefone: " + valorOuTraco(oficina.getTelefone())
                + "   CNPJ: " + valorOuTraco(oficina.getCnpj()))).append("\\par\n");
        }
        rtf.append("\\par\n\\pard\\ql\n");

        String codigo = s.codigo() != null && !s.codigo().isBlank() ? s.codigo() : "#" + s.idServico();
        linha(rtf, "Ordem de Serviço: ", codigo);
        linha(rtf, "Serviço: ", valorOuTraco(s.titulo()));
        linha(rtf, "Veículo: ", veiculo);
        linha(rtf, "Início: ", EstiloCliente.dataBR(s.dataServico()));
        if (etapas.conclusao() != null) linha(rtf, "Concluído em: ", EstiloCliente.dataBR(etapas.conclusao()));
        linha(rtf, "Status: ", ServicoCliente.traduzirStatus(s.status()));
        linha(rtf, "Emitido em: ", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")));
        rtf.append("\\par\n");

        rtf.append("\\b\\fs26 ").append(escapar("Etapas realizadas")).append("\\b0\\fs24\\par\n");
        List<ServicoResponseDTO.ItemView> lista = etapas.checklist();
        if (lista.isEmpty()) {
            rtf.append(escapar("Nenhuma etapa registrada.")).append("\\par\n");
        } else {
            int[] larguras = {5103, 1701, 2267};
            tabela(rtf, new String[]{"Etapa", "Situação", "Concluída em"}, larguras, true);
            for (ServicoResponseDTO.ItemView it : lista) {
                boolean ok = ServicoCliente.etapaConcluida(it.status());
                tabela(rtf, new String[]{valorOuTraco(it.descricao()), ok ? "Concluída" : "Pendente",
                    ok ? EstiloCliente.dataBR(it.dataRealizacao()) : "—"}, larguras, false);
            }
            rtf.append("\\pard\\par\n");
        }

        rtf.append("\\b\\fs26 ").append(escapar("Descrição")).append("\\b0\\fs24\\par\n");
        rtf.append(escapar(valorOuTraco(descricao))).append("\\par\\par\n");
        if (etapas.observacaoSaida() != null) {
            rtf.append("\\b\\fs26 ").append(escapar("Estado do veículo na saída")).append("\\b0\\fs24\\par\n");
            rtf.append(escapar(valorOuTraco(etapas.observacaoSaida().descricao()))).append("\\par\n");
        }
        rtf.append(FECHAMENTO);
        return rtf.toString();
    }

    private static void linha(StringBuilder rtf, String rotulo, String valor) {
        rtf.append("\\b ").append(escapar(rotulo)).append("\\b0 ").append(escapar(valor)).append("\\par\n");
    }
}
