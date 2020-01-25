if (process.env.NODE_ENV !== 'production') {
    require('dotenv').config();
}

const jwt = require('jsonwebtoken');
const secret = process.env.SECRET;
const saltrounds = parseInt(process.env.SALTROUNDS);
const bcrypt = require('bcryptjs');

const hash = function(password) {
    return new Promise((resolve, reject) => {
        bcrypt.hash(password, saltrounds, function(err, hashed) {
            if (err)
                return reject(err);
            resolve(hashed);
        }
    )
    });
}

exports.hash = hash;