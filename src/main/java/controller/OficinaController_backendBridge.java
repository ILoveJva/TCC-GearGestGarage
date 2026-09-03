package controller;

import br.com.oficina.shared.config.Conexao;
import br.com.oficina.oficina.*;
import br.com.oficina.usuario.*;
import br.com.oficina.veiculo.*;
import br.com.oficina.estoque.*;
import br.com.oficina.atendimento.*;
import br.com.oficina.financeiro.*;

/** Wiring dos controllers do backend (br.com.oficina) a partir de uma Conexao. */
class OficinaController_backendBridge {

    final br.com.oficina.oficina.OficinaController oficinaController;
    final UsuarioController usuarioController;
    final ClienteController clienteController;
    final FuncionarioController funcionarioController;
    final VeiculoController veiculoController;
    final PecaController pecaController;
    final OrcamentoController orcamentoController;
    final ServicoController servicoController;
    final br.com.oficina.atendimento.TipoServicoController tipoServicoController;
    final br.com.oficina.atendimento.CatalogoServicoController catalogoServicoController;
    final br.com.oficina.atendimento.OrcamentoPecaRepository orcamentoPecaRepository;
    final DespesaController despesaController;
    final EstoqueController estoqueController;

    OficinaController_backendBridge(Conexao con) {
        UsuarioRepository usuarioRepo = new UsuarioRepository(con);
        this.oficinaController = new br.com.oficina.oficina.OficinaController(
            new OficinaService(new OficinaRepository(con)));
        this.usuarioController = new UsuarioController(new UsuarioService(usuarioRepo));
        this.clienteController = new ClienteController(new ClienteService(usuarioRepo));
        this.funcionarioController = new FuncionarioController(new FuncionarioService(usuarioRepo));
        this.veiculoController = new VeiculoController(new VeiculoService(new VeiculoRepository(con), usuarioRepo));
        PecaRepository pecaRepo = new PecaRepository(con);
        this.pecaController = new PecaController(new PecaService(pecaRepo));
        this.orcamentoController = new OrcamentoController(new OrcamentoService(new OrcamentoRepository(con)));
        this.servicoController = new ServicoController(new ServicoService(new ServicoRepository(con)));
        this.tipoServicoController = new br.com.oficina.atendimento.TipoServicoController(
            new br.com.oficina.atendimento.TipoServicoService(
                new br.com.oficina.atendimento.TipoServicoRepository(con)));
        this.catalogoServicoController = new br.com.oficina.atendimento.CatalogoServicoController(
            new br.com.oficina.atendimento.CatalogoServicoService(
                new br.com.oficina.atendimento.CatalogoServicoRepository(con)));
        this.orcamentoPecaRepository = new br.com.oficina.atendimento.OrcamentoPecaRepository(con);
        this.despesaController = new DespesaController(new DespesaService(new DespesaRepository(con)));
        this.estoqueController = new EstoqueController(
            new EstoqueService(pecaRepo, new MovimentacaoEstoqueRepository(con)));
    }
}
