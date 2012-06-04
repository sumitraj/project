
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
        ArrayList<Object> holding_list_index;
        ArrayList<ArrayList<Object>> sol;// = new ArrayList<ArrayList<Object>>();
        

        public  Func() {
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
            holding_list_index = new ArrayList<Object>();
            for ( int i = 0; i<n; i++) {
                holding_list_index.add(i);
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

        ArrayList<ArrayList<Object>> tabu_search( ArrayList<ArrayList<Object>> s) {
            return s;
        }

        void vrptw() {

            //System.out.println(sol.get(0));
            
            
            
            while ( numVeh <= m && !(holding_list_index.isEmpty()) ) {
                count = 0;
                ArrayList<ArrayList<Object>> sol1;
                while ( count < countLimit ) {
                    // sol1 = tabu_search(sol);

                    // Compare sol and sol1
                    
                    if (false/*better sol*/) {
                        sol = sol1;
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
        /*ArrayList<ArrayList<Object>>*/ ob.sol = new ArrayList<ArrayList<Object>>();
        ArrayList<Object> initial_sol = new ArrayList<Object>();
        initial_sol.add(index);
        ob.holding_list_index.remove(index);
        initial_sol.add(4);
        ob.sol.add(initial_sol);
        
        //System.out.println(sol.get( 0 ).remove(1));
        //System.out.println(sol.get( 0 ));
        ob.vrptw(); /* vrptw :  Vehicle Routing Problem with Time Windows */
    }

}
