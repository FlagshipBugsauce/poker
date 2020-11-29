import {AWSError, EC2, ElasticBeanstalk, ELB, S3} from "aws-sdk";
import {join} from 'path';
import {copyFileSync, existsSync, readFileSync, writeFileSync} from "fs";
import {execSync} from "child_process";
import {HeadObjectRequest} from "aws-sdk/clients/s3";
import {PromiseResult} from "aws-sdk/lib/request";
import {
  ApplicationDescriptionsMessage,
  ApplicationVersionDescriptionsMessage
} from "aws-sdk/clients/elasticbeanstalk";
import {DescribeInstancesResult} from "aws-sdk/clients/ec2";
import {DescribeAccessPointsOutput} from "aws-sdk/clients/elb";

/*
    TODO: So I don't forget...
      Need to figure out what security group settings need to be updated and what load balancer
      settings need to be updated in order to get websocket working. I changed too many things when
      I was trying to get this working so I need to pin down exactly which changes are needed. In
      particular, I allowed TCP connections to basically all ports in and out. This is probably
      some kind of security risk.
 */

// Finish with error exit code when error is thrown in an async function.
process.on('unhandledRejection', up => {
  throw up;
});

const run = (cmd: string, cwd = __dirname) => {
  console.log(cmd);
  execSync(cmd, {stdio: 'inherit', cwd: cwd});
};
const region: string = 'us-east-1';
const s3: S3 = new S3({region});
const elasticBeanstalk = new ElasticBeanstalk({region});
const ec2: EC2 = new EC2({region});
const elb: ELB = new ELB({region});

let version: string = process.env.BASE_APP_VERSION || '0.0.1';
const deploymentBucket: string = 'flagship-bugsauce-poker-bucket';
const artifactPath: () => string = (): string => `poker-${version}.jar`;
const appName: string = "flagship-bugsauce-poker";
const description: string = 'Online multiplayer poker application.';
const envName: string = 'bugsauce-poker';
const templateName: string = 'spring-template';
const cloudfrontConfigPath: string = join('miscellaneous', 'cloudfront', 'configs', 'elb.yml');
const cloudfrontPath: string = join('miscellaneous', 'cloudfront');

/**
 * Find the most recent version, increment it by 1 and assign the result to the version variable.
 */
function updateVersion(): Promise<void> {
  return elasticBeanstalk.describeApplicationVersions({ApplicationName: appName}).promise()
      .then((res: PromiseResult<ApplicationVersionDescriptionsMessage, AWSError>) => {
        const versions: string[] = res.ApplicationVersions
            .map(app => app.VersionLabel)
            .sort((a, b) => Number(b) - Number(a));
        if (versions.length === 0) return;
        const latest: string[] = versions[0].split('.');
        latest[2] = (Number(latest[2]) + 1).toString(); // Increment version by 1.
        version = latest.join('.');
      })
}

/**
 * Creates a new version of the application. If an application doesn't exist, it will be created.
 */
function createApplicationVersion(): Promise<any> {
  console.log('Creating a new application version');
  return updateVersion().then(() => elasticBeanstalk.createApplicationVersion({
    ApplicationName: appName,
    AutoCreateApplication: true,
    Description: description,
    Process: false,
    SourceBundle: {
      S3Bucket: deploymentBucket,
      S3Key: artifactPath()
    },
    VersionLabel: version
  }).promise());
}

/**
 * Creates a new Elastic Beanstalk environment.
 */
function createEnvironment(): Promise<any> {
  console.log('Creating Elastic Beanstalk environment.');
  return elasticBeanstalk.createEnvironment({
    ApplicationName: appName,
    Description: description,
    EnvironmentName: envName,
    SolutionStackName: '64bit Amazon Linux 2018.03 v2.11.1 running Java 8',
    VersionLabel: version,
    TemplateName: templateName
  }).promise();
}

/**
 * Creates a new Elastic Beanstalk configuration.
 */
function createConfiguration(): Promise<any> {
  console.log('Creating Elastic Beanstalk configuration.');
  return elasticBeanstalk.createConfigurationTemplate({
    ApplicationName: appName,
    OptionSettings: [
      {
        Namespace: 'aws:elasticbeanstalk:application:environment:',
        OptionName: 'SERVER_PORT',
        Value: '5000'
      },
      {
        Namespace: 'aws:elasticbeanstalk:environment:',
        OptionName: 'EnvironmentType',
        Value: 'SingleInstance'
      },
      {
        Namespace: 'aws:autoscaling:launchconfiguration:',
        OptionName: 'IamInstanceProfile',
        Value: 'aws-elasticbeanstalk-ec2-role'
      }
    ],
    TemplateName: templateName,
    SolutionStackName: '64bit Amazon Linux 2018.03 v2.11.1 running Java 8'
  }).promise();
}

