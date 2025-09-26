#!/bin/bash

# 支付服务启动脚本

# 设置Java环境变量（如果需要）
# export JAVA_HOME=/path/to/java
# export PATH=$JAVA_HOME/bin:$PATH

# 设置应用名称
APP_NAME="sifang-payment"

# 设置JVM参数
JVM_OPTS="-Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -XX:+UseG1GC"

# 检查jar包是否存在
if [ ! -f "target/${APP_NAME}.jar" ]; then
    echo "错误: ${APP_NAME}.jar 文件不存在，请先构建项目"
    echo "请执行: mvn clean package"
    exit 1
fi

# 启动应用
echo "正在启动 ${APP_NAME} 服务..."
nohup java $JVM_OPTS -jar target/${APP_NAME}.jar > nohup.out 2>&1 &

# 输出启动信息
echo "应用已启动，进程ID: $!"
echo "日志文件: nohup.out"
echo "可以通过以下命令查看日志: tail -f nohup.out"