/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.services.filters;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import com.wci.tt.infomodels.InfoModel;
import com.wci.umls.server.helpers.ConfigUtility;

/**
 * An abstract class to pull up some functionality used by multiple filters.
 */
public abstract class ProcessingFilter {
  /** The output path. */
  protected static String outputDirectoryPath;

  /** The input directory path. */
  protected static String inputDirectoryPath;

  static {
    // Configure input and output directories
    try {
      Properties p = ConfigUtility.getConfigProperties();
      if (p.containsKey("filters.directory.input")) {
        inputDirectoryPath = p.getProperty("filters.directory.input");
      }
      if (p.containsKey("filters.directory.output")) {
        outputDirectoryPath = p.getProperty("filters.directory.output");
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Returns the output files use by the deleteOutputFiles() method.
   *
   * @return the output files
   */
  protected abstract List<String> getOutputFiles();

  /**
   * Delete output files used by filter (before running main operation again).
   *
   * @param outputFilePath the output file path
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void deleteOutputFiles(String outputFilePath) throws IOException {
    for (String outputFile : getOutputFiles()) {
      try {
        Files.delete(Paths.get(outputFilePath + outputFile));
      } catch (NoSuchFileException e) {
      }
    }
  }

  /**
   * Prints the filtered term to the specified file.
   *
   * @param outputFile the output file
   * @param term the term
   * @throws IOException Signals that an I/O exception has occurred.
   */
  protected synchronized void printTerm(String outputFile, String term)
    throws IOException {
    Files.write(Paths.get(outputDirectoryPath + outputFile),
        Arrays.asList(term), CREATE, APPEND);
  }

  /**
   * Prints the filtered model to the specified file.
   *
   * @param outputFile the output file
   * @param model the model
   * @throws IOException Signals that an I/O exception has occurred.
   */
  protected synchronized void printModel(String outputFile, InfoModel<?> model)
    throws IOException {
    Files.write(Paths.get(outputDirectoryPath + outputFile),
        Arrays.asList(model.toString()), CREATE, APPEND);
  }

  /**
   * Prints the filtered terms to the specified file.
   *
   * @param outputFile the output file
   * @param terms the terms
   * @throws IOException Signals that an I/O exception has occurred.
   */
  protected synchronized void printTerms(String outputFile, List<String> terms)
    throws IOException {
    if (!terms.isEmpty()) {
      Files.write(Paths.get(outputDirectoryPath + outputFile), terms, CREATE,
          APPEND);
    }
  }

  /**
   * Populate filter list based on input file.
   *
   * @param filename the filename
   * @return the List
   * @throws IOException Signals that an I/O exception has occurred.
   */
  protected synchronized static List<String> readInputFile(String filename)
    throws IOException {
    return Files.lines(Paths.get(inputDirectoryPath + filename))
        .map(s -> s.toLowerCase()).collect(Collectors.toList());
  }

  /**
   * Returns all numerical tokens found in term.
   *
   * @param term the term
   * @return the numerics
   */
  protected List<String> getNumericalTokens(String term) {
    // find Numeric value from term
    String str = term.replaceAll("[^0-9]+", " ");
    List<String> numerics = Arrays.asList(str.trim().split(" "));

    if (numerics.size() == 1 && numerics.get(0).trim().length() == 0) {
      return new ArrayList<>();
    }

    return numerics;
  }
}
