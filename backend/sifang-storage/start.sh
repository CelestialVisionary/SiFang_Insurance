#!/bin/bash

# 设置Java环境变量（如果需要）
# export JAVA_HOME=/path/to/java
# export PATH=$JAVA_HOME/bin:$PATH

# 服务配置
APP_NAME=sifang-storage
APP_PORT=8006
JAR_NAME=$APP_NAME-1.0.0-SNAPSHOT.jar

# 检查JAR文件是否存在
if [ ! -f "$JAR_NAME" ]; then
    echo "错误: $JAR_NAME 文件不存在，请先构建项目"
    exit 1
fi

echo "正在启动 $APP_NAME 服务..."
echo "服务端口: $APP_PORT"

# 启动服务
nohup java -jar \
    -Xms512m -Xmx512m \
    -Dserver.port=$APP_PORT \
    -Dspring.profiles.active=prod \
    $JAR_NAME > $APP_NAME.log 2>&1 &

echo "服务启动中，请稍候..."
echo "查看日志: tail -f $APP_NAME.log"
echo "停止服务: kill $(ps -ef | grep $JAR_NAME | grep -v grep | awk '{print $2}')"

# 检查服务是否启动成功
sleep 5
if ps -ef | grep $JAR_NAME | grep -v grep > /dev/null; then
    echo "服务启动成功！"
else
    echo "警告: 服务可能启动失败，请查看日志确认"
fi