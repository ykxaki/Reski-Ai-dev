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
