package pro.mikey.fabric.xray.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class AbstractScreen extends Screen {
    protected static final Identifier TEXTURE = new Identifier("textures/gui/recipe_book.png");

    protected AbstractScreen(Text title) {
        super(title);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }


}
