const express = require("express");
const router = express.Router();
const db = require("./setupdb.js");

router.post("/", (req,res) => {

    const docname = req.body.company + "-" + req.body.flight;
    const luggageName = req.body.name;
    let data = {};
    data[luggageName] = req.body.newstatus; //newstatus should never be "awaiting boarding"
    db.collection('Flights').doc(docname).get()
    .then((doc) => {
        if (!doc.exists) {
            res.status(201).send("flight not found");
            return;
        } else { //checks in luggage regardless of whether user checked in on phone first
            db.collection('Flights').doc(docname).set(
                data, {merge: true}
            );
            res.status(200).send("success");
        }
    });
    /*db.collection('Flights').doc(docname).set(
        data, {merge: true}
    );
    res.status(200).send("success");*/

})

module.exports = router;