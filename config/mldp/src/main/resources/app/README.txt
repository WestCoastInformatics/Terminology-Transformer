PROD NDC Transformer Setup

1. Use provided starting point (mysql, Tomcat, java, mvn, emacs, etc. installed)

2. Edit /etc/default/tomcat8 - set memory and -Drefset.config in JAVA_OPTS

JAVA_OPTS="... -Drun.config.ndc=/home/ec2-tomcat/ndc/config/config.properties"

3. Follow "DIRECTORY AND PROJECT SETUP" shown below

4. Create the database - N/A - "refset" db already created

CREATE database ndcdb CHARACTER SET utf8 default collate utf8_unicode_ci;

5. Configure MYSQL - ./etc/mysql/my.cnf (restart mysql when finished)

max_allowed_packet = 100M
innodb_file_per_table

6. Download initial prod data and indexes, load prod data

cd ~/ndc/data
wget https://s3.amazonaws.com/wci1/TermServer/ndc-indexes.zip
wget https://s3.amazonaws.com/wci1/TermServer/ndc-sql.zip
unzip ndc-sql.zip
mysql -uotf -p ndcdb < ndc.sql
mysqlndc < ~/fixWindowsExportData.sql

/bin/rm -rf indexes
unzip ndc-indexes.zip

7. nginx configuration

see /etc/nginx/virtual.conf


DIRECTORY AND PROJECT SETUP

1. Create space and pull code

mkdir ~/ndc
cd ~/ndc
mkdir config data
git clone https://github.com/WestCoastInformatics/Terminology-Transformer.git code

2. Build code with proper config

cd ~/ndc/code
git pull
mvn -Dconfig.artifactId=tt-config-ndc-rxnorm clean install

3. Unpack and edit config

cd ~/ndc
unzip ~/ndc/code/config/ndc-rxnorm/target/tt-config*.zip -d config

# edit ~/ndc/config/config.properties
# * javax.persistence.jdbc.url=jdbc:mysql://127.0.0.1:3306/ndcdb?useUnicode=true&characterEncoding=UTF-8&rewriteBatchedStatements=true&useLocalSessionState=true
# * javax.persistence.jdbc.username=otf
# * javax.persistence.jdbc.password=*******
# * hibernate.search.default.indexBase=/home/ec2-tomcat/ndc/data/indexes


REDEPLOY INSTRUCTIONS

# run as ec2-tomcat
cd ~/ndc/code
git pull
mvn -Dconfig.artifactId=tt-config-ndc-rxnorm clean install

/bin/rm -rf /var/lib/tomcat8/work/Catalina/localhost/ndc-tt-rest
/bin/rm -rf /var/lib/tomcat8/webapps/ndc-tt-rest
/bin/rm -rf /var/lib/tomcat8/webapps/ndc-tt-rest.war
/bin/cp -f /home/ec2-tomcat/ndc/code/rest/target/tt-rest*war /var/lib/tomcat8/webapps/ndc-tt-rest.war
y