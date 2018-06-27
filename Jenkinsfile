def CONTAINER_NAME="jenkins-pipeline"
def CONTAINER_TAG="latest"
def DOCKER_HUB_USER="mychawki"
def HTTP_PORT="9999"


  
node {

	
    stage('Initialize'){
        def dockerHome = tool 'myDocker'
        def mavenHome  = tool 'myMaven'
        env.PATH = "${dockerHome}/bin:${mavenHome}/bin:${env.PATH}"
        
    }

    stage('Checkout') {
        checkout scm
        
 		//def version = pom.version.replace("-SNAPSHOT", ".${currentBuild.number}")
 		def pom = readMavenPom file: 'pom.xml'
        print "Build: " + pom.version
        env.POM_VERSION = pom.version
        env.POM_ARTIFACT = pom.artifactId
    }
    
    stage("Set Version") {
      echo "Start Set Version Stage"
      getVersions()
      echo "Original major version ${env.NEW_VERSION}"
      
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

    stage("Image Prune"){
        imagePrune()
    }

   // stage('Image Build'){
   //     imageBuild()
   // }

    stage('Push to Docker Registry'){
        withCredentials([usernamePassword(credentialsId: 'dockerHubAccount', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
            pushToImage(USERNAME, PASSWORD)
        }
    }

    stage('Run App'){
        runApp(DOCKER_HUB_USER, HTTP_PORT)
        //runLocalApp(CONTAINER_NAME, CONTAINER_TAG, HTTP_PORT)
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

def runLocalApp(containerName, tag, httpPort){
    sh "docker run -d --rm -p $httpPort:$httpPort --name ${env.POM_ARTIFACT} ${env.POM_ARTIFACT}:${env.POM_VERSION}"
    echo "Application started on port: ${httpPort} (http)"
}

def runApp(dockerHubUser, httpPort){
    sh "docker pull $dockerHubUser/${env.POM_ARTIFACT}:${env.POM_VERSION}"
    sh "docker run -d --rm -p $httpPort:$httpPort --name ${env.POM_ARTIFACT} $dockerHubUser/${env.POM_ARTIFACT}:${env.POM_VERSION}"
    echo "Application started on port: ${httpPort} (http)"
}

def getVersions() {
	def version = "${env.POM_VERSION}".toString().split('.')
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