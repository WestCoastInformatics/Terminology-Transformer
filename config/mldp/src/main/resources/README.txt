SETUP 

cd
mkdir mldp
cd mldp
mkdir config data code1 code2

# term server
cd ../code2
git clone https://github.com/WestCoastInformatics/UMLS-Terminology-Server.git .
git checkout develop
mvn clean install

# transformer
cd code1
git clone https://github.com/WestCoastInformatics/Terminology-Transformer.git .
git checkout mldp
mvn clean install

# config
cd ~/mldp
unzip code1/config/mldp/target/tt-config-mldp-*.zip -d config

# create db
echo "CREATE database mldpdb CHARACTER SET utf8 default collate utf8_unicode_ci;" | mysql

# integration-test "reset"
cd /home/ec2-tomcat/mldp/code1/integration-test
mvn clean install -Preset -DskipTests=false -Drun.config.umls=/home/ec2-tomcat/mldp/config/config.properties -Dmaven.home=/project/maven-current/bin/mvn



REDEPLOY INSTRUCTIONS

cd ~/mldp/code2
git pull
mvn clean install

/bin/rm -rf /var/lib/tomcat8/work/Catalina/localhost/mldp-server-rest
/bin/rm -rf /var/lib/tomcat8/webapps/mldp-server-rest
/bin/rm -rf /var/lib/tomcat8/webapps/mldp-server-rest.war
/bin/cp -f ~/umls/code/rest/target/mldp-server-rest*war /var/lib/tomcat8/webapps/mldp-server-rest.war

