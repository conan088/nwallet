#!/bin/sh

#echo "git---> update git code :--> git clone https://18600236146:Yqp515623...@gitee.com/yang_qinjian/server.git"
#  sh /home/server/start-im-platform.sh
cd /home/server/
svn update
chmod 777 *.sh
/data/maven/maven3/bin/mvn clean install

cd /home/server/im-platform
cp  -f target/im-platform.jar /home/im-platform.jar
pkill -f im-platform.jar
sleep 1 
echo "启动服务"
nohup java -jar -Xms512m -Xmx1024m -XX:PermSize=128M -XX:MaxPermSize=256M /home/im-platform.jar  >/dev/null 2>&1 &

echo "打印日志..."
echo "完成"
sleep 3 && tail -fn500 /data/logs/im-platform/info.log


