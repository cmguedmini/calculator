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
      withSonarQubeEnv('Sonar-6') {
         sh ${mvnHome}/bin/mvn package clean package sonar:sonar"
      }         
   }
   stage('Email Notification'){
      mail bcc: '', body: '''Hi Welcome to jenkins email alerts
	  Thanks,
      Devops Team''', cc: '', from: '', replyTo: '', subject: 'Jenkins Job', to: 'c.mguedmini@roam-smart.com'
   }
}