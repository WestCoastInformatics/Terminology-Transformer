package com.wci.tt;

import com.wci.tt.helpers.DataContextType;
import com.wci.tt.helpers.HasTerminology;

public interface DataContext extends HasTerminology {

  public DataContextType getDataType();

  public void setDataType(DataContextType dataType);

  public String getCustomer();

  public  void setCustomer(String customer);

  public String getSemanticType();

  public void setSemanticType(String semanticType);

  public String getSpecialty();

  public void setSpecialty(String specialty);

}
