import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author dingchenchen
 * @since 2020/9/15
 */
public class Test1 {

    public static void main(String[] args) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse("2019-10-31");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(DateUtil.offset(date, DateField.MONTH,4));
        System.out.println(DateUtil.dayOfMonth(date));

        LocalDateTime localDateTime = dateToLocalDateTime(date).plusMonths(4);
        System.out.println(localDateTimeToDate(localDateTime));
    }

    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            throw new IllegalArgumentException("The parameter localDateTime is null");
        }
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        if (null == date) {
            throw new IllegalArgumentException("The parameter date is null");
        }

        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }
}
