# Kubernetes

## Commands

### Set Google Cloud Project

    gcloud config set project microservices
    gcloud auth application-default login

### Creating Google Cloud Kubernetes Cluster:

    gcloud container clusters create 
        --machine-type n1-standard-2 
        --num-nodes 2 
        --zone europe-west6-a 
        --cluster-version latest 
        --enable-autorepair 
        --enable-autoupgrade 
        --enable-autoupgrade 
        --enable-autorepair 
        --max-surge-upgrade 1 
        --max-unavailable-upgrade 0 
        --enable-vertical-pod-autoscaling 
        --enable-shielded-nodes 
        --node-locations "europe-west6-a"
        microservices-eu-west

### Create Cert Manager

    kubectl apply -f https://github.com/jetstack/cert-manager/releases/download/v1.2.0/cert-manager.crds.yaml

    helm repo add jetstack https://charts.jetstack.io

    helm repo update

    helm install 
        cert-manager jetstack/cert-manager 
        --namespace cert-manager 
        --create-namespace 
        --version v1.2.0

    kubectl get pods --namespace cert-manager

### Create an Public IP Address:

    gcloud compute addresses create microservices-static-ip --global
    gcloud compute addresses describe microservices-static-ip --global
    gcloud compute addresses delete microservices-static-ip --global

Public IP's to create:

    gcloud compute addresses create keycloak-ip admin-ip javadoc-ip --global

### Delete everything in Namespace

    kubectl delete all --all -n {namespace}
