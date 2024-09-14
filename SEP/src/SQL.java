import java.sql.*;
import java.util.*;

/**
 * @author Lucas Su
 * @create 2023-09-04-21:41
 */
public class SQL {
    // 根据自己的数据库修改:
    String className = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/test1";
    String user = "root";
    String password = "abc123";

    public Graph connection() throws Exception {

        // initialize driver
        Class.forName(className);
        Connection connection = DriverManager.getConnection(url, user, password);

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        System.out.println("Graph Initializing...");
        String sql_search = "SELECT * FROM device_list";
        preparedStatement = connection.prepareStatement(sql_search);
        resultSet = preparedStatement.executeQuery();

        // round 1, find FAULTED.
        Set<String> faultedPlant = new HashSet<>();
        while (resultSet.next()) {
            String plant_status = resultSet.getString("status");
            if (plant_status.equals("FAULTED")) {
                String plant_item = resultSet.getString("device_name");
                faultedPlant.add(plant_item);
            }
        }

        Graph graph = new Graph();
        resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String nodeName = resultSet.getString(2);
            String isSource = resultSet.getString(3);
            String isDestination = resultSet.getString(4);
            String connectFrom = resultSet.getString(5);
            String connectTo = resultSet.getString(6);
            String status = resultSet.getString(7); // mia added on 2023/10/18
            String cost = resultSet.getString(8);
            String device_cost = resultSet.getString(9);

            int cost_I = Integer.parseInt(cost);
            int device_cost_I = Integer.parseInt(device_cost);
            this.createNode(nodeName, isSource, isDestination, connectFrom, connectTo, status);// mia added on
            // 2023/10/18

            if (faultedPlant.contains(nodeName) || faultedPlant.contains(connectFrom)
                    || faultedPlant.contains(connectTo))
                continue;
            if (nodeName.equals("NULL") || (connectTo != null && connectTo.equals("NULL"))) continue;

            graph.addEdge(nodeName, connectTo, cost_I, device_cost_I);
        }

