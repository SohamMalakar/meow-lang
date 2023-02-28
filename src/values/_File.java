package src.values;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;

// TODO: implement encoding
public class _File extends _Value
{
    private File file;
    private RandomAccessFile randomAccessFile;

    private String filename;
    private String mode;

    private String writeBuffer;
    private String encoding;

    private int buffering;
    private long pointer;

    private boolean closed;
    private boolean readable;
    private boolean writable;

    private boolean textMode;

    public _File(String filename, String mode, int buffering, String encoding) throws Exception
    {
        this.filename = filename;
        this.mode = mode;
        this.buffering = buffering;
        this.encoding = encoding;

        this.closed = false;
        this.writeBuffer = "";

        this.file = new File(filename);

        switch (mode)
        {
        case "r":
            this.pointer = 0;
            this.readable = true;
            this.writable = false;
            this.randomAccessFile = file.exists() ? new RandomAccessFile(filename, "r") : null;
            this.textMode = true;
            break;

        case "w":
            this.pointer = 0;
            this.readable = false;
            this.writable = true;
            this.randomAccessFile = new RandomAccessFile(filename, "rw");
            new PrintWriter(filename).close(); // delete contents
            this.textMode = true;
            break;

        case "a":
            this.pointer = size();
            this.readable = false;
            this.writable = true;
            this.randomAccessFile = new RandomAccessFile(filename, "rw");
            this.textMode = true;
            break;

        case "x":
            this.pointer = 0;
            this.readable = false;
            this.writable = true;
            this.randomAccessFile = file.exists() ? null : new RandomAccessFile(filename, "rw");
            this.textMode = true;
            break;

        case "r+":
            this.pointer = 0;
            this.readable = true;
            this.writable = true;
            // this.randomAccessFile = new RandomAccessFile(filename, "rw");
            this.randomAccessFile = file.exists() ? new RandomAccessFile(filename, "rw") : null;
            this.textMode = true;
            break;

        case "w+":
            this.pointer = 0;
            this.readable = true;
            this.writable = true;
            this.randomAccessFile = new RandomAccessFile(filename, "rw");
            new PrintWriter(filename).close(); // delete contents
            this.textMode = true;
            break;

        case "a+":
            this.pointer = size();
            this.readable = true;
            this.writable = true;
            this.randomAccessFile = new RandomAccessFile(filename, "rw");
            this.textMode = true;
            break;

        default:
            throw new Exception("ValueError: invalid mode: '" + mode + "'");
        }
    }

    public String rawValue() throws Exception
    {
        return "<file name='" + filename + "' mode='" + mode + "' buffering=" + buffering + " encoding='" + encoding +
            "'>";
    }

    public String value() throws Exception
    {
        return rawValue();
    }

    public String type()
    {
        return "file";
    }

    public int size()
    {
        return file.exists() ? (int)file.length() : 0;
    }

    public File getFile()
    {
        return file;
    }

    public RandomAccessFile getRandomAccessFile()
    {
        return randomAccessFile;
    }

    public String getWriteBuffer()
    {
        return writeBuffer;
    }

    public long getPointer() throws Exception
    {
        if (closed)
            throw new Exception("ValueError: I/O operation on closed file.");

        return pointer;
    }

    public long seek(int offset, int position) throws Exception
    {
        if (closed)
            throw new Exception("ValueError: I/O operation on closed file.");

        doFlush();

        if (position == 0)
            pointer = offset;
        else if (position == 1)
            pointer = (int)(offset + pointer);
        else if (position == 2)
            pointer = offset + size();
        else
            throw new Exception("ValueError: invalid whence (" + position + ", should be 0, 1 or 2)");

        randomAccessFile.seek(pointer);
        return pointer;
    }

    public boolean isClosed()
    {
        return closed;
    }

    public boolean isReadable() throws Exception
    {
        if (closed)
            throw new Exception("ValueError: I/O operation on closed file.");

        return readable;
    }

    public boolean isWritable() throws Exception
    {
        if (closed)
            throw new Exception("ValueError: I/O operation on closed file.");

        return writable;
    }

    public _Value getComparisonEq(_Value other) throws Exception
    {
        if (!other.type().equals("file"))
            return new _Bool("false");

        // TODO: implement this
        return new _Bool("false");
    }

    public _Value getComparisonNe(_Value other) throws Exception
    {
        if (!other.type().equals("file"))
            return new _Bool("true");

        // TODO: implement this
        return new _Bool("true");
    }

    public void read(byte[] buffer) throws Exception
    {
        if (closed)
            throw new Exception("ValueError: I/O operation on closed file.");

        if (!readable)
            throw new Exception("UnsupportedOperation: not readable");

        if (textMode)
        {
            doFlush();
            randomAccessFile.read(buffer);
            pointer += buffer.length;
        }
    }

    public String readline() throws Exception
    {
        if (closed)
            throw new Exception("ValueError: I/O operation on closed file.");

        if (!readable)
            throw new Exception("UnsupportedOperation: not readable");

        if (textMode)
        {
            doFlush();
            String buffer = "";

            int size = size();

            while (pointer < size)
            {
                char c = (char)randomAccessFile.read();
                pointer += 1;
                if (c == '\n')
                    break;
                buffer += c;
            }

            return buffer;
        }

        return null;
    }

    public ArrayList<_Value> readlines() throws Exception
    {
        if (textMode)
        {
            ArrayList<_Value> strings = new ArrayList<>();

            int size = size();

            while (pointer < size)
                strings.add(new _String(readline()));

            return strings;
        }

        return null;
    }

    public void write(byte[] bytes) throws Exception
    {
        if (closed)
            throw new Exception("ValueError: I/O operation on closed file.");

        if (!writable)
            throw new Exception("UnsupportedOperation: not writable");

        if (textMode)
        {
            String temp;
            writeBuffer += new String(bytes);

            while (writeBuffer.length() > buffering)
            {
                temp = writeBuffer.substring(0, buffering);
                writeBuffer = writeBuffer.substring(buffering);
                pointer += temp.length();
                randomAccessFile.write(temp.getBytes());
            }
        }
    }

    public void writelines(_List list) throws Exception
    {
        if (closed)
            throw new Exception("ValueError: I/O operation on closed file.");

        if (!writable)
            throw new Exception("UnsupportedOperation: not writable");

        if (textMode)
        {
            for (int i = 0; i < list.size(); i++)
            {
                var index = new _Number("int", String.valueOf(i));
                var element = list.get(index);

                if (!element.type().equals("str"))
                    throw new Exception("TypeError: write() argument must be str, not " + element.type());

                write(element.rawValue().getBytes());
            }
        }
    }

    public void flush() throws Exception
    {
        if (closed)
            throw new Exception("ValueError: I/O operation on closed file.");

        doFlush();
    }

    private void doFlush() throws IOException
    {
        if (textMode)
        {
            pointer += writeBuffer.length();
            randomAccessFile.write(writeBuffer.getBytes());
            writeBuffer = ""; // resetting the write buffer
        }
    }

    public void close() throws Exception
    {
        if (textMode)
        {
            doFlush();
            randomAccessFile.close();
            closed = true;
        }
    }
}
