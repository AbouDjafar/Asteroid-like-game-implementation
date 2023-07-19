package gamesample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Circle;

public class Overload extends Sprite implements Atout{
    private Circle noyau;
    private double rayon;
    private boolean statut;
       
    public Overload(double x, double y, double r){
        statut = true;
        rayon = r;
        noyau = new Circle(x, y, r);
        super.setXPos(noyau.getBoundsInLocal().getMinX());
        super.setYPos(noyau.getBoundsInLocal().getMinY());
        super.setLargeur(noyau.getBoundsInLocal().getWidth());
        super.setLongueur(noyau.getBoundsInLocal().getHeight());
    }
    public Overload(){
        this(0,0,0);
    }
        
    @Override
    public boolean estActif() {
        return statut;
    }
    
    public double getRayon(){
        return rayon;
    }
    
    public void setRayon(double r){
        rayon = r;
    }
    
    public Circle getNoyau(){
        return noyau;
    }
    
    public void setstatut(boolean s){
        statut = s;        
    }

    @Override
    public void dessiner(GraphicsContext gc) {
        gc.strokeOval(noyau.getCenterX() - rayon/2, noyau.getCenterY() - rayon/2, rayon, rayon);
        super.setXPos(noyau.getCenterX() - rayon/2);
        super.setYPos(noyau.getCenterY() - rayon/2);
        super.setLargeur(rayon);
        super.setLongueur(rayon);
    }
    
}
