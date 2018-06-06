const http = require('http');
const request = require('request');
const util = require('util');
const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const url = "http://preference:8080";

const responseStringFormat = "customer => %s\n";

var options = {
    url: url,
    headers: {
        'User-Agent': 'request'
    }
};

app.use(bodyParser.json()); // Inject JSON parser

app.get('/', function(request, response) {
    getPreference(function(e,r,b) {
        if(!e) {
            response.send(util.format(responseStringFormat, b));
        } else {
            response.send(util.format(responseStringFormat, e));
        }
    });
});

function getPreference(callback) {
    request.get(options, (error, response, body) => {
        return callback(error, response, body);
    });
};

app.listen(8080, function() {
    console.log('Customer listening on port 8080')
});