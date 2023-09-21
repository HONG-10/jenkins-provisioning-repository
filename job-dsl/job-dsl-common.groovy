/*
 * 홈페이지 공통모듈 / Oz CI pipeline 등록 JOB DSL Script
 */

def jobFolderName = 'jenkins-pipeline-common'

folder(jobFolderName) {
  displayName('[CI/CD Practice] 공통 Job')
  description('공통 Job Folder')
}

pipelineJob(jobFolderName + '/' + 'section-01') {
  displayName('1-00. ========== [ 공통 Jar CI ] ==========')
  disabled()
}

pipelineJob(jobFolderName + '/' + 'section-02') {
  displayName('2-00. ========== [ SonarQube 정적 분석 ] ==========')
  disabled()
}

def pipelineJobArgsList = [

/**************************************************
 * section-01 ::: START
 **************************************************/

/*
 * [ 공통 Jar CI ]  pipeline No 1. ::: START
 */
[
  // 해당 pipeline Job 이름
  pipelineJobName  : jobFolderName + '/' + 'common-jar-ci-01' ,

  // 해당 pipeline Job 의 UI 출력명
  pipelineJobDisplayName  : '1-01. [ 공통 Jar CI ] Application 이름' ,

  // 해당 pipeline Job description
  pipelineJobDescription  : '<br><h3>[ From SCM ::: ... ]</h3><h3>[ To Nexus ::: ... ]</h3>' ,

  // DevOps Repository 내 해당 jenkinsfile 경로
  jenkinsfilePath  : "${JENKINSFILE_ROOT_PATH}/application-01/comm-jar/ci",

  // DevOps Repository 내 해당 jenkinsfile 이름
  jenkinsfileName  : 'Jenkinsfile-application-01-common-jar-ci',

  // DevOps Repository URL
  scmRepositoryURL : "${DEVOPS_REPOSITORY_URL}",

  // DevOps Repository 내 해당 Jenkinsfile 등이 위치한 branch
  scmRepositoryTargetBranchName : "${DEVOPS_REPOSITORY_TARGET_BRANCH_NAME}",

  // DevOps Repository 연동 Jenkins credentialsId
  scmRepositoryCredentials : "${DEVOPS_REPOSITORY_GIT_CREDENTIALS_ID}",

  // 해당 Job 의 실행 이력 유지 기간(일)
  buildLogKeepDays : 15,

  // 해당 Job 의 보관할 실행 이력 최대갯수
  buildLogKeepNum  : 30
],
/*
 * [ 공통 Jar CI ]  pipeline No 1. ::: END
 */

/**************************************************
 * section-01 ::: END
 **************************************************/

/**************************************************
 * section-02 ::: START
 **************************************************/

/*
 * [ SonarQube 정적 분석 ]  pipeline No 1. ::: START
 */
[
  // 해당 pipeline Job 이름
  pipelineJobName  : jobFolderName + '/' + 'sonarqube-analyze-application-01-develop' ,

  // 해당 pipeline Job 의 UI 출력명
  pipelineJobDisplayName  : '2-01. [ SonarQube 정적 분석 ] Application 이름 - develop 브랜치' ,

  // 해당 pipeline Job description
  pipelineJobDescription  : '<br><h3>[ From SCM ::: ... ]</h3><h3>[ To SonarQube ::: ... ]</h3>' ,

  // DevOps Repository 내 해당 jenkinsfile 경로
  jenkinsfilePath  : "${JENKINSFILE_ROOT_PATH}/sonarqube-analyze/application-01",

  // DevOps Repository 내 해당 jenkinsfile 이름
  jenkinsfileName  : 'Jenkinsfile-sonarqube-analyze-application-01-develop',

  // DevOps Repository URL
  scmRepositoryURL : "${DEVOPS_REPOSITORY_URL}",

  // DevOps Repository 내 해당 Jenkinsfile 등이 위치한 branch
  scmRepositoryTargetBranchName : "${DEVOPS_REPOSITORY_TARGET_BRANCH_NAME}",

  // DevOps Repository 연동 Jenkins credentialsId
  scmRepositoryCredentials : "${DEVOPS_REPOSITORY_GIT_CREDENTIALS_ID}",

  // 해당 Job 의 실행 이력 유지 기간(일)
  buildLogKeepDays : 15,

  // 해당 Job 의 보관할 실행 이력 최대갯수
  buildLogKeepNum  : 30
] ,
/*
 * [ SonarQube 정적 분석 ]  pipeline No 1. ::: END
 */


/*
 * [ SonarQube 정적 분석 ]  pipeline No 1. ::: START
 */
[
  // 해당 pipeline Job 이름
  pipelineJobName  : jobFolderName + '/' + 'sonarqube-analyze-application-01-main' ,

  // 해당 pipeline Job 의 UI 출력명
  pipelineJobDisplayName  : '2-01. [ SonarQube 정적 분석 ] Application 이름 - main 브랜치' ,

  // 해당 pipeline Job description
  pipelineJobDescription  : '<br><h3>[ From SCM ::: ... ]</h3><h3>[ To SonarQube ::: ... ]</h3>' ,

  // DevOps Repository 내 해당 jenkinsfile 경로
  jenkinsfilePath  : "${JENKINSFILE_ROOT_PATH}/sonarqube-analyze/application-01",

  // DevOps Repository 내 해당 jenkinsfile 이름
  jenkinsfileName  : 'Jenkinsfile-sonarqube-analyze-application-01-main',

  // DevOps Repository URL
  scmRepositoryURL : "${DEVOPS_REPOSITORY_URL}",

  // DevOps Repository 내 해당 Jenkinsfile 등이 위치한 branch
  scmRepositoryTargetBranchName : "${DEVOPS_REPOSITORY_TARGET_BRANCH_NAME}",

  // DevOps Repository 연동 Jenkins credentialsId
  scmRepositoryCredentials : "${DEVOPS_REPOSITORY_GIT_CREDENTIALS_ID}",

  // 해당 Job 의 실행 이력 유지 기간(일)
  buildLogKeepDays : 15,

  // 해당 Job 의 보관할 실행 이력 최대갯수
  buildLogKeepNum  : 30
]
/*
 * [ SonarQube 정적 분석 ]  pipeline No 2. ::: END
 */

/******************************
 * section-02 ::: END
 ******************************/

]   // End of pipelineJobArgsList

/*
 * Job DSL 기반 pipeline Job 등록 ::: START
 */

def jobDslUtil = new GroovyShell().parse(new File("${JOB_DSL_UTIL_PATH}").getText("UTF-8"))

pipelineJobArgsList.each { pipelineJobArgs ->
  jobDslUtil.createPipelineJob(this, pipelineJobArgs)
}

/*
 * Job DSL 기반 pipeline Job 등록 ::: END
 */
