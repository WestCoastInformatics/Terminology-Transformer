/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.rest.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.wci.tt.jpa.services.rest.AbbreviationRest;
import com.wci.tt.jpa.services.rest.TransformServiceRest;
import com.wci.umls.server.UserRole;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.PfsParameter;
import com.wci.umls.server.helpers.TypeKeyValue;
import com.wci.umls.server.helpers.TypeKeyValueList;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.helpers.PfsParameterJpa;
import com.wci.umls.server.jpa.helpers.TypeKeyValueJpa;
import com.wci.umls.server.jpa.services.ProjectServiceJpa;
import com.wci.umls.server.jpa.services.SecurityServiceJpa;
import com.wci.umls.server.rest.impl.RootServiceRestImpl;
import com.wci.umls.server.services.ProjectService;
import com.wci.umls.server.services.SecurityService;
import com.wci.umls.server.services.helpers.PushBackReader;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

/**
 * Class implementation the REST Service for Transform routines for
 * {@link TransformServiceRest}.
 * 
 * Includes hibernate tags for MEME database.
 */
@Path("/abbr")
@Api(value = "/abbr")
@SwaggerDefinition(info = @Info(description = "Abbreviation Operations", title = "Abbreviation Operations", version = "1.0.0"))
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class AbbreviationRestImpl extends RootServiceRestImpl
    implements AbbreviationRest {
  
  static {
   ;
  }

  /** The security service. */
  private SecurityService securityService;

  /**
   * Instantiates an empty {@link AbbreviationRestImpl}.
   *
   * @throws Exception the exception
   */
  public AbbreviationRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
    System.out.println("AbbreviationRestImpl: YAY ECLIPSE YAY");
  }

  @Override
  @Path("/import/{type}")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @ApiOperation(value = "Import abbreviations", notes = "Import abbreviations of single type from comma or tab-delimited file", response = TypeKeyValueJpa.class)
  public ValidationResult importAbbreviations(
    @ApiParam(value = "Form data header", required = true) @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
    @ApiParam(value = "Content of definition file", required = true) @FormDataParam("file") InputStream in,
    @ApiParam(value = "Type of abbreviation, e.g. medAbbr", required = true) @PathParam("type") String type,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info("RESTful call (TKV): /import");
    final ProjectService projectService = new ProjectServiceJpa();
    try {
      authorizeApp(securityService, authToken, "import abbreviations",
          UserRole.VIEWER);
      ValidationResult result = new ValidationResultJpa();
      PushBackReader pbr = null;
      try {

        // Read from input stream

        Reader reader = new InputStreamReader(in, "UTF-8");
        pbr = new PushBackReader(reader);
        int addedCt = 0;
        int lineCt = 0;
        PfsParameter pfs = new PfsParameterJpa();
        pfs.setMaxResults(1);

        projectService.setTransactionPerOperation(false);
        projectService.beginTransaction();
        String line = pbr.readLine();

        // check for empty contents
        if (line == null) {
          throw new Exception("Empty file");
        }

        // skip header line if present
        if (line.toLowerCase().startsWith("abbreviation")) {
          lineCt++;
          result.getComments().add("Skipped header line: " + line);
          line = pbr.readLine();
        }

        // start processing
        do {
          lineCt++;
          line = line.replace("[^S\t ", "");
          final String fields[] = line.split("\t");

          System.out.println("line " + lineCt);

          if (fields.length == 2) {
            System.out.println("  field 0: " + fields[0]);
            System.out.println("  field 1: " + fields[1]);

            // if fields valid
            if (fields[0] != null && !fields[0].isEmpty() && fields[1] != null
                && !fields[1].isEmpty()) {

              // check for matches
              final TypeKeyValueList matches =
                  projectService.findTypeKeyValuesForQuery("type:\"" + type
                      + "\"" + " AND key:\"" + fields[0] + "\"", pfs);
              if (matches.getTotalCount() > 0) {
                System.out.println("matches " + matches);
                boolean valueMatched = false;
                for (TypeKeyValue match : matches.getObjects()) {
                  if (fields[1].equals(match.getValue())) {
                    valueMatched = true;
                  }
                }
                if (!valueMatched) {
                  // add different expansion for same
                  System.out.println("Adding " + fields[0] + " / " + fields[1]);
                  projectService.addTypeKeyValue(
                      new TypeKeyValueJpa(type, fields[0], fields[1]));
                  addedCt++;
                }

              } else {
                // add new abbreviation/expansion pair
                System.out.println("Adding " + fields[0] + " / " + fields[1]);
                projectService.addTypeKeyValue(
                    new TypeKeyValueJpa(type, fields[0], fields[1]));
                addedCt++;
              }
            } else {
              // invalid line, do nothing
            }
          } else {
            // invalid line, do nothing
          }
        } while ((line = pbr.readLine()) != null);

        projectService.commit();
        if (addedCt == 0) {
          result.getWarnings().add("No abbreviations added.");
        } else {
          result.getComments()
              .add("Successfully created " + addedCt + " abbreviations");
        }
        result.getWarnings().add("Skipped " + (lineCt - addedCt) + " lines");

      } catch (Exception e) {
        projectService.rollback();
        result.addError("Unexpected error: " + e.getMessage());
      } finally {
        if (pbr != null) {
          pbr.close();
        }
      }
      return result;
    } catch (

    Exception e) {
      handleException(e, "trying to import abbreviations ");
      return null;
    } finally {
      projectService.close();
      securityService.close();
    }
  }

  @Override
  @Path("/import/{type}/validate")
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @ApiOperation(value = "Validate abbreviations import file", notes = "Validates abbreviations from comma or tab-delimited file", response = TypeKeyValueJpa.class)
  public ValidationResult validateAbbreviationsFile(
    @ApiParam(value = "Form data header", required = true) @FormDataParam("file") FormDataContentDisposition contentDispositionHeader,
    @ApiParam(value = "Content of definition file", required = true) @FormDataParam("file") InputStream in,
    @ApiParam(value = "Type of abbreviation, e.g. medAbbr", required = true) @PathParam("type") String type,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info("RESTful call (TKV): /find");
    final ProjectService projectService = new ProjectServiceJpa();
    try {
      authorizeApp(securityService, authToken, "validate abbreviations file",
          UserRole.ADMINISTRATOR);
      ValidationResult result = new ValidationResultJpa();
      PushBackReader pbr = null;
      try {

        // Read from input stream
        Reader reader = new InputStreamReader(in, "UTF-8");
        pbr = new PushBackReader(reader);
        int lineCt = 0;
        PfsParameter pfs = new PfsParameterJpa();
        pfs.setMaxResults(1);

        String line = pbr.readLine();

        // check for empty contents
        if (line == null) {
          throw new Exception("Empty file");
        }

        // skip header line if present
        if (line.toLowerCase().startsWith("abbreviation")) {
          lineCt++;
          result.getComments().add("Skipped header line: " + line);
          line = pbr.readLine();
        }

        // start processing
        do {
          lineCt++;
          line = line.replace("[^S\t ", "");
          final String fields[] = line.split("\t");

          System.out.println("line " + lineCt);

          if (fields.length == 2) {
            System.out.println("  field 0: " + fields[0]);
            System.out.println("  field 1: " + fields[1]);
            if (fields[0] != null && !fields[0].isEmpty() && fields[1] != null
                && !fields[1].isEmpty()) {
              final TypeKeyValueList matches =
                  projectService.findTypeKeyValuesForQuery("type:\"" + type
                      + "\"" + " AND key:\"" + fields[0] + "\"", pfs);
              if (matches.getTotalCount() > 0) {
                System.out.println("matches " + matches);
                for (TypeKeyValue match : matches.getObjects()) {
                  if (fields[1].equals(match.getValue())) {
                    result.getWarnings()
                        .add("[Line " + lineCt
                            + "] Duplicate Abbreviation/expansion pair: "
                            + fields[0] + " / " + match.getValue());
                  } else {
                    result.getWarnings()
                        .add("[Line " + lineCt + "] Abbreviation " + fields[0]
                            + " already exists with expansion: "
                            + match.getValue());
                  }
                }

              }
            } else {
              result.getWarnings()
                  .add("[Line " + lineCt + "] Incomplete line: " + line);
            }
          } else {
            String fieldsStr = "";
            for (int i = 0; i < fields.length; i++) {
              fieldsStr += "[" + fields[i] + "] ";
            }
            result.getWarnings()
                .add("[Line " + lineCt + "] Expected two fields " + lineCt
                    + " but found " + fields.length + ": " + fieldsStr);
          }
        } while ((line = pbr.readLine()) != null);
      } catch (Exception e) {
        result.getErrors().add("Unexpected error: " + e.getMessage());
      } finally {
        if (pbr != null) {
          pbr.close();
        }
      }

      return result;

    } catch (

    Exception e) {
      handleException(e, "trying to validate abbreviations file ");
      return null;
    } finally {
      projectService.close();
      securityService.close();
    }
  }

  @GET
  @Override
  @Produces("application/octet-stream")
  @Path("/export/{type}")
  @ApiOperation(value = "Export abbreviations", notes = "Exports abbreviations for type as comma or tab-delimited file", response = TypeKeyValueJpa.class)
  public InputStream exportAbbreviationsFile(
    @ApiParam(value = "Type of abbreviation, e.g. medAbbr", required = true) @PathParam("type") String type,
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info("RESTful call (TKV): /find");
    final ProjectService projectService = new ProjectServiceJpa();
    try {
      authorizeApp(securityService, authToken, "export abbreviations",
          UserRole.USER);

      // Write a header
      // Obtain members for refset,
      // Write RF2 simple refset pattern to a StringBuilder
      // wrap and return the string for that as an input stream

      StringBuilder sb = new StringBuilder();
      sb.append("abbreviation").append("\t");
      sb.append("expansion").append("\t");
      sb.append("\r\n");

      // sort by key
      PfsParameter pfs = new PfsParameterJpa();
      pfs.setSortField("key");

      TypeKeyValueList abbrs = projectService
          .findTypeKeyValuesForQuery("type:\"" + type + "\"", pfs);
      for (TypeKeyValue abbr : abbrs.getObjects()) {
        if (!abbr.getKey().startsWith("##")) {
          sb.append(abbr.getKey()).append("\t");
          sb.append(abbr.getValue()).append("\t");
          sb.append("\r\n");
        }
      }
      return new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
    } catch (Exception e) {
      handleException(e, "trying to export abbreviations");
    } finally {
      projectService.close();
      securityService.close();
    }
    return null;
  }

  @Override
  public TypeKeyValue addAbbreviation(TypeKeyValueJpa typeKeyValue,
    String authToken) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TypeKeyValue getAbbreviation(Long id, String authToken)
    throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void updateAbbreviation(TypeKeyValueJpa typeKeyValue, String authToken)
    throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void removeAbbreviation(Long id, String authToken) throws Exception {
    // TODO Auto-generated method stub
    
  }

  @Override
  public TypeKeyValueList findAbbreviations(String query, PfsParameterJpa pfs,
    String authToken) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

 

}
