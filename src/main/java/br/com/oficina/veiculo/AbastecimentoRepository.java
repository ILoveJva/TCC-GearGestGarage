package br.com.oficina.veiculo;

import br.com.oficina.shared.config.*;
import java.util.ArrayList;
import java.util.List;

public class AbastecimentoRepository {
    private final Tabela tAbastecimento;

    public AbastecimentoRepository(Conexao con) {
        this.tAbastecimento = con.tabela("abastecimento");
    }

    private AbastecimentoEntity map(Registro r) {
        return new AbastecimentoEntity(
            r.getLong("id_abastecimento"),
            r.getLong("id_veiculo"),
            r.get("data_abastecimento"),
            r.getDouble("valor"),
            r.getDouble("litros"),
            r.get("tipo_combustivel"),
            r.getInt("quilometragem"));
    }

    public AbastecimentoEntity salvar(AbastecimentoEntity a) {
        long id = tAbastecimento.inserir(new Registro()
            .set("id_veiculo", a.getIdVeiculo())
            .set("data_abastecimento", a.getDataAbastecimento())
            .set("valor", a.getValor())
            .set("litros", a.getLitros())
            .set("tipo_combustivel", a.getTipoCombustivel())
            .set("quilometragem", a.getQuilometragem()));
        a.setIdAbastecimento(id);
        return a;
    }

    /** Histórico de um veículo, mais recente primeiro. */
    public List<AbastecimentoEntity> listarPorVeiculo(long idVeiculo) {
        List<AbastecimentoEntity> out = new ArrayList<>();
        for (Registro r : tAbastecimento.filtrar(reg -> reg.getLong("id_veiculo") == idVeiculo))
            out.add(map(r));
        out.sort((x, y) -> {
            int porData = y.getDataAbastecimento().compareTo(x.getDataAbastecimento());
            if (porData != 0) return porData;
            return Long.compare(y.getIdAbastecimento(), x.getIdAbastecimento());
        });
        return out;
    }

    public boolean remover(long id) {
        return tAbastecimento.remover(id);
    }
}
