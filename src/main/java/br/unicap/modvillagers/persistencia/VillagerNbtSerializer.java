package br.unicap.modvillagers.persistencia;

import br.unicap.modvillagers.excecoes.PersistenciaNBTException;
import br.unicap.modvillagers.excecoes.RegistroCivilException;
import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.padroes.builder.VillagerDocumentBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Serializador que integra {@link VillagerDocument} diretamente ao sistema nativo
 * de persistência NBT ({@link NbtCompound}) do Minecraft, garantindo que a identidade
 * civil de cada Villager seja salva junto ao mundo do jogo.
 */
public final class VillagerNbtSerializer {

    /** Chave raiz gravada no NBT customizado da entidade {@code VillagerEntity}. */
    public static final String ROOT_KEY = "VillagerRG";
    /** Chave NBT para o nome completo do Villager. */
    public static final String TAG_NOME = "nome";
    /** Chave NBT para o CPF do Villager. */
    public static final String TAG_CPF = "cpf";
    /** Chave NBT para o RG do Villager. */
    public static final String TAG_RG = "rg";
    /** Chave NBT para o bioma de origem do Villager. */
    public static final String TAG_BIOMA = "bioma_origem";
    /** Chave NBT para a data de emissão do documento em formato ISO-8601. */
    public static final String TAG_DATA_GERACAO = "data_geracao";
    /** Chave NBT para o histórico cronológico de profissões. */
    public static final String TAG_HISTORICO_PROFISSOES = "historico_profissoes";

    private VillagerNbtSerializer() {
    }

    /**
     * Serializa um {@link VillagerDocument} para dentro do {@link NbtCompound} nativo do Villager.
     *
     * @param documento documento civil a persistir
     * @param entityNbt composto NBT da entidade no Minecraft
     * @throws PersistenciaNBTException se o documento ou o NBT forem nulos
     */
    public static void writeToNbt(VillagerDocument documento, NbtCompound entityNbt) throws PersistenciaNBTException {
        if (documento == null) {
            throw new PersistenciaNBTException("Documento civil nulo não pode ser serializado em NBT.");
        }
        if (entityNbt == null) {
            throw new PersistenciaNBTException("NbtCompound da entidade não pode ser nulo.");
        }

        NbtCompound docTag = new NbtCompound();
        docTag.putString(TAG_NOME, documento.getNome());
        docTag.putString(TAG_CPF, documento.getCpf());
        docTag.putString(TAG_RG, documento.getRg());
        docTag.putString(TAG_BIOMA, documento.getBiomaOrigem());
        docTag.putString(TAG_DATA_GERACAO, documento.getDataGeracao().toString());

        NbtList listaProfissoes = new NbtList();
        for (String profissao : documento.getHistoricoProfissoes()) {
            listaProfissoes.add(NbtString.of(profissao));
        }
        docTag.put(TAG_HISTORICO_PROFISSOES, listaProfissoes);

        entityNbt.put(ROOT_KEY, docTag);
    }

    /**
     * Lê um {@link VillagerDocument} a partir do {@link NbtCompound} nativo do Villager,
     * utilizando {@link VillagerDocumentBuilder}.
     *
     * @param entityNbt composto NBT da entidade no Minecraft
     * @return documento restaurado, ou {@code null} se a entidade ainda não possuir tag {@value #ROOT_KEY}
     * @throws PersistenciaNBTException se os dados NBT estiverem incompletos ou corrompidos
     */
    public static VillagerDocument readFromNbt(NbtCompound entityNbt) throws PersistenciaNBTException {
        if (entityNbt == null || !entityNbt.contains(ROOT_KEY, NbtElement.COMPOUND_TYPE)) {
            return null;
        }

        NbtCompound docTag = entityNbt.getCompound(ROOT_KEY);
        if (!docTag.contains(TAG_NOME, NbtElement.STRING_TYPE)
                || !docTag.contains(TAG_CPF, NbtElement.STRING_TYPE)
                || !docTag.contains(TAG_RG, NbtElement.STRING_TYPE)) {
            throw new PersistenciaNBTException("Tag VillagerRG incompleta no NBT: nome, CPF e RG são obrigatórios.");
        }

        LocalDate dataGeracao = LocalDate.now();
        if (docTag.contains(TAG_DATA_GERACAO, NbtElement.STRING_TYPE)) {
            try {
                dataGeracao = LocalDate.parse(docTag.getString(TAG_DATA_GERACAO));
            } catch (DateTimeParseException e) {
                throw new PersistenciaNBTException("Data inválida na tag NBT VillagerRG.", e);
            }
        }

        List<String> historico = new ArrayList<>();
        if (docTag.contains(TAG_HISTORICO_PROFISSOES, NbtElement.LIST_TYPE)) {
            NbtList nbtList = docTag.getList(TAG_HISTORICO_PROFISSOES, NbtElement.STRING_TYPE);
            for (int i = 0; i < nbtList.size(); i++) {
                historico.add(nbtList.getString(i));
            }
        }

        try {
            return new VillagerDocumentBuilder()
                    .comNome(docTag.getString(TAG_NOME))
                    .comCpf(docTag.getString(TAG_CPF))
                    .comRg(docTag.getString(TAG_RG))
                    .comBiomaOrigem(docTag.contains(TAG_BIOMA, NbtElement.STRING_TYPE)
                            ? docTag.getString(TAG_BIOMA)
                            : "Planície")
                    .comDataGeracao(dataGeracao)
                    .comHistoricoProfissoes(historico)
                    .build();
        } catch (RegistroCivilException e) {
            throw new PersistenciaNBTException("Falha ao reconstruir VillagerDocument do NBT: " + e.getMessage(), e);
        }
    }
}
