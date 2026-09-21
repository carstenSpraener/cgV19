# Spezifikation: Operation-CodeSection (Before/In/Tail) mit Pfad-API

## Motivation

Anwender wollen Operationen (z. B. `MOperation`) als hierarchische Sektionen erzeugen:
eine Operation-Sektion mit drei Sub-Scopes (`Before` / `In` / `Tail`), die **einmalig**
angelegt und über Pfade adressiert wird:

```java
String opName = mOperation.getName();
CodeSection csOperation = target.getOrCreate("OPERATIONS/" + opName, StandardSections.operation());
csOperation.insert("BEFORE_OPERATION", snippet);
```

## Ziel / Nicht-Ziel

**Ziel:** Zwei additive Convenience-Methoden über dem bestehenden Scope-Modell:
Pfad-basiertes `getOrCreate` (Target-Ebene) und Pfad-basiertes `insert`
(Sektions-Ebene). Die „Before/In/Tail“-Sektion selbst wird **nicht** neu gebaut,
sondern als `StandardSections.operation()` wiederverwendet.

**Nicht-Ziel:** Änderungen am bestehenden Sektions-Modell, neue Rendering-Pfade,
DSL-Änderungen, rekursives Dedupe.

## Ist-Zustand (Wiederverwendung)

| Baustein | Status |
|---|---|
| Before/In/Tail-Sektion | vorhanden: `StandardSections.operation()` → `SimpleCodeSection` mit Sub-Scopes `BEFORE_OPERATION`, `IN_OPERATION`, `AFTER_OPERATION` |
| Get-or-create (1 Segment) | vorhanden: `CodeSection.getOrCreateScope(name, supplier)` (`computeIfAbsent`, Supplier läuft 1×, Kind-Id = Name, Parent gesetzt) |
| Pfad-Auflösung (lesen) | vorhanden: `CodeTarget.getSection("A/B/C")` (1. Segment = Key, Fallback Id; Rest via `getScope`) |
| Pfad-`getOrCreate` | **fehlt** |
| Pfad-`insert` | **fehlt** |

## API

### 1. `CodeTarget.getOrCreate(String path, Supplier<CodeSection> supplier)` (neu)

Erzeugt die Sektion am Pfad, falls sie nicht existiert; liefert andernfalls die
bestehende. Supplier läuft genau einmal (deterministic Caching).

| Pfad | Verhalten |
|---|---|
| `"NAME"` (ohne `/`) | Top-Level: existiert (Key **oder** Id) → zurückgeben. Sonst Supplier ausführen, `setId("NAME")`, an das Ende der ToC hängen. |
| `"A/B"` | Parent `A` auflösen (Key, Fallback Id — wie `getSection`). Parent fehlt → `IllegalArgumentException` mit Pfad im Message. Sonst `A.getOrCreateScope("B", supplier)`. |
| `"A/B/C"` | rekursiv: `A` → `getOrCreateScope("B", …)`? **Nein** — nur das **letzte** Segment wird via `getOrCreateScope` erzeugt; Zwischen-Segmente müssen existieren (sonst Exception). |
| `"/A/B"` | führender Slash wird normalisiert (siehe Pfad-Semantik). |

- **Thread-Safety:** Methode `synchronized` (mySectionMap ist eine unsichere `LinkedHashMap`).
- **Return:** die (ggf. neu erzeugte) Sektion am Pfad.

### 2. `CodeSection.insert(String path, CodeSnippet snippet)` (neu)

Fügt das Snippet **ans Ende** der per Pfad adressierten Sub-Sektion ein.

- Interface: `default`-Methode, wirft `UnsupportedOperationException`
  (konsistent mit `getOrCreateScope`). Implementierung in `AbstractCodeSection`.
- Pfad-Auflösung relativ zu dieser Sektion: Segmente via `getScope` abwandern.
  **Jedes** fehlende Segment → `IllegalArgumentException` (keine Auto-Erzeugung —
  Erzeugung gehört zu `getOrCreate`).
- Aspect/Element: keine Sonderbehandlung; der Snippet-Konstruktor übernimmt sie
  wie üblich aus dem `CodeTargetContext`.
- **Return:** die Sektion, die das Snippet erhalten hat (Chaining).

```java
// CodeSection.java
default CodeSection insert(String path, CodeSnippet snippet) {
    throw new UnsupportedOperationException("Path-based insertion is not supported by " + getClass().getSimpleName());
}

// AbstractCodeSection.java (Kern der Implementierung)
@Override
public CodeSection insert(String path, CodeSnippet snippet) {
    CodeSection target = resolveScopePath(path);   // private Helper, wirft bei fehlendem Segment
    target.add(snippet);
    return target;
}
```

### 3. Pfad-Semantik (gemeinsam für `getSection`, `getOrCreate`, `insert`)

