def registry;
pipeline {
  agent any
  environment {
    imageName = "periodic-backup"
    context = "periodic-db-backup"
  }
  stages {
    stage("Interactive Input") {
      steps {
        script {
          registry = input(
            id: 'registry', message: 'Enter remote registry:',
            parameters: [
            [$class: 'TextParameterDefinition', defaultValue: 'dockerRegistry:5000', description: 'Remote address of registry.', name: 'Config']
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
            sh script: "curl http://${registry}/v2/_catalog", label: "Remote images"
            sh script: "docker images", label: "Local images"
          }
        }
      }
    }
    stage('Build image') {
       steps {
         sh script: "docker build -t ${imageName} ${context}/"
       }
    }
    stage("Push to registry") {
      when {
        branch 'master'
      }
      steps {
        echo ("Remote: ${registry}")
        sh script: "docker tag ${imageName} ${registry}/${imageName}:${BUILD_NUMBER}"
        sh script: "docker push ${registry}/${imageName}:${BUILD_NUMBER}"
      }
    }
    stage('CleanUp') {
      parallel{
        stage("CleanUp docker") {
          steps {
            sh script: "docker kill \$(docker ps -q) || true", label: "Stop all containers"
            sh script: "docker rm \$(docker ps -a -q) || true", label: "Delete all containers"

            sh script: "docker images | grep ${imageName} | awk '{print \$3 }' | sort -u", label: "Show related docker images"
            sh script: "docker images | grep ${imageName} | awk '{print \$3 }' | sort -u | xargs docker rmi --force || true", label: "Delete related docker images"

            sh script: "docker images -q -f dangling=true", label: "Show dangling docker images"
            sh script: "docker rmi \$(docker images -q -f dangling=true) --force || true", label: "Delete dangling docker images"
          }
        }
        stage("Registry info") {
          steps {
            sh script: "curl http://${registry}/v2/_catalog", label: "Remote images"
            sh script: "curl http://${registry}/v2/${imageName}/tags/list", label: "Remote image tags"
          }
        }
      }
    }
  }
}
