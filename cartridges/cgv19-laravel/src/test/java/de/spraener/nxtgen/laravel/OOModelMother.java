package de.spraener.nxtgen.laravel;

import de.spraener.nxtgen.cartridge.rest.RESTStereotypes;
import de.spraener.nxtgen.model.ModelHelper;
import de.spraener.nxtgen.oom.OOModelBuilder;
import de.spraener.nxtgen.oom.model.MAssociation;
import de.spraener.nxtgen.oom.model.MClass;
import de.spraener.nxtgen.oom.model.MPackage;
import de.spraener.nxtgen.oom.model.OOModel;
import de.spraener.nxtgen.symfony.SymfonyStereotypes;

public class OOModelMother {
    // TODO: Build a default model to use in your test cases
    public static OOModel createDefaultModel() {
        OOModel model = OOModelBuilder.createModel(
            m -> OOModelBuilder.createPackage(m, "my.test.model",
                    p -> OOModelBuilder.createPackage(p, "app",
                            app -> OOModelBuilder.addStereotype(app, LaravelStereotypes.LARAVELAPPLICATION.getName(),
                                    "namespace=App"
                                    )
                            )
                    )
        );

        MPackage appRoot = (MPackage) ModelHelper.findByFQName(model, "my.test.model.app", ".");
        MPackage models = OOModelBuilder.createPackage(appRoot, "models",
            m -> OOModelBuilder.addStereotype(m, SymfonyStereotypes.PHPPACKAGE.getName(), "namespace=Models")
        );
        MPackage events = OOModelBuilder.createPackage(models, "events",
                m -> OOModelBuilder.addStereotype(m, SymfonyStereotypes.PHPPACKAGE.getName(), "namespace=Events")
        );
        MClass event = OOModelBuilder.createMClass(events, "Event",
            e -> OOModelBuilder.addStereotype(e, RESTStereotypes.ENTITY.getName())
        );

        event.createAttribute("name", "String").setModel(model);
        event.createAttribute("abstract", "text").setModel(model);
        event.createAttribute("description", "text").setModel(model);
        event.createAttribute("start_ts", "DateTime").setModel(model);
        event.createAttribute("end_ts", "DateTime").setModel(model);

        MClass participation = OOModelBuilder.createMClass(events, "Participation",
                p->OOModelBuilder.addStereotype(p,RESTStereotypes.ENTITY.getName()),
                p->p.createAttribute("registration_ts", "DateTime")
                );

        MAssociation assoc = OOModelBuilder.createAssociation(event, participation, "participations", "0..*");
        assoc.setOpositeAttribute("event");
        assoc.setOpositeMultiplicity("1");
        assoc.setAssociationType("OneToMany");
        OOModelBuilder.addStereotype(assoc, LaravelStereotypes.MANAGEDRELATION.getName());

        assoc = OOModelBuilder.createAssociation(participation, event, "event", "1");
        assoc.setOpositeAttribute("participations");
        assoc.setOpositeMultiplicity("1");
        assoc.setAssociationType("ManyToOne");
        OOModelBuilder.addStereotype(assoc, LaravelStereotypes.MANAGEDRELATION.getName());

        MPackage cntrlPkg = OOModelMother.findOrCreatePackages(appRoot, "http", "controllers", "event");
        OOModelBuilder.createMClass(cntrlPkg, "EventResource",
                c->OOModelBuilder.addStereotype(c, RESTStereotypes.RESOURCE.getName()),
                c->OOModelBuilder.createDependency(c, event,
                        d->OOModelBuilder.addStereotype(d, RESTStereotypes.CONTROLLEDENTITY.getName())
                )
        );

        OOModelBuilder.createMClass(cntrlPkg, "ParticipationResource",
                c->OOModelBuilder.addStereotype(c, RESTStereotypes.RESOURCE.getName()),
                c->OOModelBuilder.createDependency(c, participation,
                        d->OOModelBuilder.addStereotype(d, RESTStereotypes.CONTROLLEDENTITY.getName())
                )
        );

        return model;
    }

    public static MClass getEvent(OOModel model) {
        return model.findClassByName("my.test.model.app.models.events.Event");
    }

    public static MClass getParticipation(OOModel model) {
        return model.findClassByName("my.test.model.app.models.events.Participation");
    }

    public static MClass getEventResourceController(OOModel model) {
        return model.findClassByName("my.test.model.app.http.controllers.event.EventResource");
    }

    public static MClass getParticipationResourceController(OOModel model) {
        return model.findClassByName("my.test.model.app.http.controllers.event.ParticipationResource");
    }

    public static MPackage findOrCreatePackages(MPackage rootPkg, String... subPackages) {
        MPackage pkg = rootPkg;
        for( String pkgName : subPackages ) {
            pkg = pkg.findOrCreatePackage(pkgName);
            pkg.setModel(rootPkg.getModel());
        }
        return pkg;
    }
}
