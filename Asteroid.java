package gamesample;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Polygon;

public class Asteroid extends Sprite{
    private double grandeur;
    private int type;
    private double angle;
    
    public Asteroid(double x, double y, int t, double g, double a){
        super(x, y, 1, 1);
        grandeur = g;
        type = t;
        angle = a;
        switch(t){
            case 0: // polygone a 7 points
                super.setForme(new double[]{x, y, x-15, y+5, x-20, y+30, x-3, y+50, x+30, y+35, x+30, y+15, x+3, y+10});
            break;
            case 1: // polygone à 5 points
                super.setForme(new double[]{x, y, x-20, y+15, x-15, y+40, x+30, y+37, x+45, y+15});
            break;
            case 2: // polygone à 10 points
                super.setForme(new double[]{x, y, x+25, y, x+37, y+20, x+27, y+40, x+23, y+30, x, y+33, x-5, y+40, x-15, y+30, x-10, y+15, x, y+13});
            break;
            default:
                super.setForme(new double[]{0, 0});
            break;
        }
        setForme(homothetie(g, super.getForme()));
        super.tourner(a);
    }
    
    public int getLeType(){
        return type;
    }
    public double getAngle(){
        return angle;
    }
    
    public void dessiner(GraphicsContext gc){
        double[] X, Y;
        X = new double[super.getForme().getPoints().size() / 2];
        Y = new double[super.getForme().getPoints().size() / 2];
        for(int i = 0; i < X.length; i++){
            X[i] = super.getForme().getPoints().get(2*i);
            Y[i] = super.getForme().getPoints().get(2*i + 1);
        }
        gc.strokePolygon(X, Y, X.length);
    }
    
    public void setForme(Polygon p){
        double points[] = new double[p.getPoints().size()];
        for(int i = 0; i < p.getPoints().size(); i++){
            points[i] = p.getPoints().get(i);
        }
        super.setForme(points);
    }
    public void setGrandeur(int g){
        grandeur = g;
    }
    public double getGrandeur(){
        return grandeur;
    }
    
    /*public void deplacer(double dist){
        double[] tmp = new double[super.getForme().getPoints().size()];
        for(int i = 0; i < super.getForme().getPoints().size(); i++){
            tmp[i] = super.getForme().getPoints().get(i) + dist;
        }
        super.setForme(tmp);
    }*/
    public void deplacer(Point2D D, double angle){
        double[] tmp = new double[super.getForme().getPoints().size()];
        for(int i = 0; i < super.getForme().getPoints().size()/2; i++){
            tmp[2*i] = ((super.getForme().getPoints().get(2*i) - super.getForme().getPoints().get(0)) + D.getX());
            tmp[2*i+1] = ((super.getForme().getPoints().get(2*i+1) - super.getForme().getPoints().get(1)) + D.getY());
        }
        super.tourner(angle);
        super.setForme(tmp);
    }
    
    private Polygon homothetie(double rapport, Polygon figure){
        GameSample gs = new GameSample();
        double[] tmp = new double[figure.getPoints().size()];
        Point2D pt, pt2, centre = gs.pointIntersection(new Point2D(figure.getBoundsInLocal().getMinX(), figure.getBoundsInLocal().getMinY()), new Point2D(figure.getBoundsInLocal().getMaxX(), figure.getBoundsInLocal().getMaxY()), new Point2D(figure.getBoundsInLocal().getMinX() + figure.getBoundsInLocal().getWidth(), figure.getBoundsInLocal().getMinY()), new Point2D(figure.getBoundsInLocal().getMinX(), figure.getBoundsInLocal().getMinY() + figure.getBoundsInLocal().getHeight()));
        for(int i = 0; i < figure.getPoints().size()/2; i++){
            pt2 = new Point2D(figure.getPoints().get(2*i), figure.getPoints().get(2*i+1));
            pt = gs.movix2(centre, pt2, rapport * centre.distance(pt2));
            tmp[2*i] = pt.getX();
            tmp[2*i+1]= pt.getY();
        } 
        for(int i = 0; i < tmp.length; i++)
            figure.getPoints().set(i, tmp[i]);
        
        return figure;
    }
}
