# From file

## Execution EU

```
#export REDIS_HOST=10.240.2.4
export GCP_PROJECT_ID=autosuggest-194816
export GCP_REGION=europe-west1
export GCP_NETWORK=autosuggest-network
export GCP_SUBNETWORK=autosuggest-subnetwork-europe-west1-d
export BUCKET_NAME=autosuggest-bucket
export BEAM_PROJECT=autosuggest-beam-eu
export BEAM_PROJECT_GRP_ID=org.example
export BEAM_PROJECT_PKG=org.example.autosuggest
export BEAM_MAIN_CLASS=${BEAM_PROJECT_PKG}.RelevantWords
export REDIS_HOST=35.190.207.120
export REDIS_PORT=6379

mvn compile exec:java \
      -Dexec.mainClass=${BEAM_MAIN_CLASS} \
      -Dexec.args="--project=${GCP_PROJECT_ID} \
      --stagingLocation=gs://${BUCKET_NAME}/staging/ \
      --gcpTempLocation=gs://${BUCKET_NAME}/temp/ \
      --output=gs://${BUCKET_NAME}/output \
      --runner=DataflowRunner \
      --region=${GCP_REGION} \
      --jobName=${BEAM_PROJECT} \
      --network=${GCP_NETWORK} \
      --subnetwork=regions/${GCP_REGION}/subnetworks/${GCP_SUBNETWORK} \
      --redisHost=${REDIS_HOST} --redisPort=${REDIS_PORT}"

```

Local
```
mvn compile exec:java -Dexec.mainClass=${BEAM_MAIN_CLASS} \
     -Dexec.args="--output=products --redisHost=localhost --redisPort=6379" -Pdirect-runner
```

## Execution US

```
export REDIS_HOST=10.240.2.7
export GCP_PROJECT_ID=autosuggest-194816
export GCP_REGION=us-central1
export GCP_NETWORK=autosuggest-network
export GCP_SUBNETWORK=autosuggest-subnetwork-us-central1-a
export BUCKET_NAME=autosuggest-bucket
export BEAM_PROJECT=autosuggest-beam-us
export BEAM_PROJECT_GRP_ID=org.example
export BEAM_PROJECT_PKG=org.example.autosuggest
export BEAM_MAIN_CLASS=${BEAM_PROJECT_PKG}.RelevantWords
echo export REDIS_HOST=104.198.144.10
export REDIS_PORT=6379

mvn compile exec:java \
      -Dexec.mainClass=${BEAM_MAIN_CLASS} \
      -Dexec.args="--project=${GCP_PROJECT_ID} \
      --stagingLocation=gs://${BUCKET_NAME}/staging/ \
      --gcpTempLocation=gs://${BUCKET_NAME}/temp/ \
      --output=gs://${BUCKET_NAME}/output \
      --runner=DataflowRunner \
      --region=${GCP_REGION} \
      --jobName=${BEAM_PROJECT} \
      --network=${GCP_NETWORK} \
      --subnetwork=regions/${GCP_REGION}/subnetworks/${GCP_SUBNETWORK} \
      --redisHost=${REDIS_HOST} --redisPort=${REDIS_PORT}"
```

Local
```
mvn compile exec:java -Dexec.mainClass=${BEAM_MAIN_CLASS} \
     -Dexec.args="--output=products --redisHost=localhost --redisPort=6379" -Pdirect-runner
```

## Execution JP

```
export REDIS_HOST=10.240.3.5
export GCP_PROJECT_ID=autosuggest-194816
export GCP_REGION=asia-east1
export GCP_NETWORK=autosuggest-network
export GCP_SUBNETWORK=autosuggest-subnetwork-asia-east1-a
export BUCKET_NAME=autosuggest-bucket
export BEAM_PROJECT=autosuggest-beam-jp
export BEAM_PROJECT_GRP_ID=org.example
export BEAM_PROJECT_PKG=org.example.autosuggest
export BEAM_MAIN_CLASS=${BEAM_PROJECT_PKG}.RelevantWords
echo export REDIS_HOST=35.229.234.110
export REDIS_PORT=6379

mvn compile exec:java \
      -Dexec.mainClass=${BEAM_MAIN_CLASS} \
      -Dexec.args="--project=${GCP_PROJECT_ID} \
      --stagingLocation=gs://${BUCKET_NAME}/staging/ \
      --gcpTempLocation=gs://${BUCKET_NAME}/temp/ \
      --output=gs://${BUCKET_NAME}/output \
      --runner=DataflowRunner \
      --region=${GCP_REGION} \
      --jobName=${BEAM_PROJECT} \
      --network=${GCP_NETWORK} \
      --subnetwork=regions/${GCP_REGION}/subnetworks/${GCP_SUBNETWORK} \
      --redisHost=${REDIS_HOST} --redisPort=${REDIS_PORT}"
```

