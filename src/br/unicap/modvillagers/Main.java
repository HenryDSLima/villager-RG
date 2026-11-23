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
import br.unicap.modvillagers.padroes.builder.VillagerDocumentBuilder;
import br.unicap.modvillagers.padroes.observer.AuditoriaHistoricoObserver;
import br.unicap.modvillagers.padroes.observer.EventoRegistroCivil.TipoEvento;
import br.unicap.modvillagers.padroes.observer.NotificacaoJogadorObserver;
import br.unicap.modvillagers.padroes.strategy.FormatoCompactoStrategy;
import br.unicap.modvillagers.padroes.strategy.FormatoFichaCompletaStrategy;
import br.unicap.modvillagers.padroes.strategy.FormatoJsonStrategy;
import br.unicap.modvillagers.persistencia.ArquivoNBTPersistencia;
import br.unicap.modvillagers.servico.CartorioRegistroCivil;
import br.unicap.modvillagers.servico.DocumentGenerator;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Classe principal de demonstração da arquitetura completa do Sistema de Registro Civil
 * para Villagers (Etapas 3, 4 e 5 — POO / UNICAP), integrando fundamentos de POO,
 * exceções específicas, coleções, Java I/O e os padrões de projeto GoF
 * <b>Builder</b>, <b>Strategy</b> e <b>Observer</b>.
 */
public class Main {

    /**
     * Construtor padrão da classe de entrada da aplicação.
     */
    public Main() {
    }

