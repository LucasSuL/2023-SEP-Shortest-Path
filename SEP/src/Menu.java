import java.util.*;

public class Menu {
    public static Graph graph;

    // private static boolean DirtyBit = false;
    // private static int lastId = 0;

    // =================================================
    // author:mia
    // time: 2023/9/28
    // public static ArrayList<Node> nodeList = new ArrayList<>();
    public static LinkedHashMap<String, Node> all_nodes; // key:node name ,value:Node
    // =================================================

    public static void main(String[] args) throws Exception {

        Scanner inputScanner = new Scanner(System.in);
        int[] options = new int[]{0, 1, 2, 3, 4, 5, 6};
        boolean run = true;
        while (run) {

            all_nodes = new LinkedHashMap<>();

            displayMainMenu();
            int operation = getOperation(inputScanner, options, false);
            while (operation == -1) {
                operation = getOperation(inputScanner, options, true);
            }
            run = execute(operation, inputScanner);
        }
    }

    public static void displayMainMenu() {
        String menu = getTitle();
        menu += "::                                MAIN MENU                                  ::" + "\n";
        menu += "-------------------------------------------------------------------------------" + "\n";
        menu += ":: Please choose your option :                                      ::" + "\n";
        menu += ":: 1. Add Device                                                             ::" + "\n";
        menu += ":: 2. Delete Device                                                          ::" + "\n";
        menu += ":: 3. Update Device Information / Accessible Device List                     ::" + "\n";
        menu += ":: 4. Update Edge Cost                                                       ::" + "\n";
        menu += ":: 5. Show the shortest path                                                 ::" + "\n";
        menu += ":: 6. Show the information of all nodes                                      ::" + "\n";
        menu += ":: 0. Exit                                                                   ::" + "\n";
        menu += "===============================================================================" + "\n";
        System.out.println(menu);
    }

    public static String getTitle() {
        String title = "\n\n";
        title += "===============================================================================" + "\n";
        title += "::        ATSYS Shortest Path Algorithm for Material Transportation          ::" + "\n";
        title += "===============================================================================" + "\n";
        return title;
    }

