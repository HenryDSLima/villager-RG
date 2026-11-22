package br.unicap.modvillagers.excecoes;

/**
 * Exceção lançada quando um documento civil ou seus dados obrigatórios
 * estão ausentes, nulos ou em formato inválido.
 */
public class DocumentoInvalidoException extends RegistroCivilException {

    public DocumentoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
