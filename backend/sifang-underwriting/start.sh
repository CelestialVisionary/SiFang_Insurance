#!/bin/bash

# 核保服务启动脚本

# 设置Java环境变量
JAVA_HOME=/usr/local/java
PATH=$JAVA_HOME/bin:$PATH
export JAVA_HOME PATH

# 服务配置
APP_NAME=sifang-underwriting
APP_PORT=8005
JAR_NAME=${APP_NAME}.jar

# 检查JAR包是否存在
if [ ! -f "${JAR_NAME}" ]; then
    echo "错误: ${JAR_NAME} 文件不存在！"
    echo "请先执行 mvn clean package 命令构建项目。"
    exit 1
fi

# 检查服务是否已启动
PID=$(ps -ef | grep ${JAR_NAME} | grep -v grep | awk '{print $2}')
if [ -n "${PID}" ]; then
    echo "警告: ${APP_NAME} 服务已经在运行，进程ID: ${PID}"
    read -p "是否停止当前运行的服务并重新启动？(y/n): " choice
    if [ "${choice}" != "y" ] && [ "${choice}" != "Y" ]; then
        echo "启动已取消。"
        exit 0
    fi
    echo "正在停止服务..."
    kill -15 ${PID}
    sleep 5
fi

# 设置JVM参数
JVM_OPTS="-Xms512m -Xmx512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 启动命令
echo "正在启动 ${APP_NAME} 服务..."
nohup java ${JVM_OPTS} -jar ${JAR_NAME} --server.port=${APP_PORT} --spring.profiles.active=prod > ${APP_NAME}.log 2>&1 &

# 检查启动状态
sleep 3
PID=$(ps -ef | grep ${JAR_NAME} | grep -v grep | awk '{print $2}')
if [ -n "${PID}" ]; then
    echo "${APP_NAME} 服务启动成功！进程ID: ${PID}"
    echo "日志文件: ${APP_NAME}.log"
    echo "访问地址: http://localhost:${APP_PORT}/api"
else
    echo "错误: ${APP_NAME} 服务启动失败！"
    echo "请查看日志文件: ${APP_NAME}.log"
    exit 1
fi

# 查看启动日志
echo "\n=== 启动日志 ==="
tail -n 20 ${APP_NAME}.log