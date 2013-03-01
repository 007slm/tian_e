package com.orange.browser;

public class LocationInfo {
    
    public static final int GPSMODE = 0;
    public static final int WIFIMODE = 1;
    public static final int BASEMODE = 2;
    
    int mMode = GPSMODE;
	String mLat;
	String mLon;
	
	String mCellid;
    String mLac;
    String mMnc;


    public LocationInfo(String latitude, String longitude) {
		this.mLat = latitude;
		this.mLon = longitude;
	}
    
    public LocationInfo(String cellid, String lac, String mnc) {
        this.mCellid = cellid;
        this.mLac = lac;
        this.mMnc = mnc;
    }
	
	public int getMode() {
	    return mMode;
	}

	public String getLat() {
		return this.mLat;
	}

	public String getLon() {
		return this.mLon;
	}
	
    public String getCellid() {
        return mCellid;
    }
    
    public String getLac() {
        return mLac;
    }
    
    public String getMnc() {
        return mMnc;
    }
	
	public void setMode(int mode) {
	    mMode = mode;
	}

	public void setLat(String latitude) {
		this.mLat = latitude;
	}

	public void setLon(String longitude) {
		this.mLon = longitude;
	}
	
    public void setCellid(String cellid) {
        mCellid = cellid;
    }
    
    public void setLac(String lac) {
        mLac = lac;
    }
	
    public void setMnc(String mnc) {
        mMnc = mnc;
    }
	
}
