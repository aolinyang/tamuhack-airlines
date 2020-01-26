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
        if (doc.exists && doc.data()[luggageName] !== 'undefined') {
            res.status(200).send(doc.data()[luggageName]);
            return;
        } else {
            db.collection('Flights').doc(docname).set(
                data, {merge: true}
            );
            res.status(200).send("awaiting boarding");
        }
    });
    /*db.collection('Flights').doc(docname).set(
        data, {merge: true}
    );
    res.status(200).send("awaiting boarding");*/

})

module.exports = router;