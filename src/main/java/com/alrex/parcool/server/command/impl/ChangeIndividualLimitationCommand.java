package com.alrex.parcool.server.command.impl;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.info.LimitationByServer;
import com.alrex.parcool.server.command.args.ActionArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class ChangeIndividualLimitationCommand {
	private static final String ARGS_NAME_PLAYERS = "targets";
	private static final String ARGS_NAME_ACTION = "action";
	private static final String ARGS_NAME_MAX_STAMINA_VALUE = "max_stamina_value";
	private static final String ARGS_NAME_STAMINA_CONSUMPTION = "stamina_consumption";
	private static final String ARGS_NAME_POSSIBILITY = "possibility";

	public static ArgumentBuilder<CommandSourceStack, ?> getBuilder() {
		return Commands
				.literal("limitation")
				.requires(commandSource -> commandSource.hasPermission(2))
				.then(Commands
						.literal("enable")
						.then(Commands
								.argument(ARGS_NAME_PLAYERS, EntityArgument.players())
								.executes(ChangeIndividualLimitationCommand::enableLimitation))
				)
				.then(Commands
						.literal("disable")
						.then(Commands
								.argument(ARGS_NAME_PLAYERS, EntityArgument.players())
								.executes(ChangeIndividualLimitationCommand::disableLimitation))
				)
				.then(Commands
						.literal("set")
						.then(Commands
								.argument(ARGS_NAME_PLAYERS, EntityArgument.players())
								.then(Commands
										.literal("to_default")
										.executes(ChangeIndividualLimitationCommand::setLimitationDefault)
								)
								.then(Commands
										.literal("max_stamina")
										.then(Commands
												.argument(ARGS_NAME_MAX_STAMINA_VALUE, IntegerArgumentType.integer(0, Integer.MAX_VALUE))
												.executes(ChangeIndividualLimitationCommand::changeLimitationOfMaxStamina)
										)
								)
								.then(Commands
										.literal("possibility")
										.then(Commands
												.literal("infinite_stamina")
												.then(Commands
														.argument(ARGS_NAME_POSSIBILITY, BoolArgumentType.bool())
														.executes(ChangeIndividualLimitationCommand::changePossibilityOfInfiniteStamina)
												)
										)
										.then(Commands
												.argument(ARGS_NAME_ACTION, ActionArgumentType.action())
												.then(Commands
														.argument(ARGS_NAME_POSSIBILITY, BoolArgumentType.bool())
														.executes(ChangeIndividualLimitationCommand::changePossibilityOfAction)
												)
										)
								)
								.then(Commands
										.literal("least_stamina_consumption")
										.then(Commands
												.argument(ARGS_NAME_ACTION, ActionArgumentType.action())
												.then(Commands
														.argument(ARGS_NAME_STAMINA_CONSUMPTION, IntegerArgumentType.integer(0, Integer.MAX_VALUE))
														.executes(ChangeIndividualLimitationCommand::changeStaminaConsumption)
												)
										)
								)
						)
				);
	}

	private static int setLimitationDefault(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS);
		int num = 0;
		for (ServerPlayer player : targets) {
			new LimitationByServer.IndividualLimitationChanger(player)
					.setDefault()
					.sync();
			num++;
		}
		context.getSource().sendSuccess(new TranslatableComponent("parcool.command.message.success.setLimitationToDefault", num), true);
		return 0;
	}

	private static int enableLimitation(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS);
		int num = 0;
		for (ServerPlayer player : targets) {
			new LimitationByServer.IndividualLimitationChanger(player)
					.setEnforced(true)
					.sync();
			num++;
		}
		context.getSource().sendSuccess(new TranslatableComponent("parcool.command.message.success.enableLimitation", num), true);
		return 0;
	}

	private static int disableLimitation(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS);
		int num = 0;
		for (ServerPlayer player : targets) {
			new LimitationByServer.IndividualLimitationChanger(player)
					.setEnforced(false)
					.sync();
			num++;
		}
		context.getSource().sendSuccess(new TranslatableComponent("parcool.command.message.success.disableLimitation", num), true);
		return 0;
	}

	private static int changeLimitationOfMaxStamina(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS);
		int newValue = IntegerArgumentType.getInteger(context, ARGS_NAME_MAX_STAMINA_VALUE);
		int num = 0;
		for (ServerPlayer player : targets) {
			new LimitationByServer.IndividualLimitationChanger(player)
					.setMaxStaminaLimitation(newValue)
					.sync();
			num++;
		}
		context.getSource().sendSuccess(new TranslatableComponent("parcool.command.message.success.setMaxStamina", num, newValue), true);
		return 0;
	}

	private static int changeStaminaConsumption(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS);
		Class<? extends Action> action = ActionArgumentType.getAction(context, ARGS_NAME_ACTION);
		int newValue = IntegerArgumentType.getInteger(context, ARGS_NAME_STAMINA_CONSUMPTION);
		int num = 0;
		for (ServerPlayer player : targets) {
			new LimitationByServer.IndividualLimitationChanger(player)
					.setStaminaConsumptionOf(action, newValue)
					.sync();
			num++;
		}
		context.getSource().sendSuccess(new TranslatableComponent("parcool.command.message.success.setStaminaConsumption", num, action.getSimpleName(), newValue), true);
		return 0;
	}

	private static int changePossibilityOfInfiniteStamina(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS);
		boolean newValue = BoolArgumentType.getBool(context, ARGS_NAME_POSSIBILITY);
		int num = 0;
		for (ServerPlayer player : targets) {
			new LimitationByServer.IndividualLimitationChanger(player)
					.setInfiniteStaminaPermission(newValue)
					.sync();
			num++;
		}
		context.getSource().sendSuccess(new TranslatableComponent("parcool.command.message.success.setPermissionInfiniteStamina", num, newValue), true);
		return 0;
	}

	private static int changePossibilityOfAction(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, ARGS_NAME_PLAYERS);
		Class<? extends Action> action = ActionArgumentType.getAction(context, ARGS_NAME_ACTION);
		boolean newValue = BoolArgumentType.getBool(context, ARGS_NAME_POSSIBILITY);
		int num = 0;
		for (ServerPlayer player : targets) {
			new LimitationByServer.IndividualLimitationChanger(player)
					.setPossibilityOf(action, newValue)
					.sync();
			num++;
		}
		context.getSource().sendSuccess(new TranslatableComponent("parcool.command.message.success.setPermissionOfAction", num, action.getSimpleName(), newValue), true);
		return 0;
	}
}
