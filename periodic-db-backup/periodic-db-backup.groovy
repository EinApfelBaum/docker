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
            sh "docker --version", label: "Docker Version"
            sh "git --version", label: "GitVersion"
            echo "Current dir: ${pwd()}"
            sh 'ls -la'
            sh "env", label: "Build Enviroment"
          }
        }  
        stage("Docker images") {
          steps{
            sh "curl http://${userInput}/v2/_catalog", label: "Remote images"
            sh "docker images", label: "Local images"
          }
        }
      }        
    }
    stage('Build and test') {
       steps {
         sh 'docker build -t periodic-backup periodic-db-backup/'
       }
    }
  }     
}
