package com.picsy.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

/**
 * @author samuelkirton
 */
public class ContentUtils {

	/**
	 * Get the real path from the provided URI.
	 * NOTE: This will block the UI thread
	 * @param	uri	The URI to get the real path for
	 * @param	context	The application context
	 * @return	The real path based on the provided URI
	 */
	public static String getRealPathFromURI(Uri uri, Context context) {
		String[] proj = { MediaStore.Images.Media.DATA };
		CursorLoader loader = new CursorLoader(context, uri, proj, null, null, null);
		Cursor cursor = loader.loadInBackground();
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
}