Local
```
mvn compile exec:java -Dexec.mainClass=${BEAM_MAIN_CLASS} \
     -Dexec.args="--output=products --redisHost=localhost --redisPort=6379" -Pdirect-runner
```

# From Pub/Sub

## Execution EU

```
#export REDIS_HOST=10.240.2.4
export GCP_PROJECT_ID=autosuggest-194816
export GCP_REGION=europe-west1
export GCP_NETWORK=autosuggest-network
export GCP_SUBNETWORK=autosuggest-subnetwork-europe-west1-d
export BUCKET_NAME=autosuggest-bucket
export BEAM_PROJECT=autosuggest-beam-streaming-eu
export BEAM_PROJECT_GRP_ID=org.example
export BEAM_PROJECT_PKG=org.example.autosuggest
export BEAM_MAIN_CLASS=${BEAM_PROJECT_PKG}.RelevantWordsStreaming
export REDIS_HOST=35.190.207.120
export REDIS_PORT=6379
export TOPIC=projects/autosuggest-194816/topics/products
export SUBSCRIPTION=projects/autosuggest-194816/subscriptions/dataflow-pipeline-products

mvn compile exec:java \
      -Dexec.mainClass=${BEAM_MAIN_CLASS} \
      -Dexec.args="--project=${GCP_PROJECT_ID} \
      --stagingLocation=gs://${BUCKET_NAME}/staging/ \
      --gcpTempLocation=gs://${BUCKET_NAME}/temp/ \
      --output=gs://${BUCKET_NAME}/output \
      --runner=DataflowRunner \
      --region=${GCP_REGION} \
      --jobName=${BEAM_PROJECT} \
      --network=${GCP_NETWORK} \
      --subnetwork=regions/${GCP_REGION}/subnetworks/${GCP_SUBNETWORK} \
      --redisHost=${REDIS_HOST} --redisPort=${REDIS_PORT} \
      --topic=${TOPIC} --subscription=${SUBSCRIPTION} --streaming"

```

Local
```
mvn compile exec:java -Dexec.mainClass=${BEAM_MAIN_CLASS} \
     -Dexec.args="--output=products --redisHost=localhost --redisPort=6379" -Pdirect-runner
```

## Execution US

```
#export REDIS_HOST=10.240.2.4
export GCP_PROJECT_ID=autosuggest-194816
export GCP_REGION=us-central1
export GCP_NETWORK=autosuggest-network
export GCP_SUBNETWORK=autosuggest-subnetwork-us-central1-a
export BUCKET_NAME=autosuggest-bucket
export BEAM_PROJECT=autosuggest-beam-streaming-us
export BEAM_PROJECT_GRP_ID=org.example
export BEAM_PROJECT_PKG=org.example.autosuggest
export BEAM_MAIN_CLASS=${BEAM_PROJECT_PKG}.RelevantWordsStreaming
export REDIS_HOST=35.226.88.160
export REDIS_PORT=6379
export TOPIC=projects/autosuggest-194816/topics/products
export SUBSCRIPTION=projects/autosuggest-194816/subscriptions/dataflow-pipeline-products

mvn compile exec:java \
      -Dexec.mainClass=${BEAM_MAIN_CLASS} \
      -Dexec.args="--project=${GCP_PROJECT_ID} \
      --stagingLocation=gs://${BUCKET_NAME}/staging/ \
      --gcpTempLocation=gs://${BUCKET_NAME}/temp/ \
      --output=gs://${BUCKET_NAME}/output \
      --runner=DataflowRunner \
      --region=${GCP_REGION} \
      --jobName=${BEAM_PROJECT} \
      --network=${GCP_NETWORK} \
      --subnetwork=regions/${GCP_REGION}/subnetworks/${GCP_SUBNETWORK} \
      --redisHost=${REDIS_HOST} --redisPort=${REDIS_PORT} \
      --topic=${TOPIC} --subscription=${SUBSCRIPTION} --streaming"
```

Local
```
mvn compile exec:java -Dexec.mainClass=${BEAM_MAIN_CLASS} \
     -Dexec.args="--output=products --redisHost=localhost --redisPort=6379" -Pdirect-runner
```