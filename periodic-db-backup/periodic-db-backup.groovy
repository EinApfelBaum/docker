def userInput;
pipeline {
  agent any
  environment {
    imageName = "periodic-backup"
    tagName = "periodic-db-backup"
  }
  stages {
    stage("Interactive Input") {
      steps {
        script {
          userInput = input(
            id: 'userInput', message: 'Enter remote registry:', 
            parameters: [
            [$class: 'TextParameterDefinition', defaultValue: 'None', description: 'Remote address of registry.', name: 'Config']
          ])
        }
      }
    }
    stage('Prepare') {
      parallel {
        stage("Info") {
          steps {
            sh script: "docker --version", label: "Docker Version"
            sh script: "git --version", label: "GitVersion"

            echo "Current dir: ${pwd()}"
            sh script: "ls -la"
            sh script: "env", label: "Build Enviroment"
          }
        }  
        stage("Docker images") {
          steps{
            sh script: "curl http://${userInput}/v2/_catalog", label: "Remote images"
            sh script: "docker images", label: "Local images"
          }
        }
      }        
    }
    stage('Build image') {
       steps {
         sh script: "docker build -t ${userInput}/${imageName} ${tagName}/"
       }
    }
    stage("Push to registry") {
      steps{        
        echo ("Remote: ${userInput}") 
        //sh "docker tag ${imageName} ${userInput}/${imageName}:${BRANCH_NAME}"
        sh "docker push ${userInput}/${imageName}"
      }
    }
    stage('CleanUp') {
      steps {
        sh script: "docker kill \$(docker ps -q) || true", label: "Stop all containers"
        sh script: "docker rm \$(docker ps -a -q) || true", label: "Delete all containers"

        sh script: "docker images | grep ${imageName} | awk '{print \$3 }' | sort -u", label: "Show related docker images"
        sh script: "docker images | grep ${imageName} | awk '{print \$3 }' | sort -u | xargs docker rmi --force || true", label: "Delete related docker images"
        
        sh script: "docker images -q -f dangling=true", label: "Show dangling docker images"
        sh script: "docker rmi \$(docker images -q -f dangling=true) --force || true", label: "Delete dangling docker images"
      }
    }  
  }     
}
