const express = require('express');
const router = express.Router();
const db = require("./setupdb.js");

router.get("/", (req,res) => {

    db.collection("Customers").doc(req.query.username).get()
    .then((username) => {
        res.send(username.data().devices);
    })

})

module.exports = router;