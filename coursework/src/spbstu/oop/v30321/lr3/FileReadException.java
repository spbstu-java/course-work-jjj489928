package spbstu.oop.v30321.lr3;

//исключение для ошибок чтения файлов
public class FileReadException extends Exception {
    //создает исключение с сообщением
    public FileReadException(String message) {
        super(message);
    }

    //создает исключение с сообщением и причиной
    public FileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}