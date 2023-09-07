package de.spraener.nxtgen.laravel.scripts;

import de.spraener.nxtgen.laravel.LaravelHelper;
import de.spraener.nxtgen.laravel.tools.MustacheSnippet;
import de.spraener.nxtgen.oom.model.MAttribute;
import de.spraener.nxtgen.oom.model.MClass;

import static de.spraener.nxtgen.laravel.LaravelHelper.Attributes.getLabel;
import static de.spraener.nxtgen.laravel.LaravelHelper.Attributes.isDetailOnly;

public class FilamentFormsAndTables {
    private static final MustacheSnippet columnDefinition = new MustacheSnippet("""
                         {{columnType}}::make('{{attrName}}')
                             ->toggleable({{toggleHiddenDefault}})
                             ->label('{{lable}}')
                             ->sortable()
                             ->searchable(){{&asDate}}
                         ,
            """);

    private static final MustacheSnippet formSchema = new MustacheSnippet(
    """
                {{formType}}::make('{{attrName}}')
                    ->label('{{lable}}'){{&additionalSettings}}
                ,
    """);

    public static String generateFormSchema(MClass mc) {
        StringBuilder sb = new StringBuilder();
        for (MAttribute a : mc.getAttributes()) {
            String formType = LaravelHelper.Attributes.toFormType(a);
            String lable = getLabel(a);
            String additionalSettings = "";
            if (LaravelHelper.Attributes.isNummeric(a)) {
                additionalSettings += "\n                    ->numeric()";
            }
            if (!LaravelHelper.Attributes.isNullable(a)) {
                additionalSettings += "\n                    ->required()";
            }
            sb.append(formSchema.evaluateWith(
                    "formType="+formType,
                    "attrName="+a.getName(),
                    "lable="+lable,
                    "additionalSettings="+additionalSettings
            ));
        }
        return sb.toString();
    }


    public static String generateTableColumns(MClass mc) {
        StringBuilder sb = new StringBuilder();
        for (MAttribute a : mc.getAttributes()) {
            String columnType = LaravelHelper.Attributes.toColumnType(a);
            String toggleHiddenDefault = "";
            if (isDetailOnly(a)) {
                toggleHiddenDefault = "isToggledHiddenByDefault:true";
            }
            String asDate = "";
            if (LaravelHelper.Attributes.isDateAttribute(a)) {
                asDate = "->date()";
            }
            String lable = getLabel(a);
            sb.append(columnDefinition.evaluateWith(
                    "columnType=" + columnType,
                    "attrName=" + a.getName(),
                    "toggleHiddenDefault=" + toggleHiddenDefault,
                    "asDate=" + asDate,
                    "lable=" + lable
            ));
        }
        return sb.toString();
    }

}

