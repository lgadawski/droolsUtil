package com.gadawski.util.facts;

/**
 * Class that represents car proposal for custom customer.
 * 
 * @author l.gadawski@gmail.com
 * 
 */
public class CarProposal {
    
    
	public Car car;
    public Customer customer;

    /**
	 * @param customer
	 * @param car
	 */
	public CarProposal(Customer customer, Car car) {
	    this.customer  =customer;
	    this.car = car;
	}

	/**
	 * 
	 */
	public CarProposal() {

	}
}
