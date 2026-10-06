package br.unicap.modvillagers.itens;

public class InspectorItem extends Item {
    
    public InspectorItem() {
        super(); 
    }

    @Override
    public boolean interactLivingEntity(String jogador, String alvo) {
        System.out.println("O jogador " + jogador + " está inspecionando o " + alvo);
        return true;
    }
}