        // close resources
        resultSet.close();
        preparedStatement.close();
        connection.close();
        return graph;
    }


    // ===================== add a new method =============================
    // author: mia
    // time:2023/9/28
    public void createNode(String nodeName, String isSource, String isDest, String connectFrom, String connectTo,
                           String status) {
        if (!Menu.all_nodes.containsKey(nodeName)) { // if this node doesn't exist, create a new Node object
            String deviceType = checkDeviceType(isSource, isDest);
            Node newNode = new Node(nodeName, 1, deviceType, status); // create new node
            Menu.all_nodes.put(nodeName, newNode); // add it to the all_nodes hashmap
            newNode.addNeighbors(connectFrom, connectTo); // add new neighbours

            if (Menu.all_nodes.containsKey(connectFrom)) {
                Menu.all_nodes.get(connectFrom).addNeighbors("", nodeName);
            }

            if (Menu.all_nodes.containsKey(connectTo)) {
                Menu.all_nodes.get(connectTo).addNeighbors(nodeName, null);
            }
        }
        // if this node already exists, only add new neighbours to it
        Menu.all_nodes.get(nodeName).addNeighbors(connectFrom, connectTo); // add new neighbours
    }

    // call this method to first check the type of this device
    // can be Source, divergent, convergent, regular,Destination
    public String checkDeviceType(String isSource, String isDest) {
        String type = "Regular";
        if (isSource.equals("YES")) {
            type = "Source";
        }
        if (isDest.equals("YES")) {
            type = "Destination";
        }

        return type;
    }

    // call this method to remove/delete a specific device
    public boolean removeDevice(String deviceName) {
        // initialize driver
        boolean hasRemoved = false;
        try {
            Class.forName(className);
            Connection connection = DriverManager.getConnection(url, user, password);

            PreparedStatement preparedStatement = null;

            ResultSet resultSet = null;

            String sqlSearch = "DELETE FROM device_list WHERE device_name = '" + deviceName + "'"
                    + " OR connect_from = '" + deviceName + "'"
                    + " OR connect_to = '" + deviceName + "'";

            preparedStatement = connection.prepareStatement(sqlSearch);
            preparedStatement.executeUpdate();
            hasRemoved = true;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Error, please try again!");
        }
        return hasRemoved;
    }
    // ===========================================================================

    // ===========================================================================
    // "cost" is the cost of path from current device to connect_to device
    public boolean addDevice(String DeviceName, String IsSource, String IsDestination, String To,
                             String cost, String NodeCost)
            throws Exception {
        boolean hasAdded = false;
        String From = "";
        try {
            Class.forName(className);
            Connection connection = DriverManager.getConnection(url, user, password);

            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            // mia updated on 2023/10/18 (add "cost " and "device_cost")
            int pathCost = Integer.parseInt(cost);
            int deviceCost = Integer.parseInt(NodeCost);
            String sql_search = "INSERT INTO device_list (`device_name`, `is_source`, `is_destination`, `connect_from`, `connect_to`, `status`,`cost`,`device_cost`) VALUES ('"
                    + DeviceName + "', '" + IsSource + "', '" + IsDestination + "','" + From + "','"
                    + To + "', 'IDLE','" + pathCost + "', '" + deviceCost + "');\n";

            preparedStatement = connection.prepareStatement(sql_search);
            preparedStatement.executeUpdate();
            hasAdded = true;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Error, please try again!");
        }
        return hasAdded;
    }

    // call this method to remove/delete a specific device
    public void updateDevice(String update_device, String newName, String status,
                             ArrayList<ArrayList<String>> neighbor, int flag, String new_device_cost)
            throws ClassNotFoundException, SQLException {
        Class.forName(className);
        Connection connection = DriverManager.getConnection(url, user, password);

        ResultSet resultSet = null;
        if (new_device_cost.isEmpty()) {
            String sqlUpdate = "UPDATE device_list SET device_name = ?, status = ? WHERE device_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {
                preparedStatement.setString(1, newName);
                preparedStatement.setString(2, status);
                preparedStatement.setString(3, update_device);
                preparedStatement.executeUpdate();
            }
        } else {
            String sqlUpdate = "UPDATE device_list SET device_name = ?, status = ?, device_cost = ? WHERE device_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {
                preparedStatement.setString(1, newName);
                preparedStatement.setString(2, status);
                preparedStatement.setString(3, new_device_cost);
                preparedStatement.setString(4, update_device);
                preparedStatement.executeUpdate();
            }
        }

        // 清除该设备之前的连接关系
        if (flag == 1) {
            String sqlDelete = "UPDATE device_list SET connect_from = NULL, connect_to = NULL WHERE device_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlDelete)) {
                preparedStatement.setString(1, update_device);
                preparedStatement.executeUpdate();
            }

            // 插入新的连接关系
            String sqlInsertRelation = "UPDATE device_list SET connect_from = ?, connect_to = ? WHERE device_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlInsertRelation)) {
                for (ArrayList<String> pair : neighbor) {
                    String from = pair.get(0);
                    String to = pair.get(1);
                    preparedStatement.setString(1, from);
                    preparedStatement.setString(2, to);
                    preparedStatement.setString(3, newName);
                    preparedStatement.executeUpdate();
                }
            }
        }
    }

    public void modDevice(String from, String to, int cost) throws ClassNotFoundException, SQLException {
        Class.forName(className);
        Connection connection = DriverManager.getConnection(url, user, password);

        String updateSQL = "UPDATE device_list SET cost = ? WHERE device_name = ? AND connect_to = ?";

        // use PreparedStatement to set parameters and execute update
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setInt(1, cost); // set cost parameter
            preparedStatement.setString(2, from); // set connect_from parameter
            preparedStatement.setString(3, to); // set connect_to parameter

            // execute update
            int updatedRows = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // close the connection
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        }
    }

    public void updateName(String update_device, String newName) throws ClassNotFoundException, SQLException {
        Class.forName(className);
        Connection connection = DriverManager.getConnection(url, user, password);

        String sqlUpdate = "UPDATE device_list SET device_name = ? WHERE device_name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {
            preparedStatement.setString(1,newName);
            preparedStatement.setString(2, update_device);
            preparedStatement.executeUpdate();
        }

        String sqlUpdate1 = "UPDATE device_list SET connect_to = ? WHERE connect_to = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate1)) {
            preparedStatement.setString(1,newName);
            preparedStatement.setString(2, update_device);
            preparedStatement.executeUpdate();
        }
        connection.close();
    }

    public void updateStatusManual(String update_device, String new_status) throws ClassNotFoundException, SQLException {
        Class.forName(className);
        Connection connection = DriverManager.getConnection(url, user, password);

        String sqlUpdate = "UPDATE device_list SET status = ? WHERE device_name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {
            preparedStatement.setString(1, new_status);
            preparedStatement.setString(2, update_device);
            preparedStatement.executeUpdate();
        }
        connection.close();
    }


    public void updateStatusAuto(Set<Map.Entry<List<String>, Integer>> pathsWithCosts) throws ClassNotFoundException, SQLException{
        Class.forName(className);
        Connection connection = DriverManager.getConnection(url, user, password);

        // change all the not faulted device to IDLE
        String sqlUpdate1 = "UPDATE device_list SET status = ? WHERE status<> ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate1)) {
            preparedStatement.setString(1, "IDLE");
            preparedStatement.setString(2, "FAULTED");
            preparedStatement.executeUpdate();
        }

        for (Map.Entry<List<String>, Integer> pathEntry : pathsWithCosts) {
            List<String> path = pathEntry.getKey();
            for (String device_name : path){
                String sqlUpdate2 = "UPDATE device_list SET status = ? WHERE device_name = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate2)) {
                    preparedStatement.setString(1, "IN_USE");
                    preparedStatement.setString(2, device_name);
                    preparedStatement.executeUpdate();
                }
            }
        }
        connection.close();
    }

    public void updateDeviceCost(String update_device, String new_device_cost) throws ClassNotFoundException, SQLException {
        Class.forName(className);
        Connection connection = DriverManager.getConnection(url, user, password);

        String sqlUpdate = "UPDATE device_list SET device_cost = ? WHERE device_name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {
            preparedStatement.setString(1, new_device_cost);
            preparedStatement.setString(2, update_device);
            preparedStatement.executeUpdate();
        }
        connection.close();
    }

    public List<String> displayToEdge(String update_device) throws ClassNotFoundException, SQLException {
        Class.forName(className);
        Connection connection = DriverManager.getConnection(url, user, password);

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String sql_search = "SELECT connect_to FROM device_list WHERE device_name = '" + update_device + "'";

        preparedStatement = connection.prepareStatement(sql_search);
        resultSet = preparedStatement.executeQuery();

        Set<String> deviceSet_To = new HashSet<>();

        int id = 1;
        while (resultSet.next()) {
            String connect_to = resultSet.getString("connect_to");
            if (connect_to == null) continue;
            deviceSet_To.add(connect_to);
        }
        List<String> deviceList_To = new ArrayList<>(deviceSet_To);
        for (int i = 0; i < deviceList_To.size(); i++) {
            System.out.println(id + ". " + deviceList_To.get(i));
            id++;
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return deviceList_To;
    }

    public void updateDeviceTo(String update_device, String selected_dest_device, String new_dest, String new_cost) throws ClassNotFoundException, SQLException {
        Class.forName(className);
        Connection connection = DriverManager.getConnection(url, user, password);

        String sqlUpdate = "UPDATE device_list SET connect_to = ?, cost = ? WHERE device_name = ? AND connect_to = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdate)) {
            preparedStatement.setString(1, new_dest);
            preparedStatement.setString(2, new_cost);
            preparedStatement.setString(3, update_device);
            preparedStatement.setString(4, selected_dest_device);
            preparedStatement.executeUpdate();
        }
        connection.close();
    }
}
