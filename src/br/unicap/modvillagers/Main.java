package br.unicap.modvillagers;

import br.unicap.modvillagers.entidades.LivingEntity;
import br.unicap.modvillagers.entidades.Player;
import br.unicap.modvillagers.entidades.Villager;
import br.unicap.modvillagers.eventos.RegistroCivilEventBus;
import br.unicap.modvillagers.itens.InspectorItem;
import br.unicap.modvillagers.itens.Item;
import br.unicap.modvillagers.itens.ItemRegistrable;
import br.unicap.modvillagers.modelo.DocumentAttachment;
import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.persistencia.NBTTagCompound;
import br.unicap.modvillagers.servico.DocumentGenerator;

/**
 * Classe principal de demonstração da integração completa entre todos os componentes
 * do Sistema de Registro Civil para Villagers (Etapa 3 - POO / UNICAP).
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  SISTEMA DE REGISTRO CIVIL PARA VILLAGERS - DEMONSTRAÇÃO");
        System.out.println("============================================================\n");

        RegistroCivilEventBus eventBus = new RegistroCivilEventBus();

        // 1. Interface ItemRegistrable e Registro Polimórfico
        System.out.println("--- 1. REGISTRO DE ITENS (INTERFACE ItemRegistrable) ---");
        InspectorItem prancheta = new InspectorItem();
        ItemRegistrable itemRegistravel = prancheta;
        eventBus.registrarItem(itemRegistravel);
        System.out.println("Status registrado: " + itemRegistravel.isRegistrado());
        System.out.println("Total de itens registrados no EventBus: " + eventBus.getItensRegistrados().size());
        System.out.println();

        // 2. Composição (DocumentAttachment -> VillagerDocument) e Dependência (DocumentGenerator)
        System.out.println("--- 2. EMISSÃO DE DOCUMENTOS NO SPAWN (COMPOSIÇÃO E DEPENDÊNCIA) ---");
        Villager villagerPlanicie = eventBus.emitirDocumentoNoSpawn("Planície", "Desempregado");
        Villager villagerDeserto = eventBus.emitirDocumentoNoSpawn("Deserto", "Ferreiro de Armas");
        Villager villagerTaiga = eventBus.emitirDocumentoNoSpawn("Taiga", "Cartógrafo");

        VillagerDocument docPlanicie = villagerPlanicie.getDocumentAttachment().getDocumento();
        VillagerDocument docDeserto = villagerDeserto.getDocumentAttachment().getDocumento();
        VillagerDocument docTaiga = villagerTaiga.getDocumentAttachment().getDocumento();

        System.out.println("Validação matemática (Módulo 11) dos CPFs gerados:");
        System.out.println(" - " + docPlanicie.getCpf() + " válido? " + DocumentGenerator.validarCpf(docPlanicie.getCpf()));
        System.out.println(" - " + docDeserto.getCpf() + " válido? " + DocumentGenerator.validarCpf(docDeserto.getCpf()));
        System.out.println(" - " + docTaiga.getCpf() + " válido? " + DocumentGenerator.validarCpf(docTaiga.getCpf()));
        System.out.println(" - 111.111.111-11 válido? " + DocumentGenerator.validarCpf("111.111.111-11"));
        System.out.println();

        // 3. Encapsulamento, Getters Completos e Atualização de Histórico de Profissões
        System.out.println("--- 3. HISTÓRICO DE EMPREGOS E ENCAPSULAMENTO ---");
        eventBus.atualizarHistoricoEmprego(villagerPlanicie, "Fazendeiro");
        eventBus.atualizarHistoricoEmprego(villagerPlanicie, "Bibliotecário");
        eventBus.atualizarHistoricoEmprego(villagerPlanicie, "Clérigo");

        System.out.println("Consulta via getters de VillagerDocument:");
        System.out.println(" - getNome(): " + docPlanicie.getNome());
        System.out.println(" - getCpf(): " + docPlanicie.getCpf());
        System.out.println(" - getRg(): " + docPlanicie.getRg());
        System.out.println(" - getBiomaOrigem(): " + docPlanicie.getBiomaOrigem());
        System.out.println(" - getDataGeracao(): " + docPlanicie.getDataGeracao());
        System.out.println(" - getHistoricoProfissoes(): " + docPlanicie.getHistoricoProfissoes());

        try {
            docPlanicie.getHistoricoProfissoes().add("Profissão Adulterada");
        } catch (UnsupportedOperationException e) {
            System.out.println("Proteção de encapsulamento confirmada: lista de profissões não pode ser alterada externamente.");
        }
        System.out.println();

        // 4. Herança e Polimorfismo (Item vs InspectorItem; LivingEntity vs Villager)
        System.out.println("--- 4. HERANÇA E POLIMORFISMO NA INTERAÇÃO COM ENTIDADES ---");
        Player jogador = new Player("Henry", "Planície");
        LivingEntity ovelha = new LivingEntity("Ovelha", 8.0, "Planície");

        Item itemComum = new Item("Graveto", "Um graveto comum de madeira.", 64);
        Item itemPolimorfico = prancheta; // Referência do tipo base Item apontando para InspectorItem

        boolean resItemComum = eventBus.dispararInteracaoEntidade(jogador, itemComum, villagerPlanicie);
        System.out.println("Interação com Item comum (" + itemComum.getNomeItem() + ") em Villager -> retornou: " + resItemComum);

        boolean resOvelha = eventBus.dispararInteracaoEntidade(jogador, itemPolimorfico, ovelha);
        System.out.println("Interação com InspectorItem em Ovelha -> retornou: " + resOvelha);

        System.out.println("\nInteração com InspectorItem (via referência Item) em Villager da Planície:");
        boolean resVillager1 = eventBus.dispararInteracaoEntidade(jogador, itemPolimorfico, villagerPlanicie);
        System.out.println("Resultado da inspeção: " + resVillager1);

        System.out.println("\nInteração com InspectorItem em Villager do Deserto:");
        boolean resVillager2 = eventBus.dispararInteracaoEntidade(jogador, itemPolimorfico, villagerDeserto);
        System.out.println("Resultado da inspeção: " + resVillager2);
        System.out.println();

        // 5. Persistência Simulada via NBT (writeToNBT / readFromNBT)
        System.out.println("--- 5. PERSISTÊNCIA EM NBT (writeToNBT / readFromNBT) ---");
        NBTTagCompound tagSalva = new NBTTagCompound();
        villagerPlanicie.salvarDados(tagSalva);
        System.out.println("NBT serializado: " + tagSalva);

        DocumentAttachment attachmentRestaurado = new DocumentAttachment();
        attachmentRestaurado.readFromNBT(tagSalva);
        VillagerDocument docRestaurado = attachmentRestaurado.getDocumento();

        System.out.println("Documento restaurado do NBT: " + docRestaurado);
        System.out.println("Integridade preservada (original.equals(restaurado))? "
                + docPlanicie.equals(docRestaurado));
        System.out.println();

        // 6. Ciclo de Vida da Composição 1:1 (Remoção da Entidade)
        System.out.println("--- 6. CICLO DE VIDA DA COMPOSIÇÃO (REMOÇÃO DO VILLAGER) ---");
        System.out.println("Antes da remoção - Villager Taiga possui documento? "
                + villagerTaiga.getDocumentAttachment().hasDocumento());
        villagerTaiga.removerDoMundo();
        System.out.println("Após removerDoMundo() - Villager Taiga possui documento? "
                + villagerTaiga.getDocumentAttachment().hasDocumento());
        boolean resRemovido = eventBus.dispararInteracaoEntidade(jogador, itemPolimorfico, villagerTaiga);
        System.out.println("Tentativa de inspeção após remoção -> retornou: " + resRemovido);

        System.out.println("\n============================================================");
        System.out.println("  DEMONSTRAÇÃO CONCLUÍDA COM SUCESSO!");
        System.out.println("============================================================");
    }
}
