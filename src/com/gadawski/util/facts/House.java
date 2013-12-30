package com.gadawski.util.facts;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author l.gadawski@gmail.com
 * 
 */
@Entity
@Table(name = "HOUSES")
public class House implements Serializable{
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
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "HouseID", unique = true, updatable = false, nullable = false)
	private Long houseID;
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
	House() {
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
	public House(String name, BigDecimal price, BigDecimal area) {
		this.setName(name);
		this.setPrice(price);
		this.setArea(area);
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		House other = (House) obj;
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
	public void setName(String name) {
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
	public void setPrice(BigDecimal price) {
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
	public void setArea(BigDecimal area) {
		this.area = area;
	}

	/**
	 * @return
	 */
	public Long getId() {
		return houseID;
	}

	/**
	 * @param id
	 */
	public void setId(Long id) {
		this.houseID = id;
	}
}
