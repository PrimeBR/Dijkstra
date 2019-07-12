package Dijkstra;

import com.mxgraph.view.mxGraph;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class EdgeComparatorTest {
    private Object edge1;
    private Object edge2;
    private int expected;

    public EdgeComparatorTest(int expected, Object edge1, Object edge2) {
        this.expected = expected;
        this.edge1 = edge1;
        this.edge2 = edge2;
    }
    @BeforeClass
    public static void beforeClass() {
        System.out.println("Before EdgeComparatorTest");
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        mxGraph graph;
        graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80, 30);
        Object v2 = graph.insertVertex(parent, null, "World!", 240, 150, 80, 30);
        Object v3 = graph.insertVertex(parent, null, "Andrey", 210, 120, 80, 30);
        Object v4 = graph.insertVertex(parent, null, "Anton", 200, 40, 80, 30);
        Object[] vertices = graph.getChildVertices(graph.getDefaultParent());
        graph.insertEdge(parent, null, 5.5, vertices[0], vertices[1]);
        graph.insertEdge(parent, null, 5.5, vertices[0], vertices[2]);
        graph.insertEdge(parent, null, 12.4, vertices[0], vertices[3]);
        Object [] edges = graph.getOutgoingEdges(v1, parent);
        return Arrays.asList(new Object[][] {
                { 0, edges[0], edges[1]},
                { -1, edges[0], edges[2]},
                { 1, edges[2], edges[1]},
        });
    }


    @AfterClass
    public static void afterClass(){
        System.out.println("After EdgeComparatorTest");
    }


    @Test
    public void compare() {
        assertEquals(expected, new EdgeComparator().compare(edge1, edge2));
    }
}