    public static int getOperation(Scanner inputScanner, int[] options, boolean wrong) {
        if (wrong) {
            System.out.println(":: Your input is valid, please try again : ");
        } else {
            System.out.println(":: Your option (Please type the valid number) : ");
        }

        String input = inputScanner.nextLine();
        // Check if the input is empty
        if (input.equals("") || input == null) {
            return -1;
        }
        // Check if the input is number
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) < 48 || input.charAt(i) > 57) {
                return -1;
            }
        }
        // Check if the input is valid number
        int inputNumber = Integer.parseInt(input);
        if (inputNumber >= 0 && inputNumber < options.length) {
            return inputNumber;
        }
        return -1;
    }

    public static boolean execute(int option, Scanner inputScanner) throws Exception {
        SQL sql = new SQL();
//        graph = sql.connection();
        switch (option) {
            case 0:
                exit();
                return false;
            case 1:
                clear();
                graph = sql.connection();
                String title1 = "";
                while (true) {
                    title1 += "::                                ADD DEVICE                                 ::" + "\n";
                    title1 += "-------------------------------------------------------------------------------" + "\n";
                    title1 += ":: Please choose type of new device:                                         ::" + "\n";
                    title1 += ":: 1. Regular                                                                ::" + "\n";
                    title1 += ":: 2. Source                                                                 ::" + "\n";
                    title1 += ":: 3. Destination                                                            ::" + "\n";
                    title1 += ":: 4. Convergent                                                             ::" + "\n";
                    title1 += ":: 5. Divergent                                                              ::" + "\n";
                    title1 += "===============================================================================" + "\n";
                    System.out.println(title1);
                    String nodeType = inputScanner.nextLine();
                    while (!checkInputWithBound(nodeType, 1, 5)) {
                        nodeType = inputScanner.nextLine();
                    }
                    String IsSource = "NO";
                    String IsDestination = "NO";
                    switch (nodeType) {
                        case "2" -> {
                            nodeType = "Source";
                            IsSource = "YES";
                        }
                        case "5" -> nodeType = "Divergent";
                        case "4" -> nodeType = "Convergent";
                        case "1" -> nodeType = "Regular";
                        case "3" -> {
                            nodeType = "Destination";
                            IsDestination = "YES";
                        }
                    }
                    // author: mia updated on 2023/10/6 ==================
                    System.out.println("Type name: ");
                    String DeviceName = inputScanner.nextLine();
                    System.out.println("Type device cost: ");
                    String device_cost = inputScanner.nextLine();

                    String cost = "0";
                    String To = "";
                    if (!nodeType.equals("Destination")) {
                        System.out.println("Type To");
                        To = inputScanner.nextLine();
                        System.out.println("Type the cost between them");
                        cost = inputScanner.nextLine();
                    }else {

                    }

                    boolean if_added = sql.addDevice(DeviceName, IsSource, IsDestination,  To, cost, device_cost);
                    if (if_added) {
                        System.out.println("Device " + DeviceName + " has been added!");
                        System.out.println("");
                        break;
                    }
                }
                // =====================================================
                graph = sql.connection();
                pressToContinue(inputScanner);
                break;
            case 2:
                clear();
                graph = sql.connection();

                String title2 = "";
                title2 += "::                              DELETE DEVICE                                ::" + "\n";
                title2 += "-------------------------------------------------------------------------------" + "\n";
                System.out.println(title2);

                // =================================================
                // author: mia
                // time: 2023/9/28
                while (true) {
                    System.out.println(":: Please type the name of the device you wish to delete: ");
                    String deleteDevice = inputScanner.nextLine(); // let the user enter the device name
                    while (!checkInput(deleteDevice)) {
                        deleteDevice = inputScanner.nextLine();
                    }
                    boolean hasRemoved = sql.removeDevice(deleteDevice); // call removeDevice() ro delete the device
                    if (hasRemoved) {
                        System.out.println("Device " + deleteDevice + " has been deleted!");
                        System.out.println("");
                        break;
                    }
                }
                graph = sql.connection();
                pressToContinue(inputScanner);
                break;

            case 3:
                clear();
                graph = sql.connection();

                String title3 = "";
                title3 += "::           Update Device Information / Accessible Device List              ::" + "\n";
                title3 += "-------------------------------------------------------------------------------" + "\n";
                System.out.println(title3);
                // =================================================
                // author: Hao Luo
                // time: 2023/9/28
                System.out.println(":: Please enter the name of the device you want to update: ");
                String update_device = inputScanner.nextLine(); // let the user enter the device name
                while (!checkInput(update_device)) {
                    update_device = inputScanner.nextLine();
                }

                System.out.println("Do you want to modify its name?(Y/N):");
                String answer = inputScanner.nextLine();
                while (!Objects.equals(answer, "Y") && !Objects.equals(answer, "N")) {
                    System.out.println(":: Your input is invalid! Please try again : ");
                    answer = inputScanner.nextLine();
                }
                if (answer.equals("Y")) {
                    System.out.println(":: Please enter the new name: ");
                    String newName = inputScanner.nextLine();
                    sql.updateName(update_device, newName);

                    System.out.println("Device name updated to: " + newName);

                    update_device = newName;
                }
                System.out.println("Do you want to modify its status?(Y/N):");
                String answer2 = inputScanner.nextLine();
                while (!Objects.equals(answer2, "Y") && !Objects.equals(answer2, "N")) {
                    System.out.println(":: Your input is invalid! Please try again: ");
                    answer2 = inputScanner.nextLine();
                }
                if (answer2.equals("Y")) {
                    System.out.println(":: What is the new status?(IDLE/FAULTED):");
                    String new_status = inputScanner.nextLine();
                    while (!Objects.equals(new_status, "IDLE") && !Objects.equals(new_status, "FAULTED")) {
                        System.out.println(":: Your input is invalid! Please try again : ");
                        new_status = inputScanner.nextLine();
                    }
                    sql.updateStatusManual(update_device, new_status);
                    System.out.println("Device status updated to: " + new_status);
                }

                System.out.println("Do you want to update its cost?(Y/N):");
                String answer_1 = inputScanner.nextLine();
                while (!Objects.equals(answer_1, "Y") && !Objects.equals(answer_1, "N")) {
                    System.out.println(":: Your input is invalid! Please try again : ");
                    answer_1 = inputScanner.nextLine();
                }


                if (answer_1.equals("Y")) {
                    System.out.println(":: Please enter new device cost: ");
                    String new_device_cost = inputScanner.nextLine();
                    sql.updateDeviceCost(update_device, new_device_cost);

                    System.out.println("Device cost updated to: " + new_device_cost);
                }

                System.out.println("Do you want to reconnect its reachable neighbors?(Y/N):");
                String answer3 = inputScanner.nextLine();
                while (!Objects.equals(answer3, "Y") && !Objects.equals(answer3, "N")) {
                    System.out.println(":: Your input is invalid! Please try again : ");
                    answer3 = inputScanner.nextLine();
                }

                if (answer3.equals("Y")) {
                    boolean flag = true;
                    while (flag) {
                        System.out.println();
                        System.out.println("Connected To Device: ");
                        List<String> deviceList_To = sql.displayToEdge(update_device);
                        String selected_dest_device = "";
                        if (deviceList_To.size() > 1) {
                            System.out.println("Please select the index of destination: ");
                            String select_edge_index = inputScanner.next();
                            selected_dest_device = deviceList_To.get(Integer.parseInt(select_edge_index) - 1);
                        } else if (deviceList_To.size() == 1) {
                            selected_dest_device = deviceList_To.get(0);
                        } else {
                            System.out.println("No connection for this device.");
                            pressToContinue(inputScanner);
                            break;
                        }

                        System.out.println("Please enter the new destination: ");
                        String new_dest = inputScanner.next();
                        System.out.println("Please enter the cost to "+ new_dest);
                        String new_cost = inputScanner.next();

                        sql.updateDeviceTo(update_device, selected_dest_device, new_dest, new_cost);
                        System.out.println("Device Connect_To updated to: " + new_dest);

                        System.out.println("Do you want to continue updating device destination? (Y/N)");
                        String user_choice_continue = inputScanner.next();
                        while (!Objects.equals(user_choice_continue, "Y") && !Objects.equals(user_choice_continue, "N")) {
                            System.out.println(":: Your input is invalid! Please try again : ");
                            user_choice_continue = inputScanner.nextLine();
                        }
                        if (user_choice_continue.equals("N")) flag = false;
                    }
                }
                graph = sql.connection();
                pressToContinue(inputScanner);

                break;
            case 4:
                clear();
                graph = sql.connection();

                String title4 = "";
                title4 += "::                             UPDATE EDGE COST                              ::" + "\n";
                title4 += "-------------------------------------------------------------------------------" + "\n";
                System.out.println(title4);

                System.out.println(":: Please type the start device: ");
                String m_Start = inputScanner.nextLine(); // let the user enter the device name
                while (!checkInput(m_Start)) {
                    m_Start = inputScanner.nextLine();
                }

                System.out.println(":: Please type the end device: ");
                String m_End = inputScanner.nextLine(); // let the user enter the device name
                while (!checkInput(m_End)) {
                    m_End = inputScanner.nextLine();
                }

                System.out.println(":: The new cost of the edge: ");
                int edge_cost = inputScanner.nextInt();

                System.out.println("Mod successful");

                sql.modDevice(m_Start, m_End, edge_cost);
                graph = sql.connection();

                pressToContinue(inputScanner);
                break;

            case 5:
                clear();
                graph = sql.connection();
                SearchAlgo algo;
                Set<Map.Entry<List<String>, Integer>> pathsWithCosts = null;
                List<String> endNodes = new ArrayList<>();
                int k = 5;

                String title5 = "";
                title5 += "::                          SHOW THE SHORTEST PATH                           ::" + "\n";
                title5 += "-------------------------------------------------------------------------------" + "\n";
                System.out.println(title5);

                System.out.println("Single destination (S) or Multiple (M)?");
                String mode = inputScanner.nextLine();

//                String mode = "S"; //test1009

                if (mode.equals("S")) {
                    System.out.println(":: Please enter start device: ");
                    String start = inputScanner.nextLine();
                    while (!checkInput(start))
                        start = inputScanner.nextLine();

                    System.out.println(":: Please enter target device: ");
                    String target = inputScanner.nextLine();
                    while (!checkInput(target))
                        target = inputScanner.nextLine();

//                    String start = "SOURCE_8";
//                    String target = "DEST_LOAD";
                    endNodes.add(target);

                    algo = new SearchAlgo();
                    pathsWithCosts = algo.kShortestPaths(graph.graph, start, target, k);

                    printPaths(pathsWithCosts, endNodes, graph.graph, mode);

                } else if (mode.equals("M")) {

                    System.out.println(":: Please enter start device: ");
                    String start = inputScanner.nextLine();
                    while (!checkInput(start))
                        start = inputScanner.nextLine();


                    System.out.println("Please enter target devices (separated by spaces): ");
                    while (true) {
                        String user_raw_input = inputScanner.nextLine();
                        String[] target_devices = user_raw_input.split(" ");

                        boolean allValid = true;

                        for (int i = 0; i < target_devices.length; i++) {
                            String target = target_devices[i];
                            if (!checkInput(target)) {
                                allValid = false;
                                break;
                            }
                            endNodes.add(target);
                        }

                        if (allValid) {
                            break; // 如果所有目标都有效，退出循环
                        } else {
                            endNodes.clear(); // 清空无效的目标
                        }
                    }

//                    // FOR TEST
//                    String start = "SOURCE_8";
//                    endNodes.add("DEST_LOAD");
//                    endNodes.add("DEST_CLEAN");

                    algo = new SearchAlgo();
                    pathsWithCosts = algo.kShortestPaths2(graph.graph, start, endNodes, k);
                    printPaths(pathsWithCosts, endNodes, graph.graph, mode);

                } else {
                    System.out.println("ERROR!");
                }

                //Call SQL function to change status to IN-USE
                if (pathsWithCosts != null)
                    sql.updateStatusAuto(pathsWithCosts);
                pressToContinue(inputScanner);
                break;
            case 6:
                clear();
                graph = sql.connection();
                String title6 = getTitle();
                title6 += "::                              SHOW DEVICE                                  ::" + "\n";
                title6 += "-------------------------------------------------------------------------------" + "\n";
                System.out.println(title6);

                // Mia updated on 2023/9/30 ===========================================

                Iterator<Map.Entry<String, Node>> iterator = all_nodes.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<String, Node> entry = iterator.next();
                    // String key = entry.getKey();
                    Node value = entry.getValue();
                    value.printNode();
                }
                // ======================================================
                pressToContinue(inputScanner);

                break;
        }
        return true;
    }

    public static boolean checkInput(String input) {
        // Check if the input is empty
        if (input.equals("")) {
            System.out.println(":: Input can not be empty! Please try again : ");
            return false;
        }

        // ==============================================
        // mia updated on 2023/9/28
        if (!all_nodes.containsKey(input)) {
            System.out.println(":: Invalid input. Please provide valid target devices: ");
            return false;
        }
        // ==============================================
        // // Check if the input is number
        // for (int i = 0; i < input.length(); i++) {
        // if (input.charAt(i) < 48 || input.charAt(i) > 57) {
        // System.out.println(":: Your input has to be number! Please try again : ");
        // return false;
        // }
        // }
        return true;
    }

    public static boolean checkInputWithBound(String input, int lowBound, int upBound) {
        // Check if the input is empty
        if (input == "" || input == null) {
            System.out.println(":: Input can not be empty! Please try again : ");
            return false;
        }
        // Check if the input is number
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) < 48 || input.charAt(i) > 57) {
                System.out.println(":: Your input has to be number!  Please try again : ");
                return false;
            }
        }
        // Check bound
        int num = Integer.parseInt(input);
        if (num < lowBound || num > upBound) {
            System.out.println(":: Your input is invalid!  Please try again : ");
            return false;
        }
        return true;
    }

    public static void exit() {
        String exitMessage = getTitle();
        exitMessage += "::                            EXIT MESSAGE                                   ::" + "\n";
        exitMessage += "-------------------------------------------------------------------------------" + "\n";
        exitMessage += "::                                                                           ::" + "\n";
        exitMessage += "::                                                                           ::" + "\n";
        exitMessage += "::                             Thank you!                                    ::" + "\n";
        exitMessage += "::                                                                           ::" + "\n";
        exitMessage += "::                                                                           ::" + "\n";
        exitMessage += "===============================================================================" + "\n";
    }

    public static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void pressToContinue(Scanner inputScanner) {
        System.out.println(":: Press any key to continue ... ");
        inputScanner.nextLine();
    }

    private static void printPaths(Set<Map.Entry<List<String>, Integer>> paths, List<String> endNodes,
                                   Map<String, List<Graph.Edge>> graph, String mode) {
        System.out.println("-------------------------------------------------------------------------------");
        if (paths == null || paths.size() == 0) {
            System.out.println("No shortest paths available.");
        }
        // Convert the set to a list for sorting by cost.
        List<Map.Entry<List<String>, Integer>> sortedPaths = new ArrayList<>(paths);
        sortedPaths.sort(Comparator.comparingInt(Map.Entry::getValue));

        int index = 1;
        for (Map.Entry<List<String>, Integer> pathEntry : sortedPaths) {
            List<String> path = pathEntry.getKey();


            int cost = pathEntry.getValue();

            // Print the cost.
            System.out.println("\033[1mPath: #\033[0m" + index++);

            System.out.println("\t\033[1mTotal Cost: \033[0m" + cost);

            // Print the path with subpath costs.
            int subpathStartIndex = 0;
            for (int i = 0; i < path.size(); i++) {
                String currentNode = path.get(i);
                if (endNodes.contains(currentNode)) {
                    // Calculate and print the subpath cost.
                    List<String> subpath = path.subList(subpathStartIndex, i + 1);
                    int subpathCost = calculateSubPathCost(subpath, graph);
                    if (mode.equals("M"))
                        System.out.println("\t\t\033[1mSubpath Cost: \033[0m" + subpathCost);

                    // Print the subpath.
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < subpath.size(); j++) {
                        if (j == subpath.size() - 1)
                            sb.append("\033[1m\033[3m").append(subpath.get(j)).append("\033[0m");
                        else
                            sb.append("\033[1m\033[3m").append(subpath.get(j)).append("\033[0m").append(" -> ");
                    }
                    System.out.println("\t\t" + sb);


                    System.out.println(); // Start a new line for end nodes.
                    subpathStartIndex = i + 1; // Update the subpath start index.
                }
            }
//            System.out.println();
        }
        System.out.println("-------------------------------------------------------------------------------");
    }

    // Calculate the cost of a subpath, excluding the start and end nodes.
    private static int calculateSubPathCost(List<String> subpath, Map<String, List<Graph.Edge>> graph) {
        int cost = 0;

        for (int i = 0; i < subpath.size() - 1; i++) {
            String currentNode = subpath.get(i);
            String nextNode = subpath.get(i + 1);

            // Calculate node cost (plantCost) if it has not been visited yet.
            if (i > 0 && i < subpath.size() - 1) {
                cost += getNodeCost(currentNode, graph);
            }

            // Calculate edge cost (weight) if it has not been visited yet.
            cost += getEdgeCost(currentNode, nextNode, graph);
        }
        return cost;
    }

    // Get the cost of a node (plantCost).
    private static int getNodeCost(String node, Map<String, List<Graph.Edge>> graph) {
        for (Graph.Edge edge : graph.get(node)) {
            // System.out.println("node:" + node + " edge.plantCost:" + edge.plantCost);
            return edge.plantCost; // Assuming fromNodeCost is the node cost.
        }
        return 0; // Default to 0 if node is not found.
    }

    // Get the cost of an edge (weight).
    private static int getEdgeCost(String fromNode, String toNode, Map<String, List<Graph.Edge>> graph) {
        for (Graph.Edge edge : graph.get(fromNode)) {
            if (edge.to.equals(toNode)) {
                return edge.weight; // Assuming weight is the edge cost.
            }
        }
        return 0; // Default to 0 if edge is not found.
    }
}