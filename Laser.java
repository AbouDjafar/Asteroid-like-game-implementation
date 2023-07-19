package gamesample;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Polygon;

/**
 * @author Djafar
 */
public class Laser extends Sprite implements Atout {
    private boolean statut;
    private int taille;
    private Polygon forme;
    private double[] trajectoire;
    private Point2D ptA;
    private Point2D ptB;
    
    public Laser(Point2D A, Point2D C, Point2D B, int t){
        statut = true;
        taille = t;
        ptA = A;
        ptB = B;        
        // la trajectoire est la droite passant par les points A et C du polygone du vaisseau
        double m = (C.getY() - A.getY()) / (C.getX() - A.getX());
        trajectoire = new double[]{m, (A.getY() - m*A.getX())};        
        super.setXPos(B.getX());
        super.setYPos(B.getY());
        super.setVitesseX(1);
        super.setVitesseY(1);
        super.setForme(new double[]{B.getX(), B.getY(), A.getX(), A.getY()});
        
        //super.setLargeur(new Line(A.getX(), A.getY(), B.getX(), B.getY()).getBoundsInLocal().getWidth());
        //super.setLongueur(new Line(A.getX(), A.getY(), B.getX(), B.getY()).getBoundsInLocal().getHeight());
    }
    
    public Laser(){
        this(new Point2D(0,0), new Point2D(0,0), new Point2D(0,0), 0);
    }
    
    public Point2D getPtA(){
        return ptA;
    }
    
    public int getTaille(){
        return taille;
    }
    
    public Point2D getPtB(){
        return ptB;
    }
    
    public void setPtA(Point2D A){
        ptA = A;
    }
    
    public void setPtB(Point2D B){
        ptB = B;
        super.setXPos(B.getX());
        super.setYPos(B.getY());
    }
    
    public void setTaille(int t){
        taille = t;
    }
    
    public void setStatut(boolean s){
        statut = s;
    }
    
    public void dessiner(GraphicsContext gc, Point2D debut, Point2D fin){
        gc.strokeLine(debut.getX(), debut.getY(), fin.getX(), fin.getY());
    }
    @Override
    public boolean estActif(){
        return statut;
    }

    @Override
    public void dessiner(GraphicsContext gc) {
        gc.strokePolygon(new double[]{ptB.getX(), ptA.getX()}, new double[]{ptB.getY(), ptA.getY()}, 2);
    }
    
}
