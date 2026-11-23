package br.unicap.modvillagers.persistencia;

import br.unicap.modvillagers.entidades.Villager;
import br.unicap.modvillagers.excecoes.PersistenciaNBTException;
import br.unicap.modvillagers.modelo.DocumentAttachment;
import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.padroes.strategy.DocumentoFormatStrategy;
import br.unicap.modvillagers.padroes.strategy.FormatoFichaCompletaStrategy;
import br.unicap.modvillagers.servico.CartorioRegistroCivil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Responsável pela persistência em disco dos dados NBT dos Villagers e pela
 * exportação de relatórios de censo civil utilizando recursos de Java I/O
 * ({@link BufferedReader}, {@link BufferedWriter}, {@link Files} e {@code try-with-resources}).
 *
 * <p>Integra-se ao padrão <b>Strategy</b> ({@link DocumentoFormatStrategy}) para permitir
 * a exportação de relatórios de censo em diferentes formatos de apresentação.</p>
 */
public class ArquivoNBTPersistencia {

    private static final String INICIO_BLOCO = "[VILLAGER]";
    private static final String FIM_BLOCO = "[END_VILLAGER]";
    private static final String PREFIXO_STRING = "S:";
    private static final String PREFIXO_LISTA = "L:";
    private static final String SEPARADOR_LISTA = "|";

    /**
     * Cria uma nova instância do serviço de persistência em arquivos NBT.
     */
    public ArquivoNBTPersistencia() {
    }

    /**
     * Salva uma lista de {@link Villager}s em um arquivo de dados NBT em formato textual estruturado (UTF-8).
     *
     * @param villagers      lista de Villagers ativos a serem persistidos
     * @param caminhoArquivo caminho do arquivo de destino
     * @throws PersistenciaNBTException se ocorrer falha de validação, serialização NBT ou I/O
     */
    public void salvarVillagers(List<Villager> villagers, Path caminhoArquivo) throws PersistenciaNBTException {
        if (villagers == null) {
            throw new PersistenciaNBTException("A lista de Villagers para salvamento não pode ser nula.");
        }
        if (caminhoArquivo == null) {
            throw new PersistenciaNBTException("O caminho do arquivo de destino não pode ser nulo.");
        }

        criarDiretorioPaiSeNecessario(caminhoArquivo);

        try (BufferedWriter writer = Files.newBufferedWriter(caminhoArquivo, StandardCharsets.UTF_8)) {
            writer.write("# Villager-RG NBT Data File");
            writer.newLine();

            for (Villager villager : villagers) {
                if (villager == null || !villager.isAtiva() || !villager.getDocumentAttachment().hasDocumento()) {
                    continue;
                }

                NBTTagCompound nbt = new NBTTagCompound();
                villager.salvarDados(nbt);

                writer.write(INICIO_BLOCO);
                writer.newLine();

                for (Map.Entry<String, String> entrada : nbt.getStringTags().entrySet()) {
                    writer.write(PREFIXO_STRING + entrada.getKey() + "=" + escapar(entrada.getValue()));
                    writer.newLine();
                }

                for (Map.Entry<String, List<String>> entrada : nbt.getListTags().entrySet()) {
                    List<String> itensEscapados = new ArrayList<>();
                    for (String item : entrada.getValue()) {
                        itensEscapados.add(escapar(item));
                    }
                    writer.write(PREFIXO_LISTA + entrada.getKey() + "=" + String.join(SEPARADOR_LISTA, itensEscapados));
                    writer.newLine();
                }

                writer.write(FIM_BLOCO);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaNBTException(
                "Erro de I/O ao salvar os registros NBT no arquivo '" + caminhoArquivo + "': " + e.getMessage(), e
            );
        }
    }

    /**
     * Lê um arquivo de dados NBT e reconstrói todas as instâncias de {@link Villager}
     * com seus respectivos {@link DocumentAttachment} e {@link VillagerDocument}.
     *
     * @param caminhoArquivo caminho do arquivo NBT salvo
     * @return lista de Villagers restaurados
     * @throws PersistenciaNBTException se o arquivo não existir, estiver corrompido ou houver falha de I/O
     */
    public List<Villager> carregarVillagers(Path caminhoArquivo) throws PersistenciaNBTException {
        if (caminhoArquivo == null) {
            throw new PersistenciaNBTException("O caminho do arquivo para leitura não pode ser nulo.");
        }
        if (!Files.exists(caminhoArquivo)) {
            throw new PersistenciaNBTException("Arquivo NBT não encontrado em: '" + caminhoArquivo + "'.");
        }

        List<Villager> restaurados = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(caminhoArquivo, StandardCharsets.UTF_8)) {
            NBTTagCompound nbtAtual = null;
            String linha;
            int numeroLinha = 0;

            while ((linha = reader.readLine()) != null) {
                numeroLinha++;
                String limpa = linha.trim();
                if (limpa.isEmpty() || limpa.startsWith("#")) {
                    continue;
                }

                if (INICIO_BLOCO.equals(limpa)) {
                    if (nbtAtual != null) {
                        throw new PersistenciaNBTException(
                            "Formato NBT inválido na linha " + numeroLinha + ": bloco [VILLAGER] aberto sem fechar o anterior."
                        );
                    }
                    nbtAtual = new NBTTagCompound();
                } else if (FIM_BLOCO.equals(limpa)) {
                    if (nbtAtual == null) {
                        throw new PersistenciaNBTException(
                            "Formato NBT inválido na linha " + numeroLinha + ": [END_VILLAGER] sem bloco aberto."
                        );
                    }
                    restaurados.add(reconstruirVillager(nbtAtual));
                    nbtAtual = null;
                } else {
                    if (nbtAtual == null) {
                        throw new PersistenciaNBTException(
                            "Formato NBT inválido na linha " + numeroLinha + ": conteúdo fora de um bloco [VILLAGER]."
                        );
                    }
                    processarLinhaTag(limpa, numeroLinha, nbtAtual);
                }
            }

            if (nbtAtual != null) {
                throw new PersistenciaNBTException("Formato NBT inválido: arquivo terminou com bloco [VILLAGER] sem fechamento.");
            }
        } catch (IOException e) {
            throw new PersistenciaNBTException(
                "Erro de I/O ao ler o arquivo NBT '" + caminhoArquivo + "': " + e.getMessage(), e
            );
        }

        return restaurados;
    }

