package de.spraener.nxtgen.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class CGV19 implements Plugin<Project> {

    void apply( Project p ) {
        def extension = p.extensions.create('cgV19', CGV19Extension)

        p.buildscript.configurations.create('cgv19')
        p.configurations.create('cartridge')
        p.task('generate').doLast {
            println 'Generating with cgV19 and model: '+extension.model;
            String cgClasspath = "";
            p.configurations.cartridge.resolve()forEach( {
                if( !cgClasspath.empty ) {
                    cgClasspath += ";"
                }
                cgClasspath+=it.getPath();
            })
            String nextGen = "de.spraener.nxtgen.NextGen";
            p.javaexec {
                mainClass = nextGen;
                classpath = p.configurations.getByName('cartridge')
                args extension.model
            }
        }
    }
}
