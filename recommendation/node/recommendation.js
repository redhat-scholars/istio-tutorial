const os = require('os');
const logger = require('pino')();
const express = require('express');
const bodyParser = require('body-parser');
const app = express();
const responseString = 'recommendation => ';

let misbehave = false;

require('kube-probe')(app); // Add liveness and readiness URLs
app.use(bodyParser.json()); // Inject JSON parser

app.get('/', function( request, response ) {
  var hostname = os.hostname();
  if(misbehave) {
    response.sendStatus(503).end(`recommendation misbehavior from ${hostname}\n`);
  } else {
    response.send(`${responseString} ${hostname}\n`);
  }
});

app.get('/misbehave', function(request, response) {
  misbehave = true;
  response.send(`${responseString} Following requests to '/' will return a 503\n`);
});

app.get ('/behave', function(request, response) {
  misbehave = false;
  response.send(`${responseString} Following requests to '/' will return a 200\n`);
});

app.listen(8080, () => logger.info('Recommendation listening on port 8080'));