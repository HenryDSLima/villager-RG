package br.unicap.modvillagers.modelo;

/**
 * Interface de composição (Duck Interface) injetada em {@code net.minecraft.entity.passive.VillagerEntity}
 * via Mixin para anexar um {@link VillagerDocument} persistente a cada Villager do Minecraft.
 */
public interface VillagerDocumentHolder {

    /**
     * Retorna o documento civil atualmente anexado ao Villager.
     *
     * @return {@link VillagerDocument} ou {@code null} se ainda não foi emitido
     */
    VillagerDocument villagerrg$getDocumento();

    /**
     * Define o documento civil anexado ao Villager.
     *
     * @param documento documento civil a associar
     */
    void villagerrg$setDocumento(VillagerDocument documento);

    /**
     * Garante que o Villager possua um documento civil válido, emitindo-o caso ainda não exista.
     *
     * @return o documento civil existente ou recém-emitido
     */
    VillagerDocument villagerrg$garantirDocumento();
}
