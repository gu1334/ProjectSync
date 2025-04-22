# 📁 ProjectSync - API de Gerenciamento de Projetos

A **ProjectSync** é uma API RESTful desenvolvida em Java com Spring Boot para gerenciamento de projetos e equipes, com foco em escalabilidade, segurança e boas práticas de arquitetura. Esta aplicação utiliza **Clean Architecture**, **OAuth2**, mensageria com **Kafka**, documentação com **Swagger**, e é totalmente containerizada com **Docker**.

---

## 🚀 Funcionalidades

- 🔐 Autenticação de usuários (OAuth2)
- 👥 Cadastro e autenticação de usuários
- 🧠 Criação, edição, listagem e exclusão de **projetos**
- 👨‍👩‍👧 Gerenciamento de **equipes**
- 🔁 Transferência de propriedade de projetos
- 📄 Documentação automática com Swagger
- 🧪 Testes unitários e de integração
- 🐳 Docker para ambiente isolado
- ☁️ Infraestrutura como código (Terraform + AWS)
- 📩 Integração com Kafka para mensageria assíncrona

---

## 🧰 Tecnologias Utilizadas

- Java 17
- Spring Boot
- Spring Security + OAuth2
- JWT
- MySQL
- Kafka
- Docker
- Swagger / OpenAPI
- JUnit 5 / Mockito
- Terraform
- AWS (EC2, RDS, S3)
- GitLab CI/CD

---

## 📦 Estrutura do Projeto

```
ProjectSync
├── controller/
│   ├── AuthenticationController.java
│   ├── ProjectController.java
│   └── TeamController.java
├── service/
├── repository/
├── entities/
├── dtos/
├── exceptions/
└── config/
```

---

## ⚙️ Como Executar o Projeto

### Pré-requisitos

- Docker + Docker Compose
- JDK 17+
- Git

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/project-sync-api.git
cd project-sync-api
```

### 2. Suba os containers com Docker

```bash
docker-compose up --build
```

> Isso irá subir o backend, banco de dados e Kafka.

---

## 🔐 Autenticação

Utilizamos OAuth2 com JWT.

- **Signup:** `POST /auth/signup`
- **Login:** `POST /auth/login`

O token retornado deve ser usado no header de autenticação:

```
Authorization: Bearer <seu_token>
```

---

## 📚 Documentação Swagger

Acesse via navegador:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🔗 Endpoints da API

### 🧑‍💼 **Usuário**

#### 🔸 Cadastrar usuário (Signup)

```http
POST /auth/signup
Content-Type: application/json

{
  "name": "Gustavo",
  "email": "gustavo@email.com",
  "password": "senhaSegura123"
}
```

#### 🔸 Autenticação (Login)

```http
POST /auth/login
Content-Type: application/json

{
  "email": "gustavo@email.com",
  "password": "senhaSegura123"
}
```

> Retorna um token JWT.

---

### 📁 **Projetos**

#### 🔸 Criar projeto

```http
POST /projects
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "API Financeira",
  "description": "Controle de finanças pessoais",
  "teamId": 1
}
```

#### 🔸 Buscar todos os projetos

```http
GET /projects?page=0&size=10
Authorization: Bearer <token>
```

#### 🔸 Atualizar projeto

```http
PATCH /projects/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "API Atualizada",
  "description": "Nova descrição do projeto"
}
```

#### 🔸 Deletar projeto

```http
DELETE /projects/{id}
Authorization: Bearer <token>
```

#### 🔸 Transferir propriedade do projeto

```http
PATCH /projects/{projectId}/change-owner?newOwnerId=<uuid>
Authorization: Bearer <token>
```

---

### 👥 **Equipes**

#### 🔸 Criar equipe

```http
POST /teams
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Time Backend"
}
```

#### 🔸 Buscar todas as equipes

```http
GET /teams
Authorization: Bearer <token>
```

#### 🔸 Atualizar equipe

```http
PATCH /teams/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Time Backend Atualizado"
}
```

---

### 💬 **Kafka (Mensageria)**

- A aplicação publica mensagens no Kafka ao criar, editar ou excluir projetos.
- Os tópicos utilizados estão definidos nas configurações da aplicação (`application.yml`).

---

## 🌐 Deploy na AWS

Infraestrutura provisionada com Terraform:
- Banco de dados MySQL (RDS)
- EC2 para backend
- S3 para arquivos e artefatos

> Pipeline automatizado com GitLab CI/CD

---

## 🤝 Contribuições

Sinta-se à vontade para abrir Issues ou Pull Requests!

---

## 🧑‍💻 Autor

**Gustavo**  
Desenvolvedor Backend Júnior em formação 🚀  
Certificações em segurança, apaixonado por tecnologia, automações e boas práticas de desenvolvimento.

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---
