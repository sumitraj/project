/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package newpackage;
import java.util.*;
import java.io.*;
import java.io.IOException;
import org.xml.sax.InputSource;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part; 


@WebServlet(name="NewServlet", urlPatterns={"/NewServlet"})
public class NewServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    int kk;
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int len = 0;
        String data="";  
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("Hello from FileUploadServlet");
        Collection fileParts = request.getParts();
        Iterator filePartIt = fileParts.iterator();
        while(filePartIt.hasNext()){
            Part filePart = (javax.servlet.http.Part)filePartIt.next();
            out.println("File Name " + filePart.getName());
            out.println("File Size " + filePart.getSize());
            out.println("File Content ");
            BufferedReader fileReader =
            new BufferedReader(new InputStreamReader(filePart.getInputStream()));
            String line = null;
            while(( line = fileReader.readLine()) != null){
                data = data + line;
            }
        }
        out.println("Hello Sir");
        out.close();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(data));
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("Point");
            len = nList.getLength();
            out.println("dfsd,j");
        } catch (Exception e) {
            e.printStackTrace();
        }



        
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
