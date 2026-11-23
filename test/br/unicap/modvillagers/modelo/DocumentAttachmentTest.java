package br.unicap.modvillagers.modelo;

import br.unicap.modvillagers.excecoes.PersistenciaNBTException;
import br.unicap.modvillagers.persistencia.NBTTagCompound;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes unitários para {@link DocumentAttachment}, validando composição 1:1,
 * serialização/desserialização em {@link NBTTagCompound} e exceções de persistência.
 */
@DisplayName("Testes de DocumentAttachment e Serialização NBT")
class DocumentAttachmentTest {

    @Test
    @DisplayName("Deve serializar e restaurar VillagerDocument via NBT preservando todos os dados")
    void deveSerializarEDesserializarViaNBT() throws PersistenciaNBTException {
        DocumentAttachment original = new DocumentAttachment("Deserto");
        original.registrarProfissao("Ferreiro de Armas");
        original.registrarProfissao("Armeiro");

        NBTTagCompound nbt = new NBTTagCompound();
        original.writeToNBT(nbt);

        DocumentAttachment restaurado = new DocumentAttachment();
        restaurado.readFromNBT(nbt);

        assertEquals(original.getDocumento(), restaurado.getDocumento());
    }

    @Test
    @DisplayName("Deve lançar PersistenciaNBTException quando NBT for nulo ou incompleto")
    void deveLancarExcecaoParaNbtInvalidoOuIncompleto() {
        DocumentAttachment attachment = new DocumentAttachment();

        assertThrows(PersistenciaNBTException.class, () -> attachment.writeToNBT(null));
        assertThrows(PersistenciaNBTException.class, () -> attachment.readFromNBT(null));

        NBTTagCompound incompleto = new NBTTagCompound();
        incompleto.setString(DocumentAttachment.TAG_NOME, "Sem CPF e RG");
        assertThrows(PersistenciaNBTException.class, () -> attachment.readFromNBT(incompleto));
    }

    @Test
    @DisplayName("Deve lançar PersistenciaNBTException para data corrompida ou CPF inválido no NBT")
    void deveLancarExcecaoParaDadosCorrompidosNoNbt() {
        DocumentAttachment attachment = new DocumentAttachment();

        NBTTagCompound dataInvalida = new NBTTagCompound();
        dataInvalida.setString(DocumentAttachment.TAG_NOME, "Carlos Silva");
        dataInvalida.setString(DocumentAttachment.TAG_CPF, attachment.getDocumento().getCpf());
        dataInvalida.setString(DocumentAttachment.TAG_RG, "12.345.678-9");
        dataInvalida.setString(DocumentAttachment.TAG_DATA_GERACAO, "data-invalida");
        assertThrows(PersistenciaNBTException.class, () -> attachment.readFromNBT(dataInvalida));

        NBTTagCompound cpfInvalido = new NBTTagCompound();
        cpfInvalido.setString(DocumentAttachment.TAG_NOME, "Carlos Silva");
        cpfInvalido.setString(DocumentAttachment.TAG_CPF, "111.111.111-11");
        cpfInvalido.setString(DocumentAttachment.TAG_RG, "12.345.678-9");
        assertThrows(PersistenciaNBTException.class, () -> attachment.readFromNBT(cpfInvalido));
    }

    @Test
    @DisplayName("Deve liberar o documento civil ao destruir o componente (ciclo de vida da composição)")
    void deveDestruirDocumentoNaComposicao() {
        DocumentAttachment attachment = new DocumentAttachment("Taiga");
        assertTrue(attachment.hasDocumento());

        attachment.destruir();
        assertFalse(attachment.hasDocumento());
        assertNull(attachment.getDocumento());
        assertThrows(PersistenciaNBTException.class, () -> attachment.writeToNBT(new NBTTagCompound()));
    }
}
