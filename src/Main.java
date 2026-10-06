import task1.Task1Main;
import task3.Task3Main;
import task4.Task4Main;

import java.util.Scanner;

/**
 * Лабораторна 5 (потоки введення-виведення): головне меню.
 * Завдання 2 не виконується (виконано Task 3 Mid OOP, пункт 3.3), тому нумерація пунктів
 * збігається з номерами завдань: 1, 3, 4.
 */
public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Лабораторна 5: потоки введення-виведення ===");
            System.out.println("1. Завдання 1: рядок з максимальною кількістю слів у файлі");
            System.out.println("3. Завдання 3: шифрування потоку символів");
            System.out.println("4. Завдання 4: частота тегів на веб-сторінці");
            System.out.println("0. Вихід");
            System.out.print("Ваш вибір: ");
            if (!in.hasNextLine()) {
                return;
            }
            switch (in.nextLine().trim()) {
                case "1" -> Task1Main.run(in);
                case "3" -> Task3Main.run(in);
                case "4" -> Task4Main.run(in);
                case "0" -> {
                    return;
                }
                default -> System.out.println("Невідомий пункт меню.");
            }
        }
    }
}
