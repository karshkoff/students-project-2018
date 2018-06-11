pipelineJob("CI_job") {

	displayName('greetings app CI')

	triggers {
		scm('*/5 * * * *')
	}

	definition {
		cpsScm {
			scm {
				git {
					remote {
						url('https://github.com/karshkoff/students-project-2018.git')
						credentials('github-karshkoff')
					}
					branch('master')
				}
			}
			scriptPath('jenkins/CI_job.groovy')
		}
	}
}

pipelineJob("CD_job") {

	displayName('greetings app CD')

	triggers {
		upstream('CI_job', 'SUCCESS')
	}

	definition {
		cpsScm {
			scm {
				git {
					remote {
						url('https://github.com/karshkoff/students-project-2018.git')
						credentials('github-karshkoff')
					}
					branch('master')
				}
			}
			scriptPath('jenkins/CD_job.groovy')
		}
	}
}