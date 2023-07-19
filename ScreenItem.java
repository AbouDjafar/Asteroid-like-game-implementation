package gamesample;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;

public class ScreenItem{
    private double x;
    private double y;
    private double largeur;
    private double longueur;
    private Object forme;
    
    public ScreenItem(double px, double py, double l, double L){
        x = px;
        y = py;
        largeur = l;
        longueur = L;
    }
    public ScreenItem(){
        this(0,0,0,0);
    }
    public Rectangle2D getDim(){
        return new Rectangle2D(x, y, largeur, longueur);
    }
    
    public void setForme(Object f){
        forme = f;
    }
    public void setLargeur(double n){
        largeur = Math.max(0, n);
    }
    public void setLongueur(double n){
        longueur = Math.max(0, n);
    }
    
    public void dessiner(GraphicsContext gc, double r){
        gc.fillRoundRect(x, y, largeur, longueur, r, r);
    }
    public void dessiner(GraphicsContext gc, double rayon, double angle, double circonference){
        gc.fillArc(x, y, rayon, rayon, angle, circonference, ArcType.ROUND);
    }
}
