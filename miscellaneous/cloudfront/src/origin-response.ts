import {CloudFrontHeaders, CloudFrontResponseEvent, CloudFrontResponseResult} from "aws-lambda";

export const handler = async (event: CloudFrontResponseEvent):
    Promise<CloudFrontResponseResult> => {
  const response = event.Records[0].cf.response;
  setHeader(response.headers, 'X-Frame-Options', 'SAMEORIGIN');
  return response;
};

export const setHeader =
    (customHeaders: CloudFrontHeaders, headerName: string, headerValue: string): void => {
      customHeaders[headerName.toLowerCase()] = [
        {
          key: headerName,
          value: headerValue
        }
      ];
    };
