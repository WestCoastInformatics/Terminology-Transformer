/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.algo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import com.wci.umls.server.algo.Algorithm;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.FieldedStringTokenizer;
import com.wci.umls.server.helpers.LocalException;
import com.wci.umls.server.jpa.algo.RrfReaders;
import com.wci.umls.server.jpa.content.AtomJpa;
import com.wci.umls.server.jpa.content.AttributeJpa;
import com.wci.umls.server.jpa.content.CodeJpa;
import com.wci.umls.server.jpa.content.ConceptJpa;
import com.wci.umls.server.jpa.content.DescriptorJpa;
import com.wci.umls.server.jpa.services.HistoryServiceJpa;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.AtomClass;
import com.wci.umls.server.model.content.Attribute;
import com.wci.umls.server.model.content.Code;
import com.wci.umls.server.model.content.ComponentHasAttributes;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.model.content.Descriptor;
import com.wci.umls.server.model.content.MapSet;
import com.wci.umls.server.model.content.Mapping;
import com.wci.umls.server.model.content.Relationship;
import com.wci.umls.server.model.meta.IdType;
import com.wci.umls.server.model.meta.Language;
import com.wci.umls.server.model.meta.RootTerminology;
import com.wci.umls.server.model.meta.Terminology;
import com.wci.umls.server.services.RootService;
import com.wci.umls.server.services.helpers.ProgressEvent;
import com.wci.umls.server.services.helpers.ProgressListener;
import com.wci.umls.server.services.helpers.PushBackReader;

import gnu.trove.strategy.HashingStrategy;

/**
 * Implementation of an algorithm to import RF2 snapshot data.
 */
