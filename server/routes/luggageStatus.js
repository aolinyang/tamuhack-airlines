const express = require('express');
const router = express.Router();
const db = require("./setupdb.js");

router.get("/", (req,res) => {
    
    const docname = req.query.company + "-" + req.query.flight;
    const luggageName = req.query.name;
    db.collection('Flights').doc(docname).get()
    .then((doc) => {
            if (doc.exists) {
                const luggageStatus = doc.data()[luggageName]
                res.status(200).send(luggageStatus || "unregistered"); //*meme man* javaskript ekspert
            } else {
                res.end(); //only if flight can't be found, but that shouldn't be possible
            }
        
    })
    .catch((err) => {
        console.log('Error getting documents', err);
        res.end();
    });

})

module.exports = router;