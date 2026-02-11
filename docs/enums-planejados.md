# Enums de Domínio — Sistema de Cervejaria Artesanal

Este documento lista todos os enums planejados para o sistema, organizados por pacote.
Os enums serão implementados posteriormente como classes Java.

---

## 1. Pacote `dominio.lote`

### StatusLote

| Valor | Descrição | Transições Permitidas |
|-------|-----------|----------------------|
| `PLANEJADO` | Lote criado, aguardando início | → EM_BRASSAGEM, DESCARTADO |
| `EM_BRASSAGEM` | Mostura e fervura em andamento | → FERMENTANDO, DESCARTADO |
| `FERMENTANDO` | Levedura trabalhando no mosto | → MATURANDO, DESCARTADO |
| `MATURANDO` | Cerveja em processo de maturação | → PRONTO_PARA_ENVASE, DESCARTADO |
| `PRONTO_PARA_ENVASE` | Aguardando aprovação final | → ENVASADO, MATURANDO, DESCARTADO |
| `ENVASADO` | Lote concluído com sucesso | (estado final) |
| `DESCARTADO` | Lote descartado por problemas | (estado final) |

**Métodos planejados:**
- `boolean podeTransicionarPara(StatusLote novoStatus)`
- `boolean isFinal()` — retorna true para ENVASADO e DESCARTADO
- `Set<StatusLote> transicoesPermitidas()`

---

### StatusEtapaExecutada

| Valor | Descrição |
|-------|-----------|
| `PENDENTE` | Etapa ainda não iniciada |
| `EM_ANDAMENTO` | Etapa em execução |
| `CONCLUIDA` | Etapa finalizada com sucesso |
| `COM_PROBLEMAS` | Etapa finalizada com ressalvas |

---

## 2. Pacote `dominio.insumo`

### CategoriaInsumo

| Valor | Descrição | Obrigatório na Receita |
|-------|-----------|------------------------|
| `MALTE` | Grãos maltados (cevada, trigo, etc.) | ✅ Sim |
| `LUPULO` | Flores de lúpulo para amargor/aroma | ⚠️ Geralmente sim |
| `LEVEDURA` | Fermento para fermentação | ✅ Sim |
| `AGUA` | Base líquida da cerveja | ✅ Sim |
| `ADJUNTO` | Ingredientes extras (açúcar, frutas, especiarias) | ❌ Não |
| `CLARIFICANTE` | Agentes de clarificação (irish moss, gelatina) | ❌ Não |

**Métodos planejados:**
- `boolean isObrigatorio()` — retorna true para MALTE, LEVEDURA, AGUA
- `String getDescricao()`

---

## 3. Pacote `dominio.estoque`

### TipoMovimentoEstoque

| Valor | Descrição | Efeito no Estoque |
|-------|-----------|-------------------|
| `ENTRADA` | Adição de insumo ao estoque | + quantidade |
| `SAIDA` | Retirada de insumo do estoque | - quantidade |

---

### MotivoMovimento

| Valor | Tipo Associado | Descrição |
|-------|----------------|-----------|
| `COMPRA` | ENTRADA | Compra de fornecedor |
| `DEVOLUCAO_FORNECEDOR` | ENTRADA | Devolução recebida de fornecedor |
| `AJUSTE_INVENTARIO_POSITIVO` | ENTRADA | Correção de contagem (sobra) |
| `USO_PRODUCAO` | SAIDA | Consumo em lote de produção |
| `DESCARTE_VALIDADE` | SAIDA | Descarte por vencimento |
| `DESCARTE_QUALIDADE` | SAIDA | Descarte por problema de qualidade |
| `AJUSTE_INVENTARIO_NEGATIVO` | SAIDA | Correção de contagem (falta) |
| `PERDA` | SAIDA | Perda acidental |

**Métodos planejados:**
- `TipoMovimentoEstoque getTipoMovimento()` — retorna ENTRADA ou SAIDA

---

## 4. Pacote `dominio.qualidade`

### TipoAvaliacaoQualidade

| Valor | Descrição | Campos Principais |
|-------|-----------|-------------------|
| `SENSORIAL` | Avaliação baseada nos sentidos | aparência, aroma, sabor, corpo |
| `TECNICA` | Avaliação baseada em medições | pH, densidade, temperatura |

---

### ParecerQualidade

| Valor | Descrição | Permite Envase |
|-------|-----------|----------------|
| `APROVADO` | Cerveja dentro dos padrões | ✅ Sim |
| `APROVADO_COM_RESSALVAS` | Cerveja aceitável com observações | ✅ Sim |
| `REPROVADO` | Cerveja fora dos padrões | ❌ Não |

