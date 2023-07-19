package gamesample;

import java.util.Objects;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

/*
 * @author Djafar
 */
enum Sens {haut, bas, gauche, droite, haut_droite, haut_gauche, bas_droite, bas_gauche};

public class Vaisseau extends Sprite{
    private double angle;
    
    public Vaisseau(double x, double y){
        super(x-15, y, 0.0, 0.0);
        super.setForme(new double[]{x, y, x+15, y+50, x, y+40, x-15, y+50});
        angle = 0;
    }
    public Vaisseau(){
        super.setForme(new double[]{15.0, 0.0, 30.0, 50.0, 15.0, 40.0, 0.0, 50.0});
        angle = 0;
    }
    public double getAngle(){
        return angle;
    }
    public void dessiner(GraphicsContext gc, Methode m){
        double[] xPoints, yPoints;
        xPoints = new double[super.getForme().getPoints().size() / 2];
        yPoints = new double[super.getForme().getPoints().size() / 2];
        for(int i = 0; i < super.getForme().getPoints().size() / 2; i++){
            xPoints[i] = super.getForme().getPoints().get(i * 2);
            yPoints[i] = super.getForme().getPoints().get((i * 2) + 1);
        }
        if(m == Methode.PEINDRE){
            gc.fillPolygon(xPoints, yPoints, xPoints.length);
        }else{
            gc.strokePolygon(xPoints, yPoints, xPoints.length);
        }
    }
    public void deplacer(double distanceX, double distanceY){
        super.setXPos(super.getVitesseX() * distanceX);
        super.setYPos(super.getVitesseX() * distanceY);
        double[] pts = new double[super.getForme().getPoints().size()];
        for(int i = 0; i < super.getForme().getPoints().size(); i++){
            if(i % 2 == 0)
                pts[i] = super.getForme().getPoints().get(i) + (super.getVitesseX() * distanceX);
            else
                pts[i] = super.getForme().getPoints().get(i) + (super.getVitesseY() * distanceY);
        }        
        super.setForme(pts);
    }
    public void deplacer(Point2D[] P){
        if(P.length != 4)
            System.err.println("les 4 points requis n'y sont pas!");
        else{
            Point2D M = null;
            if(getSens() == Sens.haut || getSens() == Sens.haut_droite || getSens() == Sens.droite){
                super.setXPos(P[3].getX());
                super.setYPos(P[0].getY());
            }
            if(getSens() == Sens.bas || getSens() == Sens.bas_droite){
                super.setXPos(P[1].getX());
                super.setYPos(P[3].getY());
            }
            if(getSens() == Sens.bas_gauche || getSens() == Sens.gauche){
                super.setXPos(P[0].getX());
                super.setYPos(P[1].getY());
            }
            if(getSens() == Sens.haut_gauche){
                super.setXPos(P[0].getX());
                super.setYPos(P[0].getY());
            }
            super.setForme(new double[]{P[0].getX(), P[0].getY(), P[1].getX(), P[1].getY(), P[2].getX(), P[2].getY(), P[3].getX(), P[3].getY()});
        }
    }
    private double rotXPoint(double px, double py, double cx, double cy, double angle){
        return (Math.cos(angle) * (px - cx) - Math.sin(angle) * (py - cy) + cx);
    }
    private double rotYPoint(double px, double py, double cx, double cy, double angle){
        return (Math.sin(angle) * (px - cx) + Math.cos(angle) * (py - cy) + cy);
    }
    public void tourner(double angle){
        // Effectuer une rotation avec comme pivot la pointe du vaisseau
        angle = angle * (Math.PI / 180);
        this.angle = (this.angle + (angle / (Math.PI/180))) % 360;
        double[] pts = new double[super.getForme().getPoints().size()];
        for(int i = 0; i < super.getForme().getPoints().size(); i++){
            if(i > 1){
                if(i % 2 == 0)
                    pts[i] = rotXPoint(super.getForme().getPoints().get(i), super.getForme().getPoints().get(i + 1), super.getForme().getPoints().get(0), super.getForme().getPoints().get(1), angle);
                else
                    pts[i] = rotYPoint(super.getForme().getPoints().get(i - 1), super.getForme().getPoints().get(i), super.getForme().getPoints().get(0), super.getForme().getPoints().get(1), angle);
            }else{
                pts[i] = super.getForme().getPoints().get(i);
            }
        }        
        super.setForme(pts);
    }
    public Sens getSens(){
        Sens s = null;
        if(Objects.equals(super.getForme().getPoints().get(0), super.getForme().getPoints().get(4))){
            if(super.getForme().getPoints().get(1) < super.getForme().getPoints().get(5))
                s = Sens.haut;
            else
                s = Sens.bas;
        }
        if(Objects.equals(super.getForme().getPoints().get(1), super.getForme().getPoints().get(5))){
            if(super.getForme().getPoints().get(0) > super.getForme().getPoints().get(4))
                s = Sens.droite;
            else
                s = Sens.gauche;
        }
        if(super.getForme().getPoints().get(0) > super.getForme().getPoints().get(4)){
            if(super.getForme().getPoints().get(1) < super.getForme().getPoints().get(5))
                s = Sens.haut_droite;
            else
                s = Sens.bas_droite;
        }
        if(super.getForme().getPoints().get(0) < super.getForme().getPoints().get(4)){
            if(super.getForme().getPoints().get(1) < super.getForme().getPoints().get(5))
                s = Sens.haut_gauche;
            else
                s = Sens.bas_gauche;
        }
        return s;
    }
    public void setForme(Point2D A, Point2D B, Point2D C, Point2D D){
        super.setForme(new double[]{A.getX(), A.getY(), B.getX(), B.getY(), C.getX(), C.getY(), D.getX(), D.getY()});
    }
}
