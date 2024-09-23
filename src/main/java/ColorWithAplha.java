public class ColorWithAplha extends Color {
    float alpha = 1.0f;

    public ColorWithAplha(float r, float g, float b, float alpha) {
        super(r, g, b);
        this.alpha = alpha;
    }
}
