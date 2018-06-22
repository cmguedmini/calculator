node{
   stage('SCM Checkout'){
     git 'https://github.com/cmguedmini/calculator.git'
   }
   stage('Compile-Package'){
      // Get maven home path
      def mvnHome =  tool name: 'M3', type: 'maven'   
      sh "${mvnHome}/bin/mvn package"
   }
   
   stage('SonarQube analysis') {
	  def mvnHome =  tool name: 'M3', type: 'maven'

		sh "${mvnHome}/bin/mvn sonar:sonar -Dsonar.host.url=http://192.168.2.4:9000 -Dsonar.login=5435b3e7ae838a430a19d0b6f289dc5ab1147406"        
   }
   stage('Email Notification'){
      mail bcc: '', body: '''Hi Welcome to jenkins email alerts
	  Thanks,
      Devops Team''', cc: '', from: '', replyTo: '', subject: 'Jenkins Job', to: 'c.mguedmini@roam-smart.com'
   }
}