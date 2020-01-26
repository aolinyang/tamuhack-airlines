const admin = require('firebase-admin');


/*admin.initializeApp({
  credential: admin.credential.applicationDefault()
});

const db = admin.firestore(); */


const serviceAccount = require('./../firebase_key.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();

module.exports = db;