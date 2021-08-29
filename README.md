# Promethues CSV Exporter

This code allow user to query to prometheus API in openshift and translate the json message to CSV format.


## Run locally using docker

```bash
oc login

TOKEN=$(oc whoami --show-token)
API_ADDR=$(oc get route prometheus-k8s -n  openshift-monitoring --no-headers |  awk '{print "https://"$2}')

docker run -p 8080:8080 -e TOKEN=$TOKEN -e API_ADDR=$API_ADDR quay.io/kahlai/prom-exporter:v4
```



## Deploy to Openshift (Option 1)

```bash
oc login

oc new-app quay.io/kahlai/prom-exporter:v4
oc expose service/prom-exporter

NAMESPACE=<project namespace you going to deploy to>
SERVICE_ACCOUNT=prometheus-test

oc create sa $SERVICE_ACCOUNT

oc create clusterrolebinding $SERVICE_ACCOUNT --clusterrole prometheus-k8s --serviceaccount $NAMESPACE:$SERVICE_ACCOUNT

oc patch deploy prom-exporter -p "{\"spec\" : {\"template\": {\"spec\" : {\"serviceAccount\" : \"$SERVICE_ACCOUNT\"}}}}"
```



## Deploy to Openshift (Option 2)

```bash
oc login

TOKEN=$(oc whoami --show-token)

oc new-app quay.io/kahlai/prom-exporter:v4 -e TOKEN=$TOKEN
oc expose service/prom-exporter
```

Note : Token will expire in option2

# Development

Launch in Codeready Workspace
https://workspaces.openshift.com/f?url=https://github.com/kahlai/prom_exporter.git

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
mvn clean package -Pnative -Dquarkus.native.container-build=true -Dquarkus.container-image.build=true
```



