package view;

import br.com.oficina.atendimento.dto.ServicoResponseDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Leitura dos itens de uma OS para as telas do cliente.
 *
 * O sistema grava os itens com {@code etapa}: 0 = descrição geral do trabalho,
 * 1 = itens do checklist e 2 = observação de saída (ver {@code ServicoService}).
 * Os dados de exemplo, porém, numeram as etapas 1..N, todas como checklist. Por isso:
 * só tratamos a etapa 2 como observação de saída quando a OS também tem a etapa 0
 * (ou seja, foi registrada pelo fluxo do sistema).
 */
final class ServicoCliente {

    private ServicoCliente() {}

    record Etapas(ServicoResponseDTO.ItemView descricao,
                  List<ServicoResponseDTO.ItemView> checklist,
                  ServicoResponseDTO.ItemView observacaoSaida,
                  String conclusao) {
        long concluidas() { return checklist.stream().filter(i -> etapaConcluida(i.status())).count(); }
    }

    static Etapas separar(ServicoResponseDTO s) {
        List<ServicoResponseDTO.ItemView> itens = s.itens() == null ? List.of() : s.itens();
        ServicoResponseDTO.ItemView desc = itens.stream().filter(i -> i.etapa() == 0).findFirst().orElse(null);
        ServicoResponseDTO.ItemView obs = desc == null ? null
                : itens.stream().filter(i -> i.etapa() == 2).findFirst().orElse(null);

        List<ServicoResponseDTO.ItemView> checklist = new ArrayList<>();
        String ultima = null;
        for (ServicoResponseDTO.ItemView i : itens) {
            if (i == desc || i == obs) continue;
            checklist.add(i);
            if (etapaConcluida(i.status()) && i.dataRealizacao() != null && !i.dataRealizacao().isBlank()
                    && (ultima == null || i.dataRealizacao().compareTo(ultima) > 0))
                ultima = i.dataRealizacao();
        }
        checklist.sort((a, b) -> Integer.compare(a.etapa(), b.etapa()));
        String conclusao = "CONCLUIDA".equalsIgnoreCase(s.status()) ? ultima : null;
        return new Etapas(desc, checklist, obs, conclusao);
    }

    /** Etapas são gravadas como CONCLUIDO pelo sistema, mas há dados antigos com CONCLUIDA. */
    static boolean etapaConcluida(String status) {
        return "CONCLUIDO".equalsIgnoreCase(status) || "CONCLUIDA".equalsIgnoreCase(status);
    }

    static boolean finalizado(String status) {
        return "CONCLUIDA".equalsIgnoreCase(status) || "CANCELADA".equalsIgnoreCase(status);
    }

    static String traduzirStatus(String status) {
        if (status == null) return "—";
        return switch (status.toUpperCase()) {
            case "ABERTA" -> "Aguardando início";
            case "EM_ANDAMENTO" -> "Em andamento";
            case "CONCLUIDA" -> "Finalizado";
            case "CANCELADA" -> "Cancelado";
            case "CONGELADA" -> "Aguardando novo orçamento";
            default -> status;
        };
    }
}