- `'/'`-getrennt, Segmente = Scope-Namen bzw. Top-Level-Key/Id.
- **Führender Slash:** wird toleriert und vor der Aufspaltung entfernt
  (`"/A/B"` ≡ `"A/B"`). Gemeinsamer privater Helper in `CodeTarget`
  (`normalizePath`), den `getSection` und `getOrCreate` nutzen; in
  `AbstractCodeSection` analog für `insert`.
- Effekt auf `getSection`: Pfade mit führendem Slash liefern heute `null`,
  danach die aufgelöste Sektion — strikt eine Erweiterung, kein Break.

### 4. Naming: `Tail` vs. `AFTER_OPERATION` (Entscheidung)

**Empfehlung:** Bestehende Namen behalten (`BEFORE_OPERATION` / `IN_OPERATION` /
`AFTER_OPERATION`). „Tail“ ist semantisch `AFTER_OPERATION`; die Namen sind bereits
im dokumentierten Modell (`SectionName`, `StandardSections.operation()`).

**Option (falls wörtliche Namen gewünscht):** `SectionName.TAIL` ergänzen +
Factory-Variante `StandardSections.operationTail()` mit Scopes `BEFORE` / `IN` /
`TAIL`. Rein additiv, 10 Zeilen.

## Rendering-Konvention (dokumentieren, kein Code)

Pre-Order-Rendering (eigene Snippets **zuerst**, dann Kinder) erzwingt das Layout:

| Inhalt | Wo |
|---|---|
| Signatur + öffnende Klammer (z. B. `public void foo() {`) | eigene Snippets der Operation-Sektion |
| Vor-Body (Guards, Annotationen) | `BEFORE_OPERATION` |
| Body | `IN_OPERATION` |
| Nach-Body **inkl. schließender Klammer** | `AFTER_OPERATION` (letzte Zeile) |

Die schließende Klammer darf **nicht** in die eigenen Snippets, sonst rendert sie
vor den Kindern. Indent: `OPERATIONS` als **Top-Level-Sektion** anlegen (wie
`METHODS`) → Signatur auf Ebene 1, Body-Scopes auf Ebene 2 (Java: 4/8 Spaces).
`rendersChildrenInline()` bleibt `false`.

## Backward Compatibility

- Alle Änderungen rein additiv (neue Methoden, Default-Implementierungen) →
  binär- und quellarisch kompatibel.
- `getSection` mit führendem Slash: `null` → aufgelöste Sektion (Erweiterung).
- Tiefe 0 / flache Sektionen: Rendering unverändert.

## Tests (neu, `cgv19-core`)

| Test | Erwartung |
|---|---|
| `getOrCreate` 1 Segment, neu | Sektion existiert, Id = Name, Supplier lief 1× |
| `getOrCreate` 1 Segment, existierend (Key **und** Id-Fall) | dieselbe Instanz, Supplier lief 0× |
| `getOrCreate` `"A/B"`, Parent fehlt | `IllegalArgumentException` mit Pfad |
| `getOrCreate` `"A/B"`, Parent da, Kind neu | Kind-Id = `B`, Parent-Ref. gesetzt, Supplier 1× |
| `getOrCreate` mit führendem Slash | identisch zum Pfad ohne Slash |
| `insert` 1 Segment, existierend | Snippet am Ende der Sub-Sektion, Return = Sub-Sektion |
| `insert` `"A/B"` mit fehlendem `B` | `IllegalArgumentException` (keine Auto-Erzeugung) |
| `insert` mit führendem Slash | wie ohne |
| `getSection("/A/B")` | löst auf (Regressionstest für Normalisierung) |
| Rendering: Operation mit Signatur + Before/In/Tail | erwartete Indent-Stufen, Klammer am Ende |
| Thread-Safety `getOrCreate` (Parallel-Test) | Supplier genau 1×, konsistente Instanz |

Bestehende Suite (135 Tests) bleibt grün.

## Ausdrücklich nicht im Scope

- „Trailing own snippets“ (eigene Snippets nach den Kindern rendern) — würde das
  Pre-Order-Modell brechen; falls nötig später als eigenes Feature.
- Auto-Erzeugung fehlender Scopes durch `insert`.
- Groovy-DSL-Erweiterungen (Target-Level-Pfade decken `to`/`first` bereits ab).
- Rekursives Dedupe über Scope-Grenzen.

## Status

Implementiert: `CodeTarget.getOrCreate(path, supplier)`, `CodeSection.insert(path, snippet)`
(Default wirft UOE, Implementierung in `AbstractCodeSection`), Pfad-Normalisierung
(führender Slash) in `getSection`/`getOrCreate`/`insert`. Tests:
`CodeTargetGetOrCreateTest` (11) und `CodeSectionPathInsertTest` (6), Suite grün
(152 Tests). Naming-Entscheidung: bestehende `BEFORE_OPERATION`/`IN_OPERATION`/
`AFTER_OPERATION` beibehalten, `TAIL`-Alias nicht angelegt.
