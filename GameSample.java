package gamesample;

import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 *
 * @author Djafar
 */
enum Methode {TRACER, PEINDRE};

public class GameSample extends Application {
    private Scene scene;
    private Group root;
    private Canvas toile;
    private GraphicsContext gc;
    private ArrayList<String> touches;
    private long lastTime;
    private double inertie;
    private int wrapped, wrapped2, vie;
    private Vaisseau vs;
    private ScreenItem laserBar, shield, overload, phase;
    private boolean feu, pause;
    private Orbe orb1, orb2;
    private Overload AOE; // All Of Ennemy (lol)
    private boolean phasing;
    private long[] score;
    private ArrayList<Asteroid> asteroides;
    private Asteroid[] subAstero;    
    
    @Override
    public void init(){
        root = new Group();
        scene = new Scene(root);
        toile = new Canvas(800, 700);
        gc = toile.getGraphicsContext2D();        
        touches = new ArrayList<>();        
        inertie = 1.0;
        wrapped = 0;        
        wrapped2 = 0;
        phasing = false;
        vie = 3;
        score = new long[2];
        score[0] = 0;
        score[1] = 0;
        subAstero = new Asteroid[2];
        feu = false;
        pause = false;
        root.getChildren().add(toile);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setResizable(false);
        ArrayList<Laser> laserStack = new ArrayList<>();
        ArrayList<Laser> uselessLasers = new ArrayList<>();
        ArrayList<Asteroid> uselessAsteroids = new ArrayList<>();
        Asteroid[] subAstero = new Asteroid[2];
        double[] vars = new double[16];
        /*
            vars[0]: permet de gèrer la barre de chargement du laser (lasrBar)
            vars[1]: permet de signaler si le super laser est activé ou non ie 0 pour NON et 1 pour OUI
            vars[2]: temps écoulé après l'activation du super laser
            vars[3]: active les boucliers
            vars[4], vars[5]: somme modulable de l'angle de rotation des boucliers
            vars[6]: temps écoulé après l'activation des boucliers
            vars[7]: active l'overload
            vars[8]: temps d'immobilisation du vaisseau après activation de l'overload
            vars[9]: durée de l'état de phase
            vars[10]: nombre d'overload disponible
            vars[11]: temps restant avant la fin de l'effet d'overload
            vars[12]: nombre de boucliers disponible
            vars[13]: temps restant avant disparition des boucliers
            vars[14]: nombre de phase disponible
            vars[15]: temps restant avant la fin de la phase
        */
        vars[1] = 0;
        vars[3] = 0;
        vars[4] = 0;
        vars[5] = 0;
        vars[6] = 0;
        vars[7] = 0;
        vars[8] = 1500;
        vars[9] = 1000;
        vars[10] = 0;
        vars[11] = 0;
        vars[12] = 0;
        vars[13] = 0;
        vars[14] = 0;
        vars[15] = 0;
        vs = new Vaisseau();
        vs.setVitesseX(1.0);
        vs.setVitesseY(1.0);
        vs.deplacer(toile.getWidth()/2, (toile.getHeight() / 2) - vs.getLongueur());
        laserBar = new ScreenItem(50, toile.getHeight() - 65, 0, 20);
        overload = new ScreenItem(460, toile.getHeight() - 120, 100, 100);
        shield = new ScreenItem(560, toile.getHeight() - 120, 100, 100);
        phase = new ScreenItem(660, toile.getHeight() - 120, 100, 100);
        asteroides = new ArrayList<>();
// ---------------------------------------------------------------------        
        primaryStage.setTitle("GameSample");
        primaryStage.setScene(scene);
        scene.setFill(Color.BLACK);
        primaryStage.setWidth(800);
        primaryStage.setHeight(700);
// ----------------------------------------------------------------------
        scene.setOnKeyPressed((KeyEvent e) -> {            
            String valeur = e.getCode().toString();
            if(!touches.contains(valeur) && vars[8] >= 1500)
                touches.add(valeur);            
        });
        scene.setOnKeyReleased((KeyEvent e) -> {            
            String valeur = e.getCode().toString();
            touches.remove(valeur);
            //System.out.println("inertie: "+inertie); // test
        });
// ----------------------------------------------------------------------
        lastTime = System.nanoTime();
        AnimationTimer gameLoop;
        gameLoop = new AnimationTimer(){
            @Override
            public void handle(long now) {
                if(phasing){
                    gc.setFill(Color.BLACK);
                    gc.setStroke(Color.BLACK);
                }else{
                    gc.setFill(Color.WHITE);
                    gc.setStroke(Color.WHITE);
                }
                double elapsedTime = (now - lastTime) / 9999990.0;
                lastTime = now;       
                if(touches.contains("ESCAPE")){            
                    System.out.println("pause!");
                    if(pause){
                        pause = false;
                    }else
                        pause = true;
                }
                if(touches.contains("RIGHT")){                    
                    vs.tourner(elapsedTime);
                }
                if(touches.contains("LEFT")){
                    vs.tourner(-elapsedTime);
                }                
                if((touches.contains("UP") || inertie > 1.0) && vars[1] == 0.0){
                    inertie = (!touches.contains("UP")) ? Math.max(1.0, (inertie - elapsedTime/4)) : Math.min(15.0, (inertie + elapsedTime));
                    wrapped = Math.max(wrapped - 1, 0);
                    double angl = vs.getAngle();
                    Point2D W = wrapAround(vs);
                    if(W != null){
                        vs = new Vaisseau(W.getX(), W.getY());
                        vs.tourner(angl);        
                    }
                    
                    Point2D A, B, C, D, B_prim, D_prim;
                    A = new Point2D(vs.getForme().getPoints().get(0), vs.getForme().getPoints().get(1));
                    B = new Point2D(vs.getForme().getPoints().get(2), vs.getForme().getPoints().get(3));
                    C = new Point2D(vs.getForme().getPoints().get(4), vs.getForme().getPoints().get(5));
                    D = new Point2D(vs.getForme().getPoints().get(6), vs.getForme().getPoints().get(7));
                    B_prim = symetrie(A, B);
                    D_prim = symetrie(A, D);
                    //vs.deplacer(new Point2D[]{movix(A, C, inertie, vs.getSens(), inertie), movix(B, D_prim, inertie, vs.getSens(), inertie), movix(C, A, inertie, vs.getSens(), inertie), movix(D, B_prim, inertie, vs.getSens(), inertie)});
                    vs.deplacer(new Point2D[]{movix2(A, symetrie(A, C), inertie), movix2(B, D_prim, inertie), movix2(C, A, inertie), movix2(D, B_prim, inertie)});
                    //System.out.println("angle: "+(vs.getAngle())+"\ndistance: "+pFixe.distance(vs.getForme().getPoints().get(0), vs.getForme().getPoints().get(1)));
                    /* ------- pour le test ----------
                    Polyline trajectoire1, trajectoire2, trajectoire3;
                    trajectoire1 = setTrajectoire(pointIntersection(new Point2D(0, 600), new Point2D(600, 600), A, C), C, A, pointIntersection(new Point2D(0, 0), new Point2D(600, 0), C, A));
                    trajectoire2 = setTrajectoire(pointIntersection(new Point2D(0, 600), new Point2D(600, 600), B, D_prim), B, D_prim, pointIntersection(new Point2D(0, 0), new Point2D(600, 0), B, D_prim));
                    trajectoire3 = setTrajectoire(pointIntersection(new Point2D(0, 600), new Point2D(600, 600), D, B_prim), D, B_prim, pointIntersection(new Point2D(0, 0), new Point2D(600, 0), D, B_prim));
                    System.out.println("\n"+trajectoire1.getPoints()+"\n"+trajectoire2.getPoints()+"\n"+trajectoire3.getPoints());
                    drawPath(trajectoire1, gc);
                    drawPath(trajectoire2, gc);
                    drawPath(trajectoire3, gc);
                    ------------------------------ */
                    
                }
        // ********* Les lasers ********************
                Point2D A, B, C, D;
                A = new Point2D(vs.getForme().getPoints().get(0), vs.getForme().getPoints().get(1));
                B = new Point2D(vs.getForme().getPoints().get(2), vs.getForme().getPoints().get(3));
                C = new Point2D(vs.getForme().getPoints().get(4), vs.getForme().getPoints().get(5));
                D = new Point2D(vs.getForme().getPoints().get(6), vs.getForme().getPoints().get(7));
                Laser ray = new Laser(A, C, movix2(A, C, 5), 5);
                if(laserBar.getDim().getWidth() <= 1)
                    vars[1] = 0;
                
                if(touches.contains("SPACE")){
                    feu = true;
                    if(laserBar.getDim().getWidth() < 100)
                        laserBar.setLargeur(laserBar.getDim().getWidth() + 1);                                      
                    //System.out.println("elapsed "+laserStack.size()+": "+elapsedTime);
                }else{                      
                    if(feu){
                        if(laserBar.getDim().getWidth() >= 100){
                            vars[2] = 300;
                            vars[1] = 1;
                            System.out.println("super Laser");
                        }else
                            vars[1] = 0;
                        laserStack.add(ray);
                        feu = false;
                    }    
                    laserBar.setLargeur(laserBar.getDim().getWidth() - vars[0]);
                } 
                if(vars[1] == 1.0 && vars[2] > 0){
                    vars[0] = 0;
                    vars[2] = Math.max(0, vars[2] - elapsedTime);
                    ray.setTaille(200);
                    ray.setPtB(movix2(C, A, ray.getTaille()));
                    laserStack.add(ray);
                }else
                    vars[0] = elapsedTime;
        // ********   Les boucliers  *************
                Point2D centre = movix2(C, A, 20);
                if(touches.contains("B") && vars[3] == 0){
                    shield.dessiner(gc, 80, 90, 360);
                    vars[13] = 0;
                    vars[3] = 1;                       
                    orb1 = new Orbe(movix2(A, C, 80).getX(), movix2(A, C, 80).getY(), 100);
                    orb2 = new Orbe(movix2(A, C, 80).getX(), movix2(A, C, 80).getY(), 100);
                    score[0] = -500;
                }
                if(vars[3] == 1){  // pour augmenter la vitesse des boucliers, augmenter le multiplicateur de elapsedTime
                    orb1 = new Orbe(orb1.rotX(centre, movix2(A, C, 80), vars[4] = (vars[4] + elapsedTime*2) % 360), orb1.rotY(centre, movix2(A, C, 90), vars[4] = (vars[4] + elapsedTime*2) % 360), orb1.getStatut());
                    orb2 = new Orbe(orb2.rotX(centre, movix2(A, C, 80), vars[5] = (vars[5] - elapsedTime*2) % 360), orb2.rotY(centre, movix2(A, C, 90), vars[5] = (vars[5] - elapsedTime*2) % 360), orb2.getStatut());
                    if(vars[6] >= 3000){
                        vars[6] = 0;
                        vars[3] = 0;
                    }else
                        vars[6] += elapsedTime;
                }
        // *******       Overload     ***************
                if(touches.contains("V") && vars[7] == 0){
                    overload.dessiner(gc, 80, 90, 360);
                    vars[7] = 1;
                    vars[8] = 0;
                    vars[11] = 0;
                    AOE = new Overload(C.getX(), C.getY(), 100);
                    score[0] = -2000;
                }
                if(vars[7] > 0){
                    AOE.setRayon(AOE.getRayon() + elapsedTime);                    
                }
                if(vars[8] < 1500)
                    vars[8] += elapsedTime;
        // **********       La Phase        *****************
                if(touches.contains("N") && !phasing){
                    phase.dessiner(gc, 80, 90, 360);
                    phasing = true;
                    scene.setFill(Color.WHITE);                    
                    vars[9] = 0;
                    vars[15] = 0;
                    score[0] = -1000;
                }
                if(vars[9] < 1000){
                    vars[9] += elapsedTime;
                }else{
                    if(scene.getFill().equals(Color.WHITE)){
                        scene.setFill(Color.BLACK);
                        phasing = false;
                    }
                }
                if(score[0] > 0){
                    score[0] -= 5;
                    score[1] += 5;
                }
                if(score[0] < 0){
                    score[0] += 5;
                    score[1] -= 5;
                }
                
                if(touches.contains("P") && !asteroides.isEmpty()) // test only
                    asteroides.get(0).tourner(elapsedTime);
                if(touches.contains("M")){ // test only
                    score[0] -= 100;
                }
                if(touches.contains("A")){ // test only
                    Asteroid a = new Asteroid(Math.random() * toile.getWidth(), Math.random() * toile.getHeight(), (int)(Math.min(2, Math.random() * 3)), 1.5, Math.random() * 360);
                    asteroides.add(a);
                }
// ----------------------------------------------------------------------------------
            if(!pause){    
                gc.clearRect(0, 0, scene.getWidth(), scene.getHeight());
                /*      Asteroides      */
                    subAstero[0] = null;
                    subAstero[1] = null;
                    wrapped2 = Math.max(wrapped2 - 1, 0);
                    if(!uselessAsteroids.isEmpty()){
                        asteroides.removeAll(uselessAsteroids);
                        uselessAsteroids.clear();
                    }   
                    asteroides.forEach((Asteroid a) -> {   
                        Point2D p = null;
                        if(toile.intersects(a.getForme().getBoundsInLocal()))
                            p = movix2(new Point2D(a.getForme().getPoints().get(0), a.getForme().getPoints().get(1)), new Point2D(a.getForme().getPoints().get(8), a.getForme().getPoints().get(9)), elapsedTime/(2*a.getGrandeur()));
                        else{
                            p = wrapAround2(a);
                        } 
                        a.deplacer(p, a.getAngle());
                        a.dessiner(gc);
                        if(AOE != null){
                            if(AOE.estActif() && a.collision(AOE)){
                                uselessAsteroids.add(a);
                                score[0] += 20;
                            }
                        }
                        if(orb1 != null){
                            if(a.collision(orb1) && orb1.estActif()){
                                orb1.setStatut(orb1.getStatut() - (15 / a.getLeType()));
                                uselessAsteroids.add(a);
                                subAstero[0] = fission(a, 0);
                                subAstero[1] = fission(a, 1);
                            }
                        }
                        if(orb2 != null){
                            if(a.collision(orb2) && orb2.estActif()){
                                orb2.setStatut(orb2.getStatut() - (int)(10 * a.getGrandeur()));
                                uselessAsteroids.add(a);
                                subAstero[0] = fission(a, 0);
                                subAstero[1] = fission(a, 1);
                            }
                        }
                        laserStack.forEach((Laser l) -> { // collision avec un faisseau laser
                            if(a.getForme().contains(l.getPtB())){
                                subAstero[0] = fission(a, 0);
                                subAstero[1] = fission(a, 1);
                                uselessLasers.add(l);
                                uselessAsteroids.add(a);                           
                            }
                        });
                        if(a.collision(vs) && !phasing){
                            vie--;                  
                            subAstero[0] = fission(a, 0);
                            subAstero[1] = fission(a, 1);
                            uselessAsteroids.add(a);
                        }
                    });
                    if(subAstero[0] != null && subAstero[1] != null){
                        asteroides.add(subAstero[0]);
                        asteroides.add(subAstero[1]);
                    }

                    gc.strokeText("Laser ", 10, toile.getHeight() - 50);
                    gc.strokeRoundRect(50, toile.getHeight() - 65, 100, 20, 20, 20);
                    if(laserBar.getDim().getWidth() > 1)
                        laserBar.dessiner(gc, 20);
                    gc.strokeText("Score "+score[1], toile.getWidth() - 120, 30);
                    gc.fillPolygon(new double[]{30, 37, 30, 23}, new double[]{10, 30, 25, 30}, 4);
                    gc.strokeText("X "+vie, 45, 30);     

                    vs.dessiner(gc, Methode.TRACER);    // le vaisseau spatial

            // *************** Rendering des propulseurs ****************
                    Point2D p1, p2, p3, p4;
                    p1 = movix2(A, C, A.distance(C) + 20);
                    p3 = movix2(A, C, A.distance(C) + 45);
                    p4 = movix2(B, p1, B.distance(p1) + 5);
                    p2 = movix2(D, p1, D.distance(p1) + 5);
                    if(touches.contains("UP"))
                        gc.fillPolygon(new double[]{p1.getX(), p2.getX(), p3.getX(), p4.getX()}, new double[]{p1.getY(), p2.getY(), p3.getY(), p4.getY()}, 4);

                    if(!uselessLasers.isEmpty()){
                        laserStack.removeAll(uselessLasers);
                        uselessLasers.clear();
                    }
                    for(Laser l: laserStack){ // les rayons laser
                        l.setStatut(toile.contains(l.getPtA()));
                        if(l.estActif()){
                            l.setPtA(movix2(l.getPtA(), l.getPtB(), 10));
                            l.setPtB(movix2(l.getPtB(), symetrie(l.getPtB(), l.getPtA()), 10));
                            l.dessiner(gc);
                        }else
                            uselessLasers.add(l);
                    }                
                    if(vars[3] == 1.0 && (orb1.estActif() || orb2.estActif()) && vars[6] < 3000){
                        vars[13] += elapsedTime;
                        shield.dessiner(gc, 80, 90, 360 - (360 * vars[13] / 3000));
                        if(orb1.estActif())
                            orb1.dessiner(gc);
                        if(orb2.estActif())
                            orb2.dessiner(gc);
                    }
                    if(vars[7] > 0){
                        AOE.setstatut(AOE.getRayon() <= toile.getWidth());                    
                        if(AOE.estActif()){
                            AOE.dessiner(gc);                        
                        }else
                            vars[7] = 0;
                    }
                    if(vars[8] < 1500){
                        vars[11] += elapsedTime;
                        overload.dessiner(gc, 80, 90, 360 - (360 * vars[11] / 1500));
                    }

                    if(phasing)
                        gc.setFill(Color.BLACK);
                    else
                        gc.setFill(Color.WHITE);                
                    if(phasing){
                        vars[15] += elapsedTime;
                        phase.dessiner(gc, 80, 90, 360 - (360 * vars[15] / 1000));
                    }
                    gc.strokeOval(470, (toile.getHeight() - 110), 60, 60); // pour overload
                    gc.strokeOval(570, (toile.getHeight() - 110), 60, 60); // pour bouclier
                    gc.strokeOval(670, (toile.getHeight() - 110), 60, 60); // pour phase
                    if(phasing)
                        gc.setFill(Color.WHITE);
                    else
                        gc.setFill(Color.BLACK);
                    gc.fillArc(470, (toile.getHeight() - 110), 60, 60, 0, 360, ArcType.ROUND); // pour overload
                    gc.fillArc(570, (toile.getHeight() - 110), 60, 60, 0, 360, ArcType.ROUND); // pour bouclier
                    gc.fillArc(670, (toile.getHeight() - 110), 60, 60, 0, 360, ArcType.ROUND); // pour phase
                    if(phasing)
                        gc.setFill(Color.BLACK);
                    else
                        gc.setFill(Color.WHITE);
                    vars[10] = score[1] / 2000;
                    vars[12] = score[1] / 500;
                    vars[14] = score[1] / 1000;
                    gc.fillText("Overload\n\tx"+(int)(vars[10]), 475, (toile.getHeight() - 75));
                    gc.fillText("Bouclier\n\tx"+(int)(vars[12]), 575, (toile.getHeight() - 75));
                    gc.fillText("Phase\n\tx"+(int)(vars[14]), 675, (toile.getHeight() - 75));
                }else{
                    gc.strokeRect(toile.getWidth() / 2 - 400 / 2 , toile.getHeight() / 2 - 300 / 2, 400, 300);
                }
            }
        };
        gameLoop.start();
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }   
    
