package com.orange.browser;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

public class LocationUtil {
    
    
    public static boolean getCurrLocation(BrowserActivity context) {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        Location gpsLocation = (locationManager)
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        long currentTime = System.currentTimeMillis();
        Log.i("!@@@@","~~~~~~~~~");
        if (gpsLocation != null) {
                //&& gpsLocation.getTime() >= (currentTime - 60000L)) {

            String latitude = String.valueOf(gpsLocation.getLatitude());
            String longitude = String.valueOf(gpsLocation.getLongitude());
            context.mLocationInfo = new LocationInfo(latitude, longitude);
            context.mLocationInfo.setMode(LocationInfo.GPSMODE);
            Log.i("!@@@@","########");
            return true;

        } else {

            Location networkLocation = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if ((networkLocation != null)
                    && networkLocation.getTime() >= (currentTime - 60000L)) {

                String latitude = String.valueOf(networkLocation.getLatitude());
                String longitude = String.valueOf(networkLocation
                        .getLongitude());
                context.mLocationInfo = new LocationInfo(latitude, longitude);
                context.mLocationInfo.setMode(LocationInfo.WIFIMODE);
                Log.i("!@@@@","$$$$$$");
                return true;

            } else {

                TelephonyManager telephonyManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                CellLocation cellLocation = telephonyManager.getCellLocation();
                if ((cellLocation instanceof GsmCellLocation)) {

                    GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
                    if (gsmCellLocation != null) {

                        String cellId = Integer.toString(gsmCellLocation.getCid());
                        String lac = Integer.toString(gsmCellLocation.getLac());
                        String mnc = getImsi(telephonyManager);// Note:use imsi
                        context.mLocationInfo = new LocationInfo(cellId, lac, mnc);
                        context.mLocationInfo.setMode(LocationInfo.BASEMODE);
                        Log.i("!@@@@","!!!!!!!!!!!");
                        return true;

                    }

                }

            }

        }
        Log.i("!@@@@","**********");

        return false;
    }
    
    
    public static boolean getCurrentCell(BrowserActivity context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        CellLocation cellLocation = telephonyManager.getCellLocation();
        if ((cellLocation instanceof GsmCellLocation)) {

            GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
            if (gsmCellLocation != null) {

                String cellId = Integer.toString(gsmCellLocation.getCid());
                String lac = Integer.toString(gsmCellLocation.getLac());
                String mnc = getImsi(telephonyManager);// Note:use imsi
                context.mLocationInfo = new LocationInfo(cellId, lac, mnc);
                context.mLocationInfo.setMode(LocationInfo.BASEMODE);
                return true;

            }

        }
        
        return false;
    }
    
    
    private static String getImsi(TelephonyManager tm) {
        String imsi = null;
        if (tm == null) {
            return null;
        }
        imsi = tm.getSubscriberId();
        if (!TextUtils.isEmpty(imsi) && imsi.length() < 15)
        {
            String simSN = tm.getSimSerialNumber();
            if (simSN != null && simSN.length() > 6)
            {
                imsi = imsi + simSN.substring(6);
            }
        }
        if (imsi == null) {
            imsi = tm.getSimSerialNumber();
        }
        if (imsi == null) {
            imsi = tm.getDeviceId();
        }
        if (imsi != null && imsi.equals("310260000000000")) {
            imsi = "460099064111033";
            // imsi = "46000106411102211";//"";
        }
        return imsi;
    }
    
    
}
