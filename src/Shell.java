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
                    String word = scanner.nextLine();
                    word = word.replaceAll("#.*", "");
                    text += word;

                    if (!word.endsWith(";"))
                        break;
                }

                text = text.replaceAll(";", "\n").trim();

                if (text.isEmpty())
                    continue;

                try
                {
                    Run.run(text, false);
                }
                catch (Exception e)
                {
                    e.printStackTrace(System.err);
                }
            }
        }
    }
}
