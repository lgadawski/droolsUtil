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
 * Class that represents Person object.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
@Entity
@Table(name = "A_CUSTOMERS")
public class Customer implements Serializable {
    /**
     * Min age for person.
     */
    public static final int sMIN_AGE = 12;
    /**
     * Max age for person.
     */
    public static final int sMAX_AGE = 90;
    /**
     * Min income value for person.
     */
    public static final float sMIN_INCOME = 1200;
    /**
     * Max income value for person.
     */
    public static final float sMAX_INCOME = 12000;
    /**
     * Min cash value for person.
     */
    public static final float sMIN_CASH = 0;
    /**
     * Max cash value for person.
     */
    public static final float sMAX_CASH = 500000;
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Entity id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cust_seq")
    @SequenceGenerator(name = "cust_seq", sequenceName = "cust_seq", allocationSize = 500)
    @Column(name = "CustomerID", unique = true, updatable = false, nullable = false)
    private long customerID;
    /**
     * Name of person, in files represents as sequence integer values.
     */
    private String name;
    /**
     * Person's age.
     */
    private int age;
    /**
     * Person's income.
     */
    @Column(name = "income", scale = 2)
    private BigDecimal income;
    /**
     * Person's cash.
     */
    @Column(name = "cash", scale = 2)
    private BigDecimal cash;
    /**
     * Flag indicates if person is married.
     */
    private boolean married;

    /**
     * For persistence.
     */
    public Customer() {
    }

    /**
     * Constructs Person objects.
     * 
     * @param name
     *            - name of person.
     * @param age
     *            - person's age.
     * @param income
     *            - person's income.
     * @param cash
     *            - person's cash.
     * @param married
     *            - if married?
     */
    public Customer(final String name, final int age, final BigDecimal income,
            final BigDecimal cash, final boolean married) {
        this.name = name;
        this.age = age;
        this.income = income;
        this.cash = cash;
        this.married = married;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + age;
        result = prime * result + ((cash == null) ? 0 : cash.hashCode());
        result = prime * result + ((income == null) ? 0 : income.hashCode());
        result = prime * result + (married ? 1231 : 1237);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        final Customer other = (Customer) obj;
        if (age != other.age)
            return false;
        if (cash == null) {
            if (other.cash != null)
                return false;
        } else if (!cash.equals(other.cash))
            return false;
        if (income == null) {
            if (other.income != null)
                return false;
        } else if (!income.equals(other.income))
            return false;
        if (married != other.married)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Customer [name=" + name + ", age=" + age + ", income=" + income
                + ", cash=" + cash + ", married=" + married + "]";
    }

    /**
     * @return person age
     */
    public int getAge() {
        return age;
    }

    /**
     * @param age
     *            - set person age.
     */
    public void setAge(final int age) {
        this.age = age;
    }

    /**
     * @return person income.
     */
    public BigDecimal getIncome() {
        return income;
    }

    /**
     * @param income
     *            - set person income.
     */
    public void setIncome(final BigDecimal income) {
        this.income = income;
    }

    /**
     * @return person cash.
     */
    public BigDecimal getCash() {
        return cash;
    }

    /**
     * @param cash
     */
    public void setCash(final BigDecimal cash) {
        this.cash = cash;
    }

    /**
     * @return
     */
    public boolean isMarried() {
        return married;
    }

    /**
     * @param married
     */
    public void setMarried(final boolean married) {
        this.married = married;
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
    public long getId() {
        return customerID;
    }

    /**
     * @param id
     */
    public void setId(final long id) {
        this.customerID = id;
    }
}
