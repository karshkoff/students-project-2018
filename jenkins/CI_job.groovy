def CONTAINER_NAME = "app"
def CONTAINER_TAG = "latest"
def DOCKER_HUB_USER = "karshkoff"
def APP_HTTP_PORT = "5000"
def IMAGE_NAME = DOCKER_HUB_USER + "/" + CONTAINER_NAME + ":" + CONTAINER_TAG

node {

	stage('Initialize') {
		CONTAINER_TAG = sh(returnStdout: true, script: "git describe --tags 2>/dev/null").trim()
		echo "Build tag: $CONTAINER_TAG"

        if (CONTAINER_TAG == '') {
        	currentBuild.result = 'FAILED'
			sh "exit ${exitCode}"    
        }
		def dockerHome = tool 'myDocker'
		env.PATH = "${dockerHome}/bin:${env.PATH}"
	}

	stage('Checkout') {
		deleteDir()
		checkout scm
	}

	stage('Build') {
		try {
			sh 'docker rmi "$(docker images -q)"'
		} catch (error) {
		}

		sh "docker build -t $IMAGE_NAME --pull --no-cache ."
		echo "Image $IMAGE_NAME build complete"
	}

	stage('Unit test') {
		try {
			sh "docker stop $CONTAINER_NAME"
		} catch (error) {
		}

		sh "docker run -d --rm -p $APP_HTTP_PORT:$APP_HTTP_PORT --name $CONTAINER_NAME $IMAGE_NAME"
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
			sh "docker tag $IMAGE_NAME $IMAGE_NAME"
			sh "docker push $IMAGE_NAME"
			echo "Image $IMAGE_NAME push complete"
		}
	}
}
