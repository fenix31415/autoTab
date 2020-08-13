package fenix31415.autotab;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.options.StickyKeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;

import java.util.List;
import java.util.stream.Collectors;

public class AutoTab implements ModInitializer {

    public static final String MOD_ID = "fenix";
    public SpecHUD hud;

    @Override
    public void onInitialize() {
        init_hud();
        init_command_tab();
        init_keybinds();
    }

    private void  init_hud() {
        hud = new SpecHUD();
    }

    public static int giveDiamond(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final ServerCommandSource source = context.getSource();
        final ServerPlayerEntity player = source.getPlayer();

        //List<PlayerEntity> allPlayers = (List<PlayerEntity>) player.world.getPlayers();

        List<PlayerListEntry> allPlayers = MinecraftClient.getInstance().getNetworkHandler().getPlayerList().stream().filter(p -> p.getGameMode()==GameMode.SPECTATOR).collect(Collectors.toList());
        //return playerListEntry != null && playerListEntry.getGameMode() == GameMode.SPECTATOR;
        System.out.println(allPlayers.size());

        if(!player.inventory.insertStack(new ItemStack(Items.DIAMOND))){
            throw new SimpleCommandExceptionType(new TranslatableText("inventory.isfull")).create();
        }
        return 1;
    }

    private void init_command_tab() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("tab").executes(AutoTab::giveDiamond)));
    }

    private void init_keybinds() {
        KeyBinding binding1 = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.fabric-key-binding-api-v1-testmod.test_keybinding_1", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P, "key.category.first.test"));
        KeyBinding binding2 = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.fabric-key-binding-api-v1-testmod.test_keybinding_2", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "key.category.second.test"));
        KeyBinding stickyBinding = KeyBindingHelper.registerKeyBinding(new StickyKeyBinding("key.fabric-key-binding-api-v1-testmod.test_keybinding_sticky", GLFW.GLFW_KEY_R, "key.category.first.test", () -> true));

        ClientTickCallback.EVENT.register(client -> {
            while (binding1.wasPressed()) {
                client.player.sendMessage(new LiteralText("Key 1 was pressed!"), false);
            }

            while (binding2.wasPressed()) {
                client.player.sendMessage(new LiteralText("Key 2 was pressed!"), false);
            }

            if (stickyBinding.isPressed()) {
                client.player.sendMessage(new LiteralText("Sticky Key was pressed!"), false);
            }
        });
    }
}
