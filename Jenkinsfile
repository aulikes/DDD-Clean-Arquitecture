pipeline {
  agent any

  environment {
    // Variables necesarias para Sonar y Gradle
    DOCKER_IMAGE = 'ecommerce-api'
    DOCKER_PORT = '8093'
    SPRING_PROFILE = 'rabbit'
  }

  stages {

    stage('Checkout') {
      steps {
        checkout scm
      }
    }

//     stage('Checkout') {
//       steps {
//         checkout([
//           $class: 'GitSCM',
//           branches: [[name: 'main']],
//           userRemoteConfigs: [[url: 'https://github.com/aulikes/DDD-Clean-Arquitecture.git']],
//           extensions: [[$class: 'CloneOption', noTags: false, shallow: false, depth: 0]]
//         ])
//       }
//     }

    stage('Build & Test') {
      steps {
        // Limpia posibles daemons colgados
        sh 'pkill -f gradle || true'
        // Luego build
        sh './gradlew clean build jacocoTestReport --no-daemon'
      }
    }

    stage('SonarQube Analysis') {
      steps {
        withSonarQubeEnv('sonaqube-docker') {
          withCredentials([string(credentialsId: 'Jenkins-Sonar', variable: 'SONAR_TOKEN')]) {
            sh "./gradlew sonarqube -Dsonar.login=${SONAR_TOKEN} --info"
          }
        }
      }
    }

    stage('Build Docker Image') {
      steps {
        script {
          sh "docker build -t ${DOCKER_IMAGE} ."
        }
      }
    }

    stage('Run Container') {
      steps {
        script {
          // Stop previous container if running
          sh "docker rm -f ${DOCKER_IMAGE} || true"
          // Run new container on port 8090
          sh "docker run -d -e SPRING_PROFILE=${SPRING_PROFILE} -p ${DOCKER_PORT}:${DOCKER_PORT} --name ${DOCKER_IMAGE} ${DOCKER_IMAGE} -Dsonar.scm.disabled=true"
        }
      }
    }
  }

  post {
    failure {
      echo 'Pipeline failed.'
    }
    success {
      echo 'Pipeline completed successfully.'
    }
  }
}
