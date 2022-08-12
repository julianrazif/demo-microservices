# Cara Kerja

### Doc
* [Official docker for kafka] (https://github.com/confluentinc/cp-all-in-one/blob/7.2.1-post/cp-all-in-one-community/docker-compose.yml)
* [Official docker for kafdrop] (https://github.com/obsidiandynamics/kafdrop)
* [Bitnami docker for redis-sentinel] (https://github.com/bitnami/containers/blob/main/bitnami/redis-sentinel/docker-compose.yml)

## Docker
* create user defined network on docker, run "docker network create --gateway 172.25.0.1 --subnet 172.25.0.0/16 demo" command

## Kafka
* change directory to /docker/kafka-cluster
* run "docker-compose up" command
* to check all containers is running, run "docker ps" command
* install or run kafkacat by docker, run "docker run -it --network=host confluentinc/cp-kafkacat kafkacat -L -b localhost:19092" command
* run kafdrop for monitoring kafka cluster on localhost:9000
* create topic with name "stream_dss-reqreply_api" replicas = 3, partitions = 5
* create topic with name "reqreply_api-stream_dss" replicas = 3, partitions = 5

## Redis Sentinel
* change directory to /docker/redis-sentinel
* run "docker-compose up" command
* to check all containers is running, run "docker ps" command
* install redis client or run "redis-cli -p 6379 -h localhost" command

## Services
* change directory to /docker/application
* run "docker-compose up" command
* to check all containers is running, run "docker ps" command
* run dss-service on localhost:8081
* run api-service on localhost:8082