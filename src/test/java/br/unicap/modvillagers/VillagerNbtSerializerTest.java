package br.unicap.modvillagers;

import br.unicap.modvillagers.excecoes.PersistenciaNBTException;
import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.padroes.builder.VillagerDocumentBuilder;
import br.unicap.modvillagers.persistencia.VillagerNbtSerializer;
import br.unicap.modvillagers.servico.DocumentGenerator;
import net.minecraft.nbt.NbtCompound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testes unitários de serialização e desserialização usando o {@link NbtCompound} nativo do Minecraft.
 */
@DisplayName("Testes de VillagerNbtSerializer (NBT nativo do Minecraft)")
class VillagerNbtSerializerTest {

    @Test
    @DisplayName("Deve serializar e restaurar VillagerDocument no NbtCompound nativo sem perda de dados")
    void deveSerializarERestaurarNoNbtNativo() throws PersistenciaNBTException {
        DocumentGenerator gerador = new DocumentGenerator(new Random(77));
        VillagerDocument original = new VillagerDocumentBuilder()
                .comNome(gerador.gerarNome("Deserto"))
                .comCpf(gerador.gerarCpfValido())
                .comRg(gerador.gerarRg())
                .comBiomaOrigem("Deserto")
                .comDataGeracao(LocalDate.of(2026, 11, 20))
                .comHistoricoProfissoes(List.of("Desempregado", "Ferreiro de Armas", "Armeiro"))
                .build();

        NbtCompound entityNbt = new NbtCompound();
        VillagerNbtSerializer.writeToNbt(original, entityNbt);

        VillagerDocument restaurado = VillagerNbtSerializer.readFromNbt(entityNbt);
        assertEquals(original, restaurado);
    }

    @Test
    @DisplayName("Deve retornar null para NBT sem tag VillagerRG e lançar exceção para tag corrompida")
    void deveTratarTagAusenteOuCorrompida() throws PersistenciaNBTException {
        assertNull(VillagerNbtSerializer.readFromNbt(new NbtCompound()));

        NbtCompound corrompido = new NbtCompound();
        NbtCompound docTag = new NbtCompound();
        docTag.putString(VillagerNbtSerializer.TAG_NOME, "Incompleto");
        corrompido.put(VillagerNbtSerializer.ROOT_KEY, docTag);

        assertThrows(PersistenciaNBTException.class, () -> VillagerNbtSerializer.readFromNbt(corrompido));
    }
}
