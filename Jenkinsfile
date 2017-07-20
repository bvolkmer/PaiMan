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
            agent {label "android-sdk"}
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
            agent {label "android-emulator"}
            steps {
                parallel javafx: {
                    sh './gradlew app:check'
                },
                android: {
                    echo "Kill running emulators"
                    sh '$ANDROID_HOME/platform-tools/adb devices | grep emulator | cut -f1 | while read line; do ' +
                            '$ANDROID_HOME/platform-tools/adb -s $line emu kill; done'
                    echo "Create android test devices"
                    sh 'echo "no\n" | $ANDROID_HOME/tools/bin/avdmanager create avd -n jenkins-paiman-19 -k ' +
                            '"system-images;android-19;default;armeabi-v7a" --force'
                    sh 'echo "no\n" | $ANDROID_HOME/tools/bin/avdmanager create avd -n jenkins-paiman-21 -k ' +
                            '"system-images;android-21;default;armeabi-v7a" --force'
                    sh 'echo "no\n" | $ANDROID_HOME/tools/bin/avdmanager create avd -n jenkins-paiman-24 -k ' +
                            '"system-images;android-24;default;armeabi-v7a" --force'
                    echo "Start emulators"
                    sh '$ANDROID_HOME/emulator/emulator @jenkins-paiman-19 -no-audio -no-window &'
                    sh '$ANDROID_HOME/emulator/emulator @jenkins-paiman-21 -no-audio -no-window &'
                    sh '$ANDROID_HOME/emulator/emulator @jenkins-paiman-24 -no-audio -no-window &'
                    echo "Wait for emulators"
                    waitUntil {
                        sh 'if [ `$ANDROID_HOME/platform-tools/adb devices | grep emulator | cut -f1 | wc -l` -ne 3 ]; then return 1; fi'
                    }
                    sh './android-wait-for-emulator.sh `$ANDROID_HOME/platform-tools/adb devices | grep emulator | cut -f1 `'
                    echo "RunCheck"
                    sh './gradlew android:connectedCheck'
                }
            }
        }
    }
}