package com.leecampbell.playerservice;

import org.apache.commons.cli.*;

public class CommandLineFactory {
    public static CommandLine parseArguments(String[] args) {
        Options options = new Options();
        Option configFile = new Option("c", "config", true, "config file path");
        configFile.setRequired(true);
        options.addOption(configFile);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try{
            return parser.parse(options, args);
        } catch (org.apache.commons.cli.ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Global Poker PlayerService",
                    "\nCommand line arguments for the Player Service:",
                    options,
                    "\nService not running.");
            System.exit(1);
            return null;
        }
    }
}
