package br.unicap.modvillagers.excecoes;

/**
 * Exceção verificada (checked) lançada quando ocorre falha na serialização,
 * desserialização ou leitura/escrita em arquivo de dados NBT.
 */
public class PersistenciaNBTException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Cria a exceção de persistência NBT com mensagem explicativa.
     *
     * @param mensagem descrição da falha ocorrida
     */
    public PersistenciaNBTException(String mensagem) {
        super(mensagem);
    }

    /**
     * Cria a exceção de persistência NBT com mensagem e exceção subjacente.
     *
     * @param mensagem descrição da falha ocorrida
     * @param causa    causa original (ex.: {@link java.io.IOException})
     */
    public PersistenciaNBTException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
