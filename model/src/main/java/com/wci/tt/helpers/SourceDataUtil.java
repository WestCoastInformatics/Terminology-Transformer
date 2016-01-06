package com.wci.tt.helpers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility class for handling source data files
 *
 */
public class SourceDataUtil {

  /**
   * Size of the buffer to read/write data
   */
  private static final int BUFFER_SIZE = 4096;

  public static void writeSourceDataFile(InputStream fileInputStream,
    String destinationFolder, String fileName) throws IOException {

    System.out
        .println("write input stream " + destinationFolder + " " + fileName);

    BufferedOutputStream bos = new BufferedOutputStream(
        new FileOutputStream(destinationFolder + File.separator + fileName));
    byte[] bytesIn = new byte[BUFFER_SIZE];
    int read = 0;
    while ((read = fileInputStream.read(bytesIn)) != -1) {
      bos.write(bytesIn, 0, read);
    }
    bos.close();
  }

  /**
   * Boolean check for whether a given file input stream represents a zip file
   * @param fileInputStream
   * @return
   * @throws Exception
   */
  public static boolean isValidCompressedSourceDataFile(InputStream fileInputStream)
    throws Exception {

    ZipEntry zipEntry = new ZipInputStream(fileInputStream).getNextEntry();

    // if no zip entry, not a zipped file
    if (zipEntry == null) {
      return false;
    }

    // check that all files are top-level elements (no directories)
    while (zipEntry != null) {
      if (zipEntry.isDirectory()) {
       return false;
      }
    }

    return true;

  }

  public static void extractCompressedSourceDataFile(InputStream fileInputStream,
    String destinationFolder) throws Exception {
    File destDir = new File(destinationFolder);
    if (!destDir.exists()) {
      destDir.mkdir();
    }
    
    if (!isValidCompressedSourceDataFile(fileInputStream)) {
      throw new Exception("Unzip requested for either an uncompressed file or a compressed file containing subdirectories");
    }

    // convert file stream to zip input stream and get first entry
    ZipInputStream zipIn = new ZipInputStream(fileInputStream);
    ZipEntry entry = zipIn.getNextEntry();

    // iterates over entries in the zip file
    while (entry != null) {

      System.out.println("destination" + destinationFolder);
      System.out.println("entry name" + entry.getName());
      // only extract top-level elements
      if (!entry.isDirectory()) {

        // preserve archive name by replacing file separator with underscore
        extractZipEntry(zipIn, destinationFolder + File.separator
            + entry.getName().replace("/", "_"));
      }
      zipIn.closeEntry();
      entry = zipIn.getNextEntry();
    }
    zipIn.close();
  }

  /**
   * Private helper class. Extracts a zip entry (file entry)
   * @param zipIn
   * @param filePath
   * @throws IOException
   */
  private static void extractZipEntry(ZipInputStream zipIn, String filePath)
    throws IOException {
    BufferedOutputStream bos =
        new BufferedOutputStream(new FileOutputStream(filePath));
    byte[] bytesIn = new byte[BUFFER_SIZE];
    int read = 0;
    while ((read = zipIn.read(bytesIn)) != -1) {
      bos.write(bytesIn, 0, read);
    }
    bos.close();
  }
}
