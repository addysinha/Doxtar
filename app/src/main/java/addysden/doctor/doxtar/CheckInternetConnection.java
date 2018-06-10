package addysden.doctor.doxtar;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Abhyudaya sinha on 2/2/2016.
 */
public class CheckInternetConnection {

    Context mContext;
    public CheckInternetConnection(Context Context) {
        this.mContext = Context;
    }

    protected boolean isNetworkConnected() {

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

}
