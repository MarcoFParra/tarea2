package mp.tareas.tarea2;

public class Reina {

    int posColumna = 0;
    int posFila = 0;
    boolean posicionCorrecta = false;

    public Reina()
    {

    }

    public Reina(int posColumna, int posFila)
    {
        this.posColumna =posColumna;
        this.posFila=posFila;

    }

    public int getPosColumna() {
        return posColumna;
    }

    public int getPosFila() {
        return posFila;
    }

    public void setPos(int posColumna,int posFila) {
        this.posColumna = posColumna;
        this.posFila = posFila;

    }

    public boolean isPosicionCorrecta() {
        return posicionCorrecta;
    }

    public void setPosicionCorrecta(boolean posicionCorrecta) {
        this.posicionCorrecta = posicionCorrecta;
    }
}
