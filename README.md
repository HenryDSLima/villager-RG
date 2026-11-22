# Villager-RG — Sistema de Registro Civil para Villagers

Projeto Integrador da disciplina de **Programação Orientada a Objetos (POO)** do curso de **Ciência da Computação — Universidade Católica de Pernambuco (UNICAP)**.

- **Autor:** Henry de Souza Lima
- **Orientadora:** Profa. Ivna Cristine Brasileiro Valença de Oliveira
- **Etapa Atual:** Etapa 3 (Implementação das classes e integração dos componentes em Java)

---

## Sobre o Projeto

O **Villager-RG** implementa a arquitetura orientada a objetos de um **Sistema de Registro Civil para Villagers** (inspirado em uma modificação para *Minecraft Java Edition* utilizando a API Fabric). O sistema atribui uma identidade civil única, persistente e imutável a cada Villager gerado no mundo, composta por:

- **Nome completo** sorteado de acordo com o bioma de origem;
- **CPF matematicamente válido** (calculado e verificável pelo algoritmo oficial do Módulo 11);
- **RG formatado** com dígito verificador;
- **Bioma de origem** e **Data de emissão**;
- **Histórico de profissões** exercidas ao longo do tempo.

Jogadores podem interagir com entidades vivas utilizando o item customizado **Prancheta de Recenseamento** (`InspectorItem`) para consultar e exibir os dados civis formatados no chat, enquanto o componente `DocumentAttachment` gerencia a persistência simulada em estrutura **NBT (*Named Binary Tag*)**.

---

## Arquitetura e Organização de Pacotes

A implementação segue fielmente o **Diagrama de Classes UML da Etapa 2** e o **Diagrama de Casos de Uso da Etapa 1**:

```text
src/
└── br/unicap/modvillagers/
    ├── Main.java                                 # Demonstração completa da integração dos componentes
    ├── entidades/
    │   ├── LivingEntity.java                     # Classe base para entidades vivas do jogo
    │   ├── Player.java                           # Subclasse de LivingEntity com histórico de chat
    │   └── Villager.java                         # Subclasse de LivingEntity composta por DocumentAttachment
    ├── eventos/
    │   └── RegistroCivilEventBus.java            # Barramento de eventos (spawn, profissão, registro e interação)
    ├── itens/
    │   ├── ItemRegistrable.java                  # Interface de contrato para registro padronizado de itens
    │   ├── Item.java                             # Classe base de itens nativos (interactLivingEntity)
    │   └── InspectorItem.java                    # Prancheta de Recenseamento (herda Item, implementa ItemRegistrable)
    ├── modelo/
    │   ├── VillagerDocument.java                 # Modelo encapsulado e imutável do documento civil
    │   └── DocumentAttachment.java               # Componente 1:1 com VillagerDocument e serialização NBT
    ├── persistencia/
    │   └── NBTTagCompound.java                   # Estrutura simulada de persistência NBT
    └── servico/
        └── DocumentGenerator.java                # Serviço de geração/validação de CPF, RG e nomes por bioma
```

---

## Conceitos de POO Aplicados

1. **Encapsulamento e Visibilidade:**
   - Todos os atributos das classes são `private`, com acesso controlado por métodos getters e setters com validação de argumentos.
   - Em `VillagerDocument`, dados de identidade (`nome`, `cpf`, `rg`, `biomaOrigem`, `dataGeracao`) são propositalmente imutáveis após a construção (sem setters), e `getHistoricoProfissoes()` retorna uma lista não modificável (`Collections.unmodifiableList`), permitindo alterações apenas via `adicionarProfissao(String)`.
2. **Construtores e Sobrecarga:**
   - Todas as classes possuem construtores paramétricos que garantem a inicialização dos objetos em estado válido (impedindo identidades nulas ou incompletas).
   - Sobrecarga de construtores em `VillagerDocument`, `DocumentAttachment`, `DocumentGenerator`, `LivingEntity`, `Player`, `Villager`, `Item` e `InspectorItem`.
3. **Herança:**
   - `InspectorItem` estende `Item`.
   - `Player` e `Villager` estendem `LivingEntity`.
4. **Polimorfismo (Sobrescrita `@Override` e Despacho Dinâmico):**
   - `InspectorItem` sobrescreve `interactLivingEntity(Player jogador, LivingEntity alvo)`, alterando o comportamento padrão de `Item` para inspecionar o registro civil de `Villager`s.
   - `RegistroCivilEventBus.dispararInteracaoEntidade(Player, Item, LivingEntity)` aciona `interactLivingEntity` por meio de referência polimórfica do tipo base `Item`.
   - `Player` e `Villager` sobrescrevem `getDescricaoEntidade()` de `LivingEntity`.
5. **Interfaces:**
   - `ItemRegistrable` define o contrato de registro de itens no sistema de eventos, implementado por `InspectorItem` e consumido polimorficamente por `RegistroCivilEventBus.registrarItem(ItemRegistrable)`.
6. **Composição (1:1) e Dependência:**
   - **Composição:** `DocumentAttachment` (todo) cria e controla o ciclo de vida de `VillagerDocument` (parte). Quando um `Villager` é removido do mundo (`removerDoMundo()`), o documento associado é liberado.
   - **Dependência:** `DocumentAttachment` utiliza temporariamente `DocumentGenerator` na emissão do documento sem manter referência permanente de instância.

---

## Pré-requisitos

- **Java Development Kit (JDK) 17** ou superior instalado e configurado no `PATH`.

Verifique sua versão com:

```bash
java -version
javac -version
```

---

## Como Compilar e Executar

A partir da raiz do repositório (`villager-RG`):

### Windows (PowerShell)

```powershell
$arquivos = Get-ChildItem -Path "src" -Recurse -Filter "*.java" | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d bin $arquivos
java "-Dfile.encoding=UTF-8" -cp bin br.unicap.modvillagers.Main
```

### Linux / macOS (Bash)

```bash
mkdir -p bin
javac -encoding UTF-8 -d bin $(find src -name "*.java")
java -Dfile.encoding=UTF-8 -cp bin br.unicap.modvillagers.Main
```

---

## Fluxo Demonstrado na Execução (`Main`)

Ao executar a classe `br.unicap.modvillagers.Main`, o programa demonstra de forma integrada:

1. **Registro polimórfico de itens** no `RegistroCivilEventBus` por meio da interface `ItemRegistrable`;
2. **Emissão automática de documentos civis no spawn** de Villagers de diferentes biomas (`Planície`, `Deserto` e `Taiga`), validando matematicamente cada CPF gerado pelo algoritmo do Módulo 11;
3. **Atualização do histórico de empregos** e consulta completa dos getters de `VillagerDocument`, confirmando a proteção da lista imutável;
4. **Interação polimórfica** entre jogador, itens (`Item` comum vs. `InspectorItem` referenciado como `Item`) e entidades (`Villager` vs. `LivingEntity` comum);
5. **Serialização e desserialização em NBT** (`writeToNBT` e `readFromNBT`), validando a integridade completa dos dados restaurados;
6. **Ciclo de vida da composição 1:1**, comprovando que a remoção do `Villager` do mundo libera o documento civil associado.