    private double[] equationDroite(Point2D A, Point2D B){
    // Renvoit m et p de l'equation y = mx + p de (AB)
        double m = (B.getY() - A.getY()) / (B.getX() - A.getX());
        return (new double[]{m, (A.getY() - m*A.getX())});
    }
    protected Point2D pointIntersection(Point2D A, Point2D B, Point2D C, Point2D D){
    // Renvoit le point d'intersection de deux droites ie (AB) et (CD)
        double x = 0, y = 0;
        double[] eq1, eq2;       
        if((A.getX() == B.getX()) || (C.getX() == D.getX())){
            if(A.getX() == B.getX()){
                eq2 = equationDroite(C, D);
                x = A.getX();
                y = eq2[1];
            }
            if(C.getX() == D.getX()){
                eq1 = equationDroite(A, B);
                x = C.getX();
                y = eq1[1];
            }
        }else{  
            eq1 = equationDroite(A, B);
            eq2 = equationDroite(C, D);
            x = (eq2[1] - eq1[1]) / (eq1[0] - eq2[0]);
            y = eq1[0] * x + eq1[1];        
        }
        return (new Point2D(x, y));
    }
    private Point2D symetrie(Point2D O, Point2D P){
    // renvoit le symétrique de P ie P' par rapport à O
        double x, y;
        x = O.getX() + (O.getX() - P.getX());
        y = O.getY() + (O.getY() - P.getY());
        return (new Point2D(x, y));
    }
    
