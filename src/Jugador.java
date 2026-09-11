import java.util.Random;

import javax.swing.JPanel;

public class Jugador {

    private final int TOTAL_CARTAS = 10;
    private final int MARGEN = 10;
    private final int DISTANCIA = 40;

    private Carta[] cartas = new Carta[TOTAL_CARTAS];
    private Random r = new Random();

    public void repartir(int cantidadBarajas, int[] vecesUsada) {
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            int indice;
            do {
                indice = r.nextInt(52) + 1;
            } while (vecesUsada[indice] >= cantidadBarajas);
            vecesUsada[indice]++;
            cartas[i] = new Carta(indice);
        }
    }

    public void mostrar(JPanel pnl) {
        pnl.removeAll();
        pnl.setLayout(null);
        int posicion = MARGEN + TOTAL_CARTAS * DISTANCIA;
        for (Carta carta : cartas) {
            posicion -= DISTANCIA;
            carta.mostrar(pnl, posicion, MARGEN);
        }
        pnl.repaint();
    }

    public String getGrupos() {
        boolean[] usada = new boolean[TOTAL_CARTAS];

        String grupos = textoGruposPorNombre(usada);
        String escaleras = textoEscalerasMismaPinta(usada);
        int puntaje = calcularPuntaje(usada);

        String resultado = "";
        if (grupos.isEmpty() && escaleras.isEmpty()) {
            resultado = "No se encontraron grupos\n";
        } else {
            if (!grupos.isEmpty()) {
                resultado += "Se encontraron los siguientes grupos:\n" + grupos;
            }
            if (!escaleras.isEmpty()) {
                if (!resultado.isEmpty()) {
                    resultado += "\n";
                }
                resultado += "Escaleras de la misma pinta:\n" + escaleras;
            }
        }
        resultado += "Puntaje: " + puntaje;
        return resultado;
    }

    private String textoGruposPorNombre(boolean[] usada) {
        String texto = "";
        int[] contadores = new int[NombreCarta.values().length];
        for (Carta carta : cartas) {
            contadores[carta.getNombre().ordinal()]++;
        }
        for (int i = 0; i < contadores.length; i++) {
            if (contadores[i] >= 2) {
                texto += Grupo.values()[contadores[i]] + " de " + NombreCarta.values()[i] + "\n";
            }
        }
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            if (contadores[cartas[i].getNombre().ordinal()] >= 2) {
                usada[i] = true;
            }
        }
        return texto;
    }

    private String textoEscalerasMismaPinta(boolean[] usada) {
        int totalPintas = Pinta.values().length;
        int totalNombres = NombreCarta.values().length;
        int[][] mesa = new int[totalPintas][totalNombres];
        boolean[][] enEscalera = new boolean[totalPintas][totalNombres];

        for (Carta carta : cartas) {
            mesa[carta.getPinta().ordinal()][carta.getNombre().ordinal()]++;
        }

        String texto = "";
        for (int p = 0; p < totalPintas; p++) {
            int inicio = -1;
            for (int n = 0; n <= totalNombres; n++) {
                boolean presente = n < totalNombres && mesa[p][n] > 0;
                if (presente) {
                    if (inicio < 0) {
                        inicio = n;
                    }
                } else if (inicio >= 0) {
                    int longitud = n - inicio;
                    if (longitud >= 2) {
                        texto += Grupo.values()[longitud] + " de " + Pinta.values()[p]
                                + " en escalera de " + NombreCarta.values()[inicio]
                                + " a " + NombreCarta.values()[n - 1] + "\n";
                        for (int k = inicio; k < n; k++) {
                            enEscalera[p][k] = true;
                        }
                    }
                    inicio = -1;
                }
            }
        }

        for (int i = 0; i < TOTAL_CARTAS; i++) {
            int p = cartas[i].getPinta().ordinal();
            int n = cartas[i].getNombre().ordinal();
            if (enEscalera[p][n]) {
                usada[i] = true;
            }
        }
        return texto;
    }

    private int calcularPuntaje(boolean[] usada) {
        int suma = 0;
        for (int i = 0; i < TOTAL_CARTAS; i++) {
            if (!usada[i]) {
                suma += cartas[i].getValorPuntaje();
            }
        }
        return suma;
    }

}
