pipeline{
    agent any
    stages {
        stage('Build lib') {
            steps {
                checkout scm
                sh  './gradlew libpaiman:build'
            }
        }
        stage('Test lib') {
            steps {
                sh './gradlew libpaiman:check'
            }
        }
        stage('Build client') {
            steps {
                parallel javafx: {
                    sh './gradlew app:build'
                },
                android: {
                    sh './gradlew android:assemble'
                }
            }
        }
        stage('Test clients') {
            steps {
                parallel javafx: {
                    sh './gradlew app:check'
                },
                android: {
                    sh './gradlew android:connectedCheck'
                }
            }
        }
    }
}