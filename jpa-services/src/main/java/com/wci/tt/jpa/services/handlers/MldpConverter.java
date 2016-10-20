/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.tt.jpa.services.handlers;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.wci.tt.DataContext;
import com.wci.tt.TransformRecord;
import com.wci.tt.helpers.DataContextTuple;
import com.wci.tt.helpers.DataContextType;
import com.wci.tt.jpa.helpers.DataContextTupleJpa;
import com.wci.tt.jpa.infomodels.ConditionModel;
import com.wci.tt.jpa.infomodels.ProcedureModel;
import com.wci.tt.jpa.infomodels.SiteModel;
import com.wci.tt.jpa.services.helper.DataContextMatcher;
import com.wci.tt.services.handlers.ConverterHandler;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.jpa.services.ContentServiceJpa;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.services.ContentService;

/**
 * Converter for MDLP services, converts identifiers to string names.
 */
public class MldpConverter extends AbstractAcceptsHandler
    implements ConverterHandler {

  /**
   * Instantiates an empty {@link MldpConverter}.
   *
   * @throws Exception the exception
   */
  public MldpConverter() throws Exception {
    // Configure input/output matchers

    // Convert a condition model to itself
    DataContextMatcher matcher = new DataContextMatcher();
    matcher.configureContext(DataContextType.INFO_MODEL, null, null, null,
        ConditionModel.class.getName(), null, null);
    addMatcher(matcher, matcher);

    // Convert a procedure model to itself
    matcher = new DataContextMatcher();
    matcher.configureContext(DataContextType.INFO_MODEL, null, null, null,
        ProcedureModel.class.getName(), null, null);
    addMatcher(matcher, matcher);

  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public String getName() {
    return "MLDP Converter Handler";
  }

  /* see superclass */
  @Override
  public DataContextTuple convert(String inputString, TransformRecord record)
    throws Exception {
    Logger.getLogger(getClass()).debug("  convert - " + inputString);

    final DataContext inputContext = record.getInputContext();
    final DataContext outputContext = record.getProviderOutputContext();
    // final String origInputString = record.getInputString();
    // final DataContext origInputContext = record.getOutputContext();

    // Validate input/output context
    validate(inputContext, outputContext);

    // DefaultHandler returns "converted form" of input with ids looked up as
    // names
    final DataContextTuple tuple = new DataContextTupleJpa();

    final ContentService service = new ContentServiceJpa();

    // Ensure that input is valid, then pass through
    if (inputString != null && !inputString.isEmpty() && inputContext != null
        && outputContext != null) {

      // Handle procedure model
      if (inputContext.getInfoModelClass()
          .equals(ProcedureModel.class.getName())) {

        if (record.getOutputs().size() == 1) {
          final String data = record.getOutputs().get(0).getValue();
          final ProcedureModel model = new ProcedureModel().getModel(data);

          model.setProcedure(getNameForId(service, model.getProcedure()));
          model.setCondition(getNameForId(service, model.getCondition()));
          if (model.getSite() != null) {
            final SiteModel site = model.getSite();
            site.setBodySite(getNameForId(service, site.getBodySite()));
            site.setLaterality(getNameForId(service, site.getLaterality()));
            site.setPosition(getNameForId(service, site.getPosition()));
          }
          if (model.getRelatedSite() != null) {
            final SiteModel site = model.getRelatedSite();
            site.setBodySite(getNameForId(service, site.getBodySite()));
            site.setLaterality(getNameForId(service, site.getLaterality()));
            site.setPosition(getNameForId(service, site.getPosition()));
          }
          model.setApproach(getNameForId(service, model.getApproach()));
          model.setDevice(getNameForId(service, model.getDevice()));
          model.setSubstance(getNameForId(service, model.getSubstance()));
          model.setQual(getNameForId(service, model.getQual()));

          tuple.setData(model.getModelValue());
        }

      } else if (inputContext.getInfoModelClass()
          .equals(ConditionModel.class.getName())) {

        if (record.getOutputs().size() == 1) {
          final String data = record.getOutputs().get(0).getValue();
          final ConditionModel model = new ConditionModel().getModel(data);

          model.setCondition(getNameForId(service, model.getCondition()));
          model.setSubcondition(getNameForId(service, model.getSubcondition()));
          if (model.getSite() != null) {
            final SiteModel site = model.getSite();
            site.setBodySite(getNameForId(service, site.getBodySite()));
            site.setLaterality(getNameForId(service, site.getLaterality()));
            site.setPosition(getNameForId(service, site.getPosition()));
          }
          model.setCause(getNameForId(service, model.getCause()));
          model.setOccurrence(getNameForId(service, model.getOccurrence()));
          model.setCourse(getNameForId(service, model.getCourse()));
          model.setCategory(getNameForId(service, model.getCategory()));
          model.setSeverity(getNameForId(service, model.getSeverity()));

          tuple.setData(model.getModelValue());
        }

      }

      tuple.setDataContext(outputContext);
    }

    service.close();
    return tuple;
  }

  /**
   * Returns the name for id.
   *
   * @param service the service
   * @param terminologyId the terminology id
   * @return the name for id
   * @throws Exception the exception
   */
  @SuppressWarnings("static-method")
  public String getNameForId(ContentService service, String terminologyId)
    throws Exception {
    if (terminologyId == null) {
      return null;
    }
    if (terminologyId.equals("")) {
      return "";
    }
    final Concept concept =
        service.getConcept(terminologyId, "HKT", "latest", Branch.ROOT);
    return concept.getName();
  }

  /* see superclass */
  @Override
  public void close() throws Exception {
    // n/a - nothing opened
  }

}
