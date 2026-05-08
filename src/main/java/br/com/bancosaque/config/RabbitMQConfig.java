package br.com.bancosaque.config;

public final class RabbitMQConfig {

    public static final String HOST = "localhost";
    public static final int PORT = 5672;
    public static final String USERNAME = "guest";
    public static final String PASSWORD = "guest";
    public static final String VIRTUAL_HOST = "/";

    public static final String EXCHANGE_NAME = "banco.exchange";
    public static final String EXCHANGE_TYPE = "direct";

    public static final String QUEUE_NAME = "fila.saque";

    public static final String ROUTING_KEY = "saque.realizado";

    private RabbitMQConfig() {
        throw new UnsupportedOperationException("Classe de configuração não deve ser instanciada.");
    }
}
