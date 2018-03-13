const request = require("request");
//const url = "https://maps.googleapis.com/maps/api/geocode/json?address=Florence";
const url = "http://localhost:8080";

request.get(url, (error, response, body) => {
  //let json = JSON.parse(body);
  console.log(
    `City: ` + body.toString()
  );
});