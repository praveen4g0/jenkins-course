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
        shell("echo -n Testing published image")
        shell("docker run -d -p 3000:3000 --name nodejs-app praveen4g0/demo-docker-app:v0.0.1")
        shell("sleep 5")
        shell("curl http://$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' nodejs-app):3000")
        shell("echo -n Cleaning provisioned docker images")
        shell("docker stop nodejs-app && docker rm nodejs-app")
    }
}
