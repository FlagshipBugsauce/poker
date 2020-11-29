# Server Deployment

Most steps of server deployment are handled by `index.ts`. There are, however, several steps that
must be performed manually. These steps only need to be performed once per environment, and are only
required as part of the initial deployment process. Everything is automated following the initial
deployment.

## Manual Steps
1. Purchase the desired custom domain through Route 53. It's easiest to get the domain from Amazon,
   since all configurations can be achieved either automatically using the AWS-SDK, or manually by
   using the AWS console.
   
2. Request a certificate in the AWS console using ACM service.
    - Go to ACM.
    - Click "Request a certificate".
    - Select "Request a public certificate".
    - Add the domains and sub-domains to be covered by the certificate.
    - Select "DNS validation".
    - Add appropriate tags.
    - Click "Review".
    - Click "Confirm and request".
    - Should be taken to a status UI. Click the arrow to expand the domains that were added, and
      click "Create record in Route 53". This will perform the Route 53 configurations
      automatically.  
      
    _It may be possible to perform this step automatically, but it is a one time thing, so it's not
    a huge burden to do this manually once per environment._

3. Run the server deployment script. This should create an Elastic Beanstalk application and
   environment, and it should also deploy a CloudFront distribution. May need to deploy the
   Cloudfront distribution by itself first, without the custom domain alias and then setup Route 53
   records that point to the distribution (I don't remember the exact order I did this in).
   
4. _This step will most likely be removed_. In the AWS console, go to Elastic Beanstalk and find
   the environment which was created in step 3. Go to Configuration -> Load Balancer. In order for
   websocket to work, listeners on port 80 and port 443 should be created (if they don't already
   exist), and the protocol should be TCP. The EC2 security group also needs to be updated to have
   inbound rules that will allow TCP traffic for these ports. Need to do more testing to confirm,
   but this is essential for websocket to work correctly as far as I'm currently aware. It be 
   possible to automate all of this, but for now, this needs to be done manually.
