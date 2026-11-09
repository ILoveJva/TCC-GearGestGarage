package br.com.oficina.veiculo;

import java.util.List;

public class AbastecimentoService {
    private final AbastecimentoRepository repository;
    public AbastecimentoService(AbastecimentoRepository repository) { this.repository = repository; }

    public AbastecimentoEntity registrar(long idVeiculo, String data, double valor, double litros,
                                         String tipoCombustivel, int quilometragem) {
        if (valor <= 0) throw new IllegalArgumentException("O valor deve ser maior que zero.");
        if (litros <= 0) throw new IllegalArgumentException("A quantidade de litros deve ser maior que zero.");
        if (quilometragem < 0) throw new IllegalArgumentException("A quilometragem não pode ser negativa.");
        return repository.salvar(new AbastecimentoEntity(null, idVeiculo, data, valor, litros,
            tipoCombustivel, quilometragem));
    }

    public List<AbastecimentoEntity> listarPorVeiculo(long idVeiculo) { return repository.listarPorVeiculo(idVeiculo); }
    public void remover(long id) { repository.remover(id); }
}
