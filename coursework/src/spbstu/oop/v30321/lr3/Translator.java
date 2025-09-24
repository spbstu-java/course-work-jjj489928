package spbstu.oop.v30321.lr3;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//переводчик текста по словарю поддерживает:
//многословные фразы, апострофы, дефисы
public class Translator {
    private final Map<String, String> dictionary = new HashMap<>(); //ключ -> перевод
    private final String dictionaryFile; //путь к файлу словаря
    private final List<String> phrases = new ArrayList<>(); //все фразы словаря (ключи)
    private final Map<String, List<String>> byHead = new HashMap<>(); //индекс по первому слову

    //шаблон: слово (буквы/цифры), внутри допускаются апострофы и дефисы
    private static final Pattern WORD_PATTERN =
            Pattern.compile("[\\p{L}\\p{N}]+(?:['’‘‛′\\-–—−][\\p{L}\\p{N}]+)*");

    //конструктор: запоминаем путь к словарю
    public Translator(String dictionaryFile) {
        this.dictionaryFile = dictionaryFile;
    }

    //нормализация: регистр, апостроф/дефис, пробелы, вычищаем лишнее
    private String normalize(String s) {
        if (s == null) return ""; //null -> пустая строка
        return s.toLowerCase()    //в нижний регистр
                .replaceAll("[’‘‛′]", "'") //все апострофы -> '
                .replaceAll("[–—−]", "-")  //все дефисы/тире -> -
                .replaceAll("\\s+", " ")   //схлопываем пробелы
                .trim()                                     //убираем крайние пробелы
                .replaceAll("[^a-zA-Zа-яА-Я0-9'\\- ]", ""); //удаляем лишние символы
    }

    //загружаем словарь из файла
    public void loadDictionary() throws InvalidFileFormatException, FileReadException {
        try (BufferedReader reader = new BufferedReader(new FileReader(dictionaryFile))) { //открываем файл
            String line;        //текущая строка
            int lineNumber = 0; //номер строки (для сообщений)

            while ((line = reader.readLine()) != null) { //читаем построчно
                lineNumber++;                            //увеличиваем номер
                if (line.trim().isEmpty()) continue;     //пропускаем пустые строки

                String[] parts = line.split("\\|", 2); //делим на ключ|перевод
                if (parts.length != 2) {                          //проверяем формат
                    throw new InvalidFileFormatException(
                            "Неверный формат строки " + lineNumber + ": " + line);
                }

                String key = normalize(parts[0]);     //нормализуем ключ
                String translation = parts[1].trim(); //перевод как есть

                //проверяем пустые значения
                if (key.isEmpty() || translation.isEmpty()) {
                    throw new InvalidFileFormatException(
                            "Пустое слово или перевод в строке " + lineNumber);
                }

                dictionary.put(key, translation); //сохраняем в словарь
                phrases.add(key);                 //добавляем фразу в список
            }

            //сортируем: сначала по числу слов (длиннее - выше), затем по длине строки
            phrases.sort((a, b) -> {
                int wcA = a.split(" ").length, wcB = b.split(" ").length; //число слов
                return wcA != wcB ? Integer.compare(wcB, wcA)      //больше слов - раньше
                        : Integer.compare(b.length(), a.length()); //при равенстве - длиннее раньше
            });

            //перестраиваем индекс по первому слову фразы
            byHead.clear();                      //очищаем индекс
            for (String phrase : phrases) {      //для каждой фразы
                String head = firstWord(phrase); //берем первое слово
                byHead.computeIfAbsent(head, k -> new ArrayList<>()).add(phrase); //добавляем в индекс
            }

        } catch (FileNotFoundException e) { //файл не найден
            throw new FileReadException("Файл не найден: " + dictionaryFile, e);
        } catch (IOException e) {           //ошибка чтения
            throw new FileReadException("Ошибка чтения файла: " + dictionaryFile, e);
        } catch (RuntimeException e) {            //любая непредвиденная ошибка обработки
            throw new InvalidFileFormatException( //оборачиваем с причиной (с cause)
                    "Ошибка при обработке словаря: " + dictionaryFile, e);
        }
    }

