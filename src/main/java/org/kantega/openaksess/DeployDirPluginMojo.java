package org.kantega.openaksess;

import java.io.File;

/**
 * Deploys a plugin using target/classes
 *
 * @goal deploy
 * @execute phase="test"

 */
public class DeployDirPluginMojo extends AbstractDeployPluginMojo {
    /**
     * Artifactfile
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File classesDirectory;

    @Override
    protected File getPluginFile() {
        return classesDirectory;
    }
}
