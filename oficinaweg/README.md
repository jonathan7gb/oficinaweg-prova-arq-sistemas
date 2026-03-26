# Oficina WEG - Refatoracao SOLID

Este projeto refatora o fluxo de manutencao da oficina em uma arquitetura orientada a objetos com responsabilidades separadas por camadas (`controller`, `dto`, `model`, `repository`, `service`).

## Objetivo da refatoracao

O monolito original centralizava regras de permissao e fluxo de OS em blocos extensos de `if/else`, tornando manutencao e seguranca fragilizadas.

A nova versao aplica regras de negocio de forma explicita em servicos e valida entradas com DTOs, reduzindo acoplamento e risco de regressao.

## Regras de negocio implementadas

- Somente `Professor` pode abrir OS.
- Somente `Aluno` escalado pode registrar execucao.
- A OS exige rastreabilidade completa antes do encerramento:
  - equipamento
  - defeito relatado
  - materiais com quantidade
  - laudo tecnico
- Somente o professor responsavel pela OS ou um `Coordenador` pode encerrar.
- Encerramento so ocorre quando a OS esta em `AGUARDANDO_APROVACAO`.

## Principios SOLID aplicados

- **S (Single Responsibility Principle)**
  - `OsController`: apenas recebe/retorna HTTP.
  - `OsService`: concentra regras de negocio da OS.
  - `GlobalExceptionHandler`: centraliza tratamento de erros HTTP.
- **O (Open/Closed Principle)**
  - Permissao de encerramento foi estendida com `Coordenador` sem reescrever controller.
- **L (Liskov Substitution Principle)**
  - `Aluno`, `Professor` e `Coordenador` substituem `Usuario` em polimorfismo seguro.
- **I (Interface Segregation Principle)**
  - DTOs separados por caso de uso (`Abrir`, `Executar`, `Encerrar`, `Sinalizar`) evitam contratos inchados.
- **D (Dependency Inversion Principle)**
  - `OsService` depende de abstrações de persistencia (`OsRepository`, `UsuarioRepository`) injetadas pelo Spring.

## Melhorias de seguranca e manutencao

- Validacoes de entrada com Jakarta Validation (`@Valid`, `@NotBlank`, `@NotNull`, `@Size`).
- Erros de dominio padronizados (`BusinessRuleException`, `ResourceNotFoundException`).
- Fluxo de status controlado para evitar transicoes invalidas.
- Testes unitarios para cenarios criticos de permissao e rastreabilidade.

## Estrutura principal

- `src/main/java/com/centroweg/oficinaweg/controller`
- `src/main/java/com/centroweg/oficinaweg/dto`
- `src/main/java/com/centroweg/oficinaweg/model`
- `src/main/java/com/centroweg/oficinaweg/repository`
- `src/main/java/com/centroweg/oficinaweg/service`

## Como rodar

### 1) Compilar e testar

```powershell
cd "C:\Users\jonathan_uber\Documents\BackEnd\oficinaweg\oficinaweg"
.\mvnw.cmd test
```

### 2) Subir aplicacao

```powershell
cd "C:\Users\jonathan_uber\Documents\BackEnd\oficinaweg\oficinaweg"
.\mvnw.cmd spring-boot:run
```

## Endpoints principais

- `POST /api/os/sinalizar`
- `POST /api/os/abrir`
- `PATCH /api/os/executar`
- `PATCH /api/os/encerrar`
- `GET /api/os`


