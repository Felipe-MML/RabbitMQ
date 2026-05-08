package br.com.bancosaque.messaging;

import br.com.bancosaque.config.RabbitMQConfig;
import br.com.bancosaque.config.RabbitMQConnectionFactory;
import br.com.bancosaque.dto.SaqueEventoDTO;
import br.com.bancosaque.exception.MensageriaException;
import br.com.bancosaque.service.NotificacaoEmailService;
import br.com.bancosaque.util.JsonUtil;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;


public class SaqueConsumer implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(SaqueConsumer.class);

    private final Connection connection;
    private final Channel channel;
    private final NotificacaoEmailService notificacaoEmailService;

    public SaqueConsumer() {
        this.notificacaoEmailService = new NotificacaoEmailService();
        try {
            this.connection = RabbitMQConnectionFactory.criarConexao();
            this.channel = connection.createChannel();
            RabbitMQConnectionFactory.configurarInfraestrutura(channel);

            channel.basicQos(1);

        } catch (IOException | TimeoutException e) {
            throw new MensageriaException("Falha ao inicializar o Consumer RabbitMQ.", e);
        }
    }


    public void iniciarConsumo() {
        try {
            log.info("[Consumer] Aguardando mensagens na fila '{}'...", RabbitMQConfig.QUEUE_NAME);
            log.info("[Consumer] Pressione CTRL+C para encerrar.");

            DeliverCallback onMensagemRecebida = (consumerTag, delivery) -> {
                String mensagemJson = new String(delivery.getBody(), StandardCharsets.UTF_8);
                log.info("[Consumer] Mensagem recebida. Processando...");
                log.debug("[Consumer] Payload bruto: {}", mensagemJson);

                try {
                    SaqueEventoDTO evento = JsonUtil.fromJson(mensagemJson, SaqueEventoDTO.class);
                    notificacaoEmailService.enviarNotificacao(evento);

                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    log.info("[Consumer] Mensagem processada e confirmada (ACK).");

                } catch (Exception e) {
                    log.error("[Consumer] Erro ao processar mensagem: {}", e.getMessage(), e);
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                    log.warn("[Consumer] Mensagem devolvida à fila (NACK + requeue).");
                }
            };

            CancelCallback onCancelamento = consumerTag ->
                    log.warn("[Consumer] Consumer cancelado: {}", consumerTag);

            channel.basicConsume(
                    RabbitMQConfig.QUEUE_NAME,
                    false,
                    onMensagemRecebida,
                    onCancelamento
            );

        } catch (IOException e) {
            throw new MensageriaException("Falha ao iniciar consumo da fila RabbitMQ.", e);
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
            log.info("[Consumer] Conexão RabbitMQ encerrada.");
        } catch (IOException | TimeoutException e) {
            log.warn("[Consumer] Erro ao fechar conexão RabbitMQ: {}", e.getMessage());
        }
    }
}