**Métodos planejados:**
- `boolean permiteEnvase()` — retorna true para APROVADO e APROVADO_COM_RESSALVAS

---

## 5. Pacote `dominio.equipamento`

### TipoEquipamento

| Valor | Descrição | Capacidade Típica |
|-------|-----------|-------------------|
| `PANELA_MOSTURA` | Panela para mostura dos grãos | 20-200L |
| `PANELA_FERVURA` | Panela para fervura do mosto | 20-200L |
| `FERMENTADOR` | Tanque para fermentação | 20-500L |
| `MATURADOR` | Tanque para maturação | 20-500L |
| `BARRIL` | Barril para carbonatação/armazenamento | 19-50L |
| `ENVASADORA` | Equipamento de envase | N/A |
| `MOEDOR` | Moinho de malte | N/A |
| `TROCADOR_CALOR` | Chiller/trocador de calor | N/A |

---

### StatusEquipamento

| Valor | Descrição | Pode ser Alocado |
|-------|-----------|------------------|
| `DISPONIVEL` | Pronto para uso | ✅ Sim |
| `EM_USO` | Alocado a um lote ativo | ❌ Não |
| `EM_MANUTENCAO` | Em manutenção/reparo | ❌ Não |
| `EM_LIMPEZA` | Em processo de sanitização | ❌ Não |
| `INDISPONIVEL` | Fora de operação | ❌ Não |

**Métodos planejados:**
- `boolean podeSerAlocado()` — retorna true apenas para DISPONIVEL

---

## 6. Pacote `dominio.receita`

### StatusReceita

| Valor | Descrição | Pode Criar Lote |
|-------|-----------|-----------------|
| `RASCUNHO` | Receita em elaboração | ❌ Não |
| `ATIVA` | Receita pronta para produção | ✅ Sim |
| `ARQUIVADA` | Receita desativada/histórica | ❌ Não |

**Métodos planejados:**
- `boolean podeCriarLote()` — retorna true apenas para ATIVA

---

### TipoEtapaProducao

| Valor | Descrição | Ordem Típica |
|-------|-----------|--------------|
| `MOSTURA` | Mistura do malte com água quente | 1 |
| `FILTRAGEM` | Separação do mosto dos grãos | 2 |
| `FERVURA` | Fervura do mosto com lúpulo | 3 |
| `RESFRIAMENTO` | Resfriamento do mosto | 4 |
| `FERMENTACAO_PRIMARIA` | Primeira fase da fermentação | 5 |
| `FERMENTACAO_SECUNDARIA` | Segunda fase (opcional) | 6 |
| `MATURACAO` | Maturação da cerveja | 7 |
| `CARBONATACAO` | Carbonatação natural ou forçada | 8 |
| `ENVASE` | Envase em garrafas/barris | 9 |

---

## 7. Pacote `dominio.comum`

### UnidadeMedida

| Valor | Símbolo | Usado Para |
|-------|---------|------------|
| `QUILOGRAMA` | kg | Malte, adjuntos |
| `GRAMA` | g | Lúpulo, especiarias |
| `LITRO` | L | Água, mosto, cerveja |
| `MILILITRO` | mL | Levedura líquida, aditivos |
| `UNIDADE` | un | Sachês, pacotes |

**Métodos planejados:**
- `String getSimbolo()`
- `UnidadeMedida getBase()` — retorna unidade base (kg para g, L para mL)
- `double converterPara(double valor, UnidadeMedida destino)`

---

### EscalaDensidade

| Valor | Descrição | Faixa Típica |
|-------|-----------|--------------|
| `SG` | Specific Gravity (gravidade específica) | 1.000 - 1.150 |
| `PLATO` | Graus Plato | 0° - 35° |

**Métodos planejados:**
- `double converterPara(double valor, EscalaDensidade destino)`

---

## 8. Resumo dos Enums por Pacote

| Pacote | Enums |
|--------|-------|
| `dominio.lote` | StatusLote, StatusEtapaExecutada |
| `dominio.insumo` | CategoriaInsumo |
| `dominio.estoque` | TipoMovimentoEstoque, MotivoMovimento |
| `dominio.qualidade` | TipoAvaliacaoQualidade, ParecerQualidade |
| `dominio.equipamento` | TipoEquipamento, StatusEquipamento |
| `dominio.receita` | StatusReceita, TipoEtapaProducao |
| `dominio.comum` | UnidadeMedida, EscalaDensidade |

**Total: 12 enums planejados**

---

## 9. Próximos Passos

1. Implementar os enums no pacote `dominio.comum` primeiro (UnidadeMedida, EscalaDensidade)
2. Implementar enums dos subdomínios (CategoriaInsumo, StatusLote, etc.)
3. Implementar os Value Objects que usam esses enums
4. Prosseguir para as entidades
