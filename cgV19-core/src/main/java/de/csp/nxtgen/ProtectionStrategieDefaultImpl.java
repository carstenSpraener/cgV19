package de.csp.nxtgen;

import java.io.*;

public class ProtectionStrategieDefaultImpl implements ProtectionStrategie {

    @Override
    public boolean isProtected(File f) {
        try {
            if( f.length()==0 ) {
                return false;
            }
            BufferedReader br = new BufferedReader( new FileReader(f));
            String line;
            int lineNr = 0;
            while( (line=br.readLine()) != null  && lineNr<5) {
                lineNr++;
                if( line.contains(GENERATED_LINE) ) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
