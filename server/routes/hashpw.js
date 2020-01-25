const express = require('express');
var router = express.Router();
const hash = require("./utils").hash;

router.post("/", (req, res) => {
    const tohash = req.body.tohash;
    hash(tohash).then((hashedpw) => {
        console.log(hashedpw);
        res.status(200);
        res.send({
            "hashedpw":hashedpw
        });
    });
})

module.exports = router;