package game;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL21.*;

public class Image {
    ByteBuffer bytes;
    int width;
    int height;
    int textureBuffer = -1;

    public Image(ByteBuffer bytes, int width, int height) {
        this.bytes = bytes;
        this.width = width;
        this.height = height;
        textureBuffer = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureBuffer);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        bytes.rewind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE,
                bytes);
    }

    public int getTextureBuffer() {
//        if (textureBuffer == -1) {
//            textureBuffer = glGenTextures();
//            glBindTexture(GL_TEXTURE_2D, textureBuffer);
//            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//            bytes.rewind();
//            System.out.println(bytes.capacity());
//            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE,
//                    bytes);
//        }
        return textureBuffer;
    }
}
