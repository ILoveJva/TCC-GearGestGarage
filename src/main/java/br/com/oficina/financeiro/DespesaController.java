package br.com.oficina.financeiro;

import java.util.List;

public class DespesaController {
    private final DespesaService service;
    public DespesaController(DespesaService service) { this.service = service; }

    public DespesaEntity cadastrar(String descricao, String categoria, double valor,
                                   String dataDespesa, String formaPagamento, String observacao, long idOficina) {
        return service.cadastrar(descricao, categoria, valor, dataDespesa, formaPagamento, observacao, idOficina);
    }

    public List<DespesaEntity> todas()   { return service.listarTodas(); }
    public DespesaEntity porId(long id)  { return service.buscar(id); }
    public void remover(long id)         { service.remover(id); }
}
