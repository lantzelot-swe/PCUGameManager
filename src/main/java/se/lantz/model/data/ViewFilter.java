package se.lantz.model.data;

public class ViewFilter
{
  public static final String BEGINS_WITH_TEXT = "Begins with text";
  public static final String ENDS_WITH_TEXT = "Ends with text";
  public static final String CONTAINS_TEXT = "Contains text";
  public static final String EQUALS_TEXT = "Equals text";
  public static final String IS = "Is";
  public static final String BEFORE = "Before";
  public static final String AFTER = "After";
  
 
  private String filterData;
  private String operator;
  private String field;

  public ViewFilter(String field, String operator, String filterData)
  {
    super();
    this.filterData = filterData;
    this.operator = operator;
    this.field = field;
  }

  public String getFilterData()
  {
    return filterData;
  }

  public void setFilterData(String filterData)
  {
    this.filterData = filterData;
  }

  public String getOperator()
  {
    return operator;
  }

  public void setOperator(String operator)
  {
    this.operator = operator;
  }

  public String getField()
  {
    return field;
  }

  public void setField(String field)
  {
    this.field = field;
  }
}
