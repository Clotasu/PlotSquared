/*
 * Copyright (c) IntellectualCrafters - 2014. You are not allowed to distribute
 * and/or monetize any of our intellectual property. IntellectualCrafters is not
 * affiliated with Mojang AB. Minecraft is a trademark of Mojang AB.
 * 
 * >> File = MainCommand.java >> Generated by: Citymonstret at 2014-08-09 01:42
 */

package com.intellectualcrafters.plot.commands;

import com.intellectualcrafters.plot.C;
import com.intellectualcrafters.plot.PlayerFunctions;
import com.intellectualcrafters.plot.PlotMain;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * PlotMain command class
 *
 * @author Citymonstret
 */
public class MainCommand implements CommandExecutor {

	private static SubCommand[] _subCommands = new SubCommand[] { new Claim(), new Paste(), new Copy(), new Auto(), new Home(), new Visit(),
			new TP(), new Set(), new Clear(), new Delete(), new SetOwner(), new Denied(), new Helpers(), new Trusted(),
			new Info(), new list(), new Help(), new Debug(), new Schematic(), new plugin(), new Inventory(),
			new Reload(), new Merge(), new Unlink(), new Kick(), new Setup() };

	public static ArrayList<SubCommand> subCommands = new ArrayList<SubCommand>() {
		{
			addAll(Arrays.asList(_subCommands));
		}
	};

	public static boolean no_permission(Player player) {
		PlayerFunctions.sendMessage(player, C.NO_PERMISSION);
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("reload")) {
					new Reload().executeConsole(args);
				}
			}
			else {
				PlotMain.sendConsoleSenderMessage(C.PREFIX.s() + C.HELP_HEADER.s());
				PlotMain.sendConsoleSenderMessage("&6/plots reload &c-&6 reloads the plugin");
			}
			return false;
		}
		Player player = (Player) sender;
		if (!player.hasPermission("plots.use")) {
			return no_permission(player);
		}
		if ((args.length < 1)
				|| ((args.length >= 1) && (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("he")))) {
			if (args.length < 2) {
				StringBuilder builder = new StringBuilder();
				builder.append(C.HELP_INFO.s());
				for (SubCommand.CommandCategory category : SubCommand.CommandCategory.values()) {
					builder.append("\n"
							+ C.HELP_INFO_ITEM.s().replaceAll("%category%", category.toString().toLowerCase()).replaceAll("%category_desc%", category.toString()));
				}
				PlayerFunctions.sendMessage(player, builder.toString());
				return true;
			}
			String cat = args[1];
			SubCommand.CommandCategory cato = null;
			for (SubCommand.CommandCategory category : SubCommand.CommandCategory.values()) {
				if (cat.equalsIgnoreCase(category.toString())) {
					cato = category;
					break;
				}
			}
			if (cato == null) {
				StringBuilder builder = new StringBuilder();
				builder.append(C.HELP_INFO.s());
				for (SubCommand.CommandCategory category : SubCommand.CommandCategory.values()) {
					builder.append("\n"
							+ C.HELP_INFO_ITEM.s().replaceAll("%category%", category.toString().toLowerCase()).replaceAll("%category_desc%", category.toString()));
				}
				PlayerFunctions.sendMessage(player, builder.toString());
				return true;
			}
			StringBuilder help = new StringBuilder();
			for (String string : helpMenu(player, cato)) {
				help.append(string + "\n");
			}
			PlayerFunctions.sendMessage(player, help.toString());
			return true;
		}
		else {
			for (SubCommand command : subCommands) {
				if (command.cmd.equalsIgnoreCase(args[0]) || command.alias.equalsIgnoreCase(args[0])) {
					String[] arguments = new String[args.length - 1];
					for (int x = 1; x < args.length; x++) {
						arguments[x - 1] = args[x];
					}
					if (command.permission.hasPermission(player)) {
						return command.execute(player, arguments);
					}
					else {
						return no_permission(player);
					}
				}
			}
			PlayerFunctions.sendMessage(player, C.NOT_VALID_SUBCOMMAND);
		}
		return false;
	}

	public static ArrayList<String> helpMenu(Player player, final SubCommand.CommandCategory category) {
		ArrayList<String> help = new ArrayList<String>() {
			{
				add(t(C.HELP_HEADER.s()));
				add(t(C.HELP_CATEGORY.s().replaceAll("%category%", category.toString())));
			}
		};
		for (SubCommand cmd : subCommands) {
			if (cmd.permission.hasPermission(player) && (cmd.category == category)) {
				String s = t(C.HELP_PAGE.s());
				s = s.replaceAll("%alias%", cmd.alias);
				s = s.replaceAll("%usage%", cmd.usage.contains("plot") ? cmd.usage : "/plot " + cmd.usage);
				s = s.replaceAll("%cmd%", cmd.cmd);
				s = s.replaceAll("%desc%", cmd.description);
				help.add(s);
			}
		}
		if (help.size() < 2) {
			help.add(t(C.NO_COMMANDS.s()));
		}
		return help;
	}

	private static String t(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}

}
