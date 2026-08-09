# Budget Planner

API REST para registro e consulta de despesas pessoais em **português do Brasil**, com suporte a **texto** e **áudio**. O backend usa **Spring AI** (OpenAI) para interpretar linguagem natural, persistir gastos no **PostgreSQL** e responder consultas com **tool calling**.

Projeto desenvolvido no contexto do Santander Bootcamp 2026 - AI Java Back-end, com arquitetura **hexagonal** (domínio, portas, adaptadores) como evolução da ideia apresentada em aula.

---

## Funcionalidades

| Área                              | Função                                                                                          |
|-----------------------------------|-------------------------------------------------------------------------------------------------|
| **Registro**                      | Criar gastos por texto ou áudio; a IA extrai valor, categoria e data (quando mencionada)        |
| **Consulta estruturada**          | Listar gastos por ano/mês (obrigatórios) e categoria opcional (JSON, sem IA)                    |
| **Consulta em linguagem natural** | Perguntar sobre gastos por texto ou áudio; a IA usa tools para buscar no banco                  |
| **Gestão manual**                 | Atualizar e remover gastos por `id` (ideal para UI ou Postman)                                  |
| **Voz**                           | Transcrição usando Whisper, gerando confirmação e respostas de consulta em MP3 (Text-To-Speech) |

**Limitação Intencional de Moeda para MVP:** para este MVP, a API opera apenas em **BRL**, mesmo separando um campo no banco de dados de despesas para que futuramente possam ser persistidas sob outras moedas. Valores de registro via IA são sempre interpretados como reais; no `PUT`, se `currency` é enviado, deve ser `BRL`. Como a consulta de valores é feita por tool calling, a implementação das funcionalidades necessárias para cálculo dinâmico com base em câmbio de moedas fugiria ao escopo do projeto para o bootcamp.

---

## Stack

- Java 21
- Spring Boot 4.1
- Spring AI 2.0 (OpenAI — chat, transcrição, TTS)
- Spring Data JPA + PostgreSQL 16
- Maven

---

## Arquitetura (visão geral)

```
presentation/     → ExpenseController, DTOs
application/      → serviços (registro, consulta, gestão, TTS, transcrição)
domain/           → Expense, Money, ExpenseCategory, portas (Repository, Parser, …)
infrastructure/   → JPA, Spring AI (parser, interpreter, tools, Whisper, TTS)
```

**Registro (texto/áudio):** transcrição (se áudio) → parser JSON (`SpringAiExpenseParser`) → `Expense` → repositório.

**Consulta por linguagem natural:** `SpringAiExpenseQueryInterpreter` + `ChatClient` → tool `listExpenses` → `ExpenseQueryService` → banco. A resposta em texto pode incluir dados estruturados das consultas executadas (`ExpenseQueryExecutionRecorder`).

**Consulta estruturada:** `GET /api/expenses` → `ExpenseQueryService` direto (sem IA).

---

## Pré-requisitos

- Java 21
- Docker (PostgreSQL)
- Chave da API OpenAI (`OPENAI_API_KEY`)

---

## Configuração e execução

### 1. Variáveis de ambiente

Copie o modelo e preencha:

```bash
cp .env.example .env
```

| Variável         | Descrição                        |
|------------------|----------------------------------|
| `DB_USERNAME`    | Usuário PostgreSQL               |
| `DB_PASSWORD`    | Senha PostgreSQL                 |
| `DB_NAME`        | Nome do banco (`budget_planner`) |
| `OPENAI_API_KEY` | Chave OpenAI                     |

O `application.yml` importa o arquivo `.env` automaticamente.

### 2. Banco de dados

```bash
docker compose up -d
```

PostgreSQL na porta **5434** (host) → `5432` (container).

### 3. Rodar a aplicação

**Windows:**

```powershell
.\mvnw.cmd spring-boot:run
```

**Linux/macOS:**

```bash
./mvnw spring-boot:run
```

URL base padrão: `http://localhost:8080`

### 4. Testes

```powershell
.\mvnw.cmd test
```

Testes de integração com OpenAI (`*IT.java` com `@EnabledIfEnvironmentVariable`) só rodam se `OPENAI_API_KEY` estiver definida no ambiente.

---

## API

Base path: `/api/expenses`

### Resumo dos endpoints

| Método   | Path                  | Content-Type          | Resposta                          |
|----------|-----------------------|-----------------------|-----------------------------------|
| `GET`    | `/api/expenses`       | —                     | JSON (listagem por mês)           |
| `POST`   | `/api/expenses`       | `application/json`    | JSON (`201`) — registro por texto |
| `POST`   | `/api/expenses`       | `multipart/form-data` | MP3 — registro por áudio          |
| `POST`   | `/api/expenses/query` | `application/json`    | JSON — consulta por texto         |
| `POST`   | `/api/expenses/query` | `multipart/form-data` | MP3 — consulta por áudio          |
| `PUT`    | `/api/expenses/{id}`  | `application/json`    | JSON                              |
| `DELETE` | `/api/expenses/{id}`  | —                     | `204` ou `404`                    |

