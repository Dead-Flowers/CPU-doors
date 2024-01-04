#!/bin/bash
set -e

cd /app
python manage.py migrate
python manage.py collectstatic --noinput
python manage.py runserver 0.0.0.0:8000
#uvicorn doors.asgi:application --lifespan=off --port 8000 --host 0.0.0.0 "$@"