/**
 * Uploads Maven package to S3 deployment bucket.
 */
function uploadArtifact(): Promise<any> {
  return s3.listObjects({Bucket: deploymentBucket}).promise().catch(() => {
    console.log('Creating deployment bucket.');
    return s3.createBucket({Bucket: deploymentBucket}).promise()
  }).then(() => {
    // Check if artifact exists, if not, create the artifact.
    if (!existsSync(artifactPath())) {
      console.log('Creating Maven package.');
      buildPokerServer();
    }
    const params: HeadObjectRequest = {Bucket: deploymentBucket, Key: artifactPath()};
    return s3.headObject(params).promise().then(() => false).catch(() => true);
  }).then(res => {
    if (res) {
      console.log('Uploading Maven package to deployment bucket.');
      return s3.putObject({
        Bucket: deploymentBucket,
        Key: artifactPath(),
        Body: readFileSync(artifactPath())
      }).promise();
    } else {
      return null;
    }
  });
}

/**
 * Checks if the application exists in Elastic Beanstalk. Returns a boolean promise.
 */
function applicationExists(): Promise<boolean> {
  console.log('Checking if application exists.');
  return elasticBeanstalk.describeApplications({ApplicationNames: [appName]}).promise()
      .then((res: PromiseResult<ApplicationDescriptionsMessage, AWSError>) =>
          !(res.$response.error ||
              !res.$response.data ||
              !res.Applications ||
              res.Applications.length === 0))
      .catch(e => {
        console.error(e);
        return false;
      });
}

/**
 * Updates the environment to use the specified version of the application.
 */
function updateEnvironment(): Promise<any> {
  console.log('Updating environment to use new application version.');
  return elasticBeanstalk.updateEnvironment({
    ApplicationName: appName,
    EnvironmentName: envName,
    VersionLabel: version
  }).promise();
}

/**
 * Retrieves the ELB domain which is needed for serverless lambda@edge configuration.
 */
function getElbDomain(): Promise<string> {
  // TODO: May need to pause here for several minutes until the environment is setup... Not sure...
  return ec2.describeInstances().promise()
      .then((result: PromiseResult<DescribeInstancesResult, AWSError>) =>
          result.Reservations
              .map(r => r.Instances)
              .find(i => i[0].Tags.find(t => t.Value === envName))[0].InstanceId)
      .then((id: string) =>
          elb.describeLoadBalancers().promise()
              .then((result: PromiseResult<DescribeAccessPointsOutput, AWSError>) =>
                  result.LoadBalancerDescriptions
                      .find(elb => elb.Instances
                          .find(i => i.InstanceId === id)).DNSName));
}


function createCloudfrontConfig(domain: string) {
  writeFileSync(cloudfrontConfigPath, `domainName: ${domain}`);
}

function deployCloudfront() {
  run('npx serverless deploy', cloudfrontPath)
}

/**
 * Deploys a new version of the application to an existing environment.
 */
function deployNewVersion(): Promise<any> {
  return createApplicationVersion().then(updateEnvironment);
}

/**
 * Creates a new application, configuration template and environment. This will executed when no
 * application with the provided name exists.
 */
function deployNewApplication(): Promise<any> {
  return createApplicationVersion()
      .then(createConfiguration)
      .then(createEnvironment)
}


/**
 * Deploys the application to Elastic Beanstalk.
 */
function deploy(): Promise<any> {
  return updateVersion()
      .then(uploadArtifact)
      .then(applicationExists)
      .then(exists => exists ? deployNewVersion() : deployNewApplication())
      .then(getElbDomain)
      .then(createCloudfrontConfig)
      .then(deployCloudfront);
}

/**
 * Builds the Maven package of the application.
 */
function buildPokerServer(): void {
  const pomPath: string = join('..', '..', 'poker-server', 'pom.xml');
  const jarPath: string = join('poker-server', 'target', 'poker-0.0.1.jar');
  run(`mvn package -f ${pomPath}`);
  copyFileSync(jarPath, artifactPath());
}

(async () => await deploy().then(getElbDomain).then(createCloudfrontConfig).catch(e => {
  console.error(e);
  throw e;
}))();
