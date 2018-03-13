const http = require('http');
const util = require('util');
const express = require('express');
const app = express();
const bodyParser = require('body-parser');

const responseStringFormat = "recommendation => %s\n";

var misbehave = false;

app.use(bodyParser.json()); // Inject JSON parser

app.get('/', function( request, response ) {
    /*
     setTimeout( function() {
        response.send(util.format(responseStringFormat, "test"));
    }, 3000 ); 
    */
   var hostname = os.hostname();
    if(misbehave) {
        response.sendStatus(503).end(util.format("recommendation misbehavior from %s\n", hostname));
    } else {
        response.send(util.format(responseStringFormat, hostname));
    }
});

app.get('/misbehave', function(request, response) {
    misbehave = true;
    response.send(util.format(responseStringFormat, "Following requests to '/' will return a 503\n"));
});

app.get ('behave', function(request, response) {
    misbehave = false;
    response.send(util.format(responseStringFormat, "Following requests to '/' will return a 200\n"));
});

app.listen(8080, function() {
    console.log('Recommendation listening on port 8080')
});