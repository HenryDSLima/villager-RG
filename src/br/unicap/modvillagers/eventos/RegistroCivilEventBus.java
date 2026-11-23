package br.unicap.modvillagers.eventos;

import br.unicap.modvillagers.entidades.LivingEntity;
import br.unicap.modvillagers.entidades.Player;
import br.unicap.modvillagers.entidades.Villager;
import br.unicap.modvillagers.itens.Item;
import br.unicap.modvillagers.itens.ItemRegistrable;
import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.padroes.observer.ConsoleLogObserver;
import br.unicap.modvillagers.padroes.observer.EventoRegistroCivil;
import br.unicap.modvillagers.padroes.observer.EventoRegistroCivil.TipoEvento;
import br.unicap.modvillagers.padroes.observer.RegistroCivilObserver;
import br.unicap.modvillagers.servico.CartorioRegistroCivil;
import br.unicap.modvillagers.util.ValidacaoUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Simula o barramento de eventos ({@code Event Bus}) do Mod Loader Fabric, atuando
 * como <b>Subject</b> no padrão <b>Observer (GoF)</b>.
 *
 * <p>Gerencia o registro polimórfico de itens ({@link ItemRegistrable}) em um {@link Map}
 * indexado por {@code registryId}, integra-se ao {@link CartorioRegistroCivil} e notifica
 * dinamicamente todos os {@link RegistroCivilObserver}s inscritos sempre que um evento
 * civil ocorre.</p>
 */
public class RegistroCivilEventBus {

    private final Map<String, ItemRegistrable> itensPorRegistryId;
    private final CartorioRegistroCivil cartorio;
    private final List<RegistroCivilObserver> observadores;

    /**
     * Cria um barramento de eventos com um novo {@link CartorioRegistroCivil} e
     * {@link ConsoleLogObserver} inscrito por padrão.
     */
    public RegistroCivilEventBus() {
        this(new CartorioRegistroCivil(), true);
    }

    /**
     * Cria um barramento de eventos associado ao cartório informado, com
     * {@link ConsoleLogObserver} inscrito por padrão.
     *
     * @param cartorio instância de {@link CartorioRegistroCivil}
     */
    public RegistroCivilEventBus(CartorioRegistroCivil cartorio) {
        this(cartorio, true);
    }

    /**
     * Cria um barramento de eventos permitindo configurar se o observador padrão de console
     * deve ser inscrito automaticamente.
     *
     * @param cartorio                   instância de {@link CartorioRegistroCivil}
     * @param registrarLogConsolePadrao  se {@code true}, inscreve {@link ConsoleLogObserver}
     */
    public RegistroCivilEventBus(CartorioRegistroCivil cartorio, boolean registrarLogConsolePadrao) {
        this.itensPorRegistryId = new LinkedHashMap<>();
        this.cartorio = ValidacaoUtil.exigirNaoNulo(cartorio, "CartorioRegistroCivil");
        this.observadores = new ArrayList<>();
        if (registrarLogConsolePadrao) {
            adicionarObservador(new ConsoleLogObserver());
        }
    }

    /**
     * Inscreve um observador ({@link RegistroCivilObserver}) para receber eventos do barramento.
     *
     * @param observador instância de observador a ser inscrita
     */
    public void adicionarObservador(RegistroCivilObserver observador) {
        ValidacaoUtil.exigirNaoNulo(observador, "RegistroCivilObserver");
        if (!this.observadores.contains(observador)) {
            this.observadores.add(observador);
        }
    }

    /**
     * Remove um observador previamente inscrito no barramento.
     *
     * @param observador instância de observador a ser removida
     * @return {@code true} se o observador estava inscrito e foi removido
     */
    public boolean removerObservador(RegistroCivilObserver observador) {
        return this.observadores.remove(observador);
    }

    /**
     * Retorna uma visualização imutável dos observadores atualmente inscritos.
     *
     * @return lista não modificável de observadores
     */
    public List<RegistroCivilObserver> getObservadores() {
        return Collections.unmodifiableList(observadores);
    }

    /**
     * Notifica todos os observadores inscritos sobre o {@link EventoRegistroCivil} ocorrido.
     *
     * @param evento evento a ser distribuído aos observadores
     */
    public void notificarObservadores(EventoRegistroCivil evento) {
        ValidacaoUtil.exigirNaoNulo(evento, "EventoRegistroCivil");
        for (RegistroCivilObserver observador : new ArrayList<>(observadores)) {
            observador.onEvento(evento);
        }
    }

    /**
     * Registra qualquer item que implemente {@link ItemRegistrable}, indexando-o
     * pelo seu {@code registryId} em um {@link Map}, e notifica os observadores.
     *
     * @param item item registrável a ser adicionado
     */
    public void registrarItem(ItemRegistrable item) {
        ValidacaoUtil.exigirNaoNulo(item, "Item registrável");
        item.registrar();
        this.itensPorRegistryId.put(item.getRegistryId(), item);
        notificarObservadores(new EventoRegistroCivil(
            TipoEvento.ITEM_REGISTRADO,
            "Item registrado: " + item.getNomeExibicao() + " (" + item.getRegistryId() + ")"
        ));
    }

