# Villager-RG — Sistema de Registro Civil para Villagers

Projeto Integrador da disciplina de **Programação Orientada a Objetos (POO)** do curso de **Ciência da Computação — Universidade Católica de Pernambuco (UNICAP)**.

- **Autor:** Henry de Souza Lima
- **Orientadora:** Profa. Ivna Cristine Brasileiro Valença de Oliveira
- **Etapa Atual:** Etapa 5 (Padrões de Projeto GoF, Refatoração Arquitetural e Javadoc Completo)

---

## Sobre o Projeto

O **Villager-RG** implementa a arquitetura orientada a objetos de um **Sistema de Registro Civil para Villagers** (inspirado em uma modificação para *Minecraft Java Edition* utilizando a API Fabric). Cada Villager gerado recebe uma identidade civil única, persistente e imutável composta por:

- **Nome completo** sorteado conforme o bioma de origem;
- **CPF matematicamente válido** (algoritmo oficial do Módulo 11);
- **RG formatado** com dígito verificador;
- **Bioma de origem**, **Data de emissão** e **Histórico de profissões** (`List` cronológica e `Set` de profissões distintas).

O sistema contempla inspeção polimórfica via **Prancheta de Recenseamento** (`InspectorItem`), gestão centralizada no **Cartório de Registro Civil** (`CartorioRegistroCivil`), persistência em arquivos UTF-8 com **Java I/O** (`ArquivoNBTPersistencia`), tratamento de **exceções específicas**, **3 padrões de projeto GoF**, documentação **Javadoc** completa e **30 testes unitários em JUnit 5**.

---

## Padrões de Projeto GoF Aplicados (Etapa 5)

A justificativa técnica detalhada de cada padrão está documentada em [docs/PADROES_DE_PROJETO.md](docs/PADROES_DE_PROJETO.md). Em resumo:

### 1. Builder (Criacional)
- **Classes:** `VillagerDocumentBuilder` (`br.unicap.modvillagers.padroes.builder`).
- **Problema resolvido:** `VillagerDocument` é um objeto imutável com múltiplos atributos obrigatórios (`nome`, `cpf`, `rg`) e opcionais (`biomaOrigem`, `dataGeracao`, `historicoProfissoes`), tornando construtores posicionais extensos e sujeitos a erros.
- **Aplicação no sistema:** integrado em `DocumentAttachment` (emissão inicial e restauração em `readFromNBT`), `CartorioRegistroCivil.emitirERegistrarVillager(...)` e `Main`.

### 2. Strategy (Comportamental)
- **Classes:** interface `DocumentoFormatStrategy` e implementações `FormatoFichaCompletaStrategy`, `FormatoCompactoStrategy` e `FormatoJsonStrategy` (`br.unicap.modvillagers.padroes.strategy`).
- **Problema resolvido:** a formatação de saída ficava acoplada diretamente em `VillagerDocument` e `InspectorItem`, dificultando a adição de novos formatos sem alterar código existente (violação de SRP e OCP).
- **Aplicação no sistema:** `InspectorItem` recebe e alterna estratégias dinamicamente via `setFormatoExibicao(...)`; `VillagerDocument.formatar(...)` e `ArquivoNBTPersistencia.exportarRelatorioCenso(...)` também delegam a renderização à estratégia escolhida.

### 3. Observer (Comportamental)
- **Classes:** `EventoRegistroCivil` (com enum `TipoEvento`), interface `RegistroCivilObserver` e observadores `ConsoleLogObserver`, `AuditoriaHistoricoObserver` e `NotificacaoJogadorObserver` (`br.unicap.modvillagers.padroes.observer`).
- **Problema resolvido:** `RegistroCivilEventBus` acoplava diretamente a publicação de eventos à impressão fixa no console (`System.out.println`), impedindo reações múltiplas e independentes.
- **Aplicação no sistema:** `RegistroCivilEventBus` foi refatorado como **Subject**, notificando observadores inscritos nos eventos de registro de itens, nascimento de Villagers, mudança de profissão, inspeção e remoção.

---

## Arquitetura e Organização de Pacotes

