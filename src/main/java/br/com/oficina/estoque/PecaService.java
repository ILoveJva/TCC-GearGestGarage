package br.com.oficina.estoque;

import br.com.oficina.estoque.dto.PecaResponseDTO;
import java.util.ArrayList;
import java.util.List;

public class PecaService {
    private final PecaRepository repository;
    public PecaService(PecaRepository repository) { this.repository = repository; }

    public PecaEntity cadastrar(String nomePopular, String vidaUtilTempo, String vidaUtilKm, String sistema) {
        return repository.salvar(new PecaEntity(null, nomePopular, vidaUtilTempo, vidaUtilKm, sistema));
    }
    public List<PecaEntity> listarTodasEntidades() {
        return repository.listarTodas();
    }

    public void atualizar(long id, String nome, String vidaTempo, String vidaKm, String sistema) {
        PecaEntity p = repository.buscarPorId(id);
        if (p == null) throw new br.com.oficina.shared.exception.RecursoNaoEncontradoException("Peca " + id + " nao encontrada");
        p.setNomePopular(nome);
        p.setVidaUtilTempo(vidaTempo);
        p.setVidaUtilKm(vidaKm);
        p.setSistema(sistema);
        repository.atualizar(p);
    }

    public List<PecaResponseDTO> listar() {
        List<PecaResponseDTO> out = new ArrayList<>();
        for (PecaEntity p : repository.listarTodas()) out.add(paraDTO(p));
        return out;
    }
    public PecaResponseDTO buscarDTO(long id) {
        PecaEntity p = repository.buscarPorId(id);
        if (p == null) throw new br.com.oficina.shared.exception.RecursoNaoEncontradoException("Peca " + id + " nao encontrada");
        return paraDTO(p);
    }
    public PecaEntity buscarEntidade(long id) {
        return repository.buscarPorId(id);
    }
    private PecaResponseDTO paraDTO(PecaEntity p) {
        return new PecaResponseDTO(p.getIdPeca(), p.getNomeExibicao(), p.getVidaUtilKm(), p.getVidaUtilTempo());
    }
}
