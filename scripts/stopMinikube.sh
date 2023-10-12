#!/bin/bash
kubectl delete -f kubernetes.yaml
eval $(minikube docker-env -u)
minikube stop
minikube delete

# #!/bin/bash
# kubectl delete -f deploy.yaml
# eval $(minikube docker-env -u)
# minikube stop
# minikube delete
