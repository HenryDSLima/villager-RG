package br.unicap.modvillagers.padroes.builder;

import br.unicap.modvillagers.excecoes.CpfInvalidoException;
import br.unicap.modvillagers.excecoes.DocumentoInvalidoException;
import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.servico.DocumentGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testes unitários para o padrão criacional Builder ({@link VillagerDocumentBuilder}).
 */
@DisplayName("Testes do Padrão Builder (VillagerDocumentBuilder)")
class VillagerDocumentBuilderTest {

    @Test
    @DisplayName("Deve construir VillagerDocument completo de forma fluente com histórico inicial")
    void deveConstruirDocumentoCompletoViaBuilder() {
        DocumentGenerator gerador = new DocumentGenerator(new Random(101));
        String cpf = gerador.gerarCpfValido();
        String rg = gerador.gerarRg();
        LocalDate data = LocalDate.of(2026, 8, 20);

        VillagerDocument doc = new VillagerDocumentBuilder()
                .comNome("Elena Campos")
                .comCpf(cpf)
                .comRg(rg)
                .comBiomaOrigem("Taiga")
                .comDataGeracao(data)
                .comProfissao("Desempregado")
                .comHistoricoProfissoes(List.of("Cartógrafo", "Bibliotecário"))
                .build();

        assertEquals("Elena Campos", doc.getNome());
        assertEquals(cpf, doc.getCpf());
        assertEquals(rg, doc.getRg());
        assertEquals("Taiga", doc.getBiomaOrigem());
        assertEquals(data, doc.getDataGeracao());
        assertEquals(List.of("Desempregado", "Cartógrafo", "Bibliotecário"), doc.getHistoricoProfissoes());
    }

    @Test
    @DisplayName("Deve aplicar valores padrão de bioma e data quando não informados")
    void deveAplicarValoresPadraoNoBuilder() {
        DocumentGenerator gerador = new DocumentGenerator(new Random(202));
        VillagerDocument doc = new VillagerDocumentBuilder()
                .comNome("Aldo Silva")
                .comCpf(gerador.gerarCpfValido())
                .comRg(gerador.gerarRg())
                .build();

        assertEquals("Planície", doc.getBiomaOrigem());
        assertEquals(LocalDate.now(), doc.getDataGeracao());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios e CPF ao chamar build()")
    void deveValidarRegrasAoConstruir() {
        assertThrows(DocumentoInvalidoException.class, () -> new VillagerDocumentBuilder().build());
        assertThrows(CpfInvalidoException.class, () -> new VillagerDocumentBuilder()
                .comNome("Carlos Silva")
                .comCpf("000.000.000-00")
                .comRg("12.345.678-9")
                .build());
    }
}
