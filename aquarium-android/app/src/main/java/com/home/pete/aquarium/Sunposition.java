package com.home.pete.aquarium;

/**
 * Created by pete on 5/27/17.
 */

public class Sunposition {

    private double m_latitude;
    private double m_longitude;
    private int m_timeZone;
    private double m_julianDate;

    public Sunposition()
    {
        m_longitude = 0.0;
        m_longitude = 0.0;
        m_timeZone = 0;
    }

    public Sunposition(double lat, double lon, int zone)
    {
        m_latitude = lat;
        m_longitude = lon;
        m_timeZone = zone;
    }

    public void setPosition(double lat, double lon, int zone)
    {
        m_latitude = lat;
        m_longitude = lon;
        m_timeZone = zone;
    }

    private double degToRad(double angleDeg)
    {
        return (Math.PI * angleDeg / 180.0);
    }

    private double radToDeg(double angleRad)
    {
        return (180.0 * angleRad / Math.PI);
    }

    private double calcMeanObliquityOfEcliptic(double t)
    {
        double seconds = 21.448 - t*(46.8150 + t*(0.00059 - t*(0.001813)));
        double e0 = 23.0 + (26.0 + (seconds/60.0))/60.0;

        return e0;              // in degrees
    }

    private double calcGeomMeanLongSun(double t)
    {
        double L = 280.46646 + t * (36000.76983 + 0.0003032 * t);

        while ((int) L > 360) {
            L -= 360.0;
        }

        while (L <  0) {
            L += 360.0;
        }

        return L;              // in degrees
    }

    private double calcObliquityCorrection(double t)
    {
        double e0 = calcMeanObliquityOfEcliptic(t);
        double omega = 125.04 - 1934.136 * t;
        double e = e0 + 0.00256 * Math.cos(degToRad(omega));

        return e;               // in degrees
    }

    private double calcEccentricityEarthOrbit(double t)
    {
        double e = 0.016708634 - t * (0.000042037 + 0.0000001267 * t);
        return e;               // unitless
    }

    private double calcGeomMeanAnomalySun(double t)
    {
        double M = 357.52911 + t * (35999.05029 - 0.0001537 * t);
        return M;               // in degrees
    }

    private double calcEquationOfTime(double t)
    {
        double epsilon = calcObliquityCorrection(t);
        double l0 = calcGeomMeanLongSun(t);
        double e = calcEccentricityEarthOrbit(t);
        double m = calcGeomMeanAnomalySun(t);
        double y = Math.tan(degToRad(epsilon)/2.0);

        y *= y;

        double sin2l0 = Math.sin(2.0 * degToRad(l0));
        double sinm   = Math.sin(degToRad(m));
        double cos2l0 = Math.cos(2.0 * degToRad(l0));
        double sin4l0 = Math.sin(4.0 * degToRad(l0));
        double sin2m  = Math.sin(2.0 * degToRad(m));
        double Etime = y * sin2l0 - 2.0 * e * sinm + 4.0 * e * y * sinm * cos2l0 - 0.5 * y * y * sin4l0 - 1.25 * e * e * sin2m;
        return radToDeg(Etime)*4.0;	// in minutes of time
    }

    private double calcTimeJulianCent(double jd)
    {
        return (jd - 2451545.0)/36525.0;
    }

    private double calcSunTrueLong(double t)
    {
        double l0 = calcGeomMeanLongSun(t);
        double c = calcSunEqOfCenter(t);

        return l0 + c;               // in degrees
    }

    private double calcSunApparentLong(double t)
    {
        double o = calcSunTrueLong(t);

        double  omega = 125.04 - 1934.136 * t;
        return o - 0.00569 - 0.00478 * Math.sin(degToRad(omega));          // in degrees
    }

    private double calcSunDeclination(double t)
    {
        double e = calcObliquityCorrection(t);
        double lambda = calcSunApparentLong(t);

        double sint = Math.sin(degToRad(e)) * Math.sin(degToRad(lambda));
        return radToDeg(Math.asin(sint));           // in degrees
    }

    private double calcHourAngleSunrise(double lat, double solarDec)
    {
        double latRad = degToRad(lat);
        double sdRad  = degToRad(solarDec);

        return (Math.acos(Math.cos(degToRad(90.833))/(Math.cos(latRad)*Math.cos(sdRad))-Math.tan(latRad) * Math.tan(sdRad)));              // in radians
    }

    private double calcHourAngleSunset(double lat, double solarDec)
    {
        double latRad = degToRad(lat);
        double sdRad  = degToRad(solarDec);
        double HA = (Math.acos(Math.cos(degToRad(90.833))/(Math.cos(latRad)*Math.cos(sdRad))-Math.tan(latRad) * Math.tan(sdRad)));

        return -HA;              // in radians
    }

    private double calcJD(int y, int m, int d)
    {
        if (m <= 2) {
            y -= 1;
            m += 12;
        }
        double A = Math.floor(y/100);
        double B = 2 - A + Math.floor(A/4);

        return Math.floor(365.25*(y + 4716)) + Math.floor(30.6001*(m+1)) + d + B - 1524.5;
    }

