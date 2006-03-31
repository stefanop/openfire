/**
 * $RCSfile$
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2006 Jive Software. All rights reserved.
 *
 * This software is published under the terms of the GNU Public License (GPL),
 * a copy of which is included in this distribution.
 */

package org.jivesoftware.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Servlet that gets favicons of webservers and includes them in HTTP responses. This
 * servlet can be used when getting a favicon can take some time so pages can use this
 * servlet as the image source to let the page load quickly and get the favicon images
 * as they are available.<p>
 * <p/>
 * This servlet expects the web application to have the <tt>images/server_16x16.gif</tt>
 * file that is used when no favicon is found.
 *
 * @author Gaston Dombiak
 */
public class FaviconServlet extends HttpServlet {

    /**
     * The content-type of the images to return.
     */
    private static final String CONTENT_TYPE = "image/jpeg";
    private byte[] defaultBytes;
    private HttpClient client;


    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // Create a pool of HTTP connections to use to get the favicons
        client = new HttpClient(new MultiThreadedHttpConnectionManager());
        HttpConnectionManagerParams params = client.getHttpConnectionManager().getParams();
        params.setConnectionTimeout(2000);
        params.setSoTimeout(2000);
        // Load the default favicon to use when no favicon was found of a remote host
        try {
            URL resource = config.getServletContext().getResource("/images/server_16x16.gif");
            defaultBytes = getImage(resource.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve the image based on it's name.
     *
     * @param request  the httpservletrequest.
     * @param response the httpservletresponse.
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String host = request.getParameter("host");
        // Check special cases where we need to change host to get a favicon
        host = "gmail.com".equals(host) ? "google.com" : host;

        byte[] bytes = getImage(host, defaultBytes);
        if (bytes != null) {
            writeBytesToStream(bytes, response);
        }
    }

    /**
     * Writes out a <code>byte</code> to the ServletOuputStream.
     *
     * @param bytes the bytes to write to the <code>ServletOutputStream</code>.
     */
    private void writeBytesToStream(byte[] bytes, HttpServletResponse response) {
        response.setContentType(CONTENT_TYPE);

        // Send image
        try {
            ServletOutputStream sos = response.getOutputStream();
            sos.write(bytes);
            sos.flush();
            sos.close();
        }
        catch (IOException e) {
            // Do nothing
        }
    }

    /**
     * Returns the favicon image bytes of the specified host.
     *
     * @param host the name of the host to get its favicon.
     * @return the image bytes found, otherwise null.
     */
    private byte[] getImage(String host, byte[] defaultImage) {
        byte[] bytes = getImage("http://" + host + "/favicon.ico");
        if (bytes == null) {
            // Return byte of default icon
            bytes = defaultImage;
        }
        return bytes;
    }

    private byte[] getImage(String url) {
        try {
            // Try to get the fiveicon from the url using an HTTP connection from the pool
            // that also allows to configure timeout values (e.g. connect and get data)
            GetMethod get = new GetMethod(url);
            client.executeMethod(get);
            return get.getResponseBody();
        }
        catch (IllegalStateException e) {
            // Something failed (probably a method not supported) so try the old stye now
            try {
                URLConnection urlConnection = new URL(url).openConnection();
                urlConnection.setReadTimeout(1000);

                urlConnection.connect();
                DataInputStream di = new DataInputStream(urlConnection.getInputStream());

                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(byteStream);

                int len;
                byte[] b = new byte[1024];
                while ((len = di.read(b)) != -1) {
                    out.write(b, 0, len);
                }
                di.close();
                out.flush();

                return byteStream.toByteArray();
            }
            catch (IOException ioe) {
                // We failed again so return null
                return null;
            }
        }
        catch (IOException ioe) {
            // We failed so return null
            return null;
        }
    }

}
