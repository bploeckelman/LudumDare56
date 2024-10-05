package lando.systems.ld56.utils;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.Scaling;

import java.io.Serializable;

public class RectangleI implements Serializable, Shape2D {

    /** Static temporary rectangle. Use with care! Use only when sure other code will not also use this. */
    static public final RectangleI tmp = new RectangleI();

    /** Static temporary rectangle. Use with care! Use only when sure other code will not also use this. */
    static public final RectangleI tmp2 = new RectangleI();

    private static final long serialVersionUID = 3908901097811785720L;
    public int x, y;
    public int width, height;

    /** Constructs a new rectangle with all values set to zero */
    public RectangleI () {

    }

    /** Constructs a new rectangle with the given corner point in the bottom left and dimensions.
     * @param x The corner point x-coordinate
     * @param y The corner point y-coordinate
     * @param width The width
     * @param height The height */
    public RectangleI (int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /** Constructs a rectangle based on the given rectangle
     * @param rect The rectangle */
    public RectangleI (RectangleI rect) {
        x = rect.x;
        y = rect.y;
        width = rect.width;
        height = rect.height;
    }

    /** Constructs a rectangle based on the given rectangle
     * @param rect The rectangle */
    public RectangleI (Rectangle rect) {
        x = (int) rect.x;
        y = (int) rect.y;
        width = (int) rect.width;
        height = (int) rect.height;
    }

    /** @param x bottom-left x coordinate
     * @param y bottom-left y coordinate
     * @param width width
     * @param height height
     * @return this rectangle for chaining */
    public RectangleI set (int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        return this;
    }

    /** @return the x-coordinate of the bottom left corner */
    public int getX () {
        return x;
    }

    /** Sets the x-coordinate of the bottom left corner
     * @param x The x-coordinate
     * @return this rectangle for chaining */
    public RectangleI setX (int x) {
        this.x = x;

        return this;
    }

    /** @return the y-coordinate of the bottom left corner */
    public int getY () {
        return y;
    }

    /** Sets the y-coordinate of the bottom left corner
     * @param y The y-coordinate
     * @return this rectangle for chaining */
    public RectangleI setY (int y) {
        this.y = y;

        return this;
    }

    /** @return the width */
    public int getWidth () {
        return width;
    }

    /** Sets the width of this rectangle
     * @param width The width
     * @return this rectangle for chaining */
    public RectangleI setWidth (int width) {
        this.width = width;

        return this;
    }

    /** @return the height */
    public int getHeight () {
        return height;
    }

    /** Sets the height of this rectangle
     * @param height The height
     * @return this rectangle for chaining */
    public RectangleI setHeight (int height) {
        this.height = height;

        return this;
    }

    /** return the Vector2 with coordinates of this rectangle
     * @param position The Vector2 */
    public Vector2 getPosition (Vector2 position) {
        return position.set(x, y);
    }

    /** Sets the x and y-coordinates of the bottom left corner from vector
     * @param position The position vector
     * @return this rectangle for chaining */
    public RectangleI setPosition (Vector2 position) {
        this.x = (int) position.x;
        this.y = (int) position.y;

        return this;
    }

    /** Sets the x and y-coordinates of the bottom left corner
     * @param x The x-coordinate
     * @param y The y-coordinate
     * @return this rectangle for chaining */
    public RectangleI setPosition (int x, int y) {
        this.x = x;
        this.y = y;

        return this;
    }

    /** Sets the width and height of this rectangle
     * @param width The width
     * @param height The height
     * @return this rectangle for chaining */
    public RectangleI setSize (int width, int height) {
        this.width = width;
        this.height = height;

        return this;
    }

    /** Sets the squared size of this rectangle
     * @param sizeXY The size
     * @return this rectangle for chaining */
    public RectangleI setSize (int sizeXY) {
        this.width = sizeXY;
        this.height = sizeXY;

        return this;
    }

    /** @return the Vector2 with size of this rectangle
     * @param size The Vector2 */
    public Vector2 getSize (Vector2 size) {
        return size.set(width, height);
    }

    /** @param x point x coordinate
     * @param y point y coordinate
     * @return whether the point is contained in the rectangle */
    public boolean contains (int x, int y) {
        return this.x <= x && this.x + this.width >= x && this.y <= y && this.y + this.height >= y;
    }

    /** @param x point x coordinate
     * @param y point y coordinate
     * @return whether the point is contained in the rectangle */
    public boolean contains (float x, float y) {
        return this.x <= x && this.x + this.width >= x && this.y <= y && this.y + this.height >= y;
    }

    /** @param point The coordinates vector
     * @return whether the point is contained in the rectangle */
    public boolean contains (Vector2 point) {
        return contains(point.x, point.y);
    }

    /** @param circle the circle
     * @return whether the circle is contained in the rectangle */
    public boolean contains (Circle circle) {
        return (circle.x - circle.radius >= x) && (circle.x + circle.radius <= x + width) && (circle.y - circle.radius >= y)
            && (circle.y + circle.radius <= y + height);
    }

    /** @param rectangle the other {@link RectangleI}.
     * @return whether the other rectangle is contained in this rectangle. */
    public boolean contains (RectangleI rectangle) {
        int xmin = rectangle.x;
        int xmax = xmin + rectangle.width;

        int ymin = rectangle.y;
        int ymax = ymin + rectangle.height;

        return ((xmin > x && xmin < x + width) && (xmax > x && xmax < x + width))
            && ((ymin > y && ymin < y + height) && (ymax > y && ymax < y + height));
    }

    /** @param r the other {@link RectangleI}
     * @return whether this rectangle overlaps the other rectangle. */
    public boolean overlaps (RectangleI r) {
        return x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y;
    }

    /** Sets the values of the given rectangle to this rectangle.
     * @param rect the other rectangle
     * @return this rectangle for chaining */
    public RectangleI set (RectangleI rect) {
        this.x = rect.x;
        this.y = rect.y;
        this.width = rect.width;
        this.height = rect.height;

        return this;
    }

    /** Sets the values of the given rectangle to this rectangle.
     * @param rect the other rectangle
     * @return this rectangle for chaining */
    public RectangleI set (Rectangle rect) {
        this.x = (int) rect.x;
        this.y = (int) rect.y;
        this.width = (int) rect.width;
        this.height = (int) rect.height;

        return this;
    }

    /** Merges this rectangle with the other rectangle. The rectangle should not have negative width or negative height.
     * @param rect the other rectangle
     * @return this rectangle for chaining */
    public RectangleI merge (RectangleI rect) {
        int minX = Math.min(x, rect.x);
        int maxX = Math.max(x + width, rect.x + rect.width);
        this.x = minX;
        this.width = maxX - minX;

        int minY = Math.min(y, rect.y);
        int maxY = Math.max(y + height, rect.y + rect.height);
        this.y = minY;
        this.height = maxY - minY;

        return this;
    }

    /** Merges this rectangle with the other rectangle. The rectangle should not have negative width or negative height.
     * @param rect the other rectangle
     * @return this rectangle for chaining */
    public RectangleI merge (Rectangle rect) {
        int minX = (int) Math.min(x, rect.x);
        int maxX = (int) Math.max(x + width, rect.x + rect.width);
        this.x = minX;
        this.width = maxX - minX;

        int minY = (int) Math.min(y, rect.y);
        int maxY = (int) Math.max(y + height, rect.y + rect.height);
        this.y = minY;
        this.height = maxY - minY;

        return this;
    }

    /** Merges this rectangle with a point. The rectangle should not have negative width or negative height.
     * @param x the x coordinate of the point
     * @param y the y coordinate of the point
     * @return this rectangle for chaining */
    public RectangleI merge (int x, int y) {
        int minX = Math.min(this.x, x);
        int maxX = Math.max(this.x + width, x);
        this.x = minX;
        this.width = maxX - minX;

        int minY = Math.min(this.y, y);
        int maxY = Math.max(this.y + height, y);
        this.y = minY;
        this.height = maxY - minY;

        return this;
    }

    /** Merges this rectangle with a point. The rectangle should not have negative width or negative height.
     * @param vec the vector describing the point
     * @return this rectangle for chaining */
    public RectangleI merge (Vector2 vec) {
        return merge((int) vec.x, (int) vec.y);
    }

    /** Merges this rectangle with a list of points. The rectangle should not have negative width or negative height.
     * @param vecs the vectors describing the points
     * @return this rectangle for chaining */
    public RectangleI merge (Vector2[] vecs) {
        int minX = x;
        int maxX = x + width;
        int minY = y;
        int maxY = y + height;
        for (int i = 0; i < vecs.length; ++i) {
            Vector2 v = vecs[i];
            minX = (int) Math.min(minX, v.x);
            maxX = (int) Math.max(maxX, v.x);
            minY = (int) Math.min(minY, v.y);
            maxY = (int) Math.max(maxY, v.y);
        }
        x = minX;
        width = maxX - minX;
        y = minY;
        height = maxY - minY;
        return this;
    }

    /** Calculates the aspect ratio ( width / height ) of this rectangle
     * @return the aspect ratio of this rectangle. Returns 0 if height is 0 to avoid ArithmeticException */
    public int getAspectRatio () {
        return (height == 0) ? 0 : width / height;
    }

    /** Calculates the center of the rectangle. Results are located in the given Vector2
     * @param vector the Vector2 to use
     * @return the given vector with results stored inside */
    public Vector2 getCenter (Vector2 vector) {
        vector.x = x + width / 2f;
        vector.y = y + height / 2f;
        return vector;
    }

    /** Moves this rectangle so that its center point is located at a given position
     * @param x the position's x
     * @param y the position's y
     * @return this for chaining */
    public RectangleI setCenter (int x, int y) {
        setPosition(x - width / 2, y - height / 2);
        return this;
    }

    /** Moves this rectangle so that its center point is located at a given position
     * @param position the position
     * @return this for chaining */
    public RectangleI setCenter (Vector2 position) {
        setPosition((int) position.x - width / 2, (int) position.y - height / 2);
        return this;
    }

    /** Fits this rectangle around another rectangle while maintaining aspect ratio. This scales and centers the rectangle to the
     * other rectangle (e.g. Having a camera translate and scale to show a given area)
     * @param rect the other rectangle to fit this rectangle around
     * @return this rectangle for chaining
     * @see Scaling */
    public RectangleI fitOutside (RectangleI rect) {
        int ratio = getAspectRatio();

        if (ratio > rect.getAspectRatio()) {
            // Wider than tall
            setSize(rect.height * ratio, rect.height);
        } else {
            // Taller than wide
            setSize(rect.width, rect.width / ratio);
        }

        setPosition((rect.x + rect.width / 2) - width / 2, (rect.y + rect.height / 2) - height / 2);
        return this;
    }

    /** Fits this rectangle into another rectangle while maintaining aspect ratio. This scales and centers the rectangle to the
     * other rectangle (e.g. Scaling a texture within a arbitrary cell without squeezing)
     * @param rect the other rectangle to fit this rectangle inside
     * @return this rectangle for chaining
     * @see Scaling */
    public RectangleI fitInside (RectangleI rect) {
        int ratio = getAspectRatio();

        if (ratio < rect.getAspectRatio()) {
            // Taller than wide
            setSize(rect.height * ratio, rect.height);
        } else {
            // Wider than tall
            setSize(rect.width, rect.width / ratio);
        }

        setPosition((rect.x + rect.width / 2) - width / 2, (rect.y + rect.height / 2) - height / 2);
        return this;
    }

    /** Converts this {@code Rectangle} to a string in the format {@code [x,y,width,height]}.
     * @return a string representation of this object. */
    public String toString () {
        return "[" + x + "," + y + "," + width + "," + height + "]";
    }

    /** Sets this {@code Rectangle} to the value represented by the specified string according to the format of
     * {@link #toString()}.
     * @param v the string.
     * @return this rectangle for chaining */
    public RectangleI fromString (String v) {
        int s0 = v.indexOf(',', 1);
        int s1 = v.indexOf(',', s0 + 1);
        int s2 = v.indexOf(',', s1 + 1);
        if (s0 != -1 && s1 != -1 && s2 != -1 && v.charAt(0) == '[' && v.charAt(v.length() - 1) == ']') {
            try {
                int x = Integer.parseInt(v.substring(1, s0));
                int y = Integer.parseInt(v.substring(s0 + 1, s1));
                int width = Integer.parseInt(v.substring(s1 + 1, s2));
                int height = Integer.parseInt(v.substring(s2 + 1, v.length() - 1));
                return this.set(x, y, width, height);
            } catch (NumberFormatException ex) {
                // Throw a GdxRuntimeException
            }
        }
        throw new GdxRuntimeException("Malformed Rectangle: " + v);
    }

    public int area () {
        return this.width * this.height;
    }

    public int perimeter () {
        return 2 * (this.width + this.height);
    }

    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + NumberUtils.floatToRawIntBits(height);
        result = prime * result + NumberUtils.floatToRawIntBits(width);
        result = prime * result + NumberUtils.floatToRawIntBits(x);
        result = prime * result + NumberUtils.floatToRawIntBits(y);
        return result;
    }

    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        RectangleI other = (RectangleI)obj;
        if (NumberUtils.floatToRawIntBits(height) != NumberUtils.floatToRawIntBits(other.height)) return false;
        if (NumberUtils.floatToRawIntBits(width) != NumberUtils.floatToRawIntBits(other.width)) return false;
        if (NumberUtils.floatToRawIntBits(x) != NumberUtils.floatToRawIntBits(other.x)) return false;
        if (NumberUtils.floatToRawIntBits(y) != NumberUtils.floatToRawIntBits(other.y)) return false;
        return true;
    }
}
