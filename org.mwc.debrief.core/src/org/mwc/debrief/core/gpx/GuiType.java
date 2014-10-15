/*
 *    Debrief - the Open Source Maritime Analysis Application
 *    http://debrief.info
 *
 *    (C) 2000-2014, PlanetMayo Ltd
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the Eclipse Public License v1.0
 *    (http://www.eclipse.org/legal/epl-v10.html)
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-147 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.08.30 at 10:32:43 PM EDT 
//


package org.mwc.debrief.core.gpx;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for guiType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="guiType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="tote" type="{org.mwc.debrief.core}toteType" minOccurs="0"/>
 *         &lt;element name="component" type="{org.mwc.debrief.core}componentType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Background" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="colour" type="{org.mwc.debrief.core}colourType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "guiType", propOrder = {
    "toteOrComponentOrBackground"
})
public class GuiType {

    @XmlElements({
        @XmlElement(name = "Background", type = GuiType.Background.class),
        @XmlElement(name = "tote", type = ToteType.class),
        @XmlElement(name = "component", type = ComponentType.class)
    })
    protected List<Object> toteOrComponentOrBackground;

    /**
     * Gets the value of the toteOrComponentOrBackground property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the toteOrComponentOrBackground property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getToteOrComponentOrBackground().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GuiType.Background }
     * {@link ToteType }
     * {@link ComponentType }
     * 
     * 
     */
    public List<Object> getToteOrComponentOrBackground() {
        if (toteOrComponentOrBackground == null) {
            toteOrComponentOrBackground = new ArrayList<Object>();
        }
        return this.toteOrComponentOrBackground;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="colour" type="{org.mwc.debrief.core}colourType"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "colour"
    })
    public static class Background {

        @XmlElement(required = true)
        protected ColourType colour;

        /**
         * Gets the value of the colour property.
         * 
         * @return
         *     possible object is
         *     {@link ColourType }
         *     
         */
        public ColourType getColour() {
            return colour;
        }

        /**
         * Sets the value of the colour property.
         * 
         * @param value
         *     allowed object is
         *     {@link ColourType }
         *     
         */
        public void setColour(final ColourType value) {
            this.colour = value;
        }

    }

}