```text
villager-RG/
├── docs/
│   └── PADROES_DE_PROJETO.md                          # Justificativa detalhada dos padrões GoF
├── lib/
│   └── junit-platform-console-standalone-1.10.2.jar   # Executor oficial JUnit 5
├── src/br/unicap/modvillagers/
│   ├── Main.java                                      # Demonstração completa (Etapas 3, 4 e 5)
│   ├── entidades/
│   │   ├── LivingEntity.java                          # Classe base de entidades vivas
│   │   ├── Player.java                                # Subclasse de LivingEntity com histórico de chat
│   │   └── Villager.java                              # Subclasse de LivingEntity com DocumentAttachment
│   ├── eventos/
│   │   └── RegistroCivilEventBus.java                 # Subject (Observer) + Map de itens + integração com cartório
│   ├── excecoes/
│   │   ├── RegistroCivilException.java                # Exceção base de domínio
│   │   ├── CpfInvalidoException.java                  # CPF inválido (Módulo 11)
│   │   ├── DocumentoInvalidoException.java            # Campos obrigatórios inválidos/ausentes
│   │   ├── VillagerDuplicadoException.java            # CPF duplicado no cartório
│   │   ├── VillagerNaoEncontradoException.java        # Busca sem resultado no cartório
│   │   └── PersistenciaNBTException.java              # Exceção checked para falhas NBT e Java I/O
│   ├── itens/
│   │   ├── ItemRegistrable.java                       # Interface de contrato para registro de itens
│   │   ├── Item.java                                  # Classe base de itens nativos
│   │   └── InspectorItem.java                         # Prancheta de Recenseamento (Herança + Interface + Strategy)
│   ├── modelo/
│   │   ├── VillagerDocument.java                      # Modelo imutável e encapsulado do documento civil
│   │   └── DocumentAttachment.java                    # Composição 1:1 com VillagerDocument + Builder + NBT
│   ├── padroes/
│   │   ├── builder/
│   │   │   └── VillagerDocumentBuilder.java           # Padrão GoF Builder
│   │   ├── observer/
│   │   │   ├── EventoRegistroCivil.java               # Payload imutável de evento e enum TipoEvento
│   │   │   ├── RegistroCivilObserver.java             # Interface Observer
│   │   │   ├── ConsoleLogObserver.java                # Observador de log em console
│   │   │   ├── AuditoriaHistoricoObserver.java        # Observador de auditoria histórica
│   │   │   └── NotificacaoJogadorObserver.java        # Observador de alertas no chat do jogador
│   │   └── strategy/
│   │       ├── DocumentoFormatStrategy.java           # Interface Strategy
│   │       ├── FormatoFichaCompletaStrategy.java      # Formatação em ficha completa
│   │       ├── FormatoCompactoStrategy.java           # Formatação em linha única
│   │       └── FormatoJsonStrategy.java               # Formatação em JSON estruturado
│   ├── persistencia/
│   │   ├── NBTTagCompound.java                        # Estrutura simulada de Named Binary Tag
│   │   └── ArquivoNBTPersistencia.java                # Persistência em disco (Java I/O) + relatórios com Strategy
│   ├── servico/
│   │   ├── DocumentGenerator.java                     # Geração/validação de CPF, RG e nomes por bioma
│   │   └── CartorioRegistroCivil.java                 # Gestão de registros com List, Map, Set e Builder
│   └── util/
│       └── ValidacaoUtil.java                         # Validações centralizadas sem duplicação
└── test/br/unicap/modvillagers/                       # 30 testes unitários em JUnit 5 (9 classes)
    ├── itens/InspectorItemTest.java
    ├── modelo/DocumentAttachmentTest.java
    ├── modelo/VillagerDocumentTest.java
    ├── padroes/builder/VillagerDocumentBuilderTest.java
    ├── padroes/observer/RegistroCivilObserverTest.java
    ├── padroes/strategy/DocumentoFormatStrategyTest.java
    ├── persistencia/ArquivoNBTPersistenciaTest.java
    └── servico/
        ├── CartorioRegistroCivilTest.java
        └── DocumentGeneratorTest.java
```

---

## Pré-requisitos

- **Java Development Kit (JDK) 17** ou superior configurado no `PATH`.

---

## Como Compilar e Executar a Aplicação

A partir da raiz do repositório (`villager-RG`):

### Windows (PowerShell)

```powershell
$src = Get-ChildItem -Path "src" -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d bin $src
java "-Dfile.encoding=UTF-8" -cp bin br.unicap.modvillagers.Main
```

### Linux / macOS (Bash)

```bash
mkdir -p bin
javac -encoding UTF-8 -d bin $(find src -name "*.java")
java -Dfile.encoding=UTF-8 -cp bin br.unicap.modvillagers.Main
```

---

## Como Executar os Testes Unitários (JUnit 5)

Com o código principal compilado em `bin/`:

### Windows (PowerShell)

```powershell
$tests = Get-ChildItem -Path "test" -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -cp "bin;lib\junit-platform-console-standalone-1.10.2.jar" -d bin-test $tests
java "-Dfile.encoding=UTF-8" -jar "lib\junit-platform-console-standalone-1.10.2.jar" execute --class-path "bin;bin-test" --scan-class-path
```

### Linux / macOS (Bash)

```bash
mkdir -p bin-test
javac -encoding UTF-8 -cp "bin:lib/junit-platform-console-standalone-1.10.2.jar" -d bin-test $(find test -name "*.java")
java -Dfile.encoding=UTF-8 -jar lib/junit-platform-console-standalone-1.10.2.jar execute --class-path "bin:bin-test" --scan-class-path
```

---

## Como Gerar a Documentação Javadoc

Todas as classes, interfaces, enums, construtores e métodos públicos possuem Javadoc validado sem avisos (`-Xdoclint:all`):

### Windows (PowerShell)

```powershell
$src = Get-ChildItem -Path "src" -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
javadoc -encoding UTF-8 -charset UTF-8 -Xdoclint:all -d javadoc $src
```

### Linux / macOS (Bash)

```bash
javadoc -encoding UTF-8 -charset UTF-8 -Xdoclint:all -d javadoc $(find src -name "*.java")
```
