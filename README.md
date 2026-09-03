# Swag Labs — Suíte de Automação E2E

[![E2E](https://github.com/IgorRoberth/saucelab/actions/workflows/e2e.yml/badge.svg)](https://github.com/IgorRoberth/saucelab/actions/workflows/e2e.yml)
[![CodeQL](https://github.com/IgorRoberth/saucelab/actions/workflows/codeql.yml/badge.svg)](https://github.com/IgorRoberth/saucelab/actions/workflows/codeql.yml)
[![Sonar](https://github.com/IgorRoberth/saucelab/actions/workflows/sonar.yml/badge.svg)](https://github.com/IgorRoberth/saucelab/actions/workflows/sonar.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**[Relatório Allure da última execução](https://igorroberth.github.io/saucelab/)**

Suíte de testes end-to-end em **Java + Playwright**, estruturada em **Page Object Model**, com pipeline de CI/CD completa em GitHub Actions.

O alvo é o [Swag Labs / SauceDemo](https://www.saucedemo.com) — aplicação pública mantida pela Sauce Labs para prática de automação, que expõe cenários de login com usuário bloqueado, usuário com falhas de renderização e usuário com degradação de performance.

> Projeto de portfólio. O objetivo não é cobrir 100% da aplicação, e sim demonstrar arquitetura de suíte, estabilidade de execução e maturidade de pipeline.

---

## Por que este projeto existe

Muita suíte de automação funciona na máquina de quem escreveu e falha em qualquer outro lugar. As decisões aqui são orientadas a três problemas concretos:

| Problema | Decisão |
|---|---|
| Teste que quebra sem o código mudar (flakiness) | Auto-waiting do Playwright, zero `sleep`, retry só em CI |
| Suíte que demora e trava o feedback | Execução paralela por shards + matrix de browsers |
| Falha em CI sem contexto para investigar | Screenshot de todo teste; trace e vídeo na falha, anexados ao run |

---

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Motor de automação | Playwright for Java |
| Test runner | JUnit 5 |
| Asserções | Playwright web-first assertions (DOM) + AssertJ (valores puros) |
| Build | Maven |
| Relatório | Allure Report, publicado no GitHub Pages |
| CI | GitHub Actions |
| Qualidade de código | SonarQube Cloud |
| Segurança | CodeQL + Dependabot |
| Performance | Lighthouse CI |

Todas as ferramentas usadas são gratuitas para repositórios públicos.

---

## Arquitetura

```
src/test/java/
├── pages/          # Page Objects — seletores e ações da página
├── components/     # Componentes reutilizáveis (header, menu lateral)
├── tests/          # Cenários de teste — só orquestração e asserção
├── support/        # BaseTest, fixtures, configuração do Playwright
└── data/           # Massa de teste e enums de usuário
```

### Regras de arquitetura

O padrão só entrega valor se for respeitado sem exceção. Estas cinco regras são o contrato do repositório:

1. **Nenhum seletor fora de `pages/`.** Se um `locator("#id")` aparecer em `tests/`, o padrão vazou.
2. **Nenhuma asserção dentro de `pages/`.** Page Object descreve capacidade; o veredito é responsabilidade do teste.
3. **Métodos de navegação retornam a próxima página.** Isso torna o teste legível como uma frase e habilita encadeamento com autocomplete.
4. **Métodos são verbos, classes são substantivos.** `LoginPage.loginAs()`, `CartPage.removeItem()`.
5. **Métodos de consulta retornam `Locator`, nunca `String`.** `textContent()` lê o DOM uma vez só; o `Locator` deixa a asserção do Playwright reavaliar até o timeout.

### Exemplo

```java
@Test
@DisplayName("Usuário bloqueado não deve acessar o inventário")
void shouldBlockLockedOutUser() {
    LoginPage loginPage = new LoginPage(page).navigate();

    loginPage.loginExpectingFailure(User.LOCKED_OUT);

    assertThat(loginPage.errorMessage())
        .hasText(ErrorMessages.LOCKED_OUT);
}
```

O código é escrito em inglês; a documentação e a descrição de negócio dos cenários, em português.

---

## Escopo de testes

| Área | Cenários |
|---|---|
| Autenticação | Login válido, senha incorreta, campos vazios, usuário bloqueado |
| Catálogo | Listagem, ordenação por nome e preço, detalhe do produto |
| Carrinho | Adicionar, remover, persistência entre páginas |
| Checkout | Fluxo completo, validação de campos obrigatórios, cálculo de total |
| Visual/estado | Comportamento sob `problem_user` e `performance_glitch_user` |

Cada teste tem um ID rastreável (`AUTH-001`, `CART-003`) referenciado no relatório.

---

## Pipeline

### GitHub Actions

Repositório público, portanto minutos ilimitados em runner padrão.

- **Push e pull request** — suíte completa, matrix de Chromium/Firefox/WebKit
- **Agendado semanal** — verificação de regressão contra o ambiente público
- **`workflow_dispatch`** — execução manual com inputs para escolher browser e suíte
- **Sharding** — execução dividida em paralelo, com merge dos relatórios ao final
- **Cache** — dependências Maven e binários do Playwright
- **Evidência por teste** — uma pasta por caso em `target/evidencias/`, com screenshot sempre e `trace.zip` + vídeo na falha
- **Relatório Allure** — HTML gerado e anexado a todo run, verde ou vermelho
- **Job summary** — resumo de passou/falhou direto na aba Actions
- **Passo a passo no relatório** — cada ação e cada validação viram um passo nomeado, com screenshot próprio
- **GitHub Pages** — relatório Allure publicado a cada execução

---

## Pré-requisitos

| Requisito | Versão mínima | Verificar com |
|---|---|---|
| JDK | 21 | `java -version` |
| Maven | 3.9 | `mvn -version` |
| Git | qualquer | `git --version` |
| Espaço em disco | ~1,5 GB | — |

O espaço é necessário porque o Playwright baixa Chromium, Firefox e WebKit em versões próprias, isoladas do sistema.

**Não é necessário:** ter Chrome, Firefox ou Edge instalados, baixar chromedriver ou geckodriver, nem configurar `PATH` de driver. O Playwright gerencia tudo.

### Instalando o JDK 21

```bash
# Linux (Ubuntu/Debian)
sudo apt install openjdk-21-jdk

# macOS
brew install openjdk@21

# Windows — baixar o instalador em https://adoptium.net
```

Confirme que `JAVA_HOME` aponta para o JDK 21. Versão inferior faz o build falhar e também é rejeitada pelo scanner do SonarQube Cloud.

---

## Instalação

```bash
git clone https://github.com/IgorRoberth/<nome-do-repo>.git
cd <nome-do-repo>

mvn clean install -DskipTests
```

Em seguida, baixe os browsers do Playwright. Este passo é obrigatório e roda uma única vez:

```bash
mvn exec:java -D exec.mainClass=com.microsoft.playwright.CLI \
              -D exec.args="install"
```

Em Linux, se faltar biblioteca de sistema:

```bash
mvn exec:java -D exec.mainClass=com.microsoft.playwright.CLI \
              -D exec.args="install-deps"
```

---

## Executando

```bash
mvn test                              # suíte completa, headless
mvn test -D browser=firefox           # browser específico (chromium | firefox | webkit)
mvn test -D headed=true               # com interface visível
mvn test -D test=AuthenticationTest   # uma classe
mvn test -D groups=smoke              # por tag
```

Testes em quarentena ficam fora da execução padrão:

```bash
mvn test -D groups=quarantine -D excludedGroups=    # roda apenas os instáveis
```

### Relatório

```bash
mvn allure:report      # gera em target/allure-report
mvn allure:serve       # gera e abre no navegador
```

O relatório da última execução em CI fica publicado no GitHub Pages — link no topo deste README.

### Métricas do relatório

O Allure publicado traz, além do passo a passo de cada caso, seis painéis de leitura rápida:

| Painel | O que responde | De onde vem o dado |
|---|---|---|
| Situação | Quanto da suíte passou, falhou, quebrou ou foi ignorado neste run | Status de cada teste |
| Duração | Como os testes se distribuem por tempo de execução — expõe o caso lento isolado | Cronometragem do run |
| Severidade | O que quebrou pesa quanto: uma falha `blocker` e uma `minor` não valem o mesmo | `@Severity` nos testes |
| Tendência | Passou/falhou run a run — mostra regressão e instabilidade ao longo do tempo | Histórico acumulado |
| Tendência das durações | Se a suíte está ficando mais lenta a cada run | Histórico acumulado |
| Tendência das tentativas | Quantos testes só passaram no retry — é o termômetro de flakiness | Histórico acumulado |
| Tendência das categorias | Se o tipo de falha muda de perfil (defeito de produto x defeito de teste) | Histórico acumulado |

**Severidade.** Todo teste declara o impacto do que ele protege, para que a triagem de um run vermelho comece pelo que importa:

| Nível | Critério | Exemplos |
|---|---|---|
| `blocker` | Sem isto não há produto | Login válido, listagem do catálogo, fluxo completo de checkout |
| `critical` | Perda de dinheiro ou de controle de acesso | Usuário bloqueado, acesso sem sessão, adicionar ao carrinho, cálculo do total |
| `normal` | Funcionalidade esperada, com contorno | Validações de formulário, logout, ordenação por preço |
| `minor` | Incômodo visível, sem impedir a compra | Ordenação por nome, imagem genérica sob `problem_user` |
| `trivial` | Comportamento apenas documentado | CKO-005, o checkout que conclui com carrinho vazio |

**Tendências.** Os quatro gráficos de tendência são desenhados a partir da pasta `history/` do relatório anterior — sem ela o Allure não tem memória e o painel sai vazio. No CI o `target/` nasce limpo a cada run, então o job de publicação busca esse histórico de onde ele de fato está: o próprio relatório publicado no Pages. Junto vai um `executor.json`, que transforma cada ponto do gráfico em link para o run que o produziu.

Consequência prática: um run isolado mostra um ponto só. A tendência começa a ter leitura a partir do segundo run publicado em `main`.

---

### Investigando falhas

A execução deixa uma pasta por teste em `target/evidencias/`, nomeada pelo ID do catálogo —
`AUTH-001-shouldSignInStandardUser/`. Todo teste guarda o screenshot final; o que falha guarda
também o vídeo e o `trace.zip`. Abra o trace com:

```bash
mvn exec:java -D exec.mainClass=com.microsoft.playwright.CLI \
              -D exec.args="show-trace target/evidencias/<pasta>/trace-<browser>.zip"
```

O trace viewer mostra timeline, snapshot do DOM em cada passo, requisições de rede e console. É a forma mais rápida de entender uma falha que só acontece em CI.

---

## Variáveis de ambiente

Nenhuma é obrigatória para execução local — os defaults cobrem o cenário padrão.

| Variável | Default | Uso |
|---|---|---|
| `BASE_URL` | `https://www.saucedemo.com` | Apontar para outro ambiente |
| `BROWSER` | `chromium` | Browser padrão |
| `HEADED` | `false` | Execução com interface |
| `TIMEOUT_MS` | `30000` | Timeout por ação |
| `RETRIES` | `0` local / `2` em CI | Tentativas em caso de falha |

Credenciais não são variáveis de ambiente: o Swag Labs publica os usuários de teste na própria tela de login, e eles vivem em `data/`.

---

## Problemas comuns

| Sintoma | Causa provável | Solução |
|---|---|---|
| `Executable doesn't exist` | Browsers não baixados | Rodar o comando `install` |
| Falha ao iniciar browser no Linux | Bibliotecas de sistema ausentes | Rodar `install-deps` |
| `UnsupportedClassVersionError` | JDK abaixo de 21 | Corrigir `JAVA_HOME` |
| Testes passam local e falham em CI | Diferença de timing ou viewport | Abrir o `trace.zip` do artefato do run |
| Download dos browsers muito lento | Rede corporativa com proxy | Definir `HTTPS_PROXY` antes do install |

---

## Tratamento de flakiness

- **Zero `Thread.sleep`.** O auto-waiting do Playwright aguarda o elemento estar visível, estável e habilitado antes de qualquer ação.
- **Espera é asserção, não comando.** Nada de `waitFor()` ou `waitForSelector()` onde existe web-first assertion: `assertThat(locator).isVisible()` reavalia sozinha até o timeout. Asserção sobre `String` capturada do DOM é proibida — congela o valor no instante errado.
- **Retry apenas em CI**, nunca local — retry local esconde bug real durante o desenvolvimento.
- **Quarentena.** Teste instável é marcado com `@Tag("quarantine")`, sai da pipeline de bloqueio e vai para execução separada, com issue aberta. Nunca é desabilitado em silêncio.

---

## Achados

Comportamentos encontrados no alvo durante a implementação, documentados por teste.

| ID | Achado |
|---|---|
| CKO-005 | O checkout conclui um pedido com o carrinho vazio. Os três passos aceitam a cesta sem itens, o resumo exibe `Total: $0.00` e a confirmação é exibida normalmente. Coberto por teste que documenta o comportamento atual — se a aplicação for corrigida, o teste quebra. |

---

## Limitações conhecidas

Declaradas de propósito, porque o alvo é uma aplicação de demonstração:

- Sem testes de API — o Swag Labs não expõe backend público.
- Sem validação de banco de dados.
- **Sem teste de carga.** A ferramenta natural aqui seria o k6, e ela foi deliberadamente deixada de fora: gerar volume contra um ambiente público mantido por terceiros é abuso de infraestrutura alheia, independente da intenção. Teste de carga exige ambiente próprio e autorização — condições que este projeto não tem.
- Cobertura de acessibilidade limitada aos fluxos principais.

---

## Estrutura de documentação

| Arquivo | Papel |
|---|---|
| `README.md` | Porta de entrada — o que é, como instalar, como executar |
| `SPECS/SPEC_MODEL.md` | Decisões técnicas, convenções e catálogo de casos de teste |
| `.cursorrules` | Contrato de engenharia — regras de arquitetura e Clean Code |

---

## Roadmap de implementação

Ordem deliberada: o repositório fica verde e funcional antes de ficar bonito.

- [x] Suíte executando localmente com Page Object Model
- [x] Workflow básico no GitHub Actions
- [x] CodeQL + Dependabot
- [ ] SonarQube Cloud — workflow pronto, aguardando projeto criado e `SONAR_TOKEN` no repositório
- [x] Allure Report publicado no GitHub Pages
- [x] Matrix de browsers
- [ ] Sharding
- [x] Lighthouse CI

---

## Autor

Igor Roberth — QA com foco em automação e segurança.
[LinkedIn](https://linkedin.com/in/igorroberth) · [GitHub](https://github.com/IgorRoberth)
---

## Licença

Distribuído sob a [Licença MIT](LICENSE) — uso livre, inclusive comercial, mantendo o aviso de copyright.
