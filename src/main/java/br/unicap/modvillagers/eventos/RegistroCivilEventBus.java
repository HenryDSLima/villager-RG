package br.unicap.modvillagers.eventos;

import br.unicap.modvillagers.itens.ItemRegistrable;
import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.padroes.builder.VillagerDocumentBuilder;
import br.unicap.modvillagers.padroes.observer.AuditoriaHistoricoObserver;
import br.unicap.modvillagers.padroes.observer.EventoRegistroCivil;
import br.unicap.modvillagers.padroes.observer.EventoRegistroCivil.TipoEvento;
import br.unicap.modvillagers.padroes.observer.LoggerModObserver;
import br.unicap.modvillagers.padroes.observer.RegistroCivilObserver;
import br.unicap.modvillagers.servico.DocumentGenerator;
import br.unicap.modvillagers.util.ValidacaoUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Barramento central de eventos do mod Villager-RG atuando como <b>Subject</b> no padrão
 * <b>Observer (GoF)</b> e mantendo o censo em memória dos Villagers carregados no servidor
 * com {@link Map}, {@link Set} e {@link List}.
 */
public class RegistroCivilEventBus {

    private static final RegistroCivilEventBus INSTANCE = new RegistroCivilEventBus();

    private final DocumentGenerator gerador;
    private final Set<String> cpfsEmitidos;
    private final Map<UUID, VillagerDocument> documentosCarregados;
    private final Map<UUID, String> profissoesAtuais;
    private final Map<String, ItemRegistrable> itensPorId;
    private final List<RegistroCivilObserver> observadores;
    private final AuditoriaHistoricoObserver auditoriaObserver;

    private RegistroCivilEventBus() {
        this.gerador = new DocumentGenerator();
        this.cpfsEmitidos = ConcurrentHashMap.newKeySet();
        this.documentosCarregados = new ConcurrentHashMap<>();
        this.profissoesAtuais = new ConcurrentHashMap<>();
        this.itensPorId = new ConcurrentHashMap<>();
        this.observadores = new ArrayList<>();
        this.auditoriaObserver = new AuditoriaHistoricoObserver();

        adicionarObservador(new LoggerModObserver());
        adicionarObservador(this.auditoriaObserver);
    }

    /**
     * Retorna a instância ativa do barramento de eventos do mod.
     *
     * @return instância de {@link RegistroCivilEventBus}
     */
    public static RegistroCivilEventBus getInstance() {
        return INSTANCE;
    }

