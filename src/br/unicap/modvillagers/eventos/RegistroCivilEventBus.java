package br.unicap.modvillagers.eventos;

import br.unicap.modvillagers.entidades.LivingEntity;
import br.unicap.modvillagers.entidades.Player;
import br.unicap.modvillagers.entidades.Villager;
import br.unicap.modvillagers.itens.Item;
import br.unicap.modvillagers.itens.ItemRegistrable;
import br.unicap.modvillagers.modelo.VillagerDocument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simula o barramento de eventos ({@code Event Bus}) do Mod Loader Fabric,
 * conectando os casos de uso definidos na Etapa 1:
 * registro polimórfico de itens ({@link ItemRegistrable}), emissão automática
 * de documentos civis no surgimento de Villagers, atualização do histórico de
 * empregos e despacho de interações do jogador com entidades vivas.
 */
public class RegistroCivilEventBus {

    private final List<ItemRegistrable> itensRegistrados;
    private final List<Villager> villagersMonitorados;

    public RegistroCivilEventBus() {
        this.itensRegistrados = new ArrayList<>();
        this.villagersMonitorados = new ArrayList<>();
    }

    /**
     * Registra qualquer item que implemente o contrato {@link ItemRegistrable},
     * demonstrando o uso polimórfico de interfaces no sistema de eventos.
     *
     * @param item item registrável a ser adicionado
     */
    public void registrarItem(ItemRegistrable item) {
        if (item == null) {
            throw new IllegalArgumentException("Item registrável não pode ser nulo.");
        }
        item.registrar();
        if (!this.itensRegistrados.contains(item)) {
            this.itensRegistrados.add(item);
        }
        System.out.println("[EventBus] Item registrado: " + item.getNomeExibicao()
                + " (" + item.getRegistryId() + ")");
    }

    public List<ItemRegistrable> getItensRegistrados() {
        return Collections.unmodifiableList(itensRegistrados);
    }

    /**
     * Caso de uso: Emitir Documento de Identidade no surgimento (spawn) de um Villager.
     *
     * @param bioma            bioma onde o Villager nasceu
     * @param profissaoInicial profissão inicial do Villager
     * @return instância de {@link Villager} já registrada com seu documento civil
     */
    public Villager emitirDocumentoNoSpawn(String bioma, String profissaoInicial) {
        Villager villager = new Villager(bioma, profissaoInicial);
        this.villagersMonitorados.add(villager);

        VillagerDocument doc = villager.getDocumentAttachment().getDocumento();
        System.out.println("[EventBus] Documento civil emitido no spawn -> "
                + doc.getNome() + " | CPF: " + doc.getCpf()
                + " | RG: " + doc.getRg()
                + " | Bioma: " + doc.getBiomaOrigem());
        return villager;
    }

    /**
     * Caso de uso: Atualizar Histórico de Emprego quando um Villager muda de profissão.
     *
     * @param villager      Villager que mudou de profissão
     * @param novaProfissao nova profissão assumida
     */
    public void atualizarHistoricoEmprego(Villager villager, String novaProfissao) {
        if (villager == null) {
            throw new IllegalArgumentException("Villager não pode ser nulo.");
        }
        String profissaoAnterior = villager.getProfissaoAtual();
        villager.setProfissaoAtual(novaProfissao);
        System.out.println("[EventBus] Histórico de emprego atualizado para "
                + villager.getDocumentAttachment().getDocumento().getNome()
                + ": " + profissaoAnterior + " -> " + villager.getProfissaoAtual());
    }

    /**
     * Caso de uso: Inspecionar Cidadão / Visualizar Dados.
     * Despacha a interação utilizando referência polimórfica do tipo base {@link Item}.
     *
     * @param jogador   jogador que inicia a interação
     * @param itemNaMao item equipado pelo jogador
     * @param alvo      entidade viva alvo
     * @return resultado booleano retornado pela implementação do item
     */
    public boolean dispararInteracaoEntidade(Player jogador, Item itemNaMao, LivingEntity alvo) {
        if (jogador == null || itemNaMao == null || alvo == null) {
            return false;
        }
        return itemNaMao.interactLivingEntity(jogador, alvo);
    }

    public List<Villager> getVillagersMonitorados() {
        return Collections.unmodifiableList(villagersMonitorados);
    }
}
