package br.com.bancosaque.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

/**
 * Utilitário centralizado para serialização e desserialização JSON via Gson.
 * Inclui suporte a {@link LocalDateTime} com adaptadores customizados.
 */
public final class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    private JsonUtil() {
        throw new UnsupportedOperationException("Classe utilitária não deve ser instanciada.");
    }

    /**
     * Serializa um objeto para JSON.
     *
     * @param objeto objeto a serializar
     * @return JSON em formato String
     */
    public static String toJson(Object objeto) {
        return GSON.toJson(objeto);
    }

    /**
     * Desserializa uma String JSON para um objeto do tipo especificado.
     *
     * @param json  JSON em formato String
     * @param clazz classe de destino
     * @param <T>   tipo de retorno
     * @return instância do objeto desserializado
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }
}
