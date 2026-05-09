# Saque Mensageria — Fluxo Bancário Assíncrono com RabbitMQ

Aplicação Java puro (sem Spring Boot) que simula um fluxo bancário assíncrono de **saque em conta corrente** integrado ao **RabbitMQ**.

---

## Arquitetura

```
┌─────────────────────────────────────────────────────┐
│                  PublisherMain                       │
│  (Menu Console → Saque → Validação → Débito)        │
│                      │                              │
│               SaquePublisher                        │
│          (Publica evento JSON no RabbitMQ)          │
└──────────────────────┬──────────────────────────────┘
                       │ fila.saque (AMQP)
                       ▼
┌─────────────────────────────────────────────────────┐
│                  ConsumerMain                        │
│              SaqueConsumer                          │
│    (Consome mensagem → Desserializa → Email)        │
│           NotificacaoEmailService                   │
│           (Simula envio de e-mail no console)       │
└─────────────────────────────────────────────────────┘
```

---

## Estrutura de Pacotes

```
src/main/java/br/com/bancosaque/
├── config/
│   ├── RabbitMQConfig.java              # Constantes (host, porta, exchange, fila, routing key)
│   └── RabbitMQConnectionFactory.java   # Criação de conexão e declaração de infraestrutura
├── model/
│   └── ContaCorrente.java               # Entidade de domínio
├── dto/
│   ├── SaqueRequestDTO.java             # Dados da requisição de saque
│   └── SaqueEventoDTO.java              # Evento publicado na fila
├── repository/
│   ├── ContaCorrenteRepository.java     # Interface do repositório
│   └── ContaCorrenteRepositoryImpl.java # Implementação em memória (mockada)
├── service/
│   ├── SaqueService.java                # Orquestrador do fluxo de saque
│   └── NotificacaoEmailService.java     # Simulação de envio de e-mail
├── messaging/
│   ├── SaquePublisher.java              # Publica eventos no RabbitMQ
│   └── SaqueConsumer.java               # Consome eventos do RabbitMQ
├── exception/
│   ├── ContaNaoEncontradaException.java
│   ├── SaldoInsuficienteException.java
│   ├── ValorSaqueInvalidoException.java
│   └── MensageriaException.java
├── util/
│   ├── JsonUtil.java                    # Wrapper do Gson (serialização/desserialização)
│   └── LocalDateTimeAdapter.java        # Adaptador Gson para LocalDateTime
├── PublisherMain.java                   # Entry point do Publisher
└── ConsumerMain.java                    # Entry point do Consumer
```

---

## 1. Subindo o RabbitMQ com Docker

Execute o comando abaixo para iniciar o RabbitMQ com a interface de gerenciamento:

```bash
docker run -d \
  --hostname rabbit-host \
  --name rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:3-management
```

Acesse o painel de gerenciamento:

