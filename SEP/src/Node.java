import java.util.ArrayList;

public class Node {
    // type can be: Source, divergent, convergent, regular, Destination
    private String type;
    // status can be: available, faulted, inUse
    private String status;
    // Record the last id number, so that can give the new node correct ID;

    // ===================================================
    // author:Mia
    // time: 2023/9/28
    // Store all the neighbors of the node.
    private ArrayList<ArrayList<String>> neighbor;

    private String nodeName; // store the name of the node
    private int cost; // store the cost of the node

    // constructors
    public Node(String node_name) {
        this.nodeName = node_name;
        this.neighbor = new ArrayList<>();
        // this.status = "IDLE"; // at initial stage, node is available

    }

    public Node(String node_name, int cost, String type, String status) { // mia add "status" on 2023/10/18
        this.nodeName = node_name;
        this.cost = cost;
        this.type = type;
        this.neighbor = new ArrayList<>();
        this.status = status; // at initial stage, node is available
    }

    // call this method to add a pair of new neighbours
    public void addNeighbors(String neighbour_from, String neighbour_to) {
        ArrayList<String> newNeighbours = new ArrayList<>();
        newNeighbours.add(neighbour_from);
        newNeighbours.add(neighbour_to);
        this.neighbor.add(newNeighbours);
    }

    // call this method to remove a neighbour
    public void removeNeighbor(String neighbour) {
        for (ArrayList<String> innerList : this.neighbor) {
            for (String n : innerList) {
                if (n.equals(neighbour)) {
                    n = ""; // this neighbour is not my neighbour anymore
                }
            }
        }
    }
    // ===================================================

    // public Node(int lastId) {
    // this.id = lastId + 1;
    // this.neighbor = new ArrayList<>();
    // this.status = "available";
    // this.type = "regular";
    // lastId = this.id; // Update lastId for next time initialization.
    // }

    // Delete the particular node from the neighbor according to the id.
    // public void deleteNeighbor(int id) {
    // for (int i = 0; i < this.neighbor.size(); i++) {
    // if (this.neighbor.get(i).getId() == id) {
    // this.neighbor.remove(i);
    // }
    // }
    // }

    // Print information of all the neighbor nodes.
    // public void showNeighbor() {
    // System.out.println("The neighbors of node " + this.getId() + " are: ");
    // for (int i = 0; i < this.neighbor.size(); i++) {
    // this.neighbor.get(i).printNode();
    // }
    // }

    // call this method to print out the device info
    public void printNode() {
        // System.out.println("Node id: " + this.getId() + "; ");
        System.out.println("Node name: " + this.nodeName + "; ");
        System.out.println("Type: " + this.getType() + "; ");
        System.out.println("Status: " + this.getStatus() + "; ");
        // System.out.println("Neighbor number: " + this.neighbor.size() + "; ");
        System.out.println("");
    }

    // public void setId(int id) {
    // this.id = id;
    // }

    public void setStatus(String status) {
        // available, faulted, inUse
        if (this.status.equals("IDLE") || this.status.equals("FAULTED") || this.status.equals("IN USE")) {
            this.status = status;
        } else {
            System.out.println("Error! Valid status can be available or faulted or inUse!");
        }
    }

    public String getStatus() {
        return status;
    }

    public void setType(String type) {
        // source, divergent, convergent, regular, destination
        if (type.equals("Source") || type.equals("Divergent") || type.equals("Convergent")
                || type.equals("Regular") || type.equals("Destination")) {
            this.type = type;
            return;
        }
        System.out.println("Error, wrong type!");
    }

    public String getType() {
        return type;
    }

    // ===================================================
    // author:Mia
    // time: 2023/9/28
    public String getNodeName() {
        return this.nodeName;
    }

    public int getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public ArrayList<ArrayList<String>> getNeighbor() {
        return this.neighbor;
    }
    // ===================================================

}