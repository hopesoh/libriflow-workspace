# LibriFlow

E-commerce de livros construído como um **Monólito em Camadas** em Java 23 + Spring Boot 3.

> Este projeto é um **estudo de anti-padrões**. O código é funcional, mas foi escrito
> 
> intencionalmente com más práticas para servir como base de comparação com arquiteturas bem estruturadas.
> 
> https://www.amazon.com/Monolith-Microservices-Evolutionary-Patterns-Transform/dp/1492047848


<img width="582" height="764" alt="image" src="https://github.com/user-attachments/assets/4fb3286e-76fd-4b1d-9c9d-452dbc19798b" />

---

## Arquitetura

O projeto segue organização **Package by Layer** (por camada técnica, não por domínio):

```
com.libriflow
├── model/          → Book, User, Order  (entidades JPA com acoplamento forte entre si)
├── repository/     → BookRepository, UserRepository, OrderRepository
├── service/        → BookService, UserService, OrderService  (serviços anêmicos)
└── controller/     → BookController, UserController, OrderController
```

---

## Anti-padrões Implementados

| Anti-padrão | Onde está | Por que vai doer |
|---|---|---|
| **Acoplamento forte por entidade** | `Order.java` — `List<Book>` e `User` direto | Separar pagamentos do catálogo vira uma cirurgia |
| **Lógica no controller** | `OrderController#purchase` | Impossível testar unitariamente sem subir o contexto inteiro |
| **N+1 writes no loop** | `OrderController` — `bookRepository.save(book)` dentro do `for` | Um pedido com 10 livros = 10 UPDATEs separados + 1 INSERT |
| **FetchType.EAGER em coleções** | `Book.orders`, `User.orders`, `Order.books` | Buscar qualquer livro carrega todos os pedidos na memória |
| **Repository injetado no controller** | `BookController` e `OrderController` | A camada de serviço existe mas é bypassed à vontade |
| **Entidade JPA como payload** | Todos os endpoints | `POST /api/users` recebe senha em texto, `GET /api/orders` devolve estoque dos livros |
| **Senha em texto puro** | `User.password` + `UserService#save` | Sem `@JsonIgnore`, a senha sai em todo `GET /api/users` |
| **Service anêmico** | `OrderService.java` | Tem 4 métodos que apenas delegam pro repository — a lógica real está no controller |

---

## Rodando o Projeto

**Pré-requisito:** Java 23 e Maven 3.8+

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 23)
./mvnw spring-boot:run
```

O banco H2 em memória é criado automaticamente. Ao iniciar, 2 usuários e 4 livros
são inseridos via `CommandLineRunner` na própria classe `LibriFlowApplication`.

---

## Endpoints

### Livros — `/api/books`

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/books` | Lista todos os livros |
| `GET` | `/api/books/{id}` | Busca livro por ID |
| `GET` | `/api/books/search?title=` | Busca por título |
| `GET` | `/api/books/search?author=` | Busca por autor |
| `GET` | `/api/books/by-price?max=50.00` | Busca por preço máximo |
| `GET` | `/api/books/in-stock` | Lista livros com estoque disponível |
| `POST` | `/api/books` | Cria novo livro |
| `PUT` | `/api/books/{id}` | Atualiza livro |
| `DELETE` | `/api/books/{id}` | Remove livro |

**Exemplo de criação de livro:**
```json
POST /api/books
{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "978-0132350884",
  "price": 89.90,
  "stock": 15
}
```

### Usuários — `/api/users`

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/users` | Lista todos os usuários (senha exposta!) |
| `GET` | `/api/users/{id}` | Busca usuário por ID |
| `POST` | `/api/users` | Cria usuário (senha em texto puro) |
| `PUT` | `/api/users/{id}` | Atualiza usuário |
| `DELETE` | `/api/users/{id}` | Remove usuário |

**Exemplo de criação de usuário:**
```json
POST /api/users
{
  "name": "Ana Costa",
  "email": "ana@example.com",
  "password": "123456"
}
```

### Pedidos — `/api/orders`

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/orders` | Lista todos os pedidos |
| `GET` | `/api/orders/{id}` | Busca pedido por ID |
| `GET` | `/api/orders/user/{userId}` | Pedidos de um usuário |
| `GET` | `/api/orders/user/{userId}/status/{status}` | Pedidos por usuário e status |
| `POST` | `/api/orders/purchase?userId=1` | **Realiza compra** (lógica no controller) |
| `DELETE` | `/api/orders/{id}` | Remove pedido |

**Exemplo de compra:**
```
POST /api/orders/purchase?userId=1
Body: [1, 2, 3]
```

O endpoint valida estoque, calcula o total e decrementa o estoque de cada livro —
tudo dentro do próprio controller.

---

## Console H2

Acesse **http://localhost:8080/h2-console**

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:libriflow` |
| Username | `sa` |
| Password | *(em branco)* |

---

## O que seria necessário para extrair microsserviços

Por causa dos anti-padrões acima, separar este monólito exigiria:

1. **Criar DTOs** para desacoplar o contrato da API das entidades JPA
2. **Remover `List<Book>` de `Order`** — substituir por uma lista de IDs ou `OrderItem` com valor snapshot
3. **Mover a lógica de `OrderController`** para um `CheckoutService` próprio
4. **Eliminar `@ManyToMany` entre `Order` e `Book`** — a tabela `order_books` cria FK rígida entre os dois domínios
5. **Introduzir comunicação assíncrona** (evento ou HTTP) entre o serviço de catálogo e o de pedidos
6. **Separar os bancos** — hoje as FKs entre `orders`, `users` e `books` impedem bancos distintos
