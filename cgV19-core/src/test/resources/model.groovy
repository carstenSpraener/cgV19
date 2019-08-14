import de.csp.nxtgen.model.Model
import de.csp.nxtgen.groovy.ModelDSL

Model model = ModelDSL.make {
    mPackage {
        name 'de.csp.persistence.dto'
        mClass {
            name 'Person'
            stereotype 'Entity', {
                taggedValue 'dbTable', 'PERSON';
            }
            pkgName ''

            mAttribute {
                name 'vorname'
                stereotype 'dbField'
                type 'String'
            }

            mReference {
                name 'address'
                type 'Address'
                stereotype 'dbReference'
                taggedValue 'quantity', 'ONE_TO_MANY'
            }
        }

        mClass {
            name 'Address'
            stereotype 'Entity'
            taggedValue 'dbTable', 'ADR'

            mAttribute {
                name 'street'
                type 'String'
                stereotype 'dbField'
                taggedValue 'dbType', 'CHAR(32)'
                taggedValue 'dbFieldName', 'ADR_STREET'
            }

            mAttribute {
                name 'zipCd'
                type 'String'
                stereotype 'dbField'
                taggedValue 'dbType', 'CHAR(10)'
                taggedValue 'dbFieldName', 'ADR_ZIP'
            }

            mAttribute {
                name 'city'
                type 'String'
                stereotype 'dbField'
                taggedValue 'dbType', 'CHAR(32)'
                taggedValue 'dbFieldName', 'ADR_CITY'
            }
        }
    }
};

model.toString();
