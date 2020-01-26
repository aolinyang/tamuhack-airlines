const express = require('express');
const router = express.Router();
const db = require("./setupdb.js");

router.get("/", (req,res) => {

    db.collection("Flights").doc(req.query.flight).get()
    .then((flight) => {
        res.send(flight.data());
    })

})

module.exports = router;