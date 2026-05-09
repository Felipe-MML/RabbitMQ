package br.com.bancosaque;

import br.com.bancosaque.model.EventoSaque;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class ConsumerMain {

    private final static String QUEUE_NAME = "fila_saques";
    private final static String HOST = "localhost";
    private final static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        System.out.println("=== TERMINAL DE NOTIFICAÇÕES (SUBSCRIBER) ===");
        System.out.println("Aguardando mensagens... (Pressione CTRL+C para sair)");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String json = new String(delivery.getBody(), "UTF-8");
                try {
                    EventoSaque evento = mapper.readValue(json, EventoSaque.class);
                    processarNotificacao(evento);
                } catch (Exception e) {
                    System.err.println("Erro ao processar mensagem: " + e.getMessage());
                }
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });

        } catch (Exception e) {
            System.err.println("Erro no Subscriber: " + e.getMessage());
        }
    }

    private static void processarNotificacao(EventoSaque evento) {
        System.out.println("\n--------------------------------------------------");
        System.out.println("📧 NOVO E-MAIL ENVIADO");
        System.out.println("Para: " + evento.getEmail());
        System.out.println("Assunto: Saque Confirmado - Conta " + evento.getNumeroConta());
        System.out.println("--------------------------------------------------");
        System.out.println("Prezado(a) " + evento.getTitular() + ",");
        System.out.println("Confirmamos um saque de R$ " + evento.getValorSaque());
        System.out.println("Data: " + evento.getDataHora());
        System.out.println("Saldo Disponível: R$ " + evento.getSaldoRestante());
        System.out.println("--------------------------------------------------\n");
    }
}
