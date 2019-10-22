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
import static java.lang.StrictMath.abs;

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

    SolucionesAdapter solucionesAdapter;
    RecyclerView recyclerView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_notif = findViewById(R.id.tv_notif);

        win_anim_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        win_anim_close =  AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);

        bottomSheet = (LinearLayout)findViewById(R.id.bottomSheet);

        final BottomSheetBehavior bsb = BottomSheetBehavior.from(bottomSheet);

        solucionesAdapter = new SolucionesAdapter(this,solucions);
        recyclerView = findViewById(R.id.recycler_me);
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

                buscar(reinas,0,6);

                termino = true;
                timer.cancel();
            }
        };
        timer.schedule(myTimerTask, 0);

    }


    void buscar(ArrayList<Reina> reinas,int currentRow,int N)
    {
        if(is_solution(reinas, N))
        {
            putImages();
            solucions.add(new Solucion(reinas,Screenshot.takescreenshot(findViewById(R.id.tablero))));
                try {
                    tv_notif.setText("Solucion encontrada");
                    tv_notif.setTextColor( getResources().getColor(android.R.color.holo_blue_dark));
                    win_anim_open.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
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
                    Thread.sleep(100);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
        }
        else
        {
            // construct a vector of valid candidates
            ArrayList<Reina> candidates = new ArrayList<>();
            if(construct_candidates(reinas,currentRow,N,candidates))
            {
                for(int i=0; i < candidates.size(); ++i)
                {
                    // Push this in the partial solution and move further
                    reinas.add(candidates.get(i));

                    buscar(reinas,currentRow + 1, N);
                    // If no feasible solution was found then we ought to remove this and try the next one

                    reinas.remove(reinas.size()-1);
                }
            }
            //putImages(reinas,N);

        }

    }

    boolean is_solution(ArrayList<Reina> reinas, int N)
    {
        return reinas.size() == N;
    }

    boolean construct_candidates( ArrayList<Reina> reinas, int row, int N, ArrayList<Reina> candidates)
    {
        for(int i=0; i<N; ++i)
        {
            putImages();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            candidates.add( new Reina(row,i));
            if(!is_safe_square(reinas,row,i,N))
            {
                candidates.remove( candidates.size()-1);
            }
        }
        return candidates.size() > 0;
    }

    boolean is_safe_square(ArrayList<Reina> reinas, int row, int col, int N)
    {
        for(int i=0; i<reinas.size(); ++i)
        {
            if( i == row || reinas.get(i).getPosFila() == col) return false;
            if(abs(i - row) == abs(reinas.get(i).getPosFila() - col)) return false;
        }
        return true;
    }

   /* void putImages(ArrayList<Reina> reinas, int N)
    {

        quitImages(findViewById(android.R.id.content));
        for(int i=0; i<N; ++i)
        {
            for(int j=0; j<N; ++j)
            {
                if(reinas.get(i) == j){
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String tag = String.valueOf(i) + String.valueOf(j);

                    ((ImageView)(findViewById(android.R.id.content)).findViewWithTag(tag)).
                            setImageDrawable(getResources().getDrawable(R.drawable.ic_stars_24px));
                }
            }
        }
    }
    */









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
        }catch (Exception ex){
            //Toast.makeText(this,ex.toString(),Toast.LENGTH_SHORT).show();
        }
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
