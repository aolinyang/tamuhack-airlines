const express = require("express");
const router = express.Router();
const db = require("./setupdb.js");

router.post("/", (req,res) => {

    const docname = req.body.company + "-" + req.body.flight;
    db.collection('Flights').doc(docname).get()
    .then((doc) => {
        if (doc.exists) {
            res.status(200).send("flight already exists");
        } else {
            db.collection('Flights').doc(docname).set({});
            res.status(200).send("successfully added flight");
        }
    });

})

module.exports = router;