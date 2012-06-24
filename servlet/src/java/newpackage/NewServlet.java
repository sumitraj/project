/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package newpackage;
import java.util.*;
import java.io.*;
import org.xml.sax.InputSource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part; 
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

@WebServlet(name="NewServlet", urlPatterns={"/NewServlet"})
public class NewServlet extends HttpServlet {

        private static final long serialVersionUID = 1L;
        int n;                              /* n is the total number of locations */
        int m;                              /* m is the maximum number of vehicles available */
        int speed;
        int count;                          /* count is the number of iterations executed without any improvement in solution*/
        int countLimit;                     /* count is bounded by countLimit */
        int numVeh;                         /* numVeh is the number of vehicles used in a particular iteration */
        float depot_x ;                     /* x and y coordinates of the depot   */
        float depot_y ;
        float service_time;
        float dist;
        float dist1;
        float[] x;
        float[] y;
        float[] holding_x;
        float[] holding_y;
        float[] deadlines;
        ArrayList<String> holding_list_index;
        ArrayList<String> holding_list_index1;
        ArrayList<ArrayList<String>> sol;

        public  NewServlet() {
            
            /*n = 9;
            m = 4;
            speed = 2;
            service_time = 1;
            countLimit = 1000;
            depot_x = 1;
            depot_y = 1;*/

            
            numVeh = 1;
            count = 0;
            dist = 0;
            dist1 = 0;
            holding_x = x;
            holding_y = y;
            holding_list_index = new ArrayList<String>();

        }

//================================================================================================================

        private static String getTagValue(String sTag, Element eElement) {
	NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

        Node nValue = (Node) nlList.item(0);

	return nValue.getNodeValue();
        }

//================================================================================================================

        /* Returns the Manhattan distance between two locations */
        float distance_between_two_locations( float x0, float y0, float x1, float y1 ) {
            return  Math.abs(x0 - x1) + Math.abs(y0 - y1);
        }

//=============================================================================================================================

        int find_id_of_nearest_location ( float t_x, float t_y, ArrayList<String> al  ) {
            float min = 999999;
            float t;
            int index = 0;
            for ( int i = 0; i<al.size(); i++  ) {
                t = distance_between_two_locations (t_x, t_y, x[Integer.parseInt(al.get(i))] , y[Integer.parseInt(al.get(i))]  );
                if (t < min) {
                    min = t;
                    index = i;
                }
            }
            return  Integer.parseInt(al.get(index));
        }

//======================================================================================================================================

        /* Returns the greedy initial solution */
        ArrayList<String> find_initial_sol(ArrayList<String> initial_sol){
            ArrayList<String> temp = new ArrayList<String>( holding_list_index );
            ArrayList<String> init = initial_sol;
            int t = holding_list_index.size();
            int id;
            float t_x, t_y, curr_x, curr_y, d;
            float time = - service_time;
            t_x = depot_x;
            t_y = depot_y;
            curr_x = depot_x;
            curr_y = depot_y;
            while ( !temp.isEmpty()  ) {
                System.out.println(":::::");
                id = find_id_of_nearest_location(curr_x, curr_y, temp);
                time = time + service_time + distance_between_two_locations (curr_x, curr_y, x[id], y[id] )/speed ;
                if (time <= deadlines[id]) {
                    init.add(Integer.toString(id));

                    curr_x = x[id];
                    curr_y = y[id];
                    holding_list_index.remove(Integer.toString(id));
                }
                temp.remove(Integer.toString(id));
            }
            return init;
        }
//================================================================================================================


        /* Returns the route length of the input parameter  */
        float route_length (ArrayList<String> s) {
            if (s.size() == 0 ) {
                return 0;
            }
            float len = 0;
            len = len + distance_between_two_locations( depot_x, depot_y, x[Integer.parseInt(s.get(0))], y[Integer.parseInt(s.get(0))] );
            for ( int i = 1; i<s.size(); i++ ) {
                len = len + distance_between_two_locations( x[Integer.parseInt(s.get(i-1))], y[Integer.parseInt(s.get(i-1))], x[Integer.parseInt(s.get(i))], y[Integer.parseInt(s.get(i))] );

            }
            len = len + distance_between_two_locations( depot_x, depot_y, x[Integer.parseInt(s.get(s.size()-1))], y[Integer.parseInt(s.get(s.size()-1))] );
            return len;
        }

//===============================================================================================================================


