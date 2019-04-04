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

// Add a timeStamp to incoming data created
exports.addTimeStampTemp = functions.database.ref('/{gid}/Data/{sid}/{id}')
.onCreate((snapshot, context) => {
    const original = snapshot.val();
    const Timestamp = admin.database.ServerValue.TIMESTAMP;

    return snapshot.ref.child('time').set(Timestamp);
});

exports.sendPushNotification = functions.database.ref('/{gid}/Data/{sid}/{id}')
.onWrite((data, context) =>{
    var db = admin.database();
    const gid = context.params.gid;
    const sid = context.params.sid;
    const dataNode = data.after.val()
    const dataValue = dataNode.value;
 
    db.ref(`/${gid}/Ranges/${sid}/Low`).once('value', snapshotLow=>{
        db.ref('/'+gid+'/Ranges/'+sid+'/High').once('value', snapshotHigh=>{
            const lowRangeVal = snapshotLow.val();
            const highRangeVal = snapshotHigh.val();

            var title;

            if(!((dataValue >lowRangeVal) && (dataValue < highRangeVal))){
                if(!(dataValue >lowRangeVal)){
                    title = sid+" is "+parseFloat(lowRangeVal - dataValue).toFixed(2) + " units UNDER threshold";
                    console.log(title);
                }
                else{
                    title = sid+" is "+parseFloat(dataValue - highRangeVal).toFixed(2) + " units ABOVE threshold";
                    console.log(title);
                }

                const payload ={
                    notification: {
                        title: title,
                        body:"Tap for more information"
                    }
                };
                admin.messaging().sendToTopic(gid, payload).then((response)=>{
                   return console.log('Notification sent successfully:',response);
               }) 
               .catch((error)=>{
                    console.log('Notification sent failed:',error);
               });
            }
            else{
                console.log("highRange", highRangeVal +"INSIDE RANGE");
            }

        });
    });
   
});
