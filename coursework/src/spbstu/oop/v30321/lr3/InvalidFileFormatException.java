package spbstu.oop.v30321.lr3;

//исключение для ошибок формата файла словаря
public class InvalidFileFormatException extends Exception {
    //создает исключение с сообщением
    public InvalidFileFormatException(String message) {
        super(message);
    }

    //создает исключение с сообщением и причиной
    public InvalidFileFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}