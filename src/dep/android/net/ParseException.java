package dep.android.net;

/**
 * 
 *
 * When WebAddress Parser Fails, this exception is thrown
 */
public class ParseException extends RuntimeException {
    public String response;

    ParseException(String response) {
        this.response = response;
    }
}
