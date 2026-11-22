package br.unicap.modvillagers;

import br.unicap.modvillagers.entidades.LivingEntity;
import br.unicap.modvillagers.entidades.Player;
import br.unicap.modvillagers.entidades.Villager;
import br.unicap.modvillagers.eventos.RegistroCivilEventBus;
import br.unicap.modvillagers.excecoes.CpfInvalidoException;
import br.unicap.modvillagers.excecoes.PersistenciaNBTException;
import br.unicap.modvillagers.excecoes.VillagerDuplicadoException;
import br.unicap.modvillagers.excecoes.VillagerNaoEncontradoException;
import br.unicap.modvillagers.itens.InspectorItem;
import br.unicap.modvillagers.itens.Item;
import br.unicap.modvillagers.itens.ItemRegistrable;
import br.unicap.modvillagers.modelo.VillagerDocument;
import br.unicap.modvillagers.persistencia.ArquivoNBTPersistencia;
import br.unicap.modvillagers.servico.CartorioRegistroCivil;
import br.unicap.modvillagers.servico.DocumentGenerator;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Classe principal de demonstração da integração completa do Sistema de Registro Civil
 * para Villagers (Etapas 3 e 4 - POO / UNICAP):
 * encapsulamento, herança, polimorfismo, interfaces, composição, exceções específicas,
 * coleções (List, Map, Set) e persistência em arquivos com Java I/O.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  SISTEMA DE REGISTRO CIVIL PARA VILLAGERS - ETAPA 4");
        System.out.println("============================================================\n");

        CartorioRegistroCivil cartorio = new CartorioRegistroCivil();
        RegistroCivilEventBus eventBus = new RegistroCivilEventBus(cartorio);

        // 1. Interface ItemRegistrable e Registro Polimórfico em Map
        System.out.println("--- 1. REGISTRO DE ITENS (INTERFACE ItemRegistrable + Map) ---");
        InspectorItem prancheta = new InspectorItem();
        ItemRegistrable itemRegistravel = prancheta;
        eventBus.registrarItem(itemRegistravel);
        System.out.println("Item recuperado por registryId: "
                + eventBus.buscarItemRegistrado(prancheta.getRegistryId()));
        System.out.println();

        // 2. Emissão de documentos no spawn, Cartório e Coleções (List, Map, Set)
        System.out.println("--- 2. EMISSÃO DE DOCUMENTOS E COLEÇÕES (List, Map, Set) ---");
        Villager villagerPlanicie = eventBus.emitirDocumentoNoSpawn("Planície", "Desempregado");
        Villager villagerDeserto = eventBus.emitirDocumentoNoSpawn("Deserto", "Ferreiro de Armas");
        Villager villagerTaiga = eventBus.emitirDocumentoNoSpawn("Taiga", "Cartógrafo");
        Villager villagerPlanicie2 = eventBus.emitirDocumentoNoSpawn("Planície", "Fazendeiro");

        eventBus.atualizarHistoricoEmprego(villagerPlanicie, "Fazendeiro");
        eventBus.atualizarHistoricoEmprego(villagerPlanicie, "Bibliotecário");
        eventBus.atualizarHistoricoEmprego(villagerPlanicie, "Fazendeiro");

        VillagerDocument docPlanicie = villagerPlanicie.getDocumentAttachment().getDocumento();
        System.out.println("Histórico cronológico (List): " + docPlanicie.getHistoricoProfissoes());
        System.out.println("Profissões únicas exercidas (Set): " + docPlanicie.getProfissoesUnicas());
        System.out.println("Biomas distintos registrados no cartório (Set): " + cartorio.listarBiomasRegistrados());
        System.out.println("Contagem por profissão atual (Map): " + cartorio.contarPorProfissao());
        System.out.println("Agrupamento por bioma (Map<String, List<Villager>>):");
        for (Map.Entry<String, List<Villager>> entrada : cartorio.agruparPorBioma().entrySet()) {
            System.out.println(" - " + entrada.getKey() + ": " + entrada.getValue().size() + " cidadão(s)");
        }
        System.out.println();

        // 3. Herança e Polimorfismo na Interação com Entidades
        System.out.println("--- 3. HERANÇA E POLIMORFISMO NA INTERAÇÃO ---");
        Player jogador = new Player("Henry", "Planície");
        LivingEntity ovelha = new LivingEntity("Ovelha", 8.0, "Planície");
        Item itemComum = new Item("Graveto", "Um graveto comum de madeira.", 64);
        Item itemPolimorfico = prancheta;

        System.out.println("Item comum em Villager -> retornou: "
                + eventBus.dispararInteracaoEntidade(jogador, itemComum, villagerPlanicie));
        System.out.println("InspectorItem em Ovelha -> retornou: "
                + eventBus.dispararInteracaoEntidade(jogador, itemPolimorfico, ovelha));
        System.out.println("\nInspectorItem (via referência Item) em Villager da Planície:");
        eventBus.dispararInteracaoEntidade(jogador, itemPolimorfico, villagerPlanicie);
        System.out.println();

        // 4. Tratamento de Exceções Específicas
        System.out.println("--- 4. TRATAMENTO DE EXCEÇÕES ESPECÍFICAS ---");
        try {
            new VillagerDocument("Villager Inválido", "111.111.111-11", "12.345.678-9");
        } catch (CpfInvalidoException e) {
            System.out.println("[Exceção capturada - CpfInvalidoException] " + e.getMessage());
        }

        try {
            cartorio.registrarVillager(villagerDeserto);
        } catch (VillagerDuplicadoException e) {
            System.out.println("[Exceção capturada - VillagerDuplicadoException] " + e.getMessage());
        }

        try {
            String cpfInexistente = new DocumentGenerator().gerarCpfUnico(cartorio.getCpfsEmitidos());
            cartorio.buscarPorCpf(cpfInexistente);
        } catch (VillagerNaoEncontradoException e) {
            System.out.println("[Exceção capturada - VillagerNaoEncontradoException] " + e.getMessage());
        }
        System.out.println();

        // 5. Persistência em Arquivos com Java I/O (NBT + Relatório de Censo)
        System.out.println("--- 5. PERSISTÊNCIA EM ARQUIVOS (JAVA I/O) ---");
        ArquivoNBTPersistencia persistencia = new ArquivoNBTPersistencia();
        Path arquivoNbt = Path.of("dados", "registro_civil_villagers.nbt");
        Path arquivoRelatorio = Path.of("dados", "relatorio_censo.txt");

        try {
            persistencia.salvarVillagers(cartorio.listarTodos(), arquivoNbt);
            System.out.println("Registros NBT salvos com sucesso em: " + arquivoNbt.toAbsolutePath());

            List<Villager> carregados = persistencia.carregarVillagers(arquivoNbt);
            System.out.println("Total de Villagers restaurados do arquivo NBT: " + carregados.size());

            Villager restauradoPlanicie = carregados.get(0);
            System.out.println("Integridade do 1º documento restaurado (equals): "
                    + docPlanicie.equals(restauradoPlanicie.getDocumentAttachment().getDocumento()));

            persistencia.exportarRelatorioCenso(cartorio, arquivoRelatorio);
            System.out.println("Relatório de censo exportado com sucesso em: " + arquivoRelatorio.toAbsolutePath());
        } catch (PersistenciaNBTException e) {
            System.err.println("[Erro de Persistência NBT] " + e.getMessage());
        }

        try {
            persistencia.carregarVillagers(Path.of("dados", "arquivo_inexistente.nbt"));
        } catch (PersistenciaNBTException e) {
            System.out.println("[Exceção capturada - PersistenciaNBTException] " + e.getMessage());
        }
        System.out.println();

        // 6. Ciclo de Vida da Composição 1:1
        System.out.println("--- 6. CICLO DE VIDA DA COMPOSIÇÃO (REMOÇÃO DO VILLAGER) ---");
        String cpfTaiga = villagerTaiga.getDocumentAttachment().getDocumento().getCpf();
        cartorio.removerPorCpf(cpfTaiga);
        villagerTaiga.removerDoMundo();
        System.out.println("Villager Taiga removido do cartório e do mundo. Documento ativo? "
                + villagerTaiga.getDocumentAttachment().hasDocumento());
        System.out.println("Total de Villagers ativos restantes no cartório: " + cartorio.getTotalRegistrados()
                + " | Total histórico de CPFs emitidos (Set): " + cartorio.getCpfsEmitidos().size());
        System.out.println("Villager Planície 2 permanece ativo: " + villagerPlanicie2.getDescricaoEntidade());

        System.out.println("\n============================================================");
        System.out.println("  DEMONSTRAÇÃO DA ETAPA 4 CONCLUÍDA COM SUCESSO!");
        System.out.println("============================================================");
    }
}
