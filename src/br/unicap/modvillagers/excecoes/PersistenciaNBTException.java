package br.unicap.modvillagers.excecoes;

/**
 * Exceção verificada (checked) lançada quando ocorre falha na serialização,
 * desserialização ou leitura/escrita em arquivo de dados NBT.
 */
public class PersistenciaNBTException extends Exception {

    public PersistenciaNBTException(String mensagem) {
        super(mensagem);
    }

    public PersistenciaNBTException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
