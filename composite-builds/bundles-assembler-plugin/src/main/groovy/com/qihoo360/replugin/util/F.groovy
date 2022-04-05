package com.qihoo360.replugin.util

import java.util.zip.*

class F {
    public static boolean zipFile(String filePath, String zipPath) {
        BufferedReader bin = null;
        ZipOutputStream out = null;
        try {
            File file = new File(filePath);
            bin = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "ISO-8859-1"));
            FileOutputStream f = new FileOutputStream(zipPath);
            CheckedOutputStream ch = new CheckedOutputStream(f, new CRC32());
            out = new ZipOutputStream(new BufferedOutputStream(ch));

            int c;
            out.putNextEntry(new ZipEntry(file.getName()));
            while ((c = bin.read()) != -1)
                out.write(c);
        }

        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            try {
                if (bin != null) bin.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static void zip(ZipOutputStream out, File f, String base) throws Exception {
//      System.out.println("Zipping  "+f.getName());
        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            out.putNextEntry(new ZipEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], base + fl[i].getName());
            }
        } else {
            out.putNextEntry(new ZipEntry(base));
            FileInputStream is = new FileInputStream(f);
            BufferedInputStream bin = new BufferedInputStream(is);//修改BUG!二进制输出采用buffered
            int b;
            while ((b = bin.read()) != -1)
                out.write(b);
            bin.close();
        }

    }

    public static boolean zipDirectory(String dir, String zipPath) {
        ZipOutputStream out = null;
        try {
            File dirFile = new File(dir);
            if (!dirFile.isDirectory()) return false;
            FileOutputStream fo = new FileOutputStream(zipPath);
            CheckedOutputStream ch = new CheckedOutputStream(fo, new CRC32());
            out = new ZipOutputStream(new BufferedOutputStream(ch));
            zip(out, dirFile, "");

        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    static List<File> unzip(String zipPath, String destDirPath) {
        return unzip(new File(zipPath), new File(destDirPath))
    }

    static List<File> unzip(File zipFile, File destDir) {
        if (!destDir.exists()) {
            destDir.mkdirs()
        }
        List<File> unzipDir = new ArrayList<>()
        new ZipFile(zipFile).withCloseable { theZip ->
            theZip.entries().each { zipEntry ->
                File file = new File(destDir, zipEntry.name)
                unzipDir.add(file)
                if (zipEntry.directory) {
                    createOrExistsDir(file)
                    return
                }
                createOrExistsFile(file)
                file.withOutputStream { fout ->
                    fout.write(theZip.getInputStream(zipEntry).bytes)
                }
            }
        }
        return unzipDir
    }

    private static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    private static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}