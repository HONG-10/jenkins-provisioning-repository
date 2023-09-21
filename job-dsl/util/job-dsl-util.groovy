/*
 * Pipeline 기본 정의 Class
 * 
 * @see jenkins-casc-config.yaml
 */

/*
 * CI/CD pipeline 생성 Method
 */
def createPipelineJob(def dslFactory, Map args) {

  dslFactory.pipelineJob( args.pipelineJobName ) {

    displayName( args.pipelineJobDisplayName )
    description( args.pipelineJobDescription )

    properties {
      // "Do not allow concurrent builds" 는 기본적으로 항상 활성화
      if( args.disableConcurrentBuilds == null || args.disableConcurrentBuilds == true ) {
        disableConcurrentBuilds()
      }
    }

    logRotator {
      artifactDaysToKeep( args.artifactKeepDays ?: 0 )
      artifactNumToKeep( args.artifactKeepNum ?: 0 )
      daysToKeep( args.buildLogKeepDays ?: 15 )
      numToKeep( args.buildLogKeepNum ?: 30 )
    }

    definition {
      cpsScm {
        scm {
          scriptPath( args.jenkinsfilePath + '/' + args.jenkinsfileName )
          // lightweight(true)

          git {
            remote {
              url( args.scmRepositoryURL )
              credentials( args.scmRepositoryCredentials )
            }

            branches( args.scmRepositoryTargetBranchName )

            extensions {
              cleanBeforeCheckout()
              wipeOutWorkspace()
              cloneOptions {
                noTags(true)
                shallow(true)
                depth(1)
                timeout(30)
              }
            }
            
            // 해당 Jenkinsfile 이 위치한 경로만 clond 되도록 sparseCheckout 설정
            configure { git ->
              git / 'extensions' / 'hudson.plugins.git.extensions.impl.SparseCheckoutPaths' / 'sparseCheckoutPaths' {
                [ args.jenkinsfilePath ].each { checkoutPath ->
                  'hudson.plugins.git.extensions.impl.SparseCheckoutPath' {
                    path("${checkoutPath}")
                  }
                }

                // sparseCheckout 경로 추가 로직 : 특별히 사용하지는 않음
                if( args.additionalFilePath != null && args.additionalFilePath instanceof Collection ) {

                  args.additionalFilePath.each { checkoutPath ->
                    'hudson.plugins.git.extensions.impl.SparseCheckoutPath' {
                      path("${checkoutPath}")
                    }
                  }

                }   // End of 'if'

              }  // End of 'git extensions'
            } // End of 'configure'
          }   // End of 'git'
        }   // End of 'scm'
      }   // End of 'cpsScm'
    }   // End of 'definition'
  }   // End of 'pipelineJob'
}   // End of 'createPipelineJob'

