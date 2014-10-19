package hr.foi.tosulc.fetchplace.helpers;

import android.content.Context;
import android.content.Intent;

/**
 * Created by tosulc on 14.10.2014..
 */
public class CommonUtilities {
    public static final String EXTRA_MESSAGE = "message";
    public static final String DISPLAY_MESSAGE_ACTION = "hr.foi.tosulc.fetchplace.DISPLAY_MESSAGE";

    /**
     * Notifies UI to display a message.
     * <p/>
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

}
