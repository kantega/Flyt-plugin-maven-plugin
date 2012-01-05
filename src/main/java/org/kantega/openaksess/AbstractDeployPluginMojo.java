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
     * @required
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
    /**
     * Unique plugin id
     * @parameter expression="${project.groupId}:${project.artifactId}"
     * @required
     */
    private String pluginKey;


    public void execute()
            throws MojoExecutionException, MojoFailureException {
        try {
            String params = "file=" + getPluginFile().getAbsolutePath()
                    +"&resourceDirectory=" + resourceDirectory.getAbsolutePath()
                    +"&id=" + pluginKey;

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
            outputStream.write(params.getBytes());
            outputStream.close();

            int responseCode = urlConnection.getResponseCode();

            StringWriter sw = new StringWriter();
            String line = null;
            BufferedReader br = new BufferedReader(new InputStreamReader(responseCode == 200 ? urlConnection.getInputStream() : urlConnection.getErrorStream()));
            while((line = br.readLine()) != null) {
                sw.append(line +"\n");
            }
            br.close();

            if(responseCode != 200) {
                getLog().error(sw.toString());
                throw new MojoFailureException("Plugin deployment failed with exception: ");
            } else {
                getLog().info("Deployment succeded in " + (System.currentTimeMillis()-before) +"ms: " + sw.toString());
            }
        } catch (MalformedURLException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    protected abstract File getPluginFile();
}
