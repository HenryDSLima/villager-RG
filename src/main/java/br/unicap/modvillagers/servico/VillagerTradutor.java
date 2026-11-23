package br.unicap.modvillagers.servico;

import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

import java.util.Map;

/**
 * Traduz os tipos de bioma ({@link VillagerType}) e profissões nativas ({@link VillagerProfession})
 * do Minecraft para nomes em português utilizados no Registro Civil.
 */
public final class VillagerTradutor {

    private static final Map<VillagerType, String> BIOMAS = Map.of(
        VillagerType.PLAINS, "Planície",
        VillagerType.DESERT, "Deserto",
        VillagerType.TAIGA, "Taiga",
        VillagerType.SAVANNA, "Savana",
        VillagerType.SNOW, "Neve",
        VillagerType.SWAMP, "Pântano",
        VillagerType.JUNGLE, "Selva"
    );

    private static final Map<VillagerProfession, String> PROFISSOES = Map.ofEntries(
        Map.entry(VillagerProfession.NONE, "Desempregado"),
        Map.entry(VillagerProfession.NITWIT, "Desocupado"),
        Map.entry(VillagerProfession.ARMORER, "Armeiro"),
        Map.entry(VillagerProfession.BUTCHER, "Açougueiro"),
        Map.entry(VillagerProfession.CARTOGRAPHER, "Cartógrafo"),
        Map.entry(VillagerProfession.CLERIC, "Clérigo"),
        Map.entry(VillagerProfession.FARMER, "Fazendeiro"),
        Map.entry(VillagerProfession.FISHERMAN, "Pescador"),
        Map.entry(VillagerProfession.FLETCHER, "Flecheiro"),
        Map.entry(VillagerProfession.LEATHERWORKER, "Coureiro"),
        Map.entry(VillagerProfession.LIBRARIAN, "Bibliotecário"),
        Map.entry(VillagerProfession.MASON, "Pedreiro"),
        Map.entry(VillagerProfession.SHEPHERD, "Pastor"),
        Map.entry(VillagerProfession.TOOLSMITH, "Ferreiro de Ferramentas"),
        Map.entry(VillagerProfession.WEAPONSMITH, "Ferreiro de Armas")
    );

    private VillagerTradutor() {
    }

    /**
     * Converte um {@link VillagerType} nativo do Minecraft para o nome do bioma em português.
     *
     * @param tipo tipo nativo do Villager
     * @return nome do bioma em português
     */
    public static String traduzirBioma(VillagerType tipo) {
        if (tipo == null) {
            return "Planície";
        }
        return BIOMAS.getOrDefault(tipo, "Planície");
    }

    /**
     * Converte uma {@link VillagerProfession} nativa do Minecraft para o nome em português.
     *
     * @param profissao profissão nativa do Villager
     * @return nome da profissão em português
     */
    public static String traduzirProfissao(VillagerProfession profissao) {
        if (profissao == null) {
            return "Desempregado";
        }
        String traduzida = PROFISSOES.get(profissao);
        if (traduzida != null) {
            return traduzida;
        }
        return profissao.id();
    }
}
