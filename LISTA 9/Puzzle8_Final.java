
import java.util.*;

public class Puzzle8_Final {

    static class Estado {
        int[][] tabuleiro;
        String caminho;
        int profundidade;
        int custo;

        Estado(int[][] t, String c, int p, int g) {
            tabuleiro = new int[3][3];
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    tabuleiro[i][j] = t[i][j];
            caminho = c;
            profundidade = p;
            custo = g;
        }

        boolean objetivo() {
            int[][] finalEstado = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 0 } };
            return Arrays.deepEquals(tabuleiro, finalEstado);
        }

        List<Estado> expandir() {
            List<Estado> filhos = new ArrayList<>();
            int[] dx = { -1, 1, 0, 0 };
            int[] dy = { 0, 0, -1, 1 };
            String[] movs = { "U", "D", "L", "R" };
            int x = 0, y = 0;

            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++)
                    if (tabuleiro[i][j] == 0) {
                        x = i;
                        y = j;
                    }

            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i], ny = y + dy[i];
                if (nx >= 0 && nx < 3 && ny >= 0 && ny < 3) {
                    int[][] novo = new int[3][3];
                    for (int a = 0; a < 3; a++)
                        novo[a] = tabuleiro[a].clone();
                    novo[x][y] = novo[nx][ny];
                    novo[nx][ny] = 0;
                    filhos.add(new Estado(novo, caminho + movs[i], profundidade + 1, custo + 1));
                }
            }
            return filhos;
        }

        int heuristica1() {
            int h = 0, val = 1;
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++, val++)
                    if (tabuleiro[i][j] != 0 && tabuleiro[i][j] != val)
                        h++;
            return h;
        }

        int heuristica2() {
            int h = 0;
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 3; j++) {
                    int val = tabuleiro[i][j];
                    if (val != 0) {
                        int ti = (val - 1) / 3;
                        int tj = (val - 1) % 3;
                        h += Math.abs(i - ti) + Math.abs(j - tj);
                    }
                }
            return h;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Estado && Arrays.deepEquals(tabuleiro, ((Estado) o).tabuleiro);
        }

        @Override
        public int hashCode() {
            return Arrays.deepHashCode(tabuleiro);
        }
    }

    public static void bfs(Estado inicial) {
        long inicio = System.currentTimeMillis();
        int nosVisitados = 0;
        Queue<Estado> fila = new LinkedList<>();
        Set<Estado> visitados = new HashSet<>();
        fila.add(inicial);
        visitados.add(inicial);

        while (!fila.isEmpty()) {
            Estado atual = fila.poll();
            nosVisitados++;
            // System.out.println("BFS Expandindo: " + atual.caminho + " (Profundidade: " + atual.profundidade + ")");
            if (atual.objetivo()) {
                System.out.println(
                        "BFS: Solução encontrada - " + atual.caminho + " (Profundidade: " + atual.profundidade + ")");
                long fim = System.currentTimeMillis();
                System.out.println("Tempo: " + (fim - inicio) + " ms");
                System.out.println("Nós visitados: " + nosVisitados);
                return;
            }
            for (Estado filho : atual.expandir()) {
                if (!visitados.contains(filho)) {
                    visitados.add(filho);
                    fila.add(filho);
                }
            }
        }
    }

    public static void dfs(Estado inicial) {
        long inicio = System.currentTimeMillis();
        int nosVisitados = 0;
        final int LIMITE = 20;
        Stack<Estado> pilha = new Stack<>();
        Set<Estado> visitados = new HashSet<>();
        pilha.push(inicial);
        visitados.add(inicial);

        while (!pilha.isEmpty()) {
            Estado atual = pilha.pop();
            nosVisitados++;
            // System.out.println("DFS Expandindo: " + atual.caminho + " (Profundidade: " + atual.profundidade + ")");
            if (atual.objetivo()) {
                System.out.println(
                        "DFS: Solução encontrada - " + atual.caminho + " (Profundidade: " + atual.profundidade + ")");
                long fim = System.currentTimeMillis();
                System.out.println("Tempo: " + (fim - inicio) + " ms");
                System.out.println("Nós visitados: " + nosVisitados);
                return;
            }
            for (Estado filho : atual.expandir()) {
                if (!visitados.contains(filho) && filho.profundidade <= LIMITE) {
                    visitados.add(filho);
                    pilha.push(filho);
                }
            }
        }
    }

    public static void astar(Estado inicial, int heuristica) {
        long inicio = System.currentTimeMillis();
        int nosVisitados = 0;
        PriorityQueue<Estado> fila = new PriorityQueue<>(
                Comparator.comparingInt(e -> e.custo + (heuristica == 1 ? e.heuristica1() : e.heuristica2())));
        Set<Estado> visitados = new HashSet<>();
        fila.add(inicial);

        while (!fila.isEmpty()) {
            Estado atual = fila.poll();
            nosVisitados++;
            // System.out.println("A* H" + heuristica + " Expandindo: " + atual.caminho + " (Profundidade: " + atual.profundidade + ")");
            if (atual.objetivo()) {
                System.out.println("A*: Solução encontrada com heurística " + heuristica + " - " + atual.caminho
                        + " (Profundidade: " + atual.profundidade + ")");
                long fim = System.currentTimeMillis();
                System.out.println("Tempo: " + (fim - inicio) + " ms");
                System.out.println("Nós visitados: " + nosVisitados);
                return;
            }
            visitados.add(atual);
            for (Estado filho : atual.expandir()) {
                if (!visitados.contains(filho))
                    fila.add(filho);
            }
        }
    }

    public static void printTabuleiro(int[][] tab) {
        System.out.println("Tabuleiro final:");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                System.out.print((tab[i][j] == 0 ? " " : tab[i][j]) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        
        int[][] inicial = {
            {1, 2, 3},
            {4, 0, 6},
            {7, 5, 8}
        };
        Estado e = new Estado(inicial, "", 0, 0);
        bfs(e);
        dfs(e);
        astar(e, 1);
        astar(e, 2);
    }
}