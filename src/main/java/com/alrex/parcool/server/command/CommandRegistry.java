package com.alrex.parcool.server.command;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.server.command.args.ActionArgumentType;
import com.alrex.parcool.server.command.impl.ChangeIndividualLimitationCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class CommandRegistry {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal(ParCool.MOD_ID)
                        .then(ControlLimitationCommand.getBuilder())
		);
	}

	public static void registerArgumentTypes(FMLCommonSetupEvent event) {
		ArgumentTypes.register("parcool:action_name", ActionArgumentType.class, new EmptyArgumentSerializer<>(ActionArgumentType::action));
		ArgumentTypes.register("parcool:limitation_item_name", LimitationItemArgumentType.class, new LimitationItemArgumentType.Serializer());
		ArgumentTypes.register("parcool:limitation_id", LimitationIDArgumentType.class, new EmptyArgumentSerializer<>(LimitationIDArgumentType::new));
		ArgumentTypeInfos.registerByClass(ActionArgumentType.class, SingletonArgumentInfo.contextFree(ActionArgumentType::action));
	}
}
