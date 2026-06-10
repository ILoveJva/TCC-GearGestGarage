package br.com.oficina.estoque;

import br.com.oficina.estoque.dto.PecaResponseDTO;
import br.com.oficina.shared.exception.RecursoNaoEncontradoException;
import java.util.ArrayList;
import java.util.List;

public class PecaService {
    private final PecaRepository repository;
    public PecaService(PecaRepository repository) { this.repository = repository; }

    public FabricantePecaEntity cadastrarFabricante(String nome, String pais) {
        return repository.salvarFabricante(new FabricantePecaEntity(null, nome, pais));
    }
    public PecaEntity cadastrar(String nomePeca, String vidaUtilTempo, String vidaUtilKm,
                                long idFabricante, long idVeiculo) {
        if (repository.buscarFabricante(idFabricante) == null)
            throw new RecursoNaoEncontradoException("Fabricante " + idFabricante + " nao encontrado");
        return repository.salvar(new PecaEntity(null, nomePeca, vidaUtilTempo, vidaUtilKm, idFabricante, idVeiculo));
    }
    public List<PecaResponseDTO> listar() {
        List<PecaResponseDTO> out = new ArrayList<>();
        for (PecaEntity p : repository.listarTodas()) out.add(paraDTO(p));
        return out;
    }
    public PecaResponseDTO buscarDTO(long id) {
        PecaEntity p = repository.buscarPorId(id);
        if (p == null) throw new RecursoNaoEncontradoException("Peca " + id + " nao encontrada");
        return paraDTO(p);
    }
    private PecaResponseDTO paraDTO(PecaEntity p) {
        FabricantePecaEntity f = p.getIdFabricantePeca() != null ? repository.buscarFabricante(p.getIdFabricantePeca()) : null;
        return new PecaResponseDTO(p.getIdPeca(), p.getNomePeca(),
            f != null ? f.getNome() : "?", p.getVidaUtilKm(), p.getVidaUtilTempo());
    }
}
