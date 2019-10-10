package mp.tareas.tarea2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SolucionesAdapter extends RecyclerView.Adapter<SolucionesAdapter.ViewHolder> {
    private ViewHolder viewHolder;
    private ArrayList<Solucion> Soluciones;
    private Activity activity;

    public SolucionesAdapter(Activity context, ArrayList<Solucion> Soluciones)
    {
        this.Soluciones = Soluciones;
        this.activity = context;
    }

    public void update(ArrayList<Solucion> Soluciones)
    {
        this.Soluciones = Soluciones;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.btn_main, parent, false);
        viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    public int getScreenWidth() {

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        if (viewHolder != null) {

            BitmapDrawable ob = new BitmapDrawable(activity.getResources(), Soluciones.get(i).getBitmap());
            viewHolder.img_route.setBackground(ob);
            //viewHolder.img_route.setImageBitmap(Soluciones.get(i).getBitmap());
            viewHolder.lbl_name.setText("Solucion " + String.valueOf( i + 1) );
            viewHolder.itemView.setClickable(true);
            viewHolder.itemView.setFocusable(true);

        }
    }

    @Override
    public int getItemCount() {
        return (null != Soluciones ? Soluciones.size() : 0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView img_route;
        private TextView lbl_name;

        public ViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.card);
            img_route = v.findViewById(R.id.img_button);
            lbl_name = v.findViewById(R.id.tv_button);

        }
    }

    private static Bitmap setImage(Solucion solucion,int width,int height) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);


        Canvas canvas = new Canvas(image);
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

                for (Point point : solucion.getPuntos()) {
                    if (point.equals(col,row))
                    {
                        paint.setColor(Color.RED);
                        break;
                    }
                }
                canvas.drawRect(new Rect(x, y, width / 6, height / 6), paint);
            }
        }

        return image;
    }
}
