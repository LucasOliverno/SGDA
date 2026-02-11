# 🍺 SGCA - Sistema de Gestão de Cervejaria Artesanal

Sistema didático desenvolvido em Java puro para gestão do ciclo de vida de produção de cervejas artesanais, focado em **Domain-Driven Design (DDD)** e boas práticas de Orientação a Objetos.

## 🎯 Objetivo

Modelar o domínio complexo de uma cervejaria, desde o cadastro de insumos e criação de receitas, até o controle de produção (lotes), fermentação, maturação e envase, garantindo regras de negócio fortes e encapsulamento.

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas inspirada no DDD:

- **Dominio (`br.com.cervejaria.dominio`)**: Coração do sistema (Entidades e Regras de Negócio).
- **Serviço (`br.com.cervejaria.servico`)**: Serviços de domínio stateless.
- **Aplicação (`br.com.cervejaria.aplicacao`)**: Casos de Uso (Orquestração).
- **Infraestrutura (`br.com.cervejaria.infraestrutura`)**: Persistência em memória.
- **Apresentação (`br.com.cervejaria.apresentacao`)**: Interface Gráfica JavaFX e Console.

## 🚀 Como Executar

O projeto possui dois modos de execução:

### 1. Interface Gráfica (Recomendado)
Interface visual desenvolvida em JavaFX.

```bash
mvn javafx:run
# Ou execute a classe: br.com.cervejaria.apresentacao.MainApp
```

### 2. Modo Console (Demonstração)
Script de demonstração executando um fluxo completo via terminal.

```bash
mvn exec:java -Dexec.mainClass="br.com.cervejaria.Main"
# Ou execute a classe: br.com.cervejaria.Main
```

## 📦 Módulos do Domínio

- **Insumo**: Gerenciamento de maltes, lúpulos, leveduras e água.
- **Receita**: Definição de receitas, estilos e parâmetros (IBU, ABV).
- **Lote**: Rastreamento da produção e mudanças de estado.
- **Estoque**: Controle de movimentação.
- **Qualidade**: Avaliações sensoriais e físico-químicas.

## 🛠️ Tecnologias

- **Java 21**
- **JavaFX** (Interface Gráfica)
- **Maven** (Gerenciamento de dependências)

---
*Projeto Didático - Modelagem de Software*
