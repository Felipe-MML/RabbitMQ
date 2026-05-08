package br.com.bancosaque;

import br.com.bancosaque.exception.MensageriaException;
import br.com.bancosaque.messaging.SaqueConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerMain {

    private static final Logger log = LoggerFactory.getLogger(ConsumerMain.class);

    public static void main(String[] args) {
        exibirBanner();

        SaqueConsumer consumer = null;

        try {
            consumer = new SaqueConsumer();

            final SaqueConsumer consumerFinal = consumer;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("[ConsumerMain] Sinal de desligamento recebido. Encerrando consumer...");
                consumerFinal.close();
                System.out.println("\nConsumer encerrado. Até logo!");
            }));

            consumer.iniciarConsumo();
            Thread.currentThread().join();

        } catch (MensageriaException e) {
            log.error("[ConsumerMain] Falha ao conectar ao RabbitMQ: {}", e.getMessage());
            System.out.println("\nNão foi possível conectar ao RabbitMQ.");
            System.out.println("Verifique se o Docker está em execução:");
            System.out.println("docker run -d --hostname rabbit-host --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management");

        } catch (InterruptedException e) {
            log.info("[ConsumerMain] Thread principal interrompida. Encerrando...");
            Thread.currentThread().interrupt();
        }
    }

    private static void exibirBanner() {
        System.out.println();
        System.out.println("─".repeat(60));
        System.out.println("       CONSUMER DE SAQUE - BANCO JAVA MSG                ");
        System.out.println("     Aguardando eventos na fila RabbitMQ...                  ");
        System.out.println("─".repeat(60));
        System.out.println();
    }
}
