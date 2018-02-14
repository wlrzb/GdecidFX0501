package prefuse.util.display;

import java.awt.Graphics2D;

import prefuse.Display2;
import prefuse.util.PrefuseLib;

/**
 * PinatListener that paints useful debugging statistics over a prefuse
 * display. This includes the current frame rate, the number of visible
 * items, memory usage, and display navigation information.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class DebugStatsPainter implements PaintListener {

    /**
     * Does nothing.
     * @see prefuse.util.display.PaintListener#prePaint(prefuse.Display2, java.awt.Graphics2D)
     */
    public void prePaint(Display2 d, Graphics2D g) {
        
    }
    
    /**
     * Prints a debugging statistics string in the Display.
     * @see prefuse.util.display.PaintListener#postPaint(prefuse.Display2, java.awt.Graphics2D)
     */
    public void postPaint(Display2 d, Graphics2D g) {
        g.setFont(d.getFont());
        g.setColor(d.getForeground());
        g.drawString(PrefuseLib.getDisplayStats(d), 5, 15);
    }

} // end of class DebugStatsPainter
