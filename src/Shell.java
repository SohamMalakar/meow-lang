package src;

import java.util.Scanner;

class Shell
{
    public static void main(String[] args)
    {
        try (Scanner scanner = new Scanner(System.in))
        {
            while (true)
            {
                System.out.print("🐈 >>> ");
                String text = scanner.nextLine();

                if (text.trim().isEmpty())
                    continue;

                try
                {
                    Run.run(text);
                }
                catch (Exception e)
                {
                    e.printStackTrace(System.err);
                }
            }
        }
    }
}
