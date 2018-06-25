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
        def pom = readMavenPom file: 'pom.xml'
 		def version = pom.version.replace("-SNAPSHOT", ".${currentBuild.number}")
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

    stage('Image Build'){
        imageBuild()
    }

    stage('Push to Docker Registry'){
        withCredentials([usernamePassword(credentialsId: 'dockerHubAccount', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
            pushToImage(USERNAME, PASSWORD)
        }
    }

    stage('Run App'){
        runApp("${IMAGE}", "${VERSION}", DOCKER_HUB_USER, HTTP_PORT)
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
        sh "docker stop ${pom.artifactId}"
    } catch(error){}
}

def imageBuild(){
    sh "docker build -t ${pom.artifactId}:${version}  -t ${pom.artifactId} --pull --no-cache ."
    echo "Image build complete"
}

def pushToImage(dockerUser, dockerPassword){
    sh "docker login -u $dockerUser -p $dockerPassword"
    sh "docker tag ${pom.artifactId}:${version} $dockerUser/${pom.artifactId}:${version}"
    sh "docker push $dockerUser/${pom.artifactId}:${version}"
    echo "Image push complete"
}

def runLocalApp(containerName, tag, httpPort){
    sh "docker run -d --rm -p $httpPort:$httpPort --name $containerName $containerName:$tag"
    echo "Application started on port: ${httpPort} (http)"
}

def runApp(containerName, tag, dockerHubUser, httpPort){
    sh "docker pull $dockerHubUser/$containerName"
    sh "docker run -d --rm -p $httpPort:$httpPort --name $containerName $dockerHubUser/$containerName:$tag"
    echo "Application started on port: ${httpPort} (http)"
}