package br.unicap.modvillagers.excecoes;

/**
 * Exceção lançada quando um documento civil ou seus dados obrigatórios
 * estão ausentes, nulos ou em formato inválido.
 */
public class DocumentoInvalidoException extends RegistroCivilException {

    private static final long serialVersionUID = 1L;

    /**
     * Cria a exceção com a mensagem explicando qual dado do documento é inválido.
     *
     * @param mensagem descrição do problema encontrado
     */
    public DocumentoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
