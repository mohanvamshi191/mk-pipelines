def common = new com.mirantis.mk.Common()
stage("Mirror") {
  node() {
    try{
      def branches = BRANCHES.tokenize(',')
      def pollBranches = []
      for (i=0; i < branches.size; i++) {
          pollBranches.add([name:branches[i]])
      }
      dir("source") {
        checkout changelog: true, poll: true,
          scm: [$class: 'GitSCM', branches: pollBranches, doGenerateSubmoduleConfigurations: false, submoduleCfg: [], 
          extensions: [[$class: 'CleanCheckout']], submoduleCfg: [], userRemoteConfigs: [[credentialsId: CREDENTIALS_ID, url: SOURCE_URL]]]
        common.mirrorGit(SOURCE_URL, TARGET_URL, CREDENTIALS_ID, BRANCHES)
      }
    } catch (Throwable e) {
       // If there was an error or exception thrown, the build failed
       currentBuild.result = "FAILURE"
       throw e
    } finally {
       common.sendNotification(currentBuild.result,"",["slack"])
    }
  }
}