    /**
     * Exporta um relatório textual do censo civil utilizando a estratégia padrão ({@link FormatoFichaCompletaStrategy}).
     *
     * @param cartorio         instância do cartório contendo os registros
     * @param caminhoRelatorio caminho do arquivo de relatório de saída
     * @throws PersistenciaNBTException se ocorrer erro de validação ou escrita em disco
     */
    public void exportarRelatorioCenso(CartorioRegistroCivil cartorio, Path caminhoRelatorio) throws PersistenciaNBTException {
        exportarRelatorioCenso(cartorio, caminhoRelatorio, new FormatoFichaCompletaStrategy());
    }

    /**
     * Exporta um relatório textual do censo civil formatando cada cidadão conforme
     * a estratégia {@link DocumentoFormatStrategy} informada (padrão Strategy).
     *
     * @param cartorio         instância do cartório contendo os registros
     * @param caminhoRelatorio caminho do arquivo de relatório de saída
     * @param estrategia       estratégia de formatação para os documentos listados
     * @throws PersistenciaNBTException se ocorrer erro de validação ou escrita em disco
     */
    public void exportarRelatorioCenso(
            CartorioRegistroCivil cartorio,
            Path caminhoRelatorio,
            DocumentoFormatStrategy estrategia) throws PersistenciaNBTException {
        if (cartorio == null) {
            throw new PersistenciaNBTException("Cartório não pode ser nulo para exportação do relatório.");
        }
        if (caminhoRelatorio == null) {
            throw new PersistenciaNBTException("Caminho do relatório não pode ser nulo.");
        }
        if (estrategia == null) {
            throw new PersistenciaNBTException("Estratégia de formatação não pode ser nula.");
        }

        criarDiretorioPaiSeNecessario(caminhoRelatorio);

        try (BufferedWriter writer = Files.newBufferedWriter(caminhoRelatorio, StandardCharsets.UTF_8)) {
            writer.write("============================================================");
            writer.newLine();
            writer.write("         RELATÓRIO DE CENSO CIVIL - VILLAGER-RG");
            writer.newLine();
            writer.write("         Formato utilizado: " + estrategia.getNomeFormato());
            writer.newLine();
            writer.write("============================================================");
            writer.newLine();
            writer.write("Total de Villagers ativos: " + cartorio.getTotalRegistrados());
            writer.newLine();
            writer.write("Total histórico de CPFs emitidos: " + cartorio.getCpfsEmitidos().size());
            writer.newLine();
            writer.write("Biomas registrados: " + cartorio.listarBiomasRegistrados());
            writer.newLine();
            writer.newLine();

            writer.write("--- Distribuição por Profissão ---");
            writer.newLine();
            for (Map.Entry<String, Integer> entrada : cartorio.contarPorProfissao().entrySet()) {
                writer.write(" - " + entrada.getKey() + ": " + entrada.getValue());
                writer.newLine();
            }
            writer.newLine();

            writer.write("--- Cidadãos Cadastrados ---");
            writer.newLine();
            for (Villager villager : cartorio.listarTodos()) {
                VillagerDocument doc = villager.getDocumentAttachment().getDocumento();
                writer.write(doc.formatar(estrategia, villager.getProfissaoAtual()));
                writer.newLine();
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaNBTException(
                "Erro de I/O ao exportar relatório de censo para '" + caminhoRelatorio + "': " + e.getMessage(), e
            );
        }
    }

    private void criarDiretorioPaiSeNecessario(Path caminhoArquivo) throws PersistenciaNBTException {
        Path diretorioPai = caminhoArquivo.getParent();
        if (diretorioPai != null && !Files.exists(diretorioPai)) {
            try {
                Files.createDirectories(diretorioPai);
            } catch (IOException e) {
                throw new PersistenciaNBTException(
                    "Não foi possível criar o diretório '" + diretorioPai + "': " + e.getMessage(), e
                );
            }
        }
    }

    private void processarLinhaTag(String linha, int numeroLinha, NBTTagCompound nbt) throws PersistenciaNBTException {
        int posIgual = linha.indexOf('=');
        if (posIgual <= 2) {
            throw new PersistenciaNBTException(
                "Linha NBT malformada na linha " + numeroLinha + ": '" + linha + "'."
            );
        }

        String prefixo = linha.substring(0, 2);
        String chave = linha.substring(2, posIgual).trim();
        String valorBruto = linha.substring(posIgual + 1);

        if (PREFIXO_STRING.equals(prefixo)) {
            nbt.setString(chave, desescapar(valorBruto));
        } else if (PREFIXO_LISTA.equals(prefixo)) {
            List<String> lista = dividirListaEscapada(valorBruto);
            nbt.setStringList(chave, lista);
        } else {
            throw new PersistenciaNBTException(
                "Prefixo de tag NBT desconhecido na linha " + numeroLinha + ": '" + prefixo + "'."
            );
        }
    }

    private Villager reconstruirVillager(NBTTagCompound nbt) throws PersistenciaNBTException {
        DocumentAttachment attachment = new DocumentAttachment();
        attachment.readFromNBT(nbt);

        VillagerDocument doc = attachment.getDocumento();
        String entityId = nbt.hasKey(Villager.TAG_ENTITY_ID)
                ? nbt.getString(Villager.TAG_ENTITY_ID)
                : UUID.randomUUID().toString().substring(0, 8);

        String profissaoAtual = nbt.hasKey(Villager.TAG_PROFISSAO_ATUAL)
                ? nbt.getString(Villager.TAG_PROFISSAO_ATUAL)
                : (!doc.getHistoricoProfissoes().isEmpty()
                    ? doc.getHistoricoProfissoes().get(doc.getHistoricoProfissoes().size() - 1)
                    : "Desempregado");

        return new Villager(entityId, doc.getBiomaOrigem(), profissaoAtual, attachment);
    }

    private static String escapar(String texto) {
        return texto
                .replace("\\", "\\\\")
                .replace("|", "\\p")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    private static String desescapar(String texto) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '\\' && i + 1 < texto.length()) {
                char proximo = texto.charAt(++i);
                if (proximo == 'p') {
                    resultado.append('|');
                } else if (proximo == 'n') {
                    resultado.append('\n');
                } else {
                    resultado.append(proximo);
                }
            } else {
                resultado.append(c);
            }
        }
        return resultado.toString();
    }

    private static List<String> dividirListaEscapada(String valorBruto) {
        List<String> itens = new ArrayList<>();
        if (valorBruto.isEmpty()) {
            return itens;
        }
        StringBuilder atual = new StringBuilder();
        for (int i = 0; i < valorBruto.length(); i++) {
            char c = valorBruto.charAt(i);
            if (c == '\\' && i + 1 < valorBruto.length()) {
                atual.append(c).append(valorBruto.charAt(++i));
            } else if (c == '|') {
                itens.add(desescapar(atual.toString()));
                atual.setLength(0);
            } else {
                atual.append(c);
            }
        }
        itens.add(desescapar(atual.toString()));
        return itens;
    }
}
