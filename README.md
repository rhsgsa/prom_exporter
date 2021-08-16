# Promethues CSV Exporter

This code allow user to query to prometheus API in openshift and translate the json message to CSV format.

## Run locally using docker

```bash
oc login

TOKEN=$(oc whoami --show-token)
API_ADDR=$(oc get route prometheus-k8s -n  openshift-monitoring --no-headers |  awk '{print "https://"$2}')

docker run -p 8080:8080 -e TOKEN=$TOKEN -e API_ADDR=$API_ADDR quay.io/kahlai/prom-exporter:v2
```

## Running in Openshift

```bash
oc login

TOKEN=$(oc whoami --show-token)

oc new-app quay.io/kahlai/prom-exporter:v2 -e TOKEN=$TOKEN
oc expose service/prom-exporter
```

# Development

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```bash
export TOKEN=$(oc whoami --show-token)
export API_ADDR=$(oc get route prometheus-k8s -n  openshift-monitoring --no-headers |  awk '{print "https://"$2}')
```

```bash
./mvnw compile quarkus:dev
```



## Package the application as docker image

```bash
./mvnw clean package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true
```



