package br.com.oficina.veiculo;

import java.util.List;

public class AbastecimentoController {
    private final AbastecimentoService service;
    public AbastecimentoController(AbastecimentoService service) { this.service = service; }

    public AbastecimentoEntity registrar(long idVeiculo, String data, double valor, double litros,
                                         String tipoCombustivel, int quilometragem) {
        return service.registrar(idVeiculo, data, valor, litros, tipoCombustivel, quilometragem);
    }

    public List<AbastecimentoEntity> listarPorVeiculo(long idVeiculo) { return service.listarPorVeiculo(idVeiculo); }
    public void remover(long id) { service.remover(id); }
}
