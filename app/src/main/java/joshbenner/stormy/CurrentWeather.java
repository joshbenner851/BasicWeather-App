package joshbenner.stormy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Josh on 3/12/2015.
 */
public class CurrentWeather
{
    private String mIcon;
    private long mTime;
    private String mTimeZone;

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public String getFormattedTime(){
        //formatting from unix seconds to the date
        //returns a date string
        SimpleDateFormat format = new SimpleDateFormat("h:mm a");
        format.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        Date dateTime = new Date(getTime()*1000);
        String timeString = format.format(dateTime);
        return timeString;
    }
    public int getIconId(){
        int iconId = R.drawable.clear_day;
        if(mIcon.equals("clear-day")){
            iconId = R.drawable.clear_day;
        }
        else if(mIcon.equals("clear-night")){
            iconId = R.drawable.clear_night;
        }
        else if (mIcon.equals("rain")) {
            iconId = R.drawable.rain;
        }
        else if (mIcon.equals("snow")) {
            iconId = R.drawable.snow;
        }
        else if (mIcon.equals("sleet")) {
            iconId = R.drawable.sleet;
        }
        else if (mIcon.equals("wind")) {
            iconId = R.drawable.wind;
        }
        else if (mIcon.equals("fog")) {
            iconId = R.drawable.fog;
        }
        else if (mIcon.equals("cloudy")) {
            iconId = R.drawable.cloudy;
        }
        else if (mIcon.equals("partly-cloudy-day")) {
            iconId = R.drawable.partly_cloudy;
        }
        else if (mIcon.equals("partly-cloudy-night")) {
            iconId = R.drawable.cloudy_night;
        }
        return iconId;
    }
    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getTemp() {
        return (int)Math.round(mTemp);
    }

    public void setTemp(double temp) {
        mTemp = temp;
    }

    public double getHumid() {
        return mHumid;
    }

    public void setHumid(double humid) {
        mHumid = humid;
    }

    public double getPrecip()
    {
        //converting precipitation ot a percent
        double precipChance = mPrecip*100;

        return (int)Math.round(precipChance);
    }

    public void setPrecip(double precip) {
        mPrecip = precip;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    private double mTemp;
    private double mHumid;
    private double mPrecip;
    private String summary;

}
