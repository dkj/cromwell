/*
 * Cromwell Server REST API
 * Describes the REST API provided by a Cromwell server
 *
 * The version of the OpenAPI document: 30
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package cromwell.client.model;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import cromwell.client.model.MapValueType;
import cromwell.client.model.ValueTypeObjectFieldTypesInner;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cromwell.client.JSON;

/**
 * The type expected for a given value.
 */
@ApiModel(description = "The type expected for a given value.")
@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2022-10-27T17:48:17.553365Z[Etc/UTC]")
public class ValueType {
  /**
   * The type of this value
   */
  @JsonAdapter(TypeNameEnum.Adapter.class)
  public enum TypeNameEnum {
    STRING("String"),
    
    FILE("File"),
    
    DIRECTORY("Directory"),
    
    FLOAT("Float"),
    
    INT("Int"),
    
    BOOLEAN("Boolean"),
    
    OPTIONAL("Optional"),
    
    ARRAY("Array"),
    
    TUPLE("Tuple"),
    
    MAP("Map"),
    
    OBJECT("Object"),
    
    PAIR("Pair");

    private String value;

    TypeNameEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    public static TypeNameEnum fromValue(String value) {
      for (TypeNameEnum b : TypeNameEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    public static class Adapter extends TypeAdapter<TypeNameEnum> {
      @Override
      public void write(final JsonWriter jsonWriter, final TypeNameEnum enumeration) throws IOException {
        jsonWriter.value(enumeration.getValue());
      }

      @Override
      public TypeNameEnum read(final JsonReader jsonReader) throws IOException {
        String value =  jsonReader.nextString();
        return TypeNameEnum.fromValue(value);
      }
    }
  }

  public static final String SERIALIZED_NAME_TYPE_NAME = "typeName";
  @SerializedName(SERIALIZED_NAME_TYPE_NAME)
  private TypeNameEnum typeName;

  public static final String SERIALIZED_NAME_OPTIONAL_TYPE = "optionalType";
  @SerializedName(SERIALIZED_NAME_OPTIONAL_TYPE)
  private ValueType optionalType;

  public static final String SERIALIZED_NAME_ARRAY_TYPE = "arrayType";
  @SerializedName(SERIALIZED_NAME_ARRAY_TYPE)
  private ValueType arrayType;

  public static final String SERIALIZED_NAME_MAP_TYPE = "mapType";
  @SerializedName(SERIALIZED_NAME_MAP_TYPE)
  private MapValueType mapType;

  public static final String SERIALIZED_NAME_TUPLE_TYPES = "tupleTypes";
  @SerializedName(SERIALIZED_NAME_TUPLE_TYPES)
  private List<ValueType> tupleTypes = null;

  public static final String SERIALIZED_NAME_OBJECT_FIELD_TYPES = "objectFieldTypes";
  @SerializedName(SERIALIZED_NAME_OBJECT_FIELD_TYPES)
  private List<ValueTypeObjectFieldTypesInner> objectFieldTypes = null;

  public ValueType() {
  }

  public ValueType typeName(TypeNameEnum typeName) {
    
    this.typeName = typeName;
    return this;
  }

   /**
   * The type of this value
   * @return typeName
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "The type of this value")

  public TypeNameEnum getTypeName() {
    return typeName;
  }


  public void setTypeName(TypeNameEnum typeName) {
    this.typeName = typeName;
  }


  public ValueType optionalType(ValueType optionalType) {
    
    this.optionalType = optionalType;
    return this;
  }

   /**
   * Get optionalType
   * @return optionalType
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public ValueType getOptionalType() {
    return optionalType;
  }


  public void setOptionalType(ValueType optionalType) {
    this.optionalType = optionalType;
  }


  public ValueType arrayType(ValueType arrayType) {
    
    this.arrayType = arrayType;
    return this;
  }

   /**
   * Get arrayType
   * @return arrayType
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public ValueType getArrayType() {
    return arrayType;
  }


  public void setArrayType(ValueType arrayType) {
    this.arrayType = arrayType;
  }


  public ValueType mapType(MapValueType mapType) {
    
    this.mapType = mapType;
    return this;
  }

   /**
   * Get mapType
   * @return mapType
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public MapValueType getMapType() {
    return mapType;
  }


  public void setMapType(MapValueType mapType) {
    this.mapType = mapType;
  }


  public ValueType tupleTypes(List<ValueType> tupleTypes) {
    
    this.tupleTypes = tupleTypes;
    return this;
  }

  public ValueType addTupleTypesItem(ValueType tupleTypesItem) {
    if (this.tupleTypes == null) {
      this.tupleTypes = new ArrayList<>();
    }
    this.tupleTypes.add(tupleTypesItem);
    return this;
  }

   /**
   * Get tupleTypes
   * @return tupleTypes
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public List<ValueType> getTupleTypes() {
    return tupleTypes;
  }


  public void setTupleTypes(List<ValueType> tupleTypes) {
    this.tupleTypes = tupleTypes;
  }


  public ValueType objectFieldTypes(List<ValueTypeObjectFieldTypesInner> objectFieldTypes) {
    
    this.objectFieldTypes = objectFieldTypes;
    return this;
  }

  public ValueType addObjectFieldTypesItem(ValueTypeObjectFieldTypesInner objectFieldTypesItem) {
    if (this.objectFieldTypes == null) {
      this.objectFieldTypes = new ArrayList<>();
    }
    this.objectFieldTypes.add(objectFieldTypesItem);
    return this;
  }

   /**
   * Get objectFieldTypes
   * @return objectFieldTypes
  **/
  @javax.annotation.Nullable
  @ApiModelProperty(value = "")

  public List<ValueTypeObjectFieldTypesInner> getObjectFieldTypes() {
    return objectFieldTypes;
  }


  public void setObjectFieldTypes(List<ValueTypeObjectFieldTypesInner> objectFieldTypes) {
    this.objectFieldTypes = objectFieldTypes;
  }

  /**
   * A container for additional, undeclared properties.
   * This is a holder for any undeclared properties as specified with
   * the 'additionalProperties' keyword in the OAS document.
   */
  private Map<String, Object> additionalProperties;

  /**
   * Set the additional (undeclared) property with the specified name and value.
   * If the property does not already exist, create it otherwise replace it.
   */
  public ValueType putAdditionalProperty(String key, Object value) {
    if (this.additionalProperties == null) {
        this.additionalProperties = new HashMap<String, Object>();
    }
    this.additionalProperties.put(key, value);
    return this;
  }

  /**
   * Return the additional (undeclared) property.
   */
  public Map<String, Object> getAdditionalProperties() {
    return additionalProperties;
  }

  /**
   * Return the additional (undeclared) property with the specified name.
   */
  public Object getAdditionalProperty(String key) {
    if (this.additionalProperties == null) {
        return null;
    }
    return this.additionalProperties.get(key);
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ValueType valueType = (ValueType) o;
    return Objects.equals(this.typeName, valueType.typeName) &&
        Objects.equals(this.optionalType, valueType.optionalType) &&
        Objects.equals(this.arrayType, valueType.arrayType) &&
        Objects.equals(this.mapType, valueType.mapType) &&
        Objects.equals(this.tupleTypes, valueType.tupleTypes) &&
        Objects.equals(this.objectFieldTypes, valueType.objectFieldTypes)&&
        Objects.equals(this.additionalProperties, valueType.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(typeName, optionalType, arrayType, mapType, tupleTypes, objectFieldTypes, additionalProperties);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ValueType {\n");
    sb.append("    typeName: ").append(toIndentedString(typeName)).append("\n");
    sb.append("    optionalType: ").append(toIndentedString(optionalType)).append("\n");
    sb.append("    arrayType: ").append(toIndentedString(arrayType)).append("\n");
    sb.append("    mapType: ").append(toIndentedString(mapType)).append("\n");
    sb.append("    tupleTypes: ").append(toIndentedString(tupleTypes)).append("\n");
    sb.append("    objectFieldTypes: ").append(toIndentedString(objectFieldTypes)).append("\n");
    sb.append("    additionalProperties: ").append(toIndentedString(additionalProperties)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }


  public static HashSet<String> openapiFields;
  public static HashSet<String> openapiRequiredFields;

  static {
    // a set of all properties/fields (JSON key names)
    openapiFields = new HashSet<String>();
    openapiFields.add("typeName");
    openapiFields.add("optionalType");
    openapiFields.add("arrayType");
    openapiFields.add("mapType");
    openapiFields.add("tupleTypes");
    openapiFields.add("objectFieldTypes");

    // a set of required properties/fields (JSON key names)
    openapiRequiredFields = new HashSet<String>();
  }

 /**
  * Validates the JSON Object and throws an exception if issues found
  *
  * @param jsonObj JSON Object
  * @throws IOException if the JSON Object is invalid with respect to ValueType
  */
  public static void validateJsonObject(JsonObject jsonObj) throws IOException {
      if (jsonObj == null) {
        if (ValueType.openapiRequiredFields.isEmpty()) {
          return;
        } else { // has required fields
          throw new IllegalArgumentException(String.format("The required field(s) %s in ValueType is not found in the empty JSON string", ValueType.openapiRequiredFields.toString()));
        }
      }
      if ((jsonObj.get("typeName") != null && !jsonObj.get("typeName").isJsonNull()) && !jsonObj.get("typeName").isJsonPrimitive()) {
        throw new IllegalArgumentException(String.format("Expected the field `typeName` to be a primitive type in the JSON string but got `%s`", jsonObj.get("typeName").toString()));
      }
      // validate the optional field `optionalType`
      if (jsonObj.get("optionalType") != null && !jsonObj.get("optionalType").isJsonNull()) {
        ValueType.validateJsonObject(jsonObj.getAsJsonObject("optionalType"));
      }
      // validate the optional field `arrayType`
      if (jsonObj.get("arrayType") != null && !jsonObj.get("arrayType").isJsonNull()) {
        ValueType.validateJsonObject(jsonObj.getAsJsonObject("arrayType"));
      }
      // validate the optional field `mapType`
      if (jsonObj.get("mapType") != null && !jsonObj.get("mapType").isJsonNull()) {
        MapValueType.validateJsonObject(jsonObj.getAsJsonObject("mapType"));
      }
      JsonArray jsonArraytupleTypes = jsonObj.getAsJsonArray("tupleTypes");
      if (jsonArraytupleTypes != null) {
        // ensure the json data is an array
        if (!jsonObj.get("tupleTypes").isJsonArray()) {
          throw new IllegalArgumentException(String.format("Expected the field `tupleTypes` to be an array in the JSON string but got `%s`", jsonObj.get("tupleTypes").toString()));
        }

        // validate the optional field `tupleTypes` (array)
        for (int i = 0; i < jsonArraytupleTypes.size(); i++) {
          ValueType.validateJsonObject(jsonArraytupleTypes.get(i).getAsJsonObject());
        };
      }
      JsonArray jsonArrayobjectFieldTypes = jsonObj.getAsJsonArray("objectFieldTypes");
      if (jsonArrayobjectFieldTypes != null) {
        // ensure the json data is an array
        if (!jsonObj.get("objectFieldTypes").isJsonArray()) {
          throw new IllegalArgumentException(String.format("Expected the field `objectFieldTypes` to be an array in the JSON string but got `%s`", jsonObj.get("objectFieldTypes").toString()));
        }

        // validate the optional field `objectFieldTypes` (array)
        for (int i = 0; i < jsonArrayobjectFieldTypes.size(); i++) {
          ValueTypeObjectFieldTypesInner.validateJsonObject(jsonArrayobjectFieldTypes.get(i).getAsJsonObject());
        };
      }
  }

  public static class CustomTypeAdapterFactory implements TypeAdapterFactory {
    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
       if (!ValueType.class.isAssignableFrom(type.getRawType())) {
         return null; // this class only serializes 'ValueType' and its subtypes
       }
       final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
       final TypeAdapter<ValueType> thisAdapter
                        = gson.getDelegateAdapter(this, TypeToken.get(ValueType.class));

       return (TypeAdapter<T>) new TypeAdapter<ValueType>() {
           @Override
           public void write(JsonWriter out, ValueType value) throws IOException {
             JsonObject obj = thisAdapter.toJsonTree(value).getAsJsonObject();
             obj.remove("additionalProperties");
             // serialize additonal properties
             if (value.getAdditionalProperties() != null) {
               for (Map.Entry<String, Object> entry : value.getAdditionalProperties().entrySet()) {
                 if (entry.getValue() instanceof String)
                   obj.addProperty(entry.getKey(), (String) entry.getValue());
                 else if (entry.getValue() instanceof Number)
                   obj.addProperty(entry.getKey(), (Number) entry.getValue());
                 else if (entry.getValue() instanceof Boolean)
                   obj.addProperty(entry.getKey(), (Boolean) entry.getValue());
                 else if (entry.getValue() instanceof Character)
                   obj.addProperty(entry.getKey(), (Character) entry.getValue());
                 else {
                   obj.add(entry.getKey(), gson.toJsonTree(entry.getValue()).getAsJsonObject());
                 }
               }
             }
             elementAdapter.write(out, obj);
           }

           @Override
           public ValueType read(JsonReader in) throws IOException {
             JsonObject jsonObj = elementAdapter.read(in).getAsJsonObject();
             validateJsonObject(jsonObj);
             // store additional fields in the deserialized instance
             ValueType instance = thisAdapter.fromJsonTree(jsonObj);
             for (Map.Entry<String, JsonElement> entry : jsonObj.entrySet()) {
               if (!openapiFields.contains(entry.getKey())) {
                 if (entry.getValue().isJsonPrimitive()) { // primitive type
                   if (entry.getValue().getAsJsonPrimitive().isString())
                     instance.putAdditionalProperty(entry.getKey(), entry.getValue().getAsString());
                   else if (entry.getValue().getAsJsonPrimitive().isNumber())
                     instance.putAdditionalProperty(entry.getKey(), entry.getValue().getAsNumber());
                   else if (entry.getValue().getAsJsonPrimitive().isBoolean())
                     instance.putAdditionalProperty(entry.getKey(), entry.getValue().getAsBoolean());
                   else
                     throw new IllegalArgumentException(String.format("The field `%s` has unknown primitive type. Value: %s", entry.getKey(), entry.getValue().toString()));
                 } else { // non-primitive type
                   instance.putAdditionalProperty(entry.getKey(), gson.fromJson(entry.getValue(), HashMap.class));
                 }
               }
             }
             return instance;
           }

       }.nullSafe();
    }
  }

 /**
  * Create an instance of ValueType given an JSON string
  *
  * @param jsonString JSON string
  * @return An instance of ValueType
  * @throws IOException if the JSON string is invalid with respect to ValueType
  */
  public static ValueType fromJson(String jsonString) throws IOException {
    return JSON.getGson().fromJson(jsonString, ValueType.class);
  }

 /**
  * Convert an instance of ValueType to an JSON string
  *
  * @return JSON string
  */
  public String toJson() {
    return JSON.getGson().toJson(this);
  }
}

