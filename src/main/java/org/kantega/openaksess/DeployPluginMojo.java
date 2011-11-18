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
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Goal which touches a timestamp file.
 *
 * @goal deploy
 * @execute phase="package"
 *
 */
public class DeployPluginMojo
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
     * Artifactfile
     * @parameter expression="${project.build.directory}/${project.build.finalName}.jar"
     * @required
     */
    private File artifactFile;


    public void execute()
        throws MojoExecutionException
    {
        try {
            String params = "file=" + artifactFile.getAbsolutePath() +"&resourceDirectory=" + resourceDirectory.getAbsolutePath();

            String url = deploymentURL +"/PluginDeployment.action";

            getLog().info("Deploying to URL " + url);


            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(params.getBytes());
            outputStream.close();

            String line = null;

            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while((line = br.readLine()) != null) {
                getLog().info("Server: " + line);
            }
            br.close();



        } catch (MalformedURLException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
