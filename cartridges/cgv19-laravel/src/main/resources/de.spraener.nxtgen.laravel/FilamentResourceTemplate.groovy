import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.laravel.CreateRelationManager
import de.spraener.nxtgen.laravel.CreateResourceInfrastructure
import de.spraener.nxtgen.laravel.LaravelHelper
import de.spraener.nxtgen.oom.cartridge.GeneratorGapTransformation
import de.spraener.nxtgen.oom.model.MAttribute
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
MClass orgClass = GeneratorGapTransformation.getOriginalClass(mClass);
MClass modelClass = CreateResourceInfrastructure.getModelClass(orgClass);

String namespace = LaravelHelper.toNameSpace(mClass);
String resource = namespace+"\\"+mClass.getName();
String modelPath = LaravelHelper.toNameSpace(modelClass);
String resourceClass = mClass.getName();
String formSchema = "            //";
String tableColumns = "            //";
String tableFilters = "            //";
String tableActions = "            Tables\\Actions\\EditAction::make(),";
String tableBulkActions = "            //";
String tableEmptyStateActions = "            //";
String relations = "";
String eloquentQuery = "";

String pages(MClass mc, MClass modelClass) {
    String modelName = modelClass.getName();
    String plural = LaravelHelper.toPlural(modelName);
    return """            'index'=> Pages\\List${plural}::route('/'),
            'create' => Pages\\Create${modelName}::route('/create'),
            'edit' =>  Pages\\Edit${modelName}::route('/{record}/edit'),
""";
}

String generateFormSchema(MClass mc) {
    StringBuilder sb = new StringBuilder();
    for(MAttribute a : mc.getAttributes() ) {
        String formType = LaravelHelper.Attributes.toFormType(a);
        String lable = LaravelHelper.Attributes.getLabel(a);
        String additionalSettings = "";
        if( LaravelHelper.Attributes.isNummeric(a) ) {
            additionalSettings += "\n                    ->numeric()";
        }
        if( !LaravelHelper.Attributes.isNullable(a) ) {
            additionalSettings += "\n                    ->required()";
        }
        sb.append("""                ${formType}::make('${a.getName()}')
                    ->label('${lable}')$additionalSettings
                ,
""");
    }
    return sb.toString();
}

String generateTableColumns(MClass mc) {
    StringBuilder sb = new StringBuilder();
    for(MAttribute a : mc.getAttributes() ) {
        String columnType = LaravelHelper.Attributes.toColumnType(a);
        String toggleHiddenDefault = "";
        if( LaravelHelper.Attributes.isDetailOnly(a) ) {
            toggleHiddenDefault = "isToggledHiddenByDefault:true";
        }
        String dateFormat = "";
        if( LaravelHelper.Attributes.isDateAttribute(a) ) {
            dateFormat = "\n                    ->date()"
        }
        String lable = LaravelHelper.Attributes.getLabel(a);
        sb.append("""                ${columnType}::make('${a.getName()}')
                    ->toggleable(${toggleHiddenDefault})${dateFormat}
                    ->label('${lable}')
                    ->sortable()
                    ->searchable()
                ,
""");
    }
    return sb.toString();
}

"""<?php
//${ProtectionStrategieDefaultImpl.GENERATED_LINE}

namespace ${namespace};

use ${resource}\\Pages;
use ${resource}\\RelationManagers;
use ${modelPath}\\${modelClass.getName()};
use Filament\\Forms;
use Filament\\Forms\\Form;
use Filament\\Resources\\Resource;
use Filament\\Forms\\Components\\TextInput;
use Filament\\Forms\\Components\\Checkbox;
use Filament\\Forms\\Components\\DatePicker;
use Filament\\Forms\\Components\\DateTimePicker;
use Filament\\Forms\\Components\\ColorPicker;
use Filament\\Forms\\Components\\MarkdownEditor;

use Filament\\Tables;
use Filament\\Tables\\Columns\\TextColumn;
use Filament\\Tables\\Columns\\CheckboxColumn;
use Filament\\Tables\\Columns\\ColorColumn;
use Filament\\Tables\\Table;
use Illuminate\\Database\\Eloquent\\Builder;
use Illuminate\\Database\\Eloquent\\SoftDeletingScope;

class ${resourceClass} extends Resource
{
    protected static ?string \$model = ${modelClass.getName()}::class;

    protected static ?string \$navigationIcon = 'heroicon-o-rectangle-stack';

    public static function form(Form \$form): Form
    {
        return \$form
            ->schema([
${generateFormSchema(modelClass)}
            ]);
    }

    public static function table(Table \$table): Table
    {
        return \$table
            ->columns([
${generateTableColumns(modelClass)}
            ])
            ->filters([
${tableFilters}
            ])
            ->actions([
${tableActions}
            ])
            ->bulkActions([
                Tables\\Actions\\BulkActionGroup::make([
${tableBulkActions}
                ]),
            ])
            ->emptyStateActions([
${tableEmptyStateActions}
            ]);
    }
${relations}
    public static function getPages(): array
    {
        return [
${pages(mClass, modelClass)}
        ];
    }${eloquentQuery}
${generateRelations(mClass)}
}
"""

static String generateRelations(MClass mClass) {
    List<MClass> relationManagers = CreateRelationManager.getRelationManagers(mClass);
    if( relationManagers==null || relationManagers.size()==0 ) {
        return "";
    }
    StringBuilder rmList = new StringBuilder();
    for( MClass rm : relationManagers ) {
        rmList.append("            \\${LaravelHelper.toNameSpace(rm)}\\${rm.getName()}::class\n")
    }
    return """
    public static function getRelations(): array {
        return [
${rmList.toString()}
        ];
    }
"""
}
