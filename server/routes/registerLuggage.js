const express = require("express");
const router = express.Router();
const db = require("./setupdb.js");

router.post("/", (req,res) => {

    const docname = req.body.company + "-" + req.body.flight;
    const luggageName = req.body.name;
    let data = {};
    data[luggageName] = "awaiting boarding";
    db.collection('Flights').doc(docname).get()
    .then((doc) => {
        if (doc.exists && doc.data()[luggageName] !== 'undefined') { //already checked by a scanner
            res.status(200).send(doc.data()[luggageName]);
        } else if (doc.exists) { //registers luggage as awaiting boarding
            db.collection('Flights').doc(docname).set(
                data, {merge: true}
            );
            res.status(200).send("awaiting boarding");
        } else {
            res.status(201).send("flight not found");
        }
        return;
    });
    /*db.collection('Flights').doc(docname).set(
        data, {merge: true}
    );
    res.status(200).send("awaiting boarding");*/

})

module.exports = router;