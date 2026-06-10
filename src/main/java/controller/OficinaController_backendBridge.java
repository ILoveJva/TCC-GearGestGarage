package controller;

import br.com.oficina.shared.config.Conexao;
import br.com.oficina.oficina.*;
import br.com.oficina.usuario.*;
import br.com.oficina.veiculo.*;
import br.com.oficina.estoque.*;
import br.com.oficina.atendimento.*;

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

    OficinaController_backendBridge(Conexao con) {
        UsuarioRepository usuarioRepo = new UsuarioRepository(con);
        this.oficinaController = new br.com.oficina.oficina.OficinaController(
            new OficinaService(new OficinaRepository(con)));
        this.usuarioController = new UsuarioController(new UsuarioService(usuarioRepo));
        this.clienteController = new ClienteController(new ClienteService(usuarioRepo));
        this.funcionarioController = new FuncionarioController(new FuncionarioService(usuarioRepo));
        this.veiculoController = new VeiculoController(new VeiculoService(new VeiculoRepository(con), usuarioRepo));
        this.pecaController = new PecaController(new PecaService(new PecaRepository(con)));
        this.orcamentoController = new OrcamentoController(new OrcamentoService(new OrcamentoRepository(con)));
        this.servicoController = new ServicoController(new ServicoService(new ServicoRepository(con)));
    }
}
