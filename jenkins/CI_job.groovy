node {

	stage('Initialize') {
		echo 'Test init stage'
		echo env
	}

	stage('Checkout') {}
	stage('Build') {}
	stage('Unit test') {}
	stage('Push to dockerhub') {}

}
