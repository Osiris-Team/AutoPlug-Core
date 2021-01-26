/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.autoplug.core.logger;

import org.fusesource.jansi.Ansi;

import java.time.format.DateTimeFormatter;

/**
 * Formats messages into something useful.
 */
public class MessageFormatter {
    public static final DateTimeFormatter dtf_small = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter dtf_medium = DateTimeFormatter.ofPattern("dd-MM HH:mm");
    public static final DateTimeFormatter dtf_long = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static synchronized String formatForAnsiConsole(Message msg){
        Ansi tags;
        Ansi ansi;
        switch (msg.getType()) {
            case INFO:
                tags = Ansi.ansi().bg(Ansi.Color.WHITE)
                        .fg(Ansi.Color.BLACK).a("["+dtf_small.format(msg.getDate())+"]")
                        .fg(Ansi.Color.CYAN).a("["+msg.getLabel()+"]")
                        .fg(Ansi.Color.BLACK).a("["+msg.getType()+"]")
                        .reset();
                ansi = Ansi.ansi()
                        .a(" "+msg.getContent())
                        .reset().newline();
                return ""+tags+ansi;
            case DEBUG:
                tags = Ansi.ansi().bg(Ansi.Color.WHITE)
                        .fg(Ansi.Color.BLACK).a("["+dtf_small.format(msg.getDate())+"]")
                        .fg(Ansi.Color.CYAN).a("["+msg.getLabel()+"]")
                        .fg(Ansi.Color.MAGENTA).a("["+msg.getType()+"]")
                        .reset();
                ansi = Ansi.ansi()
                        .fg(Ansi.Color.CYAN).a("["+msg.getOriginClass().getSimpleName()+"]")
                        .a(" "+msg.getContent())
                        .reset().newline();
                return ""+tags+ansi;
            case WARN:
                tags = Ansi.ansi().bg(Ansi.Color.WHITE)
                        .fg(Ansi.Color.BLACK).a("["+dtf_small.format(msg.getDate())+"]")
                        .fg(Ansi.Color.CYAN).a("["+msg.getLabel()+"]")
                        .fg(Ansi.Color.YELLOW).a("["+msg.getType()+"]")
                        .reset();
                ansi = Ansi.ansi()
                        .fg(Ansi.Color.YELLOW).a(" "+msg.getContent())
                        .reset().newline();
                return ""+tags+ansi;
            case ERROR:
                tags = Ansi.ansi().bg(Ansi.Color.RED)
                        .fg(Ansi.Color.WHITE).a("["+dtf_small.format(msg.getDate())+"]")
                        .fg(Ansi.Color.WHITE).a("["+msg.getLabel()+"]")
                        .fg(Ansi.Color.WHITE).a("["+msg.getType()+"]");

                final StringBuilder builder = new StringBuilder();
                builder.append(tags).append(Ansi.ansi().a("[!] ############################## [!]").newline());
                if (msg.getContent()==null) builder.append(tags).append(Ansi.ansi().a("[!] No details available. [!]").newline());
                else builder.append(tags).append(Ansi.ansi().a("[!] Details: "+msg.getContent()+" [!]").newline());
                builder.append(tags).append(Ansi.ansi().a("[!] Message: "+msg.getException().getMessage()+" [!]").newline());

                for (StackTraceElement element :
                        msg.getException().getStackTrace()) {
                    builder.append(tags).append(Ansi.ansi().a("[!] "+element.toString()+" [!]").newline());
                }

                builder.append(tags).append(Ansi.ansi().a("[!] "+AL.NAME+" is shutting down in 10 seconds. Log saved to "+AL.DIR_FULL.getAbsolutePath()+". [!]").newline());
                builder.append(tags).append(Ansi.ansi().a("[!] ############################## [!]").newline());

                return builder.toString();
            default:
                tags = Ansi.ansi()
                        .fg(Ansi.Color.BLACK).a("["+dtf_small.format(msg.getDate())+"]")
                        .fg(Ansi.Color.CYAN).a("["+msg.getLabel()+"]")
                        .fg(Ansi.Color.BLACK).a("["+msg.getType()+"]")
                        .reset();
                ansi = Ansi.ansi()
                        .a(" "+msg.getContent())
                        .reset().newline();
                return ""+tags+ansi;
        }
    }

    public static synchronized String formatForFile(Message msg){
        StringBuilder builder = new StringBuilder();
        final String tags = "[" + dtf_long.format(msg.getDate()) + "][" + msg.getLabel() + "][" + msg.getType() + "]";

        switch (msg.getType()){
            case INFO:
                builder.append(tags).append(" "+msg.getContent()+"\n");
                return builder.toString();
            case DEBUG:
                builder.append(tags).append("["+msg.getOriginClass().getSimpleName()+"] "+msg.getContent()+"\n");
                return builder.toString();
            case WARN:
                builder.append(tags).append(" ================================\n");
                if (msg.getContent() == null)
                    builder.append(tags).append(" Details: No details available.\n");
                else
                    builder.append(tags).append(" Details: " + msg.getContent() + "\n");

                if (msg.getException()!=null) {
                    builder.append(tags).append(" Message: " + msg.getException().getMessage() + "\n");
                    if (msg.getException().getCause()!=null)
                        builder.append(tags).append(" Cause: " + msg.getException().getCause() + "\n");

                    for (StackTraceElement element :
                            msg.getException().getStackTrace()) {
                        builder.append(tags).append(" " + element.toString() + "\n");
                    }
                }
                builder.append(tags).append(" ================================\n");
                return builder.toString();
            case ERROR:
                builder.append(tags).append("[!] ############################## [!]\n");
                if (msg.getContent() == null)
                    builder.append(tags).append("[!] Details: No details available. [!]\n");
                else builder.append(tags).append("[!] Details: " + msg.getContent() + " [!]\n");
                builder.append(tags).append("[!] Message: " + msg.getException().getMessage() + " [!]\n");

                for (StackTraceElement element :
                        msg.getException().getStackTrace()) {
                    builder.append(tags).append("[!] " + element.toString() + " [!]\n");
                }

                builder.append(tags).append("[!] "+AL.NAME+" is shutting down in 10 seconds. Log saved to "+AL.DIR_FULL.getAbsolutePath()+". [!]\n");
                builder.append(tags).append("[!] ############################## [!]\n");
                return builder.toString();
            default:
                builder.append(tags).append(" "+msg.getContent()+"\n");
                return builder.toString();
        }
    }

}
