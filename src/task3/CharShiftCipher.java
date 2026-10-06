package task3;

/**
 * Шифр з завдання 3: кожен символ замінюється символом, код якого більший
 * на код символа-ключа.
 *
 * Щоб результат завжди був коректним символом, зсув робиться "по колу":
 * коди рахуються у просторі з N = 0xF800 допустимих символів (усі 16-бітні коди
 * крім сурогатних 0xD800-0xDFFF), а після останнього символа йде перший.
 * Завдяки цьому decrypt(encrypt(c)) == c для будь-якого символа.
 * Сурогатні пари (емодзі тощо) шифр не змінює - їх половинки лишаються як є,
 * тож такий текст теж не псується.
 */
public class CharShiftCipher {

    private static final int SURROGATE_START = 0xD800;
    private static final int SURROGATE_COUNT = 0x0800;      // 0xD800..0xDFFF
    private static final int SPACE_SIZE = 0x10000 - SURROGATE_COUNT; // 0xF800

    private final int shift;

    /** @param key символ-ключ; його код і є величиною зсуву (сурогатний символ як ключ не дозволений) */
    public CharShiftCipher(char key) {
        if (Character.isSurrogate(key)) {
            throw new IllegalArgumentException("Ключ не може бути сурогатним символом");
        }
        this.shift = toIndex(key) % SPACE_SIZE;
    }

    /** Шифрує один символ: код збільшується на код ключа. */
    public char encrypt(char c) {
        if (Character.isSurrogate(c)) {
            return c;
        }
        return fromIndex((toIndex(c) + shift) % SPACE_SIZE);
    }

    /** Розшифровує один символ: код зменшується на код ключа. */
    public char decrypt(char c) {
        if (Character.isSurrogate(c)) {
            return c;
        }
        return fromIndex(Math.floorMod(toIndex(c) - shift, SPACE_SIZE));
    }

    /** Шифрує рядок. */
    public String encrypt(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            sb.append(encrypt(text.charAt(i)));
        }
        return sb.toString();
    }

    /** Розшифровує рядок. */
    public String decrypt(String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            sb.append(decrypt(text.charAt(i)));
        }
        return sb.toString();
    }

    /** Код символа -> порядковий номер у просторі без сурогатів. */
    private static int toIndex(char c) {
        return c < SURROGATE_START ? c : c - SURROGATE_COUNT;
    }

    /** Порядковий номер у просторі без сурогатів -> символ. */
    private static char fromIndex(int index) {
        return (char) (index < SURROGATE_START ? index : index + SURROGATE_COUNT);
    }
}
