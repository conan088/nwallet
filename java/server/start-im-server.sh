#!/bin/sh

#echo "git---> update git code :--> git clone https://18600236146:Yqp515623...@gitee.com/yang_qinjian/server.git"
#  sh /home/server/start-im-server.sh
# https://github.com/bluexsx/server?tab=readme-ov-file
cd /home/server/

svn update
/data/maven/maven3/bin/mvn clean install

chmod 777 *.sh

pkill -f im-server.jar
cd /home/server/im-server
cp  -f target/im-server.jar /home/im-server.jar

echo "启动服务"
nohup java -jar -Xms512m -Xmx1024m -XX:PermSize=128M -XX:MaxPermSize=256M /home/im-server.jar  >/dev/null 2>&1 &

echo "打印日志..."
echo "完成"
sleep 3 && tail -fn500 /data/logs/im-server/info.log