        /* Returns a neighbourhood solution
         * of  the input solution
         */
        ArrayList<ArrayList<String>> tabu_search( ArrayList<ArrayList<String>> s) {

            int k = 3;   //   k = number of cases of neighbourhood search
            String swap_temp;
            int kk;
            ArrayList<ArrayList<String>> sol1 = new ArrayList<ArrayList<String>>();
            sol1 = s;
            int nn, swap_list_index, size, n1, n2;
            Random generator = new Random();
            int c = generator.nextInt( k );
            switch (c) {
                case 0:                // One Random Swap
                  nn = sol.size();
                  swap_list_index = generator.nextInt(nn);
                  size = sol.get(swap_list_index).size();
                  Random generator1 = new Random();
                  n1 = generator1.nextInt(size);
                  String value_n1 = sol1.get(swap_list_index).get(n1);
                  Random generator2 = new Random();
                  n2 = generator2.nextInt(size);
                  String value_n2 = sol1.get(swap_list_index).get(n2);

                  sol1.get(swap_list_index).remove(n1);
                  sol1.get(swap_list_index).add(n1, value_n2 );

                  sol1.get(swap_list_index).remove(n2);
                  sol1.get(swap_list_index).add(n2, value_n1 );
                  return sol1;


                case 1:         /* Holding List to Solution */
                    holding_list_index1 = new ArrayList<String>(holding_list_index)  ;
                    Random generator3 = new Random();
                    int mm = generator3.nextInt( holding_list_index1.size() );
                    kk = generator3.nextInt(numVeh);
                    ArrayList<String> te = new ArrayList<String>();
                    if (kk >= sol.size() ) {
                        te = new ArrayList<String>();
                        te.add(  holding_list_index1.get(mm)  );
                        sol1.add(kk, te);
                    }
                    else {
                        sol1.get(kk).add( generator3.nextInt(  sol.get(kk).size() )  ,  holding_list_index1.get(mm)  );
                    }
                    holding_list_index1.remove(mm);
                    break;


                case 2:        /* Shuffling within the Solution */
                    int k1, k2, k_1, k_2;
                    k1 = generator.nextInt(sol.size());
                    k2 = generator.nextInt(sol.size());
                    String temp;
                    k_1 = generator.nextInt( sol1.get(k1).size() );
                    k_2 = generator.nextInt( sol1.get(k2).size() );

                    /* Swapping within the solution */
                    temp = sol1.get(k1).get(k_1);
                    sol1.get(k1).set(k_1,sol1.get(k2).get(k_2));
                    sol1.get(k2).set(k_2,temp);
            }
            return s;
        }

//=============================================================================================================================
        /* This function checks if the input solution is
         * feasible(Deadlines met). Returns true if solution is possible
         * else returns false.
         */
        boolean is_solution_feasible (ArrayList<ArrayList<String>> neighbour_sol) {
                     int num_of_cars, location_id;
                     int flag = 0;
                     float curr_time;
                     float present_x, time_for_local_travel;
                     float present_y;
                     num_of_cars  =  neighbour_sol.size();
                     dist1 = 0;
                     for (int i = 0; i< num_of_cars; i++ ) {
                         curr_time = 0;
                         present_x = depot_x;
                         present_y = depot_y;
                         for ( int j = 0; j<neighbour_sol.get(i).size() ; j++ )  {
                             location_id = Integer.parseInt( neighbour_sol.get(i).get(j) );
                             time_for_local_travel = distance_between_two_locations(present_x, present_y, x[location_id], y[location_id] )/speed;
                             curr_time = curr_time + time_for_local_travel;
                             if ( curr_time <= deadlines[location_id] )  {
                                 present_x = x[location_id];
                                 present_y = y[location_id];
                                 curr_time = curr_time + service_time ;
                                 continue;
                             }
                             else {
                                 return false;
                             }

                         }
                     }
                     return true;
        }

//============================================================================================================================

        /* Returns true if the neighbouring solution is better
         * in terms of number of customers served and the distance
         * travelled to serve the customers else returns false
         */
        boolean is_neighbour_better( ArrayList<ArrayList<String>> neighbour_sol ) {

            int n1 = 0;
            for ( int i = 0; i<neighbour_sol.size() ; i++) {
                n1 = n1 + neighbour_sol.get(i).size();
            }

            int n2 = 0;
            for ( int i = 0; i<sol.size() ; i++) {
                n2 = n2 + sol.get(i).size();
            }

            if ( n1 > n2  ) {
                return true;
            }
            else {
                //If number of customers served are equal, check distances travelled
                if ( dist1 < dist ) {
                    return true;
                }
                else {
                    return false;
                }
            }
        }

//==============================================================================================================================



