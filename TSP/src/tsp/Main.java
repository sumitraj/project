
package tsp;
import java.io.*;
import java.util.*;
import java.lang.Math;
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
        float[] x = new float[4];
        float[] y = new float[4];
        float[] holding_x = new float[4];
        float[] holding_y = new float[4];
        float[] deadlines = new float[4];
        ArrayList<String> holding_list_index;
        ArrayList<ArrayList<String>> sol;// = new ArrayList<ArrayList<String>>();
        //int c;

        public  Func() {
            //c = 1;
            n = 4;
            m = 2;
            numVeh = 1;
            count = 0;
            countLimit = 100;
            speed = 2;
            depot_x = 1;
            depot_y = 1;
            x[0] = 1;
            y[0] = 5;
            x[1] = 2;
            y[1] = 3;
            x[2] = 4;
            y[2] = 2;
            x[3] = 5;
            y[3] = 1;
            holding_x = x;
            holding_y = y;
            holding_list_index = new ArrayList<String>();
            for ( int i = 0; i<n; i++) {
                holding_list_index.add(Integer.toString(i));
            }
            

        }
        float distance_between_two_locations( float x0, float y0, float x1, float y1 ) {
            //System.out.println( Math.abs(x0 - x1));
            //System.out.println( Math.abs(y0 - y1) );
            return  Math.abs(x0 - x1) + Math.abs(y0 - y1);
        }
        int find_nearest_location_to_depot() {
            float min = 99999;
            float dist;
            int min_index = 0;
            for (int i = 0; i<n; i++){
                dist = distance_between_two_locations(depot_x, depot_y, x[i], y[i] );
                //System.out.println(dist);
                if (dist < min) {
                   min_index = i;
                   min = dist;
                }
            }
            return min_index;
        }

        ArrayList<ArrayList<String>> tabu_search( ArrayList<ArrayList<String>> s) {

            int k = 1;   //////   k = number of cases of neighbourhood search
            String swap_temp;
            ArrayList<ArrayList<String>> sol1 = new ArrayList<ArrayList<String>>();
            sol1 = s;                 //////////////    ALWAYS  A   REFERENCE   ///////////////
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
                  
                case 1:

                    break;
            }
            return s;
        }
        

        void vrptw() {

            //System.out.println(sol.get(0));
            
            
            ArrayList<ArrayList<String>> neighbour_sol = new ArrayList<ArrayList<String>>(sol);
            for (int i = 0; i<sol.size();i++) {
                neighbour_sol.remove(i);
                neighbour_sol.add(i, new ArrayList<String>(sol.get(i)) );
            }
            //base_sol.add(sol.get(0));
            //base_sol.get(0).remove(0);

            while ( numVeh <= m && !(holding_list_index.isEmpty()) ) {
                count = 0;
                //System.out.println(sol.get(0).get(2));
                ArrayList<ArrayList<String>> sol_new = new ArrayList<ArrayList<String>>();

                while ( count < countLimit ) {

                     System.out.println();

                     sol_new = tabu_search( neighbour_sol );
                     
                     System.out.println();

                     //  Is neighbour_sol feasible  ??

                     // Compare sol and neighbour_sol
                     
                    if (false/*neighbour_sol is better than sol*/) {
                        //sol = sol_new;
                        sol = new ArrayList<ArrayList<String>>(neighbour_sol);
                        for (int i = 0; i<neighbour_sol.size();i++) {
                            sol.remove(i);
                            sol.add(i, new ArrayList<String>(neighbour_sol.get(i)) );
                        }
                    }
                    else {
                        count = count + 1;
                    }
                }
                numVeh = numVeh + 1;
            }
        }
}
public class Main {        
    public static void main(String[] args) {

        Func ob = new Func();
        /* Finding the nearest location to the depot for the initial solution.*/
        int index = ob.find_nearest_location_to_depot();
        //System.out.println(index);
        /* Preparing initial solution */
        /*ArrayList<ArrayList<String>>*/ ob.sol = new ArrayList<ArrayList<String>>();
        ArrayList<String> initial_sol = new ArrayList<String>();
        initial_sol.add(Integer.toString(index));
        initial_sol.add(Integer.toString(2));
        initial_sol.add(Integer.toString(3));
        ob.holding_list_index.remove(index);
        //initial_sol.add(4);
        ob.sol.add(initial_sol);

        System.out.println( ob.sol.get(0).size() );


        //System.out.println(sol.get( 0 ).remove(1));
        //System.out.println(sol.get( 0 ));
        ob.vrptw(); /* vrptw :  Vehicle Routing Problem with Time Windows */
    }

}
