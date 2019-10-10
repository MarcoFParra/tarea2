package mp.tareas.tarea2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.Build.VERSION_CODES.N;

public class MainActivity extends AppCompatActivity  {

    int columns = 6 ,rows=6;
    int width = 0;
    int height = 0;
    TextView tv_notif;

    boolean termino=false;
    ArrayList<Solucion> solucions = new ArrayList<>();
    private Animation fab_open,fab_close,rotate_forward,rotate_backward,win_anim_open,win_anim_close;

    ArrayList<Reina> reinas = new ArrayList<>();
    private LinearLayout bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_notif = findViewById(R.id.tv_notif);

        win_anim_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        win_anim_close =  AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);

        bottomSheet = (LinearLayout)findViewById(R.id.bottomSheet);

        final BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheet);

        final SolucionesAdapter solucionesAdapter = new SolucionesAdapter(this,solucions);
        final RecyclerView recyclerView = findViewById(R.id.recycler_me);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2,RecyclerView.HORIZONTAL,false));
        recyclerView.setAdapter(solucionesAdapter);



        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        final Timer timer = new Timer();
        final TimerTask myTimerTask = new TimerTask() {
            @Override
            public void run() {

                for (int column = 0; column < columns; column++) {
                    for (int row = 0; row < rows; row++) {
                        for (int r = 0; r < 5; r++) {
                            if (reinas.size() == 0)
                                reinas.add(new Reina(column,row));
                            else
                                reinas.add(new Reina());
                            init(r);
                        }
                        if (addSolucion(reinas)) {
                            try {
                                tv_notif.setText("Solucion encontrada");
                                tv_notif.setTextColor( getResources().getColor(android.R.color.holo_blue_dark));
                                win_anim_open.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        tv_notif.setClickable(false);
                                        tv_notif.startAnimation(win_anim_close);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                solucionesAdapter.update(solucions);
                                recyclerView.scrollBy(1,0);
                                tv_notif.startAnimation(win_anim_open);
                                Thread.sleep(300);


                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            reinas.clear();
                        } else {
                            reinas.clear();
                        }
                    }
                }
                termino = true;
                timer.cancel();
            }
        };
        timer.schedule(myTimerTask, 0);

    }

    boolean addSolucion(ArrayList<Reina> list)
    {
        if (!esSolucion(list))
            return false;
        Solucion solucionTemp = new Solucion(list,Screenshot.takescreenshot(findViewById(R.id.tablero)));
        for (Solucion solucion : solucions)
        {
            if (solucion.equals(solucionTemp))
            return false;

        }
        solucions.add(solucionTemp);
        return true;
    }



    private static Bitmap setImage(Solucion solucion,int width,int height) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Paint paint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint3.setColor(Color.GREEN);
        paint3.setTextAlign(Paint.Align.LEFT);


        Canvas canvas = new Canvas(image);
        canvas.drawCircle(width/2 , height/2,height/2 ,paint3);

        int x, y;
        for (int col = 0; col < 6; col++) {
            for (int row = 0; row < 6; row++) {
                x = (width / 6) * row;
                y = (height / 6) * col;
                if ((row % 2 == 0) == (col % 2 == 0)) {
                    paint.setColor(Color.BLACK);
                    //canvas.drawColor(Color.BLACK);
                } else {
                    paint.setColor(Color.WHITE);
                    //canvas.drawColor(Color.WHITE);
                }

                /*for (Point point : solucion.getPuntos()) {
                    if (point.equals(col,row))
                    {
                        paint.setColor(Color.RED);
                        break;
                    }
                }*/
                canvas.drawRect(new Rect(x, y, width / 6, height / 6), paint);
            }
        }



        return image;
    }

    Boolean esSolucion(ArrayList<Reina> list)
    {
        for (Reina reina:list)
        {
            if (!reina.isPosicionCorrecta())
                return false;
        }
        return true;
    }

    void init(int r) {

        int columnaInicial = reinas.get(r).getPosColumna();
        int filaInicial =  reinas.get(r).getPosFila();

        for (int column = columnaInicial; column < columns; column++) {
            for (int row = filaInicial; row < rows; row++) {
                reinas.get(r).setPos(column, row);
                putImages();
                if (isValidPosition(r)) {
                    reinas.get(r).setPosicionCorrecta(true);
                    return;
                }
                else reinas.get(r).setPosicionCorrecta(false);
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    boolean isValidPosition(int pos) {

        int c = 0, f = 0;
        if (reinas.size() == 1)
            return true;

        for (int r = 0; r < reinas.size(); r++) {
            if (r != pos) {

                if (reinas.get(r).getPosColumna() == reinas.get(pos).getPosColumna())
                    return false;
                if (reinas.get(r).getPosFila() == reinas.get(pos).getPosFila())
                    return false;

                for (c = reinas.get(pos).getPosColumna(), f = reinas.get(pos).getPosFila();
                     (c >= 0 || f >= 0); c--, f--) {
                    if (reinas.get(r).getPosFila() == f && reinas.get(r).getPosColumna() == c)
                        return false;
                }

                for (c = reinas.get(pos).getPosColumna(), f = reinas.get(pos).getPosFila();
                     (c < columns || f >= 0); c++, f--) {
                    if (reinas.get(r).getPosFila() == f && reinas.get(r).getPosColumna() == c)
                        return false;
                }
                for (c = reinas.get(pos).getPosColumna(), f = reinas.get(pos).getPosFila();
                     (c >= 0 || f < rows); c--, f++) {
                    if (reinas.get(r).getPosFila() == f && reinas.get(r).getPosColumna() == c)
                        return false;
                }
                for (c = reinas.get(pos).getPosColumna(), f = reinas.get(pos).getPosFila();
                     (c < columns || f < rows); c++, f++) {
                    if (reinas.get(r).getPosFila() == f && reinas.get(r).getPosColumna() == c)
                        return false;
                }
            }
        }
        return true;
    }


    boolean isSolution()
    {
        boolean solution = false;

        return solution;
    }


    void putImages()
    {
        try {
            quitImages(findViewById(android.R.id.content));
            for (Reina reina: reinas)
            {
                String tag = String.valueOf(reina.getPosColumna()) + String.valueOf(reina.getPosFila());
                ((ImageView)(findViewById(android.R.id.content)).findViewWithTag(tag)).
                        setImageDrawable(getResources().getDrawable(R.drawable.ic_stars_24px));

            }
        }catch (Exception ex){}
    }

    public void quitImages(View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    //you can recursively call this method
                    quitImages(child);
                }
            } else if (v instanceof ImageView) {
                ((ImageView) v).setImageDrawable(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Bitmap setImage(String text) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        int x, y;
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 6; col++) {
                x = width/6 * row;
                y = height/6 * col;
                if ((row % 2 == 0) == (col % 2 == 0)) {
                    paint.setColor(Color.BLACK);
                    canvas.drawColor(Color.BLACK);
                }
                else {
                    paint.setColor(Color.WHITE);
                    canvas.drawColor(Color.WHITE);
                }
                canvas.drawRect(new Rect(x,y,width/6,height/6),paint);
            }
        }

        return image;
    }


}
