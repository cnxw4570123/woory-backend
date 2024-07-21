#!/usr/bin/env bash

REPOSITORY=/home/ubuntu/woory-backend
LOG_FILE=$REPOSITORY/log.txt

echo "deploy-woory.sh 시작 " | sudo tee -a $LOG_FILE

cd $REPOSITORY || echo "repository 없음 $REPOSITORY" | sudo tee -a $LOG_FILE
echo "현재 디렉토리: $REPOSITORY" | sudo tee -a $LOG_FILE

APP_NAME=Woory
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep '.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

CURRENT_PID=$(pgrep -f $APP_NAME)

# 서비스 종료
if [ -z "$CURRENT_PID"]
then
  echo "실행중인 woory서비스 없음." | sudo tee -a $LOG_FILE
else
  echo "kill -9 $CURRENT_PID" | sudo tee -a $LOG_FILE
  sleep 5
fi

# 실행 및 로그 저장
nohup sudo java -jar -Dspring.profiles.active=dev -Dapp.name=$APP_NAME "$JAR_PATH" > jarExcuete.log 2>&1 < /dev/null &

# 실행된 프로세스ID 확인
RUNNING_PROCESS=$(ps aux | grep java | grep "$JAR_NAME")
if [ -z "$RUNNING_PROCESS"]
then
  echo "애플리케이션 프로세스 실행 X" | sudo tee -a $LOG_FILE
else
  echo "어플리케이션 프로세스 확인: $RUNNING_PROCESS" | sudo tee -a $LOG_FILE
fi
