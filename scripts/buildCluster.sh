#!/bin/bash
account= aws ecr describe-repositories --repository instantonaws/system --query 'repositories[0].registryId'
echo $account

uri= aws ecr describe-repositories --repository instantonaws/system --query 'repositories[0].repositoryUri' 
echo $uri

region= aws configure get region
echo $region

eksctl create cluster --name=guide-cluster --nodes=1 --node-type=t2.small
aws ecr get-login-password | podman login --username AWS --password-stdin account.dkr.ecr.region.amazonaws.com
aws ecr create-repository --repository-name instantonaws/system
podman tag dev.local/system:1.0-SNAPSHOT uri:1.0-SNAPSHOT
podman push uri:1.0-SNAPSHOT
