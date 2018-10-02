package data.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wolf
 */
public class TextFile extends File {

    //</editor-fold>
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    private File file;
    private String encoding = "UTF8";

    private Scanner scanner;
    private RandomAccessFile randomFile;
    private BufferedReader reader;
    private BufferedWriter writer;

    private List<String> uniqueList;
    private int maxLines = 1024;

    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Constructors">
    public TextFile(String filename) {
        super(filename);
        this.file = new File(filename);
    }

    public TextFile(String filename, String encoding) {
        this(filename);
        this.encoding = encoding;
    }

    public TextFile(File file) {
        super(file.getAbsolutePath());
        this.file = file;
    }

    public TextFile(File file, String encoding) {
        this(file);
        this.encoding = encoding;
    }

    //</editor-fold>
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="List Prefixes">
    public File[] listPrefixedFiles(final String prefix) {

        return listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(prefix);
            }
        });

    }

    //</editor-fold>
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Text Merger (Line)">

    /**
     * A line should contain at least count times split pattern If no such case
     * exists it is merged with next line until count is satisfied
     *
     * @param pattern
     * @param count
     * @return new set of lines
     */
    public List<String> writeLinesBySplitPattern(String pattern, int count) {
        List<String> lines = readLines();
        List<String> newLines = new ArrayList<>();
        for (int i = 0; i < lines.size() - 1; i++) {
            String line = lines.get(i);
            int splitCount = line.split(pattern).length;
            if (splitCount == count) {
                newLines.add(line);
            } else if (splitCount < count) {
                String newLine = line + " " + lines.get(i + 1);
                lines.set(i, newLine);
                lines.remove(i + 1);
                i--;
            }
        }

        (new TextFile(this.getFilename())).delete();
        (new TextFile(this.getFilename())).writeFullTextUTF8(newLines);

        return newLines;
    }


    //</editor-fold>
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Filename Fixer">
    public File[] fixFilenames(final String matches, String pattern, String replacer) {
        File[] files = listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.matches(matches);
            }
        });

        List<File> fileList = new ArrayList<>();
        for (File f : files) {
            String name = f.getName();
            name = name.replaceAll(pattern, replacer);
            File newNameFile = new File(name);
            f.renameTo(newNameFile);
            fileList.add(newNameFile);
        }

        return fileList.toArray(new File[0]);
    }

    //</editor-fold>
    //////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Exists and Counts">
    public static boolean checkFilenameExists(String filename) {
        return (new File(filename)).exists();
    }

    //</editor-fold>
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    public void clearfile() {
        openBufferWrite();
        closeBufferWrite();
    }

    public String getFilename() {
        return file.getName();
    }

    public String getFolder() {
        return file.getParent();
    }

    public String takeNextFilename() {
        String name = getFilename();
        String parent = getFolder();
        int extensionIndex = name.lastIndexOf(".");
        int seperatingIndex = name.lastIndexOf("-");

        if (extensionIndex == -1) {
            Integer number = getFileNumber(parent, name) + 1;
            name = name + "-" + number.toString();
        } else if (seperatingIndex != -1 && extensionIndex != -1) {
            Integer number = getFileNumber(parent, name.substring(0, seperatingIndex)) + 1;
            name = name.substring(0, seperatingIndex) + "-" + number.toString() + name.substring(extensionIndex);
        } else if (extensionIndex != -1) {
            Integer number = getFileNumber(parent, name.substring(0, extensionIndex)) + 1;
            name = name.substring(0, extensionIndex) + "-" + number.toString() + name.substring(extensionIndex);
        }

        String newName = parent + "/" + name;
        file = new File(newName);
        return newName;
    }

    public String nextFilename() {
        String name = getFilename();
        String parent = getFolder();
        String newName;
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        format.applyPattern("0000000");

        int extensionIndex = name.lastIndexOf(".");
        int numberIndex = name.lastIndexOf("-");

        if (numberIndex == -1 && extensionIndex == -1) {
            if (name.matches("\\d+")) {
                String possibleNumber = name;
                String nextNumber = format.format(Integer.parseInt(possibleNumber) + 1);
                newName = nextNumber;
            } else {
                newName = name + "-" + format.format(1);
            }
        } else if (extensionIndex == -1) {
            String possibleNumber = name.substring(numberIndex + 1);
            if (possibleNumber.matches("\\d+")) {

                String nextNumber = format.format(Integer.parseInt(possibleNumber) + 1);
                newName = name.substring(0, numberIndex) + "-" + nextNumber;
            } else {
                String nextNumber = format.format(1);
                newName = name + "-" + nextNumber;
            }
        } else if (numberIndex == -1) {
            String possibleNumber = name.substring(0, extensionIndex);
            if (possibleNumber.matches("\\d+")) {
                String nextNumber = format.format(Integer.parseInt(possibleNumber) + 1);
                newName = nextNumber + name.substring(extensionIndex);

            } else {
                newName = name.substring(0, extensionIndex) + format.format(1) + name.substring(extensionIndex);
            }
        } else {
            String possibleNumber = name.substring(numberIndex + 1, extensionIndex);
            if (possibleNumber.matches("\\d+")) {

                String nextNumber = format.format(Integer.parseInt(possibleNumber) + 1);
                newName = name.substring(0, numberIndex) + "-" + nextNumber + name.substring(extensionIndex);
            } else {
                String nextNumber = format.format(1);
                newName = name.substring(0, extensionIndex) + "-" + nextNumber + name.substring(extensionIndex);
            }
        }

        newName = parent + "/" + newName;
        file = new File(newName);

        if (file.exists()) {
            return nextFilename();
        }

        return newName;
    }

    public static String getFilename(String[] names) {
        String filename = "";
        for (String name : names) {
            filename += "/" + name;
        }

        return filename.toLowerCase();
    }

    public static String getFilename(String directory, String[] names) {
        String filename = directory;
        for (String name : names) {
            filename += "/" + name;
        }

        return filename.toLowerCase();
    }

    public static File[] getFiles(String directory) {
        return (new File(directory)).listFiles();
    }

    public static File[] getFiles(String directory, boolean trace) {
        List<File> fileList = new ArrayList<>();
        File[] list = (new File(directory)).listFiles();
        for (File file : list) {
            if (file.isDirectory()) {
                File[] files = getFiles(file.getPath(), trace);
                fileList.addAll(Arrays.asList(files));
            } else {
                fileList.add(file);
            }
        }

        return fileList.toArray(new File[0]);
    }

    public static List<File> getFileList(String directory) {
        return Arrays.asList(getFiles(directory));
    }


    public static List<File> getFilesSortedByDate(String directory, String afterFilename) {

        List<File> files = getFileList(directory);

        if (afterFilename != null) {
            Comparator<File> dateComparator = new Comparator<File>() {

                @Override
                public int compare(File o1, File o2) {
                    return Long.compare(o1.lastModified(), o2.lastModified());
                }
            };

            File afterFile = new File(afterFilename);

            if (afterFile.exists()) {
                Collections.sort(files, dateComparator);
                int afterIndex = files.indexOf(afterFile);
                if (afterIndex != -1) {
                    files = files.subList(afterIndex, files.size());
                }
            }
        }

        return files;
    }

    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Static NIO Faster">


    public static int moveFilesInFolder(String source, String destination) {
        int count = 0;
        (new File(destination)).mkdir();

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(source))) {
            for (Path path : directoryStream) {
                File f = path.toFile();
                String parentDirectory = f.getParent();
                String currentDirectory = f.getPath();

                if (!parentDirectory.contains(destination) && !currentDirectory.contains(destination)) {
                    if (f.isDirectory()) {
                        moveFilesInFolder(f.getPath(), destination);
                    } else if (!f.isDirectory()) {

                        File destinationFile = new File(destination + "/" + f.getName());
                        if (!destinationFile.exists()) {
                            Files.copy(path, destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("COPY:" + destinationFile.getPath());
                        }

                        Files.delete(path);
                        System.out.println("DELETE:" + f.getPath());
                    }

                    if (f.isDirectory() && !currentDirectory.contains(destination)) {
                        Files.delete(path);
                        System.out.println("DELETE:" + f.getPath());
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        return count;
    }

    public static int countFiles(String directory, long aboveKB) {
        int count = 0;
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
                File f = path.toFile();
                long size = f.length();
                if (size >= aboveKB) {
                    count++;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        return count;
    }

    public static Set<String> listFilenames(String directory, long aboveKB) {
        Set<String> fileSet = new HashSet<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
                File f = path.toFile();
                float size = ((float) f.length());

                if (size >= aboveKB) {
                    fileSet.add(path.toString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fileSet;
    }

    public static Set<File> listFiles(String directory, long aboveByte) {
        Set<File> fileSet = new HashSet<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory))) {
            for (Path path : directoryStream) {
                File f = path.toFile();
                float size = ((float) f.length());

                if (size >= aboveByte) {
                    fileSet.add(f);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fileSet;
    }

    public static String readText(File f) {
        String text = null;
        Charset charset = Charset.forName("UTF-8");
        try (BufferedReader reader = Files.newBufferedReader(f.toPath(), charset)) {
            String line = null;
            String value = "";
            while ((line = reader.readLine()) != null) {
                value += line + "\n";
            }
            text = value;
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        return text;
    }

    //  BUGGY
    //public static String readPartialText(File f) {
//        String text = "";
//        try {
//            RandomAccessFile file = new RandomAccessFile(f, "r");
//
//            try (FileChannel channel = file.getChannel()) {
//                MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
//                buffer.load();
//                char value = (char) buffer.get(0);
//                if (!Character.isDigit(value)) {
//                    for (int i = 0; i < buffer.limit(); i++) {
//                        text += (char) buffer.get(i);
//                    }
//                }
//            } catch (IOException ex) {
//                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return text;
//    }
    public static void writeText(File f, String text) {

        writeText(f, "UTF-8", text);
    }

    public static void writeText(File f, String charSet, String text) {

        if (text != null) {
            Charset charset = Charset.forName(charSet);

            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(f.toURI()), charset, StandardOpenOption.WRITE)) {
                writer.write(text, 0, text.length());
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //</editor-fold>
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Static Methods">
    public static String[] getFilenames(String directory) {
        return (new File(directory)).list();
    }

    public static Set<String> filterFiles(String directory, float bytes) {
        Set<String> fileSet = new HashSet<>();
        File[] files = getFiles(directory);

        for (File file : files) {
            long size = file.length();
            if (size > bytes) {
                fileSet.add(file.getPath());
            }
        }

        return fileSet;
    }

    //</editor-fold>
    ///////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////
    public boolean isOpenForRead() {
        return reader != null;
    }

    public boolean isOpenForWrite() {
        return writer != null;
    }

    public TextFile copyfile(String destinationFilepath) {
        try {
            File f2 = new File(destinationFilepath);
            if (!f2.exists()) {
                f2.createNewFile();
            }
            InputStream in = new FileInputStream(file);

            //For Append the file.
            //OutputStream out = new FileOutputStream(f2,true);
            //For Overwrite the file.
            OutputStream out = new FileOutputStream(f2);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            return new TextFile(f2);

        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " in the specified directory.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public TextFile copyfile(File destinationFile) {
        try {

            if (!destinationFile.exists()) {
                destinationFile.createNewFile();
            }

            InputStream in = new FileInputStream(file);

            //For Append the file.
            //OutputStream out = new FileOutputStream(f2,true);
            //For Overwrite the file.
            OutputStream out = new FileOutputStream(destinationFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            return new TextFile(destinationFile);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " in the specified directory.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public void openParagraphScanner() {
        try {
            this.scanner = new Scanner(new BufferedInputStream(new FileInputStream(file)), "UTF-16");
            this.scanner.useDelimiter(Pattern.compile("(\u2029|\r\n)+"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openTermScanner() {
        try {
            this.scanner = new Scanner(new BufferedInputStream(new FileInputStream(file)), "UTF-8");
            this.scanner.useDelimiter(Pattern.compile("(\\b|\\n|\\s)"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openBufferRead() {
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void openBufferRead(String code) {
        try {
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), code));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void openBufferReadUTF8() {
        try {
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void closeBufferRead() {
        try {
            reader.close();
            reader = null;
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openBufferWrite() {
        try {
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openNextBufferWrite() {
        if (writer != null) {

            try {
                //Close
                writer.close();

            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //Initialize new file
        takeNextFilename();
        //Open for write
        openBufferWrite();

    }

    public void openBufferWriteUTF8() {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF8"));
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openBufferWrite(String code) {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), code));
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openBufferWrite(boolean append) {

        try {
            writer = new BufferedWriter(new FileWriter(file, append));

        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void openBufferUniqueWrite(boolean append) {
        try {

            if (!append) {
                uniqueList = new LinkedList<>();
            } else {
                uniqueList = readLines();
            }
            writer = new BufferedWriter(new FileWriter(file, append));

        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void openBufferUniqueWrite() {
        try {

            uniqueList = new LinkedList<>();
            writer = new BufferedWriter(new FileWriter(file, false));

        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void openBufferUniqueWrite(int maxLines) {
        openBufferUniqueWrite();
        this.maxLines = maxLines;
    }

    public void closeBufferWrite() {
        try {

            if (uniqueList != null) {
                uniqueList.clear();
            }

            if (writer != null) {
                writer.close();
                writer = null;
            }
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeScanner() {

        scanner.close();
        scanner = null;

    }

    public void openRandomAccess() {
        try {

            randomFile = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void closeRandomAccess() {
        try {
            randomFile.close();
            randomFile = null;
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String readNextParagraph() {
        String paragraph = null;

        if (scanner.hasNext()) {
            paragraph = scanner.next();
        }

        return paragraph;

    }

    public String readNextTerm() {
        String word = null;

        while (scanner.hasNext()) {

            word = scanner.next();

        }
        return word;
    }

    public String readNextTerm(String delimiter) {
        String word = null;

        while (scanner.hasNext()) {

            word = scanner.next();

            if (!word.matches(delimiter)) {
                break;
            }

        }
        return word;
    }

    public void saveAs(String filepath) {
        copyfile(filepath);
    }

    public void saveAs(File file) {
        copyfile(file);
    }

    public String[] getAllLinesInString() {
        LinkedList<String> lineList = new LinkedList<>();
        openBufferReadUTF8();
        String line = null;

        while ((line = readNextLine()) != null) {
            if (!lineList.contains(line)) {
                lineList.add(line);
            }
        }

        closeBufferRead();
        return lineList.toArray(new String[]{});
    }

    public LinkedList<String> getUniqueLines() {
        LinkedList<String> lineList = new LinkedList<>();
        openBufferReadUTF8();
        String line = null;

        while ((line = readNextLine()) != null) {

            line = line.trim();

            if (lineList.indexOf(line) == -1) {
                lineList.add(line);
            }
        }

        closeBufferRead();
        return lineList;
    }

    public LinkedList<String> getLinesSorted() {
        LinkedList<String> lineList = new LinkedList<>();
        openBufferReadUTF8();
        String line = null;

        while ((line = readNextLine()) != null) {
            if (lineList.indexOf(line) == -1) {
                lineList.add(line);
            }
        }

        closeBufferRead();

        Collections.sort(lineList);

        return lineList;
    }

    public void removeDublicateLines(String destinationPath) {
        LinkedList lineList = getUniqueLines();
        TextFile textFile = new TextFile(destinationPath);
        textFile.writeFullTextUTF8(lineList);
    }

    public void removeDuplicateLines(File destinationFile) {
        LinkedList lineList = getUniqueLines();
        TextFile textFile = new TextFile(destinationFile);
        textFile.writeFullTextUTF8(lineList);
    }

    public void removeBlankLines(File destinationFile) {
        TextFile textFile = new TextFile(destinationFile);
        textFile.openBufferWriteUTF8();
        openBufferReadUTF8();
        String line = null;
        while ((line = readNextLine()) != null) {
            if (!line.equals("\n")) {
                textFile.writeNextLine(line);
            }
        }
        closeBufferRead();
        textFile.closeBufferWrite();
    }

    public void removeBlankLines(String destinationPath) {
        TextFile textFile = new TextFile(destinationPath);
        textFile.openBufferWriteUTF8();
        openBufferReadUTF8();
        String line = null;
        while ((line = readNextLine()) != null) {
            if (!line.equals("\n") && !line.equals("")) {
                textFile.writeNextLine(line);
            }
        }
        closeBufferRead();
        textFile.closeBufferWrite();
    }

    public void filterLinesContains(String pattern, File destinationFile) {

        TextFile textFile = new TextFile(destinationFile);
        textFile.openBufferWriteUTF8();
        openBufferReadUTF8();
        String line = null;
        while ((line = readNextLine()) != null) {
            if (line.contains(pattern)) {
                textFile.writeNextLine(line);
            }
        }
        closeBufferRead();
        textFile.closeBufferWrite();
    }

    public void filterLinesContains(String pattern, String destinationPath) {
        TextFile textFile = new TextFile(destinationPath);
        textFile.openBufferWriteUTF8();
        openBufferReadUTF8();
        String line = null;
        while ((line = readNextLine()) != null) {
            if (line.contains(pattern)) {
                textFile.writeNextLine(line);
            }
        }
        closeBufferRead();
        textFile.closeBufferWrite();
    }

    public void splitToLinesBy(String pattern, String destinationPath) {
        TextFile textFile = new TextFile(destinationPath);
        textFile.openBufferWriteUTF8();
        openBufferReadUTF8();
        String line = null;
        while ((line = readNextLine()) != null) {
            if (line.contains(pattern)) {
                String[] lines = line.split(pattern);
                for (String newLine : lines) {
                    textFile.writeNextLine(newLine);
                }
            }
        }
        closeBufferRead();
        textFile.closeBufferWrite();
    }

    public void splitToLinesBy(String pattern, File destinationFile) {
        TextFile textFile = new TextFile(destinationFile);
        textFile.openBufferWriteUTF8();
        openBufferReadUTF8();
        String line = null;
        while ((line = readNextLine()) != null) {
            if (line.contains(pattern)) {
                String[] lines = line.split(pattern);
                for (String newLine : lines) {
                    textFile.writeNextLine(newLine);
                }
            }
        }
        closeBufferRead();
        textFile.closeBufferWrite();
    }

    public void removeTextFromEndOfLines(String text, String destinationPath) {
        TextFile textFile = new TextFile(destinationPath);
        textFile.openBufferWriteUTF8();
        openBufferReadUTF8();
        String line = null;
        while ((line = readNextLine()) != null) {
            if (line.contains(text)) {
                int index = line.lastIndexOf(text);
                textFile.writeNextLine(line.substring(0, index));
            }
        }
        closeBufferRead();
        textFile.closeBufferWrite();
    }

    public void removeTextFromEndOfLines(String text, File destinationFile) {
        TextFile textFile = new TextFile(destinationFile);
        textFile.openBufferWriteUTF8();
        openBufferReadUTF8();
        String line = null;
        while ((line = readNextLine()) != null) {
            if (!line.contains(text)) {
                int index = line.lastIndexOf(text);
                textFile.writeNextLine(line.substring(0, index));
            }
        }
        closeBufferRead();
        textFile.closeBufferWrite();
    }

    public void appendTextToEndOfLines(String text, String destinationPath) {
        TextFile textFile = new TextFile(destinationPath);
        textFile.openBufferWriteUTF8();
        openBufferReadUTF8();
        String line = null;
        while ((line = readNextLine()) != null) {
            if (!line.contains(text)) {
                textFile.writeNextLine(line + text);
            }
        }
        closeBufferRead();
        textFile.closeBufferWrite();
    }

    public void appendTextToEndOfLines(String text, File destinationFile) {
        TextFile textFile = new TextFile(destinationFile);
        textFile.openBufferWriteUTF8();
        openBufferReadUTF8();
        String line = null;
        while ((line = readNextLine()) != null) {
            if (!line.contains(text)) {
                textFile.writeNextLine(line + text);
            }
        }
        closeBufferRead();
        textFile.closeBufferWrite();
    }

    public void sortLinesAlphabetically(File destinationFile) {
        LinkedList<String> lineList = getLinesSorted();
        TextFile textFile = new TextFile(destinationFile);
        textFile.writeFullTextUTF8(lineList);
    }

    public void sortLinesAlphabetically(String destinationPath) {
        LinkedList<String> lineList = getLinesSorted();
        TextFile textFile = new TextFile(destinationPath);
        textFile.writeFullTextUTF8(lineList);
    }

    ///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////
    // <editor-fold defaultstate="collapsed" desc="Buffer Read and Write">
    public String readNextLine() {
        try {
            return reader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String[] readNextLine(String splitBy) {
        try {
            String line = null;
            line = reader.readLine();
            if (line != null) {
                return line.split(splitBy);
            }
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void writeLines(List<String> lines) {
        for (String line : lines) {
            try {
                writer.write(line);
                writer.newLine();
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void writeNextLine(String line) {
        try {
            writer.write(line);
            writer.newLine();
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeNextUiqueLine(String line) {
        try {

            if (uniqueList.indexOf(line) == -1) {
                writer.write(line);
                writer.newLine();
                uniqueList.add(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void writeNextUniqueLine(String line, int maxLines) {
        if (uniqueList.indexOf(line) == -1) {
            uniqueList.add(line);

            if (uniqueList.size() >= maxLines) {
                flushUniqueLines();
                nextFilename();
                openBufferUniqueWrite(true);
            }

        }

    }

    public synchronized void writeNextLine(String line, int maxLines) {

        uniqueList.add(line);

        if (uniqueList.size() >= maxLines) {
            flushUniqueLines();
            nextFilename();
            openBufferUniqueWrite(true);
        }

    }


    public void flushUniqueLines() {

        for (String line : uniqueList) {
            if (!line.isEmpty()) {
                writeNextLine(line);
            }
        }

        uniqueList.clear();
    }

    public static void flushUniqueLines(Map<String, TextFile> map) {
        List<TextFile> list = new ArrayList<>(map.values());
        for (TextFile txtFile : list) {
            txtFile.flushUniqueLines();
        }
    }

    //</editor-fold>
    ///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////
    // <editor-fold defaultstate="collapsed" desc="Read and Write Full Text">
    public String readFullText() {

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            String line;
            String text = "";
            while ((line = reader.readLine()) != null) {
                text += line + "\n";
            }
            return text;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
                reader = null;
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    public String readFullText(List<String[]> replacements) {

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            String line;
            String text = "";
            while ((line = reader.readLine()) != null) {
                for (String[] replace : replacements) {
                    line = line.replaceAll(replace[0], replace[1]);
                }
                text += line + "\n";
            }
            return text;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
                reader = null;
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    public String readFullText(List<String[]> replacements, String encoding) {

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            String line;
            String text = "";
            while ((line = reader.readLine()) != null) {
                for (String[] replace : replacements) {
                    line = line.replaceAll(replace[0], replace[1]);
                }
                text += line + "\n";
            }
            return text;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
                reader = null;
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    public void readFullTextAsList(List<String[]> replacements, String[] startEnd, List<List<String>> results, String encoding) {

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            String line;
            String text = "";
            boolean start = false;
            int groupCount = 0;
            while ((line = reader.readLine()) != null) {

                if (line.equals(startEnd[0])) {
                    start = true;
                    List<String> groupList = new LinkedList<>();
                    results.add(groupList);

                } else if (line.equals(startEnd[1])) {
                    start = false;
                } else if (start) {

                    for (String[] replace : replacements) {
                        line = line.replaceAll(replace[0], replace[1]);
                    }
                    results.get(groupCount).add(line);
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
                reader = null;
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public String readFullText(List<String[]> replacements, String[] startEnd, String encoding) {
        String text = "";
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            String line;

            boolean start = false;

            while ((line = reader.readLine()) != null) {

                if (line.equals(startEnd[0])) {
                    start = true;
                    List<String> groupList = new LinkedList<>();

                } else if (line.equals(startEnd[1])) {
                    start = false;
                } else if (start) {

                    for (String[] replace : replacements) {
                        line = line.replaceAll(replace[0], replace[1]);
                    }
                    text += line;
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
                reader = null;
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }

            return text;
        }

    }

    public List<String> readLines() {
        List<String> lineList = new LinkedList<>();

        if (file.exists()) {

            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;

                while ((line = reader.readLine()) != null) {

                    lineList.add(line);

                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    reader.close();
                    reader = null;
                } catch (IOException ex) {
                    Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return lineList;
    }

    public List<String[]> readLinesDelimited(String delimited) {
        List<String[]> lineList = new LinkedList<>();

        if (file.exists()) {

            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;

                while ((line = reader.readLine()) != null) {

                    lineList.add(line.split(delimited));

                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    reader.close();
                    reader = null;
                } catch (IOException ex) {
                    Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return lineList;
    }

    public List<String> readLines(int limit) {
        List<String> lineList = new LinkedList<>();

        if (file.exists()) {

            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line;

                while ((line = reader.readLine()) != null) {

                    lineList.add(line);

                    if (lineList.size() > limit) {
                        break;
                    }

                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    reader.close();
                    reader = null;
                } catch (IOException ex) {
                    Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return lineList;
    }

    public List<String> readLines(List<String> excludeRegexes) {
        List<String> lineList = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            lineList = new LinkedList<>();
            while ((line = reader.readLine()) != null) {

                boolean doExclude = false;

                for (String excludeRegex : excludeRegexes) {
                    if (line.matches(excludeRegex)) {
                        doExclude = true;
                        break;
                    }
                }
                if (!doExclude) {
                    lineList.add(line);
                }

            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
                reader = null;
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }

            return lineList;
        }
    }

    public List<String> readNonEmptyLines() {
        List<String> lineList = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            lineList = new LinkedList<>();
            while ((line = reader.readLine()) != null) {

                if (!(line.isEmpty() || line.trim().isEmpty())) {
                    lineList.add(line);
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
                reader = null;
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }

            return lineList;
        }
    }

    public void readLinesBetween(String[] startEnd, List<String> between, String encoding, boolean clearTags) {

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            String line;

            while ((line = reader.readLine()) != null) {

                TextPattern.obtainPatterns(startEnd[0], startEnd[1], clearTags, line, between);

            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();

            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String readLinesBetween(String[] startEnd, String encoding) {
        String text = "";
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
            String line;

            while ((line = reader.readLine()) != null) {

                text += TextPattern.obtainPatterns(startEnd[0], startEnd[1], false, line);

            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();

            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }

            return text;
        }
    }

    public void writeFullText(String text) {

        try {
            writer = new BufferedWriter(new FileWriter(file, false));
            writer.write(text);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void writeFullText(String text, boolean append) {

        try {
            writer = new BufferedWriter(new FileWriter(file, append));

            writer.write(text);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public String readFullTextUTF8() {

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            String line;
            String text = "";
            while ((line = reader.readLine()) != null) {
                text += line + "\n";
            }

            return text;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();

            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    public void writeFullTextUTF8(String text) {

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF8"));
            writer.write(text);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void writeFullTextUTF8(List<String> lineList) {

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF8"));
            for (String line : lineList) {
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void writeFullTextUTF8(List<String> lineList, boolean append) {

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), "UTF8"));
            for (String line : lineList) {
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void writeFullTextUTF8(List<String> lineList, String startMarker, String endMarker, boolean append) {

        try {

            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), "UTF8"));
            writer.newLine();
            writer.write(startMarker + "\n");
            for (String line : lineList) {
                writer.write(line);
                writer.newLine();
            }
            writer.write(endMarker);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void writeFullTextUTF8(List<String> lineList, List<String[]> corrections, boolean append) {

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), "UTF8"));
            for (String line : lineList) {
                String correctLine = TextPattern.replaceAllWith(line, corrections);

                if (!correctLine.matches("(\\s)+") && correctLine.length() != 0) {
                    writer.write(correctLine);
                    writer.newLine();
                }

            }
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    // </editor-fold>
    ///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////
    // <editor-fold defaultstate="collapsed" desc="Asyncronous Write and Read">
    public void AsynchronousLineWrite(String text) {
        try {
            randomFile.writeUTF(text + "\n");
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public long AsynchronousWrite(String text) {
        try {
            randomFile.writeUTF(text + "\n");
            return randomFile.getFilePointer();
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0L;
    }

    public String AsynchronousLineRead(long seek) {
        try {
            randomFile.seek(seek);
            return randomFile.readUTF();
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String AsynchronousLineRead() {
        try {
            return randomFile.readLine();
        } catch (IOException ex) {
            Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    //</editor-fold>
    ///////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////

    //Append a file onto other
    public static void appendUnique(String source, String destination) {
        TextFile sourceFile = new TextFile(source);
        TextFile destinationFile = new TextFile(destination);

        sourceFile.openBufferRead();
        destinationFile.openBufferUniqueWrite(true);

        String line;
        while ((line = sourceFile.readNextLine()) != null) {
            destinationFile.writeNextLine(line);
        }
        sourceFile.closeBufferRead();
        destinationFile.closeBufferWrite();
    }

    //Split a file's lines into different files, a kind of file grouping  
    public static void splitFileByLine(String splitLineBy, String source, String[] destinations) {
        splitFileByLine(splitLineBy, source, destinations, false);
    }

    public static void splitFileByLine(String splitLineBy, String source, String[] destinations, boolean append) {
        TextFile[] destinationFiles = new TextFile[destinations.length];
        TextFile sourceFile = new TextFile(source);

        for (int i = 0; i < destinationFiles.length; i++) {
            destinationFiles[i] = new TextFile(destinations[i]);
            destinationFiles[i].openBufferWrite(append);

        }

        sourceFile.openBufferRead();
        String[] lineSplit;
        while ((lineSplit = sourceFile.readNextLine(splitLineBy)) != null) {
            for (int i = 0; i < lineSplit.length; i++) {
                destinationFiles[i].writeNextLine(lineSplit[i]);
            }
        }
        sourceFile.closeBufferRead();

        for (int i = 0; i < destinationFiles.length; i++) {
            destinationFiles[i].closeBufferWrite();
        }
    }

    public static void readWriteByLine(String source, String destination, List<String[]> replacements) {
        readWriteByLine(source, destination, false, replacements);
    }

    public static void mergeWriteByLine(String sourceFolder, String destination) {
        List<String[]> replacements = new ArrayList<>();
        mergeWriteByLine(sourceFolder, destination, replacements);
    }

    public static void mergeWriteByLine(String sourceFolder, String destination, final String filter, List<String[]> replacements) {
        File[] files = (new File(sourceFolder)).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.matches(filter);
            }
        });

        for (File f : files) {
            readWriteByLine(f.getPath(), destination, true, replacements);
        }
    }

    public static void mergeWriteByLineUnique(String sourceFolder, String destination, final String filter) {

        HashSet<String> lineSet = new HashSet<>();
        File[] files = (new File(sourceFolder)).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.matches(filter);
            }
        });

        for (File f : files) {
            readWriteByLineUnique(lineSet, f.getPath(), destination);
        }
    }

    public static void mergeWriteByLine(String sourceFolder, String destination, final String filter) {
        File[] files = (new File(sourceFolder)).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.matches(filter);
            }
        });

        for (File f : files) {
            readWriteByLine(f.getPath(), destination, true);
        }
    }

    public static void mergeWriteByLine(String sourceFolder, String destination, List<String[]> replacements) {
        File[] files = (new File(sourceFolder)).listFiles();

        for (File f : files) {
            readWriteByLine(f.getPath(), destination, true, replacements);
        }
    }

    public static void readWriteByLine(String source, String destination, boolean append, List<String[]> replacements) {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        if (!source.equals(destination)) {
            try {
                reader = new BufferedReader(new FileReader(source));
                writer = new BufferedWriter(new FileWriter(destination, append));

                String line = null;

                while ((line = reader.readLine()) != null) {
                    String outLine = line;
                    for (String[] regex : replacements) {
                        outLine = outLine.replaceAll(regex[0], regex[1]);
                    }

                    writer.write(outLine + "\n");

                }
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    reader.close();
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void readWriteByLine(String source, String destination, boolean append) {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        if (!source.equals(destination)) {
            try {
                reader = new BufferedReader(new FileReader(source));
                writer = new BufferedWriter(new FileWriter(destination, append));

                String line = null;

                while ((line = reader.readLine()) != null) {
                    String outLine = line;
                    writer.write(outLine + "\n");

                }
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    reader.close();
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void readWriteByLineUnique(HashSet<String> lineSet, String source, String destination) {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        if (!source.equals(destination)) {
            try {
                reader = new BufferedReader(new FileReader(source));
                writer = new BufferedWriter(new FileWriter(destination, true));

                String line = null;

                while ((line = reader.readLine()) != null) {
                    String outLine = line;
                    if (!lineSet.contains(outLine)) {
                        lineSet.add(outLine);
                        writer.write(outLine + "\n");
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    reader.close();
                    writer.close();
                } catch (IOException ex) {
                    Logger.getLogger(TextFile.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static Integer getFileNumber(String directory, String prefix) {
        List<Integer> numberList = new ArrayList<>();
        Pattern regex = Pattern.compile("(.*?)\\-(\\d+)(\\..*$)?");

        File[] files = getFiles(directory);
        for (File f : files) {
            if (f.isFile()) {
                String name = f.getName();
                String nm = null;
                String number = null;
                Matcher matcher = regex.matcher(name);
                while (matcher.find()) {
                    number = matcher.group(2);
                    nm = matcher.group(1);
                }

                if (number != null && nm.equals(prefix)) {
                    numberList.add(new Integer(number));
                }
            }
        }

        if (!numberList.isEmpty()) {
            Collections.sort(numberList);
            return numberList.get(numberList.size() - 1);
        } else {
            return 0;
        }
    }

    public static void main(String[] args) {
        TextFile file = new TextFile("resources/deneme");
        file.openNextBufferWrite();
        file.writeNextLine("deneme-1.txt");
        file.closeBufferWrite();

    }
}
