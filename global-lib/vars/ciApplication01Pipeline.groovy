#!/usr/bin/env groovy

/*
 * 'maven-app-01' CI 공통 pipeline
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
            name: 'CI_JOB_OPTION',
            description: '''
[ 개발 ] CI Job 실행 Option<br>
&nbsp;&nbsp;(1) 해당 값을 입력하지 않는 경우, Job 자체가 실행되지 않고 'ABORTED' 로 종료됩니다.<br>
&nbsp;&nbsp;(2) description 내에서 줄바꿈은 &lt;br&gt; 입니다.<br>
''',
            script: [
              $class: 'GroovyScript',
              script: [
                classpath: [], sandbox: true,
                script: '''return [
                  'OPTION_1' : '[OPTION_1] CI 실행 Only',   // '...:selected' 로 지정시 자동 선택
                  'OPTION_2' : '[OPTION_2] CI 실행 후 CD 실행'
                ]'''
              ]
            ]
          ],
          string( 
            name: 'PARAM_SAMPLE_STRING',
            description: '''
[[ 개발 ]] 샘플 String Parameter<br>
''',
            defaultValue: '',
            trim: true
          )
        ]
      ),   // End of parameters
      pipelineTriggers([
        parameterizedCron('''
# 매주 월 ~ 금 8, 12, 16, 18시 30분에 CI_JOB_OPTION=OPTION_3 Paramater 로 실행
H(30-30) 8,12,16,18 * * 1-5 %CI_JOB_OPTION=OPTION_1
        ''')
      ])   // End of pipelineTriggers

    ])

  break

  case 'stg':

  break

  case 'prd':

  break

}   // End of switch
// 개발/스테이징과 운영 환경별 JOB Parameter 구분 ::: END


// 개발, 스테이징, 운영 환경별 CI/CD 대상 branch 구분 ::: START

// CI/CD 대상 branch
def SOURCE_REPOSITORY_TARGET_BRANCH_NAME = ''

switch (args.APP_PROFILE) {

  case 'dev':
    SOURCE_REPOSITORY_TARGET_BRANCH_NAME = 'develop'

  break;

  case 'stg':
    SOURCE_REPOSITORY_TARGET_BRANCH_NAME = 'main'

  break;

  case 'prd':
    // 운영 CI 실행시 브랜치가 아닌 Tag 기반으로 소스 clone
    // 스테이징 CI 실행 이력이 있는 Tag 가 있는 commit 시점으로 운영 CI 실행
    SOURCE_REPOSITORY_TARGET_BRANCH_NAME = 'CI--stg--' + params.TAG_VALUE

  break;

}
// 개발, 스테이징, 운영 환경별 CI/CD 대상 branch 구분 ::: END


// Tomcat Server URL
def TOMCAT_SERVER_URL = ''

// Tomcat Server 에 SSH 접속시 사용할 Credentials ID
def TOMCAT_SSH_CREDENTIAL_ID = ''

// 개발, 스테이징, 운영 환경별 배포 대상 Tomcat 서버 URL 구분 ::: START
switch (args.APP_PROFILE) {

  case 'dev':
    TOMCAT_SERVER_URL = 'tomcat-dev'
    TOMCAT_SSH_CREDENTIAL_ID = 'cicd-ssh-credentials-dev-stg'

  break;

  case 'stg':
    TOMCAT_SERVER_URL = 'tomcat-stg'
    TOMCAT_SSH_CREDENTIAL_ID = 'cicd-ssh-credentials-dev-stg'

  break;

  case 'prd':
    TOMCAT_SERVER_URL = 'tomcat-prd'
    TOMCAT_SSH_CREDENTIAL_ID = 'cicd-ssh-credentials-prd'

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

    // 새로운 빌드 버전
    // - TAG_VALUE Parameter 값 전달시   : "${TAG_VALUE}-${BUILD_DATETIME}" 형식으로 지정됨
    // - TAG_VALUE Parameter 값 미전달시 : Jenkins Job 시작 시간 기반으로 Unique 한 값이 지정될 수 있도록 함
    NEW_BUILD_VERSION = "${ params.TAG_VALUE ? params.TAG_VALUE + '-' + BUILD_DATETIME : BUILD_DATETIME }"

    // Jenkins workspace 내 해당 Job 작업 경로 ( (예) /var/jenkins_home/workspace/... )
    WORK_DIR = "${env.WORKSPACE}"

    /*
     * Source Repository 의 Git Clone 관련 정보
     */

    // 해당 프로젝트의 Source 가 있는 Git Repository Protocol / URL
    SOURCE_REPOSITORY_PROTOCOL = 'https'
    SOURCE_REPOSITORY_URL      = 'bitbucket.org/reverse-2022-vcs/maven-app-01.git'

    // Source Repository 의 Git Clone 절대 경로 : (해당 Job 작업 경로)/(프로젝트명)-(새로운 빌드 버전)
    SOURCE_REPOSITORY_CLONE_FULL_PATH = "${env.WORK_DIR}/${env.PROJECT_NAME}-${env.NEW_BUILD_VERSION}"

    // Source Repository 관련 Jenkins credentialsId : git clone / commit / push 등 에 사용
    SOURCE_REPOSITORY_SCM_CREDENTIALS_ID = 'maven-app-01-repository-git-credentials'

    /*
     * Maven 빌드 / Artifact 관련 정보
     */

    // Maven 빌드 후 Artifact (jar/war) 생성 경로
    ARTIFACT_FILE_PATH = "${args.ARTIFACT_FILE_PATH}"

    // Maven 빌드 후 생성되는 Artifact (jar/war) 파일명
    ARTIFACT_FILE_NAME = "${args.ARTIFACT_FILE_NAME}"

    /*
     * Nexus Artifact Repository 정보
     */
    // 홈페이지 Nexus URL
    // => Jenkins 전역 환경 변수 'NEXUS_URL' 값을 사용함

    // 빌드 완료된 Artifact (jar) 가 업로드되는 Nexus Repository 이름
    NEXUS_ARTIFACT_REPOSITORY_NAME = 'maven-app-01'

    // Nexus 업로드 전용 계정정보 : Maven 빌드 후 Nexus 에 War/Jar 업로드시 사용
    // => Username 과 Password 값들이 각각 ${(변수명)_USR}, ${(변수명)_PSW} 으로 지정됨
    //    (예) ${NEXUS_UPLOAD_CREDENTIALS_USR} / ${NEXUS_UPLOAD_CREDENTIALS_PSW} )
    NEXUS_UPLOAD_CREDENTIALS = credentials('nexus-maven-mirror-credentials')

    /*
     * Artifact (jar/war) 의 Maven groupId, artifactId, extension (확장자)
     */

    MAVEN_GROUP_ID           = 'cicd-practice'
    MAVEN_ARTIFACT_ID        = 'maven-app-0.0.1'
    MAVEN_ARTIFACT_EXTENSION = 'war'

    /*
     * Source Repository 에 Build Version 관련 Tag 값 push 관련 정보
     */

    // git config user.name ~ , git config user.email ~ 로 지정할 User 정보
    // - args 로 SOURCE_REPOSITORY_USER_NAME, SOURCE_REPOSITORY_USER_EMAIL 값 전달시 : 해당 값으로 지정
    // - SOURCE_REPOSITORY_USER_NAME, SOURCE_REPOSITORY_USER_EMAIL 값 미전달시 : Jenkins 전역변수로 지정된 DevOps Repository User 정보로 지정
    SOURCE_REPOSITORY_USER_NAME  = "${ args.SOURCE_REPOSITORY_USER_NAME ? args.SOURCE_REPOSITORY_USER_NAME : env.DEVOPS_REPOSITORY_USER_NAME}"
    SOURCE_REPOSITORY_USER_EMAIL = "${ args.SOURCE_REPOSITORY_USER_EMAIL ? args.SOURCE_REPOSITORY_USER_EMAIL : env.DEVOPS_REPOSITORY_USER_EMAIL}"

    // Source Repository 에 추가할 Build Version 관련 Tag 값
    SOURCE_REPOSITORY_BUILD_VERSION_TAG = "CI--${env.APP_PROFILE}--${env.NEW_BUILD_VERSION}"

    // Source Repository 에 Build Version 관련 Tag 추가시 commit message
    SOURCE_REPOSITORY_BUILD_VERSION_TAG_COMMIT_MESSAGE = "CI--${env.APP_PROFILE}--${env.NEW_BUILD_VERSION}"

    /*
     * CD (배포) Job 정보
     */

    // 해당 CI Job 에 해당하는 CD Job 경로
    CD_JOB_NAME = "${args.CD_JOB_NAME}"

  }

  options {
    skipDefaultCheckout(true)   // This is required if you want to clean before build
    timestamps ()               // Jenkins Timestamper plug-in 적용
    timeout(time: 600, unit: 'SECONDS')   // 전체 Job 10분 제한
  }

  tools {
    jdk 'openjdk-8'
    maven 'maven-3.8.8'
  }

  agent any

  stages {

    stage("Init"){

        steps {
          cleanWs()   // Clean before build
          
          echo "======== Jenkins Job Info ::: START ========"

          echo "### Jenkins Job BUILD_NUMBER  ::: '${env.BUILD_NUMBER}'"
          echo "### Jenkins Job 시작 시간     ::: '${BUILD_DATETIME}'"
          echo "### 새로운 빌드 버전          ::: '${NEW_BUILD_VERSION}'"

          echo "======== Jenkins Job Info ::: END ========"

          // 10분간 git credential cache 유지
          sh '''
          git config --global credential.helper 'cache --timeout=600'
          '''

          script {

            if( params.CI_JOB_OPTION == '' || params.CI_JOB_OPTION == null ) {
              echo '======================================================================='
              echo '[ WARN ] CI_JOB_OPTION is Empty !!! ::: All stages will be skipped ...'
              echo '======================================================================='
              
              AUTO_CANCELLED = true
              currentBuild.result = 'ABORTED'

              return

            } else {

              echo '======================================================================='
              echo "[ INFO ] PROJECT_NAME           ::: '${PROJECT_NAME}'"
              echo "[ INFO ] PROJECT_DESCRIPTION    ::: '${PROJECT_DESCRIPTION}'"
              echo "[ INFO ] CI_JOB_OPTION 값       ::: '${params.CI_JOB_OPTION}'"
              echo "[ INFO ] PARAM_SAMPLE_STRING 값 ::: '${params.PARAM_SAMPLE_STRING}'"
              echo '======================================================================='

            }

          }

        }   // End of steps

    }   // End of 'Init' stage

    stage('Source Repository Clone') {

      when {
        allOf {
          expression { AUTO_CANCELLED == false }
        }
      }

      steps {
        echo "========++++++++ Project '${env.PROJECT_NAME}' ::: Source Repository Clone ::: START ++++++++========"
/*
        withCredentials([
          usernamePassword(credentialsId: "${SOURCE_REPOSITORY_SCM_CREDENTIALS_ID}", usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')
        ]) {
          sh """
          git clone -b ${SOURCE_REPOSITORY_TARGET_BRANCH_NAME} \
            --single-branch --depth 1 \
            ${SOURCE_REPOSITORY_PROTOCOL}://${GIT_USERNAME}:${GIT_PASSWORD}@${SOURCE_REPOSITORY_URL} \
            ${SOURCE_REPOSITORY_CLONE_FULL_PATH}
          """
        }
*/
        script {
          checkout([
            $class: 'GitSCM',
            branches: [[name: "${SOURCE_REPOSITORY_TARGET_BRANCH_NAME}"]],
            doGenerateSubmoduleConfigurations: false,
            extensions: [
              [ $class: 'CloneOption', shallow: true, depth: 1, timeout: 60 ],
              [ $class: 'RelativeTargetDirectory', relativeTargetDir: "${SOURCE_REPOSITORY_CLONE_FULL_PATH}"]
            ],
            submoduleCfg: [],
            userRemoteConfigs: [
              [credentialsId: "${SOURCE_REPOSITORY_SCM_CREDENTIALS_ID}", url: "${SOURCE_REPOSITORY_PROTOCOL}://${SOURCE_REPOSITORY_URL}"]
            ]
          ])
        }

        dir("${SOURCE_REPOSITORY_CLONE_FULL_PATH}") {

          echo "======== Commit Log  ::: START ========"

          script {
            // Commit ID (짧은 길이 커밋 해시)
            env.CURRENT_COMMIT_ID = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()

            // Commit Author
            env.CURRENT_COMMIT_AUTHOR = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%an'").trim()

            // Commit Time
            env.CURRENT_COMMIT_TIME= sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%ad' --date=format:'%Y-%m-%d %H:%M:%S %z'").trim()

            // Commit Message
            env.CURRENT_COMMIT_MESSAGE = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%s'").trim()
          }

          echo "### Commit ID      ::: '${CURRENT_COMMIT_ID}'"
          echo "### Commit Author  ::: '${CURRENT_COMMIT_AUTHOR}'"
          echo "### Commit Time    ::: '${CURRENT_COMMIT_TIME}'"
          echo "### Commit Message ::: '${CURRENT_COMMIT_MESSAGE}'"

          echo "======== Commit Log  ::: END ========"
        }

      }   // End of steps

      post {
        success {
          echo '========++++++++ Source Repository Clone ::: END ++++++++========'
        }
        failure {
          echo '[ ERROR ]  Source Repository Clone ::: Failed !!!'
        }
      }   // End of post

    }  // End of 'Source Repository Clone' stage

    stage('Pre-Work Before Maven Build') {

      when {
        allOf {
          expression { AUTO_CANCELLED == false }
        }
      }

      steps {
        echo "========++++++++ Project '${env.PROJECT_NAME}' ::: Pre-Work Before Maven Build ::: START ++++++++========"

        dir("${SOURCE_REPOSITORY_CLONE_FULL_PATH}") {

          sh """
          ls -al
          """

        }   // End of dir
      }   // End of stpes

      post {
        success {
          echo '========++++++++ Pre-Work Before Maven Build ::: END ++++++++========'
        }
        failure {
          echo '[ ERROR ]  Pre-Work Before Maven Build ::: Failed !!!'
        }
      }   // End of post
    }   // End of 'Pre-Work Before Maven Build' stage

    stage('Maven Build') {

      when {
        allOf {
          expression { AUTO_CANCELLED == false }
        }
      }

      steps {
        dir("${env.SOURCE_REPOSITORY_CLONE_FULL_PATH}") {
          echo "========++++++++ Project '${env.PROJECT_NAME}' ::: Maven Build ::: START ++++++++========"

          withMaven() {  // 자동으로 global-maven-settings-xml 참조
            sh '''
            mvn clean package -Dmaven.test.skip=true
            '''
          }
        }   // End of dir
      }   // End of steps

      post {
        success {
          echo '========++++++++ Maven Build ::: END ++++++++========'
        }
        failure {
          echo '[ ERROR ]  Maven Build ::: Failed !!!'
        }
      }   // End of post

    }  // End of 'Maven Build' stage

    stage('Upload To Nexus') {

      steps {
        dir("${SOURCE_REPOSITORY_CLONE_FULL_PATH}") {
          echo "========++++++++ Project '${PROJECT_DESCRIPTION}' ::: Upload To Nexus ::: START ++++++++========"

          // Nexus 가 없으므로 임시로 빌드된 Artifact 를 '/var/jenkins_home/tmp' 하위로 COPY 하여 보존
          sh '''
          [ -d /var/jenkins_home/tmp ] || mkdir -p /var/jenkins_home/tmp
          cp "${ARTIFACT_FILE_PATH}/${ARTIFACT_FILE_NAME}" \
            /var/jenkins_home/tmp/"${MAVEN_ARTIFACT_ID}-${NEW_BUILD_VERSION}.${MAVEN_ARTIFACT_EXTENSION}"
          '''

/*
          sh '''
          set +H

          curl -v -u "${NEXUS_UPLOAD_CREDENTIALS_USR}:${NEXUS_UPLOAD_CREDENTIALS_PSW}" \
            -F maven2.groupId="${MAVEN_GROUP_ID}" \
            -F maven2.artifactId="${MAVEN_ARTIFACT_ID}" \
            -F maven2.version="${NEW_BUILD_VERSION}"  \
            -F maven2.asset1=@"${ARTIFACT_FILE_PATH}/${ARTIFACT_FILE_NAME}" \
            -F maven2.asset1.extension="${MAVEN_ARTIFACT_EXTENSION}" \
            -F maven2.generate-pom=true  \
            "${NEXUS_URL}/service/rest/v1/components?repository=${NEXUS_ARTIFACT_REPOSITORY_NAME}"
          '''
*/
        }
      }   // End of steps

      post {
        success {
          echo '========++++++++ Upload To Nexus ::: END ++++++++========'
        }
        failure {
          echo '[ ERROR ]  Upload To Nexus ::: Failed !!!'
        }
      }   // End of post

    }  // End of 'Upload To Nexus' stage

    stage('Add Build Version Tag') {

      when {
        allOf {
          expression { AUTO_CANCELLED == false }
        }
      }

      steps {
        dir("${env.SOURCE_REPOSITORY_CLONE_FULL_PATH}") {

          script {
            switch( env.APP_PROFILE ) {

              // 개발 환경 CI 의 경우 Source Repository 에 Build Version Tag 를 추가하지 않음
              case 'dev':
                echo "[ INFO ]  Add Build Version Tag ::: Skipping"

              break;

              // 스테이징 환경 CI 의 경우, Source Repository 에 Build Version Tag 추가
              case 'stg':

                sh """
                git config user.name "${SOURCE_REPOSITORY_USER_NAME}"
                git config user.email "${SOURCE_REPOSITORY_USER_EMAIL}"

                git tag \
                  -a "${SOURCE_REPOSITORY_BUILD_VERSION_TAG}" \
                  -m "${SOURCE_REPOSITORY_BUILD_VERSION_TAG_COMMIT_MESSAGE}" \
                  "${CURRENT_COMMIT_ID}"

                git push origin HEAD:"${SOURCE_REPOSITORY_TARGET_BRANCH_NAME}" --tags
                """

              break;

              case 'prd':

                // 운영 CI 의 경우, 명시적으로 main 브랜치에 tag 를 push 함
                sh """
                git config user.name "${SOURCE_REPOSITORY_USER_NAME}"
                git config user.email "${SOURCE_REPOSITORY_USER_EMAIL}"

                git tag \
                  -a "${SOURCE_REPOSITORY_BUILD_VERSION_TAG}" \
                  -m "${SOURCE_REPOSITORY_BUILD_VERSION_TAG_COMMIT_MESSAGE}" \
                  "${CURRENT_COMMIT_ID}"

                git push origin HEAD:main --tags
                """

              break;

            }   // End of switch
          }   // End of script
        }   // End of dir
      }   // End of steps

      post {
        success {
          echo '========++++++++ Add Build Version Tag ::: END ++++++++========'
        }
        failure {
          echo '[ ERROR ]  Add Build Version Tag ::: Failed !!!'
        }
      }   // End of post

    }   // End of 'Add Build Version Tag'

    stage('Trigger CD Pipeline') {

      when {
        allOf {
          expression { AUTO_CANCELLED == false }
          // CI Job 실행 Option 값 'OPTION_2' 인 경우에만 실행
          expression { params.CI_JOB_OPTION == 'OPTION_2' }
        }
      }

      steps {
        echo "========++++++++ Trigger CD Pipeline ::: START ++++++++========"

        build(
          job: "${CD_JOB_NAME}",
          parameters: [
            [$class: 'StringParameterValue', name: 'CD_JOB_OPTION', value: 'OPTION_1'] ,
            [$class: 'StringParameterValue', name: 'BUILD_VERSION', value: "${NEW_BUILD_VERSION}"]
          ]
        )

      }   // End of steps

      post {
        success {
          echo '========++++++++ Trigger CD Pipeline ::: END ++++++++========'
        }
        failure {
          echo '[ ERROR ]  Trigger CD Pipeline ::: Failed !!!'
        }
      }   // End of post

    }  // End of 'Trigger CD Pipeline' stage

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
