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
		sh "docker stop $CONTAINER_NAME"
		sh "docker run -d --rm -p $APP_HTTP_PORT:$APP_HTTP_PORT --name $CONTAINER_NAME $CONTAINER_NAME"
	}

	stage('Push to dockerhub') {
		echo "Push to dockerhub"
	}
}
