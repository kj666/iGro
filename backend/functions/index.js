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
 
    db.ref(`/${gid}/Ranges/${sid}/Low`).once('value', function(snapshotLow){
        db.ref('/'+gid+'/Ranges/'+sid+'/High').once('value', function(snapshotHigh){
            const lowRangeVal = snapshotLow.val();
            const highRangeVal = snapshotHigh.val();

            var message;

            if(!((dataValue >lowRangeVal) && (dataValue < highRangeVal))){
                if(!(dataValue >lowRangeVal)){
                    message =sid+" is reading under threshold value";
                    console.log("highRange", highRangeVal +" UNDER RANGE");
                }
                else{
                    message =sid+" is reading above threshold value";
                    console.log("highRange", highRangeVal +" ABOVE RANGE");
                }

                db.ref("/Users/").orderByChild("GreenhouseID").equalTo(gid).on("child_added", function(snapshot){
                    const users = snapshot.val();
                    console.log(snapshot.key+" hi "+ users.Name +" "+users.NotificationToken);
            
                    const payload ={
                        notification: {
                            title: users.GreenhouseID +" condition changed",
                            body: message, 
                            tag: sid
                        }
                    };
                    admin.messaging().sendToDevice(users.NotificationToken, payload);
                });
            }
            else{
                console.log("highRange", highRangeVal +"INSIDE RANGE");
            }

        });
    });
   
});
