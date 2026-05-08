package br.com.bancosaque.repository;

import br.com.bancosaque.model.ContaCorrente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class ContaCorrenteRepositoryImpl implements ContaCorrenteRepository {

    private static final Logger log = LoggerFactory.getLogger(ContaCorrenteRepositoryImpl.class);

    private final Map<String, ContaCorrente> banco = new HashMap<>();

    public ContaCorrenteRepositoryImpl() {
        carregarDadosMockados();
    }


    private void carregarDadosMockados() {
        banco.put("CC-001", new ContaCorrente("CC-001", "Ana Beatriz Silva",
                "ana.beatriz@email.com", new BigDecimal("5000.00")));

        banco.put("CC-002", new ContaCorrente("CC-002", "Carlos Eduardo Mendes",
                "carlos.mendes@email.com", new BigDecimal("12500.50")));

        banco.put("CC-003", new ContaCorrente("CC-003", "Mariana Ferreira Costa",
                "mariana.fc@email.com", new BigDecimal("890.75")));

        banco.put("CC-004", new ContaCorrente("CC-004", "Roberto Alves Neto",
                "roberto.neto@email.com", new BigDecimal("30000.00")));

        log.info("[Repositório] {} contas carregadas em memória.", banco.size());
        log.info("[Repositório] Contas disponíveis: {}", banco.keySet());
    }

    @Override
    public Optional<ContaCorrente> buscarPorId(String id) {
        log.debug("[Repositório] Buscando conta ID: {}", id);
        return Optional.ofNullable(banco.get(id));
    }

    @Override
    public void salvar(ContaCorrente conta) {
        banco.put(conta.getId(), conta);
        log.debug("[Repositório] Conta {} atualizada. Novo saldo: R$ {}", conta.getId(), conta.getSaldo());
    }
}
