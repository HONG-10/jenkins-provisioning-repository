/*
 * Jenkins schedule 관련 pipeline 등록 JOB DSL Script
 */

def jobFolderName = 'jenkins-pipeline-schedule'

folder(jobFolderName) {
  displayName('[CI/CD Practice] Schedule Job')
  description('정기 실행 Schedule Job Folder')
}

pipelineJob(jobFolderName + '/' + 'section-01') {
  displayName('1-00. ========== [ 정기 실행 Schedule Job ] ==========')
  disabled()
}


def pipelineJobArgsList = [

/**************************************************
 * section-01 ::: START
 **************************************************/

/*
 * [ 정기 실행 schedule Job ]  pipeline No 1. ::: START
 */
[
  // 해당 pipeline Job 이름
  pipelineJobName  : jobFolderName + '/' + 'schedule-01' ,

  // 해당 pipeline Job 의 UI 출력명
  pipelineJobDisplayName  : '1-01. [ 정기 실행 Schedule Job ] Sample Job No. 1' ,

  // 해당 pipeline Job description
  pipelineJobDescription  : '<br><h3>Jenkins Schedule Job ::: No. 1</h3>' ,

  // DevOps Repository 내 해당 jenkinsfile 경로
  jenkinsfilePath  : "${JENKINSFILE_ROOT_PATH}/schedule",

  // DevOps Repository 내 해당 jenkinsfile 이름
  jenkinsfileName  : 'Jenkinsfile-schedule-01',

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
 * [ 정기 실행 schedule Job ]  pipeline No 1. ::: END
 */

/*
 * [ 정기 실행 schedule Job ]  pipeline No 2. ::: START
 */
[
  // 해당 pipeline Job 이름
  pipelineJobName  : jobFolderName + '/' + 'schedule-02' ,

  // 해당 pipeline Job 의 UI 출력명
  pipelineJobDisplayName  : '1-02. [ 정기 실행 schedule Job ] Sample Job No. 2' ,

  // 해당 pipeline Job description
  pipelineJobDescription  : '<br><h3>Jenkins Schedule Job ::: No. 2</h3>' ,

  // DevOps Repository 내 해당 jenkinsfile 경로
  jenkinsfilePath  : "${JENKINSFILE_ROOT_PATH}/schedule",

  // DevOps Repository 내 해당 jenkinsfile 이름
  jenkinsfileName  : 'Jenkinsfile-schedule-02',

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
 * [ 정기 실행 schedule Job ]  pipeline No 2. ::: END
 */

/*
 * [ 정기 실행 schedule Job ]  pipeline No 3. ::: START
 */
[
  // 해당 pipeline Job 이름
  pipelineJobName  : jobFolderName + '/' + 'schedule-03' ,

  // 해당 pipeline Job 의 UI 출력명
  pipelineJobDisplayName  : '1-03. [ 정기 실행 Schedule Job ] Sample No. 3 Job' ,

  // 해당 pipeline Job description
  pipelineJobDescription  : '<br><h3>Jenkins Schedule Job ::: No. 3</h3>' ,

  // DevOps Repository 내 해당 jenkinsfile 경로
  jenkinsfilePath  : "${JENKINSFILE_ROOT_PATH}/schedule",

  // DevOps Repository 내 해당 jenkinsfile 이름
  jenkinsfileName  : 'Jenkinsfile-schedule-03',

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
 * [ 정기 실행 schedule Job ]  pipeline No 3. ::: END
 */

/**************************************************
 * section-1 ::: END
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
