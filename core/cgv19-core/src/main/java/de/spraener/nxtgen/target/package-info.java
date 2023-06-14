/*
 * @startuml class CodeTarget
 * class CodeSection
 * class CodeSnippet {
 * Object aspect
 * ModelElement me
 * }
 * CodeTarget *--> "0..n" CodeSection
 * CodeSection *--> "0..n" CodeSnippet
 * CodeSnippet *---> "beforeMe (0..n)" CodeSnippet
 * CodeSnippet *---> "afterMe (0..n)" CodeSnippet
 * @enduml <h1>CodeTarget base code generators</h1>
 */
/**
 * A generator on base of a CodeTarget does not generate a String with the
 * code but a CodeTarget that can be modified by other CodeTarget base
 * generators. The goal is to make the result of one generator usable to
 * other generators. This can be the base of reusable generators in a
 * generator library.
 */
package de.spraener.nxtgen.target;
