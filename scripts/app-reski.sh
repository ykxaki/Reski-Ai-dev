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
SPRING_DATASOURCE_USERNAME="${PG_ADMIN_USER}@${PG_SERVER_NAME}"

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
