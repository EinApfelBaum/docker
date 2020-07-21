def branchNameExt;
def dockerTag;
def test;
pipeline {
  agent any
  stages {
    stage('Info') {
        steps {
            echo "Current dir: ${pwd()}"
            sh 'docker --version'
            sh 'git --version'
            sh 'ls -la'
        }
    }
    stage("Interactive_Input") {
      steps {
        script {
          def userInput = input(
            id: 'userInput', message: 'Enter remote registry:', 
            parameters: [
            [$class: 'TextParameterDefinition', defaultValue: 'None', description: 'Remote address of registry.', name: 'Config']
          ])
          echo ("Remote: ${userInput}")
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
