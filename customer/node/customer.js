const logger = require('pino')();
const request = require('request');
const express = require('express');
const bodyParser = require('body-parser');

const app = express();
const url = 'http://preference:8080';
const responseString = 'customer =>';

var options = {
  url: url,
  headers: {
    'User-Agent': 'request'
  }
};

require('kube-probe')(app); // Add liveness and readiness URLs
app.use(bodyParser.json()); // Inject JSON parser

app.get('/', function(request, response) {
  getPreference(function(e,r,b) {
    response.send(`${responseString} ${e || b}\n`);
    if (e) logger.error(e);
  });
});

function getPreference(callback) {
  request.get(options, (error, response, body) => {
    return callback(error, response, body);
  });
}

app.listen(8080, () => logger.info('Customer listening on port 8080'));
