package br.unicap.modvillagers.util;

import java.text.Normalizer;

/**
 * Classe utilitária centralizada para validações comuns e normalização de textos,
 * eliminando duplicação de código entre as camadas do sistema.
 */
public final class ValidacaoUtil {

    private ValidacaoUtil() {
        // Classe utilitária não instanciável
    }

    /**
     * Garante que uma string não seja nula nem composta apenas por espaços,
     * retornando o texto normalizado com {@code trim()}.
     *
     * @param valor     texto a ser validado
     * @param nomeCampo nome descritivo do campo para mensagens de erro úteis
     * @return texto validado e sem espaços nas extremidades
     */
    public static String exigirTextoValido(String valor, String nomeCampo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(nomeCampo + " não pode ser nulo ou vazio.");
        }
        return valor.trim();
    }

    /**
     * Garante que uma referência de objeto não seja nula.
     *
     * @param valor     objeto a ser validado
     * @param nomeCampo nome descritivo do campo
     * @param <T>       tipo do objeto
     * @return o próprio objeto validado
     */
    public static <T> T exigirNaoNulo(T valor, String nomeCampo) {
        if (valor == null) {
            throw new IllegalArgumentException(nomeCampo + " não pode ser nulo.");
        }
        return valor;
    }

    /**
     * Garante que um valor inteiro esteja dentro do intervalo inclusivo {@code [min, max]}.
     */
    public static int exigirIntervalo(int valor, int min, int max, String nomeCampo) {
        if (valor < min || valor > max) {
            throw new IllegalArgumentException(
                nomeCampo + " deve estar entre " + min + " e " + max + " (informado: " + valor + ")."
            );
        }
        return valor;
    }

    /**
     * Garante que um valor decimal não seja negativo.
     */
    public static double exigirNaoNegativo(double valor, String nomeCampo) {
        if (valor < 0) {
            throw new IllegalArgumentException(nomeCampo + " não pode ser negativo (informado: " + valor + ").");
        }
        return valor;
    }

    /**
     * Remove acentos e converte o texto para minúsculas, útil para chaves de busca normalizadas.
     *
     * @param texto texto de entrada
     * @return texto normalizado sem acentuação
     */
    public static String normalizarChave(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcentos = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return semAcentos.toLowerCase();
    }

    /**
     * Calcula um dígito verificador de CPF segundo o algoritmo oficial do Módulo 11.
     *
     * @param digitos     vetor com os dígitos do CPF
     * @param quantidade  quantidade de dígitos base (9 para DV1, 10 para DV2)
     * @param pesoInicial peso inicial da multiplicação (10 para DV1, 11 para DV2)
     * @return dígito verificador calculado (0 a 9)
     */
    public static int calcularDigitoVerificadorCpf(int[] digitos, int quantidade, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += digitos[i] * (pesoInicial - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    /**
     * Verifica se todos os primeiros {@code limite} dígitos de um array são iguais.
     */
    public static boolean todosDigitosIguais(int[] digitos, int limite) {
        for (int i = 1; i < limite; i++) {
            if (digitos[i] != digitos[0]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Valida matematicamente um CPF brasileiro (com ou sem pontuação),
     * verificando comprimento, repetição de dígitos e ambos os dígitos verificadores.
     *
     * @param cpf string do CPF a ser validado
     * @return {@code true} se o CPF for matematicamente válido
     */
    public static boolean validarCpf(String cpf) {
        if (cpf == null) {
            return false;
        }
        String apenasDigitos = cpf.replaceAll("\\D", "");
        if (apenasDigitos.length() != 11) {
            return false;
        }

        int[] digitos = new int[11];
        for (int i = 0; i < 11; i++) {
            digitos[i] = apenasDigitos.charAt(i) - '0';
        }

        if (todosDigitosIguais(digitos, 11)) {
            return false;
        }

        int dv1Esperado = calcularDigitoVerificadorCpf(digitos, 9, 10);
        int dv2Esperado = calcularDigitoVerificadorCpf(digitos, 10, 11);

        return digitos[9] == dv1Esperado && digitos[10] == dv2Esperado;
    }
}
