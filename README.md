# Cara Kerja

### Doc
* [Official docker for kafka] (https://github.com/confluentinc/cp-all-in-one/blob/7.2.1-post/cp-all-in-one-community/docker-compose.yml)
* [Official docker for kafdrop] (https://github.com/obsidiandynamics/kafdrop)

## Kafka
* change directory to /docker
* run "docker-compose up" command
* to check all containers is running, run "docker ps" command
* install or run kafkacat by docker, run "docker run -it --network=host confluentinc/cp-kafkacat kafkacat -L -b localhost:19092" command
* run kafdrop for monitoring kafka cluster on localhost:9000