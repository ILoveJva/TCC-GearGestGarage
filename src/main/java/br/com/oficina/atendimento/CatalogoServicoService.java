package br.com.oficina.atendimento;

import java.util.List;

public class CatalogoServicoService {
    private final CatalogoServicoRepository repository;

    public CatalogoServicoService(CatalogoServicoRepository repository) {
        this.repository = repository;
    }

    public CatalogoServicoEntity cadastrar(String nome, String descricao, double valor,
                                            String tipo, String sistema,
                                            Integer validadeKm, Integer validadeMeses) {
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome do item de serviço é obrigatório.");
        if (valor < 0)
            throw new IllegalArgumentException("Valor não pode ser negativo.");
        return repository.salvar(new CatalogoServicoEntity(null, nome, descricao, valor,
            tipo, sistema, validadeKm, validadeMeses));
    }

    public List<CatalogoServicoEntity> listar() {
        return repository.listarTodos();
    }

    public List<CatalogoServicoEntity> listarPorOrcamento(long idOrcamento) {
        return repository.listarPorOrcamento(idOrcamento);
    }

    public void vincularAOrcamento(long idOrcamento, long idCatalogoServico, double valorCobrado) {
        repository.vincularAOrcamento(idOrcamento, idCatalogoServico, valorCobrado);
    }

    public java.util.Map<Long, Double> listarValoresItensPorOrcamento(long idOrcamento) {
        return repository.listarValoresItensPorOrcamento(idOrcamento);
    }

    public void removerItensDoOrcamento(long idOrcamento) {
        repository.removerItensDoOrcamento(idOrcamento);
    }

    public CatalogoServicoEntity buscarPorId(long id) {
        return repository.buscarPorId(id);
    }

    public void atualizar(CatalogoServicoEntity e) {
        if (e.getNome() == null || e.getNome().isBlank())
            throw new IllegalArgumentException("Nome do item de serviço é obrigatório.");
        repository.atualizar(e);
    }

    public List<Long> listarIdOrcamentosComItem(long idCatalogoServico) {
        return repository.listarIdOrcamentosComItem(idCatalogoServico);
    }

    public void vincularPecaAoCatalogo(long idCatalogoServico, long idPeca) {
        repository.vincularPecaAoCatalogo(idCatalogoServico, idPeca);
    }

    public List<Long> listarIdsPecasDoCatalogo(long idCatalogoServico) {
        return repository.listarIdsPecasDoCatalogo(idCatalogoServico);
    }

    public java.util.Map<Long, Long> listarLinksPecasDoCatalogo(long idCatalogoServico) {
        return repository.listarLinksPecasDoCatalogo(idCatalogoServico);
    }

    public void removerLinkPeca(long idCatalogoServicoPeca) {
        repository.removerLinkPeca(idCatalogoServicoPeca);
    }
}
