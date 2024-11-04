package game;

public class ColorWithAplha extends Color {
    float alpha = 1.0f;

    public ColorWithAplha(float r, float g, float b, float alpha) {
        super(r, g, b);
        this.alpha = alpha;
    }

    public ColorWithAplha(String colorString) {
        super(
                Integer.parseInt(colorString.substring(1,3), 16) / 255.f,
                Integer.parseInt(colorString.substring(3,5), 16) / 255.f,
                Integer.parseInt(colorString.substring(5,7), 16) / 255.f);
        alpha = Integer.parseInt(colorString.substring(7,9), 16) / 255.f;
    }

    static ColorWithAplha white = new ColorWithAplha(1f,1f,1f,1f);
    public static ColorWithAplha white() { return white; }
}
