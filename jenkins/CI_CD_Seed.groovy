def gitUrl = 'https://github.com/karshkoff/students-project-2018.git'

pipelineJob("CI_job") {

	displayName('greetings app CI')

	triggers {
		scm('H/5 * * * *')
	}

	definition {
		cpsScm {
			scm {
				git {
					remote {
						url(gitUrl)
						credentials('github-karshkoff')
					}
					branch('refs/tags/*')
				}
			}
			scriptPath('jenkins/CI_job.groovy')
		}
	}
}

pipelineJob("CD_job") {

	displayName('greetings app CD')

	parameters {
		gitParameterDefinition {
			name('IMAGE_TAG')
			branch('refs/tags/*')
			branchFilter('.*')
	    	defaultValue('latest')
	    	listSize('0')
	    	selectedValue('TOP')
	    	sortMode('DESCENDING_SMART')
	    	type('PT_TAG')
			description('')
			tagFilter('*')
			useRepository(gitUrl)
			quickFilterEnabled(false)
	    }
	}

	triggers {
		upstream('CI_job', 'SUCCESS')
	}

	definition {
		cpsScm {
			scm {
				git {
					remote {
						url(gitUrl)
						credentials('github-karshkoff')
					}
					branch('refs/tags/*')
				}
			}
			scriptPath('jenkins/CD_job.groovy')
		}
	}
}
