#!/bin/bash

# Git Submodule 업데이트

sudo git submodule update --init --recursive

if [ $? -ne 0 ]; then
    echo "Git submodule update failed. Exiting script."
    exit 1
fi

# Gradle 빌드 실행
sudo ./gradlew clean build

# 빌드가 실패하면 스크립트 종료
if [ $? -ne 0 ]; then
  echo "Gradle build failed. Exiting script."
  exit 1
fi

# Docker Compose 실행
sudo docker-compose up -d
