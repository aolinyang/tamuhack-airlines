if (process.env.NODE_ENV !== 'production') {
    require('dotenv').config();
}
  
const bodyParser = require('body-parser');
//const cookieParser = require('cookie-parser');
const express = require('express');
const app = express();
const port = process.env.PORT || 5001;

//app.use(cookieParser());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

const getLuggageRouter = require("./luggageStatus.js");
const registerLuggageRouter = require("./registerLuggage.js");
const changeStatusRouter = require("./changeStatus.js");
const addFlightRouter = require("./addFlight.js");

app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", req.headers.origin);
    res.header("Access-Control-Allow-Credentials", "true");
    res.header("Access-Control-Allow-Methods", "GET,HEAD,OPTIONS,POST,PUT,PATCH");
    res.header("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
    //intercepts OPTIONS method
    if ('OPTIONS' === req.method) {
        //respond with 200
        res.end();
      }
      else {
      //move on
        next();
      }
});

app.use(function(req, res, next) {
    console.log("HTTP request: " + req.method + " " + req.path);
    console.log("Body: " + JSON.stringify(req.body, null, 3));
    console.log("Cookies: " + JSON.stringify(req.cookies, null, 3));
    console.log();
    next();
});

app.use("/getluggagestatus", getLuggageRouter);
app.use("/registerluggage", registerLuggageRouter);
app.use("/changestatus", changeStatusRouter);
app.use("/addflight", addFlightRouter);

app.listen(port);