package br.unicap.modvillagers.servico;

import br.unicap.modvillagers.util.ValidacaoUtil;

import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Classe de serviço responsável por isolar a lógica matemática e aleatória
 * de geração de documentos civis para os Villagers (CPF válido, RG e nome por bioma).
 *
 * <p>Reutiliza {@link ValidacaoUtil} para evitar duplicação da lógica de cálculo
 * de dígitos verificadores e normalização de biomas.</p>
 */
public class DocumentGenerator {

    private static final int MAX_TENTATIVAS_CPF_UNICO = 1000;

    private static final String[] NOMES_PLANICIE = {
        "Aldo", "Beatriz", "Carlos", "Davi", "Elena", "Fernando", "Helena", "Lucas", "Mariana", "Pedro"
    };
    private static final String[] SOBRENOMES_PLANICIE = {
        "Silva", "Campos", "Oliveira", "Prado", "Mendes", "Ferreira", "Costa", "Ribeiro"
    };

    private static final String[] NOMES_DESERTO = {
        "Amir", "Farah", "Hassan", "Jamal", "Layla", "Nadia", "Omar", "Samira", "Tariq", "Yasmin"
    };
    private static final String[] SOBRENOMES_DESERTO = {
        "Al-Mansur", "Dunas", "Oasis", "Saara", "Miragem", "Areia", "Solaris", "Sultão"
    };

    private static final String[] NOMES_TAIGA = {
        "Astrid", "Bjorn", "Erik", "Freya", "Gunnar", "Ingrid", "Leif", "Sigrid", "Soren", "Torin"
    };
    private static final String[] SOBRENOMES_TAIGA = {
        "Pinheiro", "Norte", "Lobo", "Nevoeiro", "Cedro", "Vento", "Bosque", "Montanha"
    };

    private static final String[] NOMES_SAVANA = {
        "Amani", "Bako", "Chidi", "Dalila", "Imani", "Jengo", "Kofi", "Nia", "Sefu", "Zuri"
    };
    private static final String[] SOBRENOMES_SAVANA = {
        "Acácia", "Horizonte", "Sol", "Planaltos", "Baobá", "Terra", "Ocre", "Vento"
    };

    private static final String[] NOMES_NEVE = {
        "Anya", "Boris", "Dmitri", "Elsa", "Ivan", "Katya", "Mikhail", "Natasha", "Nikolai", "Svetlana"
    };
    private static final String[] SOBRENOMES_NEVE = {
        "Geada", "Inverno", "Cristal", "Gelo", "Aurora", "Tundra", "Nevasca", "Boreal"
    };

    private static final String[] NOMES_PANTANO = {
        "Breno", "Célia", "Dante", "Gisela", "Hugo", "Íris", "Lauro", "Mirela", "Otávio", "Sônia"
    };
    private static final String[] SOBRENOMES_PANTANO = {
        "Brejo", "Salgueiro", "Musgo", "Lagoa", "Névoa", "Vitória", "Turfa", "Riacho"
    };

    private static final String[] NOMES_SELVA = {
        "Cauã", "Iara", "Jaci", "Kauê", "Maiara", "Moacir", "Nayara", "Raoni", "Tainá", "Ubirajara"
    };
    private static final String[] SOBRENOMES_SELVA = {
        "Cipó", "Esmeralda", "Floresta", "Bambu", "Cacau", "Orquídea", "Rio", "Mata"
    };

    private static final Map<String, String[][]> POOL_POR_BIOMA = Map.of(
        "planicie", new String[][] { NOMES_PLANICIE, SOBRENOMES_PLANICIE },
        "deserto", new String[][] { NOMES_DESERTO, SOBRENOMES_DESERTO },
        "taiga", new String[][] { NOMES_TAIGA, SOBRENOMES_TAIGA },
        "savana", new String[][] { NOMES_SAVANA, SOBRENOMES_SAVANA },
        "neve", new String[][] { NOMES_NEVE, SOBRENOMES_NEVE },
        "pantano", new String[][] { NOMES_PANTANO, SOBRENOMES_PANTANO },
        "selva", new String[][] { NOMES_SELVA, SOBRENOMES_SELVA }
    );

    private final Random random;

    /**
     * Cria um gerador de documentos utilizando uma fonte pseudoaleatória padrão.
     */
    public DocumentGenerator() {
        this(new Random());
    }

