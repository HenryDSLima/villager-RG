package br.unicap.modvillagers.servico;

import java.util.Map;
import java.util.Random;

/**
 * Classe de serviço responsável por isolar a lógica matemática e aleatória
 * de geração de documentos civis para os Villagers (CPF válido, RG e nome por bioma).
 *
 * <p>Conforme a modelagem da Etapa 2, mantém alta coesão e baixo acoplamento,
 * sendo utilizada via relação de dependência por {@code DocumentAttachment}.</p>
 */
public class DocumentGenerator {

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
     * Construtor padrão utilizando gerador pseudoaleatório do sistema.
     */
    public DocumentGenerator() {
        this(new Random());
    }

    /**
     * Construtor sobrecarregado que aceita uma instância customizada de {@link Random}
     * (útil para testes determinísticos).
     *
     * @param random gerador de números aleatórios
     */
    public DocumentGenerator(Random random) {
        if (random == null) {
            throw new IllegalArgumentException("Gerador Random não pode ser nulo.");
        }
        this.random = random;
    }

    /**
     * Gera um número de CPF brasileiro matematicamente válido segundo o algoritmo
     * oficial do Módulo 11 (dois dígitos verificadores), formatado como {@code XXX.XXX.XXX-XX}.
     *
     * @return CPF válido e formatado
     */
    public String gerarCpfValido() {
        int[] digitos = new int[11];

        do {
            for (int i = 0; i < 9; i++) {
                digitos[i] = random.nextInt(10);
            }
        } while (todosDigitosIguais(digitos, 9));

        digitos[9] = calcularDigitoVerificadorCpf(digitos, 9, 10);
        digitos[10] = calcularDigitoVerificadorCpf(digitos, 10, 11);

        return String.format(
            "%d%d%d.%d%d%d.%d%d%d-%d%d",
            digitos[0], digitos[1], digitos[2],
            digitos[3], digitos[4], digitos[5],
            digitos[6], digitos[7], digitos[8],
            digitos[9], digitos[10]
        );
    }

    /**
     * Valida matematicamente um número de CPF (com ou sem pontuação)
     * conferindo seus dois dígitos verificadores pelo algoritmo do Módulo 11.
     *
     * @param cpf string contendo o CPF a ser validado
     * @return {@code true} se o CPF for válido; {@code false} caso contrário
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
        if (resto == 10) {
            dv = "X";
        } else if (resto == 0) {
            dv = "0";
        } else {
            dv = String.valueOf(11 - resto == 10 ? "X" : 11 - resto);
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
     * @param bioma nome do bioma (ex.: Planície, Deserto, Taiga, Savana, Neve, Pântano, Selva)
     * @return nome e sobrenome sorteados para o bioma informado
     */
    public String gerarNome(String bioma) {
        String chave = normalizarBioma(bioma);
        String[][] pool = POOL_POR_BIOMA.getOrDefault(
            chave,
            new String[][] { NOMES_PLANICIE, SOBRENOMES_PLANICIE }
        );

        String primeiroNome = pool[0][random.nextInt(pool[0].length)];
        String sobrenome = pool[1][random.nextInt(pool[1].length)];
        return primeiroNome + " " + sobrenome;
    }

    private static int calcularDigitoVerificadorCpf(int[] digitos, int quantidade, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += digitos[i] * (pesoInicial - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static boolean todosDigitosIguais(int[] digitos, int limite) {
        for (int i = 1; i < limite; i++) {
            if (digitos[i] != digitos[0]) {
                return false;
            }
        }
        return true;
    }

    private static String normalizarBioma(String bioma) {
        if (bioma == null) {
            return "planicie";
        }
        return bioma.trim()
            .toLowerCase()
            .replace("í", "i")
            .replace("â", "a")
            .replace("á", "a")
            .replace("é", "e")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ã", "a")
            .replace("õ", "o")
            .replace("ç", "c");
    }
}
