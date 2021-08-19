# Spring Cloud Data Flow

## Deploy RabbitMQ

    kubectl create -f rabbitmq/

### clean up afterwards

    kubectl delete all -l app=rabbitmq

## Deploy MySQL

    kubectl create -f mysql/

### clean up afterwards

    kubectl delete all,pvc,secrets -l app=mysql

## Create Data Flow Role Bindings and Service Account

    kubectl create -f src/kubernetes/server/server-roles.yaml
    kubectl create -f src/kubernetes/server/server-rolebinding.yaml
    kubectl create -f src/kubernetes/server/service-account.yaml

### clean up afterwards

    kubectl delete role scdf-role
    kubectl delete rolebinding scdf-rb
    kubectl delete serviceaccount scdf-sa

## Deploy Skipper

    kubectl create -f skipper/skipper-config-rabbit.yaml
    kubectl create -f skipper/skipper-deployment.yaml
    kubectl create -f skipper/skipper-svc.yaml

### clean up afterwards

    kubectl delete all,cm -l app=skipper

## Deploy Data Flow Server

    kubectl create -f src/kubernetes/server/server-config.yaml
    kubectl create -f src/kubernetes/server/server-svc.yaml
    kubectl create -f src/kubernetes/server/server-deployment.yaml

### clean up afterwards

    kubectl delete all,cm -l app=scdf-server
