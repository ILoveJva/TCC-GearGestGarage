package database;

import controller.OficinaController;
import model.Montadora;
import java.util.ArrayList;
import java.util.List;

/**
 * Acesso ao catalogo de montadoras (com seus modelos), usado pela tela de
 * cadastro de veiculo. Busca os dados reais via fachada OficinaController.
 */
public final class DB_Montadora {
    private DB_Montadora() {}

    public static List<Montadora> listarTodas() {
        OficinaController c = OficinaController.instancia;
        if (c == null) return new ArrayList<>();
        return c.montadorasComModelos();
    }
}
