# Villager-RG — Sistema de Registro Civil para Villagers

Projeto Integrador da disciplina de **Programação Orientada a Objetos (POO)** do curso de **Ciência da Computação — Universidade Católica de Pernambuco (UNICAP)**.

- **Autor:** Henry de Souza Lima
- **Orientadora:** Profa. Ivna Cristine Brasileiro Valença de Oliveira
- **Etapa Atual:** Etapa 4 (Exceções específicas, Coleções `List`/`Map`/`Set`, Java I/O, Testes Unitários com JUnit 5 e Código Limpo)

---

## Sobre o Projeto

O **Villager-RG** implementa a arquitetura orientada a objetos de um **Sistema de Registro Civil para Villagers** (inspirado em uma modificação para *Minecraft Java Edition* utilizando a API Fabric). O sistema atribui uma identidade civil única, persistente e imutável a cada Villager gerado no mundo, composta por:

- **Nome completo** sorteado de acordo com o bioma de origem;
- **CPF matematicamente válido** (calculado e verificado pelo algoritmo oficial do Módulo 11);
- **RG formatado** com dígito verificador;
- **Bioma de origem** e **Data de emissão**;
- **Histórico cronológico de profissões** (`List`) e **profissões distintas exercidas** (`Set`).

Além da inspeção de cidadãos pelo item **Prancheta de Recenseamento** (`InspectorItem`), o sistema conta com o **Cartório de Registro Civil** (`CartorioRegistroCivil`), persistência real em arquivos UTF-8 via **Java I/O** (`ArquivoNBTPersistencia`), hierarquia de **exceções específicas** e suíte de **testes unitários em JUnit 5**.

---

## Arquitetura e Organização de Pacotes

```text
villager-RG/
├── lib/
│   └── junit-platform-console-standalone-1.10.2.jar   # Executor oficial JUnit 5
├── src/br/unicap/modvillagers/
│   ├── Main.java                                      # Demonstração integrada (Etapas 3 e 4)
│   ├── entidades/
│   │   ├── LivingEntity.java                          # Classe base de entidades vivas
│   │   ├── Player.java                                # Subclasse de LivingEntity com chat
│   │   └── Villager.java                              # Subclasse de LivingEntity com DocumentAttachment
│   ├── eventos/
│   │   └── RegistroCivilEventBus.java                 # Barramento de eventos integrado ao cartório e Map de itens
│   ├── excecoes/
│   │   ├── RegistroCivilException.java                # Exceção base de domínio
│   │   ├── CpfInvalidoException.java                  # CPF fora do padrão ou com DV inválido
│   │   ├── DocumentoInvalidoException.java            # Campos obrigatórios ausentes/inválidos
│   │   ├── VillagerDuplicadoException.java            # Tentativa de duplicar CPF no cartório
│   │   ├── VillagerNaoEncontradoException.java        # Busca por CPF não cadastrado
│   │   └── PersistenciaNBTException.java              # Exceção checked para erros de NBT e I/O
│   ├── itens/
│   │   ├── ItemRegistrable.java                       # Interface de registro padronizado
│   │   ├── Item.java                                  # Classe base nativa de itens
│   │   └── InspectorItem.java                         # Prancheta de Recenseamento (herança + interface)
│   ├── modelo/
│   │   ├── VillagerDocument.java                      # Modelo imutável e encapsulado do documento civil
│   │   └── DocumentAttachment.java                    # Composição 1:1 com VillagerDocument + NBT
│   ├── persistencia/
│   │   ├── NBTTagCompound.java                        # Estrutura simulada de Named Binary Tag
│   │   └── ArquivoNBTPersistencia.java                # Leitura/escrita de arquivos NBT e relatório de censo (Java I/O)
│   ├── servico/
│   │   ├── DocumentGenerator.java                     # Geração de CPF único/válido, RG e nomes por bioma
│   │   └── CartorioRegistroCivil.java                 # Gestão de cidadãos com List, Map e Set
│   └── util/
│       └── ValidacaoUtil.java                         # Validações centralizadas sem duplicação de código
└── test/br/unicap/modvillagers/
    ├── itens/InspectorItemTest.java                   # Testes de herança, interface e polimorfismo
    ├── modelo/DocumentAttachmentTest.java             # Testes de composição e serialização NBT
    ├── modelo/VillagerDocumentTest.java               # Testes de encapsulamento, exceções e profissões
    ├── persistencia/ArquivoNBTPersistenciaTest.java   # Testes de leitura/escrita em arquivos com @TempDir
    └── servico/
        ├── CartorioRegistroCivilTest.java             # Testes de List, Map, Set e exceções do cartório
        └── DocumentGeneratorTest.java                 # Testes de validação matemática de CPF, RG e nomes
```

---

## Recursos Implementados

### Fundamentos de POO (Etapa 3)
- **Encapsulamento:** atributos `private`, dados civis imutáveis em `VillagerDocument` e coleções expostas como visualizações não modificáveis (`Collections.unmodifiableList/Set/Map`).
- **Herança e Polimorfismo:** `InspectorItem extends Item` sobrescrevendo `interactLivingEntity(Player, LivingEntity)`; `Player` e `Villager` estendendo `LivingEntity`.
- **Interface:** `ItemRegistrable` implementada por `InspectorItem` e consumida polimorficamente no `RegistroCivilEventBus`.
- **Composição e Dependência:** composição 1:1 `DocumentAttachment -> VillagerDocument` (com ciclo de vida controlado via `destruir()`) e dependência temporária de `DocumentGenerator`.

### Evolução da Aplicação (Etapa 4)
- **Tratamento de Exceções Específicas:** hierarquia dedicada em `br.unicap.modvillagers.excecoes` com mensagens úteis e `PersistenciaNBTException` (checked) para falhas de I/O e formato NBT.
- **Coleções (`List`, `Map` e `Set`):**
  - `Map<String, Villager>` para consulta por CPF em $O(1)$, `Map<String, List<Villager>>` para agrupamento por bioma, `Map<String, Integer>` para censo por profissão e `Map<String, ItemRegistrable>` no `EventBus`.
  - `Set<String>` para unicidade histórica de CPFs emitidos, biomas distintos registrados e profissões únicas em `VillagerDocument`.
  - `List<String>` e `List<Villager>` para histórico cronológico de profissões, histórico de chat e listagem ordenada.
- **Leitura e Escrita de Arquivos (Java I/O):** `ArquivoNBTPersistencia` utiliza `Files.newBufferedWriter`, `Files.newBufferedReader` (`UTF-8`) e `try-with-resources` para persistir e restaurar registros NBT em `dados/registro_civil_villagers.nbt` e exportar relatórios de censo em `dados/relatorio_censo.txt`.
- **Código Limpo sem Duplicação:** validações de texto, intervalo, normalização de acentos e cálculo de Módulo 11 centralizados em `ValidacaoUtil`.
- **Testes Unitários (JUnit 5):** 23 testes organizados em 6 classes sob `test/`, documentados com `@DisplayName` e Javadoc, cobrindo modelos, serviços, polimorfismo, exceções e I/O em disco (`@TempDir`).

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

Com a aplicação compilada em `bin/`, compile e execute a suíte JUnit 5:

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
