
package tsp;
import java.io.*;
import java.util.*;
import java.lang.Math;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
/*
  @author sumit
*/

class Func {
        int n;                              /* n is the total number of locations */
        int m;                              /* m is the maximum number of vehicles available */
        int speed;
        int count;                          /* count is the number of iterations executed without any improvement in solution*/
        int countLimit;                     /* count is bounded by countLimit */
        int numVeh;                         /* numVeh is the number of vehicles used in a particular iteration */
        float depot_x ;                     /* x and y coordinates of the depot   */
        float depot_y ;
        float dist;                       
        float dist1;
        float[] x; // = new float[4];
        float[] y; // = new float[4];
        float[] holding_x; // = new float[4];
        float[] holding_y; // = new float[4];
        float[] deadlines; // = new float[4];
        ArrayList<String> holding_list_index;
        ArrayList<String> holding_list_index1;
        ArrayList<ArrayList<String>> sol;
     
        public  Func() {
            dist = 0;
            dist1 = 0;
            n = 4;
            m = 2;
            numVeh = 1;
            count = 0;
            countLimit = 100;
            speed = 2;
            depot_x = 1;
            depot_y = 1;
            
            holding_x = x;
            holding_y = y;

            holding_list_index = new ArrayList<String>();
            for ( int i = 0; i<n; i++) {
                holding_list_index.add(Integer.toString(i));
            }
        }

//=============================================================================================================================

        /* Returns the Manhattan distance between two locations */
        float distance_between_two_locations( float x0, float y0, float x1, float y1 ) {
            return  Math.abs(x0 - x1) + Math.abs(y0 - y1);
        }

//=============================================================================================================================
        /* Returns the index of the location
         * nearest to the depot
         */
        int find_nearest_location_to_depot() {
            float min = 99999;
            float dist;
            int min_index = 0;
            for (int i = 0; i<n; i++){
                dist = distance_between_two_locations(depot_x, depot_y, x[i], y[i] );
                if (dist < min) {
                   min_index = i;
                   min = dist;
                }
            }
            return min_index;
        }
		
//====================================================================================================================================
		
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

             int n = 0;
            for ( int i = 0; i<sol.size() ; i++) {
                n = n + sol.get(i).size();
            }

            if ( n1 > n  ) {
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
        /* Returns a neighbourhood solution
         * of  the input solution
         */
        ArrayList<ArrayList<String>> tabu_search( ArrayList<ArrayList<String>> s) {

            int k = 2;   //////   k = number of cases of neighbourhood search
            String swap_temp;
            int kk;
            ArrayList<ArrayList<String>> sol1 = new ArrayList<ArrayList<String>>();
            sol1 = s;                                  // This is the neighbour_sol
            int n, swap_list_index, size, n1, n2;
            Random generator = new Random();
            int c = generator.nextInt( k );
            switch (c) {
                case 0:                // One Random Swap
                  n = sol.size();
                  swap_list_index = generator.nextInt(n);
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
                    kk = generator3.nextInt(numVeh);
                    ArrayList<String> te = new ArrayList<String>();
                    if (kk >= sol.size() ) {
                        te = new ArrayList<String>();
                        te.add(  holding_list_index1.get(0)  );
                        sol1.add(kk, te);
                    }
                    else {
                        sol1.get(kk).add( generator3.nextInt(  sol.get(kk).size() )  ,  holding_list_index1.get(0)  );
                    }
                    holding_list_index1.remove(0);
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
        void print_solution() {

            for ( int i = 0; i < sol.size(); i++ ) {
                for (int j = 0; j < sol.get(i).size(); j++ ) {
                    System.out.println(sol.get(i).get(j));
                }
                System.out.println("\n");
            }
        }
    }
//=================================================================================================================================


public class Main {

    private static String getTagValue(String sTag, Element eElement) {
	NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();

        Node nValue = (Node) nlList.item(0);

	return nValue.getNodeValue();
  }

//=================================================================================================================================

    public static void main(String[] args) {

        Func ob = new Func();
        int t;
        int len = 100;
        /* Reading XML  File  */
        try {
            File fXmlFile = new File("data.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            
            NodeList nList = doc.getElementsByTagName("Point");
            len = nList.getLength();
            ob.x = new float[len];
            ob.y = new float[len];
            ob.holding_x = new float[len];
            ob.holding_y = new float[len];
            ob.deadlines = new float[len];

            /* The locations and the deadlines are initialized here */
            for (int temp = 0; temp < nList.getLength(); temp++) {

		   Node nNode = nList.item(temp);
		   if (nNode.getNodeType() == Node.ELEMENT_NODE) {

		      Element eElement = (Element) nNode;
                      /*System.out.println("ID : " + getTagValue("id", eElement));
		      System.out.println("X : " + getTagValue("x", eElement));
		      System.out.println("Y : " + getTagValue("y", eElement));*/
                      ob.x[ Integer.parseInt( getTagValue("id", eElement) ) ] = Integer.parseInt(getTagValue("x", eElement));
                      ob.y[ Integer.parseInt( getTagValue("id", eElement) ) ] = Integer.parseInt(getTagValue("y", eElement));
                      ob.deadlines[ Integer.parseInt( getTagValue("id", eElement) ) ] = Integer.parseInt(getTagValue("deadline", eElement));


		   }
            }
            ob.holding_x = ob.x;
            ob.holding_y = ob.y;

        } catch (Exception e) {
		e.printStackTrace();
	}
        

        /* Finding  the nearest location to the depot for the initial solution.*/
        int index = ob.find_nearest_location_to_depot();
        /* Preparing initial solution   */
        ob.sol = new ArrayList<ArrayList<String>>();
        ArrayList<String> initial_sol = new ArrayList<String>();
        initial_sol.add(Integer.toString(3));
        ob.holding_list_index.remove(Integer.toString(3));
        initial_sol.add(Integer.toString(2));
        ob.holding_list_index.remove(Integer.toString(2));
        initial_sol.add(Integer.toString(1));
        ob.holding_list_index.remove(Integer.toString(1));
        ob.dist = ob.route_length (initial_sol);
        //ob.holding_list_index.remove(index);

        /* Solution is a 'list of lists' where each list member represents the path taken by a vehicle.
         * The number of list members in the solution represents the number of cars being used.
         */
        ob.sol.add(initial_sol);
        
        ob.vrptw(); /* vrptw :  Vehicle Routing Problem with Time Windows */
        ob.print_solution();
        System.out.println();
    }

}
