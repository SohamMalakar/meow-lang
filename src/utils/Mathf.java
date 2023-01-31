package src.utils;

public class Mathf
{
    public static double modulus(double x, double y)
    {
        return x - y * Math.floor(x / y);
    }

    public static int clamp(int val, int x, int y)
    {
        if (val > y)
            return y;
        else if (val < x)
            return x;
        else
            return val;
    }
}
