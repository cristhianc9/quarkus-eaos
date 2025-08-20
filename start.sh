#!/bin/bash
# start.sh - Script para desplegar el servicio Quarkus localmente
# Uso: ./start.sh [ruta/application.properties]

APP_PROPS=${1:-application.properties}

if [ ! -f "$APP_PROPS" ]; then
  echo "Archivo de configuración $APP_PROPS no encontrado."
  exit 1
fi

export QUARKUS_CONFIG_LOCATIONS=$APP_PROPS

./mvnw clean compile quarkus:dev -Dquarkus.http.port=15050 -Dquarkus.config.locations=$APP_PROPS
