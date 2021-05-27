pipeline {
    agent {
        docker {
            image 'maven:3.8.1-adoptopenjdk-16' 
            args '-v /root/.m2:/root/.m2' 
        }
    }
    stages {
        stage('spring-boot:build-image') { 
            steps {
                sh 'mvn -B -DskipTests spring-boot:build-image' 
            }
        }
    }
}