    private Polyline setTrajectoire(Point2D A, Point2D B, Point2D C, Point2D D){
        return (new Polyline(new double[]{A.getX(), A.getY(), B.getX(), B.getY(), C.getX(), C.getY(), D.getX(), D.getY()}));
    }
    private void drawPath(Polyline segmnt, GraphicsContext gc){  // For test only
        // Pour dessiner la trajectoire du sprite
        double[] x, y;
        x = new double[segmnt.getPoints().size() / 2];
        y = new double[segmnt.getPoints().size() / 2];
        int j = 0, k = 0;
        for(int i = 0; i < segmnt.getPoints().size(); i++){
            if(i % 2 == 0){
                x[j] = segmnt.getPoints().get(i);
                j++;
            }else{
                y[k] = segmnt.getPoints().get(i);
                k++;
            }
        }
        gc.strokePolyline(x, y, x.length);
    }
    
    private Point2D movix(Point2D A, Point2D B, double dist, Sens s, double d){
    // réaliser la translation de A selon la trajectoire (la droite AB) d'une distance égale à dist en fonction du sens du vecteur directeur
        Point2D tmp1 = null;
        Point2D tmp2 = null;
    // Les la droite passant par tmp1 et tmp2 est celle qui traverse le plan en long ou en large de facon parallèle à un des axes 
    // L'intersection des droites (AB) et (tmp1tmp2) donne le point de destinantion de la translation    
        if(s == Sens.haut || s == Sens.haut_droite || s == Sens.haut_gauche){
            tmp1 = new Point2D(0, A.getY() - dist);
            tmp2 = new Point2D(scene.getWidth(), A.getY() - dist);
        }
        if(s == Sens.bas || s == Sens.bas_droite || s == Sens.bas_gauche){
            tmp1 = new Point2D(0, A.getY() + dist);
            tmp2 = new Point2D(scene.getWidth(), A.getY() + dist);
        }
        if(s == Sens.droite){
            tmp1 = new Point2D(A.getX() + dist, 0);
            tmp2 = new Point2D(A.getX() + dist, scene.getHeight());
        }
        if(s == Sens.gauche){
            tmp1 = new Point2D(A.getX() - dist, 0);
            tmp2 = new Point2D(A.getX() - dist, scene.getHeight());
        }
        double distance = pointIntersection(A, B, tmp1, tmp2).distance(A);
        //System.out.println("distance: "+distance);
        
        return (distance > d) ? movix(A, B, dist/2, s, d) : pointIntersection(A, B, tmp1, tmp2);
    }
    
