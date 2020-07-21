def userInput;
pipeline {
  agent any
  stages {
    stage("Interactive Input") {
      steps {
        script {
          userInput = input(
            id: 'userInput', message: 'Enter remote registry:', 
            parameters: [
            [$class: 'TextParameterDefinition', defaultValue: 'None', description: 'Remote address of registry.', name: 'Config']
          ])
          echo ("Remote: ${userInput}")
        }
      }
    }
    stage('Prepare') {
      parallel {
        stage("Info") {
          steps {
            sh script: "echo foo", label: "my step"
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
    stage('Build and test') {
       steps {
         sh script: "docker build -t periodic-backup periodic-db-backup/"
       }
    }
  }     
}
