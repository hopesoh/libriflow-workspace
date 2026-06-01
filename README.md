# LibriFlow - Arquitetura de Microsserviços

## Visão Geral
O LibriFlow é um projeto focado no estudo prático e na aplicação de arquitetura evolutiva, demonstrando a transição estruturada de um sistema monolítico para uma arquitetura de microsserviços. O sistema simula um fluxo de e-commerce de livros, orquestrando domínios de usuários, catálogo e pedidos.

## Motivação
Este projeto foi desenvolvido com base nos conceitos arquiteturais de separação de domínios, escalabilidade e alta disponibilidade. Inspirado na literatura de transição de sistemas (notoriamente nos padrões abordados em "Monolith to Microservices" de Sam Newman), o objetivo principal é aplicar o padrão *Strangler Fig* em um cenário realista. A implementação aborda desafios técnicos cruciais do desenvolvimento backend distribuído, como o desacoplamento de bancos de dados, comunicação síncrona entre serviços, roteamento centralizado e engenharia de resiliência.

## Tecnologias Utilizadas
* **Linguagem / Framework:** Java, Spring Boot
* **Roteamento:** Spring Cloud Gateway
* **Comunicação Síncrona:** Spring Cloud OpenFeign
* **Resiliência:** Resilience4j (Circuit Breaker e Fallback)
* **Persistência:** Spring Data JPA, PostgreSQL
* **Infraestrutura:** Docker, Docker Compose
* **Gerenciamento de Dependências:** Maven (Estrutura Monorepo)

## Fases de Implementação

### Fase 1: Arquitetura Monolítica e Base de Dados
A fundação do projeto se iniciou como um monólito clássico, centralizando todas as regras de negócio. Nesta etapa preparatória, o banco de dados em memória (H2) foi substituído por um banco de dados relacional definitivo (PostgreSQL) containerizado via Docker, garantindo persistência consistente para os próximos passos da evolução.

### Fase 2: O Padrão Strangler Fig (Extração do User Service)
O primeiro movimento em direção à arquitetura distribuída ocorreu com o isolamento do domínio de usuários. O código e o banco de dados foram extraídos para um microsserviço independente (`user-service`). O monólito passou a atuar como cliente, comunicando-se com o novo serviço através de chamadas HTTP via OpenFeign, eliminando o acoplamento estrutural.

### Fase 3: Roteamento Centralizado (API Gateway)
Para preservar o encapsulamento da rede interna e unificar o ponto de consumo para aplicações clientes, foi introduzido o `api-gateway`. Configurado com o Spring Cloud Gateway, este serviço assumiu a responsabilidade de interceptar as requisições externas na porta 9000 e roteá-las dinamicamente para as instâncias corretas no cluster.

### Fase 4: Tolerância a Falhas (Circuit Breaker)
A transição para chamadas de rede introduziu o risco de falhas em cascata (*Cascading Failures*). Para proteger o sistema, o padrão Circuit Breaker foi integrado ao orquestrador através da biblioteca Resilience4j. Foram estabelecidas estratégias de *Graceful Degradation* (Fallback), permitindo que a aplicação retorne respostas controladas em cenários de indisponibilidade ou latência elevada nos microsserviços adjacentes.

### Fase 5: Isolamento do Catálogo e Redução a Order Service
O catálogo de livros foi segregado para um novo serviço (`book-service`) com esquema de dados isolado. O monólito original foi formalmente reduzido à responsabilidade exclusiva de gerenciar pedidos (`order-service`). No nível de banco de dados, chaves estrangeiras (Foreign Keys) foram substituídas por referências de identificadores simples (IDs), estabelecendo limites estritos e independentes para os Bounded Contexts.

## Como Executar o Projeto

1. **Subir a Infraestrutura de Dados:**
   Execute o arquivo Docker Compose localizado na raiz do projeto para inicializar a instância do PostgreSQL.
   ```bash
   docker-compose up -d
   ```

2. **Inicializar os Microsserviços:**
Compile e inicialize os projetos na seguinte ordem para garantir a correta injeção de dependências e disponibilidade de rotas:

- user-service (porta 8081)
- book-service (porta 8082)
- libriflow [Order Service] (porta 8080)
- api-gateway (porta 9000)

3. **Acessar a Aplicação:**
Todas as requisições devem ser realizadas exclusivamente através do API Gateway. **URL Base:** http://localhost:9000

4. **Endpoints para Teste de Integração (E2E):**
   Para validar o roteamento do Gateway e a comunicação síncrona entre os microsserviços via OpenFeign, os seguintes endpoints de teste podem ser consumidos:

   * **Validação de Usuário (Comunicação com user-service):**
     `GET http://localhost:9000/teste-integracao/{userId}`

   * **Validação de Catálogo (Comunicação com book-service):**
     `GET http://localhost:9000/teste-integracao-livro/{bookId}`

   * **Teste de Fluxo Completo (Criação de Pedido):**
     `POST http://localhost:9000/api/orders`
     *(Requer o envio de um payload em formato JSON contendo os identificadores correspondentes ao usuário e ao livro).*

## Próximos Passos (TODO)

* **Refatoração e Boas Práticas de Código:** Revisar o código base de todos os microsserviços resultantes da decomposição do monólito, visando o rigor técnico e o alinhamento com princípios de design de software (como SOLID) e padrões arquiteturais consolidados. As atividades de refatoração planejadas incluem:
  * Padronização do tráfego de dados isolando entidades de domínio através de DTOs (Data Transfer Objects) estruturados.
  * Implementação de tratamento global e padronizado de exceções (via `@ControllerAdvice`), garantindo respostas HTTP consistentes para clientes da API.
  * Aprimoramento da cobertura de testes (unitários e de integração) considerando as novas fronteiras dos serviços.
  * Aplicação de padrões de log estruturados e correlação de requisições (*Distributed Tracing*) para facilitar a observabilidade do fluxo de ponta a ponta.
