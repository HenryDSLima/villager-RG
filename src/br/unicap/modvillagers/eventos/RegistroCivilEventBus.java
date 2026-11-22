package br.unicap.modvillagers.eventos;

import br.unicap.modvillagers.entidades.LivingEntity;
import br.unicap.modvillagers.entidades.Player;
import br.unicap.modvillagers.entidades.Villager;
import br.unicap.modvillagers.itens.Item;
import br.unicap.modvillagers.itens.ItemRegistrable;
import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.servico.CartorioRegistroCivil;
import br.unicap.modvillagers.util.ValidacaoUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Simula o barramento de eventos ({@code Event Bus}) do Mod Loader Fabric,
 * integrando o registro polimórfico de itens ({@link ItemRegistrable}) em um
 * {@link Map} indexado por {@code registryId}, a emissão de documentos via
 * {@link CartorioRegistroCivil}, a atualização de histórico de profissões e
 * o despacho de interações do jogador com entidades vivas.
 */
public class RegistroCivilEventBus {

    private final Map<String, ItemRegistrable> itensPorRegistryId;
    private final CartorioRegistroCivil cartorio;

    public RegistroCivilEventBus() {
        this(new CartorioRegistroCivil());
    }

    public RegistroCivilEventBus(CartorioRegistroCivil cartorio) {
        this.itensPorRegistryId = new LinkedHashMap<>();
        this.cartorio = ValidacaoUtil.exigirNaoNulo(cartorio, "CartorioRegistroCivil");
    }

    /**
     * Registra qualquer item que implemente {@link ItemRegistrable}, indexando-o
     * pelo seu {@code registryId} em um {@link Map} para evitar registros duplicados.
     *
     * @param item item registrável a ser adicionado
     */
    public void registrarItem(ItemRegistrable item) {
        ValidacaoUtil.exigirNaoNulo(item, "Item registrável");
        item.registrar();
        this.itensPorRegistryId.put(item.getRegistryId(), item);
        System.out.println("[EventBus] Item registrado: " + item.getNomeExibicao()
                + " (" + item.getRegistryId() + ")");
    }

    public ItemRegistrable buscarItemRegistrado(String registryId) {
        String idValido = ValidacaoUtil.exigirTextoValido(registryId, "Registry ID");
        return this.itensPorRegistryId.get(idValido);
    }

    public List<ItemRegistrable> getItensRegistrados() {
        return Collections.unmodifiableList(new ArrayList<>(itensPorRegistryId.values()));
    }

    public Map<String, ItemRegistrable> getMapaItensRegistrados() {
        return Collections.unmodifiableMap(itensPorRegistryId);
    }

    public CartorioRegistroCivil getCartorio() {
        return cartorio;
    }

    /**
     * Caso de uso: Emitir Documento de Identidade no surgimento (spawn) de um Villager,
     * cadastrando-o automaticamente no {@link CartorioRegistroCivil}.
     */
    public Villager emitirDocumentoNoSpawn(String bioma, String profissaoInicial) {
        Villager villager = cartorio.emitirERegistrarVillager(bioma, profissaoInicial);
        VillagerDocument doc = villager.getDocumentAttachment().getDocumento();
        System.out.println("[EventBus] Documento civil emitido no spawn -> "
                + doc.getNome() + " | CPF: " + doc.getCpf()
                + " | RG: " + doc.getRg()
                + " | Bioma: " + doc.getBiomaOrigem());
        return villager;
    }

    /**
     * Caso de uso: Atualizar Histórico de Emprego quando um Villager muda de profissão.
     */
    public void atualizarHistoricoEmprego(Villager villager, String novaProfissao) {
        ValidacaoUtil.exigirNaoNulo(villager, "Villager");
        String profissaoAnterior = villager.getProfissaoAtual();
        villager.setProfissaoAtual(novaProfissao);
        System.out.println("[EventBus] Histórico de emprego atualizado para "
                + villager.getDocumentAttachment().getDocumento().getNome()
                + ": " + profissaoAnterior + " -> " + villager.getProfissaoAtual());
    }

    /**
     * Caso de uso: Inspecionar Cidadão / Visualizar Dados via referência polimórfica {@link Item}.
     */
    public boolean dispararInteracaoEntidade(Player jogador, Item itemNaMao, LivingEntity alvo) {
        if (jogador == null || itemNaMao == null || alvo == null) {
            return false;
        }
        return itemNaMao.interactLivingEntity(jogador, alvo);
    }

    public List<Villager> getVillagersMonitorados() {
        return cartorio.listarTodos();
    }
}
