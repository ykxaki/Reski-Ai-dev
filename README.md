# ✦ Reski AI

Reski AI é uma plataforma que recomenda trilhas de estudo baseado no seu objetivo de carreira. O usuário informa  objetivo profissional; a plataforma gera trilhas de aprendizado e competências, e acompanha progresso. <br/>
Processo completo de deploy automatizado (CI/CD), utilizando Java 17 e PostgreSQL. </br>
Inclui os scripts de criação da infraestrutura, configuração do ambiente e validação da aplicação.

## ⚙️ Pré-requisitos

- Conta Microsoft Azure.
- Java 17
- Azure CLI configurado (ou Cloud Shell no portal).
- Scripts Azure CLI para criação da infraestrutura (presente no repositório em _Reski_Ai_dev/scripts/_).

---

## 1. Clonar o Repositório
```bash
git clone https://github.com/ykxtais/Reski-Ai-dev.git
cd Reski-Ai-dev
```

## 2. Provisionando a Infraestrutura no Azure
- Scripts de Provisionamento
  - `db-reski.sh`
  - `app-reski.sh`

## 3. Permissão e execução dos scripts
```bash
chmod +x db-reski.sh app-reski.sh
```

No Cloud Shell (Bash), execute o script `db-reski.sh` incluso no repositório: <br/>
Substitua <SENHA_FORTE> por uma senha real.

```bash
#!/usr/bin/env bash
set -e

LOCATION="brazilsouth"
RG_NAME="rg-reski"

PG_SERVER_NAME="pg-reski-srv"
PG_ADMIN_USER="reskiadmin"
PG_ADMIN_PASSWORD="SENHA_FORTE"
PG_DB_NAME="db-reski"

echo "Criando Resource Group"
az group create \
  --name "${RG_NAME}" \
  --location "${LOCATION}"

echo "Criando PostgreSQL Flexible Server"
az postgres flexible-server create \
  --resource-group "${RG_NAME}" \
  --name "${PG_SERVER_NAME}" \
  --admin-user "${PG_ADMIN_USER}" \
  --admin-password "${PG_ADMIN_PASSWORD}" \
  --sku-name Standard_B1ms \
  --tier Burstable \
  --version 16 \
  --storage-size 32 \
  --location "${LOCATION}" \
  --public-access 0.0.0.0-0.0.0.0

echo "Criando banco"
az postgres flexible-server db create \
  --resource-group "${RG_NAME}" \
  --server-name "${PG_SERVER_NAME}" \
  --database-name "${PG_DB_NAME}"

echo "Infra de banco criada"
```

Esse script cria/configura:
- Resource Group
- Servidor PostgreSQL
- Banco de dados
- Regras de firewall para acesso
- Admin do servidor

<br/>

No Cloud Shell (Bash), execute o script `app-reski.sh` incluso no repositório: <br/>
Substitua <MESMA_SENHA_DO_DB> pela mesma senha usada em `db-reski.sh`.

```bash
#!/usr/bin/env bash
set -e

LOCATION="brazilsouth"
RG_NAME="rg-reski"

PG_SERVER_NAME="pg-reski-srv"
PG_ADMIN_USER="reskiadmin"
PG_ADMIN_PASSWORD="<MESMA_SENHA_DO_DB>"
PG_DB_NAME="db-reski"

APP_PLAN_NAME="asp-reski"
APP_NAME="app-reski-api"

echo "Criando App Service Plan"
az appservice plan create \
  --name "${APP_PLAN_NAME}" \
  --resource-group "${RG_NAME}" \
  --sku B1 \
  --is-linux

echo "Criando Web App."
az webapp create \
  --resource-group "${RG_NAME}" \
  --plan "${APP_PLAN_NAME}" \
  --name "${APP_NAME}" \
  --runtime "JAVA:17-java17"

PG_FQDN="${PG_SERVER_NAME}.postgres.database.azure.com"
SPRING_DATASOURCE_URL="jdbc:postgresql://${PG_FQDN}:5432/${PG_DB_NAME}?sslmode=require"
SPRING_DATASOURCE_USERNAME="${PG_ADMIN_USER}"

echo "Configurando App Settings do Web App"
az webapp config appsettings set \
  --resource-group "${RG_NAME}" \
  --name "${APP_NAME}" \
  --settings \
    "SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}" \
    "SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}" \
    "SPRING_DATASOURCE_PASSWORD=${PG_ADMIN_PASSWORD}" \
    "JAVA_OPTS=-Xms256m -Xmx512m"

echo "Web App configurado"
```
- Observação: o CLI não instala psql; aqui assumimos que você roda no Cloud Shell com psql disponível ou em ambiente onde o psql está instalado.

Esse script cria/configura:
- App Service Plan
- Web App
- Connection string

---

## 4. Testando a API
- Acesse o Swagger em: ``` https://app-reski-api.azurewebsites.net/swagger-ui/index.html ``` </br>
- azurewebsites: ``` https://app-reski-api.azurewebsites.net/v3/api-docs ``` </br>
  - endpoints utilizados do projeto: api-trilhas, api-objetivos e chat-ia-controller

---

## 💾 Testes (Swagger)

### POST Objetivo - `/objetivos`
```json
{
  "cargo": "Perito forense digital",
  "area": "Cibersegurança",
  "descricao": "Investiga incidentes de segurança, recupera e analisa dados digitais para reconstruir eventos e apresentar evidências. ",
  "demanda": "Alta e crescente"
}
```

### PUT Objetivo - `/objetivos/{id}`
```json
{
  "cargo": "Red Team",
  "area": "Cibersegurança",
  "descricao": "Profissionais que atuam de forma ofensiva, simulando adversários reais para testar a defesa da empresa. ",
  "demanda": "Alta e crescente"
}
```
---

### POST Trilha - `/trilhas`
```json
{
  "status": "estudando",
  "conteudo": "Direito Penal e Processual Penal",
  "competencia": "Direito Penal"
}
```

### PUT Trilha - `/trilhas/{id}`
```json
{
  "status": "finalizado",
  "conteudo": "Direito Penal e Processual Penal",
  "competencia": "Direito Penal"
}
```
---

### POST IA - `/chat`
```json
{
  "mensagem": "gere uma trilha de estudos para eu começar a estudar para ser cientista de dados"
}
```


# ✦ Integrantes

- Iris Tavares Alves — 557728 </br>
- Taís Tavares Alves — 557553 
