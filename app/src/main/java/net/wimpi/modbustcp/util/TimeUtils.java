package net.wimpi.modbustcp.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 在工具类中经常使用到工具类的格式化描述，这个主要是一个日期的操作类，所以日志格式主要使用 SimpleDateFormat的定义格式.
 */
public class TimeUtils {

    private TimeUtils() {
        throw new UnsupportedOperationException("u can't fuck me...");
    }

    /**
     * <p/>
     * 在工具类中经常使用到工具类的格式化描述，这个主要是一个日期的操作类，所以日志格式主要使用 SimpleDateFormat的定义格式.
     * <p/>
     * 格式的意义如下： 日期和时间模式 <br>
     * 日期和时间格式由日期和时间模式字符串指定。在日期和时间模式字符串中，未加引号的字母 'A' 到 'Z' 和 'a' 到 'z'
     * 被解释为模式字母，用来表示日期或时间字符串元素。文本可以使用单引号 (') 引起来，以免进行解释。"''"
     * 表示单引号。所有其他字符均不解释；只是在格式化时将它们简单复制到输出字符串，或者在分析时与输入字符串进行匹配。
     * <p/>
     * 定义了以下模式字母（所有其他字符 'A' 到 'Z' 和 'a' 到 'z' 都被保留）： <br>
     * <table>
     * <tr>  <td>字母</td>  <td>日期或时间元素</td>  <td>表示</td>  <td>示例</td>  <td> </td></tr>
     * <tr>  <td>G</td>  <td>Era</td>  <td>标志符</td>  <td>Text</td>  <td>AD</td>  <td> </td></tr>
     * <tr> <td>y</td>  <td>年</td>  <td>Year</td>  <td>1996;</td>  <td>96</td>  <td> </td></tr>
     * <tr>  <td>M</td>  <td>年中的月份</td>  <td>Month</td>  <td>July;</td>  <td>Jul;</td>  <td>07
     * </td></tr>
     * <tr>  <td>w</td>  <td>年中的周数</td>  <td>Number</td>  <td>27</td>  <td> </td></tr>
     * <tr>  <td>W</td>  <td>月份中的周数</td>  <td>Number</td>  <td>2</td>  <td> </td></tr>
     * <tr>  <td>D</td>  <td>年中的天数</td>  <td>Number</td>  <td>189</td>  <td> </td></tr>
     * <tr>  <td>d</td>  <td>月份中的天数</td>  <td>Number</td>  <td>10</td>  <td> </td></tr>
     * <tr>  <td>F</td>  <td>月份中的星期</td>  <td>Number</td>  <td>2</td>  <td> </td></tr>
     * <tr>  <td>E</td>  <td>星期中的天数</td>  <td>Text</td>  <td>Tuesday;</td>  <td>Tue </td></tr>
     * <tr>  <td>a</td>  <td>Am/pm</td>  <td>标记</td>  <td>Text</td>  <td>PM</td>  <td> </td></tr>
     * <tr>  <td>H</td>  <td>一天中的小时数（0-23）</td>  <td>Number</td>  <td>0 </td></tr>
     * <tr>  <td>k</td>  <td>一天中的小时数（1-24）</td>  <td>Number</td>  <td>24</td>  <td> </td></tr>
     * <tr>  <td>K</td>  <td>am/pm</td>  <td>中的小时数（0-11）</td>  <td>Number</td>  <td>0</td>  <td>
     * <p>
     * </td></tr>
     * <tr>  <td>h</td>  <td>am/pm</td>  <td>中的小时数（1-12）</td>  <td>Number</td>  <td>12</td>  <td>
     * <p>
     * </td></tr>
     * <tr>  <td>m</td>  <td>小时中的分钟数</td>  <td>Number</td>  <td>30</td>  <td> </td></tr>
     * <tr>  <td>s</td>  <td>分钟中的秒数</td>  <td>Number</td>  <td>55</td>  <td> </td></tr>
     * <tr>  <td>S</td>  <td>毫秒数</td>  <td>Number</td>  <td>978</td>  <td> </td></tr>
     * <tr>  <td>z</td>  <td>时区</td>  <td>General</td>  <td>time</td>  <td>zone</td>
     * <td>Pacific</td>  <td>Standard</td> <td>Time;</td>  <td>PST;</td>  <td>GMT-08:00 </td></tr>
     * <tr>  <td>Z</td>  <td>时区</td>  <td>RFC</td>  <td>822</td>  <td>time</td>  <td>zone</td>
     * <td>-0800</td>  <td> </td></tr>
     * </table>
     * <p/>
     * <p/>
     * <pre>
     *                     yyyy-MM-dd 1969-12-31
     *                     yyyy-MM-dd 1970-01-01
     *               yyyy-MM-dd HH:mm 1969-12-31 16:00
     *               yyyy-MM-dd HH:mm 1970-01-01 00:00
     *              yyyy-MM-dd HH:mmZ 1969-12-31 16:00-0800
     *              yyyy-MM-dd HH:mmZ 1970-01-01 00:00+0000
     *       yyyy-MM-dd HH:mm:ss.SSSZ 1969-12-31 16:00:00.000-0800
     *       yyyy-MM-dd HH:mm:ss.SSSZ 1970-01-01 00:00:00.000+0000
     *     yyyy-MM-dd'T'HH:mm:ss.SSSZ 1969-12-31T16:00:00.000-0800
     *     yyyy-MM-dd'T'HH:mm:ss.SSSZ 1970-01-01T00:00:00.000+0000
     * </pre>
     */
    public static final SimpleDateFormat DEFAULT_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * 各时间单位与毫秒的倍数
     */
    public static final int UNIT_MSEC = 1;
    public static final int UNIT_SEC = 1000;
    public static final int UNIT_MIN = 60000;
    public static final int UNIT_HOUR = 3600000;
    public static final int UNIT_DAY = 86400000;