    private double calcJDFromJulianCent(double t)
    {
        return t * 36525.0 + 2451545.0;
    }

    private double calcSunEqOfCenter(double t)
    {
        double m = calcGeomMeanAnomalySun(t);
        double mrad = degToRad(m);
        double sinm = Math.sin(mrad);
        double sin2m = Math.sin(mrad+mrad);
        double sin3m = Math.sin(mrad+mrad+mrad);

        return sinm * (1.914602 - t * (0.004817 + 0.000014 * t)) + sin2m * (0.019993 - 0.000101 * t) + sin3m * 0.000289;		// in degrees
    }

    public double calcSunriseUTC()
    {
        double t = calcTimeJulianCent(m_julianDate);
        // *** First pass to approximate sunrise
        double  eqTime = calcEquationOfTime(t);
        double  solarDec = calcSunDeclination(t);
        double  hourAngle = calcHourAngleSunrise(m_latitude, solarDec);
        double  delta = m_longitude - radToDeg(hourAngle);
        double  timeDiff = 4 * delta;	// in minutes of time
        double  timeUTC = 720 + timeDiff - eqTime;	// in minutes
        double  newt = calcTimeJulianCent(calcJDFromJulianCent(t) + timeUTC/1440.0);

        eqTime = calcEquationOfTime(newt);
        solarDec = calcSunDeclination(newt);

        hourAngle = calcHourAngleSunrise(m_latitude, solarDec);
        delta = m_longitude - radToDeg(hourAngle);
        timeDiff = 4 * delta;
        timeUTC = 720 + timeDiff - eqTime; // in minutes

        return timeUTC;
    }

    public double calcSunrise()
    {
        double t = calcTimeJulianCent(m_julianDate);
        // *** First pass to approximate sunrise
        double  eqTime = calcEquationOfTime(t);
        double  solarDec = calcSunDeclination(t);
        double  hourAngle = calcHourAngleSunrise(m_latitude, solarDec);
        double  delta = m_longitude - radToDeg(hourAngle);
        double  timeDiff = 4 * delta;	// in minutes of time
        double  timeUTC = 720 + timeDiff - eqTime;	// in minutes
        double  newt = calcTimeJulianCent(calcJDFromJulianCent(t) + timeUTC/1440.0);

        eqTime = calcEquationOfTime(newt);
        solarDec = calcSunDeclination(newt);

        hourAngle = calcHourAngleSunrise(m_latitude, solarDec);
        delta = m_longitude - radToDeg(hourAngle);
        timeDiff = 4 * delta;
        timeUTC = 720 + timeDiff - eqTime; // in minutes

        return timeUTC + (60 * m_timeZone);	// return time in minutes from midnight
    }

    public double calcSunsetUTC()
    {
        double t = calcTimeJulianCent(m_julianDate);
        // *** First pass to approximate sunset
        double  eqTime = calcEquationOfTime(t);
        double  solarDec = calcSunDeclination(t);
        double  hourAngle = calcHourAngleSunset(m_latitude, solarDec);
        double  delta = m_longitude - radToDeg(hourAngle);
        double  timeDiff = 4 * delta;	// in minutes of time
        double  timeUTC = 720 + timeDiff - eqTime;	// in minutes
        double  newt = calcTimeJulianCent(calcJDFromJulianCent(t) + timeUTC/1440.0);

        eqTime = calcEquationOfTime(newt);
        solarDec = calcSunDeclination(newt);

        hourAngle = calcHourAngleSunset(m_latitude, solarDec);
        delta = m_longitude - radToDeg(hourAngle);
        timeDiff = 4 * delta;

        return 720 + timeDiff - eqTime;	// return time in minutes from midnight
    }

    public double calcSunset()
    {
        double t = calcTimeJulianCent(m_julianDate);
        // *** First pass to approximate sunset
        double  eqTime = calcEquationOfTime(t);
        double  solarDec = calcSunDeclination(t);
        double  hourAngle = calcHourAngleSunset(m_latitude, solarDec);
        double  delta = m_longitude - radToDeg(hourAngle);
        double  timeDiff = 4 * delta;	// in minutes of time
        double  timeUTC = 720 + timeDiff - eqTime;	// in minutes
        double  newt = calcTimeJulianCent(calcJDFromJulianCent(t) + timeUTC/1440.0);

        eqTime = calcEquationOfTime(newt);
        solarDec = calcSunDeclination(newt);

        hourAngle = calcHourAngleSunset(m_latitude, solarDec);
        delta = m_longitude - radToDeg(hourAngle);
        timeDiff = 4 * delta;
        timeUTC = 720 + timeDiff - eqTime; // in minutes

        return timeUTC + (60 * m_timeZone);	// return time in minutes from midnight
    }

    public double setCurrentDate(int y, int m, int d)
    {
        m_julianDate = calcJD(y, m, d);
        return m_julianDate;
    }

    public void setTZOffset(int tz)
    {
        m_timeZone = tz;
    }

    public int moonPhase(long fromepoch)
    {
        long moon = (fromepoch - 74100) % 2551443;
        double phase = Math.floor(moon / (24 * 3600)) + 1;
        return (int)phase;
    }
}