    public Point2D movix2(Point2D A, Point2D C, double distance){
        /* translation d'un point A suivant le sens et la direction du vecteur AC d'une distance ... distance
            En d'autres termes, translater le point A d'une distance 'distance' vers le point C sur la droite (AC)
        */
        Point2D v = new Point2D(C.getX() - A.getX(), C.getY() - A.getY());
        v = v.normalize();
        v = v.multiply(distance);
        return (new Point2D(A.getX() + v.getX(), A.getY() + v.getY()));
    }
    
    public Point2D wrapAround(Vaisseau v){
        Point2D A, C, M = null, M1 = null, M2 = null;
        double angle = v.getAngle();
        A = new Point2D(v.getForme().getPoints().get(0), v.getForme().getPoints().get(1));
        C = new Point2D(v.getForme().getPoints().get(4), v.getForme().getPoints().get(5));
        Rectangle zone = new Rectangle(0, 0, scene.getWidth(), scene.getHeight()); 
        if(!(zone.contains(A)) && !(zone.contains(C)) && (wrapped == 0)){
            if(v.getSens().equals(Sens.haut)){
                M = pointIntersection(new Point2D(0, scene.getHeight()), new Point2D(scene.getWidth(), scene.getHeight()), A, C);
            }
            if(v.getSens().equals(Sens.bas)){
                M = pointIntersection(new Point2D(0, 0), new Point2D(scene.getWidth(), 0), A, C);
            }
            if(v.getSens().equals(Sens.gauche)){
                M = pointIntersection(new Point2D(scene.getWidth(), 0), new Point2D(scene.getWidth(), scene.getHeight()), A, C);
            }
            if(v.getSens().equals(Sens.droite)){
                M = pointIntersection(new Point2D(0, 0), new Point2D(0, scene.getHeight()), A, C);
            }
            if(v.getSens().equals(Sens.haut_droite)){
                M1 = pointIntersection(new Point2D(0, 0), new Point2D(0, scene.getHeight()), A, C);
                M2 = pointIntersection(new Point2D(0, scene.getHeight()), new Point2D(scene.getWidth(), scene.getHeight()), A, C);
                M = (C.distance(M1) <= C.distance(M2)) ? M1 : M2;
            }
            if(v.getSens().equals(Sens.haut_gauche)){
                M1 = pointIntersection(new Point2D(scene.getWidth(), 0), new Point2D(scene.getWidth(), scene.getHeight()), A, C);
                M2 = pointIntersection(new Point2D(0, scene.getHeight()), new Point2D(scene.getWidth(), scene.getHeight()), A, C);
                M = (C.distance(M1) <= C.distance(M2)) ? M1 : M2;
            }
            if(v.getSens().equals(Sens.bas_droite)){
                M1 = pointIntersection(new Point2D(0, 0), new Point2D(0, scene.getHeight()), A, C);
                M2 = pointIntersection(new Point2D(0, 0), new Point2D(scene.getWidth(), 0), A, C);
                M = (C.distance(M1) <= C.distance(M2)) ? M1 : M2;
            }
            if(v.getSens().equals(Sens.bas_gauche)){
                M1 = pointIntersection(new Point2D(scene.getWidth(), 0), new Point2D(scene.getWidth(), scene.getHeight()), A, C);
                M2 = pointIntersection(new Point2D(0, 0), new Point2D(scene.getWidth(), 0), A, C);
                M = (C.distance(M1) <= C.distance(M2)) ? M1 : M2;
            }
        }
        if(M != null){
            wrapped = 2;
            if(!zone.contains(M) && (!(v.getSens().equals(Sens.haut)) && !(v.getSens().equals(Sens.bas))))
                M = new Point2D(M1.getX(), M2.getY());
        }           
        
        return M;
    }
    
