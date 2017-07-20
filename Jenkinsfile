pipeline{
    agent any
    stages {
        stage('Build lib') {
            agent any
            steps {
                checkout scm
                sh  './gradlew libpaiman:build'
            }
        }
        stage('Test lib') {
            agent any
            steps {
                sh './gradlew libpaiman:check'
            }
        }
        stage('Build client') {
            steps {
                parallel javafx: {
                    agent any
                    sh './gradlew app:build'
                },
                android: {
                    agent {label: android-sdk}
                    sh './gradlew android:assemble'
                }
            }
        }
        stage('Test clients') {
            steps {
                parallel javafx: {
                    agent any
                    sh './gradlew app:check'
                },
                android: {
                    agent {label: "android-emulator"}
                    echo "Create android test devices"
                    sh "echo 'no\n' | $ANDROID_HOME/tools/bin/avdmanager create -n jenkins-paiman-19 -k " +
                            "'system-images;android-19;default;armeabi-v7' "
                    sh "echo 'no\n' | $ANDROID_HOME/tools/bin/avdmanager create -n jenkins-paiman-21 -k " +
                            "'system-images;android-23;default;armeabi-v7' "
                    sh "echo 'no\n' | $ANDROID_HOME/tools/bin/avdmanager create -n jenkins-paiman-24 -k " +
                            "'system-images;android-24;default;armeabi-v7' "
                    echo "Start emulators"
                    sh "$ANDROID_HOME/tools/emulator @jenkins-paiman-19 -no-audio -no-window"
                    sh "$ANDROID_HOME/tools/emulator @jenkins-paiman-21 -no-audio -no-window"
                    sh "$ANDROID_HOME/tools/emulator @jenkins-paiman-24 -no-audio -no-window"
                    echo "RunCheck"
                    sh './gradlew android:connectedCheck'
                }
            }
        }
        post {
            always {
                sh '($ANDROID_HOME/tools/bin/avdmanager remove -n jenkins-paiman-19 && ' +
                        '$ANDROID_HOME/tools/bin/avdmanager remove -n jenkins-paiman-23 && ' +
                        '$ANDROID_HOME/tools/bin/avdmanager remove -n jenkins-paiman-24) || return 0'
            }
        }
    }
}