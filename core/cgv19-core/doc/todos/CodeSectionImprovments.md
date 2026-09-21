# Umstellung auf das hierarchische, supplier-basierte Sektions-Modell

## Status quo

- `CodeSection` (`target/CodeSection.java`): flaches, snippet-orientiertes Interface.
  Alle konkreten Sektionen (`SimpleCodeSection`, `UniqueLineSection`,
  `NonEmptyPrefixedListSection`, `JavaImplementsCodeSection`, `PhpImplementsSection`)
  erben von der einzigen Basisklasse `AbstractCodeSection` (flache `List<CodeSnippet>`, UUID-Id).
- `CodeTarget`: flaches `LinkedHashMap<Object, CodeSection>` in Einfügereihenfolge;
  `addCodeSection` setzt den Key als Section-Id.
- Rendering: `CodeTargetToCodeConverter` (Hauptpfad via `CodeTargetCodeBlockAdapter`,
  Fallback in `GroovyCodeBlockImpl`) und `JavaRenderer` — beide flach:
  `getSectionsOrdered()` → `section.getSnippetsOrdered()`, ohne Indentierung, ohne Rekursion.

## Zielmodell (finalisierte Entscheidungen)

### 1. `CodeSection`-Interface Erweiterung

* `getOrCreateScope(String scopeName, Supplier<CodeSection> scopeSupplier)`
* `getScope(String scopeName)` für den Direktzugriff auf bereits existierende Sub-Sektionen
  (null, wenn nicht vorhanden).
* `getParent()` / `getChildren()` für Elternelement- und Kindelement-Navigation.
* `rendersChildrenInline()` (Default: `false`): steuert das Renderer-Verhalten für
  Kind-Scopes (siehe Punkt 4 und 6).
* **Entscheidung: alle als `default`-Methoden** im Interface (z. B. `getScope → null`,
  `getParent → null`, `getChildren → leere Liste`). Das Framework ist publiziert, aber
  wenig benutzt; Default-Methoden erhalten die binäre und quellarische Kompatibilität
  für externe Implementierer.

### 2. Composite-Fähigkeit in `AbstractCodeSection`

* **Keine** parallele Klasse (`CompositeCodeSection`) — die Composite-Fähigkeit kommt
  direkt in `AbstractCodeSection`, damit alle existierenden Sektions-Typen automatisch
  komponierbar sind.
* Kinder: `LinkedHashMap<String, CodeSection>` (deterministische Einfügereihenfolge),
  Zugriff synchronisiert. `getOrCreateScope` nutzt `computeIfAbsent` → der Supplier wird
  nur beim ersten Aufruf ausgeführt (deterministic Caching, thread-safe).
* Die Parent-Referenz wird bei der Kind-Erzeugung gesetzt; Scope-Id = `scopeName`
  (konsistent mit `CodeTarget.addCodeSection`, der den Key als Id setzt).

### 3. Rekursive `getSnippetsOrdered()` (Depth-First)

* Pre-Order: eigene Snippets zuerst, dann jedes Kind in Einfügereihenfolge (rekursiv).
* **Konsequenz für den Renderer:** Er darf die rekursive `getSnippetsOrdered()` nicht
  parallel mit eigener Kind-Traversierung nutzen (doppeltes Rendern). Deshalb kommt eine
  neue, additive „nur-eigene-Snippets“-Methode ins Interface (Default wirft
  `UnsupportedOperationException`, konkrete Implementierung in `AbstractCodeSection`), die
  der Renderer für den Inhalt einer Ebene verwendet.

### 4. Anpassung des `CodeTarget`-Renderers (`CodeTargetToCodeConverter`, `JavaRenderer`)

* Depth-First-Traversierung über `getChildren()`; jede Sektion rendert ihre eigenen
  Snippets (eigene-Snippets-Methode aus Punkt 3).
* Einrückungs-Vererbung: die Eltern-Sektion reicht ihr Indent-Level an die Kinder durch
  (+1 pro Ebene).
* **Inline-Scopes:** Sektionen mit `rendersChildrenInline() == true` (z. B.
  `NonEmptyPrefixedListSection`) rendern ihre gesamte rekursive Snippet-Liste
  (`getSnippetsOrdered()`, inkl. Prefix/Separator) als einen flachen Block auf dem
  Indent-Level der Sektion — ohne Rekursion in die Kinder, ohne +1-Indent, ohne
  zusätzliche Zeilenumbrüche. Die Kinder verhalten sich als reine logische Gruppierungen.
* **Backward Compatibility:** Tiefe 0 (alle heutigen flachen Sektionen) rendert exakt wie
  bisher — keine Indentierung, kein Extra-Whitespace. Bestehende Generations-Outputs
  bleiben unverändert.
* Marker (`withMarkers`) werden rekursiv an Scope-Grenzen ausgegeben.

### 5. Owner-Auflösung für `insertBefore` / `insertAfter` / `replace`

* Liegt das Ziel-Snippet nicht in der eigenen Liste (`indexOf == -1`), wird rekursiv in
  den Kindern gesucht und an die Sektion delegiert, die das Snippet enthält.
