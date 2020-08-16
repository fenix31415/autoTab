package fenix31415.autotab;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.List;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class SpecHUD extends DrawableHelper {
    private MinecraftClient client;
    private boolean isAdminsHere = false;

    public SpecHUD() {
        client = MinecraftClient.getInstance();
        HudRenderCallback.EVENT.register((__, ___) -> this.render());
    }

    public void render() {
        if (client.options.keyPlayerList.isPressed() || MinecraftClient.getInstance().player == null || client.world == null)
            return;

        final MatrixStack matrixStack = new MatrixStack();
        RenderSystem.pushMatrix();
        boolean admins = render_hud(matrixStack, this.client.getWindow().getScaledWidth());
        if (!isAdminsHere && admins) {
            MinecraftClient.getInstance().player.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 5,5);
        } else if (isAdminsHere && !admins) {
            MinecraftClient.getInstance().player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 5,1);
        }
        isAdminsHere = admins;
        RenderSystem.popMatrix();
    }

    public Text getPlayerName(PlayerListEntry playerListEntry) {
        return playerListEntry.getDisplayName() != null ? this.method_27538(playerListEntry, playerListEntry.getDisplayName().shallowCopy()) : this.method_27538(playerListEntry, Team.modifyText(playerListEntry.getScoreboardTeam(), new LiteralText(playerListEntry.getProfile().getName())));
    }

    private Text method_27538(PlayerListEntry playerListEntry, MutableText mutableText) {
        return playerListEntry.getGameMode() == GameMode.SPECTATOR ? mutableText.formatted(Formatting.RED) : mutableText;
    }

    private boolean render_hud(MatrixStack matrixStack, int i) {
        if (client.player == null) return false;
        List<PlayerListEntry> list = client.player.networkHandler.getPlayerList().stream()
                .filter(p -> p.getGameMode() == GameMode.SPECTATOR).collect(Collectors.toList());
        if (list.isEmpty()) return false;

        int maxLength = 0;
        for (PlayerListEntry playerListEntry : list) {
            int cur = this.client.textRenderer.getWidth(this.getPlayerName(playerListEntry));
            maxLength = Math.max(maxLength, cur);
        }
        list = list.subList(0, Math.min(list.size(), 80));
        int m = list.size();
        int n = m;
        int o;
        for(o = 1; n > 20; n = (m + o - 1) / o) {
            ++o;
        }
        boolean bl = this.client.isInSingleplayer() || this.client.getNetworkHandler().getConnection().isEncrypted();
        int s = Math.min(o * ((bl ? 9 : 0) + maxLength), i - 50) / o;
        int t = i / 2 - (s * o + (o - 1) * 5) / 2;
        int u = 10;
        int v = s * o + (o - 1) * 5;
        int z;
        fill(matrixStack, i / 2 - v / 2 - 1, u - 1, i / 2 + v / 2 + 1, u + n * 9, -2147483648);
        int x = this.client.options.getTextBackgroundColor(553648127);
        int aj;
        for(int y = 0; y < m; ++y) {
            z = y / n;
            aj = y % n;
            int ab = t + z * s + z * 5;
            int ac = u + aj * 9;
            fill(matrixStack, ab, ac, ab + s, ac + 8, x);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableAlphaTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            if (y < list.size()) {
                PlayerListEntry playerListEntry2 = list.get(y);
                GameProfile gameProfile = playerListEntry2.getProfile();
                if (bl) {
                    PlayerEntity playerEntity = client.world.getPlayerByUuid(gameProfile.getId());
                    if (playerEntity != null) {
                        playerEntity.isPartVisible(PlayerModelPart.CAPE);
                    }
                    this.client.getTextureManager().bindTexture(playerListEntry2.getSkinTexture());
                    DrawableHelper.drawTexture(matrixStack, ab, ac, 8, 8, 8.0F, (float)8, 8, 8, 64, 64);
                    if (playerEntity != null && playerEntity.isPartVisible(PlayerModelPart.HAT)) {
                        DrawableHelper.drawTexture(matrixStack, ab, ac, 8, 8, 40.0F, (float)8, 8, 8, 64, 64);
                    }
                    ab += 9;
                }
                this.client.textRenderer.drawWithShadow(matrixStack, this.getPlayerName(playerListEntry2), (float)ab, (float)ac, playerListEntry2.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1);
            }
        }
        return true;
    }
}
