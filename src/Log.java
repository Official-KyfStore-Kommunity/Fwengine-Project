package src;

public class Log {
    public void print(String value)
    {
        System.out.println(value);
    }
    public void print(Object value)
    {
        System.out.println(String.valueOf(value));
    }
    public void debug(String value)
    {
        System.out.println("[DEBUG INFO]: " + value);
    }
    public void debug(Object value)
    {
        System.out.println("[DEBUG INFO]: " + String.valueOf(value));
    }
    public void error(String value, Integer errorCode)
    {
        System.out.println("[ERROR] " + value + " exited with code=" + errorCode);
    }
    public void error(Object value, Integer errorCode)
    {
        System.out.println("[ERROR] " + String.valueOf(value) + " exited with code=" + errorCode);
    }
}