/*
 * Base Image CI pipeline 등록 JOB DSL Script
 */

def jobFolderName = 'jenkins-pipeline-base-image'

folder(jobFolderName) {
  displayName('[CI/CD Practice] Base Image CI')
  description('Base Image CI Job Folder')
}

pipelineJob(jobFolderName + '/' + 'section-01') {
  displayName('1-00. ========== [ Base Image CI ] ==========')
  disabled()
}


def pipelineJobArgsList = [

/**************************************************
 * section-01 ::: START
 **************************************************/

/*
 * [ Base Image CI ]  pipeline No 1. ::: START
 */
[
  // 해당 pipeline Job 이름
  pipelineJobName  : jobFolderName + '/' + 'base-image-ci-01' ,

  // 해당 pipeline Job 의 UI 출력명
  pipelineJobDisplayName  : '1-01. [ Base Image CI ] Sample Job No. 1' ,

  // 해당 pipeline Job description
  pipelineJobDescription  : '<br><h3>[ From ... ]</h3><h3>[ To ... ]</h3>' ,

  // DevOps Repository 내 해당 jenkinsfile 경로
  jenkinsfilePath  : "${JENKINSFILE_ROOT_PATH}/base-image",

  // DevOps Repository 내 해당 jenkinsfile 이름
  jenkinsfileName  : 'Jenkinsfile-base-image-ci-01',

  // DevOps Repository URL
  scmRepositoryURL : "${DEVOPS_REPOSITORY_URL}",

  // DevOps Repository 내 해당 Jenkinsfile 등이 위치한 branch
  scmRepositoryTargetBranchName : "${DEVOPS_REPOSITORY_TARGET_BRANCH_NAME}",

  // DevOps Repository 연동 Jenkins credentialsId
  scmRepositoryCredentials : "${DEVOPS_REPOSITORY_GIT_CREDENTIALS_ID}"
],
/*
 * [ Base Image CI ]  pipeline No 1. ::: END
 */

/*
 * [ Base Image CI ]  pipeline No 2. ::: START
 */
[
  // 해당 pipeline Job 이름
  pipelineJobName  : jobFolderName + '/' + 'base-image-ci-02' ,

  // 해당 pipeline Job 의 UI 출력명
  pipelineJobDisplayName  : '1-02. [ Base Image CI ] Sample Job No. 3' ,

  // 해당 pipeline Job description
  pipelineJobDescription  : '<br><h3>[ From ... ]</h3><h3>[ To ... ]</h3>' ,

  // DevOps Repository 내 해당 jenkinsfile 경로
  jenkinsfilePath  : "${JENKINSFILE_ROOT_PATH}/base-image",

  // DevOps Repository 내 해당 jenkinsfile 이름
  jenkinsfileName  : 'Jenkinsfile-base-image-ci-02',

  // DevOps Repository URL
  scmRepositoryURL : "${DEVOPS_REPOSITORY_URL}",

  // DevOps Repository 내 해당 Jenkinsfile 등이 위치한 branch
  scmRepositoryTargetBranchName : "${DEVOPS_REPOSITORY_TARGET_BRANCH_NAME}",

  // DevOps Repository 연동 Jenkins credentialsId
  scmRepositoryCredentials : "${DEVOPS_REPOSITORY_GIT_CREDENTIALS_ID}"
],
/*
 * [ Base Image CI ]  pipeline No 2. ::: END
 */

/*
 * [ Base Image CI ]  pipeline No 3. ::: START
 */
[
  // 해당 pipeline Job 이름
  pipelineJobName  : jobFolderName + '/' + 'base-image-ci-03' ,

  // 해당 pipeline Job 의 UI 출력명
  pipelineJobDisplayName  : '1-02. [ Base Image CI ] Sample Job No. 3' ,

  // 해당 pipeline Job description
  pipelineJobDescription  : '<br><h3>[ From ... ]</h3><h3>[ To ... ]</h3>' ,

  // DevOps Repository 내 해당 jenkinsfile 경로
  jenkinsfilePath  : "${JENKINSFILE_ROOT_PATH}/base-image",

  // DevOps Repository 내 해당 jenkinsfile 이름
  jenkinsfileName  : 'Jenkinsfile-base-image-ci-03',

  // DevOps Repository URL
  scmRepositoryURL : "${DEVOPS_REPOSITORY_URL}",

  // DevOps Repository 내 해당 Jenkinsfile 등이 위치한 branch
  scmRepositoryTargetBranchName : "${DEVOPS_REPOSITORY_TARGET_BRANCH_NAME}",

  // DevOps Repository 연동 Jenkins credentialsId
  scmRepositoryCredentials : "${DEVOPS_REPOSITORY_GIT_CREDENTIALS_ID}"
],
/*
 * [ Base Image CI ]  pipeline No 3. ::: END
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
