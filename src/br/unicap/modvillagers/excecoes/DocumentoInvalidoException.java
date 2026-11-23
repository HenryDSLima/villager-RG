package br.unicap.modvillagers.excecoes;

/**
 * Exceção lançada quando um documento civil ou seus dados obrigatórios
 * estão ausentes, nulos ou em formato inválido.
 */
public class DocumentoInvalidoException extends RegistroCivilException {

    private static final long serialVersionUID = 1L;

    public DocumentoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
