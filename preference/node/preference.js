const http = require('http');
const request = require('request');
const util = require('util');
const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const url = "http://recommendation:8080"

const responseStringFormat = "preference => %s\n";

app.use(bodyParser.json()); // Inject JSON parser

app.get('/', function(request, response) {
    getRecommendation(function(e,r,b) {
        if(!e) {
            response.send(util.format(responseStringFormat, b));
        } else {
            response.send(util.format(responseStringFormat, e));
        }
    });
});

function getRecommendation(callback) {
    request.get(url, (error, response, body) => {
        return callback(error, response, body);
    });
};

app.listen(8080, function() {
    console.log('Preference listening on port 8080')
});