/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bc.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * NOT THREAD SAFE!!!
 * @author bennyl
 */
public class CalendarUtils {

    private static Calendar cd = Calendar.getInstance();
    private static SimpleDateFormat hmformatter = new SimpleDateFormat("HH:mm");

    /**
     *
     * @param month 1-12 format
     * @param year
     * @return
     */
    public static int daysInMonth(int month, int year) {
        cd.set(year, month - 1, 1);
        return cd.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static boolean isBetween(Date start, Date who, Date end) {
        return who.equals(start)
                || who.equals(end)
                || (who.after(start) && who.before(end));
    }

    public static Date dateFor(int year, int month, int day) {
        cd.set(year, month - 1, day);
        return cd.getTime();
    }

    public static SimpleDateFormat getHourMinuteFormatter() {
        return hmformatter;
    }

    public static boolean isTheSameDay(Date a, Date b) {
        return a.getDate() == b.getDate() && a.getYear() == b.getYear() && a.getMonth() == b.getMonth();
    }

    public static boolean isTheSameMonth(Date a, Date b) {
        return a.getYear() == b.getYear() && a.getMonth() == b.getMonth();
    }

    public static enum WeekDays {

        Sunday(Calendar.SUNDAY),
        Monday(Calendar.MONDAY),
        Tuesday(Calendar.TUESDAY),
        Wednesday(Calendar.WEDNESDAY),
        Thursday(Calendar.THURSDAY),
        Friday(Calendar.FRIDAY),
        Saturday(Calendar.SATURDAY);
        int ord;

        private WeekDays(int ord) {
            this.ord = ord;
        }

        public int toDayNumber() {
            return ord;
        }

        public static WeekDays byNumber(int num) {
            for (WeekDays v : values()) {
                if (v.toDayNumber() == num) {
                    return v;
                }
            }

            return null;
        }
    }
}
