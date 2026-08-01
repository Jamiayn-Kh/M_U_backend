package mn.mungunurlal.moldorder.exception;

public class MoldOrderNotFoundException extends RuntimeException {

    public MoldOrderNotFoundException(Long id) {
        super("Хэвний хүсэлт олдсонгүй: " + id);
    }
}