public class RrfLoaderAlgorithm extends HistoryServiceJpa
    implements Algorithm {

  /** The prefix. */
  private String prefix = "RXN";

  /** Listeners. */
  private List<ProgressListener> listeners = new ArrayList<>();

  /** The terminology. */
  private String terminology;

  /** The terminology version. */
  private String version;

  /** The single mode. */
  private boolean singleMode = false;

  /** The codes flag. */
  private boolean codesFlag = true;

  /** The release version. */
  private String releaseVersion;

  /** The release version date. */
  private Date releaseVersionDate;

  /** The readers. */
  private RrfReaders readers;

  /** The loader. */
  private final String loader = "loader";

  /** The published. */
  private final String published = "PUBLISHED";

  /** The loaded terminologies. */
  private Map<String, Terminology> loadedTerminologies = new HashMap<>();

  /** The loaded root terminologies. */
  private Map<String, RootTerminology> loadedRootTerminologies =
      new HashMap<>();

  /** The loaded languages. */
  private Map<String, Language> loadedLanguages = new HashMap<>();


  /** The term id type map. */
  private Map<String, IdType> termIdTypeMap = new HashMap<>();

  /** The code map. */
  private Map<String, Long> codeIdMap = new HashMap<>();

  /** The concept map. */
  private Map<String, Long> conceptIdMap = new HashMap<>(10000);

  /** The descriptor map. */
  private Map<String, Long> descriptorIdMap = new HashMap<>(10000);

  /** The atom map. */
  private Map<String, Long> atomIdMap = new HashMap<>(10000);

  /** The atom concept id map. */
  private Map<String, String> atomConceptIdMap = new HashMap<>(10000);

  /** The atom terminology map. */
  private Map<String, String> atomTerminologyMap = new HashMap<>(10000);

  /** The atom code id map. */
  private Map<String, String> atomCodeIdMap = new HashMap<>(10000);

  /** The atom descriptor id map. */
  private Map<String, String> atomDescriptorIdMap = new HashMap<>(10000);

  /** The relationship map. */
  private Map<String, Long> relationshipMap = new HashMap<>(10000);

  /** The Constant coreModuleId. */
  private final static String coreModuleId = "900000000000207008";

  /** The Constant metadataModuleId. */
  private final static String metadataModuleId = "900000000000012004";

  /** non-core modules map. */
  private Map<String, Set<Long>> moduleConceptIdMap = new HashMap<>();

  /** The lat code map. */
  private static Map<String, String> latCodeMap = new HashMap<>();

  /** The map set map. */
  private Map<String, MapSet> mapSetMap = new HashMap<>();

  static {

    // from http://www.nationsonline.org/oneworld/country_code_list.htm
    latCodeMap.put("BAQ", "eu");
    latCodeMap.put("CZE", "cz");
    latCodeMap.put("DAN", "dk");
    latCodeMap.put("DUT", "nl");
    latCodeMap.put("ENG", "en");
    latCodeMap.put("FIN", "fi");
    latCodeMap.put("FRE", "fr");
    latCodeMap.put("GER", "de");
    latCodeMap.put("HEB", "he");
    latCodeMap.put("HUN", "hu");
    latCodeMap.put("ITA", "it");
    latCodeMap.put("JPN", "ja");
    latCodeMap.put("KOR", "ko");
    latCodeMap.put("LAV", "lv");
    latCodeMap.put("NOR", "nn");
    latCodeMap.put("POL", "pl");
    latCodeMap.put("POR", "pt");
    latCodeMap.put("RUS", "ru");
    latCodeMap.put("SCR", "sc");
    latCodeMap.put("SPA", "es");
    latCodeMap.put("SWE", "sv");
    latCodeMap.put("CHI", "zh");
    latCodeMap.put("TUR", "tr");
    latCodeMap.put("EST", "et");
    latCodeMap.put("GRE", "el");
  }

  /**
   * Instantiates an empty {@link RrfLoaderAlgorithm}.
   * @throws Exception if anything goes wrong
   */
  public RrfLoaderAlgorithm() throws Exception {
    super();
  }

  /**
   * Sets the terminology.
   *
   * @param terminology the terminology
   */
  public void setTerminology(String terminology) {
    this.terminology = terminology;
  }

  public String getTerminology() {
    return terminology;
  }

  /**
   * Sets the terminology version.
   *
   * @param version the terminology version
   */
  public void setVersion(String version) {
    this.version = version;
  }

  public String getVersion() {
    return version;
  }

  /**
   * Sets the release version.
   *
   * @param releaseVersion the rlease version
   */
  public void setReleaseVersion(String releaseVersion) {
    this.releaseVersion = releaseVersion;
  }

  /**
   * Sets the single mode.
   *
   * @param singleMode the single mode
   */
  public void setSingleMode(boolean singleMode) {
    this.singleMode = singleMode;
  }

  /**
   * Sets the codes flag.
   *
   * @param codesFlag the codes flag
   */
  public void setCodesFlag(boolean codesFlag) {
    this.codesFlag = codesFlag;
  }

  /**
   * Sets the readers.
   *
   * @param readers the readers
   */
  public void setReaders(RrfReaders readers) {
    this.readers = readers;
  }

  /**
   * Sets the prefix.
   *
   * @param prefix the prefix
   */
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  /**
   * Compute.
   *
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public void compute() throws Exception {
    try {
      Logger.getLogger(getClass()).info("Start loading RRF");
      Logger.getLogger(getClass()).info("  terminology = " + terminology);
      Logger.getLogger(getClass()).info("  version = " + version);
      Logger.getLogger(getClass()).info("  single mode = " + singleMode);
      Logger.getLogger(getClass()).info("  releaseVersion = " + releaseVersion);
      releaseVersionDate = ConfigUtility.DATE_FORMAT
          .parse(releaseVersion.substring(0, 4) + "0101");

      // Track system level information
      long startTimeOrig = System.nanoTime();

      // control transaction scope
      setTransactionPerOperation(false);
      // Turn of ID computation when loading a terminology
      setAssignIdentifiersFlag(false);
      // Let loader set last modified flags.
      setLastModifiedFlag(false);

      // faster performance.
      beginTransaction();


      // Load the content
      loadMrconso();

      // Attributes
      loadMrsat();

      // Final logging messages
      Logger.getLogger(getClass()).info("      elapsed time = " + getTotalElapsedTimeStr(startTimeOrig));
      Logger.getLogger(getClass()).info("Done ...");

      // clear and commit
      commit();
      clear();

    } catch (Exception e) {
      Logger.getLogger(getClass()).error(e.getMessage());
      throw e;
    }
  }




  /**
   * Load MRSAT. This is responsible for loading {@link Attribute}s.
   *
   * @throws Exception the exception
   */
  private void loadMrsat() throws Exception {
    Logger.getLogger(getClass()).info("  Load MRSAT data");
    String line = null;

    int objectCt = 0;
    final PushBackReader reader = readers.getReader(RrfReaders.Keys.MRSAT);
    // make set of all atoms that got an additional attribute
    final Set<Atom> modifiedAtoms = new HashSet<>();
    final Set<Relationship<? extends ComponentHasAttributes, ? extends ComponentHasAttributes>> modifiedRelationships =
        new HashSet<>();
    final Set<Code> modifiedCodes = new HashSet<>();
    final Set<Descriptor> modifiedDescriptors = new HashSet<>();
    final Set<Concept> modifiedConcepts = new HashSet<>();
    final String fields[] = new String[13];
    while ((line = reader.readLine()) != null) {
      line = line.replace("\r", "");
      FieldedStringTokenizer.split(line, "|", 13, fields);

      // Skip non-matching in single mode
      if (singleMode && !fields[9].equals(terminology)
          && !fields[9].equals("SAB")) {
        continue;
      }
      
      // Only create attributes if ATN=NDC
      if (!fields[8].equals("NDC")) {
        continue;
      }

      // Field Description
      // 0 CUI
      // 1 LUI
      // 2 SUI
      // 3 METAUI
      // 4 STYPE
      // 5 CODE
      // 6 ATUI
      // 7 SATUI
      // 8 ATN
      // 9 SAB
      // 10 ATV
      // 11 SUPPRESS
      // 12 CVF
      //
      // e.g.
      // C0001175|L0001175|S0010339|A0019180|SDUI|D000163|AT38209082||FX|MSH|D015492|N||
      // C0001175|L0001175|S0354232|A2922342|AUI|62479008|AT24600515||DESCRIPTIONSTATUS|SNOMEDCT|0|N||
      // C0001175|L0001842|S0011877|A15662389|CODE|T1|AT100434486||URL|MEDLINEPLUS|http://www.nlm.nih.gov/medlineplus/aids.html|N||
      // C0001175|||R54775538|RUI||AT63713072||CHARACTERISTICTYPE|SNOMEDCT|0|N||
      // C0001175|||R54775538|RUI||AT69142126||REFINABILITY|SNOMEDCT|1|N||
      final Attribute att = new AttributeJpa();

      att.setTimestamp(releaseVersionDate);
      att.setLastModified(releaseVersionDate);
      att.setLastModifiedBy(loader);
      att.setObsolete(fields[11].equals("O"));
      att.setSuppressible(!fields[11].equals("N"));
      att.setPublished(true);
      att.setPublishable(true);
      // fields[5] CODE not used - redundant
      if (!singleMode) {
        att.putAlternateTerminologyId(terminology, fields[6]);
      }
      att.setTerminologyId(fields[7]);
      att.setTerminology(fields[9].intern());
      if (loadedTerminologies.get(fields[9]) == null) {
        throw new Exception(
            "Attribute references terminology that does not exist: "
                + fields[9]);
      } else {
        att.setVersion(loadedTerminologies.get(fields[9]).getVersion());
      }
      att.setName(fields[8]);
      att.setValue(fields[10]);

      // Skip CV_MEMBER attributes for now
      if (fields[8].equals("CV_MEMBER")) {
        continue;
      }

      // Handle subset members and subset member attributes later
      else if (fields[8].equals("SUBSET_MEMBER")) {
        continue;

      } else if (fields[4].equals("AUI")) {
        // Get the concept for the AUI
        Atom atom = getAtom(atomIdMap.get(fields[3]));
        atom.addAttribute(att);
        addAttribute(att, atom);
      }
      // Special case of a CODE attribute where the AUI has "NOCODE" as the code
      // UMLS has one case of an early XM atom with NOCODE (ICD9CM to CCS map)
      // In loadMrconso we skip NOCODE codes, never creating them as Code
      // objects.
      else if (codesFlag && fields[4].equals("CODE")
          && atomCodeIdMap.get(fields[3]).equals("NOCODE")) {
        // Get the concept for the AUI
        final Atom atom = getAtom(atomIdMap.get(fields[3]));
        atom.addAttribute(att);
        addAttribute(att, atom);
      } else if (fields[4].equals("RUI")) {
        // Get the relationship for the RUI
        final Relationship<? extends ComponentHasAttributes, ? extends ComponentHasAttributes> relationship =
            getRelationship(relationshipMap.get(fields[3]), null);
        relationship.addAttribute(att);
        addAttribute(att, relationship);
      } else if (codesFlag && fields[4].equals("CODE")) {
        final Long codeId = codeIdMap.get(
            atomTerminologyMap.get(fields[3]) + atomCodeIdMap.get(fields[3]));
        if (codeId == null) {
          // Referential integrity error
          Logger.getLogger(getClass()).error("line = " + line);
          Logger.getLogger(getClass())
              .error("Referential integrity issue with field 3: " + fields[3]);
        } else {
          // Get the code for the terminology and CODE of the AUI
          final Code code = getCode(codeId);
          code.addAttribute(att);
          addAttribute(att, code);
        }
      } else if (fields[4].equals("CUI")) {
        // Get the concept for the terminology and CUI
        att.setTerminology(terminology);
        att.setVersion(version);
        final Concept concept =
            getConcept(conceptIdMap.get(terminology + fields[0]));
        concept.addAttribute(att);
        addAttribute(att, concept);
      } else if (fields[4].equals("SCUI")) {
        // Get the concept for the terminology and SCUI of the AUI
        final Long conceptId =
            conceptIdMap.get(atomTerminologyMap.get(fields[3])
                + atomConceptIdMap.get(fields[3]));
        if (conceptId == null) {
          // Referential integrity error
          Logger.getLogger(getClass()).error("line = " + line);
          Logger.getLogger(getClass())
              .error("Referential integrity issue with field 3: " + fields[3]);

        } else {
          final Concept concept =
              getConcept(conceptIdMap.get(atomTerminologyMap.get(fields[3])
                  + atomConceptIdMap.get(fields[3])));
          concept.addAttribute(att);
          addAttribute(att, concept);
        }
      } else if (fields[4].equals("SDUI")) {
        final Long descriptorId =
            descriptorIdMap.get(atomTerminologyMap.get(fields[3])
                + atomDescriptorIdMap.get(fields[3]));
        if (descriptorId == null) {
          // Referential integrity error
          Logger.getLogger(getClass())
          .error("line = " + line);
          Logger.getLogger(getClass())
              .error("Referential integrity issue with field 3: " + fields[3]);

        } else {
          // Get the descriptor for the terminology and SDUI of the AUI
          final Descriptor descriptor = getDescriptor(descriptorId);
          descriptor.addAttribute(att);
          addAttribute(att, descriptor);
        }
      }
      /*if (isMapSetAttribute(fields[8])) {
        processMapSetAttribute(fields[0], fields[8], fields[10], fields[7]);
      }
       */
      // Update objects before commit
      if (++objectCt % commitCt == 0) {
        // Update objects with new attributes
        for (final Concept c : modifiedConcepts) {
          updateConcept(c);
        }
        modifiedConcepts.clear();
        for (final Atom a : modifiedAtoms) {
          updateAtom(a);
        }
        modifiedAtoms.clear();
        for (final Relationship<? extends ComponentHasAttributes, ? extends ComponentHasAttributes> r : modifiedRelationships) {
          updateRelationship(r);
        }
        modifiedRelationships.clear();
        for (final Code code : modifiedCodes) {
          updateCode(code);
        }
        modifiedCodes.clear();
        for (final Descriptor d : modifiedDescriptors) {
          updateDescriptor(d);
        }
        modifiedDescriptors.clear();
      }

      // Handle SNOMED extension modules
      // If ATN=MODULE_ID and ATV=AUI
      // Look up the concept.getId() and save for later (tied to this module)
      if (fields[8].equals("MODULE_ID") && fields[4].equals("AUI")) {
        if (isExtensionModule(fields[10])) {
          // terminology + module concept id
          final String key = fields[9] + fields[10];
          Logger.getLogger(getClass())
              .info("  extension module = " + fields[10] + ", " + key);
          if (!moduleConceptIdMap.containsKey(key)) {
            moduleConceptIdMap.put(key, new HashSet<Long>());
          }
          Logger.getLogger(getClass())
              .info("    concept = " + atomConceptIdMap.get(fields[3]));
          moduleConceptIdMap.get(key)
              .add(conceptIdMap.get(atomTerminologyMap.get(fields[3])
                  + atomConceptIdMap.get(fields[3])));
        }
      }

      // log and commit
      logAndCommit(objectCt, RootService.logCt, RootService.commitCt);

      //
      // NOTE: there are no subset attributes in RRF
      //

    } // end while loop

    // add all of the mapsets
    for (MapSet mapSet : mapSetMap.values()) {
      if (mapSet.getName() == null) {
        Logger.getLogger(getClass()).warn("Mapset has no name set: " + mapSet.toString());
        throw new LocalException("Mapsets must have a name set.");
      }
      if (mapSet.getFromTerminology() == null) {
        Logger.getLogger(getClass()).warn("Mapset has no from terminology set: " + mapSet.toString());
        throw new LocalException("Mapsets must have a from terminology set.");
      }
      if (mapSet.getToTerminology() == null) {
        Logger.getLogger(getClass()).warn("Mapset has no to terminology set: " + mapSet.toString());
        throw new LocalException("Mapsets must have a to terminology set.");
      }
      mapSet.setLastModifiedBy(loader);
      mapSet.setLastModified(releaseVersionDate);
      mapSet.setObsolete(false);
      mapSet.setSuppressible(false);
      mapSet.setPublished(true);
      mapSet.setPublishable(true);
      if (mapSet.getTerminology() == null) {
        mapSet.setTerminology(terminology);
      }
      if (mapSet.getVersion() == null) {
        mapSet.setVersion(version);
      }
      if (mapSet.getTerminologyId() == null) {
        mapSet.setTerminologyId("");
      }
      if (mapSet.getTerminology() == null) {
        throw new LocalException("Mapsets has no terminology set.");
      }
      if (mapSet.getMapVersion() == null) {
        Logger.getLogger(getClass()).warn("Mapset has no version set: " + mapSet.toString());
        throw new LocalException("Mapsets must have a map version set.");
      }

      mapSet.setTimestamp(releaseVersionDate);
      addMapSet(mapSet);
    }

    // get final updates in
    for (final Concept c : modifiedConcepts) {
      updateConcept(c);
    }
    modifiedConcepts.clear();
    for (final Atom a : modifiedAtoms) {
      updateAtom(a);
    }
    modifiedAtoms.clear();
    for (final Relationship<? extends ComponentHasAttributes, ? extends ComponentHasAttributes> r : modifiedRelationships) {
      updateRelationship(r);
    }
    modifiedRelationships.clear();
    for (final Code code : modifiedCodes) {
      updateCode(code);
    }
    modifiedCodes.clear();
    for (final Descriptor d : modifiedDescriptors) {
      updateDescriptor(d);
    }
    modifiedDescriptors.clear();

    // commit
    commitClearBegin();

  }




  /**
   * Make attribute.
   *
   * @param mapping the mapping
   * @param name the name
   * @param value the value
   * @return the attribute
   * @throws Exception the exception
   */
  private Attribute makeAttribute(Mapping mapping, String name, String value)
    throws Exception {
    Attribute att = new AttributeJpa();
    att.setName(name);
    att.setValue(value);
    att.setTimestamp(releaseVersionDate);
    att.setLastModified(releaseVersionDate);
    att.setLastModifiedBy(loader);
    att.setObsolete(false);
    att.setSuppressible(false);
    att.setPublished(true);
    att.setPublishable(true);
    att.setTerminology(terminology);
    att.setVersion(version);
    att.setTerminologyId("");

    addAttribute(att, mapping);
    return att;
  }



  /**
   * Load MRCONSO.RRF. This is responsible for loading {@link Atom}s and
   * {@link AtomClass}es.
   *
   * @throws Exception the exception
   */
  private void loadMrconso() throws Exception {
    Logger.getLogger(getClass()).info("  Load MRCONSO");
    Logger.getLogger(getClass()).info("  Insert atoms and concepts ");

    // Set up maps
    String line = null;

    int objectCt = 0;
    final PushBackReader reader = readers.getReader(RrfReaders.Keys.MRCONSO);
    final String fields[] = new String[18];
    String prevCui = null;
    Concept cui = null;
    while ((line = reader.readLine()) != null) {

      line = line.replace("\r", "");
      FieldedStringTokenizer.split(line, "|", 18, fields);

      // Skip non-matching in single mode
      if (singleMode && !fields[11].equals(terminology)) {
        continue;
      }
      
      // Only create atoms if SAB=RXNORM
      if (!fields[11].equals("RXNORM")) {
        continue;
      }

      // Field Description
      // 0 CUI
      // 1 LAT
      // 2 TS
      // 3 LUI
      // 4 STT
      // 5 SUI
      // 6 ISPREF
      // 7 AUI
      // 8 SAUI
      // 9 SCUI
      // 10 SDUI
      // 11 SAB
      // 12 TTY
      // 13 CODE
      // 14 STR
      // 15 SRL
      // 16 SUPPRESS
      // 17 CVF
      //
      // e.g.
      // C0000005|ENG|P|L0000005|PF|S0007492|Y|A7755565||M0019694|D012711|MSH|PEN|D012711|(131)I-Macroaggregated
      // Albumin|0|N|256|

      // set the root terminology language
      loadedRootTerminologies.get(fields[11])
          .setLanguage(loadedLanguages.get(fields[1]));

      final Atom atom = new AtomJpa();
      atom.setLanguage(fields[1].intern());
      atom.setTimestamp(releaseVersionDate);
      atom.setLastModified(releaseVersionDate);
      atom.setLastModifiedBy(loader);
      atom.setObsolete(fields[16].equals("O"));
      atom.setSuppressible(!fields[16].equals("N"));
      atom.setPublished(true);
      atom.setPublishable(true);
      atom.setName(fields[14]);
      atom.setTerminology(fields[11].intern());
      if (loadedTerminologies.get(fields[11]) == null) {
        throw new Exception(
            "Atom references terminology that does not exist: " + fields[11]);
      }
      atom.setVersion(
          loadedTerminologies.get(fields[11]).getVersion().intern());
      // skip in single mode
      if (!singleMode) {
        atom.putAlternateTerminologyId(terminology, fields[7]);
      }
      atom.setTerminologyId(fields[8]);
      atom.setTermType(fields[12].intern());
      atom.setWorkflowStatus(published);

      atom.setCodeId(fields[13]);
      atom.setDescriptorId(fields[10]);
      atom.setConceptId(fields[9]);

      atom.setStringClassId(fields[5]);
      atom.setLexicalClassId(fields[3]);
      atom.setCodeId(fields[13]);

      // Handle root terminology short name, hierarchical name, and sy names
      if (fields[11].equals("SRC") && fields[12].equals("SSN")) {
        final Terminology t = loadedTerminologies.get(fields[13].substring(2));
        if (t == null || t.getRootTerminology() == null) {
          Logger.getLogger(getClass()).error("  Null root " + line);
        } else {
          t.getRootTerminology().setShortName(fields[14]);
        }
      }
      if (fields[11].equals("SRC") && fields[12].equals("RHT")) {
        final Terminology t = loadedTerminologies.get(fields[13].substring(2));
        if (t == null || t.getRootTerminology() == null) {
          Logger.getLogger(getClass()).error("  Null root " + line);
        } else {
          t.getRootTerminology().setHierarchicalName(fields[14]);
        }
      }

      if (fields[11].equals("SRC") && fields[12].equals("RPT")) {
        final Terminology t = loadedTerminologies.get(fields[13].substring(2));
        if (t == null || t.getRootTerminology() == null) {
          Logger.getLogger(getClass()).error("  Null root " + line);
        } else {
          t.getRootTerminology().setPreferredName(fields[14]);
        }
      }
      if (fields[11].equals("SRC") && fields[12].equals("RSY")
          && !fields[14].equals("")) {
        final Terminology t = loadedTerminologies.get(fields[13].substring(2));
        if (t == null || t.getRootTerminology() == null) {
          Logger.getLogger(getClass()).error("  Null root " + line);
        } else {
          List<String> syNames = t.getRootTerminology().getSynonymousNames();
          syNames.add(fields[14]);
        }
      }

      // Handle terminology sy names
      if (fields[11].equals("SRC") && fields[12].equals("VSY")
          && !fields[14].equals("")) {
        final Terminology t = loadedTerminologies.get(fields[13].substring(2));
        if (t == null || t.getRootTerminology() == null) {
          Logger.getLogger(getClass()).error("  Null root " + line);
        } else {
          List<String> syNames = t.getSynonymousNames();
          syNames.add(fields[14]);
        }
      }

      // Determine organizing class type for terminology
      if (!atom.getDescriptorId().equals("")) {
        termIdTypeMap.put(atom.getTerminology(), IdType.DESCRIPTOR);
      } else if (!atom.getConceptId().equals("")) {
        termIdTypeMap.put(atom.getTerminology(), IdType.CONCEPT);
      } // OTHERWISE it remains "CODE"

      // skip in single mode
      if (!singleMode) {
        atom.putConceptTerminologyId(terminology, fields[0]);
      }

      // Add atoms and commit periodically
      addAtom(atom);
      logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);
      atomIdMap.put(fields[7], atom.getId());
      atomTerminologyMap.put(fields[7], atom.getTerminology());
      atomConceptIdMap.put(fields[7], atom.getConceptId());
      atomCodeIdMap.put(fields[7], atom.getCodeId());
      atomDescriptorIdMap.put(fields[7], atom.getDescriptorId());

      // CUI - skip in single mode
      if (!singleMode) {
        // Add concept
        if (prevCui == null || !fields[0].equals(prevCui)) {
          if (prevCui != null) {
            cui.setName(getComputedPreferredName(cui));
            addConcept(cui);
            conceptIdMap.put(cui.getTerminology() + cui.getTerminologyId(),
                cui.getId());
            logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);
          }
          cui = new ConceptJpa();
          cui.setTimestamp(releaseVersionDate);
          cui.setLastModified(releaseVersionDate);
          cui.setLastModifiedBy(loader);
          cui.setPublished(true);
          cui.setPublishable(true);
          cui.setTerminology(terminology);
          cui.setTerminologyId(fields[0]);
          cui.setVersion(version);
          cui.setWorkflowStatus(published);
        }
        cui.addAtom(atom);
        prevCui = fields[0];
      }

      // Handle Subset
      // C3539934|ENG|S|L11195730|PF|S13913746|N|A23460885||900000000000538005||SNOMEDCT_US|SB|900000000000538005|Description
      // format|9|N|256|
/*      if (fields[12].equals("SB")) {

        // Have to handle the type later, when we get to attributes
        final AtomSubset atomSubset = new AtomSubsetJpa();
        setSubsetFields(atomSubset, fields);
        cuiAuiAtomSubsetMap.put(fields[0] + fields[7], atomSubset);
        idTerminologyAtomSubsetMap.put(
            atomSubset.getTerminologyId() + atomSubset.getTerminology(),
            atomSubset);
        final ConceptSubset conceptSubset = new ConceptSubsetJpa();
        setSubsetFields(conceptSubset, fields);
        cuiAuiConceptSubsetMap.put(fields[0] + fields[7], conceptSubset);
        idTerminologyConceptSubsetMap.put(
            conceptSubset.getTerminologyId() + conceptSubset.getTerminology(),
            conceptSubset);
      }*/

    }
    // Add last concept
    if (prevCui != null) {
      cui.setName(getComputedPreferredName(cui));
      addConcept(cui);
      conceptIdMap.put(cui.getTerminology() + cui.getTerminologyId(),
          cui.getId());
      logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);
    }

    // Set the terminology organizing class types
    for (final Terminology terminology : loadedTerminologies.values()) {
      final IdType idType = termIdTypeMap.get(terminology.getTerminology());
      if (idType != null && idType != IdType.CODE) {
        terminology.setOrganizingClassType(idType);
        updateTerminology(terminology);
      }
    }

    Logger.getLogger(getClass()).info("  Add concepts");
    objectCt = 0;
    // NOTE: Hibernate-specific to support iterating
    // Restrict to timestamp used for THESE atoms, in case multiple RRF
    // files are loaded
    final Session session = manager.unwrap(Session.class);
    org.hibernate.Query hQuery = session
        .createQuery("select a from AtomJpa a " + "where conceptId is not null "
            + "and conceptId != '' and timestamp = :timestamp "
            + "order by terminology, conceptId");
    hQuery.setParameter("timestamp", releaseVersionDate);
    hQuery.setReadOnly(true).setFetchSize(1000);
    ScrollableResults results = hQuery.scroll(ScrollMode.FORWARD_ONLY);
    prevCui = null;
    cui = null;
    while (results.next()) {
      final Atom atom = (Atom) results.get()[0];
      if (atom.getConceptId() == null || atom.getConceptId().isEmpty()) {
        continue;
      }
      if (prevCui == null || !prevCui.equals(atom.getConceptId())) {
        if (cui != null) {
          // compute preferred name
          cui.setName(getComputedPreferredName(cui));
          addConcept(cui);
          conceptIdMap.put(cui.getTerminology() + cui.getTerminologyId(),
              cui.getId());
          logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);
        }
        cui = new ConceptJpa();
        cui.setTimestamp(releaseVersionDate);
        cui.setLastModified(releaseVersionDate);
        cui.setLastModifiedBy(loader);
        cui.setPublished(true);
        cui.setPublishable(true);
        cui.setTerminology(atom.getTerminology());
        cui.setTerminologyId(atom.getConceptId());
        cui.setVersion(atom.getVersion());
        cui.setWorkflowStatus(published);
      }
      cui.addAtom(atom);
      prevCui = atom.getConceptId();
    }
    if (cui != null) {
      cui.setName(getComputedPreferredName(cui));
      addConcept(cui);
      conceptIdMap.put(cui.getTerminology() + cui.getTerminologyId(),
          cui.getId());
      commitClearBegin();
    }
    results.close();
    Logger.getLogger(getClass()).info("  Add descriptors");
    objectCt = 0;

    // NOTE: Hibernate-specific to support iterating
    hQuery = session.createQuery(
        "select a from AtomJpa a " + "where descriptorId is not null "
            + "and descriptorId != '' and timestamp = :timestamp "
            + "order by terminology, descriptorId");
    hQuery.setParameter("timestamp", releaseVersionDate);
    hQuery.setReadOnly(true).setFetchSize(1000);
    results = hQuery.scroll(ScrollMode.FORWARD_ONLY);
    String prevDui = null;
    Descriptor dui = null;
    while (results.next()) {
      final Atom atom = (Atom) results.get()[0];
      if (atom.getDescriptorId() == null || atom.getDescriptorId().isEmpty()) {
        continue;
      }
      if (prevDui == null || !prevDui.equals(atom.getDescriptorId())) {
        if (dui != null) {
          // compute preferred name
          dui.setName(getComputedPreferredName(dui));
          addDescriptor(dui);
          descriptorIdMap.put(dui.getTerminology() + dui.getTerminologyId(),
              dui.getId());
          logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);
        }
        dui = new DescriptorJpa();
        dui.setTimestamp(releaseVersionDate);
        dui.setLastModified(releaseVersionDate);
        dui.setLastModifiedBy(loader);
        dui.setPublished(true);
        dui.setPublishable(true);
        dui.setTerminology(atom.getTerminology());
        dui.setTerminologyId(atom.getDescriptorId());
        dui.setVersion(atom.getVersion());
        dui.setWorkflowStatus(published);
      }
      dui.addAtom(atom);
      prevDui = atom.getDescriptorId();
    }
    if (dui != null) {
      dui.setName(getComputedPreferredName(dui));
      addDescriptor(dui);
      descriptorIdMap.put(dui.getTerminology() + dui.getTerminologyId(),
          dui.getId());
      commitClearBegin();
    }
    results.close();

    // Use flag to decide whether to handle codes
    if (codesFlag) {
      Logger.getLogger(getClass()).info("  Add codes");
      objectCt = 0;
      // NOTE: Hibernate-specific to support iterating
      // Skip NOCODE
      hQuery = session
          .createQuery("select a from AtomJpa a " + "where codeId is not null "
              + "and codeId != '' and timestamp = :timestamp "
              + "order by terminology, codeId");
      hQuery.setParameter("timestamp", releaseVersionDate);
      hQuery.setReadOnly(true).setFetchSize(1000);
      results = hQuery.scroll(ScrollMode.FORWARD_ONLY);
      String prevCode = null;
      Code code = null;
      while (results.next()) {
        final Atom atom = (Atom) results.get()[0];
        if (atom.getCodeId() == null || atom.getCodeId().isEmpty()) {
          continue;
        }
        // skip where code == concept - problem because rels connect to the code
        // if (atom.getCodeId().equals(atom.getConceptId())) {
        // continue;
        // }
        // skip where code == descriptor
        // if (atom.getCodeId().equals(atom.getDescriptorId())) {
        // continue;
        // }
        if (prevCode == null || !prevCode.equals(atom.getCodeId())) {
          if (code != null) {
            // compute preferred name
            code.setName(getComputedPreferredName(code));
            addCode(code);
            codeIdMap.put(code.getTerminology() + code.getTerminologyId(),
                code.getId());
            logAndCommit(++objectCt, RootService.logCt, RootService.commitCt);
          }
          code = new CodeJpa();
          code.setTimestamp(releaseVersionDate);
          code.setLastModified(releaseVersionDate);
          code.setLastModifiedBy(loader);
          code.setPublished(true);
          code.setPublishable(true);
          code.setTerminology(atom.getTerminology());
          code.setTerminologyId(atom.getCodeId());
          code.setVersion(atom.getVersion());
          code.setWorkflowStatus(published);
        }
        code.addAtom(atom);
        prevCode = atom.getCodeId();
      }
      if (code != null) {
        code.setName(getComputedPreferredName(code));
        addCode(code);
        codeIdMap.put(code.getTerminology() + code.getTerminologyId(),
            code.getId());
        commitClearBegin();
      }
      results.close();
    }


    // commit
    commitClearBegin();

    // Update all root terminologies now that we know languages and names
    for (final RootTerminology root : loadedRootTerminologies.values()) {
      updateRootTerminology(root);
    }

    // Update all root terminologies now that we know languages and names
    for (final Terminology terminology : loadedTerminologies.values()) {
      updateTerminology(terminology);
    }
    commitClearBegin();

  }

  /**
   * Load atoms before computing.
   *
   * @param atomClass the atom class
   * @return the computed preferred name
   * @throws Exception the exception
   */
  @Override
  public String getComputedPreferredName(AtomClass atomClass) throws Exception {
    final List<Atom> atoms = new ArrayList<>();
    for (final Atom atom : atomClass.getAtoms()) {
      atoms.add(getAtom(atom.getId()));
    }
    atomClass.setAtoms(atoms);
    return super.getComputedPreferredName(atomClass);
  }



  /**
   * Reset.
   *
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public void reset() throws Exception {
    // do nothing
  }

  /**
   * Fires a {@link ProgressEvent}.
   * @param pct percent done
   * @param note progress note
   */
  public void fireProgressEvent(int pct, String note) throws Exception {
    final ProgressEvent pe = new ProgressEvent(this, pct, pct, note);
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).updateProgress(pe);
    }
    Logger.getLogger(getClass()).info("    " + pct + "% " + note);
  }

  /**
   * Adds the progress listener.
   *
   * @param l the l
   */
  /* see superclass */
  @Override
  public void addProgressListener(ProgressListener l) {
    listeners.add(l);
  }

  /**
   * Removes the progress listener.
   *
   * @param l the l
   */
  /* see superclass */
  @Override
  public void removeProgressListener(ProgressListener l) {
    listeners.remove(l);
  }

  /**
   * Cancel.
   */
  /* see superclass */
  @Override
  public void cancel() {
    throw new UnsupportedOperationException("cannot cancel.");
  }

  /**
   * Returns the elapsed time.
   *
   * @param time the time
   * @return the elapsed time
   */
  @SuppressWarnings({
      "boxing", "unused"
  })
  private static Long getElapsedTime(long time) {
    return (System.nanoTime() - time) / 1000000000;
  }

  /**
   * Returns the total elapsed time str.
   *
   * @param time the time
   * @return the total elapsed time str
   */
  @SuppressWarnings("boxing")
  private static String getTotalElapsedTimeStr(long time) {
    Long resultnum = (System.nanoTime() - time) / 1000000000;
    String result = resultnum.toString() + "s";
    resultnum = resultnum / 60;
    result = result + " / " + resultnum.toString() + "m";
    resultnum = resultnum / 60;
    result = result + " / " + resultnum.toString() + "h";
    return result;
  }

  /**
   * Close.
   *
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public void close() throws Exception {
    super.close();
    readers = null;
  }

  /**
   * Standard hashing strategy.
   */
  @SuppressWarnings("serial")
  public class StandardStrategy implements HashingStrategy<String> {

    /**
     * Instantiates an empty {@link StandardStrategy}.
     */
    public StandardStrategy() {
      // n/a
    }

    /* see superclass */
    @Override
    public int computeHashCode(String object) {
      return object.hashCode();
    }

    /* see superclass */
    @Override
    public boolean equals(String o1, String o2) {
      return o1.equals(o2);
    }

  }

  /**
   * Indicates whether or not extension module is the case.
   *
   * @param moduleId the module id
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  @SuppressWarnings("static-method")
  private boolean isExtensionModule(String moduleId) {
    return !moduleId.equals(coreModuleId) && !moduleId.equals(metadataModuleId);
  }

}
