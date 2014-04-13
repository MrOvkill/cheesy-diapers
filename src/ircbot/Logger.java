package ircbot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger
{
    public static void logChat(String channel, String sender, String message)
    {
        try
        {    
            File log = new File("log" + Main.sep + "log.txt");
            SimpleDateFormat sdfDate = new SimpleDateFormat("dd,MM,yyyy,HH,mm,ss");
            Date now = new Date();
            if(!log.exists())
            {
                log.mkdirs();
                log.delete();
                log.createNewFile();
            }
            if(log.length() >= 1_000_000)
            {
                Runtime.getRuntime().exec("mv " + "log" + Main.sep + "log.txt " + "log" + Main.sep + sdfDate.format(now) + ".log");
            }
            sdfDate = new SimpleDateFormat("[dd-MM-yyyy HH:mm:ss] ");
            BufferedWriter lout = new BufferedWriter(new FileWriter(log, true));
            lout.write(sdfDate.format(now) + "{" + channel + "} <" + sender  + "> " + message + "\n");
            lout.close();
            lout = null;
            sdfDate = null;
            log = null;
            now = null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }
    public static void logPrivMsg(String sender, String message)
    {
        try
        {    
            File log = new File("log" + Main.sep + "log.txt");
            if(!log.exists())
            {
                log.mkdirs();
                log.delete();
                log.createNewFile();
            }
            BufferedWriter lout = new BufferedWriter(new FileWriter(log, true));
            SimpleDateFormat sdfDate = new SimpleDateFormat("[dd-MM-yyyy HH:mm:ss] ");
            Date now = new Date();
            sdfDate.format(now);
            lout.write(sdfDate.format(now) + "{PRIVATE} <" + sender  + "> " + message + "\n");
            lout.close();
            lout = null;
            sdfDate = null;
            log = null;
            now = null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
