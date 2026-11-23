# Villager-RG — Mod para Minecraft 1.20.1 (Fabric)

Adaptação completa do projeto **Sistema de Registro Civil para Villagers** (POO — UNICAP) para um **mod real e jogável de Minecraft Java Edition 1.20.1 (Fabric)**, preservando o núcleo orientado a objetos, validações matemáticas de CPF, exceções específicas e os padrões de projeto GoF (**Builder**, **Strategy** e **Observer**).

---

## Funcionalidades no Jogo

- **Registro Civil Automático e Persistente (NBT Nativo):**
  - Todo `Villager` que nasce ou é carregado no mundo recebe automaticamente:
    - **Nome completo** contextualizado ao bioma (`Planície`, `Deserto`, `Taiga`, `Savana`, `Neve`, `Pântano`, `Selva`);
    - **CPF matematicamente válido** (algoritmo do Módulo 11, com unicidade garantida por `Set`);
    - **RG formatado** com dígito verificador;
    - **Data de emissão** e **Histórico de profissões** atualizado automaticamente sempre que o Villager muda de trabalho no jogo.
  - Todos os dados são salvos diretamente no **NBT nativo do mundo** (`VillagerRG`) via Mixin (`VillagerEntityMixin`), permanecendo intactos ao fechar e reabrir o mundo.

- **Item Customizado: Prancheta de Recenseamento (`villagerrg:prancheta_recenseamento`):**
  - Disponível na aba criativa **Ferramentas** e fabricável no modo Sobrevivência:
    - Topo centro: `Pepita de Ferro` (`minecraft:iron_nugget`)
    - Centro: `Papel` (`minecraft:paper`)
    - Base centro: qualquer `Laje de Madeira` (`#minecraft:wooden_slabs`)
  - **Clique direito em um Villager:** exibe o documento civil formatado no chat (interceptando a janela de trocas via `UseEntityCallback`).
  - **Shift + Clique direito no ar:** alterna dinamicamente a estratégia de formatação (**Padrão Strategy GoF**):
    1. **Ficha Completa** (emoldurada com histórico detalhado)
    2. **Compacto** (uma linha resumida)
    3. **JSON** (saída estruturada)

- **Comandos no Jogo (`/villagerrg`):**
  - `/villagerrg censo` — exibe o censo em tempo real dos Villagers carregados (total, CPFs emitidos, biomas ativos e distribuição por profissão).
  - `/villagerrg auditoria` — exibe os últimos eventos capturados pelo `AuditoriaHistoricoObserver` (**Padrão Observer GoF**).
  - `/villagerrg formato <ficha|compacto|json>` — altera o formato de exibição da Prancheta.

---

## Como Instalar no Minecraft 1.20.1

1. Instale o **Fabric Loader** para **Minecraft 1.20.1** ([fabricmc.net](https://fabricmc.net/use/installer/)).
2. Baixe a **Fabric API** compatível com `1.20.1` (`0.92.2+1.20.1` ou superior) e coloque na pasta `.minecraft/mods/`.
3. Copie o arquivo empacotado **`build/libs/villager-rg-1.0.0.jar`** (não o `-sources.jar`) para a pasta `.minecraft/mods/`.
4. Inicie o Minecraft 1.20.1 com o perfil **Fabric**!

---

## Como Compilar, Testar e Executar em Desenvolvimento

Pré-requisito: **Java JDK 17** configurado no `PATH` / `JAVA_HOME`.

### Compilar e gerar o `.jar` do mod (com testes JUnit 5)

```powershell
.\gradlew.bat build
```

O `.jar` pronto para instalação será gerado em `build/libs/villager-rg-1.0.0.jar`.

### Executar apenas os testes unitários (JUnit 5)

```powershell
.\gradlew.bat test
```

### Abrir o cliente Minecraft diretamente para testar o mod

```powershell
.\gradlew.bat runClient
```
