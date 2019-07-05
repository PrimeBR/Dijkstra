package Dijkstra;

import com.mxgraph.model.mxCell;

import java.util.Comparator;

public class EdgeComparator implements Comparator<Object> {
    @Override
    public int compare(Object o1, Object o2) {
        if ((double)((mxCell) o1).getValue() > (double)((mxCell) o2).getValue())
            return 1;
        else if ((double)((mxCell) o1).getValue() < (double)((mxCell) o2).getValue())
            return -1;
        else
            return 0;
    }
}
