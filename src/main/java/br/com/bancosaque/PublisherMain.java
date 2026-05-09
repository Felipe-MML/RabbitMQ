package br.com.bancosaque;

import br.com.bancosaque.model.ContaCorrente;
import br.com.bancosaque.model.EventoSaque;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.math.BigDecimal;
import java.util.Scanner;

public class PublisherMain {

    private final static String QUEUE_NAME = "fila_saques";
    private final static String HOST = "localhost";
    private final static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        System.out.println("=== TERMINAL DE OPERAÇÕES BANCÁRIAS (PUBLISHER) ===");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        ContaCorrente conta = new ContaCorrente("CC-001", "Felipe", "felipe@email.com", new BigDecimal("2000.00"));
        Scanner scanner = new Scanner(System.in);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            while (true) {
                System.out.println("\nSaldo Atual: R$ " + conta.getSaldo());
                System.out.print("Digite o valor para saque (ou 'sair'): ");
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("sair")) break;

                try {
                    BigDecimal valor = new BigDecimal(input);
                    conta.sacar(valor);

                    EventoSaque evento = new EventoSaque(
                        conta.getNumeroConta(),
                        conta.getTitular(),
                        conta.getEmail(),
                        valor,
                        conta.getSaldo()
                    );
                    String json = mapper.writeValueAsString(evento);

                    channel.basicPublish("", QUEUE_NAME, null, json.getBytes("UTF-8"));

                    System.out.println(">>> Saque realizado! Evento de notificação enviado para a fila.");
                } catch (NumberFormatException e) {
                    System.out.println("Erro: Valor inválido.");
                } catch (RuntimeException e) {
                    System.out.println("Erro: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Erro de conexão com RabbitMQ: " + e.getMessage());
        } finally {
            scanner.close();
            System.out.println("Encerrando Terminal de Operações...");
        }
    }
}
