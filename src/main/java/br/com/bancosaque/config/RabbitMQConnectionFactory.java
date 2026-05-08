package br.com.bancosaque.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Factory responsável por criar e configurar conexões com o RabbitMQ.
 * Encapsula a lógica de criação de canal, exchange e fila,
 * garantindo que a infraestrutura esteja pronta antes de publicar ou consumir.
 */
public class RabbitMQConnectionFactory {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConnectionFactory.class);

    /**
     * Cria uma nova conexão com o RabbitMQ usando as configurações definidas em {@link RabbitMQConfig}.
     *
     * @return {@link Connection} ativa com o broker
     * @throws IOException      em caso de falha de I/O na conexão
     * @throws TimeoutException em caso de timeout ao conectar
     */
    public static Connection criarConexao() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RabbitMQConfig.HOST);
        factory.setPort(RabbitMQConfig.PORT);
        factory.setUsername(RabbitMQConfig.USERNAME);
        factory.setPassword(RabbitMQConfig.PASSWORD);
        factory.setVirtualHost(RabbitMQConfig.VIRTUAL_HOST);

        log.info("[RabbitMQ] Conectando em {}:{}...", RabbitMQConfig.HOST, RabbitMQConfig.PORT);
        Connection connection = factory.newConnection();
        log.info("[RabbitMQ] Conexão estabelecida com sucesso.");
        return connection;
    }

    /**
     * Declara o exchange e a fila no RabbitMQ e faz o binding entre eles.
     * Idempotente: se exchange/fila já existirem com as mesmas configurações, não lança erro.
     *
     * @param channel Canal RabbitMQ ativo
     * @throws IOException em caso de falha de comunicação
     */
    public static void configurarInfraestrutura(Channel channel) throws IOException {
        // Declara o exchange do tipo "direct" como durável
        channel.exchangeDeclare(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.EXCHANGE_TYPE,
                true   // durable
        );

        // Declara a fila como durável (sobrevive a restart do broker)
        channel.queueDeclare(
                RabbitMQConfig.QUEUE_NAME,
                true,   // durable
                false,  // exclusive
                false,  // autoDelete
                null    // arguments
        );

        // Faz o binding: fila -> exchange usando a routing key
        channel.queueBind(
                RabbitMQConfig.QUEUE_NAME,
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY
        );

        log.info("[RabbitMQ] Infraestrutura configurada: exchange='{}', fila='{}', routingKey='{}'",
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.QUEUE_NAME,
                RabbitMQConfig.ROUTING_KEY);
    }
}
