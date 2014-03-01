package com.gadawski.util.facts;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author l.gadawski@gmail.com
 * 
 */
@Entity
@Table(name = "A_HOUSES")
public class House implements Serializable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Min house price.
     */
    public static final float sMIN_PRICE = 50000;
    /**
     * Max house price.
     */
    public static final float sMAX_PRICE = 1000000;
    /**
     * Max house area.
     */
    public static final float sMAX_AREA = 100000;
    /**
     * Min house area.
     */
    public static final float sMIN_AREA = 1000;
    /**
     * Entity id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hou_seq")
    @SequenceGenerator(name = "hou_seq", sequenceName = "hou_seq", allocationSize = 500)
    @Column(name = "HouseID", unique = true, updatable = false, nullable = false)
    private long houseID;
    /**
     * House name.
     */
    private String name;
    /**
     * House price.
     */
    @Column(name = "price", scale = 2)
    private BigDecimal price;
    /**
     * House area.
     */
    @Column(name = "area", scale = 2)
    private BigDecimal area;

    /**
     * For persistence.
     */
    public House() {
    }

    /**
     * Construct house.
     * 
     * @param name
     *            - house name.
     * @param price
     *            - house price.
     * @param area
     *            - house area.
     */
    public House(final String name, final BigDecimal price,
            final BigDecimal area) {
        this.name = name;
        this.price = price;
        this.area = area;
    }

    @Override
    public String toString() {
        return "House [name=" + name + ", price=" + price + ", area=" + area
                + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((area == null) ? 0 : area.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((price == null) ? 0 : price.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final House other = (House) obj;
        if (area == null) {
            if (other.area != null)
                return false;
        } else if (!area.equals(other.area))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (price == null) {
            if (other.price != null)
                return false;
        } else if (!price.equals(other.price))
            return false;
        return true;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * @param price
     */
    public void setPrice(final BigDecimal price) {
        this.price = price;
    }

    /**
     * @return
     */
    public BigDecimal getArea() {
        return area;
    }

    /**
     * @param area
     */
    public void setArea(final BigDecimal area) {
        this.area = area;
    }

    /**
     * @return
     */
    public long getId() {
        return houseID;
    }

    /**
     * @param id
     */
    public void setId(final long id) {
        this.houseID = id;
    }
}
