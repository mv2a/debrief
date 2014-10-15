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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fixExtensionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fixExtensionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="Course" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Label" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="LabelLocation" type="{org.mwc.debrief.core}labelLocationType" default="Left" />
 *       &lt;attribute name="LabelShowing" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="Speed" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="SymbolShowing" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="ArrowShowing" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="LineShowing" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="Visible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fixExtensionType")
public class FixExtensionType {

    @XmlAttribute(name = "Course", required = true)
    protected String course;
    @XmlAttribute(name = "Label", required = true)
    protected String label;
    @XmlAttribute(name = "LabelLocation")
    protected LabelLocationType labelLocation;
    @XmlAttribute(name = "LabelShowing")
    protected Boolean labelShowing;
    @XmlAttribute(name = "Speed", required = true)
    protected String speed;
    @XmlAttribute(name = "SymbolShowing")
    protected Boolean symbolShowing;
    @XmlAttribute(name = "ArrowShowing")
    protected Boolean arrowShowing;
    @XmlAttribute(name = "LineShowing")
    protected Boolean lineShowing;
    @XmlAttribute(name = "Visible")
    protected Boolean visible;

    /**
     * Gets the value of the course property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCourse() {
        return course;
    }

    /**
     * Sets the value of the course property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCourse(final String value) {
        this.course = value;
    }

    /**
     * Gets the value of the label property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the value of the label property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(final String value) {
        this.label = value;
    }

    /**
     * Gets the value of the labelLocation property.
     * 
     * @return
     *     possible object is
     *     {@link LabelLocationType }
     *     
     */
    public LabelLocationType getLabelLocation() {
        if (labelLocation == null) {
            return LabelLocationType.LEFT;
        } else {
            return labelLocation;
        }
    }

    /**
     * Sets the value of the labelLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link LabelLocationType }
     *     
     */
    public void setLabelLocation(final LabelLocationType value) {
        this.labelLocation = value;
    }

    /**
     * Gets the value of the labelShowing property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isLabelShowing() {
        if (labelShowing == null) {
            return true;
        } else {
            return labelShowing;
        }
    }

    /**
     * Sets the value of the labelShowing property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLabelShowing(final Boolean value) {
        this.labelShowing = value;
    }

    /**
     * Gets the value of the speed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpeed() {
        return speed;
    }

    /**
     * Sets the value of the speed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpeed(final String value) {
        this.speed = value;
    }

    /**
     * Gets the value of the symbolShowing property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isSymbolShowing() {
        if (symbolShowing == null) {
            return true;
        } else {
            return symbolShowing;
        }
    }

    /**
     * Sets the value of the symbolShowing property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSymbolShowing(final Boolean value) {
        this.symbolShowing = value;
    }

    /**
     * Gets the value of the arrowShowing property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isArrowShowing() {
        if (arrowShowing == null) {
            return false;
        } else {
            return arrowShowing;
        }
    }

    /**
     * Sets the value of the arrowShowing property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setArrowShowing(final Boolean value) {
        this.arrowShowing = value;
    }

    /**
     * Gets the value of the lineShowing property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isLineShowing() {
        if (lineShowing == null) {
            return true;
        } else {
            return lineShowing;
        }
    }

    /**
     * Sets the value of the lineShowing property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLineShowing(final Boolean value) {
        this.lineShowing = value;
    }

    /**
     * Gets the value of the visible property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isVisible() {
        if (visible == null) {
            return true;
        } else {
            return visible;
        }
    }

    /**
     * Sets the value of the visible property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setVisible(final Boolean value) {
        this.visible = value;
    }

}
