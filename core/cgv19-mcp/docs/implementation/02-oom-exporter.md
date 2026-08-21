# 02-oom-exporter.md

## Ziel
`OOMExporter` in `cgv19-oom` erstellen, der ein `OOModel` → `.oom` Groovy-DSL serialisiert.

## Klassen
- `de.spraener.nxtgen.oom.model.OOMExporter`

## API
```java
public class OOMExporter {
    String export(OOModel model);
    void export(OOModel model, Path target);
}
```

## Tests (Red-Green)
1. **RED**: `OOMExporterTest.shouldExportEmptyModel()`
   - Erwartet: `"import de.spraener.nxtgen.groovy.ModelDSL\n\nModelDSL.make {\n}"`
   
2. **RED**: `OOMExporterTest.shouldExportPackage()`
   - Modell mit einem Package → `.oom` mit `mPackage { name '...' }`

3. **RED**: `OOMExporterTest.shouldExportClassWithStereotype()`
   - MClass mit Stereotype → `.oom` mit `stereotype '...'`

4. **RED**: `OOMExporterTest.shouldExportClassWithAttributes()`
   - MClass mit MAttribute → `.oom` mit `mAttribute { ... }`

5. **RED**: `OOMExporterTest.shouldExportClassWithOperations()`
   - MClass mit MOperation → `.oom` mit `mOperation { ... }`

6. **RED**: `OOMExporterTest.shouldExportTaggedValues()`
   - Stereotype mit TaggedValues → `.oom` mit `taggedValue 'key', 'value'`

7. **RED**: `OOMExporterTest.shouldExportNestedPackages()`
   - Verschachtelte Packages → korrekt eingerücktes `.oom`

## Implementierungshinweise
- `.oom`-Format ist Groovy-DSL (siehe `META-DSL.oom`, `DSL.oom`)
- Jedes ModelElement hat `getName()`, `getMetaType()`, `getStereotypes()`, `getProperties()`
- `MClass` hat `getAttributes()`, `getOperations()`, `getReferences()`, `getPackage()`
- `MPackage` hat `getClasses()`, `getPackages()`, `getFQName()`
- Einrückung: 2 Leerzeichen pro Ebene
- String-Werte mit `'''...'''` escapen (Groovy multiline strings)

## Verifikation
```bash
./gradlew :cgv19-oom:test --tests OOMExporterTest
```