    public Point2D wrapAround2(Asteroid a){
        Point2D A, B, C, D, a1, a2, wrapPoint, hg, hd, bg, bd;
        a1 = new Point2D(a.getForme().getPoints().get(0), a.getForme().getPoints().get(1));
        a2 = new Point2D(a.getForme().getPoints().get(8), a.getForme().getPoints().get(9));
        wrapPoint = null;
        hg = new Point2D(0, 0);
        hd = new Point2D(toile.getWidth() - 1, 0);
        bg = new Point2D(0, toile.getHeight() - 1);
        bd = new Point2D(toile.getWidth() - 1, toile.getHeight() - 1);
        A = pointIntersection(a1, a2, hg, hd);
        B = pointIntersection(a1, a2, hd, bd);
        C = pointIntersection(a1, a2, bd, bg);
        D = pointIntersection(a1, a2, bg, hg);
        //double[] distances = new double[]{a1.distance(A), a1.distance(B), a1.distance(C), a1.distance(D)};
        ArrayList<Double> distances = new ArrayList<Double>();
        distances.add(a1.distance(A));
        distances.add(a1.distance(B));
        distances.add(a1.distance(C));
        distances.add(a1.distance(D));
        distances.sort(null);
        distances.remove(0);
        int i = 0;
        do{
            if(distances.get(i) == a1.distance(A))
                wrapPoint = A;
            if(distances.get(i) == a1.distance(B))
                wrapPoint = B;
            if(distances.get(i) == a1.distance(C))
                wrapPoint = C;
            if(distances.get(i) == a1.distance(D))
                wrapPoint = D;
            i = i + 1;
        }while(!(toile.intersects(wrapPoint.getX(), wrapPoint.getY(), a.getLargeur(), a.getLongueur())) && i < distances.size());
        return wrapPoint;
    }
    
