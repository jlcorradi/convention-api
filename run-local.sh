#!/bin/bash
mvn clean package -DskipTests && docker-compose up --build --force-recreate