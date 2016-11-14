SEPARATING HKFT

LABS
egrep "\|(adjustment|challenge|component|property|scale|subclass|subsubclass|supersystem|system|timeAspect)\|" concepts.txt > labs/concepts.txt
touch labs/parChd.txt

MEDS:
egrep "\|(bioavailability|brand_name|df_qualifier|form|ingredient|route|unit)\|" concepts.txt > meds/concepts.txt
touch meds/parChd.txt

CONDITIONS
egrep "\|(bodySite|category|cause|condition|courses|laterality|occurrence|position|severity|subcondition)\|" concepts.txt > conditions/concepts.txt
touch conditions/parChd.txt

PROCEDURES
egrep "\|(bodySite|laterality|position|approach|device|procedure|substance|condition)\|" concepts.txt > procedures/concepts.txt
touch procedures/parChd.txt


SETUP 

cd
mkdir mldp
cd mldp
mkdir config data code1 code2

# term server
cd /home/ec2-tomcat/mldp/code2
git clone https://github.com/WestCoastInformatics/UMLS-Terminology-Server.git .
git checkout develop
mvn clean install

# transformer
cd /home/ec2-tomcat/mldp/code1
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

cd /home/ec2-tomcat/mldp/code2
git pull
mvn clean install -Drun.config.label=mldp

/bin/rm -rf /var/lib/tomcat8/work/Catalina/localhost/mldp-server-rest
/bin/rm -rf /var/lib/tomcat8/webapps/mldp-server-rest

/bin/rm -rf /var/lib/tomcat8/webapps/mldp-server-rest.war
/bin/cp -f ~/mldp/code2/rest/target/umls-server-rest*war /var/lib/tomcat8/webapps/mldp-server-rest.war

