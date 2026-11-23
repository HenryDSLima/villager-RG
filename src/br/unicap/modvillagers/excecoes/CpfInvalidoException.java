package br.unicap.modvillagers.excecoes;

/**
 * Exceção lançada quando um número de CPF não atende ao formato esperado
 * ou falha na validação matemática dos dígitos verificadores (Módulo 11).
 */
public class CpfInvalidoException extends RegistroCivilException {

    private static final long serialVersionUID = 1L;

    /**
     * Cria a exceção detalhando o CPF inválido informado.
     *
     * @param cpf valor de CPF rejeitado pela validação
     */
    public CpfInvalidoException(String cpf) {
        super("CPF inválido: '" + cpf + "'. Informe um CPF com 11 dígitos e dígitos verificadores válidos.");
    }
}
