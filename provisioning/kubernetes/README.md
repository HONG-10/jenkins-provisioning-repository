# Kubernetes Manifests for Jenkins Deployment

Refer https://devopscube.com/setup-jenkins-on-kubernetes-cluster/ for step by step process to use these manifests.


-------------------------


kubectl create namespace devops-tools

kubectl apply -f serviceAccount.yaml

kubectl create -f volume.yaml

kubectl apply -f deployment.yaml

kubectl get deployments -n devops-tools

kubectl  describe deployments --namespace=devops-tools

kubectl apply -f service.yaml

kubectl get pods --namespace=devops-tools