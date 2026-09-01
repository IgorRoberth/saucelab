# SPEC — Suíte de Automação E2E Swag Labs

Documento de especificação técnica. Define decisões, convenções e o catálogo de casos de teste antes da implementação.

**Alvo:** https://www.saucedemo.com
**Status:** especificação — nenhum item implementado
**Última revisão:** 2026-08-31

---

## 1. Objetivo

Demonstrar, em repositório público, competência em:

1. Arquitetura de suíte E2E sustentável (Page Object Model disciplinado)
2. Estabilidade de execução — suíte que não falha sem o código mudar
3. Pipeline de CI/CD com feedback rápido e evidência de falha investigável

**Não é objetivo:** cobertura exaustiva da aplicação. A suíte cobre fluxos representativos, com profundidade em vez de volume.

---

## 2. Decisões técnicas

Cada decisão registrada com a alternativa descartada e o motivo.

| # | Decisão | Alternativa descartada | Motivo |
|---|---|---|---|
| D-01 | Java 21 | Python, JavaScript | Exigência do scanner do SonarQube Cloud a partir de jul/2026; alinhamento com o mercado financeiro |
| D-02 | Playwright | Selenium WebDriver | Auto-waiting nativo reduz flakiness; gestão automática de binários de browser; imagem Docker oficial para CI |
| D-03 | Page Object Model | Screenplay Pattern | Legibilidade imediata para quem abre o repositório pela primeira vez; Screenplay adiciona indireção sem ganho neste escopo |
| D-04 | JUnit 5 | TestNG | Ecossistema mais ativo, integração direta com Allure e Maven Surefire |
| D-05 | Playwright web-first assertions para o DOM; AssertJ apenas para valores puros | AssertJ para tudo | Asserção do Playwright reavalia até o timeout, eliminando a maior fonte de flakiness; AssertJ sobre `textContent()` congela o DOM num instante único |
| D-06 | GitHub Actions como única CI | GitLab CI espelhada | Minutos ilimitados em repositório público; espelhar a mesma suíte numa segunda plataforma não acrescenta cobertura nem informação, só custo de manutenção |

---

## 3. Convenções de código

### Idioma
Código, nomes e commits em inglês. Documentação e descrição de negócio dos cenários em português.

### Nomenclatura

| Elemento | Padrão | Exemplo |
|---|---|---|
| Classe de página | Substantivo + `Page` | `CheckoutPage` |
| Classe de teste | Área + `Test` | `AuthenticationTest` |
| Método de ação | Verbo no infinitivo | `addToCart()` |
| Método de consulta | Substantivo, sem prefixo `get`, retorna `Locator` | `errorMessage()` |
| Método de teste | `should` + comportamento esperado | `shouldBlockLockedOutUser()` |

### Regras invioláveis

1. Nenhum seletor fora de `pages/` ou `components/`
2. Nenhuma asserção dentro de `pages/` ou `components/`
3. Nenhum `Thread.sleep`, `waitFor()` ou `waitForSelector()` — a espera é feita por web-first assertion
4. Método de consulta retorna `Locator`, nunca `String` — `textContent()` seguido de asserção é proibido
5. Método que causa navegação retorna a instância da próxima página
6. Credenciais e massa de teste vivem em `data/`, nunca inline no teste

### Estratégia de seletor

Ordem de preferência: `data-test` → `id` → seletor CSS semântico. XPath só quando não houver alternativa, com comentário justificando.

O Swag Labs expõe atributos `data-test` na maior parte dos elementos — deve ser a escolha padrão.

---

## 4. Massa de teste

Usuários disponíveis no ambiente, todos com a mesma senha pública documentada no site.

| Usuário | Comportamento esperado | Uso na suíte |
|---|---|---|
| `standard_user` | Fluxo normal | Caminho feliz de todos os módulos |
| `locked_out_user` | Bloqueado no login | AUTH-004 |
| `problem_user` | Imagens e campos com defeito proposital | STATE-001, STATE-002 |
| `performance_glitch_user` | Latência artificial elevada | STATE-003 |

> Confirmar a lista completa e a senha na página inicial do Swag Labs antes de implementar — o conjunto de usuários já foi ampliado ao longo do tempo.

---

## 5. Catálogo de casos de teste

Cada caso tem ID rastreável, referenciado no relatório Allure e na mensagem de commit.

Onde o resultado esperado fala em "redireciona" ou "retorna", a verificação é feita sobre um elemento exclusivo da página de destino — nunca sobre a URL. `assertThat(page).hasURL(...)` exige o `page`, que não é acessível em `tests/`, e uma URL correta não prova que a página carregou.

### AUTH — Autenticação

| ID | Cenário | Resultado esperado |
|---|---|---|
| AUTH-001 | Login com credenciais válidas | Redireciona para o inventário |
| AUTH-002 | Login com senha incorreta | Exibe erro; permanece na tela de login |
| AUTH-003 | Login com campos vazios | Exibe erro de campo obrigatório |
| AUTH-004 | Login com usuário bloqueado | Exibe mensagem de bloqueio |
| AUTH-005 | Logout pelo menu lateral | Retorna ao login; sessão encerrada |
| AUTH-006 | Acesso direto ao inventário sem sessão | Bloqueia o acesso e exibe a tela de login com mensagem de sessão ausente (ver nota) |

