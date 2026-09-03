package br.com.oficina.financeiro;

import java.util.List;

public class DespesaService {
    private final DespesaRepository repository;
    public DespesaService(DespesaRepository repository) { this.repository = repository; }

    public DespesaEntity cadastrar(String descricao, String categoria, double valor,
                                   String dataDespesa, String formaPagamento, String observacao, long idOficina) {
        return repository.salvar(new DespesaEntity(null, descricao, categoria, valor,
            dataDespesa, formaPagamento, observacao, idOficina));
    }

    public List<DespesaEntity> listarTodas() { return repository.listarTodas(); }
    public DespesaEntity buscar(long id)     { return repository.buscarPorId(id); }
    public void remover(long id)             { repository.remover(id); }
}
