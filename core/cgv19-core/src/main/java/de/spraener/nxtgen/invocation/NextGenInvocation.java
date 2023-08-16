package de.spraener.nxtgen.invocation;

import de.spraener.nxtgen.ModelLoader;
import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.model.Model;

import java.io.File;
import java.util.UUID;

public class NextGenInvocation implements Runnable, ModelLoader {
    private String workDir;
    private Model model;
    private String cartridgeName = null;
    private ModelLoader modelLoader;
    private String modelURI;

    public static final class Builder {
        private NextGenInvocation invocation = new NextGenInvocation();

        public  Builder withWorkdir(String workdir) {
            invocation.workDir = workdir;
            return this;
        }

        public  Builder withModel(Model model) {
            invocation.model = model;
            invocation.modelURI = UUID.randomUUID().toString();
            return this;
        }

        public Builder withCartridge( String name ) {
            invocation.cartridgeName = name;
            return this;
        }

        public Builder withModelLoader(ModelLoader loader) {
            invocation.modelLoader = loader;
            return this;
        }

        public Builder withModelURI(String modelURI) {
            invocation.modelURI = modelURI;
            return this;
        }

        public NextGenInvocation build() {
            return invocation;
        }
    }

    private NextGenInvocation() {}

    public static NextGenInvocation.Builder builder() {
        return new Builder();
    }

    public void run() {
        if( this.workDir != null ) {
            new File(this.workDir).mkdirs();
            NextGen.setWorkingDir(this.workDir);
        }
        if( this.cartridgeName != null ) {
            NextGen.runCartridgeWithName(this.cartridgeName);
        }
        ModelLoader activeModelLoader = null;
        if( model != null ) {
            activeModelLoader = this;
        } else if (this.modelLoader != null ) {
            activeModelLoader = this.modelLoader;
        }
        if( activeModelLoader != null ) {
            NextGen.addModelLoader(activeModelLoader);
            NextGen.setActiveLoader(activeModelLoader);
        }
        NextGen.main(new String[]{this.modelURI});
    }

    @Override
    public boolean canHandle(String modelURI) {
        return modelURI.equals(this.modelURI);
    }

    @Override
    public Model loadModel(String modelURI) {
        return this.model;
    }
}
