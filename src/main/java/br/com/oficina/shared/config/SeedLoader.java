package br.com.oficina.shared.config;

import br.com.oficina.usuario.ClienteService;
import br.com.oficina.usuario.UsuarioRepository;
import br.com.oficina.veiculo.VeiculoRepository;
import br.com.oficina.veiculo.VeiculoService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Carrega dados de exemplo do arquivo /dados_seed.txt (classpath).
 * Chamado pelo Seed apenas na primeira inicializacao.
 *
 * Formato:
 *   MONTADORA | nome | pais_origem
 *   MODELO    | nome | ano | nome_da_montadora | tipo
 *   CLIENTE   | nome | cpf | email | senha | telefone
 *   VEICULO   | placa | nome_do_modelo | email_do_cliente
 */
public final class SeedLoader {
    private SeedLoader() {}

    public static void carregar(Conexao con, long idOficina) {
        InputStream is = SeedLoader.class.getResourceAsStream("/dados_seed.txt");
        if (is == null) {
            return;
        }

        UsuarioRepository usuarioRepo = new UsuarioRepository(con);
        VeiculoService veicSvc = new VeiculoService(new VeiculoRepository(con), usuarioRepo);
        ClienteService cliSvc = new ClienteService(usuarioRepo);

        Map<String, Long> montadoraIds = new HashMap<>();
        Map<String, long[]> modeloInfo  = new HashMap<>(); // nome -> [idModelo, idMontadora]
        Map<String, Long> clienteIds   = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#")) continue;

                String[] p = linha.split("\\|");
                if (p.length < 2) continue;

                switch (p[0].trim().toUpperCase()) {

                    case "MONTADORA": {
                        if (p.length < 3) break;
                        String nome = p[1].trim();
                        String pais = p[2].trim();
                        var m = veicSvc.cadastrarMontadora(nome, pais);
                        montadoraIds.put(nome, m.getIdMontadora());
                        break;
                    }

                    case "MODELO": {
                        // formato: MODELO | nome | ano | montadora | tipo
                        if (p.length < 5) break;
                        String nome     = p[1].trim();
                        int    ano      = Integer.parseInt(p[2].trim());
                        String montNome = p[3].trim();
                        String tipo     = p[4].trim();
                        Long idMont = montadoraIds.get(montNome);
                        if (idMont == null) {
                            System.err.println("SeedLoader: montadora '" + montNome + "' nao encontrada para modelo '" + nome + "'");
                            break;
                        }
                        var mod = veicSvc.cadastrarModelo(nome, ano, tipo, idMont);
                        modeloInfo.put(nome, new long[]{mod.getIdModelo(), idMont});
                        break;
                    }

                    case "CLIENTE": {
                        // formato: CLIENTE | nome | cpf | email | senha | telefone
                        if (p.length < 6) break;
                        String nome  = p[1].trim();
                        String cpf   = p[2].trim();
                        String email = p[3].trim();
                        String senha = p[4].trim();
                        String tel   = p[5].trim();
                        var c = cliSvc.cadastrar(nome, cpf, email, senha, tel, idOficina);
                        clienteIds.put(email, c.getIdCliente());
                        break;
                    }

                    case "VEICULO": {
                        // formato: VEICULO | placa | nome_do_modelo | email_do_cliente
                        if (p.length < 4) break;
                        String placa      = p[1].trim();
                        String modeloNome = p[2].trim();
                        String cliEmail   = p[3].trim();
                        long[] info = modeloInfo.get(modeloNome);
                        Long idCli = clienteIds.get(cliEmail);
                        if (info == null || idCli == null) {
                            System.err.println("SeedLoader: dados insuficientes para veiculo placa '" + placa + "'");
                            break;
                        }
                        veicSvc.cadastrar(placa, info[1], info[0], idCli);
                        break;
                    }

                    default:
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("SeedLoader: erro ao carregar dados_seed.txt — " + e.getMessage());
        }
    }
}
