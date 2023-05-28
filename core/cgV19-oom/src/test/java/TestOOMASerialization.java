import com.google.gson.*;
import de.spraener.nxtgen.NextGen;
import de.spraener.nxtgen.model.ModelElement;
import de.spraener.nxtgen.model.impl.ModelElementImpl;
import de.spraener.nxtgen.oom.model.OOMModelLoader;
import de.spraener.nxtgen.oom.model.OOModel;
import org.junit.Test;

import java.lang.reflect.Type;

import static org.junit.Assert.*;

public class TestOOMASerialization {

    @Test
    public void testJSON() throws Exception {
        OOMModelLoader modelLoader = new OOMModelLoader();
        NextGen.setActiveLoader(modelLoader);
        OOModel model = (OOModel) new OOMModelLoader().loadModel("file:src/test/resources/activity.oom");
        String json = new Gson().toJson(model);
        assertNotNull(json);
        OOModel restoredModel = new GsonBuilder()
                .registerTypeAdapter(ModelElement.class, new InstanceCreator<ModelElementImpl>() {
                    @Override
                    public ModelElementImpl createInstance(Type type) {
                        return new ModelElementImpl();
                    }
                })
                .create()
                .fromJson(json,OOModel.class);
        assertNotNull(restoredModel);
        assertNotNull(restoredModel.getModelElements().get(0));
    }
}
