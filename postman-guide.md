# 📮 EduSpace Auth API - Guia do Postman

## 🚀 Como Importar e Usar

### 1. **Importar Collection e Environment**

#### **Collection:**
1. Abra o Postman
2. Clique em **Import** (ou Ctrl+O)
3. Selecione o arquivo: `EduSpace-Auth-API.postman_collection.json`
4. Clique **Import**

#### **Environment:**
1. No Postman, vá em **Environments** (ícone de engrenagem)
2. Clique **Import**
3. Selecione o arquivo: `EduSpace-Auth-Environment.postman_environment.json`
4. Clique **Import**
5. Selecione o environment **"EduSpace Auth Environment"** no dropdown

### 2. **Configuração Inicial**

#### **Verificar URLs:**
- `baseUrl`: `http://localhost:8080/api/v1/auth`
- `serverUrl`: `http://localhost:8080/api/v1`

#### **Dados de Teste (já configurados):**
- **Teacher**: `teacher@example.com` / `senha123`
- **Student**: `student@example.com` / `senha123`  
- **Blocked**: `blocked@example.com` / `senha123`

### 3. **Executar Testes**

#### **🔄 Sequência Recomendada:**

1. **Login Teacher** → Salva tokens automaticamente
2. **Refresh Token** → Atualiza access token
3. **Protected Endpoint** → Testa autenticação
4. **Logout** → Limpa tokens

#### **🎯 Testes Individuais:**
- Execute qualquer request da pasta **"1. Authentication"**
- Os tokens serão salvos automaticamente nas variáveis da collection
- Use nas próximas requests

### 4. **Recursos da Collection**

#### **✅ Testes Automáticos:**
Cada request tem **Tests** que verificam:
- Status codes corretos
- Presença de headers (tokens)
- Estrutura da resposta JSON
- Validação de dados

#### **🔧 Variáveis Dinâmicas:**
- `{{accessToken}}` - Token JWT atual
- `{{refreshToken}}` - Token de refresh atual  
- `{{teacherEmail}}` - Email do professor
- `{{studentEmail}}` - Email do aluno
- `{{testPassword}}` - Senha padrão

#### **📊 Scripts Automáticos:**
- **Pre-request**: Logs de início do teste
- **Test**: Validações e salvamento de tokens
- **Console Logs**: Feedback detalhado

### 5. **Cenários de Teste Incluídos**

#### **📁 1. Authentication**
- ✅ Login Teacher (Success)
- ✅ Login Student (Success)  
- ❌ Login Invalid Credentials
- ❌ Login Blocked User
- ❌ Login Invalid Data

#### **📁 2. Token Refresh**
- ✅ Refresh Token (Success)
- ❌ Refresh Token Invalid
- ❌ Refresh Token Expired

#### **📁 3. Logout**
- ✅ Logout (Success)
- ⚠️ Logout Invalid Token (sempre 204)

#### **📁 4. Protected Endpoints**
- 🔐 Com X-Access-Token header
- 🔐 Com Authorization Bearer header  
- ❌ Sem token (Unauthorized)

#### **📁 5. Full Flow Test**
- 🔄 Teste de fluxo completo

### 6. **Como Interpretar Resultados**

#### **✅ Sucesso:**
```
✅ Status code is 200
✅ Response has message
✅ Headers contain tokens
✅ Access Token salvo: eyJhbGciOiJIUzI1NiIs...
```

#### **❌ Erro:**
```
❌ Status code is 400
✅ Response contains error message
✅ No tokens in headers
```

#### **📊 Console Logs:**
```
🚀 Executando teste: Login Teacher (Success)
✅ Resposta recebida: 200 OK
⏱️ Tempo de resposta: 245ms
✅ Access Token salvo: eyJhbGciOiJIUzI1NiIs...
```

### 7. **Troubleshooting**

#### **🔧 Servidor não responde:**
- Verifique se a aplicação Spring Boot está rodando
- Confirme a URL: `http://localhost:8080`
- Check se o context-path está correto: `/api/v1`

#### **❌ Testes falhando:**
- Verifique se os usuários de teste estão no banco
- Confirme se as senhas estão com hash BCrypt correto
- Check se não há usuários bloqueados indevidamente

#### **🔒 Tokens não salvam:**
- Verifique se o environment está selecionado
- Check se os headers estão sendo retornados pelo servidor
- Confira o console do Postman para logs

#### **🍪 Cookies não funcionam:**
- Cookies são gerenciados automaticamente pelo Postman
- Para testes manuais, use os tokens salvos nas variáveis

### 8. **Comandos SQL para Dados de Teste**

```sql
-- Criar usuários de teste (execute no banco)
INSERT INTO study.users (name, email, password_hash, role, status, timezone) VALUES
('Professor João', 'teacher@example.com', '$2a$10$8K1p/wgy6T.VYa2dBGRu9.sO3BHvT1s8N1.GsxNC1DgJWwC.j6He.', 'TEACHER', 'ACTIVE', 'America/Sao_Paulo'),
('Aluno Maria', 'student@example.com', '$2a$10$8K1p/wgy6T.VYa2dBGRu9.sO3BHvT1s8N1.GsxNC1DgJWwC.j6He.', 'STUDENT', 'ACTIVE', 'America/Sao_Paulo'),
('Usuário Bloqueado', 'blocked@example.com', '$2a$10$8K1p/wgy6T.VYa2dBGRu9.sO3BHvT1s8N1.GsxNC1DgJWwC.j6He.', 'STUDENT', 'BLOCKED', 'America/Sao_Paulo');
```

### 9. **Executar Toda a Collection**

#### **🏃‍♂️ Collection Runner:**
1. Clique na collection **"EduSpace Auth API"**
2. Clique **Run collection**
3. Selecione todos os requests ou apenas os desejados
4. Clique **Run EduSpace Auth API**
5. Veja os resultados em tempo real

#### **⚡ Execução em Lote:**
```bash
# Usando Newman (CLI do Postman)
npm install -g newman
newman run EduSpace-Auth-API.postman_collection.json -e EduSpace-Auth-Environment.postman_environment.json --reporters cli,html
```

### 10. **Próximos Passos**

#### **🔧 Personalizar:**
- Adicione novos cenários de teste
- Modifique URLs para outros environments (dev, staging)
- Adicione novos endpoints protegidos

#### **🚀 CI/CD:**
- Use Newman para execução em pipelines
- Configure testes automáticos no GitHub Actions
- Gere relatórios HTML de execução

---

## 📋 **Resumo dos Arquivos:**

- `EduSpace-Auth-API.postman_collection.json` - Collection principal
- `EduSpace-Auth-Environment.postman_environment.json` - Environment de teste
- `postman-guide.md` - Este guia de instruções

**🎯 A collection está pronta para uso! Importe no Postman e comece os testes imediatamente.** 🚀
