package br.unicap.modvillagers.excecoes;

/**
 * Exceção base de domínio para erros de regra de negócio no
 * Sistema de Registro Civil para Villagers.
 */
public class RegistroCivilException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Cria uma nova exceção de domínio com uma mensagem descritiva.
     *
     * @param mensagem descrição do erro ocorrido
     */
    public RegistroCivilException(String mensagem) {
        super(mensagem);
    }

    /**
     * Cria uma nova exceção de domínio com mensagem e causa raiz.
     *
     * @param mensagem descrição do erro ocorrido
     * @param causa    exceção original que motivou o erro
     */
    public RegistroCivilException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
