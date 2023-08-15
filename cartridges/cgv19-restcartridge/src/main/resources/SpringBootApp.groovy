import de.spraener.nxtgen.CGV19Config
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.pojo.ClassFrameTargetCreator
import de.spraener.nxtgen.target.CodeBlockSnippet
import de.spraener.nxtgen.target.CodeTarget
import de.spraener.nxtgen.target.CodeTargetToCodeConverter
import de.spraener.nxtgen.target.SingleLineSnippet
import de.spraener.nxtgen.target.java.JavaSections

def pkgName = ((MClass)modelElement).getPackage().getFQName()
MClass mClass = (MClass)modelElement;
def cName = modelElement.name;

class SpringBootAppCodeTarget {
    static final String ASPECT_SPRING_BOOT_APP = "SpringBootApp"
    MClass mClass;
    String globalHostURL =CGV19Config.definitionOf("globalHostURL", "http://localhost");

    SpringBootAppCodeTarget( MClass mClass ) {
        this.mClass = mClass;
    }

    CodeTarget createCodeTarget() {
        CodeTarget main = new ClassFrameTargetCreator(mClass).createPoJoTarget();

        main.inContext(ASPECT_SPRING_BOOT_APP, mClass,
                this::addImport,
                this::addSpringAnnotations,
                this::addMainImplementation,
                this::addGsonBean
        )
        return main;
    }
    void addImport(CodeTarget t) {
        t.getSection(JavaSections.IMPORTS)
                .add(new SingleLineSnippet("import com.google.gson.Gson;"))
                .add(new SingleLineSnippet("import com.google.gson.GsonBuilder;"))
                .add(new SingleLineSnippet("import jakarta.servlet.http.HttpServletRequest;"))
                .add(new SingleLineSnippet("import org.springframework.boot.SpringApplication;"))
                .add(new SingleLineSnippet("import org.springframework.boot.autoconfigure.SpringBootApplication;"))
                .add(new SingleLineSnippet("import org.springframework.context.annotation.Bean;"))
                .add(new SingleLineSnippet("import org.springframework.security.config.annotation.web.builders.HttpSecurity;"))
                .add(new SingleLineSnippet("import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;"))
                .add(new SingleLineSnippet("import org.springframework.security.config.http.SessionCreationPolicy;"))
                .add(new SingleLineSnippet("import org.springframework.security.web.SecurityFilterChain;"))
                .add(new SingleLineSnippet("import org.springframework.web.cors.CorsConfiguration;"))
                .add(new SingleLineSnippet("import org.springframework.web.cors.CorsConfigurationSource;"))
                .add(new SingleLineSnippet("import java.util.Arrays;"))
                .add(new SingleLineSnippet("import java.util.Collections;"))
                .add(new SingleLineSnippet("import java.util.List;"))
    }

    void addSpringAnnotations(CodeTarget t) {
        t.getSection(JavaSections.CLASS_DECLARATION)
                .getFirstSnippetForAspect(ClassFrameTargetCreator.CLAZZ_FRAME)
                .insertBefore(new SingleLineSnippet("@SpringBootApplication"))
                .insertBefore(new SingleLineSnippet("@EnableWebSecurity"))
    }

    void addMainImplementation(CodeTarget t) {
        t.getSection(JavaSections.METHODS)
                .add ( new CodeBlockSnippet(
"""
    public static void main(String[] args) {
        SpringApplication.run(${this.mClass.getName()}.class, args);
    }
"""))
    }

    void addGsonBean(CodeTarget t) {
        t.getSection(JavaSections.METHODS)
                .add ( new CodeBlockSnippet(
"""  
    @Bean
    public Gson createGson() {
        return new GsonBuilder()
            .setDateFormat("dd.MM.yyyy").create();
    }
    
    
    @Bean
    public SecurityFilterChain configSecurity(HttpSecurity http ) throws Exception {
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {
                @Override
                public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of(
                        "http://localhost:8080",
                        "${globalHostURL}"
                        // TODO: Add additional CORS-URLs here
                    ));
                    config.addAllowedMethod(CorsConfiguration.ALL);
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(Collections.singletonList("*"));
                    config.setExposedHeaders(Arrays.asList("Authorization"));
                    config.setMaxAge(3600L);
                    return config;
                }
            }))
            .authorizeHttpRequests(a -> a
                .requestMatchers("*/ping").permitAll()
            );
        return http.build();
    }

"""
                ))
    }
}

return new CodeTargetToCodeConverter(new SpringBootAppCodeTarget(mClass).createCodeTarget()).toString();
