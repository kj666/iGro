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

            var message;
            let toekns =[];

            if(!((dataValue >lowRangeVal) && (dataValue < highRangeVal))){
                if(!(dataValue >lowRangeVal)){
                    message =sid+" is reading under threshold value";
                    console.log("highRange", highRangeVal +" UNDER RANGE");
                }
                else{
                    message =sid+" is reading above threshold value";
                    console.log("highRange", highRangeVal +" ABOVE RANGE");
                }

                const payload ={
                    notification: {
                        title: "Greenhouse1 condition changed ",
                        body: message
                    }
                };
                admin.messaging().sendToTopic("Greenhouse1", payload).then((response)=>{
                   return console.log('Notification sent successfully:',response);
               }) 
               .catch((error)=>{
                    console.log('Notification sent failed:',error);
               });

               /* db.ref("/Users/").orderByChild("GreenhouseID").equalTo(gid).on("child_added", snapshot=>{
                    const users = snapshot.val();
                    console.log(snapshot.key+" hi "+ users.Name +" "+users.NotificationToken);
        
                    toekns.push(users.notification);
                    const payload ={
                        notification: {
                            title: users.GreenhouseID +" condition changed "+ users.Name,
                            body: message
                        }
                    };
                    admin.messaging().sendToDevice(users.NotificationToken, payload).then((response)=>{
                        return console.log("Notification sent to "+users.Name, response);
                    }).catch((error)=>{
                        console.log("Error: ", error);
                    });
                });*/
            }
            else{
                console.log("highRange", highRangeVal +"INSIDE RANGE");
            }

        });
    });
   
});
