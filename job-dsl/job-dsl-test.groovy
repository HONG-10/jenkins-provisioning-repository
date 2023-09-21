/*
 * Test 전용 pipeline 등록 JOB DSL Script
 */

def jobFolderName = 'jenkins-pipeline-test'

folder(jobFolderName) {
  displayName('[CI/CD Practice] Test Pipeline')
  description('Test Pipeline Folder')
}

pipelineJob(jobFolderName + '/' + 'section-01') {
  displayName('1-00. ========== [ Test ] ==========')
  disabled()
}

def pipelineJobArgsList = [

/**************************************************
 * section-01 ::: START
 **************************************************/

/*
 * [ TEST ]  Test pipeline 1. ::: START
 */
[
  // 해당 pipeline Job 이름
  pipelineJobName  : jobFolderName + '/' + 'test-01' ,

  // 해당 pipeline Job 의 UI 출력명
  pipelineJobDisplayName  : '1-01. [ TEST ] Jenkinsfile-test-01 실행' ,

  // 해당 pipeline Job description
  pipelineJobDescription  : '<br><h3>[ Jenkinsfile-test-01 실행 ]</h3>' ,

  // DevOps Repository 내 해당 jenkinsfile 경로
  jenkinsfilePath  : "${JENKINSFILE_ROOT_PATH}/test",
  
  // DevOps Repository 내 해당 jenkinsfile 이름
  jenkinsfileName  : 'Jenkinsfile-test-01',

  // DevOps Repository URL
  scmRepositoryURL : "${DEVOPS_REPOSITORY_URL}",

  // DevOps Repository 내 해당 Jenkinsfile 등이 위치한 branch
  scmRepositoryTargetBranchName : "${DEVOPS_REPOSITORY_TARGET_BRANCH_NAME}",

  // DevOps Repository 연동 Jenkins credentialsId
  scmRepositoryCredentials : "${DEVOPS_REPOSITORY_GIT_CREDENTIALS_ID}",

  // 해당 Job 의 실행 이력 유지 기간(일)
  buildLogKeepDays : 1,

  // 해당 Job 의 보관할 실행 이력 최대갯수
  buildLogKeepNum  : 3
],
/*
 * [ TEST ]  Test pipeline 1. ::: END
 */

/*
 * [ TEST ]  Test pipeline 2. ::: START
 */
[
  // 해당 pipeline Job 이름
  pipelineJobName  : jobFolderName + '/' + 'test-02' ,

  // 해당 pipeline Job 의 UI 출력명
  pipelineJobDisplayName  : '1-02. [ TEST ] Jenkinsfile-test-02 실행' ,

  // 해당 pipeline Job description
  pipelineJobDescription  : '<br><h3>[ Jenkinsfile-test-02 실행 ]</h3>' ,

  // DevOps Repository 내 해당 jenkinsfile 경로
  jenkinsfilePath  : "${JENKINSFILE_ROOT_PATH}/test",
  
  // DevOps Repository 내 해당 jenkinsfile 이름
  jenkinsfileName  : 'Jenkinsfile-test-02',

  // DevOps Repository URL
  scmRepositoryURL : "${DEVOPS_REPOSITORY_URL}",

  // DevOps Repository 내 해당 Jenkinsfile 등이 위치한 branch
  scmRepositoryTargetBranchName : "${DEVOPS_REPOSITORY_TARGET_BRANCH_NAME}",

  // DevOps Repository 연동 Jenkins credentialsId
  scmRepositoryCredentials : "${DEVOPS_REPOSITORY_GIT_CREDENTIALS_ID}",

  // 해당 Job 의 실행 이력 유지 기간(일)
  buildLogKeepDays : 1,

  // 해당 Job 의 보관할 실행 이력 최대갯수
  buildLogKeepNum  : 3
],
/*
 * [ TEST ]  Test pipeline 2. ::: END
 */

/*
 * [ TEST ]  Test pipeline 3. ::: START
 */
[
  // 해당 pipeline Job 이름
  pipelineJobName  : jobFolderName + '/' + 'test-03' ,

  // 해당 pipeline Job 의 UI 출력명
  pipelineJobDisplayName  : '1-03. [ TEST ] Jenkinsfile-test-03 실행' ,

  // 해당 pipeline Job description
  pipelineJobDescription  : '<br><h3>[ Jenkinsfile-test-03 실행 ]</h3>' ,

  // DevOps Repository 내 해당 jenkinsfile 경로
  jenkinsfilePath  : "${JENKINSFILE_ROOT_PATH}/test",
  
  // DevOps Repository 내 해당 jenkinsfile 이름
  jenkinsfileName  : 'Jenkinsfile-test-03',

  // DevOps Repository URL
  scmRepositoryURL : "${DEVOPS_REPOSITORY_URL}",

  // DevOps Repository 내 해당 Jenkinsfile 등이 위치한 branch
  scmRepositoryTargetBranchName : "${DEVOPS_REPOSITORY_TARGET_BRANCH_NAME}",

  // DevOps Repository 연동 Jenkins credentialsId
  scmRepositoryCredentials : "${DEVOPS_REPOSITORY_GIT_CREDENTIALS_ID}",

  // 해당 Job 의 실행 이력 유지 기간(일)
  buildLogKeepDays : 1,

  // 해당 Job 의 보관할 실행 이력 최대갯수
  buildLogKeepNum  : 3
],
/*
 * [ TEST ]  Test pipeline 3. ::: END
 */

/**************************************************
 * section-01 ::: END
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
