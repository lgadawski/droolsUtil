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
@Table(name = "CARS")
public class Car implements Serializable {
    /**
     * Min car price.
     */
    public static final float sMIN_PRICE = 2000;
    /**
     * Max car price.
     */
    public static final float sMAX_PRICE = 200000;
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 5536636575625460686L;
    /**
     * Entity id.
     */
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="car_seq")
    @SequenceGenerator(name="car_seq", sequenceName="car_seq", allocationSize=500)
    @Column(name = "CarID", unique = true, updatable = false, nullable = false)
    private Long carID;
    /**
     * Car name.
     */
    private String name;
    /**
     * Car price.
     */
    @Column(name = "price", scale = 2)
    private BigDecimal price;

    /**
     * For persistence.
     */
    public Car() {
    }

    /**
     * Construct car.
     * 
     * @param name
     *            - car name.
     * @param price
     *            - car price.
     */
    public Car(String name, BigDecimal price) {
        this.setName(name);
        this.setPrice(price);
    }

    /**
     * Creates example car object.
     * 
     * @return example car object.
     */
    public static Car createExampleObject() {
        Car car = new Car();
        car.setName("volvo");
        car.setPrice(BigDecimal.valueOf(1324));
        return car;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((price == null) ? 0 : price.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "Car [name=" + name + ", price=" + price + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Car other = (Car) obj;
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
    public Long getId() {
        return carID;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.carID = id;
    }

}