> **AUTH-006 — comportamento observado.** A aplicação **não redireciona**: a URL permanece `/inventory.html` e a tela de login é renderizada no lugar do inventário, com a mensagem `Epic sadface: You can only access '/inventory.html' when you are logged in.` É a razão pela qual a suíte assevera o elemento da tela de destino, e não a URL — uma asserção de URL passaria aqui com o usuário barrado do lado de fora. Observado em 2026-08-31.

### CAT — Catálogo

| ID | Cenário | Resultado esperado |
|---|---|---|
| CAT-001 | Listagem carrega todos os produtos | Quantidade e nomes conferem com a massa |
| CAT-002 | Ordenação por nome (A→Z e Z→A) | Ordem alfabética correta |
| CAT-003 | Ordenação por preço (menor e maior) | Ordem numérica correta |
| CAT-004 | Abrir detalhe do produto | Nome, preço e descrição conferem com a listagem |

### CART — Carrinho

| ID | Cenário | Resultado esperado |
|---|---|---|
| CART-001 | Adicionar um item | Badge do carrinho exibe 1 |
| CART-002 | Adicionar múltiplos itens | Badge reflete a quantidade total |
| CART-003 | Remover item pela listagem | Badge decrementa |
| CART-004 | Remover item dentro do carrinho | Item some da lista |
| CART-005 | Persistência ao navegar entre páginas | Conteúdo do carrinho preservado |

### CKO — Checkout

| ID | Cenário | Resultado esperado |
|---|---|---|
| CKO-001 | Fluxo completo até confirmação | Página de pedido concluído |
| CKO-002 | Campos obrigatórios vazios | Erro por campo, sem avançar |
| CKO-003 | Cálculo do total | Subtotal + imposto conferem com a soma dos itens |
| CKO-004 | Cancelar no meio do fluxo | Retorna sem criar pedido; carrinho intacto |
| CKO-005 | Checkout com carrinho vazio | Comportamento documentado (ver nota) |

> **CKO-005** é um caso de exploração: o comportamento esperado não está definido pela aplicação. O resultado observado deve ser registrado como achado no README, não forçado a passar.

### STATE — Estados degradados

| ID | Cenário | Resultado esperado |
|---|---|---|
| STATE-001 | Catálogo sob `problem_user` | Falha detectada e reportada, não mascarada |
| STATE-002 | Checkout sob `problem_user` | Falha detectada e reportada |
| STATE-003 | Login sob `performance_glitch_user` | Conclui dentro do timeout configurado |

> Os casos STATE existem para demonstrar que a suíte **detecta** defeito, não apenas confirma o caminho feliz. Um teste que passa com `problem_user` é um teste com asserção fraca.

---

## 6. Estabilidade

| Mecanismo | Configuração |
|---|---|
| Espera | Web-first assertions do Playwright; `Thread.sleep`, `waitFor` e `waitForSelector` proibidos |
| Consulta de estado | Page Objects retornam `Locator`, nunca `String` extraída do DOM |
| Timeout padrão | 30s por ação; ajustável por teste quando justificado |
| Retry | 2 tentativas apenas em CI; zero localmente |
| Quarentena | `@Tag("quarantine")` — sai da pipeline de bloqueio, roda em job separado, exige issue aberta |
| Isolamento | Contexto de browser novo por teste; sem estado compartilhado |

Teste instável nunca é desabilitado em silêncio. Vai para quarentena com issue rastreável ou é corrigido.

---

## 7. Pipeline

| Gatilho | Escopo |
|---|---|
| Push e pull request | Suíte completa, matrix de 3 browsers |
| Agendado semanal | Regressão contra o ambiente público |
| Manual (`workflow_dispatch`) | Inputs para escolher browser e suíte |

**Quality gate — bloqueia o merge:**
- Falha de qualquer teste fora da quarentena
- Quality gate do SonarQube Cloud reprovado
- Alerta de severidade alta no CodeQL

**Artefatos em falha:** `trace.zip`, vídeo, screenshot.

---

## 8. Definition of Done

Um item só é marcado como concluído quando:

- [ ] Testes passam em execução local
- [ ] Testes passam nos três browsers em CI
- [ ] Nenhum seletor fora das Page Objects
- [ ] Nenhuma asserção dentro das Page Objects
- [ ] Nenhuma asserção sobre `String` lida do DOM — consultas retornam `Locator`
- [ ] Nenhuma asserção sobre `String` extraída do DOM — apenas web-first assertions
- [ ] Caso de teste referenciado por ID no relatório
- [ ] Quality gate do SonarQube verde
- [ ] Executado três vezes seguidas em CI sem falha intermitente

O último item é o que separa suíte confiável de suíte que "funciona na minha máquina".

---

## 9. Fora de escopo

| Item | Motivo |
|---|---|
| Testes de API | O Swag Labs não expõe backend público |
| Validação de banco | Sem acesso à camada de dados |
| Teste de carga (k6) | Gerar volume contra ambiente público de terceiros é abuso de infraestrutura alheia |
| Testes de segurança ativos | Mesma razão: varredura contra alvo não autorizado |
| Cobertura de acessibilidade completa | Limitada aos fluxos principais via axe-core |
