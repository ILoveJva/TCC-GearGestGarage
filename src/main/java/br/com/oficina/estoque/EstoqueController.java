package br.com.oficina.estoque;

import java.util.List;

public class EstoqueController {
    private final EstoqueService service;
    public EstoqueController(EstoqueService service) { this.service = service; }

    public MovimentacaoEstoqueEntity registrarEntrada(long idPeca, int quantidade, Double valorUnitario, String observacao) {
        return service.registrarEntrada(idPeca, quantidade, valorUnitario, observacao);
    }

    public MovimentacaoEstoqueEntity registrarSaidaOS(long idPeca, int quantidade, long idServico, String observacao) {
        return service.registrarSaidaOS(idPeca, quantidade, idServico, observacao);
    }

    public List<MovimentacaoEstoqueEntity> movimentacoes() {
        return service.listarMovimentacoes();
    }
}
