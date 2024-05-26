package de.spraener.nxtgen.laravel.tools;

import javax.naming.LinkRef;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FilePatcher implements AutoCloseable {
    private String filePath;
    private List<String> lines = new ArrayList<>();
    private int cursor = 0;

    public static class LineRef {
        int lineNr;
        String content;

        public LineRef(int lineNr, String content) {
            this.lineNr = lineNr;
            this.content = content;
        }

        public int getLineNr() {
            return lineNr;
        }

        public String getContent() {
            return content;
        }
    }

    public FilePatcher(String filePath) throws IOException {
        this.filePath = filePath;
        init();
    }

   private void init() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(this.filePath)) ) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
   }

   public FilePatcher gotoLine(int line) {
        if( line < 0 ) {
            line = 0;
        }
        if( line >= lines.size() ) {
            line = lines.size()-1;
        }
        this.cursor = line;
        return this;
    }

    public String readLine() {
        return this.lines.get(this.cursor);
    }

    public String readLine( int lineNr ) {
        this.gotoLine(lineNr);
        return this.readLine();
    }

    public FilePatcher gotoMatching(String regex) {
        Pattern p = Pattern.compile(regex);
        for( int idx=this.cursor; idx<lines.size(); idx++ ) {
            if (p.matcher(lines.get(idx)).matches()) {
                this.cursor = idx;
                break;
            }
        }
        return this;
    }

    public FilePatcher replaceCurrentWith( String newContent ) {
        this.lines.set(this.cursor, newContent);
        return this;
    }

    public List<LineRef> findMatching(String regex) {
        Pattern p = Pattern.compile(regex);
        List<LineRef> result = new ArrayList<>();
        for( int idx=0; idx<lines.size(); idx++ ) {
            if( p.matcher(lines.get(idx)).matches() ) {
                result.add(new LineRef(idx, lines.get(idx)));
            }
        }
        return result;
    }

    public void replaceOrAppend(String regEx, String formatStr, String... args) {
        String newLine = String.format(formatStr, (Object[])args);
        List<LineRef> matchingLines = findMatching(regEx);
        if( matchingLines.isEmpty() ) {
            this.lines.add(newLine);
        } else {
            for( LineRef lr : matchingLines ) {
                this.lines.set(lr.lineNr, newLine);
            }
        }

    }

    @Override
    public void close() throws IOException{
        try(PrintWriter pw = new PrintWriter(new FileWriter(this.filePath))) {
            for( String line: this.lines) {
                pw.println(line);
            }
            pw.flush();
        }
    }
}
