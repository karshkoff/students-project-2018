def APP_HTTP_PORT = "80"
def HOST = "lab"

node {

  stage('Checkout') {
    deleteDir()
    checkout scm
  }

  stage('Deploy to staging') {

      try {
        sh "docker images -q | xargs docker rmi -f"
      } catch (error) {
      }

      echo "Deploy tag: ${env.IMAGE_TAG}"

      ansiblePlaybook colorized: true,
      limit: "${HOST}",
      credentialsId: 'ssh-key-jenkins',
      installation: 'ansible',
      inventory: 'ansible/hosts',
      playbook: 'ansible/playbook.yml',
      vaultCredentialsId: 'ansible_vault_credentials' 
  }

  stage('Acceptance tests') {

      exitCode = sh(returnStatus: true, script: "curl --silent --connect-timeout 15 --show-error --fail" +
        " http://$HOST:$APP_HTTP_PORT")

      if (exitCode !=0 ) {
        currentBuild.result = 'FAILED'
        sh "exit ${exitCode}"
    }
  }
}
