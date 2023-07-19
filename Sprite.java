package gamesample;

import javafx.geometry.Rectangle2D;
import javafx.scene.shape.Polygon;

/**
 *
 * @author Djafar
 */
public class Sprite{
    private double xPos;
    private double yPos;
    private double largeur;
    private double longueur;
    private double vitesseX;
    private double vitesseY;
    private double angle;
    private Polygon forme;
    
    public Sprite(double xp, double yp, double vx, double vy){
        xPos = xp;
        yPos = yp;
        vitesseX = vx;
        vitesseY = vy;
        largeur = 0;
        longueur = 0;
    }
    public Sprite(){
        this(0.0, 0.0, 0.0, 0.0);
    }
    public double getXPos(){
        return xPos;
    }
    public double getYPos(){
        return yPos;
    }
    public double getLargeur(){
        return largeur;
    }
    public double getLongueur(){
        return longueur;
    }
    public double getVitesseX(){
        return vitesseX;
    }
    public double getVitesseY(){
        return vitesseY;
    }
    public Polygon getForme(){
        return forme;
    }
    public void setLargeur(double l){
        largeur = l;
    }
    public void setLongueur(double l){
        longueur = l;
    }
    public void setXPos(double n){
        xPos = n;
    }
    public void setYPos(double n){
        yPos = n;
    }
    public void setVitesseX(double n){
        vitesseX = n;
    }
    public void setVitesseY(double n){
        vitesseY = n;
    }
    public void setForme(double[] pts){
        forme = new Polygon(pts);
        largeur = forme.getBoundsInLocal().getWidth();
        longueur = forme.getBoundsInLocal().getHeight();
        xPos = forme.getBoundsInLocal().getMinX();
        yPos = forme.getBoundsInLocal().getMinY();
    }
    public Rectangle2D getDimensions(){
        return new Rectangle2D(xPos, yPos, largeur, longueur);
    }
    public boolean collision(Sprite autre){
        return autre.getDimensions().intersects(this.getDimensions());
    }
    
    private double rotXPoint(double px, double py, double cx, double cy, double angle){
        return (Math.cos(angle) * (px - cx) - Math.sin(angle) * (py - cy) + cx);
    }
    private double rotYPoint(double px, double py, double cx, double cy, double angle){
        return (Math.sin(angle) * (px - cx) + Math.cos(angle) * (py - cy) + cy);
    }
    public void tourner(double angle){
        // Effectuer une rotation avec comme pivot le point de départ 
        angle = angle * (Math.PI / 180);
        this.angle = (this.angle + (angle / (Math.PI/180))) % 360;
        double[] pts = new double[forme.getPoints().size()];
        for(int i = 0; i < forme.getPoints().size(); i++){
            if(i > 1){
                if(i % 2 == 0)
                    pts[i] = rotXPoint(forme.getPoints().get(i), forme.getPoints().get(i + 1), forme.getPoints().get(0), forme.getPoints().get(1), angle);
                else
                    pts[i] = rotYPoint(forme.getPoints().get(i - 1), forme.getPoints().get(i), forme.getPoints().get(0), forme.getPoints().get(1), angle);
            }else{
                pts[i] = getForme().getPoints().get(i);
            }
        }        
        setForme(pts);
    }
}
