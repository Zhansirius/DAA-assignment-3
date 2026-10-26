import java.util.Arrays;

/**
 * Mission 3. Escape: DFS from cell 0 to the last cell (rows * cols - 1) in the maze of Mission 1.
 * Two neighbouring cells are connected when the wall between them is broken.
 * The neighbours are tried in this order: up, right, down, left (cell - cols, cell + 1, cell + cols, cell - 1).
 * The ready method Graphs.open(w, broken) gives you the open neighbours of every cell, in no particular order:
 * to visit them in the required order, go through the four cells above one by one and take those that are in the list.
 * The program must not overflow the stack on a maze of 1000 by 1000 cells.
 */
public class Escape {

    /**
     * path: the cells of the way out, from 0 to the last cell.
     * visited: how many cells are marked as visited at the moment the exit is marked (the start and the exit are counted).
     * deadEnds: how many cells of the maze have exactly one open neighbour.
     */
    public record Result(int[] path, int visited, int deadEnds) {}
    
    public static Result escape(Input.Walls w, int[] broken) {
        int[][] g = Graphs.open(w, broken);
        int n = w.cells();
        int target = n - 1;

        int deadEnds = 0;
        for (int i = 0; i < n; i++) {
            if (g[i].length == 1) {
                deadEnds++;
            }
        }

        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        boolean[] visited = new boolean[n];

        int[] stack = new int[n];
        int[] edgePtr = new int[n];
        int top = 0;

        stack[top] = 0;
        visited[0] = true;
        int visitedCount = 1;
        boolean found = false;

        while (top >= 0) {
            int u = stack[top];
            if (u == target) {
                found = true;
                break;
            }

            boolean advanced = false;
            while (edgePtr[u] < 4) {
                int direction = edgePtr[u]++;
                int v = -1;
                if (direction == 0) v = u - w.cols();
                else if (direction == 1) v = u + 1;
                else if (direction == 2) v = u + w.cols();
                else if (direction == 3) v = u - 1;

                boolean isValidOpen = false;
                for (int neighbor : g[u]) {
                    if (neighbor == v) {
                        isValidOpen = true;
                        break;
                    }
                }

                if (isValidOpen && !visited[v]) {
                    visited[v] = true;
                    visitedCount++;
                    parent[v] = u;
                    stack[++top] = v;
                    advanced = true;
                    break;
                }
            }

            if (!advanced) {
                top--;
            }
        }

        int[] path = new int[0];
        if (found) {
            int curr = target;
            int len = 0;
            while (curr != -1) {
                len++;
                curr = parent[curr];
            }
            path = new int[len];
            curr = target;
            for (int i = len - 1; i >= 0; i--) {
                path[i] = curr;
                curr = parent[curr];
            }
        }

        return new Result(path, visitedCount, deadEnds);
    }
}
