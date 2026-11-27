package view.design;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class GerenciadorDeIcones {

    private static final Map<String, ImageIcon> cacheIcones = new HashMap<>();

    private GerenciadorDeIcones() {}

    public static ImageIcon obterIcone(String caminho, int larguraAlvo, int alturaAlvo) {
        String chave = caminho + "_" + larguraAlvo + "_" + alturaAlvo;

        if (cacheIcones.containsKey(chave)) {
            return cacheIcones.get(chave);
        }

        URL url = GerenciadorDeIcones.class.getResource(caminho);
        if (url == null) {
            String caminhoLimpo = caminho.startsWith("/") ? caminho.substring(1) : caminho;
            url = Thread.currentThread().getContextClassLoader().getResource(caminhoLimpo);
        }

        if (url != null) {
            ImageIcon imagemOriginal = new ImageIcon(url);
            Image imgEscalada = imagemOriginal.getImage().getScaledInstance(larguraAlvo, alturaAlvo, Image.SCALE_SMOOTH);
            ImageIcon iconeFinal = new ImageIcon(imgEscalada);
            cacheIcones.put(chave, iconeFinal);
            return iconeFinal;
        }

        return null;
    }
}