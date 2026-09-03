package br.com.oficina.financeiro;

import br.com.oficina.shared.config.*;
import java.util.ArrayList;
import java.util.List;

public class DespesaRepository {
    private final Tabela tDespesa;

    public DespesaRepository(Conexao con) {
        this.tDespesa = con.tabela("despesa");
    }

    private DespesaEntity map(Registro r) {
        return new DespesaEntity(
            r.getLong("id_despesa"),
            r.get("descricao"),
            r.get("categoria"),
            r.getDouble("valor"),
            r.get("data_despesa"),
            r.get("forma_pagamento"),
            r.get("observacao"),
            r.getLong("id_oficina"));
    }

    public DespesaEntity salvar(DespesaEntity d) {
        long id = tDespesa.inserir(new Registro()
            .set("descricao", d.getDescricao())
            .set("categoria", d.getCategoria())
            .set("valor", d.getValor())
            .set("data_despesa", d.getDataDespesa())
            .set("forma_pagamento", d.getFormaPagamento())
            .set("observacao", d.getObservacao())
            .set("id_oficina", d.getIdOficina()));
        d.setIdDespesa(id);
        return d;
    }

    public DespesaEntity buscarPorId(long id) {
        Registro r = tDespesa.buscarPorId(id);
        return r == null ? null : map(r);
    }

    public List<DespesaEntity> listarTodas() {
        List<DespesaEntity> out = new ArrayList<>();
        for (Registro r : tDespesa.registros()) out.add(map(r));
        return out;
    }

    public boolean remover(long id) {
        return tDespesa.remover(id);
    }
}
