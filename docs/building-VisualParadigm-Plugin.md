# Using VisualParadigm and the cgv19-plugin

The first cgV19-Project in [Getting Start
ed](GettingStarted.md) uses an _oom_ file to 
describe the model. This _oom_ file is a groovy script and can be maintained with 
any editor. But that can easily produce errors and typos that slow down the development
process.

Therefore, cgV19 provides a plugin for VisualParadigm. With VP and the plugin installed you
will be able to generate your code directly while modeling in VP. [VP has a community
edition](https://www.visual-paradigm.com/download/community.jsp) which is completely
sufficient for our needs. 

First install the community edition on your system.

* Download Visual Paradigm community edition [from here](https://www.visual-paradigm.com/download/community.jsp).
* Follow the installation steps for your OS

Then follow this steps to build and install the cgV19-VPPlugin.

* [Follow the Getting Started](GettingStarted.md) if you did not yet
* Go to the folder `cd plugins/cgV19-VPPlugin`
* run the bash script buildPlugin.sh inside the cgV19-VPPlugin folder (tested under macOS and Linux)
  ```bash
  sh ./buildPLugin.sh
  ```
* restart VisualParadigm and check the plugin. The link http://localhost:7001/ping should
  response with a "pong!"

YES! You successfully installed the cgV19-VPPlugin into VisualParadigm.

## Start using VisualParadigm with cgv19 in your project

First you need to create a new project with cgV19 enabled. Go to your projects folder
and create a new cgV19 project with:

```bash
cgv19 -m my-app.yml -c cgv19Gradle
...

Please give value for 'cgv19-Version':
23.1.0
Please give value for 'projectName':
my-app
Please give value for 'rootPackage':
de.spraener.my.app
```
When you answered all the questions you will have a new cgv19 project in the my-app folder.
(or whatever value you gave to projectName). The project has no model file for _Visual Paradigm_
but cgv19 comes with an empty starter model. Copy this model under a new name into the project.

```bash
cd my-app
mkdir model
cp ../cgV19/cartridges/cgv19-cartridge-stereotypes.vpp model/my-app.vpp
```
_(In this example, the project my-app lies next to the cgV19 clone directory)_

The community edition does not support including subprojects. So you have to create a copy
of the starter project. The starter project contains all the necessary stereotype and tagged
value definitions, that are known by the provided cartridges. If this sounds a little 
strange to you that's OK. Things will get clear very soon.

Your project is now ready to start model driven development with cgV19 and Visual Paradigm.

## The first development cycle

It's time to get started with modeling, let cgv19 generate some code, modify the generated code,
find some new needs and start the cycle again.

Open Visual Paradigm and load the copied model from `model/my-app.vpp`:
![vp-firstStart.png](images%2Fvp-firstStart.png)

Load the model with the `Project/Open` menu and click on the 
__Local File System__ button. Navigate to the model and load it
into __Visual Paradigm__.

![vp-openFirstProject.png](images%2Fvp-openFirstProject.png)

You may have to click on the red __X__ to close the selection
dialog.

![vp-projectOpend.png](images%2Fvp-projectOpend.png)

__In the next steps you will build exactly the same model
as it was generated when the project my-app was created.__

Create a new Package by selecting the "Package" in the elements
browser and click in the diagram space on the right side. Give
the package the name of you projects root package. After
that your project should look like this:

![vp-firstPackage.png](images%2Fvp-firstPackage.png)

When you click on the new inserted package you will find a small
angled arrow in the bottom right corner. Click on this arrow and
select _"New Diagram..."_. Select _"Class Diagramm"_, press 
_"Next"_, choose _"Blank"_, press _"Next"_ and give the new 
Class Diagramm a name like _"My App"_. Close the diagram
creation dialog with the _"OK"_ button.

![vp-firstClassDiagramm.png](images%2Fvp-firstClassDiagramm.png)

Now create a Class. This is done in the same way as you created
the Package. Select the _Class_ in the element browser and click
into the diagram space to create a new _Class_. Give the class
the name _App_. 

![vp-appClassCreated.png](images%2Fvp-appClassCreated.png)

The last step is to provide the _App Class_ with the << PoJo >>
stereotype. _A stereotype tells the generator how to deal with
a model element_ We want the _App Class_ to be interpreted as 
a << PoJo >>. To do so select the Class in the diagramm, 
right-click on the class and select the _"Stereotypes"_ menu.

![vp-stereotypesList.png](images%2Fvp-stereotypesList.png)

If you can see the _<< PoJo >>_ Stereotype here, select it. If 
not open the _"Edit Stereotypes"_ menu and choose the _"PoJo"_
in the list of stereotypes. Press the _">"_ button to assign the
stereotype to the class.

![vp-assignStereotype.png](images%2Fvp-assignStereotype.png)

Now the _App Class_ should have the stereotype _<< PoJo >>_ 
assigned to it.

![vp-classAppWithStereotype.png](images%2Fvp-classAppWithStereotype.png)

OK, we have modeled our first project. Let's check the result.

Open a browser and open the URL http://localhost:7001/de.spraener.my.app
or whatever you project root package was named.

The cgv19-plugin inside VisualParadigm will answer with the groovy
script. This script containes the same model as the _oom_ file:

```groovy
import de.spraener.nxtgen.groovy.ModelDSL

ModelDSL.make {
  mPackage {
    name 'de.spraener.my.app'
    documentation """"""
    id '3rwawFGGAqAAChXj'
    metaType 'Package'
    mClass {  //Exported by ClassExporterBase
      name 'App'
      documentation """"""
      id '3rwawFGGAqAAChXj:sbL6wFGGAqAAChYE'
      metaType 'Class'
      stereotype 'PoJo'
    }
  }
}
```

## Model driven development with cgv19

The model inside _Visual Paradigm_ has now the exact same structure
as the generated _oom_ file. Let's start cgv19 to use this model in 
_Visual Paradigm_ to generate the projects code:

```bash
cgv19 -m http://localhost:7001/de.spraener.my.app
```

This starts cgv19 with the _-m_ option to sepcify the model to load.

__Note:__ the package name in the URL must exactly match the name
of the package in _Visual Paradigm_. In my case "de.spraener.my.app"

cgv19 will start generating the project. You can see the result
under `src/main/java/de/spraener/my/app/App.java` and 
`src/main/java-gen/de/spraener/my/app/AppBase.java`

__Note:__ The PoJo-Cartridge, that is responsible for generating
PoJos splitts a single PoJo in two parts. A 100% generated 
Base-Class and a Class that extends the Base-Class. This splitt is done
by a _Transformation_.

![vp-appGeneratorGapTransformation.png](images%2Fvp-appGeneratorGapTransformation.png)


### A very simple enhancement

Let's enhance the app and give the class a new attribute _"name"_ 
with type "String". You can select the App-Class in 
_Visual Paradigm_ and press "Option-Shift-A" (on Mac) or
(Alt-Shift-A) on Windows/Linux all together. Then type
`name: String` and click outside the class.

![vp-attributeAdded.png](images%2Fvp-attributeAdded.png)

When you start the generation again, you will see a new 
attribute in the AppBase class together with simple getter and
setter methods.

```bash
cgv19 -m http://localhost:7001/de.spraener.my.app
cat src/main/java-gen/de/spraener/my/app/AppBase.java

//THIS FILE IS GENERATED AS LONG AS THIS LINE EXISTS
package de.spraener.my.app;


public class AppBase {

    private String name;

    public AppBase() {
        super();
    }

    public String getName() {
        return name;
    }


    public void setName( String value) {
        this.name = value;
    }

}
```

## Play around

The PoJo-Cartridge supports inheritance and relations. You can play around with it to get
a feeling of model driven development. Try:

* Add a second PoJo with attributes
* Draw a relation between App and the other PoJo
* Make a "extends" relation to a third PoJo
* Edit a PoJo in your IDE: 
  * Remove the _Protection_ line at the top ot the file
  * add some specific code
  * re-generate the project

between all these steps feel free to start generation. Here is an example of a more enhanced
model with its generated code:

![vp-enhancedModel.png](images%2Fvp-enhancedModel.png)

```log
.
├── build.gradle
├── de.spraener.my.app.oom
├── model
│   ├── my-app.vpp
├── settings.gradle
└── src
    └── main
        ├── java
        │   └── de
        │       └── spraener
        │           └── my
        │               └── app
        │                   ├── App.java
        │                   ├── Base.java
        │                   └── SecondPojo.java
        └── java-gen
            └── de
                └── spraener
                    └── my
                        └── app
                            ├── AppBase.java
                            ├── BaseBase.java
                            └── SecondPojoBase.java
```
