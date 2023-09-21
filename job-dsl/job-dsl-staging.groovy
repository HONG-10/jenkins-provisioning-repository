/*
 * 스테이징 환경 CI/CD pipeline 등록 JOB DSL Script
 */

def jobFolderName = 'jenkins-pipeline-staging'

folder(jobFolderName) {
  displayName('[CI/CD Practice] 스테이징 CI/CD')
  description('스테이징 CI/CD Job Folder')
}

pipelineJob(jobFolderName + '/' + 'section-01') {
  displayName('1-00. ========== [ 스테이징 CI ] ==========')
  disabled()
}

pipelineJob(jobFolderName + '/' + 'section-02') {
  displayName('2-00. ========== [ 스테이징 CD ] ==========')
  disabled()
}

def pipelineJobArgsList = [

/**************************************************
 * section-01 ::: 스테이징 CI ::: START
 **************************************************/

/*
 * [ 스테이징 CI ]  pipeline No 1. ::: START
 */
[
  // 해당 pipeline Job 이름
  pipelineJobName  : jobFolderName + '/' + 'application-01-staging-ci' ,

  // 해당 pipeline Job 의 UI 출력명
  pipelineJobDisplayName  : '1-01. [ 스테이징 CI ] Application 이름' ,

  // 해당 pipeline Job description
  pipelineJobDescription  : '<br><h3>[ From ... ]</h3><h3>[ To ... ]</h3>' ,

  // DevOps Repository 내 해당 jenkinsfile 경로
  jenkinsfilePath  : "${JENKINSFILE_ROOT_PATH}/application-01/staging/ci",
  
  // DevOps Repository 내 해당 jenkinsfile 이름
  jenkinsfileName  : 'Jenkinsfile-application-01-staging-ci',

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
 * [ 스테이징 CI ]  pipeline No 1. ::: END
 */

/**************************************************
 * section-01 ::: 스테이징 CI ::: END
 **************************************************/

/**************************************************
 * section-02 ::: 스테이징 CD ::: START
 **************************************************/

/*
 * [ 스테이징 CD ]  pipeline No 2. ::: START
 */

[
  // 해당 pipeline Job 이름
  pipelineJobName  : jobFolderName + '/' + 'application-01-staging-cd' ,

  // 해당 pipeline Job 의 UI 출력명
  pipelineJobDisplayName  : '2-01. [ 스테이징 CD ] Application 이름' ,

  // 해당 pipeline Job description
  pipelineJobDescription  : '<br><h3>[ From ... ]</h3><h3>[ To ... ]</h3>' ,

  // DevOps Repository 내 해당 jenkinsfile 경로
  jenkinsfilePath  : "${JENKINSFILE_ROOT_PATH}/application-01/staging/cd",
  
  // DevOps Repository 내 해당 jenkinsfile 이름
  jenkinsfileName  : 'Jenkinsfile-application-01-staging-cd',

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
 * [ 스테이징 CD ]  pipeline No 2. ::: END
 */

/**************************************************
 * section-02 ::: 스테이징 CD ::: END
 **************************************************/

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
