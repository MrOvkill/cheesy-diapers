package ircbot;

import static ircbot.Main.sep;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import jerklib.Channel;
import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.*;
import jerklib.events.IRCEvent.Type;
import jerklib.listeners.IRCEventListener;

public class EventListener implements IRCEventListener
{
    Channel chan;
    public static ConnectionManager cm;
    public static String password;
    
    
    public EventListener()
    {
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Password:");
            password = br.readLine();
            br.close();
            br = null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        cm = new ConnectionManager(new Profile("CheesyDiapers"));
        Session session = cm.requestConnection("irc.freenode.net");
        session.addIRCEventListener(this);
    }
    
    public void shutdown()
    {
        cm.quit("CheesyDiapers Shutting Down");
    }
    
    @Override
    public void receiveEvent(IRCEvent e)
    {
        try
        {
        if (e.getType() == Type.CONNECT_COMPLETE)
        {
            e.getSession().join("#ovkbot");
        }
        else if (e.getType() == Type.JOIN_COMPLETE)
        {
            JoinCompleteEvent jce = (JoinCompleteEvent) e;
            /* say hello */
            chan = jce.getChannel();
            chan.say("CheesyDiapers v0.0.1 Connected");
            chan.say("Type \"/msg CheesyDiapers bot.help\" for help");
        }
        else if (e.getType() == Type.CHANNEL_MESSAGE)
        {
            MessageEvent me = (MessageEvent)e;
            Logger.logChat(chan.getName(), me.getNick(), me.getMessage());
        }
        else if (e.getType() == Type.PRIVATE_MESSAGE)
        {
            MessageEvent pme = (MessageEvent)e;
            System.out.println("Message from \"" + pme.getNick() + "\": " + pme.getMessage());
            if(pme.getMessage().startsWith("bot."))
            {
                String message = pme.getMessage();
                if(message.toLowerCase().startsWith("bot.help"))
                {
                    pme.getSession().sayPrivate(pme.getNick(), "CheesyDiapers v0.0.1");
                    pme.getSession().sayPrivate(pme.getNick(), "bot.help - Gives this stuff");
                    pme.getSession().sayPrivate(pme.getNick(), "bot.join [channel] - Moves to the specified channel");
                    pme.getSession().sayPrivate(pme.getNick(), "bot.quit [adminpass]- Causes the bot to shut down messily.");
                    pme.getSession().sayPrivate(pme.getNick(), "bot.setvar [varname] [value of stuff] - Sets varname to value of stuff");
                    pme.getSession().sayPrivate(pme.getNick(), "bot.getvar [varname] - gets a varable or returns \"null\" if that variable doesn't exist");
                    pme.getSession().sayPrivate(pme.getNick(), "bot.sayvar [varname] - Says the varname in the current channel");
                    pme.getSession().sayPrivate(pme.getNick(), "bot.say [text] - Says the text specified");
                }
                else if(message.toLowerCase().startsWith("bot.join"))
                {
                    message = message.replaceFirst("bot.join ", "");
                    e.getSession().join(message);
                    chan = e.getSession().getChannel(message);
                }
                else if(message.toLowerCase().startsWith("bot.quit"))
                {
                    if(message.equals("bot.quit " + password))
                    {
                        System.exit(0);
                    }
                    else
                    {
                        pme.getSession().sayPrivate(pme.getNick(), "Nice try, " + pme.getNick());
                    }
                }
                else if(message.toLowerCase().startsWith("bot.setvar"))
                {
                    message = message.replaceFirst("bot.setvar ", "");
                    String[] args = message.split(" ");
                    File f = new File("vars" + sep + args[0].replace("\\", "-").replace("/", "-") + ".var");
                    if(!f.exists())
                    {
                        f.mkdirs();
                    }
                    f.delete();
                    f.createNewFile();
                    BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                    String str = "";
                    for(int i = 1; i < args.length; i++)
                    {
                        if(i < args.length-1)
                            str += args[i] + " ";
                        else
                            str += args[i];
                    }
                    bw.write(str);
                    bw.close();
                    pme.getSession().sayPrivate(pme.getNick(), "Set variable \"" + args[0] + "\" to \"" + str + "\"!");
                }
                else if (message.toLowerCase().startsWith("bot.getvar"))
                {
                    message = message.replaceFirst("bot.getvar ", "");
                    File f = new File("vars" + sep + message.replace("\\", "-").replace("/", "-") + ".var");
                    if(!f.exists())
                    {
                        pme.getSession().sayPrivate(pme.getNick(), "Variable \"" + message + "\" is \"null\"");
                    }
                    else
                    {
                        BufferedReader br = new BufferedReader(new FileReader(f));
                        pme.getSession().sayPrivate(pme.getNick(), "Variable \"" + message + "\" is \"" + br.readLine() + "\"");
                        br.close();
                    }
                }
                else if (message.toLowerCase().startsWith("bot.sayvar"))
                {
                    message = message.replaceFirst("bot.sayvar ", "");
                    File f = new File("vars" + sep + message.replace("\\", "-").replace("/", "-") + ".var");
                    if(!f.exists())
                    {
                        chan.say("Variable \"" + message + "\" is \"null\"");
                    }
                    else
                    {
                        BufferedReader br = new BufferedReader(new FileReader(f));
                        chan.say("Variable \"" + message + "\" is \"" + br.readLine() + "\"");
                        br.close();
                    }
                }
                else if(message.toLowerCase().startsWith("bot.say"))
                {
                    message = message.replaceFirst("bot.say ", "");
                    chan.say(message);
                }
                else
                {
                    pme.getSession().sayPrivate(pme.getNick(), "No such command \"" + message + "\"");
                }
            }
            else
            {
                Logger.logPrivMsg(pme.getNick(), pme.getMessage());
            }
        }
    }
    catch(Exception ex)
    {
        ex.printStackTrace();
    }
    }
}
