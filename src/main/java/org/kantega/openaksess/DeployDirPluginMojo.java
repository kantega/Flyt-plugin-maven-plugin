package org.kantega.openaksess;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

    /**
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    @Override
    protected File getPluginFile() {
        return classesDirectory;
    }

    @Override
    protected void addParameters(StringBuilder params) {
        params.append("&groupId=" + project.getGroupId());
        params.append("&artifactId=" + project.getArtifactId());
        params.append("&version=" + project.getVersion());

        if(project.getName() != null) {
            params.append("&name=" + project.getName());
        }

        if(project.getDescription() != null) {
            params.append("&description=" + project.getDescription());
        }
        Set<String> dependencies = new HashSet<String>();
        for (Dependency dependency : ((List<Dependency>)project.getDependencies())) {
            dependencies.add(dependency.getGroupId() + ":" + dependency.getArtifactId());
        }

        if(!dependencies.isEmpty()) {
            String deps = "";
            for(Iterator<String> i = dependencies.iterator();i.hasNext();) {
                String dep = i.next();
                deps+= dep;
                if(i.hasNext()) {
                    deps +=",";
                }
            }
            params.append("&dependencies=" + deps);
        }
    }
}
