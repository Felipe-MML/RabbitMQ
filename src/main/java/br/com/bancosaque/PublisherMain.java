package br.com.bancosaque;

import br.com.bancosaque.dto.SaqueRequestDTO;
import br.com.bancosaque.exception.ContaNaoEncontradaException;
import br.com.bancosaque.exception.MensageriaException;
import br.com.bancosaque.exception.SaldoInsuficienteException;
import br.com.bancosaque.exception.ValorSaqueInvalidoException;
import br.com.bancosaque.messaging.SaquePublisher;
import br.com.bancosaque.model.ContaCorrente;
import br.com.bancosaque.repository.ContaCorrenteRepository;
import br.com.bancosaque.repository.ContaCorrenteRepositoryImpl;
import br.com.bancosaque.service.SaqueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Scanner;

public class PublisherMain {

    private static final Logger log = LoggerFactory.getLogger(PublisherMain.class);

    public static void main(String[] args) {
        exibirBanner();

        ContaCorrenteRepository repository = new ContaCorrenteRepositoryImpl();

        try (SaquePublisher publisher = new SaquePublisher();
             Scanner scanner = new Scanner(System.in)) {

            SaqueService saqueService = new SaqueService(repository, publisher);
            boolean continuar = true;

            while (continuar) {
                exibirContasDisponiveis(repository);
                System.out.println();

                String idConta = solicitarIdConta(scanner);
                if (idConta.equalsIgnoreCase("sair")) {
                    log.info("Encerrando aplicação pelo usuário.");
                    break;
                }

                BigDecimal valorSaque = solicitarValorSaque(scanner);
                if (valorSaque == null) continue;

                try {
                    saqueService.realizarSaque(new SaqueRequestDTO(idConta, valorSaque));
                    System.out.println("\nSaque realizado com sucesso! Evento publicado na fila RabbitMQ.");

                } catch (ValorSaqueInvalidoException e) {
                    System.out.println("\nErro de validação: " + e.getMessage());
                } catch (ContaNaoEncontradaException e) {
                    System.out.println("\nConta não encontrada: " + e.getMessage());
                } catch (SaldoInsuficienteException e) {
                    System.out.println("\nSaldo insuficiente: " + e.getMessage());
                } catch (MensageriaException e) {
                    System.out.println("\nErro de mensageria: " + e.getMessage());
                    log.error("[PublisherMain] Erro de mensageria", e);
                }

                System.out.println("\nDeseja realizar outro saque? (s/n): ");
                String resposta = scanner.nextLine().trim();
                continuar = resposta.equalsIgnoreCase("s");
            }

        } catch (MensageriaException e) {
            log.error("[PublisherMain] Falha ao conectar ao RabbitMQ: {}", e.getMessage());
            System.out.println("\nNão foi possível conectar ao RabbitMQ. Verifique se o broker está em execução.");
            System.out.println("Comando: docker run -d --hostname rabbit-host --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management");
        }

        System.out.println("\nAplicação encerrada.");
    }

    private static String solicitarIdConta(Scanner scanner) {
        System.out.print("➤ ID da conta (ou 'sair' para encerrar): ");
        return scanner.nextLine().trim();
    }

    private static BigDecimal solicitarValorSaque(Scanner scanner) {
        System.out.print("➤ Valor do saque (ex: 150.00): R$ ");
        String valorStr = scanner.nextLine().trim().replace(",", ".");

        try {
            BigDecimal valor = new BigDecimal(valorStr);
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                System.out.println("O valor deve ser maior que zero.");
                return null;
            }
            return valor;
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido. Digite um número válido (ex: 150.00).");
            return null;
        }
    }

    private static void exibirContasDisponiveis(ContaCorrenteRepository repository) {
        System.out.println("\n" + "─".repeat(60));
        System.out.println(" SISTEMA DE SAQUE - BANCO JAVA MSG");
        System.out.println("─".repeat(60));
        System.out.println("  Contas disponíveis para teste:");
        System.out.println();

        String[] ids = {"CC-001", "CC-002", "CC-003", "CC-004"};
        for (String id : ids) {
            repository.buscarPorId(id).ifPresent(c ->
                    System.out.printf("  [%s] %-30s | Saldo: R$ %,.2f%n",
                            c.getId(), c.getNomeTitular(), c.getSaldo())
            );
        }
        System.out.println("─".repeat(60));
    }

    private static void exibirBanner() {
        System.out.println();
        System.out.println("─".repeat(60));
        System.out.println("       SIMULADOR DE SAQUE COM RABBITMQ                   ");
        System.out.println("     Java 17 + Maven + RabbitMQ Client + Gson                ");
        System.out.println("─".repeat(60));
    }
}