    /**
     * Cria um gerador de documentos utilizando uma instância customizada de {@link Random}
     * (recomendado para testes unitários determinísticos).
     *
     * @param random gerador pseudoaleatório
     */
    public DocumentGenerator(Random random) {
        this.random = ValidacaoUtil.exigirNaoNulo(random, "Gerador Random");
    }

    /**
     * Gera um número de CPF brasileiro matematicamente válido segundo o algoritmo
     * oficial do Módulo 11, formatado como {@code XXX.XXX.XXX-XX}.
     *
     * @return CPF válido e formatado
     */
    public String gerarCpfValido() {
        int[] digitos = new int[11];

        do {
            for (int i = 0; i < 9; i++) {
                digitos[i] = random.nextInt(10);
            }
        } while (ValidacaoUtil.todosDigitosIguais(digitos, 9));

        digitos[9] = ValidacaoUtil.calcularDigitoVerificadorCpf(digitos, 9, 10);
        digitos[10] = ValidacaoUtil.calcularDigitoVerificadorCpf(digitos, 10, 11);

        return String.format(
            "%d%d%d.%d%d%d.%d%d%d-%d%d",
            digitos[0], digitos[1], digitos[2],
            digitos[3], digitos[4], digitos[5],
            digitos[6], digitos[7], digitos[8],
            digitos[9], digitos[10]
        );
    }

    /**
     * Gera um CPF válido garantindo que ele não colida com nenhum CPF presente
     * no conjunto ({@link Set}) de CPFs já emitidos.
     *
     * @param cpfsExistentes conjunto de CPFs já em uso
     * @return CPF único e matematicamente válido
     */
    public String gerarCpfUnico(Set<String> cpfsExistentes) {
        ValidacaoUtil.exigirNaoNulo(cpfsExistentes, "Conjunto de CPFs existentes");
        for (int tentativa = 0; tentativa < MAX_TENTATIVAS_CPF_UNICO; tentativa++) {
            String candidato = gerarCpfValido();
            if (!cpfsExistentes.contains(candidato)) {
                return candidato;
            }
        }
        throw new IllegalStateException("Não foi possível gerar um CPF único após " + MAX_TENTATIVAS_CPF_UNICO + " tentativas.");
    }

    /**
     * Valida matematicamente um número de CPF conferindo ambos os dígitos verificadores.
     *
     * @param cpf string contendo o CPF a ser validado
     * @return {@code true} se o CPF for válido; {@code false} caso contrário
     */
    public static boolean validarCpf(String cpf) {
        return ValidacaoUtil.validarCpf(cpf);
    }

    /**
     * Gera um número de RG formatado como {@code XX.XXX.XXX-X} com dígito verificador.
     *
     * @return RG formatado
     */
    public String gerarRg() {
        int[] base = new int[8];
        base[0] = 1 + random.nextInt(9);
        for (int i = 1; i < 8; i++) {
            base[i] = random.nextInt(10);
        }

        int soma = 0;
        for (int i = 0; i < 8; i++) {
            soma += base[i] * (2 + i);
        }
        int resto = soma % 11;
        String dv;
        if (resto == 0) {
            dv = "0";
        } else if (resto == 1 || resto == 10) {
            dv = "X";
        } else {
            dv = String.valueOf(11 - resto);
        }

        return String.format(
            "%d%d.%d%d%d.%d%d%d-%s",
            base[0], base[1],
            base[2], base[3], base[4],
            base[5], base[6], base[7],
            dv
        );
    }

    /**
     * Gera um nome completo padrão para um Villager (bioma Planície).
     *
     * @return nome e sobrenome sorteados
     */
    public String gerarNome() {
        return gerarNome("Planície");
    }

    /**
     * Gera um nome completo contextualizado ao bioma de origem do Villager.
     *
     * @param bioma nome do bioma
     * @return nome e sobrenome sorteados
     */
    public String gerarNome(String bioma) {
        String chave = ValidacaoUtil.normalizarChave(bioma);
        String[][] pool = POOL_POR_BIOMA.getOrDefault(
            chave,
            new String[][] { NOMES_PLANICIE, SOBRENOMES_PLANICIE }
        );

        String primeiroNome = pool[0][random.nextInt(pool[0].length)];
        String sobrenome = pool[1][random.nextInt(pool[1].length)];
        return primeiroNome + " " + sobrenome;
    }

    /**
     * Retorna o conjunto de chaves normalizadas dos biomas com listas de nomes dedicadas.
     *
     * @return conjunto de biomas suportados
     */
    public static Set<String> getBiomasSuportados() {
        return POOL_POR_BIOMA.keySet();
    }
}
