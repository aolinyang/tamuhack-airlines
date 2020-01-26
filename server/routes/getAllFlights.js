const express = require('express');
const router = express.Router();
const db = require("./setupdb.js");

router.get("/", (req,res) => {

    db.collection("Flights").get()
    .then((snapshot) => {
        let flights = [];
        snapshot.forEach((flight) => {
            flights.push(flight.id);
        });
        res.send(flights);
    });

})

module.exports = router;