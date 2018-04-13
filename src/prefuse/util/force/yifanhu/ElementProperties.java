package prefuse.util.force.yifanhu;

import javafx.scene.paint.Color;

public interface ElementProperties {

    /**
     * Returns the red color component between zero and one.
     *
     * @return the red color component
     */
    public float r();

    /**
     * Returns the green color component between zero and one.
     *
     * @return the green color component
     */
    public float g();

    /**
     * Returns the blue color component between zero and one.
     *
     * @return the blue color component
     */
    public float b();

    /**
     * Returns the RGBA color.
     *
     * @return the color
     */
    public int getRGBA();

    /**
     * Returns the color.
     *
     * @return the color
     */
    public Color getColor();

    /**
     * Returns the alpha (transparency) component between zero and one.
     *
     * @return the alpha
     */
    public float alpha();

    /**
     * Sets the red color component.
     *
     * @param r the color component, between zero and one
     */
    public void setR(float r);

    /**
     * Sets the green color component.
     *
     * @param g the color component, between zero and one
     */
    public void setG(float g);

    /**
     * Sets the blue color component.
     *
     * @param b the color component, between zero and one
     */
    public void setB(float b);

    /**
     * Sets the alpha (transparency) color component.
     *
     * @param a the alpha component, between zero and one
     */
    public void setAlpha(float a);

    /**
     * Sets the color.
     *
     * @param color the color
     */
    public void setColor(Color color);
}