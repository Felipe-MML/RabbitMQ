package br.com.bancosaque.repository;

import br.com.bancosaque.model.ContaCorrente;

import java.util.Optional;


public interface ContaCorrenteRepository {


    Optional<ContaCorrente> buscarPorId(String id);


    void salvar(ContaCorrente conta);
}
