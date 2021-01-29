package pro.mikey.fabric.xray;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import pro.mikey.fabric.xray.cache.RenderBlock;
import pro.mikey.fabric.xray.screens.MainScreen;
import pro.mikey.fabric.xray.storage.BlockStore;
import pro.mikey.fabric.xray.storage.Stores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XRay implements ModInitializer {

	public static final String MOD_ID = "advanced-xray-fabric";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	private static ChunkPos lastScannedFromChunk = new ChunkPos(0, 0);
	public static List<RenderBlock> renderQueue = Collections.synchronizedList( new ArrayList<>() );

	private final KeyBinding xrayButton = new KeyBinding("keybinding.enable_xray", GLFW.GLFW_KEY_G, "category.xray");
	private final KeyBinding refreshButton = new KeyBinding("keybinding.refresh_xray", GLFW.GLFW_KEY_Y, "category.xray");
	private final KeyBinding guiButton = new KeyBinding("keybinding.open_gui", GLFW.GLFW_KEY_BACKSLASH, "category.xray");

	private static boolean xrayWasDown = false;
	private static boolean refreshWasDown = false;
	private static boolean guiWasDown = false;

	private final MutableText activateMessage = new TranslatableText("message.xray_active").formatted(Formatting.GREEN);
	private final MutableText refreshMessage = new TranslatableText("message.xray_refreshed").formatted(Formatting.GOLD);
	private final MutableText deactivateMessage = new TranslatableText("message.xray_deactivate").formatted(Formatting.RED);

	@Override
	public void onInitialize() {
		LOGGER.info("XRay mod has been initialized");

		ClientTickEvents.END_CLIENT_TICK.register(this::clientTickEvent);
		ClientLifecycleEvents.CLIENT_STOPPING.register(this::gameClosing);
		KeyBindingHelper.registerKeyBinding(xrayButton);
		KeyBindingHelper.registerKeyBinding(refreshButton);
		KeyBindingHelper.registerKeyBinding(guiButton);

		BlockStore blocks = Stores.BLOCKS;
		System.out.println("blocks get: " + blocks.get());
	}

	private void gameClosing(MinecraftClient client) {
		// When the game stops we want to save our stores quickly
		Stores.SETTINGS.write();
		Stores.BLOCKS.write();
	}

	private boolean playerHasMovedOutOfCacheRange(PlayerEntity player) {
		int xDiff = player.chunkX - lastScannedFromChunk.x;
		int zDiff = player.chunkZ - lastScannedFromChunk.z;

		// to be added to config later
		final int cacheDistance = Stores.SETTINGS.getInstance().get().getCacheRange();

		if (xDiff > -cacheDistance && xDiff < cacheDistance
				&& zDiff > -cacheDistance && zDiff < cacheDistance) {
			// player hasn't moved out of cache range
			return false;
		}

		return true;
	}

	private void updateBlockCache(PlayerEntity player) {
		// Update the players last chunk to eval against above.
		lastScannedFromChunk = new ChunkPos(player.chunkX, player.chunkZ);
		Util.getMainWorkerExecutor().execute(new ScanTask());
	}

	/**
	 * Handles the actual scanning process :D
	 */
	private void clientTickEvent(MinecraftClient mc) {
		PlayerEntity player = mc.player;
		World world = mc.world;

		if (player == null || world == null || mc.currentScreen != null) {
			return;
		}

		// Try and run the task :D
		if (Stores.SETTINGS.get().isActive()
			&& playerHasMovedOutOfCacheRange(player)) {
			updateBlockCache(player);
		}

		if (guiButton.isPressed() && !guiWasDown) {
			mc.openScreen(new MainScreen());
		} else if (xrayButton.isPressed() && !xrayWasDown) {
			StateSettings stateSettings = Stores.SETTINGS.get();
			stateSettings.setActive(!stateSettings.isActive());
			player.sendMessage(stateSettings.isActive() ? activateMessage : deactivateMessage, true);
		} else if (refreshButton.isPressed() && !refreshWasDown) {
			player.sendMessage(refreshMessage, true);
			updateBlockCache(player);
		}

		xrayWasDown = xrayButton.isPressed();
		refreshWasDown = refreshButton.isPressed();
		guiWasDown = guiButton.isPressed();
	}
}
