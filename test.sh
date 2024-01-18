#!/bin/bash

script="test.js"

# 사용자에게 가상 사용자 수와 테스트 기간 입력 받기
read -r -p "Enter the number of virtual users (vuser): " vuser
read -r -p "Enter the test duration (e.g., 30s, 5m): " duration

# k6 실행 명령어
base_url="http://localhost"
k6_command="k6 run -e BASE_URL=$base_url -u $vuser -d $duration $script"

# k6 실행
echo "Running $script with $vuser virtual users for $duration..."
eval "$k6_command"
