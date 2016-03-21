//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.04.23 at 12:57:08 PM EDT 
//


package org.apache.aries.blueprint.jaxb;

import java.util.List;
import java.util.Vector;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 
 *                 Tcollection is the base schema type for different ordered collection
 *                 types.  This is shared between the <array>, <list>, and <set> elements.
 *                 
 * 			
 * 
 * <p>Java class for Tcollection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Tcollection">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.osgi.org/xmlns/blueprint/v1.0.0}TtypedCollection">
 *       &lt;group ref="{http://www.osgi.org/xmlns/blueprint/v1.0.0}Gvalue" maxOccurs="unbounded" minOccurs="0"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Tcollection", namespace = "http://www.osgi.org/xmlns/blueprint/v1.0.0", propOrder = {
    "gvalue"
})
@XmlRootElement(name = "collection")
public class Tcollection
    extends TtypedCollection
{

    @XmlElementRefs({
        @XmlElementRef(name = "list", namespace = "http://www.osgi.org/xmlns/blueprint/v1.0.0", type = JAXBElement.class),
        @XmlElementRef(name = "null", namespace = "http://www.osgi.org/xmlns/blueprint/v1.0.0", type = JAXBElement.class),
        @XmlElementRef(name = "value", namespace = "http://www.osgi.org/xmlns/blueprint/v1.0.0", type = JAXBElement.class),
        @XmlElementRef(name = "service", namespace = "http://www.osgi.org/xmlns/blueprint/v1.0.0", type = JAXBElement.class),
        @XmlElementRef(name = "array", namespace = "http://www.osgi.org/xmlns/blueprint/v1.0.0", type = JAXBElement.class),
        @XmlElementRef(name = "reference", namespace = "http://www.osgi.org/xmlns/blueprint/v1.0.0", type = JAXBElement.class),
        @XmlElementRef(name = "idref", namespace = "http://www.osgi.org/xmlns/blueprint/v1.0.0", type = JAXBElement.class),
        @XmlElementRef(name = "bean", namespace = "http://www.osgi.org/xmlns/blueprint/v1.0.0", type = JAXBElement.class),
        @XmlElementRef(name = "props", namespace = "http://www.osgi.org/xmlns/blueprint/v1.0.0", type = JAXBElement.class),
        @XmlElementRef(name = "map", namespace = "http://www.osgi.org/xmlns/blueprint/v1.0.0", type = JAXBElement.class),
        @XmlElementRef(name = "ref", namespace = "http://www.osgi.org/xmlns/blueprint/v1.0.0", type = JAXBElement.class),
        @XmlElementRef(name = "set", namespace = "http://www.osgi.org/xmlns/blueprint/v1.0.0", type = JAXBElement.class),
        @XmlElementRef(name = "reference-list", namespace = "http://www.osgi.org/xmlns/blueprint/v1.0.0", type = JAXBElement.class)
    })
    @XmlAnyElement(lax = true)
    protected List<Object> gvalue = new Vector<Object>();

    /**
     * Gets the value of the gvalue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the gvalue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGvalue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Tcollection }{@code >}
     * {@link JAXBElement }{@code <}{@link Tnull }{@code >}
     * {@link JAXBElement }{@code <}{@link Tvalue }{@code >}
     * {@link JAXBElement }{@code <}{@link TinlinedService }{@code >}
     * {@link JAXBElement }{@code <}{@link Tcollection }{@code >}
     * {@link JAXBElement }{@code <}{@link TinlinedReference }{@code >}
     * {@link JAXBElement }{@code <}{@link Tref }{@code >}
     * {@link JAXBElement }{@code <}{@link TinlinedBean }{@code >}
     * {@link JAXBElement }{@code <}{@link Tprops }{@code >}
     * {@link Object }
     * {@link JAXBElement }{@code <}{@link Tmap }{@code >}
     * {@link JAXBElement }{@code <}{@link Tref }{@code >}
     * {@link JAXBElement }{@code <}{@link Tcollection }{@code >}
     * {@link JAXBElement }{@code <}{@link TinlinedReferenceList }{@code >}
     * 
     * 
     */
    public List<Object> getGvalue() {
        if (gvalue == null) {
            gvalue = new Vector<Object>();
        }
        return this.gvalue;
    }

}
