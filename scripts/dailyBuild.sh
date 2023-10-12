#!/bin/bash
while getopts t:d:b:u: flag;
do
    case "${flag}" in
        t) DATE="${OPTARG}";;
        d) DRIVER="${OPTARG}";;
        b) BUILD="${OPTARG}";;
        u) DOCKER_USERNAME="${OPTARG}";;
        *) echo "Invalid option";;
    esac
done

echo "Testing daily OpenLiberty image"

sed -i "\#<artifactId>liberty-maven-plugin</artifactId>#a<configuration><install><runtimeUrl>https://public.dhe.ibm.com/ibmdl/export/pub/software/openliberty/runtime/nightly/$DATE/$DRIVER</runtimeUrl></install></configuration>" inventory/pom.xml system/pom.xml
cat inventory/pom.xml system/pom.xml

sed -i "s;FROM icr.io/appcafe/open-liberty:kernel-slim-java11-openj9-ubi;FROM $DOCKER_USERNAME/olguides:$BUILD;g" system/Dockerfile inventory/Dockerfile
sed -i "s;RUN features.sh;;g" system/Dockerfile inventory/Dockerfile
cat system/Dockerfile inventory/Dockerfile

sudo -u runner ../scripts/testApp.sh

# #!/bin/bash
# while getopts t:d:b:u: flag;
# do
#     case "${flag}" in
#         t) DATE="${OPTARG}";;
#         d) DRIVER="${OPTARG}";;
#         b) BUILD="${OPTARG}";;
#         u) PODMAN_USERNAME="${OPTARG}";;
#         *) echo "Invalid option";;
#     esac
# done

# echo "Testing daily OpenLiberty image"

# sed -i "\#<artifactId>liberty-maven-plugin</artifactId>#a<configuration><install><runtimeUrl>https://public.dhe.ibm.com/ibmdl/export/pub/software/openliberty/runtime/nightly/$DATE/$DRIVER</runtimeUrl></install></configuration>"  system/pom.xml
# cat  system/pom.xml

# sed -i "s;FROM icr.io/appcafe/open-liberty:kernel-slim-java11-openj9-ubi;FROM $PODMAN_USERNAME/olguides:$BUILD;g" system/Containerfile 
# sed -i "s;RUN features.sh;;g" system/Containerfile 
# cat system/Containerfile 

# sudo -u runner ../scripts/testApp.sh
