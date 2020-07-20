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
    } 
    // stage('Build and test') {
    //   steps {
    //     // sh 'docker build -t periodic-backup periodic-db-backup/'
    //   }
    // }
}
