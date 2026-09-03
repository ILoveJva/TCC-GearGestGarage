package br.com.oficina.estoque;

import java.time.LocalDate;
import java.util.List;

/**
 * Regras de estoque das peças. Toda entrada/saída ajusta a quantidade da peça
 * e registra uma movimentação no histórico, mantendo os dois em sincronia.
 */
public class EstoqueService {
    private final PecaRepository pecaRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;

    public EstoqueService(PecaRepository pecaRepository, MovimentacaoEstoqueRepository movimentacaoRepository) {
        this.pecaRepository = pecaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    /** Entrada manual (cadastro) de estoque. Incrementa a peça e registra a movimentação. */
    public MovimentacaoEstoqueEntity registrarEntrada(long idPeca, int quantidade, String observacao) {
        if (quantidade <= 0) throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        pecaRepository.ajustarEstoque(idPeca, quantidade);
        return movimentacaoRepository.salvar(new MovimentacaoEstoqueEntity(
            null, idPeca, "ENTRADA", quantidade, LocalDate.now().toString(),
            "MANUAL", null, observacao));
    }

    /** Saída automática disparada pela conclusão de uma OS. Decrementa a peça e registra a movimentação. */
    public MovimentacaoEstoqueEntity registrarSaidaOS(long idPeca, int quantidade, long idServico, String observacao) {
        if (quantidade <= 0) return null;
        pecaRepository.ajustarEstoque(idPeca, -quantidade);
        return movimentacaoRepository.salvar(new MovimentacaoEstoqueEntity(
            null, idPeca, "SAIDA", quantidade, LocalDate.now().toString(),
            "OS", idServico, observacao));
    }

    public List<MovimentacaoEstoqueEntity> listarMovimentacoes() {
        return movimentacaoRepository.listarTodas();
    }
}
