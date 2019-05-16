package com.PhotoGridMaker.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import com.PhotoGridMaker.Softsimplicity.R;


/**
 * Network tools
 *
 * @author Deepak
 *
 */
public class NetUtils {

	public static final int CMNET = 20;
	public static final int CMWAP = 21;
	public static final int WIFI = 22;

	/**
	 * Get network type :3G /WIFI
	 */
	public static int getAPNType(Context context) {
		int netType = -1;
		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
//			Log.e("networkInfo.getExtraInfo()",
//					"networkInfo.getExtraInfo() is "
//							+ networkInfo.getExtraInfo());
			if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
				netType = CMNET;
			} else {
				netType = CMWAP;
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = WIFI;
		}
		return netType;
	}

	/**
	 * Get the IP address
	 * 
	 * @return
	 */
	public static String getLocalIpAddress() {
//		try {
//			for (Enumeration<NetworkInterface> en = NetworkInterface
//					.getNetworkInterfaces(); en.hasMoreElements();) {
//				NetworkInterface intf = en.nextElement();
//				for (Enumeration<InetAddress> enumIpAddr = intf
//						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//					InetAddress inetAddress = enumIpAddr.nextElement();
//					if (!inetAddress.isLoopbackAddress()) {
//						return inetAddress.getHostAddress().toString();
//					}
//				}
//			}
//		} catch (SocketException ex) {
//
//		}
		return "127.0.0.1";
	}

	/**
	 * Judging network status
	 * @param context
	 * @return
	 */
	public static boolean isCheckNetAvailable(Context context) {
		boolean isCheckNet = false;
		try {
			final ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo mobNetInfoActivity = connectivityManager
					.getActiveNetworkInfo();
			if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable()) {
				isCheckNet = false;
				return isCheckNet;
			} else {
				isCheckNet = true;
				return isCheckNet;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return isCheckNet;
	}

	public static boolean isCheckNetAvailable(Context context, int netType) {
		boolean isAvaliable = false;
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getNetworkInfo(netType);
		if (null != info && info.isConnected()) {
			isAvaliable = true;
		}else{
			Toast.makeText(context, R.string.net_wrok_unconnetion_text, Toast.LENGTH_SHORT).show();
		}
		
		return isAvaliable;
	}

	/**
	 * Determine if the network is available
	 * * @return Available Returns true Unavailable Returns false
	 */
	public static boolean NetAvailable(Context context) {
		if (!NetUtils.isCheckNetAvailable(context)) {
			ToastUtil.showToast(R.string.net_wrok_unconnetion_text);
			return false;
		}
		return true;
	}
}
