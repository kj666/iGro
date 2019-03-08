const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

const admin = require('firebase-admin');
admin.initializeApp();

//Add a timeStamp to incoming data created
exports.addTimeStampToData = functions.database.ref('data/{id}/humidity')
.onCreate((snapshot, context) => {
    const original = snapshot.val();
    const Timestamp = admin.database.ServerValue.TIMESTAMP;

    return snapshot.ref.parent.child('time').set(Timestamp);
});