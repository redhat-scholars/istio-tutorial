const logger = require('pino')();
const request = require('request');
const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const url = 'http://recommendation:8080';

const responseString = 'preference => ';

require('kube-probe')(app); // Add liveness and readiness URLs
app.use(bodyParser.json()); // Inject JSON parser

app.get('/', (request, response) => {
  getRecommendation((err, _, body) => {
    response.send(`${responseString} ${err || body}\n`);
    if (err) logger.error(err);
  });
});

function getRecommendation(callback) {
  request.get(url, (error, response, body) => {
    return callback(error, response, body);
  });
}

app.listen(8080, () => logger.info('Preference listening on port 8080'));