    /**
     * Busca um item registrado no barramento a partir de seu {@code registryId}.
     *
     * @param registryId identificador único de registro do item
     * @return item correspondente ou {@code null} se não estiver registrado
     */
    public ItemRegistrable buscarItemRegistrado(String registryId) {
        String idValido = ValidacaoUtil.exigirTextoValido(registryId, "Registry ID");
        return this.itensPorRegistryId.get(idValido);
    }

    /**
     * Retorna uma lista imutável contendo todos os itens registrados no barramento.
     *
     * @return lista de itens registráveis
     */
    public List<ItemRegistrable> getItensRegistrados() {
        return Collections.unmodifiableList(new ArrayList<>(itensPorRegistryId.values()));
    }

    /**
     * Retorna o mapa imutável de itens registrados indexados por {@code registryId}.
     *
     * @return mapa de itens registráveis
     */
    public Map<String, ItemRegistrable> getMapaItensRegistrados() {
        return Collections.unmodifiableMap(itensPorRegistryId);
    }

    /**
     * Retorna o {@link CartorioRegistroCivil} gerenciado por este barramento.
     *
     * @return instância do cartório
     */
    public CartorioRegistroCivil getCartorio() {
        return cartorio;
    }

    /**
     * Caso de uso: Emitir Documento de Identidade no surgimento (spawn) de um Villager,
     * cadastrando-o no cartório e notificando os observadores.
     *
     * @param bioma            bioma onde o Villager nasceu
     * @param profissaoInicial profissão inicial exercida
     * @return instância de {@link Villager} devidamente registrada
     */
    public Villager emitirDocumentoNoSpawn(String bioma, String profissaoInicial) {
        Villager villager = cartorio.emitirERegistrarVillager(bioma, profissaoInicial);
        VillagerDocument doc = villager.getDocumentAttachment().getDocumento();
        notificarObservadores(new EventoRegistroCivil(
            TipoEvento.VILLAGER_NASCIDO,
            "Documento civil emitido -> " + doc.getNome() + " | CPF: " + doc.getCpf()
                    + " | RG: " + doc.getRg() + " | Bioma: " + doc.getBiomaOrigem(),
            villager
        ));
        return villager;
    }

    /**
     * Caso de uso: Atualizar Histórico de Emprego quando um Villager muda de profissão,
     * notificando os observadores sobre a alteração.
     *
     * @param villager      entidade Villager cuja profissão mudou
     * @param novaProfissao nova profissão assumida
     */
    public void atualizarHistoricoEmprego(Villager villager, String novaProfissao) {
        ValidacaoUtil.exigirNaoNulo(villager, "Villager");
        String profissaoAnterior = villager.getProfissaoAtual();
        villager.setProfissaoAtual(novaProfissao);
        notificarObservadores(new EventoRegistroCivil(
            TipoEvento.PROFISSAO_ATUALIZADA,
            "Histórico atualizado para " + villager.getDocumentAttachment().getDocumento().getNome()
                    + ": " + profissaoAnterior + " -> " + villager.getProfissaoAtual(),
            villager
        ));
    }

    /**
     * Remove um Villager do cartório pelo CPF, encerra seu ciclo de vida no mundo
     * e notifica os observadores.
     *
     * @param cpf CPF do Villager a ser removido
     * @return instância de {@link Villager} removida
     */
    public Villager removerVillager(String cpf) {
        Villager removido = cartorio.removerPorCpf(cpf);
        String nome = removido.getDocumentAttachment().getDocumento().getNome();
        removido.removerDoMundo();
        notificarObservadores(new EventoRegistroCivil(
            TipoEvento.VILLAGER_REMOVIDO,
            "Villager removido do mundo e do cadastro ativo: " + nome + " (CPF: " + cpf + ")",
            removido
        ));
        return removido;
    }

    /**
     * Caso de uso: Inspecionar Cidadão / Visualizar Dados via referência polimórfica {@link Item},
     * disparando evento de inspeção aos observadores quando a leitura for bem-sucedida.
     *
     * @param jogador   jogador que realiza a interação
     * @param itemNaMao item utilizado na interação
     * @param alvo      entidade viva alvo
     * @return {@code true} se a interação foi processada com sucesso pelo item
     */
    public boolean dispararInteracaoEntidade(Player jogador, Item itemNaMao, LivingEntity alvo) {
        if (jogador == null || itemNaMao == null || alvo == null) {
            return false;
        }
        boolean sucesso = itemNaMao.interactLivingEntity(jogador, alvo);
        if (sucesso && alvo instanceof Villager villager && villager.getDocumentAttachment().hasDocumento()) {
            notificarObservadores(new EventoRegistroCivil(
                TipoEvento.CIDADAO_INSPECIONADO,
                "Jogador '" + jogador.getNomeJogador() + "' inspecionou "
                        + villager.getDocumentAttachment().getDocumento().getNome()
                        + " utilizando " + itemNaMao.getNomeItem(),
                villager
            ));
        }
        return sucesso;
    }

    /**
     * Retorna a lista imutável de todos os Villagers ativos monitorados pelo barramento.
     *
     * @return lista de Villagers cadastrados no cartório
     */
    public List<Villager> getVillagersMonitorados() {
        return cartorio.listarTodos();
    }
}