    /*private boolean appartenirDroite(Point2D X, Point2D A, Point2D B){
        // si le point X appartient à la droite AB
        double[] AB = equationDroite(A, B);
        return (X.getY() == AB[0]*X.getX()+AB[1]);
    }*/
    
    public Polygon homothetie(double rapport, Polygon figure){
        double[] tmp = new double[figure.getPoints().size()];
        Point2D pt, pt2, centre = pointIntersection(new Point2D(figure.getBoundsInLocal().getMinX(), figure.getBoundsInLocal().getMinY()), new Point2D(figure.getBoundsInLocal().getMaxX(), figure.getBoundsInLocal().getMaxY()), new Point2D(figure.getBoundsInLocal().getMinX() + figure.getBoundsInLocal().getWidth(), figure.getBoundsInLocal().getMinY()), new Point2D(figure.getBoundsInLocal().getMinX(), figure.getBoundsInLocal().getMinY() + figure.getBoundsInLocal().getHeight()));
        for(int i = 0; i < figure.getPoints().size()/2; i++){
            pt2 = new Point2D(figure.getPoints().get(2*i), figure.getPoints().get(2*i+1));
            pt = movix2(centre, pt2, rapport * centre.distance(pt2));
            tmp[2*i] = pt.getX();
            tmp[2*i+1]= pt.getY();
        } 
        for(int i = 0; i < tmp.length; i++)
            figure.getPoints().set(i, tmp[i]);
        
        return figure;
    }
    
    private Asteroid fission(Asteroid a, int indice){
        Asteroid tmp = null;
        if(a.getGrandeur() == 0.5){
            score[0] += 30;
        }else{
            
            subAstero[1] = new Asteroid(a.getXPos(), a.getYPos(), a.getLeType(), a.getGrandeur() - 0.5, a.getAngle() - 45);
            score[0] += (int)(15 * a.getGrandeur());
            if(indice == 0)
                tmp = new Asteroid(a.getXPos(), a.getYPos(), a.getLeType(), a.getGrandeur() - 0.5, a.getAngle() + 45);
            else if(indice == 1)
                tmp = new Asteroid(a.getXPos(), a.getYPos(), a.getLeType(), a.getGrandeur() - 0.5, a.getAngle() - 45);
        }
        return tmp;
    }
    
}