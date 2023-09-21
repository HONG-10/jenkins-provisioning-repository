#!/usr/bin/env groovy

/*
 * 'maven-app-01' CD 공통 pipeline
 */

def call (Map args) {

echo "========++++++++ '${this.toString().take(this.toString().lastIndexOf('@'))}' 실행 Parameter ::: START ++++++++========"
echo args.toString()
echo "========++++++++ '${this.toString().take(this.toString().lastIndexOf('@'))}' 실행 Parameter ::: END ++++++++========"

// Jenkins Job 시작 시간 : environment 내에 선언시 변수 값을 조회할 때마다 sh 를 호출하여 pipeline 외부에 별도 선언
def BUILD_DATETIME = """${ new Date().format('yyyyMMdd-HHmmss', TimeZone.getTimeZone('Asia/Seoul')) }"""

// Pipeline 실행 취소 flag
def AUTO_CANCELLED = false

// 개발/스테이징과 운영 환경별 JOB Parameter 구분 ::: START
switch (args.APP_PROFILE) {

  case 'dev':

    properties([
      parameters(
        [

          [ $class: 'ChoiceParameter', choiceType: 'PT_RADIO',
            randomName: '', filterable: false, // filterLength: 1,
            name: 'CD_JOB_OPTION',
            description: '''
[ 개발 ] CD Job 실행 Option<br>
&nbsp;&nbsp;(1) 해당 값을 입력하지 않는 경우, Job 자체가 실행되지 않고 'ABORTED' 로 종료됩니다.<br>
&nbsp;&nbsp;(2) description 내에서 줄바꿈은 &lt;br&gt; 입니다.<br>
''',
            script: [
              $class: 'GroovyScript',
              script: [
                classpath: [], sandbox: true,
                script: '''return [
                  'OPTION_1' : '[OPTION_1] CD 실행',   // '...:selected' 로 지정시 자동 선택
                ]'''
              ]
            ]
          ],
          string(
            name: 'BUILD_VERSION',
            defaultValue: '',
            description: '''
[[ 개발 ]] 배포할 BUILD_VERSION 값<br>
&nbsp;&nbsp;Nexus Repository 에 Upload 된 Artifact 들의 Version 중 하나여야 합니다.<br>
''',
            trim: true
          )
        ]
      ),   // End of parameters
    ])

  break

  case 'stg':

  break

  case 'prd':

  break

}   // End of switch
// 개발/스테이징과 운영 환경별 JOB Parameter 구분 ::: END

// Tomcat Server URL
def TOMCAT_SERVER_URL = ''

// Tomcat Server 에 SSH 접속시 사용할 Credentials ID
def TOMCAT_SSH_CREDENTIAL_ID = ''

// 개발, 스테이징, 운영 환경별 배포 대상 Tomcat 서버 URL 구분 ::: START
switch (args.APP_PROFILE) {

  case 'dev':
    TOMCAT_SERVER_URL = 'tomcat-dev'
    TOMCAT_SSH_CREDENTIAL_ID = 'cicd-ssh-credentials-develop-staging'

  break;

  case 'stg':
    TOMCAT_SERVER_URL = 'tomcat-stg'
    TOMCAT_SSH_CREDENTIAL_ID = 'cicd-ssh-credentials-develop-staging'

  break;

  case 'prd':
    TOMCAT_SERVER_URL = 'tomcat-prd'
    TOMCAT_SSH_CREDENTIAL_ID = 'cicd-ssh-credentials-production'

  break;

}   // End of switch
// 개발, 스테이징, 운영 환경별 배포 대상 Tomcat 서버 URL 구분 ::: END

pipeline {

  environment {

    /*
     * 대상 Project / Profile / Jenkins Job 실행 관련 정보
     */

    PROJECT_NAME        = "${args.PROJECT_NAME ? args.PROJECT_NAME : 'source' }"
    PROJECT_DESCRIPTION = "${args.PROJECT_DESCRIPTION}"

    // APP_PROFILE 변수 값 : 개발/스태이징/운영 환경 구분
    APP_PROFILE = "${args.APP_PROFILE}"

    // Jenkins workspace 내 해당 Job 작업 경로 ( (예) /var/jenkins_home/workspace/... )
    WORK_DIR = "${env.WORKSPACE}"

    /*
     * Artifact (jar/war) 의 Maven groupId, artifactId, extension (확장자)
     */

    MAVEN_GROUP_ID           = 'cicd-practice'
    MAVEN_ARTIFACT_ID        = 'maven-app-0.0.1'
    MAVEN_ARTIFACT_EXTENSION = 'war'

    /*
     * Nexus Artifact Repository 정보
     */

    // 빌드 완료된 Artifact (jar) 가 업로드되는 Nexus Repository 이름
    NEXUS_ARTIFACT_REPOSITORY_NAME = 'maven-app-01'

    // Nexus 다운로드 전용 계정정보 : Nexus 에서 WAR 다운로드시 사용
    // => Username/Password 형식 : "${(변수명)_USR} / ${(변수명)_PSW}" 으로 지정됨
    //    (예) ${NEXUS_DOWNLOAD_CREDENTIALS_USR} / ${NEXUS_DOWNLOAD_CREDENTIALS_PSW} )
    NEXUS_DOWNLOAD_CREDENTIALS = credentials('nexus-maven-mirror-credentials')

    /*
     * ARTIFACT 다운로드 정보
     */

    ARTIFACT_DOWNLOAD_PATH      = '.'
    ARTIFACT_DOWNLOAD_NAME      = "${env.MAVEN_ARTIFACT_ID}-${BUILD_VERSION}.${env.MAVEN_ARTIFACT_EXTENSION}"

    /*
     * Tomcat 정보
     */

    CATALINA_HOME = '/usr/local/tomcat'

  }

  options {
    skipDefaultCheckout(true)   // This is required if you want to clean before build
    timestamps ()
    timeout(time: 300, unit: 'SECONDS')   // 전체 Job 5분 제한
  }

  agent any

  stages {

    stage("Init"){

        steps {
          cleanWs()   // Clean before build
          
          echo "======== Jenkins Job Info ::: START ========"

          echo "### Jenkins Job BUILD_NUMBER  ::: '${env.BUILD_NUMBER}'"
          echo "### Jenkins Job 시작 시간     ::: '${BUILD_DATETIME}'"
          echo "### 배포할 빌드 버전          ::: '${BUILD_VERSION}'"

          echo "======== Jenkins Job Info ::: END ========"

          // 10분간 git credential cache 유지
          sh '''
          git config --global credential.helper 'cache --timeout=600'
          '''

          script {

            if( params.CD_JOB_OPTION == '' || params.CD_JOB_OPTION == null ) {
              echo '======================================================================='
              echo '[ WARN ] CD_JOB_OPTION is Empty !!! ::: All stages will be skipped ...'
              echo '======================================================================='
              
              AUTO_CANCELLED = true
              currentBuild.result = 'ABORTED'

              return

            } else {

              echo '======================================================================='
              echo "[ INFO ] PROJECT_NAME            ::: '${PROJECT_NAME}'"
              echo "[ INFO ] PROJECT_DESCRIPTION     ::: '${PROJECT_DESCRIPTION}'"
              echo "[ INFO ] CD_JOB_OPTION 값        ::: '${CD_JOB_OPTION}'"
              echo "[ INFO ] 배포할 BUILD_VERSION 값 ::: '${BUILD_VERSION}'"
              echo '======================================================================='

            }

          }

        }   // End of steps

    }   // End of 'Init' stage

    stage('Download Artifact') {

      when {
        allOf {
          expression { AUTO_CANCELLED == false }
        }
      }

      steps {
        echo "========++++++++ Download Artifact ::: START ++++++++========"
/*
        sh '''
        set +H

        curl -X 'GET' -G -k -v \
          -u "${NEXUS_DOWNLOAD_CREDENTIALS_USR}:${NEXUS_DOWNLOAD_CREDENTIALS_PSW}" \
          -d repository="${NEXUS_ARTIFACT_REPOSITORY_NAME}" \
          -d maven.groupId="${MAVEN_GROUP_ID}" \
          -d maven.artifactId="${MAVEN_ARTIFACT_ID}" \
          -d maven.baseVersion="${.BUILD_VERSION}" \
          -d maven.extension="${MAVEN_ARTIFACT_EXTENSION}" \
          -d sort=version \
          -d direction=desc \
          -o "${ARTIFACT_DOWNLOAD_PATH}/${ARTIFACT_DOWNLOAD_NAME}" \
          -L "${NEXUS_URL}"/service/rest/v1/search/assets/download
        '''
*/
          // Nexus 가 없으므로 CI Job 에서 '/var/jenkins_home/tmp' 에 보존한 Artifact 를 해당 Job 의 workspace 경로로 COPY
          sh '''
          cp /var/jenkins_home/tmp/"${MAVEN_ARTIFACT_ID}-${BUILD_VERSION}.${MAVEN_ARTIFACT_EXTENSION}" \
            "${ARTIFACT_DOWNLOAD_PATH}/${ARTIFACT_DOWNLOAD_NAME}"
          '''

      }   // End of steps

      post {
        success {
          echo '========++++++++ Download Artifact ::: END ++++++++========'
        }
        failure {
          echo '[ ERROR ]  Download Artifact ::: Failed !!!'
        }
      }   // End of post
    }  // End of 'Download Artifact' stage

    stage('Deploy WAR into Tomcat Server') {

      when {
        allOf {
          expression { AUTO_CANCELLED == false }
        }
      }

      steps {
        echo "========++++++++ Deploy WAR into Tomcat Server ::: START ++++++++========"

        script {

          def remote = [:]

          remote.name = 'tomcat'
          remote.host = "${TOMCAT_SERVER_URL}"
          remote.allowAnyHosts = true

          // TOMCAT 서버에 ssh 접속
          withCredentials(
            [ sshUserPrivateKey(
              credentialsId: "${TOMCAT_SSH_CREDENTIAL_ID}",
              keyFileVariable: 'KEY_FILE', usernameVariable: 'USERNAME', passphraseVariable: ''
            ) ]
          ) {

            remote.user         = USERNAME
            remote.identityFile = KEY_FILE
            remote.passphrase   = ''
            
            // Jenkins 서버에서 Tomcat 서버로 Artifact 전송
            sshPut(
              remote: remote,
              from: "${ARTIFACT_DOWNLOAD_PATH}/${ARTIFACT_DOWNLOAD_NAME}",
              into: "/home/${USERNAME}"
            )

            // Tomcat Clean, Artifact => ROOT.war, Tomcat Restart
            sshCommand(
              remote: remote,
              command: """
source ~/.profile

rm -rf ${CATALINA_HOME}/webapps/*
rm -rf ${CATALINA_HOME}/work/*

mv /home/${USERNAME}/${ARTIFACT_DOWNLOAD_NAME} ${CATALINA_HOME}/webapps/ROOT.war

${CATALINA_HOME}/bin/catalina.sh stop
${CATALINA_HOME}/bin/catalina.sh start
"""
            )
          }   // End of withCredentials
        }   // End of script
      }   // End of steps

      post {
        success {
          echo '========++++++++ Deploy WAR into Tomcat Server ::: END ++++++++========'
        }
        failure {
          echo '[ ERROR ]  Deploy WAR into Tomcat Server ::: Failed !!!'
        }
      }   // End of post
    }  // End of 'Deploy WAR into Tomcat Server' stage

  } // End of stages

  /*
  * Declarative: Post Actions
  */ 

  post {

    always {

      // Jenkins workspace 정리
      cleanWs(
        cleanWhenSuccess: true,
        cleanWhenAborted: true,
        cleanWhenNotBuilt: true,
        cleanWhenUnstable: true,
        cleanWhenFailure: true,
        deleteDirs: true,
        notFailBuild: true,
        disableDeferredWipeout: true,
        patterns: [
          [pattern: '**/*', type: 'INCLUDE']
        ]
      )

      dir("${env.WORKSPACE}@tmp") {
        deleteDir()
      }
      dir("${env.WORKSPACE}@script") {
        deleteDir()
      }
      dir("${env.WORKSPACE}@script@tmp") {
        deleteDir()
      }
      dir("${env.WORKSPACE}@libs") {
        deleteDir()
      }

    }   // End of always

  }  // End of post
  
}   // End of pipeline

}   // End of call
