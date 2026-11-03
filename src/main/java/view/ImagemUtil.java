package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/** Carrega uma imagem de um caminho local (fora do classpath) redimensionada, para avatares e fotos de veículo. */
public final class ImagemUtil {

    private ImagemUtil() {}

    /** Devolve null se o caminho for vazio ou o arquivo não puder ser lido como imagem. */
    public static ImageIcon carregar(String caminho, int largura, int altura) {
        if (caminho == null || caminho.isBlank()) return null;
        try {
            File arquivo = new File(caminho);
            if (!arquivo.isFile()) return null;
            BufferedImage img = javax.imageio.ImageIO.read(arquivo);
            if (img == null) return null;
            Image escalada = img.getScaledInstance(largura, altura, Image.SCALE_SMOOTH);
            return new ImageIcon(escalada);
        } catch (Exception e) {
            return null;
        }
    }
}
