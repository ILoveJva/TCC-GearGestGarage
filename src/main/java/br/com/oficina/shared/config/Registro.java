package br.com.oficina.shared.config;

import java.util.LinkedHashMap;
import java.util.Map;

/** Uma linha (registro) de uma tabela: pares coluna -> valor. */
public class Registro {
    private final Map<String, String> campos = new LinkedHashMap<>();

    public Registro set(String coluna, Object valor) {
        campos.put(coluna, valor == null ? "" : String.valueOf(valor));
        return this;
    }

    public String get(String coluna) { return campos.getOrDefault(coluna, ""); }

    public long getLong(String coluna) {
        String v = get(coluna);
        return v.isEmpty() ? 0L : Long.parseLong(v);
    }

    public int getInt(String coluna) {
        String v = get(coluna);
        return v.isEmpty() ? 0 : Integer.parseInt(v);
    }

    public double getDouble(String coluna) {
        String v = get(coluna);
        return v.isEmpty() ? 0.0 : Double.parseDouble(v);
    }

    public Map<String, String> campos() { return campos; }
}
