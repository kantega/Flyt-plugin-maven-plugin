package org.kantega.openaksess;

import org.apache.maven.artifact.Artifact;

import java.util.List;

/**
 * Deploys a plugin using target/classes
 *
 * @goal compile-deploy
 * @requiresDependencyResolution compile
 */
public class CompileAndDeployPluginMojo extends AbstractDeployDirPluginMojo {


    @Override
    protected void addParameters(StringBuilder params) {

        super.addParameters(params);

        appendClasspath(params, "compileClasspath", project.getCompileArtifacts());
        appendClasspath(params, "runtimeClasspath", project.getRuntimeArtifacts());
    }

    private void appendClasspath(StringBuilder params, String name, List<Artifact> artifacts) {
        params.append("&" + name + "=");


        int c = 0;


        for (Artifact a : artifacts) {

            if (c > 0) {
                params.append(":");
            }
            c++;
            params.append(a.getFile().getAbsolutePath());


        }
    }
}
