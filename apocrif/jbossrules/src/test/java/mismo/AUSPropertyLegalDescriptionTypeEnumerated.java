//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.05.21 at 09:59:49 PM BST 
//


package mismo;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AUS_PropertyLegalDescriptionTypeEnumerated.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AUS_PropertyLegalDescriptionTypeEnumerated">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MetesAndBounds"/>
 *     &lt;enumeration value="Other"/>
 *     &lt;enumeration value="ShortLegal"/>
 *     &lt;enumeration value="LongLegal"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AUS_PropertyLegalDescriptionTypeEnumerated")
@XmlEnum
public enum AUSPropertyLegalDescriptionTypeEnumerated {

    @XmlEnumValue("MetesAndBounds")
    METES_AND_BOUNDS("MetesAndBounds"),
    @XmlEnumValue("Other")
    OTHER("Other"),
    @XmlEnumValue("ShortLegal")
    SHORT_LEGAL("ShortLegal"),
    @XmlEnumValue("LongLegal")
    LONG_LEGAL("LongLegal");
    private final String value;

    AUSPropertyLegalDescriptionTypeEnumerated(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AUSPropertyLegalDescriptionTypeEnumerated fromValue(String v) {
        for (AUSPropertyLegalDescriptionTypeEnumerated c: AUSPropertyLegalDescriptionTypeEnumerated.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}