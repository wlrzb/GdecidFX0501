package prefuse.util.force.yifanhu;

import prefuse.visual.VisualItem;

public class ForceVectorUtils {

    public static float distance(VisualItem n1, VisualItem n2) {
        return (float) Math.hypot(n1.getX() - n2.getX(), n1.getY() - n2.getY());
    }

    public static void fcBiRepulsor(VisualItem N1, VisualItem N2, double c) {
        double xDist = N1.getX() - N2.getX();	// distance en x entre les deux noeuds
        double yDist = N1.getY() - N2.getY();
        double dist = (float) Math.sqrt(xDist * xDist + yDist * yDist);	// distance tout court

        if (dist > 0) {
            double f = repulsion(c, dist);
            
            float N1Ldx = (float)N1.getX();
            float N1Ldy = (float)N1.getY();
            float N2Ldx = (float)N2.getX();
            float N2Ldy = (float)N2.getY();

            N1Ldx += xDist / dist * f;
            N1Ldy += yDist / dist * f;

            N2Ldx -= xDist / dist * f;
            N2Ldy -= yDist / dist * f;
            
            N1.setX((double)N1Ldx);
            N1.setY((double)N1Ldy);
            N2.setX((double)N2Ldx);
            N2.setY((double)N2Ldy);
            
        }
    }

    public static void fcBiRepulsor_y(VisualItem N1, VisualItem N2, double c, double verticalization) {
        double xDist = N1.getX() - N2.getX();	// distance en x entre les deux noeuds
        double yDist = N1.getY() - N2.getY();
        double dist = (float) Math.sqrt(xDist * xDist + yDist * yDist);	// distance tout court

        if (dist > 0) {
            double f = repulsion(c, dist);
            
            float N1Ldx = (float)N1.getX();
            float N1Ldy = (float)N1.getY();
            float N2Ldx = (float)N2.getX();
            float N2Ldy = (float)N2.getY();

            N1Ldx += xDist / dist * f;
            N1Ldy += verticalization * yDist / dist * f;

            N2Ldx -= xDist / dist * f;
            N2Ldy -= verticalization * yDist / dist * f;
            
            N1.setX((double)N1Ldx);
            N1.setY((double)N1Ldy);
            N2.setX((double)N2Ldx);
            N2.setY((double)N2Ldy);
        }
    }

    public static void fcBiRepulsor_noCollide(VisualItem N1, VisualItem N2, double c) {
        double xDist = N1.getX() - N2.getX();	// distance en x entre les deux noeuds
        double yDist = N1.getY() - N2.getY();
        double dist = (float) Math.sqrt(xDist * xDist + yDist * yDist) - N1.getSize() - N2.getSize();	// distance (from the border of each node)

        if (dist > 0) {
            double f = repulsion(c, dist);
            
            float N1Ldx = (float)N1.getX();
            float N1Ldy = (float)N1.getY();
            float N2Ldx = (float)N2.getX();
            float N2Ldy = (float)N2.getY();

            N1Ldx += xDist / dist * f;
            N1Ldy += yDist / dist * f;

            N2Ldx -= xDist / dist * f;
            N2Ldy -= yDist / dist * f;
            
            N1.setX((double)N1Ldx);
            N1.setY((double)N1Ldy);
            N2.setX((double)N2Ldx);
            N2.setY((double)N2Ldy);
            
        } else if (dist != 0) {
            double f = -c;	//flat repulsion

            float N1Ldx = (float)N1.getX();
            float N1Ldy = (float)N1.getY();
            float N2Ldx = (float)N2.getX();
            float N2Ldy = (float)N2.getY();

            N1Ldx += xDist / dist * f;
            N1Ldy += yDist / dist * f;

            N2Ldx -= xDist / dist * f;
            N2Ldy -= yDist / dist * f;
            
            N1.setX((double)N1Ldx);
            N1.setY((double)N1Ldy);
            N2.setX((double)N2Ldx);
            N2.setY((double)N2Ldy);
        }
        

    }

