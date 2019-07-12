package Dijkstra;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import org.junit.Test;

import static org.junit.Assert.*;
import static java.lang.Double.POSITIVE_INFINITY;

public class DijkstraTest {
    mxGraph graph = new mxGraph();
    Object parent = graph.getDefaultParent();
    Object v1 = graph.insertVertex(parent, "a", "a", 20, 20, 80, 30);
    Object v2 = graph.insertVertex(parent, "b", "b", 240, 150, 80, 30);
    Object v3 = graph.insertVertex(parent, "c", "c", 210, 120, 80, 30);
    Object v4 = graph.insertVertex(parent, "d", "d", 200, 40, 80, 30);
    Object e1 = graph.insertEdge(parent, null, 2.0, v1, v2);
    Object e2 = graph.insertEdge(parent, null, 27.0, v1, v3);
    Object e3 = graph.insertEdge(parent, null, 3.0, v1, v4);
    Object e4 = graph.insertEdge(parent, null, 7.0, v2, v4);
    Object e5 = graph.insertEdge(parent, null, 2.0, v4, v3);
    Dijkstra dijkstra = new Dijkstra(graph, v1);


    @Test
    public void getStep() {
        Dijkstra.Steps tmp = Dijkstra.Steps.RELAXATION;
        dijkstra.setStep(tmp);
        assertEquals(Dijkstra.Steps.RELAXATION, dijkstra.getStep());
    }


    @Test
    public void getDistance() {
        dijkstra.relax(e3);
        assertEquals(3.0, dijkstra.getDistance(v4), 0.0001);
    }

    @Test
    public void isNextStep() {
        dijkstra.removeVertex(v1, e3);
        dijkstra.removeVertex(v2, e1);
        dijkstra.removeVertex(v3, e3);
        assertEquals(false, dijkstra.isNextStep());
    }


    @Test
    public void selectUnvisitedVertex() {
        dijkstra.removeVertex(v2, e1);

        assertEquals("a", ((mxCell)dijkstra.selectUnvisitedVertex()).getValue());
    }

    @Test
    public void selectNearestNeighbor() {
        dijkstra.relax(e1);
        dijkstra.relax(e2);
        dijkstra.relax(e3);
        assertEquals(2.0, ((mxCell)dijkstra.selectNearestNeighbor(v1)).getValue());
    }

    @Test
    public void relax() {
        assertNotEquals(POSITIVE_INFINITY, dijkstra.relax(e3), 0.0001);
    }

    @Test
    public void pathRestoration() {
        dijkstra.relax(e2);
        assertEquals("a c \n", dijkstra.pathRestoration(v3));

    }

    @Test
    public void toString1() {
        String expected = "вершина = d, расстояние = 9.0\n" +
                "вершина = b, расстояние = 2.0\n" +
                "вершина = c, расстояние = 11.0\n" +
                "\n" +
                "пути:\n" +
                "a b d \n" +
                "a b \n" +
                "a b d c \n";
        dijkstra.relax(e1);
        dijkstra.relax(e2);
        dijkstra.relax(e4);
        dijkstra.relax(e5);
        assertEquals(expected, dijkstra.toString());
    }
}