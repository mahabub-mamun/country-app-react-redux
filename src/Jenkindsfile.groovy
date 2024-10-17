Groovy
pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'npm install'
                sh 'npm run build'
            }
        }

        stage('Upload Artifacts to S3') {
            steps {
                withCredentials(credentialsId: '94abe4ae-5b93-485b-96c7-a28ef184b2c2') {
                    sh 'aws s3 cp build/ tashdid-rnd/artifacts/'
                    sh 'aws s3 cp dockerfile/Dockerfile s3://tashdid-rnd/dockerfile/'
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                withCredentials(credentialsId: '94abe4ae-5b93-485b-96c7-a28ef184b2c2') {
                    sh "ssh -i omnierp.pem ubuntu@ec2-15-206-91-177.ap-south-1.compute.amazonaws.com 'aws s3 cp s3://tashdid-rnd/artifacts/test.png /home/ubuntu'"
                    sh "ssh -i omnierp.pem ubuntu@ec2-15-206-91-177.ap-south-1.compute.amazonaws.com 'docker pull your-image-name'"
                    sh "ssh -i omnierp.pem ubuntu@ec2-15-206-91-177.ap-south-1.compute.amazonaws.com 'docker run -d -p 80:3000 your-image-name'"
                }
            }
        }
    }
}