package gamesample;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Circle;

public class Orbe extends Sprite implements Atout{
    private Circle sphere;
    private int statut;
    
    public Orbe(double x, double y, int st){
        super(x, y, 1, 1);
        sphere = new Circle(super.getXPos(), super.getYPos(), 10);
        statut = st;
        super.setXPos(x);
        super.setYPos(y);
        super.setLargeur(sphere.getBoundsInLocal().getWidth());
        super.setLongueur(sphere.getBoundsInLocal().getHeight());
    }
    public Orbe(){
        this(0,0,0);
    }
    
    public int getStatut(){
        return statut;
    }
    public Circle getSphere(){
        return sphere;
    }
    
    public void setStatut(int x){
        statut = x;
    }
    
    public double rotX(Point2D pivot, Point2D x, double angle){
        angle = angle * (Math.PI / 180);
        return (Math.cos(angle) * (x.getX() - pivot.getX()) - Math.sin(angle) * (x.getY() - pivot.getY()) + pivot.getX());
    }
    public double rotY(Point2D pivot, Point2D x, double angle){
        angle = angle * (Math.PI / 180);        
        return (Math.sin(angle) * (x.getX() - pivot.getX()) + Math.cos(angle) * (x.getY() - pivot.getY()) + pivot.getY());
    }
    
    @Override
    public boolean estActif() {
        return (statut > 0);
    }

    @Override
    public void dessiner(GraphicsContext gc) {
        gc.fillOval(sphere.getCenterX() - (sphere.getRadius() / 2), sphere.getCenterY() - (sphere.getRadius() / 2), sphere.getRadius(), sphere.getRadius());
    }

}
