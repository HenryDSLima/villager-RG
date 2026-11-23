package br.unicap.modvillagers.modelo;

import br.unicap.modvillagers.excecoes.CpfInvalidoException;
import br.unicap.modvillagers.excecoes.DocumentoInvalidoException;
import br.unicap.modvillagers.servico.DocumentGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes unitários para {@link VillagerDocument}, cobrindo encapsulamento,
 * validações com exceções específicas e coleções de profissões (List e Set).
 */
@DisplayName("Testes de VillagerDocument")
class VillagerDocumentTest {

    private String cpfValido;
    private String rgValido;

    @BeforeEach
    void configurar() {
        DocumentGenerator gerador = new DocumentGenerator(new Random(42));
        this.cpfValido = gerador.gerarCpfValido();
        this.rgValido = gerador.gerarRg();
    }

    @Test
    @DisplayName("Deve criar documento válido e expor todos os getters corretamente")
    void deveCriarDocumentoValido() {
        LocalDate data = LocalDate.of(2026, 10, 15);
        VillagerDocument doc = new VillagerDocument("Helena Ferreira", cpfValido, rgValido, "Planície", data);

        assertEquals("Helena Ferreira", doc.getNome());
        assertEquals(cpfValido, doc.getCpf());
        assertEquals(rgValido, doc.getRg());
        assertEquals("Planície", doc.getBiomaOrigem());
        assertEquals(data, doc.getDataGeracao());
        assertTrue(doc.getHistoricoProfissoes().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar CpfInvalidoException para CPF matematicamente inválido")
    void deveLancarExcecaoParaCpfInvalido() {
        assertThrows(CpfInvalidoException.class,
                () -> new VillagerDocument("Aldo Silva", "111.111.111-11", rgValido));
        assertThrows(CpfInvalidoException.class,
                () -> new VillagerDocument("Aldo Silva", "123.456.789-00", rgValido));
        assertThrows(CpfInvalidoException.class,
                () -> new VillagerDocument("Aldo Silva", "", rgValido));
    }

    @Test
    @DisplayName("Deve lançar DocumentoInvalidoException para nome, RG ou data inválidos")
    void deveLancarExcecaoParaCamposObrigatoriosInvalidos() {
        assertThrows(DocumentoInvalidoException.class,
                () -> new VillagerDocument("   ", cpfValido, rgValido));
        assertThrows(DocumentoInvalidoException.class,
                () -> new VillagerDocument("Aldo Silva", cpfValido, null));
        assertThrows(DocumentoInvalidoException.class,
                () -> new VillagerDocument("Aldo Silva", cpfValido, rgValido, "Planície", null));
    }

    @Test
    @DisplayName("Deve manter histórico cronológico (List), profissões únicas (Set) e encapsulamento imutável")
    void deveGerenciarHistoricoEProfissoesUnicasComEncapsulamento() {
        VillagerDocument doc = new VillagerDocument("Beatriz Mendes", cpfValido, rgValido);
        doc.adicionarProfissao("Fazendeiro");
        doc.adicionarProfissao("Fazendeiro"); // repetição consecutiva deve ser ignorada
        doc.adicionarProfissao("Bibliotecário");
        doc.adicionarProfissao("Fazendeiro"); // retorno posterior deve ser mantido na List

        assertEquals(List.of("Fazendeiro", "Bibliotecário", "Fazendeiro"), doc.getHistoricoProfissoes());
        assertEquals(Set.of("Fazendeiro", "Bibliotecário"), doc.getProfissoesUnicas());
        assertThrows(UnsupportedOperationException.class,
                () -> doc.getHistoricoProfissoes().add("Profissão Externa"));
    }
}
