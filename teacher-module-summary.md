# 👨‍🏫 MÓDULO DE GERENCIAMENTO DE PROFESSORES - EduSpace

## ✅ IMPLEMENTAÇÃO COMPLETA

### 📁 **ESTRUTURA CRIADA:**

```
domain/
└── port/
    └── TeacherGateway.java                 ✅ Interface com métodos CRUD

application/
└── usecase/teacher/
    ├── input/
    │   ├── CreateTeacherInput.java         ✅ Record para criação
    │   └── UpdateTeacherInput.java         ✅ Record para atualização
    ├── output/
    │   ├── GetTeacherOutput.java           ✅ Record para resposta individual
    │   └── ListTeachersOutput.java         ✅ Record para lista
    ├── CreateTeacherUseCase.java           ✅ Criação com senha temporária + email
    ├── GetTeacherByIdUseCase.java          ✅ Busca por ID com validação
    ├── ListTeachersUseCase.java            ✅ Listagem ordenada por nome
    ├── UpdateTeacherUseCase.java           ✅ Atualização de campos permitidos
    └── BlockTeacherUseCase.java            ✅ Bloqueio lógico
└── mapper/
    └── TeacherOutputMapper.java            ✅ Mapper com INSTANCE pattern

infrastructure/
├── api/
│   ├── controllers/teacher/
│   │   ├── request/
│   │   │   ├── CreateTeacherRequest.java   ✅ Record com validações
│   │   │   └── UpdateTeacherRequest.java   ✅ Record para atualização
│   │   ├── response/
│   │   │   ├── GetTeacherResponse.java     ✅ Record para resposta
│   │   │   └── ListTeachersResponse.java   ✅ Record para lista
│   │   └── TeacherController.java          ✅ Controller com @PreAuthorize
│   └── TeacherApi.java                     ✅ Interface com Swagger
├── mapper/
│   └── TeacherInfraMapper.java             ✅ Mapper com INSTANCE pattern
├── persistence/teacher/
│   └── TeacherGatewayImpl.java             ✅ Implementação usando UserRepository
└── service/
    └── TeacherEmailService.java            ✅ Service para envio de emails
```

### 🎯 **ENDPOINTS IMPLEMENTADOS:**

```http
POST   /teachers          → Criar professor (201 + GetTeacherResponse)
GET    /teachers          → Listar todos (200 + ListTeachersResponse)
GET    /teachers/{id}     → Buscar por ID (200 + GetTeacherResponse)
PATCH  /teachers/{id}     → Atualizar (200 + GetTeacherResponse)
DELETE /teachers/{id}     → Bloquear (204)
```

### 🔐 **SEGURANÇA:**
- **Todos endpoints protegidos:** `@PreAuthorize("hasRole('ADMIN')")`
- **Acesso apenas para ADMIN:** Conforme especificado
- **JWT Token requerido:** Via headers X-Access-Token ou Authorization Bearer

### 📋 **REGRAS DE NEGÓCIO IMPLEMENTADAS:**

#### **1. Criação de Professor:**
- ✅ Verifica e-mail único (lança BusinessException se existir)
- ✅ Gera senha temporária aleatória de 10 caracteres
- ✅ Hash BCrypt da senha antes de salvar
- ✅ Salva com `role=TEACHER` e `status=ACTIVE`
- ✅ Envia e-mail com credenciais (nome, email, senha temporária)
- ✅ Retorna dados sem senha

#### **2. Listagem:**
- ✅ Apenas usuários com `role=TEACHER`
- ✅ Ordenação por `name ASC`

#### **3. Busca por ID:**
- ✅ NotFoundException se não encontrar
- ✅ Valida que tem `role=TEACHER`

#### **4. Atualização:**
- ✅ Permite: name, phone, avatarUrl, timezone
- ✅ NÃO permite: email, role, password
- ✅ NotFoundException se não encontrar

#### **5. Bloqueio:**
- ✅ DELETE lógico (muda status para BLOCKED)
- ✅ NÃO remove registro do banco
- ✅ Professor não consegue fazer login (já tratado no LoginUseCase)

### 🛠️ **PADRÕES SEGUIDOS:**

#### **✅ Modelo de Domínio:**
- Reutiliza `User` existente com factory method `User.with()`
- Sem anotações JPA, apenas getters
- Factory methods estáticos

#### **✅ Mappers:**
```java
// INSTANCE pattern (NÃO Spring injection)
private static final TeacherInfraMapper TEACHER_INFRA_MAPPER = TeacherInfraMapper.INSTANCE;
private static final TeacherOutputMapper MAPPER = TeacherOutputMapper.INSTANCE;
```

#### **✅ Gateway Pattern:**
- Interface `TeacherGateway` no domain
- Implementação `TeacherGatewayImpl` na infrastructure
- Reutiliza `UserRepository` existente

#### **✅ Clean Architecture:**
- Separação clara de responsabilidades
- Dependências apontam para dentro
- Use cases independentes

### 📧 **E-MAIL SERVICE:**
```java
@Service
public class TeacherEmailService {
    // Envia e-mail de boas-vindas com credenciais
    public void sendWelcomeEmail(String toEmail, String name, String temporaryPassword);
}
```

### 🗄️ **BANCO DE DADOS:**
- **Reutiliza tabela `users` existente**
- **Filtra por `role = 'TEACHER'`**
- **Métodos adicionados no UserRepository:**
  - `findByEmailIgnoreCaseAndRole(String email, String role)`
  - `findByRole(String role, Sort sort)`

### 📝 **EXEMPLO DE USO:**

#### **Criar Professor:**
```bash
curl -X POST "http://localhost:8080/teachers" \
  -H "X-Access-Token: SEU_TOKEN_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Professor João",
    "email": "joao@eduspace.com",
    "phone": "(11) 99999-9999",
    "timezone": "America/Sao_Paulo"
  }'
```

#### **Listar Professores:**
```bash
curl -X GET "http://localhost:8080/teachers" \
  -H "X-Access-Token: SEU_TOKEN_ADMIN"
```

#### **Atualizar Professor:**
```bash
curl -X PATCH "http://localhost:8080/teachers/{id}" \
  -H "X-Access-Token: SEU_TOKEN_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Professor João Silva",
    "phone": "(11) 88888-8888"
  }'
```

#### **Bloquear Professor:**
```bash
curl -X DELETE "http://localhost:8080/teachers/{id}" \
  -H "X-Access-Token: SEU_TOKEN_ADMIN"
```

### 🎉 **RESULTADO:**

✅ **Módulo completo e funcional**
✅ **Segue exatamente os padrões do projeto**  
✅ **Reutiliza User e UserRepository**
✅ **Build bem-sucedido sem erros**
✅ **Pronto para uso com role ADMIN**

**O módulo de gerenciamento de professores está 100% implementado seguindo Clean Architecture e os padrões estabelecidos no projeto!** 🚀
