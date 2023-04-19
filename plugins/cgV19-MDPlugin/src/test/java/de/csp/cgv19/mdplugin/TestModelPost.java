package de.csp.cgv19.mdplugin;

import com.google.gson.Gson;
import de.csp.cgv19.mdplugin.oom.OOMImport;
import de.spraener.nxtgen.oom.model.OOMModelLoader;
import de.spraener.nxtgen.oom.model.OOModel;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class TestModelPost {
    private OOModel model = (OOModel)new OOMModelLoader().loadModel("file:src/test/resources/test.oom");

    @Test
    public void testModelPost() throws Exception {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(model);
        OOModel restoredModel = gson.fromJson(jsonStr,OOModel.class);
        Assert.assertNotNull(restoredModel);
    }

    @Test
    public void testPostSerializedModel() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(model);
        String b64Coded = Base64.getEncoder().encodeToString(baos.toByteArray());
        System.out.println(b64Coded);

        OOModel m = (OOModel)OOMImport.readFromPayload("bin", b64Coded);
        Assert.assertNotNull(m);
    }
}