    public static void fcUniRepulsor(VisualItem N1, VisualItem N2, double c) {
        double xDist = N1.getX() - N2.getX();	// distance en x entre les deux noeuds
        double yDist = N1.getY() - N2.getY();
        double dist = (float) Math.sqrt(xDist * xDist + yDist * yDist);	// distance tout court

        if (dist > 0) {
            double f = repulsion(c, dist);

            float N2Ldx = (float)N2.getX();
            float N2Ldy = (float)N2.getY();

            N2Ldx -= xDist / dist * f;
            N2Ldy -= yDist / dist * f;
            
            N2.setX((double)N2Ldx);
            N2.setY((double)N2Ldy);
        }
    }

    public static void fcBiAttractor(VisualItem N1, VisualItem N2, double c) {
        double xDist = N1.getX() - N2.getX(); // distance en x entre les deux noeuds
        double yDist = N1.getY() - N2.getY();
        double dist = (float) Math.sqrt(xDist * xDist + yDist * yDist);	// distance tout court

        if (dist > 0) {
            double f = attraction(c, dist);
            
            float N1Ldx = (float)N1.getX();
            float N1Ldy = (float)N1.getY();
            float N2Ldx = (float)N2.getX();
            float N2Ldy = (float)N2.getY();

            N1Ldx += xDist / dist * f;
            N1Ldy += yDist / dist * f;

            N2Ldx -= xDist / dist * f;
            N2Ldy -= yDist / dist * f;
            
            N1.setX((double)N1Ldx);
            N1.setY((double)N1Ldy);
            N2.setX((double)N2Ldx);
            N2.setY((double)N2Ldy);
        }
    }

    public static void fcBiAttractor_noCollide(VisualItem N1, VisualItem N2, double c) {
        double xDist = N1.getX() - N2.getX();	// distance en x entre les deux noeuds
        double yDist = N1.getY() - N2.getY();
        double dist = (float) Math.sqrt(xDist * xDist + yDist * yDist) - N1.getSize() - N2.getSize();	// distance (from the border of each node)

        if (dist > 0) {
            double f = attraction(c, dist);

            float N1Ldx = (float)N1.getX();
            float N1Ldy = (float)N1.getY();
            float N2Ldx = (float)N2.getX();
            float N2Ldy = (float)N2.getY();

            N1Ldx += xDist / dist * f;
            N1Ldy += yDist / dist * f;

            N2Ldx -= xDist / dist * f;
            N2Ldy -= yDist / dist * f;
            
            N1.setX((double)N1Ldx);
            N1.setY((double)N1Ldy);
            N2.setX((double)N2Ldx);
            N2.setY((double)N2Ldy);
        }
    }

    public static void fcBiFlatAttractor(VisualItem N1, VisualItem N2, double c) {
        double xDist = N1.getX() - N2.getX();	// distance en x entre les deux noeuds
        double yDist = N1.getY() - N2.getY();
        double dist = (float) Math.sqrt(xDist * xDist + yDist * yDist);	// distance tout court

        if (dist > 0) {
            double f = -c;
            
            float N1Ldx = (float)N1.getX();
            float N1Ldy = (float)N1.getY();
            float N2Ldx = (float)N2.getX();
            float N2Ldy = (float)N2.getY();

            N1Ldx += xDist / dist * f;
            N1Ldy += yDist / dist * f;

            N2Ldx -= xDist / dist * f;
            N2Ldy -= yDist / dist * f;
            
            N1.setX((double)N1Ldx);
            N1.setY((double)N1Ldy);
            N2.setX((double)N2Ldx);
            N2.setY((double)N2Ldy);
        }
    }

    public static void fcUniAttractor(VisualItem N1, VisualItem N2, float c) {
        double xDist = N1.getX() - N2.getX();	// distance en x entre les deux noeuds
        double yDist = N1.getY() - N2.getY();
        double dist = (float) Math.sqrt(xDist * xDist + yDist * yDist);	// distance tout court

        if (dist > 0) {
            double f = attraction(c, dist);
            
            float N2Ldx = (float)N2.getX();
            float N2Ldy = (float)N2.getY();

            N2Ldx -= xDist / dist * f;
            N2Ldy -= yDist / dist * f;
            
            N2.setX((double)N2Ldx);
            N2.setY((double)N2Ldy);
        }
    }

    protected static double attraction(double c, double dist) {
        return 0.01 * -c * dist;
    }

    protected static double repulsion(double c, double dist) {
        return 0.001 * c / dist;
    }
}
