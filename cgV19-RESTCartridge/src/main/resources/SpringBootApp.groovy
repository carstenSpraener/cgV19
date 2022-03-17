import de.spraener.nxtgen.ProtectionStrategie
import de.spraener.nxtgen.cartridge.rest.RESTJavaHelper

def pkgName = RESTJavaHelper.toPkgName(modelElement.getPackage());
def cName = modelElement.name;

return """//${ProtectionStrategie.GENERATED_LINE}
package ${pkgName};

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ${cName} {

    public static void main(String[] args) {
        SpringApplication.run(${cName}.class, args);
    }

    @Bean
    public Gson createGson() {
        return new GsonBuilder()
                .setDateFormat("dd.MM.yyyy").create();
    }
}

"""
