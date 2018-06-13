def CONTAINER_NAME = "app"
def CONTAINER_TAG = "latest"
def DOCKER_HUB_USER = "karshkoff"
def APP_HTTP_PORT = "5000"

node {

	stage('Initialize') {
		def dockerHome = tool 'myDocker'
		env.PATH = "${dockerHome}/bin:${env.PATH}"
	}

	stage('Checkout') {
		deleteDir()
		checkout scm
	}

	stage('Build') {
		try {
			sh "docker rmi -f $CONTAINER_NAME"
		} catch (error) {
		}

		sh "docker build -t $CONTAINER_NAME:$CONTAINER_TAG --pull --no-cache ."
		echo "Image $CONTAINER_NAME build complete"
	}

	stage('Unit test') {
		try {
			sh "docker stop $CONTAINER_NAME"
		} catch (error) {
		}

		sh "docker run -d --rm -p $APP_HTTP_PORT:$APP_HTTP_PORT --name $CONTAINER_NAME $CONTAINER_NAME"
		sleep 5

		exitCode = sh(returnStatus: true, script: "docker exec app python /opt/greetings_app/test_selects.py")
		if (exitCode != 0) {
			currentBuild.result = 'FAILED'
			sh "exit ${exitCode}"
		}
	}

	stage('Push to dockerhub') {
		withCredentials([usernamePassword(credentialsId: 'dockerhub-karshkoff', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
			sh "docker login -u $USERNAME -p $PASSWORD"
			sh "docker tag $CONTAINER_NAME:$CONTAINER_TAG $DOCKER_HUB_USER/$CONTAINER_NAME:$CONTAINER_TAG"
			sh "docker push $DOCKER_HUB_USER/$CONTAINER_NAME:$CONTAINER_TAG"
			echo "Image push complete"
		}
	}
}
