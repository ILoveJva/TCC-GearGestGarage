package br.com.oficina.atendimento;

import java.util.List;

public class CatalogoServicoController {
    private final CatalogoServicoService service;

    public CatalogoServicoController(CatalogoServicoService service) {
        this.service = service;
    }

    public CatalogoServicoEntity cadastrar(String nome, String descricao, double valor,
                                            String tipo, String sistema,
                                            Integer validadeKm, Integer validadeMeses) {
        return service.cadastrar(nome, descricao, valor, tipo, sistema, validadeKm, validadeMeses);
    }

    public List<CatalogoServicoEntity> todos() {
        return service.listar();
    }

    public List<CatalogoServicoEntity> listarPorOrcamento(long idOrcamento) {
        return service.listarPorOrcamento(idOrcamento);
    }

    public void vincularAOrcamento(long idOrcamento, long idCatalogoServico, double valorCobrado) {
        service.vincularAOrcamento(idOrcamento, idCatalogoServico, valorCobrado);
    }

    public java.util.Map<Long, Double> listarValoresItensPorOrcamento(long idOrcamento) {
        return service.listarValoresItensPorOrcamento(idOrcamento);
    }

    public void removerItensDoOrcamento(long idOrcamento) {
        service.removerItensDoOrcamento(idOrcamento);
    }

    public CatalogoServicoEntity buscarPorId(long id) {
        return service.buscarPorId(id);
    }

    public void atualizar(CatalogoServicoEntity e) {
        service.atualizar(e);
    }

    public List<Long> listarIdOrcamentosComItem(long idCatalogoServico) {
        return service.listarIdOrcamentosComItem(idCatalogoServico);
    }

    public void vincularPecaAoCatalogo(long idCatalogoServico, long idPeca) {
        service.vincularPecaAoCatalogo(idCatalogoServico, idPeca);
    }

    public List<Long> listarIdsPecasDoCatalogo(long idCatalogoServico) {
        return service.listarIdsPecasDoCatalogo(idCatalogoServico);
    }

    public java.util.Map<Long, Long> listarLinksPecasDoCatalogo(long idCatalogoServico) {
        return service.listarLinksPecasDoCatalogo(idCatalogoServico);
    }

    public void removerLinkPeca(long idCatalogoServicoPeca) {
        service.removerLinkPeca(idCatalogoServicoPeca);
    }
}
