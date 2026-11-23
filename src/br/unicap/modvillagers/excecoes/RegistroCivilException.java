package br.unicap.modvillagers.excecoes;

/**
 * Exceção base de domínio para erros de regra de negócio no
 * Sistema de Registro Civil para Villagers.
 */
public class RegistroCivilException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RegistroCivilException(String mensagem) {
        super(mensagem);
    }

    public RegistroCivilException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