        /* Implements the algorithm
         * as in the research paper titled
         * "Vehicle routing problem with time windows and a limited number of vehicles"
         */
        void vrptw() {

            /* Copy present solution to 'neighbour_sol' */
            ArrayList<ArrayList<String>> neighbour_sol = new ArrayList<ArrayList<String>>(sol);
            for (int i = 0; i<sol.size();i++) {
                neighbour_sol.remove(i);
                neighbour_sol.add(i, new ArrayList<String>(sol.get(i)) );
            }


            while ( numVeh <= m && !(holding_list_index.isEmpty()) ) {
                count = 0;
                ArrayList<ArrayList<String>> sol_new = new ArrayList<ArrayList<String>>();

                while ( count < countLimit  && !(holding_list_index.isEmpty())  ) {
                    neighbour_sol = new ArrayList<ArrayList<String>>(sol);
                    for (int i = 0; i<sol.size();i++) {
                        neighbour_sol.remove(i);
                        neighbour_sol.add(i, new ArrayList<String>(sol.get(i)) );
                    }

                     System.out.println();
                     sol_new = tabu_search( neighbour_sol );
                     System.out.println();
                     /*  Is neighbour_sol feasible ?  */
                     boolean possible = is_solution_feasible (neighbour_sol);
                     dist1 = 0;
                     for ( int i = 0; i < neighbour_sol.size(); i++ ) {
                         dist1 = dist1 + route_length(neighbour_sol.get(i));
                     }
                     /*  Is neighbour_sol better   ?  */
                     boolean better = is_neighbour_better( neighbour_sol );
                     System.out.println();
                    if ( possible  &&  better ) {
                        sol = new ArrayList<ArrayList<String>>(neighbour_sol);
                        int kn = sol.size();
                        for (int i = 0; i< kn ;i++) {
                            sol.remove(0);
                        }
                        for (int i = 0; i<neighbour_sol.size();i++) {
                            sol.add(i, new ArrayList<String>(neighbour_sol.get(i)) );
                        }
                        holding_list_index = holding_list_index1;
                    }
                    else {
                        for( int i = 0; i<neighbour_sol.size(); i++) {
                             neighbour_sol.remove(i);
                        }
                        for( int i = 0; i < sol.size(); i++) {
                            neighbour_sol.add(sol.get(i));
                        }
                        count = count + 1;
                    }
                }
                numVeh = numVeh + 1;
            }
        }

//==================================================================================================================================


        /* Prints the solution approximated by the algorithm */
        String print_solution( String s ) {
            s = "";
            for ( int i = 0; i<sol.size(); i++ ) {
                s = s + "Vehicle" + Integer.toString(i)+ "  :   :  ";

                for (int j = 0; j<sol.get(i).size(); j++) {
                    s = s + (sol.get(i).get(j)) + " " + "  ";
                }
                s = s + " -------------- ";
            }
            
            return s;
        }

//=====================================================================================================================================


    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int len = 0;
        String data="";  
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        Collection fileParts = request.getParts();

        Iterator filePartIt = fileParts.iterator();
        while(filePartIt.hasNext()){
            Part filePart = (javax.servlet.http.Part)filePartIt.next();
            BufferedReader fileReader =
            new BufferedReader(new InputStreamReader(filePart.getInputStream()));
            String line = null;
            while(( line = fileReader.readLine()) != null){
                data = data + line;
            }
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(data));
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("Point");
            len = nList.getLength();
            n = len;
            nList = doc.getElementsByTagName("Params");
            Node nNode;
            nNode = nList.item(0);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                m = Integer.parseInt(getTagValue("numVeh", eElement));
                speed = Integer.parseInt( getTagValue("speed", eElement) );
                service_time = Float.valueOf( getTagValue("serviceTime", eElement) );
                countLimit = Integer.parseInt( getTagValue("countLimit", eElement) );
                depot_x = Float.valueOf( getTagValue("depotX", eElement) );
                depot_y = Float.valueOf( getTagValue("depotY", eElement) );;
           }

            nList = doc.getElementsByTagName("Point");
            len = nList.getLength();
            n = len;
            x = new float[len];
            y = new float[len];
            holding_x = new float[len];
            holding_y = new float[len];
            deadlines = new float[len];
            for ( int i = 0; i<n; i++) {
                holding_list_index.add(Integer.toString(i));
            }
        
            /* The locations and the deadlines are initialized here */
            for (int temp = 0; temp < len; temp++) {

		   nNode = nList.item(temp);
		   if (nNode.getNodeType() == Node.ELEMENT_NODE) {

		      Element eElement = (Element) nNode;
                      x[ Integer.parseInt( getTagValue("id", eElement) ) ] = Integer.parseInt(getTagValue("x", eElement));
                      y[ Integer.parseInt( getTagValue("id", eElement) ) ] = Integer.parseInt(getTagValue("y", eElement));
                      deadlines[ Integer.parseInt( getTagValue("id", eElement) ) ] = Float.valueOf( getTagValue("deadline", eElement) );
		   }
            }           
            holding_x = x;
            holding_y = y;
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* Preparing initial solution   */
        sol = new ArrayList<ArrayList<String>>();
        ArrayList<String> initial_sol = new ArrayList<String>();
        initial_sol = find_initial_sol(initial_sol);
        dist = route_length (initial_sol);

        /* Solution is a 'list of lists' where each list member represents the path taken by a vehicle.
         * The number of list members in the solution represents the number of cars being used.
         */
        sol.add(initial_sol);

        vrptw(); /* vrptw :  Vehicle Routing Problem with Time Windows */

        String solution = "This is the solution";
        solution = print_solution(solution);
        out.println(solution);
        out.close();


       


        
    }





    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
