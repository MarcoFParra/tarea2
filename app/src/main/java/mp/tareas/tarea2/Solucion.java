package mp.tareas.tarea2;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;

public class Solucion {

    ArrayList<Point> puntos = new ArrayList<>();
    Bitmap bitmap;
    public Solucion(ArrayList<Reina> reinas, Bitmap bitmap)
    {
        for (Reina reina:reinas) {
            puntos.add(new Point(reina.getPosColumna(),reina.getPosFila()));
        }
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public ArrayList<Point> getPuntos() {
        return puntos;
    }

    ArrayList<Boolean> booleans = new ArrayList<>();

    public boolean equals(Solucion solucion) {
        for (Point pointLocal : puntos) {
            for (Point point : solucion.getPuntos()) {
                if (pointLocal.equals(point)) {
                    booleans.add(true);
                    break;
                }
            }
        }

        if (booleans.size() == puntos.size())
            return true;
        else
            return false;
    }
}
