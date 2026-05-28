pipeline {

    agent any

    tools {
        maven 'Maven3'
    }

    environment {
        PUBLISH_DIR = "publish"
    }

    parameters {
        string(
            name: 'BRANCH_NAME',
            defaultValue: 'develop',
            description: 'Branch name'
        )
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: params.BRANCH_NAME,
                    url: 'https://github.com/k4r3l1ns/coffee_machine.git'
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
                    params.BRANCH_NAME.startsWith('feature/')
                }
            }
            steps {
                sh 'mvn test'
            }
        }

        stage('Static Analysis') {
            when {
                expression {
                    params.BRANCH_NAME == 'develop'
                }
            }
            steps {
                sh 'mvn checkstyle:check'
            }
        }

        stage('Coverage') {
            when {
                expression {
                    params.BRANCH_NAME.startsWith('feature/')
                }
            }
            steps {
                sh 'mvn verify'
            }
        }

        stage('Install') {
            steps {
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('Publish Artifact') {
            steps {
                sh '''
                    mkdir -p publish
                    cp target/*.jar publish
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