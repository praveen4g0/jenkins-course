job('NodeJS Docker example') {
    scm {
        git('https://github.com/wardviaene/docker-demo.git') {  node -> // is hudson.plugins.git.GitSCM
            node / gitConfigName('DSL User')
            node / gitConfigEmail('jenkins-dsl@newtech.academy')
        }
    }
    triggers {
        scm('H/5 * * * *')
    }
//     wrappers {
//         nodejs('nodejs') // this is the name of the NodeJS installation in 
//                          // Manage Jenkins -> Configure Tools -> NodeJS Installations -> Name
//     }
    steps {
        dockerBuildAndPublish {
            repositoryName('praveen4g0/demo-docker-app')
            tag('${GIT_REVISION,length=9}')
            registryCredentials('dockerhub')
            forcePull(false)
            forceTag(false)
            createFingerprints(false)
            skipDecorate()
        }
//         shell("echo -n Testing published image")
//         shell("docker run -d -p 3000:3000 --name nodejs-app praveen4g0/demo-docker-app:v0.0.1")
//         shell("""
//                sleep 5 
//                url=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' nodejs-app)
//                curl http://$url:3000
//         """)
//         shell("echo -n Cleaning provisioned docker images")
//         shell("docker stop nodejs-app && docker rm nodejs-app")
    }
    
    steps {
       shell '''
       #!/usr/bin/env bash
       set -u -o pipefail

       clean() {
          echo "Exiting Running container.."
          docker stop nodejs-app

          docker rm nodejs-app
       }
       trap clean EXIT

       echo "Testing published image"
       docker run -d -p 3000:3000 --name nodejs-app praveen4g0/demo-docker-app:v0.0.1

       sleep 5s

       curl http://$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' nodejs-app):3000
       '''
    }
    
}
