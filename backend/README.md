# Backend - Retifica Renova

API REST desenvolvida com Spring Boot 3 + Java 17 para o e-commerce Retifica Renova.

## Stack
- **Framework:** Spring Boot 3.3.0
- **Language:** Java 17
- **Database:** PostgreSQL
- **Authentication:** JWT (JSON Web Token)
- **Database Migration:** Flyway
- **Build Tool:** Maven
- **External Integration:** MelhorEnvio (Cálculo de Frete)

## Estrutura do Projeto

```
src/main/java/com/retificarenova/
├── config/                 # Configurações gerais
├── security/              # JWT e autenticação
├── exception/             # Tratamento de exceções
├── util/                  # Utilitários
├── domain/                # Contextos de negócio (DDD)
│   ├── auth/             # Autenticação
│   ├── product/          # Produtos
│   ├── order/            # Pedidos
│   └── shipping/         # Frete
└── integration/          # Integrações externas
    └── melhorenvio/      # API MelhorEnvio

src/main/resources/
├── application.properties          # Configuração padrão
├── application-test.properties     # Configuração de testes
└── db/migration/                   # Scripts Flyway
```

## Como Executar

### Pré-requisitos
- Java 17+
- PostgreSQL 12+
- Maven 3.6+

### Passos

1. **Clone o repositório**
   ```bash
   git clone <repositório>
   cd backend
   ```

2. **Configure o banco de dados**
   ```bash
   createdb retifica_renova
   # Configure as credenciais em src/main/resources/application.properties
   ```

3. **Execute a aplicação**
   ```bash
   ./mvnw spring-boot:run
   ```

   Ou usando Maven diretamente:
   ```bash
   mvn clean spring-boot:run
   ```

4. **Acesse a API**
   ```
   http://localhost:8080/api
   ```

## Configuração de Variáveis de Ambiente

Criar arquivo `.env` na raiz de `/backend`:

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/retifica_renova
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
JWT_SECRET=sua-chave-secreta-aqui
JWT_EXPIRATION=86400000
MELHORENVIO_API_TOKEN=seu-token-aqui
```

## Endpoints Principais

### Autenticação
- `POST /auth/login` - Login
- `POST /auth/register` - Registro

### Produtos
- `GET /products` - Listar produtos
- `GET /products/{id}` - Detalhes do produto

### Pedidos
- `GET /orders` - Listar pedidos do usuário
- `POST /orders` - Criar pedido
- `GET /orders/{id}` - Detalhes do pedido

### Frete
- `POST /shipping/calculate` - Calcular frete

## Migrations (Flyway)

Scripts SQL estão em `src/main/resources/db/migration/`:
- `V1__initial_schema.sql` - Schema inicial

## TODO

- [ ] Implementar autenticação JWT
- [ ] Implementar autorização (roles/permissions)
- [ ] Implementar entidades JPA
- [ ] Implementar lógica de negócio dos serviços
- [ ] Integração com MelhorEnvio
- [ ] Testes unitários
- [ ] Documentação Swagger
- [ ] Docker setup

## Licença

Este projeto é licenciado sob a MIT License.
