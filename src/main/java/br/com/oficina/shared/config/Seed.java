package br.com.oficina.shared.config;

import br.com.oficina.oficina.*;
import br.com.oficina.usuario.*;

/**
 * Inicializacao minima do banco na primeira execucao.
 * Cria apenas a Oficina e o usuario administrador (login padrao).
 * Os demais dados de exemplo (clientes, montadoras, modelos, veiculos)
 * sao lidos de /dados_seed.txt pelo SeedLoader.
 */
public final class Seed {
    private Seed() {}

    public static void popular(Conexao con) {
        UsuarioRepository usuarioRepo = new UsuarioRepository(con);
        if (!usuarioRepo.listarUsuarios().isEmpty()) return;

        // Oficina principal
        OficinaRepository oficinaRepo = new OficinaRepository(con);
        OficinaEntity of = oficinaRepo.salvar(new OficinaEntity(null,
            "Gear Gest Garage", "Av. Brasil, 1000", "(11) 3000-0000", "12.345.678/0001-90"));
        long idOficina = of.getIdOficina();

        // Unico usuario de sistema: administrador / login da oficina
        FuncionarioService funcSvc = new FuncionarioService(usuarioRepo);
        funcSvc.cadastrar("Administrador", "Gerente", "", "00000000000",
            "oficina@geargest.com", "123456", "(11) 90000-0000", idOficina);

        // Dados de exemplo carregados a partir do arquivo externo
        SeedLoader.carregar(con, idOficina);
    }
}
