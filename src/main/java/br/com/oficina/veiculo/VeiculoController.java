package br.com.oficina.veiculo;

import br.com.oficina.veiculo.dto.VeiculoResponseDTO;
import java.util.List;

public class VeiculoController {
    private final VeiculoService service;
    public VeiculoController(VeiculoService service) { this.service = service; }

    public List<MontadoraEntity> montadoras() { return service.listarMontadoras(); }
    public List<ModeloEntity> modelos(long idMontadora) { return service.listarModelos(idMontadora); }

    /** Cadastra veículo; tipo é herdado do modelo selecionado. */
    public VeiculoEntity cadastrar(String placa, long idMontadora, long idModelo, long idCliente) {
        return service.cadastrar(placa, idMontadora, idModelo, idCliente);
    }
    public VeiculoResponseDTO porId(long id) { return service.buscarDTO(id); }
    public List<VeiculoResponseDTO> todos() { return service.listar(); }

    public DetalhesVeiculoEntity salvarDetalhes(long idVeiculo, String motor, String cambio,
                                                String direcao, String freios, String cor, String vin) {
        return service.salvarDetalhes(idVeiculo, motor, cambio, direcao, freios, cor, vin);
    }
    public DetalhesVeiculoEntity buscarDetalhes(long idVeiculo) {
        return service.buscarDetalhes(idVeiculo);
    }

    public MontadoraEntity cadastrarMontadora(String nome, String pais) {
        return service.cadastrarMontadora(nome, pais);
    }
    public ModeloEntity cadastrarModelo(String nome, int ano, String tipo, long idMontadora) {
        return service.cadastrarModelo(nome, ano, tipo, idMontadora);
    }
}