    /**
     * 将时间戳转为时间字符串
     * <p>格式为yyyy-MM-dd HH:mm:ss
     */
    public static String milliseconds2String(long milliseconds) {
        return milliseconds2String(milliseconds, DEFAULT_SDF);
    }

    /**
     * 将时间戳转为时间字符串
     * <p>格式为用户自定义
     */
    public static String milliseconds2String(long milliseconds, SimpleDateFormat format) {
        return format.format(new Date(milliseconds));
    }

    /**
     * 将时间字符串转为时间戳
     * <p>格式为yyyy-MM-dd HH:mm:ss
     */
    public static long string2Milliseconds(String time) {
        return string2Milliseconds(time, DEFAULT_SDF);
    }

    /**
     * 将时间字符串转为时间戳
     * <p>格式为用户自定义
     */
    public static long string2Milliseconds(String time, SimpleDateFormat format) {
        try {
            return format.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 将时间字符串转为Date类型
     * <p>格式为yyyy-MM-dd HH:mm:ss
     */
    public static Date string2Date(String formatDate) {
        return string2Date(formatDate, DEFAULT_SDF);
    }

    /**
     * 将时间字符串转为Date类型
     * <p>格式为用户自定义
     */
    public static Date string2Date(String formatDate, SimpleDateFormat format) {
        return new Date(string2Milliseconds(formatDate, format));
    }

    /**
     * 将Date类型转为时间字符串
     * <p>格式为yyyy-MM-dd HH:mm:ss
     */
    public static String date2String(Date date) {
        return date2String(date, DEFAULT_SDF);
    }

    /**
     * 将Date类型转为时间字符串
     * <p>格式为用户自定义
     */
    public static String date2String(Date date, SimpleDateFormat format) {
        return format.format(date);
    }

    /**
     * 将Date类型转为时间戳
     */
    public static long date2Milliseconds(Date date) {
        return date.getTime();
    }

    /**
     * 将时间戳转为Date类型
     */
    public static Date milliseconds2Date(long milliseconds) {
        return new Date(milliseconds);
    }

    /**
     * 毫秒时间戳单位转换（单位：unit）
     * <pre>
     * UNIT_MSEC:毫秒
     * UNIT_SEC :秒
     * UNIT_MIN :分
     * UNIT_HOUR:小时
     * UNIT_DAY :天
     * </pre>
     */
    private static long milliseconds2Unit(long milliseconds, int unit) {
        switch (unit) {
            case UNIT_MSEC:
            case UNIT_SEC:
            case UNIT_MIN:
            case UNIT_HOUR:
            case UNIT_DAY:
                return Math.abs(milliseconds) / unit;
        }
        return -1;
    }

    /**
     * 获取两个时间差（单位：unit）
     * <pre>
     * UNIT_MSEC:毫秒
     * UNIT_SEC :秒
     * UNIT_MIN :分
     * UNIT_HOUR:小时
     * UNIT_DAY :天
     * </pre>
     * <p>time1和time2格式都为yyyy-MM-dd HH:mm:ss
     */
    public static long getIntervalTime(String time1, String time2, int unit) {
        return getIntervalTime(time1, time2, unit, DEFAULT_SDF);
    }

    /**
     * 获取两个时间差（单位：unit）
     * <pre>
     * UNIT_MSEC:毫秒
     * UNIT_SEC :秒
     * UNIT_MIN :分
     * UNIT_HOUR:小时
     * UNIT_DAY :天
     * </pre>
     * <p>time1和time2格式都为format
     */
    public static long getIntervalTime(String time1, String time2, int unit, SimpleDateFormat
            format) {
        return milliseconds2Unit(string2Milliseconds(time1, format)
                - string2Milliseconds(time2, format), unit);
    }

    /**
     * 获取两个时间差（单位：unit）
     * <pre>
     * UNIT_MSEC:毫秒
     * UNIT_SEC :秒
     * UNIT_MIN :分
     * UNIT_HOUR:小时
     * UNIT_DAY :天
     * </pre>
     * <p>time1和time2都为Date
     */
    public static long getIntervalTime(Date time1, Date time2, int unit) {
        return milliseconds2Unit(date2Milliseconds(time2) - date2Milliseconds(time1), unit);
    }

    /**
     * 获取当前时间
     * <p>单位（毫秒）
     */
    public static long getCurTimeMills() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前时间
     * <p>格式为yyyy-MM-dd HH:mm:ss
     */
    public static String getCurTimeString() {
        return milliseconds2String(getCurTimeMills());
    }

    /**
     * 获取当前时间
     * <p>格式为用户自定义
     */
    public static String getCurTimeString(SimpleDateFormat format) {
        return milliseconds2String(getCurTimeMills(), format);
    }

    /**
     * 获取当前时间
     * <p>Date类型
     */
    public static Date getCurTimeDate() {
        return new Date();
    }

    /**
     * 获取与当前时间的差（单位：unit）
     * <pre>
     * UNIT_MSEC:毫秒
     * UNIT_SEC :秒
     * UNIT_MIN :分
     * UNIT_HOUR:小时
     * UNIT_DAY :天
     * <p>time1和time2格式都为yyyy-MM-dd HH:mm:ss
     */
    public static long getIntervalByNow(String time, int unit) {
        return getIntervalByNow(time, unit, DEFAULT_SDF);
    }

    /**
     * 获取与当前时间的差（单位：unit）
     * <pre>
     * UNIT_MSEC:毫秒
     * UNIT_SEC :秒
     * UNIT_MIN :分
     * UNIT_HOUR:小时
     * UNIT_DAY :天
     * <p>time1和time2格式都为format
     */
    public static long getIntervalByNow(String time, int unit, SimpleDateFormat format) {
        return getIntervalTime(getCurTimeString(), time, unit, format);
    }

    /**
     * 获取与当前时间的差（单位：unit）
     * <pre>
     * UNIT_MSEC:毫秒
     * UNIT_SEC :秒
     * UNIT_MIN :分
     * UNIT_HOUR:小时
     * UNIT_DAY :天
     * <p>time1和time2格式都为format
     */
    public static long getIntervalByNow(Date time, int unit) {
        return getIntervalTime(getCurTimeDate(), time, unit);
    }


    /**
     * @param phpTime php 返回的时间撮
     * @return java 时间撮
     */
    public static long phpToJavaTime(long phpTime) {
        long t = phpTime;
        if (t <= 0) {
            return System.currentTimeMillis();
        }
        t *= 1000;
        return t;
    }

    /**
     * 判断闰年
     */
    public static boolean isLeapYear(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }


    public static boolean isThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(new Date(time));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        if (paramWeek == currentWeek) {
            return true;
        }
        return false;
    }

    /**
     * 判断选择的日期是否是今天
     *
     * @param timestamp 时间戳
     * @return
     */
    public static boolean isToday(long timestamp) {

        return isThisTime(timestamp, Calendar.DAY_OF_MONTH);
    }


    /**
     * 判断是否是当月
     *
     * @param timestamp
     * @return
     */
    public static boolean isThisMonth(long timestamp) {

        return isThisTime(timestamp, Calendar.MONTH);
    }


    /**
     * 判断是否是今年
     *
     * @param timestamp 时间戳
     * @return
     */
    public static boolean isThisYear(long timestamp) {
        return isThisTime(timestamp, Calendar.YEAR);
    }

    /**
     * 传入时间是周几
     *
     * @param timestamp
     * @return
     */
    public static int dayOfWeek(long timestamp) {
        Date date = new Date(timestamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return (calendar.get(Calendar.DAY_OF_WEEK) - 1) % 7;
    }

    public static boolean isThisTime(long timestamp, int field) {
        Date date = new Date(timestamp);
        Calendar now = Calendar.getInstance();
        int nowTime = now.get(field);
        now.setTime(date);
        int time = now.get(field);
        Log.d("isThisTime", "time: " + time + "\n nowTime:" + nowTime);
        if (time == nowTime) {
            return true;
        } else {
            return false;
        }
    }


    private static final long MINUTE_SECONDS = 60; //1分钟多少秒
    private static final long HOUR_SECONDS = MINUTE_SECONDS * 60;
    private static final long DAY_SECONDS = HOUR_SECONDS * 24;
    private static final long YEAR_SECONDS = DAY_SECONDS * 365;


    public static String passNowTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long timeLong = 0l;
        try {
            Date date = sdf.parse(time);
            timeLong = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return getStandardDate(timeLong);
    }

    public static String getStandardDate(long timeLong) {

        boolean simpleTime = false;

        StringBuffer sb = new StringBuffer();

        long time = System.currentTimeMillis() - timeLong;
        long mill = (long) Math.ceil(time / 1000);//秒前

        long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前

        long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时

        long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前

        if (day - 1 > 0) {
            if (isThisYear(timeLong)) {
                sb.append(milliseconds2String(timeLong, new SimpleDateFormat("MM.dd HH:mm")));
            } else {
                sb.append(milliseconds2String(timeLong, new SimpleDateFormat("yyyy.MM.dd " +
                        "HH:mm")));
            }
            simpleTime = true;
        } else if (hour - 1 > 0) {
            if (hour >= 24) {
                if (isThisYear(timeLong)) {
                    sb.append(milliseconds2String(timeLong, new SimpleDateFormat("MM.dd " +
                            "HH:mm")));
                } else {
                    sb.append(milliseconds2String(timeLong, new SimpleDateFormat("yyyy.MM.dd " +
                            "HH:mm")));
                }
                simpleTime = true;
            } else {
                sb.append(hour + "小时");
            }
        } else if (minute - 1 > 0) {
            if (minute == 60) {
                sb.append("1小时");
            } else {
                sb.append(minute + "分钟");
            }
        } else if (mill - 1 > 0) {
            if (mill == 60) {
                sb.append("1分钟");
            } else {
                sb.append(mill + "秒");
            }
        } else {
            sb.append("刚刚");
        }
        if (!sb.toString().equals("刚刚") && !simpleTime) {
            sb.append("前");
        }
        return sb.toString();
    }


    public static String standardDate(long timeLong, String thisYearFormat, String lastYearFormat) {

        long diff = System.currentTimeMillis() - timeLong;
        long r = 0;
        if (diff > year) {
            return milliseconds2String(timeLong, new SimpleDateFormat(lastYearFormat));
        }
        if (diff > month) {
            return milliseconds2String(timeLong, new SimpleDateFormat(thisYearFormat));
        }

        if (diff >= 7 * day && diff < month) {
            return milliseconds2String(timeLong, new SimpleDateFormat(thisYearFormat));
        }

        if (diff > day && diff < 7 * day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "个小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }


    private final static long minute = 60 * 1000;// 1分钟
    private final static long hour = 60 * minute;// 1小时
    private final static long day = 24 * hour;// 1天
    private final static long month = 31 * day;// 月
    private final static long year = 12 * month;// 年

    /**
     * 返回文字描述的日期
     *
     * @return
     */
    public static String timeFormat(long timeLong, String thisYearFormat, String
            lastYearFormat) {

        long diff = System.currentTimeMillis() - timeLong;
        long r = 0;
        if (diff > year) {
            return milliseconds2String(timeLong, new SimpleDateFormat(lastYearFormat));
        }
        if (diff > month) {
            return milliseconds2String(timeLong, new SimpleDateFormat(thisYearFormat));
        }

        if (diff >= 7 * day && diff < month) {
            return milliseconds2String(timeLong, new SimpleDateFormat(thisYearFormat));
        }

        if (diff > day && diff < 7 * day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "个小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }


}