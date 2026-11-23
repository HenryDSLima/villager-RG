package br.unicap.modvillagers.servico;

import br.unicap.modvillagers.entidades.Villager;
import br.unicap.modvillagers.excecoes.CpfInvalidoException;
import br.unicap.modvillagers.excecoes.VillagerDuplicadoException;
import br.unicap.modvillagers.excecoes.VillagerNaoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes unitários para {@link CartorioRegistroCivil}, validando operações com
 * {@link List}, {@link Map} e {@link Set}, além das exceções de duplicidade e busca.
 */
@DisplayName("Testes de CartorioRegistroCivil (List, Map e Set)")
class CartorioRegistroCivilTest {

    private CartorioRegistroCivil cartorio;
    private DocumentGenerator gerador;

    @BeforeEach
    void configurar() {
        this.gerador = new DocumentGenerator(new Random(2026));
        this.cartorio = new CartorioRegistroCivil(gerador);
    }

    @Test
    @DisplayName("Deve cadastrar, buscar por CPF e gerar estatísticas censitárias em List, Map e Set")
    void deveCadastrarBuscarEGerarEstatisticas() {
        Villager v1 = cartorio.emitirERegistrarVillager("Planície", "Fazendeiro");
        Villager v2 = cartorio.emitirERegistrarVillager("Deserto", "Bibliotecário");
        Villager v3 = cartorio.emitirERegistrarVillager("Planície", "Fazendeiro");

        String cpf1 = v1.getDocumentAttachment().getDocumento().getCpf();
        assertSame(v1, cartorio.buscarPorCpf(cpf1));
        assertEquals(3, cartorio.getTotalRegistrados());
        assertEquals(3, cartorio.listarTodos().size());
        assertEquals(Set.of("Planície", "Deserto"), cartorio.listarBiomasRegistrados());

        Map<String, List<Villager>> porBioma = cartorio.agruparPorBioma();
        assertEquals(2, porBioma.get("Planície").size());
        assertEquals(1, porBioma.get("Deserto").size());

        Map<String, Integer> porProfissao = cartorio.contarPorProfissao();
        assertEquals(2, porProfissao.get("Fazendeiro"));
        assertEquals(1, porProfissao.get("Bibliotecário"));
        assertTrue(cartorio.getCpfsEmitidos().contains(v2.getDocumentAttachment().getDocumento().getCpf()));
        assertTrue(cartorio.getCpfsEmitidos().contains(v3.getDocumentAttachment().getDocumento().getCpf()));
    }

    @Test
    @DisplayName("Deve lançar VillagerDuplicadoException ao tentar registrar CPF já existente")
    void deveImpedirCadastroDuplicado() {
        Villager v1 = cartorio.emitirERegistrarVillager("Taiga", "Cartógrafo");
        assertThrows(VillagerDuplicadoException.class, () -> cartorio.registrarVillager(v1));
    }

    @Test
    @DisplayName("Deve lançar VillagerNaoEncontradoException e CpfInvalidoException na busca")
    void deveLancarExcecoesNaBusca() {
        String cpfNaoCadastrado = gerador.gerarCpfUnico(cartorio.getCpfsEmitidos());
        assertThrows(VillagerNaoEncontradoException.class, () -> cartorio.buscarPorCpf(cpfNaoCadastrado));
        assertThrows(CpfInvalidoException.class, () -> cartorio.buscarPorCpf("111.111.111-11"));
    }

    @Test
    @DisplayName("Deve remover do cadastro ativo mantendo o CPF no histórico (Set) para evitar reuso")
    void deveRemoverMantendoHistoricoDeCpfs() {
        Villager v1 = cartorio.emitirERegistrarVillager("Savana", "Açougueiro");
        String cpf = v1.getDocumentAttachment().getDocumento().getCpf();

        Villager removido = cartorio.removerPorCpf(cpf);
        assertSame(v1, removido);
        assertEquals(0, cartorio.getTotalRegistrados());
        assertTrue(cartorio.getCpfsEmitidos().contains(cpf));
        assertThrows(VillagerDuplicadoException.class, () -> cartorio.registrarVillager(v1));
    }
}