- **URL:** [http://localhost:15672](http://localhost:15672)
- **Usuário:** `guest`
- **Senha:** `guest`

---

## 2. Compilando o Projeto

Na raiz do projeto, execute:

```bash
mvn clean package -DskipTests
```

Isso gera dois fat JARs na pasta `target/`:
- `saque-publisher-full.jar` — Publisher (realiza saques)
- `saque-consumer-full.jar` — Consumer (recebe notificações)

---

## 3. Executando o Consumer

**Abra um terminal** e execute o Consumer *antes* do Publisher:

```bash
java -jar target/saque-consumer-full.jar
```

O consumer ficará aguardando mensagens:
```
╔══════════════════════════════════════════════════════════════╗
║     CONSUMER DE SAQUE - BANCO JAVA MSG                       ║
║     Aguardando eventos na fila RabbitMQ...                   ║
╚══════════════════════════════════════════════════════════════╝

[Consumer] Aguardando mensagens na fila 'fila.saque'...
[Consumer] Pressione CTRL+C para encerrar.
```

---

## 4. Executando o Publisher

**Abra outro terminal** e execute o Publisher:

```bash
java -jar target/saque-publisher-full.jar
```

O menu interativo será exibido com as contas disponíveis:

```
╔══════════════════════════════════════════════════════════════╗
║       SIMULADOR DE SAQUE COM RABBITMQ                        ║
║     Java 17 + Maven + RabbitMQ Client + Gson                 ║
╚══════════════════════════════════════════════════════════════╝

────────────────────────────────────────────────────────────
     SISTEMA DE SAQUE - BANCO JAVA MSG
────────────────────────────────────────────────────────────
  Contas disponíveis para teste:

  [CC-001] Ana Beatriz Silva            | Saldo: R$ 5.000,00
  [CC-002] Carlos Eduardo Mendes        | Saldo: R$ 12.500,50
  [CC-003] Mariana Ferreira Costa       | Saldo: R$ 890,75
  [CC-004] Roberto Alves Neto          | Saldo: R$ 30.000,00
────────────────────────────────────────────────────────────

➤ ID da conta (ou 'sair' para encerrar): CC-001
➤ Valor do saque (ex: 150.00): R$ 500.00
```

---

## 5. Testando o Fluxo Completo

### Passo a passo:

1. **Inicie o RabbitMQ** via Docker (passo 1)
2. **Abra o Terminal 1** → Execute o Consumer (passo 3)
3. **Abra o Terminal 2** → Execute o Publisher (passo 4)
4. No Publisher, informe o ID da conta e o valor do saque
5. Observe no **Terminal 1** (Consumer) a simulação do e-mail

### Resultado esperado no Consumer:

```
╔══════════════════════════════════════════════════════════════╗
║            NOTIFICAÇÃO DE SAQUE - BANCO JAVA MSG             ║
╠══════════════════════════════════════════════════════════════╣
║  Para:          ana.beatriz@email.com                        ║
║  Destinatário:  Ana Beatriz Silva                            ║
╠══════════════════════════════════════════════════════════════╣
║     Detalhes da Transação                                    ║
╠══════════════════════════════════════════════════════════════╣
║  Conta:         CC-001                                       ║
║  Valor Sacado:  R$ 500,00                                    ║
║  Saldo Atual:   R$ 4.500,00                                  ║
║  Data/Hora:     08/05/2025 às 14:32:11                       ║
╠══════════════════════════════════════════════════════════════╣
║  Se você não reconhece esta transação, entre em contato      ║
║  imediatamente com nossa central: 0800-123-4567               ║
╚══════════════════════════════════════════════════════════════╝
```

---

##  Cenários de Erro Tratados

| Cenário                    | Exceção                         | Mensagem                              |
|----------------------------|---------------------------------|---------------------------------------|
| Conta inexistente          | `ContaNaoEncontradaException`   | "Conta não encontrada para o ID: X"   |
| Saldo insuficiente         | `SaldoInsuficienteException`    | "Saldo insuficiente. Atual: R$ X"     |
| Valor zero ou negativo     | `ValorSaqueInvalidoException`   | "O valor do saque deve ser > zero"    |
| RabbitMQ indisponível      | `MensageriaException`           | "Falha ao conectar ao RabbitMQ"       |

---

##  Configuração do RabbitMQ

| Parâmetro    | Valor               |
|--------------|---------------------|
| Host         | `localhost`         |
| Porta AMQP   | `5672`              |
| Exchange     | `banco.exchange`    |
| Tipo         | `direct`            |
| Fila         | `fila.saque`        |
| Routing Key  | `saque.realizado`   |
| Usuário      | `guest`             |
| Senha        | `guest`             |

---

##  Princípios SOLID Aplicados

| Princípio | Aplicação                                                                 |
|-----------|---------------------------------------------------------------------------|
| **SRP**   | Cada classe tem uma única responsabilidade (Service, Repository, Publisher) |
| **OCP**   | Repository definido por interface, fácil de estender sem modificar          |
| **DIP**   | `SaqueService` depende da interface `ContaCorrenteRepository`, não da impl  |
| **ISP**   | Interfaces coesas e focadas (sem métodos desnecessários)                    |

---

##  Contas Mockadas

| ID      | Titular                  | Email                     | Saldo inicial  |
|---------|--------------------------|---------------------------|----------------|
| CC-001  | Ana Beatriz Silva        | ana.beatriz@email.com     | R$ 5.000,00    |
| CC-002  | Carlos Eduardo Mendes    | carlos.mendes@email.com   | R$ 12.500,50   |
| CC-003  | Mariana Ferreira Costa   | mariana.fc@email.com      | R$ 890,75      |
| CC-004  | Roberto Alves Neto       | roberto.neto@email.com    | R$ 30.000,00   |
