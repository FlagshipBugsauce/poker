import {AWSError, ElasticBeanstalk, S3} from "aws-sdk";
import {join} from 'path';
import {copyFileSync, existsSync, readFileSync} from "fs";
import {execSync} from "child_process";
import {HeadObjectRequest} from "aws-sdk/clients/s3";
import {PromiseResult} from "aws-sdk/lib/request";
import {
  ApplicationDescriptionsMessage,
  ApplicationVersionDescriptionsMessage
} from "aws-sdk/clients/elasticbeanstalk";

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

let version: string = process.env.BASE_APP_VERSION || '0.0.1';
const deploymentBucket: string = 'flagship-bugsauce-poker-bucket';
const artifactPath: () => string = (): string => `poker-${version}.jar`;
const appName: string = "flagship-bugsauce-poker";
const description: string = 'Online multiplayer poker application.';
const envName: string = 'bugsauce-poker';
const templateName: string = 'spring-template';

/**
 * Find the most recent version, increment it by 1 and assign the result to the version variable.
 */
function updateVersion(): Promise<void> {
  return elasticBeanstalk.describeApplicationVersions({ApplicationName: appName}).promise()
  .then((res: PromiseResult<ApplicationVersionDescriptionsMessage, AWSError>) => {
    const versions: string[] = res.ApplicationVersions
    .map(app => app.VersionLabel)
    .sort()
    .reverse();
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
      })
      .promise();
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

function deploy(): Promise<any> {
  return updateVersion()
  .then(uploadArtifact)
  .then(applicationExists)
  .then(exists => exists ? deployNewVersion() : deployNewApplication())
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

(async () => await deploy().catch(console.error))();
