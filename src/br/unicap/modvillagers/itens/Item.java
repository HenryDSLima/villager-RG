package br.unicap.modvillagers.itens;

import br.unicap.modvillagers.entidades.LivingEntity;
import br.unicap.modvillagers.entidades.Player;
import br.unicap.modvillagers.util.ValidacaoUtil;

/**
 * Classe base que representa um item nativo do Minecraft ({@code <<native>> Item}).
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
        this.nomeItem = ValidacaoUtil.exigirTextoValido(nomeItem, "Nome do item");
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = ValidacaoUtil.exigirTextoValido(descricao, "Descrição do item");
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = ValidacaoUtil.exigirIntervalo(maxStackSize, 1, 64, "Tamanho máximo da pilha");
    }

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
