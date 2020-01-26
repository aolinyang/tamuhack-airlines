const express = require('express');
const router = express.Router();
const db = require("./setupdb.js");

router.post("/", (req,res) => {

    db.collection("Customers").doc(req.body.username).set({"devices": []});
    res.send("successfully added user");

})

module.exports = router;