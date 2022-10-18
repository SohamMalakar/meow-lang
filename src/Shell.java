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

                String text = "";

                while (true)
                {
                    text += scanner.nextLine();
                    text = text.trim();

                    if (text.isEmpty())
                        break;

                    var endChar = text.charAt(text.length() - 1);

                    if (endChar != ';')
                        break;
                }

                if (text.isEmpty())
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
