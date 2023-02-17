package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import src.utils.Input;

class Shell
{
    public static void main(String[] args) throws IOException
    {
        if (args.length != 0)
        {
            Arguments.values = args;

            File file = new File(args[0]);
            String buffer = "";

            if (file.exists() && file.canRead())
            {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line;

                while ((line = bufferedReader.readLine()) != null)
                    buffer += line + "\n";

                bufferedReader.close();
            }
            else
            {
                System.err.println("meow: can't open file '" + args[0] + "': No such file or directory");
                System.exit(1);
            }

            try
            {
                Run.run(buffer, true);
            }
            catch (Exception e)
            {
                e.printStackTrace(System.err);
            }

            return;
        }

        while (true)
        {
            System.out.print("🐈 >>> ");
            String text = "";

            while (true)
            {
                String word = Input.nextLine();
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
