/*
 * Copyright 2011 Kantega AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kantega.openaksess;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 */
public abstract class AbstractDeployPluginMojo
    extends AbstractMojo
{
    /**
     * Deployment URL
     * @parameter
     */
    private String deploymentURL;

    /**
     * Deployment artifact
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    /**
     * Resource directory
     * @parameter expression="${basedir}/src/main/resources"
     * @required
     */
    private File resourceDirectory;

    public void execute()
            throws MojoExecutionException, MojoFailureException {
        try {
            StringBuilder params = new StringBuilder("file=" + getPluginFile().getAbsolutePath()
                    +"&resourceDirectory=" + resourceDirectory.getAbsolutePath());

            addParameters(params);

            String deploymentURL = getDeploymentURL();

            if(!deploymentURL.endsWith("/"))
                deploymentURL +="/";

            String url = deploymentURL +"PluginDeployment.action";

            getLog().info("Deploying to URL " + url);
            long before = System.currentTimeMillis();

            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(params.toString().getBytes("utf-8"));
            outputStream.close();

            int responseCode = urlConnection.getResponseCode();
            InputStream stream = responseCode == 200 ? urlConnection.getInputStream() : urlConnection.getErrorStream();

            String content = toString(stream);

            if(responseCode != 200) {
                getLog().error(content);
                throw new MojoFailureException("Plugin deployment failed with exception: ");
            } else {
                getLog().info("Deployment succeded in " + (System.currentTimeMillis()-before) +"ms: " + content);
            }
        } catch (MalformedURLException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private String toString(InputStream stream) throws IOException {
        StringWriter sw = new StringWriter();
        String line = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        while((line = br.readLine()) != null) {
            sw.append(line +"\n");
        }
        br.close();
        return sw.toString();
    }

    private String getDeploymentURL() throws MojoFailureException {
        if(this.deploymentURL != null) {
            return this.deploymentURL;
        } else {
            try {
                String base = "http://localhost:8080";
                URL url = new URL(base + "/oa-maven-plugin-context-path");
                getLog().info("Guessing deployment URL from peeking at " + url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                if(urlConnection.getResponseCode() == 200) {
                    String deploymentURL = base + toString(urlConnection.getInputStream()).trim();
                    getLog().info("Deployment URL is: " + deploymentURL);
                    return deploymentURL;
                } else {
                    throw  new MojoFailureException("Could not get context path from URL " + url.toExternalForm());
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }
    }

    protected void addParameters(StringBuilder params) {

    }

    protected abstract File getPluginFile();
}