* Die öffentliche API bleibt stabil; `CodeSnippet` wird **nicht** geändert (kein Owner-Feld).
* Schützt u. a. `ForAspectDSL` (`beforeSnippet`/`afterSnippet`), die über die jetzt
  rekursive `getSnippetsOrdered()` Snippets in Kind-Scopes finden kann.

### 6. Besondere Sektionen

* `NonEmptyPrefixedListSection`: strikte Aggregation — Prefix (z. B. `implements `) und
  Separator (`, `) werden auf das Ergebnis der rekursiven Snippet-Liste angewendet.
  Kind-Scopes erzwingen keine eigenen Einrückungen oder Zeilenumbrüche, sondern verhalten
  sich wie reine logische Gruppierungen (`rendersChildrenInline() == true`).
* `UniqueLineSection`: Dedupe bleibt add-time-lokal (eigene Snippets). Duplikate zwischen
  Eltern- und Kind-Scope sind eine dokumentierte Kante (kein rekursives Dedupe).

### 7. Standard-Factories & Enums (rein additiv)

* `SectionName`: Enum für generische Standard-Sektionen (`PREAMBLE`, `BEFORE_OPERATION`,
  `IN_OPERATION` etc.).
* `StandardSections`: Factory-Klasse mit wiederverwendbaren `Supplier`-Templates für
  gängige Code-Blöcke (Methoden, Klassen-Körper, Properties).
* Abgrenzung zum bestehenden `SectionType`: `SectionType` = Fabrik für Sektions-*Typen*
  (SIMPLE / UNIQUE_LINES / PREFIXED_LIST), `SectionName` = Sektions-*Namen*.

## Impact auf cgv19

### Core (`cgv19-core`, Paket `de.spraener.nxtgen.target`)

| Datei | Änderung |
|---|---|
| `CodeSection.java` | +6 `default`-Methoden (Scope, Navigation, eigene Snippets, Inline-Flag) |
| `AbstractCodeSection.java` | children-Map, parent, Scope-Methoden, rekursive `getSnippetsOrdered()`, eigene-Snippets-Methode, Owner-Auflösung in insert/replace |
| `CodeTarget.java` | Pfad-Resolution in `getSection()` (`'/'`-getrennt) |
| `CodeTargetToCodeConverter.java` | rekursive Traversierung + Indent-Erbschaft, Inline-Scopes flach |
| `JavaRenderer.java` | dito |
| `NonEmptyPrefixedListSection.java`, `UniqueLineSection.java` | Anpassung an die rekursive Semantik (Punkt 6) |
| neu: `StandardSections`, `SectionName` | additiv |

### Cartridges (cgv19-pojo, cgv19-symfony, cgv19-javalin, …)

* Keine Code-Änderungen für flache Nutzung (`getSection(...).add(...)`, Aspect-Queries
  funktionieren weiter).
* Rebuild gegen Core 24.1.x erforderlich (Cartridges referenzieren im buildscript-classpath
  noch 23.1.x — Versions-Mismatch, siehe AGENTS.md).

### `CodeTarget`

* **Pfad-Syntax für `getSection()`:** `'/'`-getrennt, wie Unix-Pfade. Das erste Segment
  ist der Top-Level-Section-Key, die folgenden Segmente werden als Scope-Namen via
  `getScope()` aufgelöst (z. B. `"METHODS/IN_OPERATION"`). Fehlt ein Segment, liefert
  `getSection` null. `addCodeSection` bleibt Top-Level-only; Keys mit `'/'` sind
  reserviert (Pfad-Ambiguität).
* **Id-Fallback:** Wird das erste Segment als Key nicht gefunden, wird es gegen die
  Section-Ids abgeglichen — so sind Sektionen mit Enum-Keys (z. B.
  `JavaSections.METHODS`) über ihren Namen erreichbar (`"METHODS/..."`). Exakte
  Key-Treffer haben Vorrang.

## Tests

* Bestehende Suite muss grün bleiben (Backward Compatibility): `SimpleCodeSectionTest`,
  `UniqueLineSectionTest`, `JavaRendererTest`, `CodeTargetRendererTest`, PoJo-Tests,
  `GroovyCodeBlockImplTest`.
* Neu: Supplier wird nur einmal ausgeführt (deterministic Caching), Thread-Safety von
  `getOrCreateScope`, Depth-First-Reihenfolge in `getSnippetsOrdered()`,
  Parent/Child-Navigation, rekursives Rendering mit Indent (Tiefe 0 unverändert),
  Owner-Auflösung (insert/replace in Kind-Scopes), `StandardSections`-Factories,
  Pfad-Lookup in `CodeTarget.getSection()`, Inline-Rendering von Kind-Scopes unter
  `NonEmptyPrefixedListSection` (kein Indent, kein Zeilenumbruch).

## Status

Implementiert (Core + `examples/cgv19-codetarget` als Demo). Die 12 vorbestehenden
Test-Fehlschläge in `cgv19-core` betreffen andere, noch fehlende DSL-Features
(`addSection`, `first`) und sind unabhängig von diesem Modell.
