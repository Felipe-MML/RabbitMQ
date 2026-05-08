package br.com.bancosaque.messaging;

import br.com.bancosaque.config.RabbitMQConfig;
import br.com.bancosaque.config.RabbitMQConnectionFactory;
import br.com.bancosaque.dto.SaqueEventoDTO;
import br.com.bancosaque.exception.MensageriaException;
import br.com.bancosaque.util.JsonUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * Publisher responsável por publicar eventos de saque na fila RabbitMQ.
 * Serializa o {@link SaqueEventoDTO} para JSON e envia ao exchange configurado.
 *
 * Implementa {@link AutoCloseable} para garantir fechamento correto dos recursos.
 */
public class SaquePublisher implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(SaquePublisher.class);

    private final Connection connection;
    private final Channel channel;

    public SaquePublisher() {
        try {
            this.connection = RabbitMQConnectionFactory.criarConexao();
            this.channel = connection.createChannel();
            RabbitMQConnectionFactory.configurarInfraestrutura(channel);
        } catch (IOException | TimeoutException e) {
            throw new MensageriaException("Falha ao inicializar o Publisher RabbitMQ.", e);
        }
    }

    /**
     * Publica um evento de saque na fila RabbitMQ.
     *
     * @param evento DTO contendo os dados do saque realizado
     * @throws MensageriaException em caso de falha ao publicar
     */
    public void publicar(SaqueEventoDTO evento) {
        try {
            String mensagemJson = JsonUtil.toJson(evento);
            byte[] corpoMensagem = mensagemJson.getBytes(StandardCharsets.UTF_8);

            // PERSISTENT garante que a mensagem sobrevive a restart do broker
            channel.basicPublish(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY,
                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                    corpoMensagem
            );

            log.info("[Publisher] ✅ Evento publicado na fila '{}' com routing key '{}'.",
                    RabbitMQConfig.QUEUE_NAME, RabbitMQConfig.ROUTING_KEY);
            log.debug("[Publisher] Payload: {}", mensagemJson);

        } catch (IOException e) {
            throw new MensageriaException("Falha ao publicar evento de saque na fila RabbitMQ.", e);
        }
    }

    @Override
    public void close() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
            log.info("[Publisher] Conexão RabbitMQ encerrada.");
        } catch (IOException | TimeoutException e) {
            log.warn("[Publisher] Erro ao fechar conexão RabbitMQ: {}", e.getMessage());
        }
    }
}
