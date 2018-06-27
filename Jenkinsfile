#!groovy​

node {

    def version
    def webAppTarget = "xxx"
    def sourceBranch = "master"
    def releaseBranch = "feature3"
    def nexusBaseRepoUrl = "http://xxx"
    def repositoryUrl = "http://xxx"
    def gitCredentialsId = "xxx"
    def nexusRepositoryId = "xxx"
    def configFileId = "xxx"
    def mvnHome = tool 'myMaven'

    def updateQAVersion = {
        def split = version.split('\\.')
        //always remove "-SNAPSHOT"
        split[2] = split[2].split('-SNAPSHOT')[0]
        //increment the middle number of version by 1
        split[1] = Integer.parseInt(split[1]) + 1
        //reset the last number to 0
        split[2] = 0
        version = split.join('.')
    }

    //FIXME: use SSH-Agent
   //FIXME: use SSH-Agent

sh "git config --replace-all credential.helper cache"
sh "git config --global --replace-all user.email chawkimguedmini@gmail.com git config --global --replace-all user.name cmguedmini"

configFileProvider([configFile(fileId: "${configFileId}", variable: "MAVEN_SETTINGS")]) {

    stage('Clean') {
        deleteDir()
    }

    dir('qa') {
        stage('Checkout QA') {
                echo 'Load from GIT'
                checkout scm
       }

            stage('Increment QA version') {
                version = sh(returnStdout: true, script: "${mvnHome}/bin/mvn -q -N org.codehaus.mojo:exec-maven-plugin:1.3.1:exec -Dexec.executable='echo' -Dexec.args='\${project.version}'").toString().trim()
                echo 'Old Version:'
                echo version
                updateQAVersion()
                echo 'New Version:'
                echo version
            }

            stage('Set new QA version') {
                echo 'Clean Maven'
                sh "${mvnHome}/bin/mvn -B clean -s '$MAVEN_SETTINGS'"

                echo 'Set new version'
                sh "${mvnHome}/bin/mvn -B versions:set -DnewVersion=${version}"
            }

            stage('QA Build') {
                echo 'Execute maven build'
                sh "${mvnHome}/bin/mvn -B install -s '$MAVEN_SETTINGS'"
            }

            stage('Push new QA version') {
                echo 'Commit and push branch'
                sh "git commit -am \"New release candidate ${version}\""
                sh "git push origin ${releaseBranch}"
            }

            stage('Push new tag') {
                echo 'Tag and push'
                sh "git tag -a ${version} -m 'release tag'"
                sh "git push origin ${version}"
            }
        }
    }
}