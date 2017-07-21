def gradleParams = "-Psnapshot=false -Pbranch=${env.BRANCH_NAME}"
pipeline{
    agent any
    stages {
        stage('Build lib') {
            agent any
            steps {
                sh "git status"
                sh "git log"
                sh "git fetch https://github.com/bvolkmer/PaiMan.git +refs/heads/master:refs/remotes/origin/master"
                sh "git branch"
                sh "git rev-list refs/remotes/origin/master.."
                sh  "./gradlew libpaiman:build $gradleParams"
            }
        }
        stage('Test lib') {
            agent any
            steps {
                sh "./gradlew libpaiman:check $gradleParams"
            }
        }
        stage('Build client') {
            agent {label "android-sdk"}
            steps {
                parallel javafx: {
                    sh "./gradlew app:distZip $gradleParams"
                },
                android: {
                    sh "./gradlew android:assemble $gradleParams"
                }
            }
        }
        stage('Test clients') {
            agent {label "android-emulator"}
            steps {
                parallel javafx: {
                    sh "./gradlew app:check $gradleParams"
                },
                android: {
                    echo "Start emulators"
                    sh '$ANDROID_HOME/emulator/emulator @jenkins-paiman-19 -no-audio -no-window -wipe-data &'
                    sh '$ANDROID_HOME/emulator/emulator @jenkins-paiman-21 -no-audio -no-window -wipe-data &'
                    sh '$ANDROID_HOME/emulator/emulator @jenkins-paiman-24 -no-audio -no-window -wipe-data &'
                    echo "Wait for emulators"
                    waitUntil {
                        script {
                            try {
                                sh 'if [ `$ANDROID_HOME/platform-tools/adb devices | grep emulator | cut -f1 | wc -l` -ne 3 ]; then exit 1; fi'
                                return true
                            } catch (exception) {
                                return false
                            }
                        }
                    }
                    sh './android-wait-for-emulator.sh `$ANDROID_HOME/platform-tools/adb devices | grep emulator | cut -f1 `'
                    echo "RunCheck"
                    sh "./gradlew android:connectedCheck $gradleParams"
                }
            }
        }
        stage ("Deploy") {
            agent { label "deploy" }
            steps {
                sh "./gradlew copyArtifacts $gradleParams"
                sh "cp archive/* /srv/http/develop/downloads/PaiMan"
            }
        }
    }
}