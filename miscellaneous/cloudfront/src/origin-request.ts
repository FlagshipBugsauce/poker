import {CloudFrontRequestEvent, CloudFrontRequestResult} from "aws-lambda";

export const handler = async (event: CloudFrontRequestEvent): Promise<CloudFrontRequestResult> => {
  const request = event.Records[0].cf.request;

  // Route to server origin.
  if (request.uri.includes('server/')) {
    request.origin = {
      custom: {
        customHeaders: {},
        // TODO: Add "environment variable" for domainName.
        domainName: 'awseb-e-k-AWSEBLoa-1PG335JLLT141-848839344.us-east-1.elb.amazonaws.com',
        keepaliveTimeout: 60,
        path: '',
        port: 80,
        protocol: 'http',
        readTimeout: 60,
        sslProtocols: ['TLSv1.2', 'TLSv1.1']
      }
    };
    request.uri = request.uri.replace('server/', '');
    return request;
  }

  // Route to Angular app.
  if (request.uri === '/index.html' || request.uri.includes('.')) {
    return request;
  } else {
    request.uri = '/index.html';
  }
  return request;
};
