def CONTAINER_NAME="jenkins-pipeline"
def CONTAINER_TAG="latest"
def DOCKER_HUB_USER="mychawki"
def HTTP_PORT="9999"


  
node {
	concurrency: 1
	sh "git config --global --replace-all user.email chawki.mguedmini@gmail.com"
	sh "git config --global --replace-all user.name cmguedmini"
	
	stage('Clean') {
        deleteDir()
    }
    
    stage('Initialize'){
        def dockerHome = tool 'myDocker'
        def mavenHome  = tool 'myMaven'
        env.PATH = "${dockerHome}/bin:${mavenHome}/bin:${env.PATH}"    
        env.CURRENT_BRANCH = env.BRANCH_NAME    
    }
    
    

    stage('Checkout') {
        checkout scm
        echo "Current Branch ${env.CURRENT_BRANCH}"
        sh 'git config credential.helper "/bin/bash ' + env.WORKSPACE + '/credential-helper.sh"'
 		//def version = pom.version.replace("-SNAPSHOT", ".${currentBuild.number}")
 		def pom = readMavenPom file: 'pom.xml'
        print "Build: " + pom.version
        env.POM_VERSION = pom.version
        env.POM_ARTIFACT = pom.artifactId
    }
    
    stage('Merge') {
		steps {
			script { 
				if (env.BRANCH_NAME != 'master' && env.BRANCH_NAME != 'prod') {
					echo 'This is not master or staging'
					withCredentials([[
						$class: 'UsernamePasswordMultiBinding',
						credentialsId: 'gitHubAccount',
						usernameVariable: 'GIT_USERNAME',
						passwordVariable: 'GIT_PASSWORD'
					]]) {
							echo "Current Branch ${env.CURRENT_BRANCH}"
							sh "git fetch origin \"+refs/heads/*:refs/remotes/origin/*\""
							sh "git checkout -b ${env.CURRENT_BRANCH} origin/${env.CURRENT_BRANCH}"
							sh "git checkout master"
							sh "git pull"
							sh "git merge origin/${env.CURRENT_BRANCH}"
						}
				} else {
				echo 'Nothing to Do --------------------'
				}
			}
		}   	
    }
    
    stage('Build'){
        sh "mvn clean install"
    }
    
    stage('Sonar'){
        try {
            sh "mvn sonar:sonar"
        } catch(error){
            echo "The sonar server could not be reached ${error}"
        }
     }
    
   /* stage('Tag') {
        

        withCredentials([[
            $class: 'UsernamePasswordMultiBinding',
            credentialsId: 'gitHubAccount',
            usernameVariable: 'GIT_USERNAME',
            passwordVariable: 'GIT_PASSWORD'
        ]]) {
        	//sh "git fetch --unshallow || true"
            sh "git fetch origin \"+refs/heads/*:refs/remotes/origin/*\""
    		sh "git checkout -b ${env.BRANCH_NAME} origin/${env.BRANCH_NAME}"
            getVersions()
      		echo "New version ${env.NEW_VERSION} for Branch ${env.BRANCH_NAME}"
      		sh "mvn -B versions:set -DgenerateBackupPoms=false -DnewVersion=${env.NEW_VERSION}"
      		
      		echo "Commit and push branch"
      		sh "git commit -am \"New release candidate ${env.NEW_VERSION}\""
	        sh "git tag -a ${env.NEW_VERSION} -m \"New Tag for release candidate ${env.NEW_VERSION}\""
	        //sh "git push https://${USERNAME_GIT}:${PASSWORD_GIT}/github.com/calculator.git --tags"
	        sh "git push origin  ${env.BRANCH_NAME} --tags"
        }
    }*/

    
	
   /* stage('Push to Docker Registry'){
        withCredentials([usernamePassword(credentialsId: 'dockerHubAccount', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
            pushToImage(USERNAME, PASSWORD)
        }
    }*/
    

    stage("Image Prune"){
        imagePrune()
    }
    
    stage('Image Build'){
        imageBuild()
    }
    
    stage('Run App'){
        //runApp(DOCKER_HUB_USER, HTTP_PORT)
        runLocalApp(HTTP_PORT)
    }
    
    stage('Push') {
    	steps {
			script { 
				if (env.BRANCH_NAME != 'master' && env.BRANCH_NAME != 'prod') {
					echo 'This is not master or staging'
			    	withCredentials([[
			            $class: 'UsernamePasswordMultiBinding',
			            credentialsId: 'gitHubAccount',
			            usernameVariable: 'GIT_USERNAME',
			            passwordVariable: 'GIT_PASSWORD'
			        ]]) {
			            sh "git checkout master"
			            sh "git push origin master"
			            sh "git push origin --delete ${env.CURRENT_BRANCH}"
			        }
			    }
		   }
	    }
    }
    
    

   


    
    
    stage('Email Notification'){
      mail bcc: '', body: '''Hi Welcome to jenkins email alerts
	  Thanks,
      Devops Team''', cc: '', from: '', replyTo: '', subject: 'Jenkins Job', to: 'c.mguedmini@roam-smart.com'
   }

}

def imagePrune(){
    try {
        sh "docker image prune -f"
        sh "docker stop ${env.POM_ARTIFACT}"
    } catch(error){
    	echo "Image Prune error: ${error}"
    }
}

def imageBuild(){
    try {
    	sh "docker build -t ${env.POM_ARTIFACT}:${env.POM_VERSION} --pull --no-cache ."
    	echo "Image build complete"
    } catch(error){
    	echo "Image Build error: ${error}"
    }
}

def pushToImage(dockerUser, dockerPassword){
    try {
		sh "docker build -t $dockerUser/${env.POM_ARTIFACT}:${env.POM_VERSION} --pull --no-cache ."
		echo "Image build complete"
	    sh "docker login -u $dockerUser -p $dockerPassword"
	    //sh "docker tag ${env.POM_ARTIFACT}:${env.POM_VERSION} $dockerUser/${env.POM_ARTIFACT}:${env.POM_VERSION}"
	    sh "docker push $dockerUser/${env.POM_ARTIFACT}:${env.POM_VERSION}"
	    echo "Image push complete"
	    } catch(error){
    	echo "Image Build/Push error: ${error}"
    }
}

def pushToRemoteGit() {
	sh "git push origin HEAD:${env.BRANCH_NAME} --follow-tags"
    sh "git tag v${env.NEW_VERSION}"
}

def runLocalApp(httpPort){
    sh "docker run -d --rm -p $httpPort:$httpPort --name ${env.POM_ARTIFACT} ${env.POM_ARTIFACT}:${env.POM_VERSION}"
    echo "Application started on port: ${httpPort} (http)"
}

def runApp(dockerHubUser, httpPort){
    sh "docker pull $dockerHubUser/${env.POM_ARTIFACT}:${env.POM_VERSION}"
    sh "docker run -d --rm -p $httpPort:$httpPort --name ${env.POM_ARTIFACT} $dockerHubUser/${env.POM_ARTIFACT}:${env.POM_VERSION}"
    echo "Application started on port: ${httpPort} (http)"
}

def getVersions() {
	def version = env.POM_VERSION.split(/[.]/)
	echo "Version ${version}"
	def major = version[0];
	def minor = version[1];
    echo "Original minor version ${minor}"
    def patch  = Integer.parseInt(version[2]) + 1;
    echo "Original patch version ${patch}"
    env.NEW_VERSION = "${major}.${minor}.${patch}";
    
    if (env.NEW_VERSION) {
      echo "Building version ${env.NEW_VERSION}"
    }
}