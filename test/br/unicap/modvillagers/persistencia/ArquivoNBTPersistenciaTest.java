package br.unicap.modvillagers.persistencia;

import br.unicap.modvillagers.entidades.Villager;
import br.unicap.modvillagers.excecoes.PersistenciaNBTException;
import br.unicap.modvillagers.servico.CartorioRegistroCivil;
import br.unicap.modvillagers.servico.DocumentGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes unitários para {@link ArquivoNBTPersistencia}, validando leitura e escrita
 * em arquivos reais (em diretório temporário), exportação de relatórios e tratamento de erros.
 */
@DisplayName("Testes de Persistência em Arquivo (Java I/O)")
class ArquivoNBTPersistenciaTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Deve salvar e carregar Villagers em arquivo NBT preservando integridade total")
    void deveSalvarECarregarVillagersEmArquivo() throws PersistenciaNBTException {
        CartorioRegistroCivil cartorio = new CartorioRegistroCivil(new DocumentGenerator(new Random(33)));
        Villager v1 = cartorio.emitirERegistrarVillager("Planície", "Fazendeiro");
        v1.setProfissaoAtual("Bibliotecário");
        Villager v2 = cartorio.emitirERegistrarVillager("Deserto", "Ferreiro de Armas");

        ArquivoNBTPersistencia persistencia = new ArquivoNBTPersistencia();
        Path arquivo = tempDir.resolve("villagers_teste.nbt");

        persistencia.salvarVillagers(cartorio.listarTodos(), arquivo);
        assertTrue(Files.exists(arquivo));

        List<Villager> restaurados = persistencia.carregarVillagers(arquivo);
        assertEquals(2, restaurados.size());
        assertEquals(v1.getId(), restaurados.get(0).getId());
        assertEquals(v1.getProfissaoAtual(), restaurados.get(0).getProfissaoAtual());
        assertEquals(v1.getDocumentAttachment().getDocumento(), restaurados.get(0).getDocumentAttachment().getDocumento());
        assertEquals(v2.getDocumentAttachment().getDocumento(), restaurados.get(1).getDocumentAttachment().getDocumento());
    }

    @Test
    @DisplayName("Deve exportar relatório de censo civil legível em arquivo UTF-8")
    void deveExportarRelatorioDeCenso() throws PersistenciaNBTException, IOException {
        CartorioRegistroCivil cartorio = new CartorioRegistroCivil(new DocumentGenerator(new Random(44)));
        Villager v1 = cartorio.emitirERegistrarVillager("Taiga", "Cartógrafo");

        ArquivoNBTPersistencia persistencia = new ArquivoNBTPersistencia();
        Path relatorio = tempDir.resolve("relatorio_censo.txt");

        persistencia.exportarRelatorioCenso(cartorio, relatorio);
        String conteudo = Files.readString(relatorio, StandardCharsets.UTF_8);

        assertTrue(conteudo.contains("RELATÓRIO DE CENSO CIVIL"));
        assertTrue(conteudo.contains("Total de Villagers ativos: 1"));
        assertTrue(conteudo.contains(v1.getDocumentAttachment().getDocumento().getCpf()));
    }

    @Test
    @DisplayName("Deve lançar PersistenciaNBTException para arquivo inexistente ou corrompido")
    void deveLancarExcecaoParaArquivoInexistenteOuCorrompido() throws IOException {
        ArquivoNBTPersistencia persistencia = new ArquivoNBTPersistencia();

        Path inexistente = tempDir.resolve("nao_existe.nbt");
        assertThrows(PersistenciaNBTException.class, () -> persistencia.carregarVillagers(inexistente));

        Path corrompido = tempDir.resolve("corrompido.nbt");
        Files.writeString(corrompido, "[VILLAGER]\nlinha_sem_igual\n[END_VILLAGER]\n", StandardCharsets.UTF_8);
        assertThrows(PersistenciaNBTException.class, () -> persistencia.carregarVillagers(corrompido));
    }
}
