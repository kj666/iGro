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
 
    db.ref("/Users/").orderByChild("GreenhouseID").equalTo(gid).on("child_added", function(snapshot){
        const users = snapshot.val();
        if(users.NotificationToken.exists()){
            console.log(snapshot.key+" hi "+ users.Name +" "+users.NotificationToken);
        }
        else{
            console.log(snapshot.key+" hi "+ users.Name +" No notification");
        }
    });
    
    // var tokensList = users.ref("NotificationToken").once('value');
   /* let tokenSnapshot;

    return Promise.all([users]).then(results =>{
        tokenSnapshot = results[0];
        console.log(tokenSnapshot);
        console.log(results);
        if(!tokenSnapshot.hasChildren()){
            return console.log('There are no notification tokens to send to.');
        }
        console.log('There are', tokensSnapshot.numChildren(), 'tokens to send notifications to.');
        const payload = {
            notification:{
                title: "Sensor exceeded Range",
                body: "Testing body"
            }
        };

        return aadmin.messaging().sendToDevice(tokenSnapshot, payload);
    }).then((response)=>{
        const tokensToRemove =[];
      /*  response.results.forEach((result, index)=>{
            const error = result.error;
            if(error){
                console.error('Failure sending notification to', tokenSnapshot[index], error);
            
                if (error.code === 'messaging/invalid-registration-token' || error.code === 'messaging/registration-token-not-registered') {
                    tokensToRemove.push(tokensSnapshot.ref.child(tokens[index]).remove());
                            }
                        }
                    });
                    return Promise.all(tokensToRemove);
                });
    
    /*var token = db.ref(`Users/{uid}/notificationToken`);
    
    db.ref(`/${gid}/Ranges/${sid}/Low`).once('value', function(snapshotLow){
        
        db.ref('/'+gid+'/Ranges/'+sid+'/High').once('value', function(snapshotHigh){
            const lowRangeVal = snapshotLow.val();
            const highRangeVal = snapshotHigh.val();

            var payload = {
                notification:{
                    title: "Sensor exceeded Range",
                    body: dataNode.value+""
                }
            };


            if(!((dataValue >lowRangeVal) && (dataValue < highRangeVal))){
                if(!(dataValue >lowRangeVal)){
                    console.log("highRange", highRangeVal +" UNDER RANGE");
                }
                else{
                    return token.once("value", function(snapshot3){
                        const payload = {
                            notification:{
                                title: "Sensor exceeded Range",
                                body: "Testing body"
                            }
                        };
                        admin.messaging().send

                    });
                    console.log("highRange", highRangeVal +" ABOVE RANGE");
                    admin.messaging().sendToTopic("Above Range", payload).then((response)=>{
                        console.log("Successfully sent message: ", response);
                        return true;
                    }).catch((error)=>{
                        console.log("Error sending message: ", error);
                        return false;
                    })
                }
            }
            else{
                console.log("highRange", highRangeVal +"INSIDE RANGE");
            }
        });
    });
*/
   
});
