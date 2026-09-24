package br.com.oficina.atendimento;

import br.com.oficina.shared.config.Conexao;
import br.com.oficina.shared.config.Registro;
import br.com.oficina.shared.config.Tabela;
import java.util.ArrayList;
import java.util.List;

public class OrcamentoPecaRepository {
    private final Tabela tOrcamentoPeca;

    public OrcamentoPecaRepository(Conexao con) {
        this.tOrcamentoPeca = con.tabela("orcamento_peca");
    }

    public void vincular(long idOrcamento, long idPeca) {
        tOrcamentoPeca.inserir(new Registro()
            .set("id_orcamento", idOrcamento)
            .set("id_peca", idPeca));
    }

    public List<Long> listarIdsPecaPorOrcamento(long idOrcamento) {
        List<Long> out = new ArrayList<>();
        for (Registro r : tOrcamentoPeca.filtrar(x -> x.getLong("id_orcamento") == idOrcamento))
            out.add(r.getLong("id_peca"));
        return out;
    }

    public void removerPecasDoOrcamento(long idOrcamento) {
        for (Registro r : tOrcamentoPeca.filtrar(x -> x.getLong("id_orcamento") == idOrcamento))
            tOrcamentoPeca.remover(r.getLong("id_orcamento_peca"));
    }
}
