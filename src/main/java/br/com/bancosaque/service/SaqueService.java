package br.com.bancosaque.service;

import br.com.bancosaque.dto.SaqueEventoDTO;
import br.com.bancosaque.dto.SaqueRequestDTO;
import br.com.bancosaque.exception.ContaNaoEncontradaException;
import br.com.bancosaque.exception.SaldoInsuficienteException;
import br.com.bancosaque.exception.ValorSaqueInvalidoException;
import br.com.bancosaque.messaging.SaquePublisher;
import br.com.bancosaque.model.ContaCorrente;
import br.com.bancosaque.repository.ContaCorrenteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Serviço de domínio responsável por orquestrar o fluxo completo de saque.
 *
 * Responsabilidades:
 * 1. Validar os dados da requisição de saque
 * 2. Buscar e validar a conta corrente
 * 3. Verificar saldo disponível
 * 4. Atualizar o saldo da conta
 * 5. Publicar o evento de saque no RabbitMQ
 *
 * Segue o princípio de Responsabilidade Única (SRP) ao delegar
 * persistência ao repositório e publicação ao publisher.
 */
public class SaqueService {

    private static final Logger log = LoggerFactory.getLogger(SaqueService.class);

    private final ContaCorrenteRepository contaRepository;
    private final SaquePublisher saquePublisher;

    public SaqueService(ContaCorrenteRepository contaRepository, SaquePublisher saquePublisher) {
        this.contaRepository = contaRepository;
        this.saquePublisher = saquePublisher;
    }

    public void realizarSaque(SaqueRequestDTO request) {
        log.info("[SaqueService] Iniciando processamento do saque. Conta: {} | Valor: R$ {}",
                request.getIdConta(), request.getValorSaque());


        validarValorSaque(request.getValorSaque());


        ContaCorrente conta = buscarConta(request.getIdConta());


        validarSaldo(conta, request.getValorSaque());


        BigDecimal saldoAnterior = conta.getSaldo();
        conta.debitar(request.getValorSaque());
        contaRepository.salvar(conta);

        log.info("[SaqueService] Saque realizado! Conta: {} | Saldo anterior: R$ {} | Novo saldo: R$ {}",
                conta.getId(), saldoAnterior, conta.getSaldo());


        SaqueEventoDTO evento = construirEvento(conta, request.getValorSaque());
        saquePublisher.publicar(evento);

        log.info("[SaqueService] Evento de saque publicado para processamento assíncrono.");
    }



    private void validarValorSaque(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("[SaqueService] Valor de saque inválido: {}", valor);
            throw new ValorSaqueInvalidoException(
                    "O valor do saque deve ser maior que zero. Valor informado: " + valor
            );
        }
    }

    private ContaCorrente buscarConta(String idConta) {
        return contaRepository.buscarPorId(idConta)
                .orElseThrow(() -> {
                    log.warn("[SaqueService] Conta não encontrada: {}", idConta);
                    return new ContaNaoEncontradaException(idConta);
                });
    }

    private void validarSaldo(ContaCorrente conta, BigDecimal valorSaque) {
        if (conta.getSaldo().compareTo(valorSaque) < 0) {
            log.warn("[SaqueService] Saldo insuficiente. Saldo: R$ {} | Solicitado: R$ {}",
                    conta.getSaldo(), valorSaque);
            throw new SaldoInsuficienteException(conta.getSaldo(), valorSaque);
        }
    }

    private SaqueEventoDTO construirEvento(ContaCorrente conta, BigDecimal valorSacado) {
        return new SaqueEventoDTO(
                conta.getId(),
                conta.getNomeTitular(),
                conta.getEmail(),
                valorSacado,
                conta.getSaldo(),
                LocalDateTime.now()
        );
    }
}
