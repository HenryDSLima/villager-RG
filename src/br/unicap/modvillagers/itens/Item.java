package br.unicap.modvillagers.itens;

import br.unicap.modvillagers.entidades.LivingEntity;
import br.unicap.modvillagers.entidades.Player;
import br.unicap.modvillagers.util.ValidacaoUtil;

/**
 * Classe base que representa um item nativo do Minecraft ({@code <<native>> Item}).
 * Fornece o comportamento padrão de interação com entidades vivas, sobrescrito por subclasses.
 */
public class Item {

    private String nomeItem;
    private String descricao;
    private int maxStackSize;

    /**
     * Cria um item genérico padrão com pilha máxima de 64 unidades.
     */
    public Item() {
        this("Item Genérico", "Item padrão sem ação especial de inspeção.", 64);
    }

    /**
     * Cria um item com nome, descrição e tamanho máximo de pilha especificados.
     *
     * @param nomeItem     nome de exibição do item
     * @param descricao    descrição funcional do item
     * @param maxStackSize limite de empilhamento no inventário (entre 1 e 64)
     */
    public Item(String nomeItem, String descricao, int maxStackSize) {
        setNomeItem(nomeItem);
        setDescricao(descricao);
        setMaxStackSize(maxStackSize);
    }

    /**
     * Retorna o nome de exibição do item.
     *
     * @return nome do item
     */
    public String getNomeItem() {
        return nomeItem;
    }

    /**
     * Atualiza o nome de exibição do item.
     *
     * @param nomeItem novo nome do item
     */
    public void setNomeItem(String nomeItem) {
        this.nomeItem = ValidacaoUtil.exigirTextoValido(nomeItem, "Nome do item");
    }

    /**
     * Retorna a descrição funcional do item.
     *
     * @return descrição do item
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Atualiza a descrição funcional do item.
     *
     * @param descricao nova descrição do item
     */
    public void setDescricao(String descricao) {
        this.descricao = ValidacaoUtil.exigirTextoValido(descricao, "Descrição do item");
    }

    /**
     * Retorna o tamanho máximo de pilha permitido para este item.
     *
     * @return limite de empilhamento (1 a 64)
     */
    public int getMaxStackSize() {
        return maxStackSize;
    }

    /**
     * Define o tamanho máximo de pilha permitido para este item (entre 1 e 64).
     *
     * @param maxStackSize novo tamanho máximo de pilha
     */
    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = ValidacaoUtil.exigirIntervalo(maxStackSize, 1, 64, "Tamanho máximo da pilha");
    }

    /**
     * Executa o comportamento padrão de interação com uma entidade viva.
     * Subclasses podem sobrescrever este método para fornecer interações especializadas.
     *
     * @param jogador jogador que utilizou o item
     * @param alvo    entidade viva alvo da interação
     * @return {@code false} por padrão, indicando ausência de ação especial
     */
    public boolean interactLivingEntity(Player jogador, LivingEntity alvo) {
        return false;
    }

    /**
     * Retorna a representação textual resumida deste item.
     *
     * @return representação em string do {@code Item}
     */
    @Override
    public String toString() {
        return "Item{"
                + "nomeItem='" + nomeItem + '\''
                + ", maxStackSize=" + maxStackSize
                + '}';
    }
}
