# Justificativa dos Padrões de Projeto GoF — Villager-RG (Etapa 5)

Este documento apresenta a análise arquitetural e a justificativa técnica da aplicação de **três padrões de projeto GoF (*Gang of Four*)** na refatoração do **Sistema de Registro Civil para Villagers (Villager-RG)**. Cada padrão foi selecionado para solucionar um problema real de acoplamento, extensibilidade ou clareza de construção identificado na evolução das Etapas 3 e 4.

---

## 1. Builder (Padrão Criacional — GoF)

### Problema Identificado
A classe de domínio `VillagerDocument` representa um documento civil **imutável** composto por múltiplos campos — parte obrigatória (`nome`, `cpf`, `rg`) e parte opcional ou contextual (`biomaOrigem`, `dataGeracao`, `historicoProfissoes`).

Antes da refatoração:
- A construção em `DocumentAttachment.readFromNBT(...)` e `CartorioRegistroCivil.emitirERegistrarVillager(...)` dependia de um construtor posicional de 5 parâmetros seguido de chamadas imperativas repetidas a `adicionarProfissao(...)`;
- Como três parâmetros consecutivos eram do tipo `String` (`nome`, `cpf`, `rg`, `biomaOrigem`), a chamada posicional era propensa a troca acidental de argumentos e pouco legível.

### Por que o Builder é Adequado
O padrão **Builder** separa o processo passo a passo de montagem da representação final imutável de `VillagerDocument`, mantendo o encapsulamento rigoroso (sem adicionar *setters* ao modelo) e centralizando a validação de invariantes (`CpfInvalidoException` e `DocumentoInvalidoException`) na chamada a `build()`.

### Onde foi Aplicado
- **Builder:** `br.unicap.modvillagers.padroes.builder.VillagerDocumentBuilder` (`comNome`, `comCpf`, `comRg`, `comBiomaOrigem`, `comDataGeracao`, `comProfissao`, `comHistoricoProfissoes`, `build`).
- **Consumidores refatorados:**
  - `DocumentAttachment(String biomaOrigem)` e `DocumentAttachment.readFromNBT(NBTTagCompound)`;
  - `CartorioRegistroCivil.emitirERegistrarVillager(String, String)`;
  - `Main` e `VillagerDocumentBuilderTest`.

---

## 2. Strategy (Padrão Comportamental — GoF)

### Problema Identificado
Anteriormente, a lógica de formatação textual para chat estava acoplada diretamente em `VillagerDocument.formatarParaChat(...)` e consumida de forma rígida por `InspectorItem` e `ArquivoNBTPersistencia`.

Consequências desse acoplamento:
- Violação do **Princípio da Responsabilidade Única (SRP)**: o modelo de dados carregava detalhes de apresentação visual;
- Violação do **Princípio Aberto/Fechado (OCP)**: suportar outros modos de exibição (como saída compacta de uma linha no chat ou JSON estruturado para depuração/exportação) exigiria estruturas `if/else` ou novos métodos fixos dentro de `VillagerDocument` e `InspectorItem`.

### Por que o Strategy é Adequado
O padrão **Strategy** encapsula cada algoritmo de formatação em uma classe dedicada que implementa um contrato comum (`DocumentoFormatStrategy`). Assim, o contexto (`InspectorItem`, `VillagerDocument` e `ArquivoNBTPersistencia`) delega a renderização à estratégia injetada, permitindo trocar o formato em tempo de execução sem alterar o código cliente.

### Onde foi Aplicado
- **Interface Strategy:** `br.unicap.modvillagers.padroes.strategy.DocumentoFormatStrategy`.
- **Estratégias Concretas:**
  - `FormatoFichaCompletaStrategy`: ficha civil detalhada emoldurada;
  - `FormatoCompactoStrategy`: resumo em linha única para chat limpo;
  - `FormatoJsonStrategy`: representação JSON estruturada.
- **Contextos refatorados:**
  - `InspectorItem`: mantém `DocumentoFormatStrategy formatoExibicao` configurável via construtor e `setFormatoExibicao(...)`;
  - `VillagerDocument`: método `formatar(DocumentoFormatStrategy, String)` e delegação de `formatarParaChat(...)` para a estratégia padrão;
  - `ArquivoNBTPersistencia`: sobrecarga `exportarRelatorioCenso(CartorioRegistroCivil, Path, DocumentoFormatStrategy)`.

---

## 3. Observer (Padrão Comportamental — GoF)

### Problema Identificado
No desenho original das Etapas 3 e 4, `RegistroCivilEventBus` imprimia mensagens diretamente no console via `System.out.println(...)` dentro de `registrarItem`, `emitirDocumentoNoSpawn` e `atualizarHistoricoEmprego`.

Consequências desse acoplamento:
- O barramento de eventos conhecia e executava diretamente efeitos colaterais de saída;
- Adicionar novas reações a eventos civis — como manter uma trilha de auditoria ou alertar jogadores presentes no mesmo bioma — obrigaria a modificar o núcleo de `RegistroCivilEventBus` a cada novo requisito.

### Por que o Observer é Adequado
O próprio domínio do projeto (modelado desde o Diagrama de Casos de Uso da Etapa 1 com o ator **Event Bus**) é orientado a eventos. O padrão **Observer** transforma `RegistroCivilEventBus` em um **Subject** desacoplado que publica objetos imutáveis `EventoRegistroCivil` para qualquer conjunto dinâmico de assinantes (`RegistroCivilObserver`).

### Onde foi Aplicado
- **Evento:** `br.unicap.modvillagers.padroes.observer.EventoRegistroCivil` (com enum `TipoEvento`: `ITEM_REGISTRADO`, `VILLAGER_NASCIDO`, `PROFISSAO_ATUALIZADA`, `CIDADAO_INSPECIONADO`, `VILLAGER_REMOVIDO`).
- **Interface Observer:** `br.unicap.modvillagers.padroes.observer.RegistroCivilObserver`.
- **Observadores Concretos:**
  - `ConsoleLogObserver`: exibe os eventos formatados no console (substituindo os `System.out.println` fixos);
  - `AuditoriaHistoricoObserver`: registra todos os eventos em histórico imutável com suporte a filtro por `TipoEvento`;
  - `NotificacaoJogadorObserver`: envia alertas ao chat de um `Player` quando ocorrem nascimentos ou mudanças de profissão no seu bioma.
- **Subject refatorado:** `RegistroCivilEventBus` (`adicionarObservador`, `removerObservador`, `getObservadores`, `notificarObservadores`, além de notificar automaticamente em `registrarItem`, `emitirDocumentoNoSpawn`, `atualizarHistoricoEmprego`, `dispararInteracaoEntidade` e `removerVillager`).

---

## Resumo Comparativo

| Padrão GoF | Categoria | Problema Resolvido | Benefício Arquitetural |
| :--- | :--- | :--- | :--- |
| **Builder** | Criacional | Construção posicional verbosa de `VillagerDocument` imutável | API fluente e legível sem comprometer encapsulamento/imutabilidade |
| **Strategy** | Comportamental | Acoplamento rígido da formatação de saída ao modelo e ao item | Troca dinâmica de formato em tempo de execução (SRP e OCP) |
| **Observer** | Comportamental | Saída fixa no console acoplada ao `RegistroCivilEventBus` | Extensibilidade reativa desacoplada (log, auditoria, alertas a jogadores) |