O mesmo path `POST /api/expenses` e `POST /api/expenses/query` aceita **texto ou áudio**; o Spring distingue pelo `Content-Type`.

---

### Listar gastos (sem IA)

```http
GET /api/expenses?year=2026&month=8
GET /api/expenses?year=2026&month=8&category=RESTAURANT
```

Filtro por mês usa `COALESCE(occurredAt, createdAt)`. Para maior praticidade, o usuário não precisa informar o momento em que realizou cada despesa (mas, caso opte por informar, essa informação é persistida no banco de dados). Se o usuário não mencionou data no registro, entra no mês de `createdAt`; assim, consultas por gastos realizados em um determinado mês assumem que despesas sem uma data declarada foram realizadas no mês em que foram inseridas no banco de dados.

---

### Registrar por texto

```http
POST /api/expenses
Content-Type: application/json

{
  "text": "Gastei 45 reais no restaurante"
}
```

Resposta `201` com `ExpenseResponse` (`id`, `category`, `amount`, `currency`, `occurredAt`, `description`, `createdAt`).

---

### Registrar por áudio

```http
POST /api/expenses
Content-Type: multipart/form-data

audio: (arquivo .m4a, .mp3, .wav, …)
```

Resposta: MP3 de confirmação. Header `X-Expense-Id` contém o UUID do gasto criado.

---

### Consultar por texto (IA + JSON rico)

```http
POST /api/expenses/query
Content-Type: application/json

{
  "question": "Quanto gastei em restaurante em julho?"
}
```

Exemplo de resposta:

```json
{
  "question": "Quanto gastei em restaurante em julho?",
  "answerText": "Você gastou 45 reais em restaurante em julho de 2026.",
  "queries": [
    {
      "year": 2026,
      "month": 7,
      "categoryFilter": "RESTAURANT",
      "expenses": [ … ],
      "totalAmount": 45.90
    }
  ]
}
```

- `answerText`: resposta em português gerada pela IA.
- `queries`: cada execução da tool `listExpenses` durante a consulta (dados completos para UI).

---

### Consultar por áudio

```http
POST /api/expenses/query
Content-Type: multipart/form-data

audio: (arquivo com a pergunta)
```

Resposta: MP3 com a resposta falada.

---

### Atualizar gasto

```http
PUT /api/expenses/{id}
Content-Type: application/json

{
  "category": "RESTAURANT",
  "amount": 54.00,
  "currency": "BRL",
  "occurredAt": "2026-07-15T12:00:00Z",
  "description": "Almoço (corrigido)"
}
```

Campos obrigatórios: `category`, `amount`. `currency` opcional (default BRL); se enviado, deve ser `BRL`.

---

### Remover gasto

```http
DELETE /api/expenses/{id}
```

`204 No Content` se removido; `404` se o `id` não existe.

---

## Categorias de despesa

Valores do enum `ExpenseCategory` (usar exatamente assim na API):

| Valor        | Uso típico                           |
|--------------|--------------------------------------|
| `GROCERIES`  | Mercado                              |
| `PHARMACY`   | Farmácia                             |
| `AUTO`       | Automóvel                            |
| `TRANSPORT`  | Transporte                           |
| `RESTAURANT` | Restaurante                          |
| `HEALTH`     | Saúde                                |
| `OTHER`      | Outros / quando a IA não tem certeza |

Nomes do enum (`RESTAURANT`, …) — uso na API estruturada e no que a IA persiste no banco.

Adicionar categoria — nova constante em   `ExpenseCategory` + `spokenLabel` + redeploy;
   prompts e tools se atualizam automaticamente.

`spokenLabel` — serve para a mensagem de confirmação
   em áudio após registro (e.g.: “categoria farmácia”);
   consultas em linguagem natural usam o texto
   que a IA gera.
---

## Fluxo típico (gestão manual, sem frontend)

1. `POST /api/expenses` (texto ou áudio) — criar gastos.
2. `GET /api/expenses?year=&month=` — listar com `id` em cada item.
3. `PUT /api/expenses/{id}` ou `DELETE /api/expenses/{id}` — corrigir ou apagar.

Gastos criados por áudio aparecem na listagem; naturalmente, o UUID não é enunciado na mensagem de voz sintetizada, mas consta no JSON da listagem.

---

## Estrutura do projeto

```
src/main/java/com/santander/bootcamp/budget_planner/
├── application/          # Casos de uso e orquestração
├── domain/               # Modelo e portas
├── infrastructure/       # JPA, Spring AI
└── presentation/         # Controller e DTOs
```

---

## Limitações do MVP

- Sem autenticação (uso local / demonstração).
- Sem multi-moeda ou conversão cambial.
- Sem update/delete por linguagem natural (apenas por `id` via REST).
- Schema JPA com `ddl-auto: update` (adequado ao bootcamp; produção usaria migrations).
- Dependência de APIs OpenAI para registro e consultas com IA.

---

## Licença

Projeto educacional — Santander Bootcamp 2026 - AI Java Back-end.
