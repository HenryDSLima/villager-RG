package br.unicap.modvillagers;

import br.unicap.modvillagers.excecoes.CpfInvalidoException;
import br.unicap.modvillagers.excecoes.DocumentoInvalidoException;
import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.servico.DocumentGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testes unitários do modelo {@link VillagerDocument} dentro do projeto Fabric.
 */
@DisplayName("Testes de VillagerDocument (Mod Fabric)")
class VillagerDocumentTest {

    @Test
    @DisplayName("Deve criar documento válido, validar CPF e proteger coleções")
    void deveValidarDocumentoEColecoes() {
        DocumentGenerator gerador = new DocumentGenerator(new Random(42));
        String cpf = gerador.gerarCpfValido();
        String rg = gerador.gerarRg();

        VillagerDocument doc = new VillagerDocument("Helena Ferreira", cpf, rg, "Planície", LocalDate.of(2026, 10, 1));
        doc.adicionarProfissao("Fazendeiro");
        doc.adicionarProfissao("Bibliotecário");
        doc.adicionarProfissao("Fazendeiro");

        assertEquals(List.of("Fazendeiro", "Bibliotecário", "Fazendeiro"), doc.getHistoricoProfissoes());
        assertEquals(Set.of("Fazendeiro", "Bibliotecário"), doc.getProfissoesUnicas());
        assertThrows(UnsupportedOperationException.class, () -> doc.getHistoricoProfissoes().add("Invasão"));
        assertThrows(CpfInvalidoException.class, () -> new VillagerDocument("Teste", "111.111.111-11", rg));
        assertThrows(DocumentoInvalidoException.class, () -> new VillagerDocument(" ", cpf, rg));
    }
}
