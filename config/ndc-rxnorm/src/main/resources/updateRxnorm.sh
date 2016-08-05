#!/bin/bash

./curl-uts-download.sh http://download.nlm.nih.gov/umls/kss/rxnorm/RxNorm_full_05022016.zip
mkdir ~/ndc/data/20160502
unzip RxNorm*zip -d ~/ndc/data/20160502 "rrf/*"
cd ~/ndc/code/admin
mvn install -PNdcRxnormUpdate -Drun.config.umls=/home/ec2-tomcat/ndc/config/config.properties -Dterminology=RXNORM -Dinput.dir=/home/ec2-tom\
cat/ndc/data
