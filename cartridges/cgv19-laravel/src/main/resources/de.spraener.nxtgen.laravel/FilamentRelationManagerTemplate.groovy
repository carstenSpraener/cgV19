import de.spraener.nxtgen.ProtectionStrategieDefaultImpl
import de.spraener.nxtgen.laravel.CreateRelationManager
import de.spraener.nxtgen.laravel.LaravelHelper
import de.spraener.nxtgen.laravel.LaravelStereotypes
import de.spraener.nxtgen.laravel.scripts.FilamentFormsAndTables
import de.spraener.nxtgen.oom.cartridge.JavaHelper
import de.spraener.nxtgen.oom.model.MClass
import de.spraener.nxtgen.oom.model.OOModel

MClass mClass = this.getProperty("modelElement");
OOModel model = mClass.getModel();
MClass targetModel = CreateRelationManager.getTargetModel(mClass);

String namespace = LaravelHelper.toNameSpace(mClass);
String relationship = mClass.getTaggedValue(LaravelStereotypes.FILAMENTRELATIONMANAGER.getName(), "relationName");

"""<?php
//${ProtectionStrategieDefaultImpl.GENERATED_LINE}

namespace ${namespace};

use Filament\\Forms;
use Filament\\Forms\\Form;
use Filament\\Resources\\RelationManagers\\RelationManager;
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

class ${mClass.getName()} extends RelationManager
{
    protected static string \$relationship = '${relationship}';

    public function form(Form \$form): Form
    {
        return \$form
            ->schema([
${FilamentFormsAndTables.generateFormSchema(targetModel)}
            ]);
    }

    public function table(Table \$table): Table
    {
        return \$table
            ->recordTitleAttribute('${JavaHelper.firstToUpperCase(relationship)}')
            ->columns([
${FilamentFormsAndTables.generateTableColumns(targetModel)}
            ])
            ->filters([
               // Specify filters here
            ])
            ->headerActions([
               // Specify header actions here
            ])
            ->actions([
                Tables\\Actions\\EditAction::make(),
                Tables\\Actions\\DeleteAction::make(),
            ])
            ->bulkActions([
                Tables\\Actions\\BulkActionGroup::make([
                    Tables\\Actions\\DeleteBulkAction::make(),
                ]),
            ])
            ->emptyStateActions([
                Tables\\Actions\\CreateAction::make(),
            ]) // Modify query here
            ;
    }
}

"""
