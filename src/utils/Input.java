package src.utils;

import java.util.Scanner;

public class Input
{
    private static Scanner scanner;

    static
    {
        scanner = new Scanner(System.in);
    }

    public static String nextLine()
    {
        return scanner.nextLine();
    }
}
