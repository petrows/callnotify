package callnotify.petro.ws.callnotify;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import java.util.Date;

public class CallReceiver extends PhonecallReceiver {

	private static int lastId = 0;

	public CallReceiver() {
	}

	public String getContactName(Context ctx, final String phoneNumber) {
		Uri uri;
		String[] projection;
		Uri mBaseUri = Contacts.Phones.CONTENT_FILTER_URL;
		projection = new String[]{android.provider.Contacts.People.NAME};
		try {
			Class<?> c = Class.forName("android.provider.ContactsContract$PhoneLookup");
			mBaseUri = (Uri) c.getField("CONTENT_FILTER_URI").get(mBaseUri);
			projection = new String[]{"display_name"};
		} catch (Exception e) {
		}


		uri = Uri.withAppendedPath(mBaseUri, Uri.encode(phoneNumber));
		Cursor cursor = ctx.getContentResolver().query(uri, projection, null, null, null);

		String contactName = "";

		if (cursor.moveToFirst()) {
			contactName = cursor.getString(0);
		}

		cursor.close();
		cursor = null;

		return contactName;
	}

	@Override
	protected void onIncomingCallReceived(Context ctx, String number, Date start) {
		Log.d("CallReceiver", "Incoming call from " + number);

		String contactName = getContactName(ctx, number);
		if (contactName.isEmpty()) {
			contactName = PhoneNumberUtils.formatNumber(number);
		}

		NotificationCompat.Builder mBuilder =
			new NotificationCompat.Builder(ctx)
				// .setLargeIcon(android.R.drawable.ic_menu_call)
				.setSmallIcon(android.R.drawable.ic_menu_call)
				.setAutoCancel(true)
				.setContentTitle(ctx.getString(R.string.incoming_call))
				.setContentText(contactName)
				.setVibrate(null)
				.setSound(null);

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
