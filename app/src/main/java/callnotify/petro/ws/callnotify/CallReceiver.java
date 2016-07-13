package callnotify.petro.ws.callnotify;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import java.io.IOException;
import java.util.Date;

public class CallReceiver extends PhonecallReceiver {

	private Context ctx = null;
	private static int lastId = 0;
	private static final String TAG = "CallReceiver";

	private String contactDisplayName = "";
	private String contactPhoto = "";

	public CallReceiver() {
	}

	public void getContactName(Context context, final String phoneNumber) {
		ctx = context;
		Uri uri;
		String[] projection;
		Uri mBaseUri = Contacts.Phones.CONTENT_FILTER_URL;
		projection = new String[]{android.provider.Contacts.People.NAME};
		try {
			Class<?> c = Class.forName("android.provider.ContactsContract$PhoneLookup");
			mBaseUri = (Uri) c.getField("CONTENT_FILTER_URI").get(mBaseUri);
			projection = new String[]{ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_THUMBNAIL_URI};
		} catch (Exception e) {
			return;
		}


		uri = Uri.withAppendedPath(mBaseUri, Uri.encode(phoneNumber));
		Cursor cursor = ctx.getContentResolver().query(uri, projection, null, null, null);

		if (cursor.moveToFirst()) {
			contactDisplayName = cursor.getString(0);
			contactPhoto = cursor.getString(1);
		}

		Log.d(TAG, "Found contact: " + contactDisplayName + ", photo " + contactPhoto);

		cursor.close();
		cursor = null;
	}

	@Override
	protected void onIncomingCallReceived(Context ctx, String number, Date start) {
		Log.d(TAG, "Incoming call from " + number);

		getContactName(ctx, number);

		if (contactDisplayName.isEmpty()) {
			contactDisplayName = PhoneNumberUtils.formatNumber(number);
		}

		NotificationCompat.Builder mBuilder =
			new NotificationCompat.Builder(ctx)
				.setSmallIcon(android.R.drawable.ic_menu_call)
				.setAutoCancel(true)
				.setContentTitle(ctx.getString(R.string.incoming_call))
				.setContentText(contactDisplayName)
				.setVibrate(null)
				.setSound(null);

		if (!contactPhoto.isEmpty()) {
			try {
				mBuilder.setLargeIcon(MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), Uri.parse(contactPhoto)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Notification notification = mBuilder.build(); //генерируем уведомление

		NotificationManager mNotificationManager =
			(NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		lastId = lastId + 1;
		mNotificationManager.notify(lastId, notification);
	}

	@Override
	protected void onIncomingCallAnswered(Context ctx, String number, Date start) {

	}

	@Override
	protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {

	}

	@Override
	protected void onOutgoingCallStarted(Context ctx, String number, Date start) {

	}

	@Override
	protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {

	}

	@Override
	protected void onMissedCall(Context ctx, String number, Date start) {

	}
}