    //переводим текст
    public String translateText(String text) {
        if (text == null || text.trim().isEmpty()) return text; //быстрый выход

        List<String> tokenList = tokenize(text);            //разбиваем текст на токены
        String[] tokens = tokenList.toArray(new String[0]); //в массив для удобства
        StringBuilder result = new StringBuilder();         //сюда собираем результат

        for (int i = 0; i < tokens.length; ) { //проходим по токенам
            if (!isWordToken(tokens[i])) {     //если не слово (пунктуация/пробелы)
                result.append(tokens[i]);      //копируем как есть
                i++;                           //к следующему токену
                continue;                      //и продолжаем
            }

            String headToken = normalize(tokens[i]);                //нормализуем первое слово
            String match = findLongestByHead(tokens, i, headToken); //ищем самую длинную фразу

            if (match != null) { //если нашли фразу
                //добавляем перевод с учетом регистра
                result.append(applyCase(tokens[i], dictionary.get(match)));
                //перескакиваем на конец фразы
                i += countWordsInTokens(tokens, i, match.split(" ").length);
            } else { //если фразы нет
                if (dictionary.containsKey(headToken)) { //пробуем одно слово
                    result.append(applyCase(tokens[i], dictionary.get(headToken))); //переводим
                } else {
                    //иначе оставляем оригинал
                    result.append(tokens[i]);
                }
                i++; //двигаемся дальше
            }
        }
        return result.toString(); //готовый перевод
    }

    //ищем самую длинную фразу с заданным первым словом
    private String findLongestByHead(String[] tokens, int startIndex, String headWord) {
        List<String> candidates = byHead.get(headWord); //берем список по индексу
        if (candidates == null) return null;            //если нет - выходим

        //проверяем фразы по порядку (уже отсортированы)
        for (String phrase : candidates) {
            String[] words = phrase.split(" ");   //слова фразы
            int idx = startIndex;                       //позиция в токенах
            boolean ok = true;                          //флаг совпадения

            for (String w : words) {                    //идем по словам фразы
                //пропускаем разделители
                while (idx < tokens.length && !isWordToken(tokens[idx])) idx++;
                //сравниваем нормализовано
                if (idx >= tokens.length || !normalize(tokens[idx]).equals(w)) {
                    ok = false; break; //не совпало - выходим
                }
                idx++;                 //к следующему слову/токену
            }
            if (ok) return phrase;     //вся фраза совпала - возвращаем
        }
        return null;                   //ничего не подошло
    }

    //токенизация: разбиваем текст на токены (слова и разделители)
    private List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>(); //результат
        Matcher m = WORD_PATTERN.matcher(text);  //ищем слова по шаблону
        int pos = 0;                             //текущая позиция в строке

        while (m.find()) {                       //нашли очередное слово
            //добавляем разделитель
            if (m.start() > pos) tokens.add(text.substring(pos, m.start()));
            //добавляем слово
            tokens.add(text.substring(m.start(), m.end()));
            pos = m.end();                       //двигаем позицию
        }
        //хвост (разделитель)
        if (pos < text.length()) tokens.add(text.substring(pos));
        return tokens;                           //список токенов
    }

    //проверяем, соответствует токен шаблону слова
    private boolean isWordToken(String token) {
        return WORD_PATTERN.matcher(token).matches(); //полное совпадение
    }

    //считаем, сколько токенов занимает N слов
    private int countWordsInTokens(String[] tokens, int startIndex, int wordCount) {
        int words = 0, idx = startIndex;           //счетчики
        while (words < wordCount && idx < tokens.length) {
            if (isWordToken(tokens[idx])) words++; //считаем только слова
            idx++;                                 //двигаемся по токенам
        }
        return idx - startIndex;                   //длина отрезка в токенах
    }

    //возвращаем первое слово фразы
    private String firstWord(String phrase) {
        int sp = phrase.indexOf(' ');                       //ищем первый пробел
        return sp == -1 ? phrase : phrase.substring(0, sp); //одно слово или до пробела
    }

    //применяем регистр оригинала к переводу
    private String applyCase(String original, String translation) {
        //все буквы верхние
        if (isAllUpperCase(original)) return translation.toUpperCase();
        //первая заглавная
        if (!original.isEmpty() && Character.isUpperCase(original.charAt(0)))
            //делаем первую заглавной
            return capitalizeFirstLetter(translation);
        return translation; //иначе как есть
    }

    //проверяем, все ли буквы в верхнем регистре
    private boolean isAllUpperCase(String str) {
        boolean hasLetter = false;         //встречалась ли буква
        for (char c : str.toCharArray()) { //по символам
            if (Character.isLetter(c)) {   //если буква
                hasLetter = true;          //отмечаем
                //не верхний - сразу false
                if (!Character.isUpperCase(c)) return false;
            }
        }
        return hasLetter; //true, если были буквы и все верхние
    }

    //делаем первую букву заглавной
    private String capitalizeFirstLetter(String word) {
        return (word == null || word.isEmpty()) ? word //защита от пустых
                //первая заглавная
                : Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    //возвращаем кол-во записей в словаре
    public int getDictionarySize() {
        return dictionary.size(); //кол-во пар ключ->перевод
    }
}