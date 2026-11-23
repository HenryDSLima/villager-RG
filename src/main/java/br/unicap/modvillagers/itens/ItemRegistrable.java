package br.unicap.modvillagers.itens;

/**
 * Interface que define o contrato padronizado para registro de itens customizados
 * no sistema de eventos do mod (conforme o Diagrama de Classes da Etapa 2).
 *
 * <p>Permite que diferentes classes de itens que já herdam de {@link Item} sejam
 * tratadas de maneira polimórfica e uniforme durante o registro no sistema.</p>
 */
public interface ItemRegistrable {

    /**
     * Retorna o identificador único do item no registro do mod (ex.: {@code villagerrg:prancheta_recenseamento}).
     *
     * @return identificador de registro
     */
    String getRegistryId();

    /**
     * Retorna o nome de exibição do item para o jogador.
     *
     * @return nome legível do item
     */
    String getNomeExibicao();

    /**
     * Executa o procedimento de registro do item no sistema do mod.
     */
    void registrar();

    /**
     * Indica se o item já foi devidamente registrado.
     *
     * @return {@code true} se o item estiver registrado; {@code false} caso contrário
     */
    boolean isRegistrado();
}
