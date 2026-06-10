package br.com.oficina.shared.config;

import br.com.oficina.oficina.*;
import br.com.oficina.usuario.*;
import br.com.oficina.veiculo.*;
import br.com.oficina.estoque.*;
import br.com.oficina.atendimento.*;
import br.com.oficina.atendimento.dto.OrcamentoRequestDTO;

/** Popula o banco com dados de teste na primeira execucao (se estiver vazio). */
public final class Seed {
    private Seed() {}

    public static void popular(Conexao con) {
        UsuarioRepository usuarioRepo = new UsuarioRepository(con);
        if (!usuarioRepo.listarUsuarios().isEmpty()) return;

        // Oficina
        OficinaRepository oficinaRepo = new OficinaRepository(con);
        OficinaEntity of = oficinaRepo.salvar(new OficinaEntity(null,
            "Gear Gest Garage", "Av. Brasil, 1000", "(11) 3000-0000", "12.345.678/0001-90"));
        long idOficina = of.getIdOficina();

        // Funcionario (login) + Clientes
        FuncionarioService funcSvc = new FuncionarioService(usuarioRepo);
        var func = funcSvc.cadastrar("Administrador", "Gerente",
            "oficina@geargest.com", "123456", "(11) 90000-0000", idOficina);

        ClienteService cliSvc = new ClienteService(usuarioRepo);
        var joao  = cliSvc.cadastrar("Joao da Silva", "joao@email.com", "1234", "(11) 98888-7777", idOficina);
        var maria = cliSvc.cadastrar("Maria Souza",  "maria@email.com","1234", "(11) 97777-6666", idOficina);
        cliSvc.cadastrar("Carlos Pereira", "carlos@email.com", "1234", "(11) 96666-5555", idOficina);

        // Catalogo
        VeiculoService veicSvc = new VeiculoService(new VeiculoRepository(con), usuarioRepo);
        var vw   = veicSvc.cadastrarMontadora("Volkswagen", "Alemanha");
        var fiat = veicSvc.cadastrarMontadora("Fiat", "Italia");
        var gol  = veicSvc.cadastrarModelo("Gol", 2020, vw.getIdMontadora());
        var polo = veicSvc.cadastrarModelo("Polo", 2022, vw.getIdMontadora());
        var uno  = veicSvc.cadastrarModelo("Uno", 2018, fiat.getIdMontadora());

        // Veiculos (id_cliente referencia a tabela cliente)
        var v1 = veicSvc.cadastrar("Hatch", "ABC-1D23", vw.getIdMontadora(), gol.getIdModelo(), joao.getIdCliente());
        veicSvc.cadastrar("Sedan", "XYZ-9K88", vw.getIdMontadora(), polo.getIdModelo(), maria.getIdCliente());
        veicSvc.cadastrar("Hatch", "QAZ-2W34", fiat.getIdMontadora(), uno.getIdModelo(), joao.getIdCliente());

        // Pecas (ligadas a fabricante e veiculo)
        PecaService pecaSvc = new PecaService(new PecaRepository(con));
        var bosch = pecaSvc.cadastrarFabricante("Bosch", "Alemanha");
        var pastilha = pecaSvc.cadastrar("Pastilha de Freio", "12 meses", "30000 km",
            bosch.getIdFabricantePeca(), v1.getIdVeiculo());
        pecaSvc.cadastrar("Oleo 5W30", "6 meses", "10000 km", bosch.getIdFabricantePeca(), v1.getIdVeiculo());

        // Orcamento + Servico (O.S.) de exemplo
        OrcamentoService orcSvc = new OrcamentoService(new OrcamentoRepository(con));
        var orc = orcSvc.criar(new OrcamentoRequestDTO(450.00, pastilha.getIdPeca(),
            v1.getIdVeiculo(), joao.getIdCliente(), func.getIdFuncionario()));

        ServicoService servSvc = new ServicoService(new ServicoRepository(con));
        var serv = servSvc.abrir("Revisao + Freios", "Corretiva", "2026-06-09", 45000,
            v1.getIdVeiculo(), idOficina, orc.getIdOrcamento());
        servSvc.adicionarItem(serv.getIdServico(), "Troca de pastilhas de freio",
            pastilha.getIdPeca(), func.getIdFuncionario());
        servSvc.adicionarItem(serv.getIdServico(), "Troca de oleo",
            pastilha.getIdPeca(), func.getIdFuncionario());
    }
}
