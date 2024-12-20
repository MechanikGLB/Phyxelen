package game.gui;

import game.*;
import game.spells.Spell;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL21.*;

public class Main {
    static int rectVertexBuffer;
    static int[] rectVertexArray = new int[8];
    static int rectColorBuffer;
    static int[] rectColorArray = new int[16];


    public void init() {
        rectVertexBuffer = glGenBuffers();
        rectColorBuffer = glGenBuffers();
    }

    public void draw(int width, int height) {
        glLoadIdentity();
//        glEnableClientState(GL_VERTEX_ARRAY);
//        glEnableClientState(GL_COLOR_ARRAY);

//        glBindBuffer(GL_ARRAY_BUFFER, rectVertexBuffer);
//        glVertexPointer(2, GL_INT, 0, 0);
//        textButton("Text");
        var player = (Player)((Client) game.Main.getGame()).getControlledCharacter();
        if (player == null)
            return;
        ArrayList<HoldableItem> inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            if (player.getHeldItem() == inventory.get(i))
                drawRect(10+100*i, 10, 90+100*i, 90, 200, 255, 200, 255);
            else
                drawRect(10+100*i, 10, 90+100*i, 90, 100, 155, 100, 255);
            drawRect(16+100*i, 16, 84+100*i, 84, 80, 100, 80, 255);
            drawRectTextured(10+100*i, 50-20, 80+100*i, 50+20, 255, 255, 255, 255, inventory.get(i).getImage().getTextureBuffer());
        }
        if (player.getHeldItem() instanceof Wand) {
            var wand = (Wand) player.getHeldItem();
            ArrayList<Spell> spells = wand.getSpells();
            drawRect(10, 100, 90+100*(spells.size()-1), 180, 80, 100, 80, 255);
            for (int i = 0; i < spells.size(); i++) {
                drawRectTextured(16+100*i, 106, 84+100*i, 174, 255, 255, 255, 255,
                        Content.getImage(spells.get(i).getImage()).getTextureBuffer());

            }
        }
    }

    static boolean textButton(String text) {
//        drawRect(0, 0, 100, 100,
//                255, 255, 255, 255);
        return false;
    }

    static private void drawRect(int x1, int y1, int x2, int y2, int r, int g, int b, int a) {
        glBegin(GL_QUADS);
        glColor4b((byte)(r/2-1),(byte)(g/2-1),(byte)(b/2-1),(byte)(a/2-1));
        glVertex2i(x1, y1);
        glVertex2i(x2, y1);
        glVertex2i(x2, y2);
        glVertex2i(x1, y2);
        glEnd();
    }

    static private void drawRectTextured(int x1, int y1, int x2, int y2, int r, int g, int b, int a, int textureBuffer) {
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, textureBuffer);
        glBegin(GL_QUADS);
        glColor4b((byte) (r / 2 - 1), (byte) (g / 2 - 1), (byte) (b / 2 - 1), (byte) (a / 2 - 1));
        glTexCoord2f(0f,1f);
        glVertex2i(x1, y1);
        glTexCoord2f(1f,1f);
        glVertex2i(x2, y1);
        glTexCoord2f(1f,0f);
        glVertex2i(x2, y2);
        glTexCoord2f(0f,0f);
        glVertex2i(x1, y2);
        glEnd();
        glDisable(GL_TEXTURE_2D);
    }
}
