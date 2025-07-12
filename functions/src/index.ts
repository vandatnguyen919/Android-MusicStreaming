import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

admin.initializeApp();

export const onNewSongAdded = functions.firestore
    .document('songs/{songId}')
    .onCreate(async (snap, context) => {
        const songData = snap.data();
        const artistId = songData.artistId;

        if (!artistId) {
            console.log('No artist ID found for the song');
            return;
        }

        // Message payload
        const message = {
            data: {
                artistId: artistId
            },
            topic: 'new_songs' // Send to all devices subscribed to this topic
        };

        try {
            // Send the message
            const response = await admin.messaging().send(message);
            console.log('Successfully sent notification:', response);
            return {success: true};
        } catch (error) {
            console.error('Error sending notification:', error);
            return {success: false, error: error};
        }
    });
