package br.unicap.modvillagers.itens;

import br.unicap.modvillagers.entidades.LivingEntity;
import br.unicap.modvillagers.entidades.Player;

/**
 * Classe base que representa um item nativo do Minecraft ({@code <<native>> Item}).
 *
 * <p>Fornece o comportamento padrão de interação com entidades vivas
 * ({@link #interactLivingEntity(Player, LivingEntity)}), que pode ser especializado
 * por subclasses por meio de herança e sobrescrita ({@code @Override}).</p>
 */
public class Item {

    private String nomeItem;
    private String descricao;
    private int maxStackSize;

    public Item() {
        this("Item Genérico", "Item padrão sem ação especial de inspeção.", 64);
    }

    public Item(String nomeItem, String descricao, int maxStackSize) {
        setNomeItem(nomeItem);
        setDescricao(descricao);
        setMaxStackSize(maxStackSize);
    }

    public String getNomeItem() {
        return nomeItem;
    }

    public void setNomeItem(String nomeItem) {
        if (nomeItem == null || nomeItem.isBlank()) {
            throw new IllegalArgumentException("Nome do item não pode ser nulo ou vazio.");
        }
        this.nomeItem = nomeItem.trim();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição do item não pode ser nula ou vazia.");
        }
        this.descricao = descricao.trim();
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public void setMaxStackSize(int maxStackSize) {
        if (maxStackSize < 1 || maxStackSize > 64) {
            throw new IllegalArgumentException("Tamanho máximo da pilha deve estar entre 1 e 64.");
        }
        this.maxStackSize = maxStackSize;
    }

    /**
     * Comportamento padrão de interação de um item com uma entidade viva.
     *
     * @param jogador jogador que utilizou o item
     * @param alvo    entidade viva alvo da interação
     * @return {@code false} por padrão, indicando que o item não realiza ação especial
     */
    public boolean interactLivingEntity(Player jogador, LivingEntity alvo) {
        return false;
    }

    @Override
    public String toString() {
        return "Item{"
                + "nomeItem='" + nomeItem + '\''
                + ", maxStackSize=" + maxStackSize
                + '}';
    }
}
