package utoronto.utsc.cs.cscc01.chatbot;

import java.io.*;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;



@WebServlet (urlPatterns = "/handlefiles")
@MultipartConfig
public class HandleFilesServlet extends HttpServlet {

    private boolean isMultipart;
//    private String filePath = "../chatbot/files/";
    private FilesDatabaseAdmin db;
    //TODO: see if these fields are needed

    // private int maxFileSize = 1024 * 1024;
    // private int maxMemSize = 4 * 1024;
    private File file ;


//    public HandleFilesServlet(FilesDatabaseAdmin db){
//        // Get the file location where it would be stored.
//        this.db = db;
//
//    }

    public void init () {
        this.db = new FilesDatabaseAdmin();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {

        String action = request.getParameter("action");


        if ("upload".equals(action)) {
            try {
                upload(request, response);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        else if ("remove".equals(action)) {
            try {
                remove(request, response);

            } catch (IOException e) {

                System.out.println(e.getMessage());
            }
        }

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {

        // request.getRequestDispatcher("/handlefiles.jsp").forward(request, response);
        listUploadedFile(request, response);

    }

    private void listUploadedFile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        try {

            db.connect();
            List<UploadedFile> listUploadedFile = db.list();
            request.setAttribute("listUploadedFile", listUploadedFile);

            RequestDispatcher dispatcher = request.getRequestDispatcher("handlefiles.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    protected void remove(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

//        File file;

        String fileName =request.getParameter("file");
        if (db.connect()) {
            db.remove(fileName);
//
//            if( fileName.lastIndexOf("\\") >= 0 ) {
//                file = new File( filePath + fileName.substring( fileName.lastIndexOf("\\")));
//            } else {
//                file = new File( filePath + fileName.substring(fileName.lastIndexOf("\\")+1));
//            }
//
//            file.delete();

            String text = "You successfully removed: " + fileName;
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(text);
        }
    }

    public void upload(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
        // Check that we have a file upload request
        java.io.PrintWriter out = response.getWriter();
        List<Part> fileParts; // Retrieves <input type="file" name="file" multiple="true">
        try {
            fileParts = request.getParts().stream().filter(part -> "file".equals(part.getName())).collect(Collectors.toList());
            for (Part filePart : fileParts) {
                String fileName = getFileName(filePart);
                InputStream fileContent = filePart.getInputStream();
                db.insertFile(fileName, fileContent, filePart.getSize());
                out.println("Uploaded Filename: " + fileName);

            }
        } catch (ServletException e) {
            out.println("Can't upload any files");
        }

    }

//        isMultipart = ServletFileUpload.isMultipartContent(request);
//        response.setContentType("text/html");
//        java.io.PrintWriter out = response.getWriter();
//
//        if( !isMultipart ) {
//            out.println("<html>");
//            out.println("<head>");
//            out.println("<title>Servlet upload</title>");
//            out.println("</head>");
//            out.println("<body>");
//            out.println("<p>No file uploaded</p>");
//            out.println("</body>");
//            out.println("</html>");
//            return;
//        }
//
//        DiskFileItemFactory factory = new DiskFileItemFactory();
//
//        //TODO: see if these fields are needed
//
//        // maximum size that will be stored in memory
//        // factory.setSizeThreshold(maxMemSize);
//
//        // Location to save data that is larger than maxMemSize.
//        // factory.setRepository(new File("../chatbot/files/mem/"));
//
//        // Create a new file upload handler
//        ServletFileUpload upload = new ServletFileUpload(factory);
//
//        // maximum file size to be uploaded.
//        // upload.setSizeMax( maxFileSize );
//
//        try {
//            // Parse the request to get file items.
//            List fileItems = upload.parseRequest(request);
//
//            db.connect();
//
//            // Process the uploaded file items
//            Iterator i = fileItems.iterator();
//
//            out.println("<html>");
//            out.println("<head>");
//            out.println("<title>Servlet upload</title>");
//            out.println("</head>");
//            out.println("<body>");
//
//            while ( i.hasNext () ) {
//                FileItem fi = (FileItem)i.next();
//                if ( !fi.isFormField () ) {
//                    // Get the uploaded file parameters
//                    String fileName = fi.getName();
//                    // Write the file
//                    if( fileName.lastIndexOf("\\") >= 0 ) {
//                        file = new File( filePath + fileName.substring( fileName.lastIndexOf("\\")));
//                    } else {
//                        file = new File( filePath + fileName.substring(fileName.lastIndexOf("\\")+1));
//                    }
//                    fi.write( file ) ;
//
//                    byte[] b = db.readFile(file);
//
//                    out.println("Uploaded Filename: " + fileName + "<br>");
//
//                    db.insertFile(fileName, b);
//
//                }
//            }
//            out.println("</body>");
//            out.println("</html>");
//
//        } catch (Exception e) {
//            out.println("<html>");
//            out.println("<head>");
//            out.println("<title>Servlet upload</title>");
//            out.println("</head>");
//            out.println("<body>");
//            out.println("</body>");
//            out.println("No file(s) uploaded! Please try again.");
//            out.println("</html>");
//
//        }
//    }

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        System.out.println("content-disposition header= "+contentDisp);
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length()-1);
            }
        }
        return "";
    }

}