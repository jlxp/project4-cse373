package mazes.generators.maze;

import java.util.Random;

import datastructures.concrete.ArrayDisjointSet;
import datastructures.concrete.ArrayHeap;
import datastructures.concrete.ChainedHashSet;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.ISet;
import mazes.entities.Maze;
import mazes.entities.Room;
import mazes.entities.Wall;
//import misc.exceptions.NotYetImplementedException;

/**
 * Carves out a maze based on Kruskal's algorithm.
 *
 * See the spec for more details.
 */
public class KruskalMazeCarver implements MazeCarver {
    @Override
    public ISet<Wall> returnWallsToRemove(Maze maze) {
        // Note: make sure that the input maze remains unmodified after this method is over.
        //
        // In particular, if you call 'wall.setDistance()' at any point, make sure to
        // call 'wall.resetDistanceToOriginal()' on the same wall before returning.
        ISet<Wall> toRemoved = new ChainedHashSet<>();
        Random rand = new Random();
        IPriorityQueue<Wall> walls = new ArrayHeap<>();
        
        for (Wall wall : maze.getWalls()) {
            wall.setDistance(rand.nextDouble());
            walls.insert(wall);
        }
        
        IDisjointSet<Room> rooms = new ArrayDisjointSet<>();
        
        for (Room room : maze.getRooms()) {
            rooms.makeSet(room);
        }
        int index = 0;
        while (index < maze.getRooms().size() - 1) {
            Wall wall = walls.removeMin();
            Room room1 = wall.getRoom1();
            Room room2 = wall.getRoom2();
            if (room2 != null && rooms.findSet(room1) != rooms.findSet(room2)) {
                rooms.union(room1, room2);
                toRemoved.add(wall);
                index++;
            }
        }
        
        for (Wall wall : maze.getWalls()) {
            wall.resetDistanceToOriginal();
        }
        return toRemoved;
    }
}