    /**
     * Ponto de entrada da aplicação de demonstração do sistema Villager-RG.
     *
     * @param args argumentos de linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  SISTEMA DE REGISTRO CIVIL PARA VILLAGERS - ETAPA 5 (GoF)");
        System.out.println("============================================================\n");

        CartorioRegistroCivil cartorio = new CartorioRegistroCivil();
        RegistroCivilEventBus eventBus = new RegistroCivilEventBus(cartorio);
        Player jogador = new Player("Henry", "Planície");

        // 1. Padrão Observer (GoF): inscrevendo observadores no EventBus
        System.out.println("--- 1. PADRÃO OBSERVER (AUDITORIA E NOTIFICAÇÃO NO EVENT BUS) ---");
        AuditoriaHistoricoObserver auditoria = new AuditoriaHistoricoObserver();
        NotificacaoJogadorObserver notificacaoJogador = new NotificacaoJogadorObserver(jogador, true);
        eventBus.adicionarObservador(auditoria);
        eventBus.adicionarObservador(notificacaoJogador);

        InspectorItem prancheta = new InspectorItem();
        ItemRegistrable itemRegistravel = prancheta;
        eventBus.registrarItem(itemRegistravel);
        System.out.println();

        // 2. Padrão Builder (GoF) + Cartório (List, Map, Set)
        System.out.println("--- 2. PADRÃO BUILDER + COLEÇÕES DO CARTÓRIO (List, Map, Set) ---");
        DocumentGenerator gerador = new DocumentGenerator();
        VillagerDocument docConstruido = new VillagerDocumentBuilder()
                .comNome("Elena Campos")
                .comCpf(gerador.gerarCpfValido())
                .comRg(gerador.gerarRg())
                .comBiomaOrigem("Planície")
                .comDataGeracao(LocalDate.of(2026, 10, 1))
                .comProfissao("Desempregado")
                .comProfissao("Bibliotecário")
                .build();
        System.out.println("Documento montado via Builder: " + docConstruido);

        Villager villagerPlanicie = eventBus.emitirDocumentoNoSpawn("Planície", "Desempregado");
        Villager villagerDeserto = eventBus.emitirDocumentoNoSpawn("Deserto", "Ferreiro de Armas");
        Villager villagerTaiga = eventBus.emitirDocumentoNoSpawn("Taiga", "Cartógrafo");

        eventBus.atualizarHistoricoEmprego(villagerPlanicie, "Fazendeiro");
        eventBus.atualizarHistoricoEmprego(villagerPlanicie, "Bibliotecário");

        System.out.println("Biomas distintos no cartório (Set): " + cartorio.listarBiomasRegistrados());
        System.out.println("Contagem por profissão (Map): " + cartorio.contarPorProfissao());
        for (Map.Entry<String, List<Villager>> entrada : cartorio.agruparPorBioma().entrySet()) {
            System.out.println(" - Bioma " + entrada.getKey() + ": " + entrada.getValue().size() + " cidadão(s)");
        }
        System.out.println();

        // 3. Padrão Strategy (GoF) + Herança/Polimorfismo
        System.out.println("--- 3. PADRÃO STRATEGY NA INSPEÇÃO (FICHA COMPLETA, COMPACTO E JSON) ---");
        Item itemPolimorfico = prancheta;

        System.out.println(">> Estratégia 1: " + prancheta.getFormatoExibicao().getNomeFormato());
        eventBus.dispararInteracaoEntidade(jogador, itemPolimorfico, villagerPlanicie);

        prancheta.setFormatoExibicao(new FormatoCompactoStrategy());
        System.out.println("\n>> Estratégia 2: " + prancheta.getFormatoExibicao().getNomeFormato());
        eventBus.dispararInteracaoEntidade(jogador, itemPolimorfico, villagerDeserto);

        prancheta.setFormatoExibicao(new FormatoJsonStrategy());
        System.out.println("\n>> Estratégia 3: " + prancheta.getFormatoExibicao().getNomeFormato());
        eventBus.dispararInteracaoEntidade(jogador, itemPolimorfico, villagerPlanicie);

        LivingEntity ovelha = new LivingEntity("Ovelha", 8.0, "Planície");
        System.out.println("\nInteração em entidade não-Villager (Ovelha): "
                + eventBus.dispararInteracaoEntidade(jogador, itemPolimorfico, ovelha));
        System.out.println();

        // 4. Tratamento de Exceções Específicas
        System.out.println("--- 4. TRATAMENTO DE EXCEÇÕES ESPECÍFICAS ---");
        try {
            new VillagerDocumentBuilder()
                    .comNome("Inválido")
                    .comCpf("111.111.111-11")
                    .comRg("12.345.678-9")
                    .build();
        } catch (CpfInvalidoException e) {
            System.out.println("[CpfInvalidoException] " + e.getMessage());
        }

        try {
            cartorio.registrarVillager(villagerDeserto);
        } catch (VillagerDuplicadoException e) {
            System.out.println("[VillagerDuplicadoException] " + e.getMessage());
        }

        try {
            cartorio.buscarPorCpf(gerador.gerarCpfUnico(cartorio.getCpfsEmitidos()));
        } catch (VillagerNaoEncontradoException e) {
            System.out.println("[VillagerNaoEncontradoException] " + e.getMessage());
        }
        System.out.println();

        // 5. Persistência em Arquivos (Java I/O) integrada à Strategy
        System.out.println("--- 5. PERSISTÊNCIA EM ARQUIVOS (JAVA I/O + STRATEGY) ---");
        ArquivoNBTPersistencia persistencia = new ArquivoNBTPersistencia();
        Path arquivoNbt = Path.of("dados", "registro_civil_villagers.nbt");
        Path relatorioCompacto = Path.of("dados", "relatorio_censo_compacto.txt");

        try {
            persistencia.salvarVillagers(cartorio.listarTodos(), arquivoNbt);
            List<Villager> restaurados = persistencia.carregarVillagers(arquivoNbt);
            System.out.println("Villagers restaurados do arquivo NBT: " + restaurados.size());
            System.out.println("Integridade do 1º documento restaurado: "
                    + villagerPlanicie.getDocumentAttachment().getDocumento()
                            .equals(restaurados.get(0).getDocumentAttachment().getDocumento()));

            persistencia.exportarRelatorioCenso(cartorio, relatorioCompacto, new FormatoCompactoStrategy());
            System.out.println("Relatório compacto exportado em: " + relatorioCompacto.toAbsolutePath());
        } catch (PersistenciaNBTException e) {
            System.err.println("[Erro NBT] " + e.getMessage());
        }
        System.out.println();

        // 6. Resumo da Auditoria (Observer) e Remoção de Entidade
        System.out.println("--- 6. AUDITORIA DE EVENTOS (OBSERVER) E REMOÇÃO ---");
        eventBus.removerVillager(villagerTaiga.getDocumentAttachment().getDocumento().getCpf());
        prancheta.setFormatoExibicao(new FormatoFichaCompletaStrategy());

        System.out.println("Total de eventos auditados pelo AuditoriaHistoricoObserver: " + auditoria.getTotalEventos());
        System.out.println(" - Nascimentos registrados: "
                + auditoria.filtrarPorTipo(TipoEvento.VILLAGER_NASCIDO).size());
        System.out.println(" - Atualizações de profissão: "
                + auditoria.filtrarPorTipo(TipoEvento.PROFISSAO_ATUALIZADA).size());
        System.out.println(" - Inspeções concluídas: "
                + auditoria.filtrarPorTipo(TipoEvento.CIDADAO_INSPECIONADO).size());
        System.out.println(" - Remoções registradas: "
                + auditoria.filtrarPorTipo(TipoEvento.VILLAGER_REMOVIDO).size());

        System.out.println("\n============================================================");
        System.out.println("  DEMONSTRAÇÃO DA ETAPA 5 CONCLUÍDA COM SUCESSO!");
        System.out.println("============================================================");
    }
}
