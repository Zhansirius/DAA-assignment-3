import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Missions 1 and 2. Building the maze out of the walls.
 * A wall k joins the cells w.a()[k] and w.b()[k] and costs w.cost()[k]. Indexes start from 0,
 * the line number in walls.txt is the index + 1. The cells are connected by DisjointSet.
 */
public class Maze {

    /**
     * Mission 1. Take the walls in the order of the file. Break a wall when its two cells are in different sets.
     * Returns the indexes of the broken walls, in the order of breaking.
     */
    public static int[] breakWalls(Input.Walls w) {
        DisjointSet dsu = new DisjointSet(w.cells());
        int[] temp = new int[w.count()];
        int idx = 0;

        for (int i = 0; i < w.count(); i++) {
            if (dsu.union(w.a()[i], w.b()[i])) {
                temp[idx++] = i;
            }
        }
        return Arrays.copyOf(temp, idx);
    }

    /**
     * Mission 2. Kruskal: the same, but take the walls from the cheapest to the most expensive.
     * Returns the indexes of the broken walls.
     */
    public static int[] cheapestMaze(Input.Walls w) {
        Integer[] indices = new Integer[w.count()];
        for (int i = 0; i < w.count(); i++) {
            indices[i] = i;
        }
        Arrays.sort(indices, Comparator.comparingInt(i -> w.cost()[i]));

        DisjointSet dsu = new DisjointSet(w.cells());
        int[] temp = new int[w.count()];
        int idx = 0;

        for (int i : indices) {
            if (dsu.union(w.a()[i], w.b()[i])) {
                temp[idx++] = i;
            }
        }
        return Arrays.copyOf(temp, idx);
    }

    /**
     * Mission 2. Prim with a priority queue. Returns the total cost of the cheapest maze.
     * It must be equal to the total cost of cheapestMaze.
     */
    public static long primCost(Input.Walls w) {
        int n = w.cells();
        int[][] head = new int[n][];
        int[][] edgeIdx = new int[n][];
        int[] degree = new int[n];

        for (int i = 0; i < w.count(); i++) {
            degree[w.a()[i]]++;
            degree[w.b()[i]]++;
        }

        for (int i = 0; i < n; i++) {
            head[i] = new int[degree[i]];
            edgeIdx[i] = new int[degree[i]];
        }

        int[] ptr = new int[n];
        for (int i = 0; i < w.count(); i++) {
            int a = w.a()[i];
            int b = w.b()[i];
            head[a][ptr[a]] = b;
            edgeIdx[a][ptr[a]++] = i;
            head[b][ptr[b]] = a;
            edgeIdx[b][ptr[b]++] = i;
        }

        boolean[] inTree = new boolean[n];
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));

        long totalCost = 0;
        inTree[0] = true;

        for (int i = 0; i < ptr[0]; i++) {
            pq.add(new int[]{head[0][i], w.cost()[edgeIdx[0][i]]});
        }

        int edgesUsed = 0;
        while (!pq.isEmpty() && edgesUsed < n - 1) {
            int[] current = pq.poll();
            int v = current[0];
            int cost = current[1];

            if (!inTree[v]) {
                inTree[v] = true;
                totalCost += cost;
                edgesUsed++;

                for (int i = 0; i < ptr[v]; i++) {
                    int next = head[v][i];
                    if (!inTree[next]) {
                        pq.add(new int[]{next, w.cost()[edgeIdx[v][i]]});
                    }
                }
            }
        }

        return totalCost;
    }
}