    /**
     * Inscreve um observador ({@link RegistroCivilObserver}) no barramento.
     *
     * @param observador observador a adicionar
     */
    public synchronized void adicionarObservador(RegistroCivilObserver observador) {
        ValidacaoUtil.exigirNaoNulo(observador, "RegistroCivilObserver");
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    /**
     * Remove um observador previamente inscrito no barramento.
     *
     * @param observador observador a remover
     * @return {@code true} se o observador foi removido
     */
    public synchronized boolean removerObservador(RegistroCivilObserver observador) {
        return observadores.remove(observador);
    }

    /**
     * Notifica todos os observadores inscritos sobre um {@link EventoRegistroCivil}.
     *
     * @param evento evento publicado
     */
    public void notificarObservadores(EventoRegistroCivil evento) {
        ValidacaoUtil.exigirNaoNulo(evento, "EventoRegistroCivil");
        List<RegistroCivilObserver> copia;
        synchronized (this) {
            copia = new ArrayList<>(observadores);
        }
        for (RegistroCivilObserver observador : copia) {
            observador.onEvento(evento);
        }
    }

    /**
     * Retorna o observador de auditoria integrado para consultas em comandos no jogo.
     *
     * @return instância de {@link AuditoriaHistoricoObserver}
     */
    public AuditoriaHistoricoObserver getAuditoriaObserver() {
        return auditoriaObserver;
    }

    /**
     * Registra um item customizado que cumpre {@link ItemRegistrable}.
     *
     * @param item item registrável
     */
    public void registrarItem(ItemRegistrable item) {
        ValidacaoUtil.exigirNaoNulo(item, "ItemRegistrable");
        item.registrar();
        itensPorId.put(item.getRegistryId(), item);
        notificarObservadores(new EventoRegistroCivil(
            TipoEvento.ITEM_REGISTRADO,
            "Item registrado: " + item.getNomeExibicao() + " (" + item.getRegistryId() + ")"
        ));
    }

    /**
     * Emite um novo {@link VillagerDocument} via {@link VillagerDocumentBuilder} garantindo
     * unicidade do CPF no {@link Set} global e o indexa no censo ativo.
     *
     * @param entityUuid       UUID nativo do Villager no Minecraft
     * @param bioma            bioma traduzido do Villager
     * @param profissaoInicial profissão inicial traduzida
     * @return documento civil recém-emitido
     */
    public synchronized VillagerDocument emitirDocumentoParaVillager(
            UUID entityUuid,
            String bioma,
            String profissaoInicial) {
        String cpf = gerador.gerarCpfUnico(cpfsEmitidos);
        VillagerDocument documento = new VillagerDocumentBuilder()
                .comNome(gerador.gerarNome(bioma))
                .comCpf(cpf)
                .comRg(gerador.gerarRg())
                .comBiomaOrigem(bioma)
                .comDataGeracao(LocalDate.now())
                .comProfissao(profissaoInicial)
                .build();

        cpfsEmitidos.add(cpf);
        if (entityUuid != null) {
            documentosCarregados.put(entityUuid, documento);
            profissoesAtuais.put(entityUuid, profissaoInicial);
        }

        notificarObservadores(new EventoRegistroCivil(
            TipoEvento.VILLAGER_NASCIDO,
            "Documento emitido -> " + documento.getNome() + " | CPF: " + documento.getCpf()
                    + " | Bioma: " + documento.getBiomaOrigem(),
            documento
        ));
        return documento;
    }

    /**
     * Indexa um documento civil previamente carregado do NBT do mundo.
     *
     * @param entityUuid     UUID do Villager
     * @param documento      documento restaurado do NBT
     * @param profissaoAtual profissão atual do Villager
     */
    public void registrarVillagerCarregado(UUID entityUuid, VillagerDocument documento, String profissaoAtual) {
        if (entityUuid == null || documento == null) {
            return;
        }
        cpfsEmitidos.add(documento.getCpf());
        documentosCarregados.put(entityUuid, documento);
        profissoesAtuais.put(entityUuid, profissaoAtual != null ? profissaoAtual : "Desempregado");
    }

    /**
     * Remove o Villager do índice de entidades carregadas quando o chunk é descarregado ou a entidade morre.
     *
     * @param entityUuid UUID do Villager
     */
    public void onVillagerDescarregado(UUID entityUuid) {
        if (entityUuid == null) {
            return;
        }
        VillagerDocument removido = documentosCarregados.remove(entityUuid);
        profissoesAtuais.remove(entityUuid);
        if (removido != null) {
            notificarObservadores(new EventoRegistroCivil(
                TipoEvento.VILLAGER_REMOVIDO,
                "Villager descarregado do censo ativo: " + removido.getNome() + " (" + removido.getCpf() + ")",
                removido
            ));
        }
    }

    /**
     * Notifica uma mudança de profissão registrada em um Villager.
     *
     * @param entityUuid    UUID do Villager
     * @param documento     documento civil atualizado
     * @param novaProfissao nova profissão assumida
     */
    public void onProfissaoAtualizada(UUID entityUuid, VillagerDocument documento, String novaProfissao) {
        if (documento == null || novaProfissao == null || novaProfissao.isBlank()) {
            return;
        }
        if (entityUuid != null) {
            profissoesAtuais.put(entityUuid, novaProfissao);
        }
        notificarObservadores(new EventoRegistroCivil(
            TipoEvento.PROFISSAO_ATUALIZADA,
            documento.getNome() + " assumiu nova profissão: " + novaProfissao,
            documento
        ));
    }

    /**
     * Notifica que um jogador inspecionou um Villager usando a Prancheta de Recenseamento.
     *
     * @param nomeJogador nome do jogador
     * @param documento   documento inspecionado
     */
    public void onCidadaoInspecionado(String nomeJogador, VillagerDocument documento) {
        if (documento == null) {
            return;
        }
        notificarObservadores(new EventoRegistroCivil(
            TipoEvento.CIDADAO_INSPECIONADO,
            "Jogador '" + nomeJogador + "' inspecionou " + documento.getNome() + " (CPF: " + documento.getCpf() + ")",
            documento
        ));
    }

    /**
     * Retorna a quantidade de Villagers atualmente carregados no censo ativo.
     *
     * @return total de cidadãos ativos
     */
    public int getTotalCarregados() {
        return documentosCarregados.size();
    }

    /**
     * Retorna a quantidade total histórica de CPFs únicos emitidos na sessão.
     *
     * @return total de CPFs no {@link Set}
     */
    public int getTotalCpfsEmitidos() {
        return cpfsEmitidos.size();
    }

    /**
     * Retorna o conjunto de biomas distintos entre os Villagers carregados.
     *
     * @return conjunto imutável de nomes de biomas
     */
    public Set<String> listarBiomasAtivos() {
        Set<String> biomas = new LinkedHashSet<>();
        for (VillagerDocument doc : documentosCarregados.values()) {
            biomas.add(doc.getBiomaOrigem());
        }
        return Collections.unmodifiableSet(biomas);
    }

    /**
     * Contabiliza a quantidade de Villagers carregados por profissão atual em um {@link Map}.
     *
     * @return mapa imutável de profissão para quantidade
     */
    public Map<String, Integer> contarPorProfissao() {
        Map<String, Integer> contagem = new LinkedHashMap<>();
        for (String profissao : profissoesAtuais.values()) {
            contagem.merge(profissao, 1, Integer::sum);
        }
        return Collections.unmodifiableMap(contagem);
    }
}
