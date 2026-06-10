package br.com.oficina.shared.config;

/**
 * Configuracao de seguranca (placeholder em Java puro).
 * Centraliza politicas simples de autenticacao/senha. Em um projeto Spring,
 * aqui ficaria a SecurityFilterChain, PasswordEncoder, etc.
 */
public final class SecurityConfig {

    private SecurityConfig() {}

    /** "Hash" didatico de senha (NAO usar em producao). */
    public static String codificarSenha(String senhaPura) {
        return Integer.toHexString(senhaPura.hashCode());
    }

    public static boolean senhaConfere(String senhaPura, String armazenada) {
        // suporta tanto senha em texto (seed antigo) quanto codificada
        return armazenada.equals(senhaPura) || armazenada.equals(codificarSenha(senhaPura));
    }
}
