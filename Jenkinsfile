pipeline {

    agent any

    environment {
        PUBLISH_DIR = "/home/jenkins/publish"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: "${env.BRANCH_NAME}",
                        url: 'YOUR_GIT_REPOSITORY_URL'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean compile test-compile'
            }
        }

        stage('Tests') {
            when {
                expression {
                    env.BRANCH_NAME.startsWith("feature/")
                }
            }
            steps {
                sh 'mvn test'
            }
        }

        stage('Static Analysis') {
            when {
                branch 'dev'
            }
            steps {
                sh 'mvn checkstyle:check'
            }
        }

        stage('Coverage') {
            steps {
                sh 'mvn jacoco:report jacoco:check'
            }
        }

        stage('Install') {
            steps {
                sh 'mvn install'
            }
        }

        stage('Publish Artifact') {
            steps {
                sh '''
                    mkdir -p $PUBLISH_DIR
                    cp target/*.jar $PUBLISH_DIR
                '''
            }
        }
    }

    post {

        success {
            echo 'Pipeline completed successfully'
        }

        failure {
            echo 'Pipeline failed'
        }
    }
}