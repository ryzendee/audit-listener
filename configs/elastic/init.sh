#!/bin/sh

ES_HOSTNAME=$(hostname)
ES_PORT=9200
ES_URL="http://$ES_HOSTNAME:$ES_PORT"

until curl -s "$ES_URL" >/dev/null; do
  echo "Ожидание... $ES_URL"
  sleep 2
done

echo "Отладка - создание индекса для методов"
curl -X PUT "$ES_URL/audit-methods" -H 'Content-Type: application/json' -d @/mappings/audit-methods.json

echo "Отладка - создание индекса для http"
curl -X PUT "$ES_URL/audit-http" -H 'Content-Type: application/json' -d @/mappings/audit-http.json

echo "Индксы созданы"
