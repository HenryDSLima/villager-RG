package br.unicap.modvillagers.servico;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes unitários para {@link DocumentGenerator}, verificando geração e validação
 * matemática de CPFs, formato de RGs, unicidade por {@link Set} e nomes por bioma.
 */
@DisplayName("Testes de DocumentGenerator")
class DocumentGeneratorTest {

    @Test
    @DisplayName("Deve gerar CPFs formatados e matematicamente válidos pelo Módulo 11")
    void deveGerarCpfsMatematicamenteValidos() {
        DocumentGenerator gerador = new DocumentGenerator(new Random(123));

        for (int i = 0; i < 50; i++) {
            String cpf = gerador.gerarCpfValido();
            assertTrue(cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}"), "Formato inválido: " + cpf);
            assertTrue(DocumentGenerator.validarCpf(cpf), "CPF gerado falhou na validação: " + cpf);
        }
    }

    @Test
    @DisplayName("Deve rejeitar CPFs nulos, curtos, repetidos ou com dígitos verificadores incorretos")
    void deveRejeitarCpfsInvalidos() {
        assertFalse(DocumentGenerator.validarCpf(null));
        assertFalse(DocumentGenerator.validarCpf("12345"));
        assertFalse(DocumentGenerator.validarCpf("000.000.000-00"));
        assertFalse(DocumentGenerator.validarCpf("999.999.999-99"));
        assertFalse(DocumentGenerator.validarCpf("123.456.789-00"));
    }

    @Test
    @DisplayName("Deve gerar CPF único que não colide com conjunto de CPFs existentes")
    void deveGerarCpfUnicoSemColisao() {
        DocumentGenerator gerador = new DocumentGenerator(new Random(7));
        Set<String> existentes = new HashSet<>();
        for (int i = 0; i < 20; i++) {
            String cpf = gerador.gerarCpfUnico(existentes);
            assertFalse(existentes.contains(cpf));
            assertTrue(DocumentGenerator.validarCpf(cpf));
            existentes.add(cpf);
        }
    }

    @Test
    @DisplayName("Deve gerar RG no formato padrão XX.XXX.XXX-X")
    void deveGerarRgNoFormatoEsperado() {
        DocumentGenerator gerador = new DocumentGenerator(new Random(99));

        for (int i = 0; i < 20; i++) {
            String rg = gerador.gerarRg();
            assertTrue(rg.matches("\\d{2}\\.\\d{3}\\.\\d{3}-[0-9X]"), "RG com formato inesperado: " + rg);
        }
    }

    @Test
    @DisplayName("Deve gerar nomes completos para diferentes biomas com ou sem acentuação")
    void deveGerarNomesPorBioma() {
        DocumentGenerator gerador = new DocumentGenerator(new Random(55));
        String[] biomas = {"Planície", "planicie", "Deserto", "Taiga", "Savana", "Neve", "Pântano", "Selva"};

        for (String bioma : biomas) {
            String nome = gerador.gerarNome(bioma);
            assertNotNull(nome);
            assertTrue(nome.contains(" "), "Nome deve conter nome e sobrenome: " + nome);
        }
    }
}
