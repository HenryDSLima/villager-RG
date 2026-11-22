package br.unicap.modvillagers.servico;

import br.unicap.modvillagers.entidades.Villager;
import br.unicap.modvillagers.excecoes.CpfInvalidoException;
import br.unicap.modvillagers.excecoes.DocumentoInvalidoException;
import br.unicap.modvillagers.excecoes.VillagerDuplicadoException;
import br.unicap.modvillagers.excecoes.VillagerNaoEncontradoException;
import br.unicap.modvillagers.modelo.DocumentAttachment;
import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.util.ValidacaoUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Serviço central de gerenciamento do Registro Civil dos Villagers (Cartório).
 *
 * <p>Demonstra o uso idiomático das coleções do Java:</p>
 * <ul>
 *   <li>{@link Map}: indexação rápida de Villagers por CPF ({@code villagersPorCpf}),
 *       agrupamento por bioma e contagem censitária por profissão.</li>
 *   <li>{@link Set}: garantia de unicidade histórica de CPFs emitidos ({@code cpfsEmitidos})
 *       e listagem de biomas distintos cadastrados.</li>
 *   <li>{@link List}: listagem ordenada de cidadãos e agrupamentos por bioma.</li>
 * </ul>
 */
public class CartorioRegistroCivil {

    private final Map<String, Villager> villagersPorCpf;
    private final Set<String> cpfsEmitidos;
    private final DocumentGenerator gerador;

    public CartorioRegistroCivil() {
        this(new DocumentGenerator());
    }

    public CartorioRegistroCivil(DocumentGenerator gerador) {
        this.villagersPorCpf = new LinkedHashMap<>();
        this.cpfsEmitidos = new HashSet<>();
        this.gerador = ValidacaoUtil.exigirNaoNulo(gerador, "DocumentGenerator");
    }

    /**
     * Emite um novo documento civil garantindo CPF único (verificado no {@link Set}),
     * instancia o {@link Villager} e o cadastra no cartório.
     *
     * @param bioma            bioma de origem do Villager
     * @param profissaoInicial profissão inicial exercida
     * @return instância de {@link Villager} registrada
     */
    public Villager emitirERegistrarVillager(String bioma, String profissaoInicial) {
        String biomaValido = ValidacaoUtil.exigirTextoValido(bioma, "Bioma");
        String profissaoValida = ValidacaoUtil.exigirTextoValido(profissaoInicial, "Profissão inicial");

        String nome = gerador.gerarNome(biomaValido);
        String cpf = gerador.gerarCpfUnico(cpfsEmitidos);
        String rg = gerador.gerarRg();

        VillagerDocument documento = new VillagerDocument(nome, cpf, rg, biomaValido, LocalDate.now());
        DocumentAttachment attachment = new DocumentAttachment(documento);
        String entityId = UUID.randomUUID().toString().substring(0, 8);
        Villager villager = new Villager(entityId, biomaValido, profissaoValida, attachment);

        registrarVillager(villager);
        return villager;
    }

    /**
     * Cadastra um {@link Villager} existente no cartório, impedindo duplicidade de CPF.
     *
     * @param villager entidade a ser registrada
     * @throws DocumentoInvalidoException se o Villager não possuir documento ativo
     * @throws VillagerDuplicadoException se o CPF já tiver sido registrado anteriormente
     */
    public void registrarVillager(Villager villager) {
        ValidacaoUtil.exigirNaoNulo(villager, "Villager");
        DocumentAttachment attachment = villager.getDocumentAttachment();
        if (attachment == null || !attachment.hasDocumento()) {
            throw new DocumentoInvalidoException("Não é possível registrar um Villager sem documento civil ativo.");
        }

        String cpf = attachment.getDocumento().getCpf();
        if (villagersPorCpf.containsKey(cpf) || cpfsEmitidos.contains(cpf)) {
            throw new VillagerDuplicadoException(cpf);
        }

        villagersPorCpf.put(cpf, villager);
        cpfsEmitidos.add(cpf);
    }

    /**
     * Busca um {@link Villager} ativo no cartório a partir do seu CPF.
     *
     * @param cpf CPF do cidadão
     * @return {@link Villager} correspondente
     * @throws CpfInvalidoException          se o CPF informado for inválido
     * @throws VillagerNaoEncontradoException se não existir cadastro para o CPF
     */
    public Villager buscarPorCpf(String cpf) {
        if (cpf == null || !ValidacaoUtil.validarCpf(cpf)) {
            throw new CpfInvalidoException(cpf);
        }
        Villager encontrado = villagersPorCpf.get(cpf.trim());
        if (encontrado == null) {
            throw new VillagerNaoEncontradoException(cpf.trim());
        }
        return encontrado;
    }

    /**
     * Remove um {@link Villager} do cadastro ativo pelo CPF, mantendo o CPF no
     * conjunto histórico {@code cpfsEmitidos} para impedir reutilização.
     *
     * @param cpf CPF do Villager a ser removido
     * @return o {@link Villager} removido
     * @throws VillagerNaoEncontradoException se não existir cadastro ativo para o CPF
     */
    public Villager removerPorCpf(String cpf) {
        Villager villager = buscarPorCpf(cpf);
        villagersPorCpf.remove(cpf.trim());
        return villager;
    }

    /**
     * Retorna uma lista imutável ({@link List}) com todos os Villagers ativos no cartório.
     */
    public List<Villager> listarTodos() {
        return Collections.unmodifiableList(new ArrayList<>(villagersPorCpf.values()));
    }

    /**
     * Retorna o conjunto imutável ({@link Set}) de todos os CPFs já emitidos pelo cartório.
     */
    public Set<String> getCpfsEmitidos() {
        return Collections.unmodifiableSet(cpfsEmitidos);
    }

    /**
     * Retorna o conjunto imutável ({@link Set}) de biomas distintos entre os Villagers cadastrados.
     */
    public Set<String> listarBiomasRegistrados() {
        Set<String> biomas = new LinkedHashSet<>();
        for (Villager villager : villagersPorCpf.values()) {
            biomas.add(villager.getBiomaAtual());
        }
        return Collections.unmodifiableSet(biomas);
    }

    /**
     * Agrupa os Villagers cadastrados por bioma em um {@link Map} de listas.
     */
    public Map<String, List<Villager>> agruparPorBioma() {
        Map<String, List<Villager>> porBioma = new LinkedHashMap<>();
        for (Villager villager : villagersPorCpf.values()) {
            porBioma.computeIfAbsent(villager.getBiomaAtual(), chave -> new ArrayList<>())
                    .add(villager);
        }
        Map<String, List<Villager>> resultado = new LinkedHashMap<>();
        for (Map.Entry<String, List<Villager>> entrada : porBioma.entrySet()) {
            resultado.put(entrada.getKey(), Collections.unmodifiableList(entrada.getValue()));
        }
        return Collections.unmodifiableMap(resultado);
    }

    /**
     * Contabiliza a quantidade de Villagers por profissão atual em um {@link Map}.
     */
    public Map<String, Integer> contarPorProfissao() {
        Map<String, Integer> contagem = new LinkedHashMap<>();
        for (Villager villager : villagersPorCpf.values()) {
            contagem.merge(villager.getProfissaoAtual(), 1, Integer::sum);
        }
        return Collections.unmodifiableMap(contagem);
    }

    public int getTotalRegistrados() {
        return villagersPorCpf.size();
    }
}
