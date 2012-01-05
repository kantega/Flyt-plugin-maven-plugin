package org.kantega.openaksess;

import java.io.File;

/**
 * Deploys a jar file
 *
 * @goal deploy-jar
 * @execute phase="test"
 */
public class DeployJarPluginMojo extends AbstractDeployPluginMojo {

    /**
     * Artifactfile
     * @parameter expression="${project.build.directory}/${project.build.finalName}.jar"
     * @required
     */
    private File artifactFile;


    @Override
    protected File getPluginFile() {
        return artifactFile;
    }
}
