/**
 * Tool that will fetch updated API docs, save to api-docs.yaml.
 */

// TODO: Polish this up at some point.
const http = require('follow-redirects').http;
const fs = require('fs');

const options = {
  'method': 'GET',
  'hostname': 'www.jonsthings.com',
  'path': '/server/v3/api-docs.yaml',
  'headers': {},
  'maxRedirects': 20
};

const req = http.request(options, function (res) {
  const chunks = [];

  res.on("data", function (chunk) {
    chunks.push(chunk);
  });

  res.on("end", function () {
    const body = Buffer.concat(chunks);
    console.log('Updating api-docs.yaml');
    fs.writeFile('./api-docs.yaml', body.toString(), (err) => {
      if (err) {
        console.log(err);
      }
    });
  });

  res.on("error", function (error) {
    console.error(error);
  });
});

req.end();
