import java.security.MessageDigest;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author dingchenchen
 * @since 2020/9/15
 */
public class Test1 {

    public static void main(String[] args) throws ParseException {
        int num = Integer.MAX_VALUE;
        System.out.println(num << 1);
        System.out.println(num >> 2);
    }

    public static String md5(String input) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte b[] = md.digest();
            int i;
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    sb.append("0");
                sb.append(Integer.toHexString(i));
            }
        } catch (Exception e) {
            return null;
        }
        return sb.toString();
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
